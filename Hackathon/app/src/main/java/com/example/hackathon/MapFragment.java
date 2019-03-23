package com.example.hackathon;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MapFragment extends Fragment {


    private GoogleMap mMap;
    MapView mMapView;
    LocationManager locationManager;
    LocationListener locationListener;
    Location lastKnownLocation;
    LatLng myCurrentlocation;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    private EditText editLocation = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mMap.clear();
                        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        //myCurrentlocation = new LatLng(Double.valueOf(1.3521),Double.valueOf(103.8198));
                        myCurrentlocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                        //mMap.clear();
                        mMap.addMarker(new MarkerOptions().position(myCurrentlocation).title("My location").snippet("and snippet")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentlocation, 16));
                        Log.i("Location", location.toString());
                        //Toast.makeText(MapsActivity.this,location.toString(),Toast.LENGTH_SHORT).show();
                        //LatLng mylocation = new LatLng(location.getLatitude(),location.getLongitude());
                        //mMap.clear();
                        //mMap.addMarker(new MarkerOptions().position(mylocation).title("New location"));
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }

                };
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1200, 10, locationListener);
//            long time= System.currentTimeMillis();
//            long duration = 5000;
//            while(System.currentTimeMillis()< time + duration){
//                continue;
//            }
                    lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    //myCurrentlocation = new LatLng(Double.valueOf(1.3521),Double.valueOf(103.8198));
                    myCurrentlocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    //mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(myCurrentlocation).title("My location").snippet("and snippet")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myCurrentlocation, 17));
                    FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
                    CollectionReference pointsRef = rootRef.collection("points_au");
                    DocumentReference parkRef = pointsRef.document("Park");
                    parkRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    GeoPoint geo = document.getGeoPoint("geo");
                                    String name = document.getString("name");
                                    double lat = geo.getLatitude();
                                    double lng = geo.getLongitude();
                                    LatLng latLng = new LatLng(lat, lng);
                                    mMap.addMarker(new MarkerOptions().position(latLng).title(name));
                                }
                            }
                        }
                    });
                }
            }});
        return rootView;
    }

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }


}
