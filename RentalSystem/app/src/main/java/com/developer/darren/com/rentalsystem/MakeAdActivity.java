package com.developer.darren.com.rentalsystem;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.developer.darren.com.rentalsystem.data.Constants;
import com.developer.darren.com.rentalsystem.model.Advertisement;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;

public class MakeAdActivity extends AppCompatActivity {

    private EditText productNameET, descriptionET, priceET;
    private Spinner catSpinner;
    private Button submitBtn;
    private ImageButton cameraBtn;
    private ImageView imageView;
    private Uri uri=null;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    DatabaseReference catRef = database.getReference(Constants.KEY_CATEGORY);
    DatabaseReference adRef = database.getReference(Constants.KEY_ADS);
    StorageReference ads = storage.getReference(Constants.KEY_S_ADS);
    List<String> catList;
    ArrayAdapter<String> catAdapter;
    private byte[] imageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_ad);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        priceET = findViewById(R.id.price_make_ad);
        productNameET = findViewById(R.id.productname_make_ad);
        descriptionET = findViewById(R.id.description_make_ad);
        catSpinner = findViewById(R.id.cat_spinner_make_ad);
        submitBtn = findViewById(R.id.submitBtn_make_ad);
        imageView = findViewById(R.id.upload_ad_image);
        cameraBtn = findViewById(R.id.upload_ad_image_btn);

        catList = new ArrayList<>();
        catAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_item,catList);
        catSpinner.setAdapter(catAdapter);

        catRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                catList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    catList.add(ds.getValue(String.class));
                    catAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(MakeAdActivity.this)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse response) {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(cameraIntent, Constants.CAMERA_REQUEST);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse response) {
                                Toast.makeText(getApplicationContext(),"Permission denied",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).
                        withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                                public void onError(DexterError error) {
                                    Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                                }
                        }).check();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userID = Constants.CURRENT_USER.getUserID();
                final String category = catList.get(catSpinner.getSelectedItemPosition());
                final String productName = productNameET.getText().toString();
                final String description = descriptionET.getText().toString();
                final String p = priceET.getText().toString();
                if(p.isEmpty() || productName.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please provide name and price",Toast.LENGTH_SHORT).show();
                    return;
                }
                final float price = Float.parseFloat(p);
                final int[] cnt = {0};
                final String[] adID = new String[1];
                adRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       // Toast.makeText(getApplicationContext(),"key: "+dataSnapshot.getKey()+" child: "+dataSnapshot.getChildrenCount(),Toast.LENGTH_SHORT).show();
                        cnt[0] = (int) dataSnapshot.getChildrenCount();
                        adID[0] = "ad"+(cnt[0]+1);
                        if(imageBytes!=null)
                            ads.child(adID[0]).putBytes(imageBytes);
                        Advertisement a = new Advertisement(description,userID, adID[0],category,productName,price,ads.child(adID[0]).getPath());
                        adRef.child(adID[0]).setValue(a).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(),"Ad placed",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.CAMERA_REQUEST && resultCode==RESULT_OK){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageBytes = baos.toByteArray();
            imageView.setImageBitmap(bitmap);
        }
    }
}