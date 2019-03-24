package com.example.hackathon;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter=5;
    private TextView userRegistration;
    private FirebaseAuth firebaseAuth;
    private Button Guest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Name =  (EditText) findViewById(R.id.etName);
        Password = (EditText) findViewById(R.id.etPasswordEmail);
        Login = (Button) findViewById(R.id.btnLogin);
        userRegistration = (TextView) findViewById(R.id.tvRegister);
        Guest = (Button) findViewById(R.id.Guest);
        firebaseAuth = FirebaseAuth.getInstance();


        Guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, MainActivity.class));
            }
        });


        userRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, RegistrationActivity.class));
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    finish();
                    startActivity(new Intent(Login.this, MainActivity.class)); //after login
                }
            }
            });

        Login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("DEBUG", "Actually clicked");
                        validate(Name.getText().toString(), Password.getText().toString());

                    }
                });

                userRegistration.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(Login.this, RegistrationActivity.class));


                    }
                });



    }

            private void validate(String userName, String userPassword){
                Log.d("DEBUG", userName);
                Log.d("DEBUG", userPassword);

                firebaseAuth.signInWithEmailAndPassword(userName, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("DEBUG", "ENTERED VALIDATION");

                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(Login.this, NewPostActivity.class));  //after login
                        }else{
                            Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
    }
