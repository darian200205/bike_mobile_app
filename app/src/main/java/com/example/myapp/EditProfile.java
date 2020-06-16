package com.example.myapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class EditProfile extends AppCompatActivity {

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    EditText fullName, email, phone;
    String userId;
    StorageReference storageReference;
    ImageView profileImage;
    Button saveBtn;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        fullName = findViewById(R.id.profileFullName);
        email = findViewById(R.id.profileEmailAddress);
        phone = findViewById(R.id.profilePhoneNo);
        profileImage = findViewById(R.id.profileImageView);
        saveBtn = findViewById(R.id.saveProfileInfo);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        user = fAuth.getCurrentUser();

        userId = fAuth.getCurrentUser().getUid();

        final DocumentReference documentReference = fStore.collection("users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                phone.setText(documentSnapshot.getString("phone"));
                fullName.setText(documentSnapshot.getString("fName"));
                email.setText(documentSnapshot.getString("email"));
            }
        });
        final StorageReference profileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mEmail = email.getText().toString();
                final String mName = fullName.getText().toString();
                final String mPhone = phone.getText().toString();
                if(TextUtils.isEmpty(mEmail)){
                    email.setError("Email invalid");
                    return;
                }
                if(TextUtils.isEmpty(mName) || mName.length() <= 1){
                    fullName.setError("Invalid name");
                }
                if(mPhone.length() <=4 ){
                    phone.setError("Invalid phone number");
                }
                user.updateEmail(mEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                            DocumentReference docRef = fStore.collection("users").document(user.getUid());
                            Map <String,Object> edited = new HashMap<>();
                            edited.put("email", mEmail);
                            edited.put("fName", mName);
                            edited.put("phone", mPhone);
                            docRef.update(edited);
                        AlertDialog.Builder emailChanged = new AlertDialog.Builder(EditProfile.this);
                        emailChanged.setMessage("Your profile has been successfully updated");
                        emailChanged.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent j = new Intent(getApplicationContext(), yourProfile.class);
                                startActivity(j);
                            }
                        });
                        emailChanged.create().show();
                    }
                }) .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder emailChanged = new AlertDialog.Builder(EditProfile.this);
                        emailChanged.setTitle("Error");
                        emailChanged.setMessage("Error " + e.getMessage());

                        emailChanged.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent j = new Intent(getApplicationContext(), yourProfile.class);
                                startActivity(j);
                            }
                        });
                        emailChanged.create().show();
                    }
                });
            }
        });

    }
}