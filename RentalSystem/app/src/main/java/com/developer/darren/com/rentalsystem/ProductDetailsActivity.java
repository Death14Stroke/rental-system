package com.developer.darren.com.rentalsystem;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.developer.darren.com.rentalsystem.data.Constants;
import com.developer.darren.com.rentalsystem.model.Advertisement;
import com.developer.darren.com.rentalsystem.model.Transaction;
import com.developer.darren.com.rentalsystem.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProductDetailsActivity extends AppCompatActivity {

    private TextView name,owner,price,description,category;
    private Button buyBtn;
    private ImageView imageView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference trRef = database.getReference(Constants.KEY_TRANSACTION);
    DatabaseReference adRef = database.getReference(Constants.KEY_ADS);
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ads = storage.getReference(Constants.KEY_S_ADS);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        name=findViewById(R.id.product_name);
        owner=findViewById(R.id.ad_user_id);
        price=findViewById(R.id.price);
        description=findViewById(R.id.description);
        category=findViewById(R.id.category);
        buyBtn=findViewById(R.id.buy_button);
        imageView=findViewById(R.id.imageView);

        final Advertisement a = Constants.SELECTED_AD;
        name.setText(a.getProductName());
        owner.setText(a.getUserID());
        price.setText(String.valueOf(a.getPrice()));
        description.setText(a.getDescription());
        category.setText(a.getCategory());
        if(a.getUrl()!=null) {
            ads.child(a.getAdID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(ProductDetailsActivity.this)
                            .load(uri)
                            .into(imageView);
                }
            });
        }

        final User user = Constants.CURRENT_USER;
        if(user==null){
            buyBtn.setVisibility(View.GONE);
        }
        else{
            buyBtn.setVisibility(View.VISIBLE);
        }
        buyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String adID = a.getAdID();
                        final String buyer = user.getUserID();
                        final String seller = a.getUserID();
                        final String[] trID = { "" };
                        final float amount = a.getPrice();
                        final int[] cnt = {0};
                        trRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                cnt[0] = (int) dataSnapshot.getChildrenCount();
                                trID[0] ="tr"+(cnt[0]+1);
                                final Transaction tr = new Transaction(trID[0],buyer,seller,adID,amount);
                                trRef.child(trID[0]).setValue(tr).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        adRef.child(tr.getAdID()).removeValue();
                                        Toast.makeText(getApplicationContext(),"Transaction success",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(),"Transaction failed",Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}