package com.example.myapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    EditText mEmail, mPassword;
    Button mLoginBtn;
    String input;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    TextView forgotTextLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.Password);
        progressBar = findViewById(R.id.progressBar2);
        mLoginBtn = findViewById(R.id.loginButton);
        fAuth = FirebaseAuth.getInstance();
        forgotTextLink = findViewById(R.id.forgotPassword);


        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), AllUsers.class));
            finish();
        }

        mLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is invalid");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is invalid");
                    return;
                }
                if(password.length() < 6 ){
                    mPassword.setError("The password is too short, at least 6 characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //log in

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Logged in as " + email , Toast.LENGTH_LONG).show();
                            Intent i = new Intent(MainActivity.this , AllUsers.class);
                            i.putExtra("userEmail", email);
                            startActivity(i);
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Error - " + task.getException().getMessage(),Toast.LENGTH_LONG ).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });
        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                    final EditText resetMail = new EditText(v.getContext());
                    AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                    passwordResetDialog.setTitle("Reset password");
                    passwordResetDialog.setMessage("Do you want to receive an email to reset your password? Enter your email");
                    passwordResetDialog.setView(resetMail);

                    passwordResetDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //extract the email and sending the email xd
                            String mail = resetMail.getText().toString();
                            fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(MainActivity.this, "An email has been sent", Toast.LENGTH_LONG).show();
                                }
                            }) .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "Error" + e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                    passwordResetDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                               //back to the login page
                        }
                    });
                passwordResetDialog.create().show();
            }
        });

    }

    public void launch(View v){
        Intent newActivity = new Intent(this, CartActivity.class);
        startActivity(newActivity);
    }


}