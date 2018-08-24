package com.developer.darren.com.rentalsystem;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.developer.darren.com.rentalsystem.data.Constants;
import com.developer.darren.com.rentalsystem.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private EditText usernameET, emailET;
    private CheckBox primeCheck;
    private Button saveBtn;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference(Constants.KEY_USERS);
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        usernameET = findViewById(R.id.profile_username);
        emailET = findViewById(R.id.profile_email);
        primeCheck = findViewById(R.id.profile_prime);
        saveBtn = findViewById(R.id.profile_saveBtn);

        final FirebaseUser user = auth.getCurrentUser();
        usernameET.setText(user.getDisplayName());
        emailET.setText(user.getEmail());
        emailET.setEnabled(false);
        primeCheck.setChecked(Constants.CURRENT_USER.isPrime());
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameET.getText().toString();
                boolean prime = primeCheck.isChecked();
                Constants.CURRENT_USER.setName(username);
                Constants.CURRENT_USER.setPrime(prime);
                userRef.child(user.getUid()).child(Constants.KEY_PRIME).setValue(prime);
                UserProfileChangeRequest userprofile=new UserProfileChangeRequest.Builder().setDisplayName(username).build();
                user.updateProfile(userprofile).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}