package com.developer.darren.com.rentalsystem;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.developer.darren.com.rentalsystem.data.Constants;
import com.developer.darren.com.rentalsystem.model.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference(Constants.KEY_USERS);
        FirebaseUser user = auth.getCurrentUser();
        if(user!=null){
            userRef.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Constants.CURRENT_USER = dataSnapshot.getValue(User.class);
                    Constants.CURRENT_USER.setUserID(dataSnapshot.getKey());
                    startActivity(new Intent(SplashActivity.this,AdsActivity.class));
                    finish();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }
}