package com.developer.darren.com.rentalsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.developer.darren.com.rentalsystem.data.Constants;
import com.developer.darren.com.rentalsystem.model.AdAdapter;
import com.developer.darren.com.rentalsystem.model.Advertisement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference adsRef = database.getReference(Constants.KEY_ADS);
    DatabaseReference catRef = database.getReference(Constants.KEY_CATEGORY);
    List<Advertisement> adList;
    AdAdapter adAdapter;
    List<String> catList;
    ArrayAdapter<String> catAdapter;
    private Spinner catTypeSpinner;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        recyclerView = findViewById(R.id.recycler_view);
        catTypeSpinner = findViewById(R.id.category_select);
        catList = new ArrayList<>();
        catAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_spinner_item,catList);
        catTypeSpinner.setAdapter(catAdapter);
        prepareCatSpinner();
        adList = new ArrayList<>();
        adAdapter = new AdAdapter(this,adList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adAdapter);

        prepareList();
    }

    private void prepareCatSpinner() {
        catRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                catList.clear();
                catList.add("All");
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    catList.add(ds.getValue(String.class));
                    catAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        catTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String category = catList.get(i);
                if(category.equals("All")){
                    prepareList();
                }
                else{
                    adsRef.orderByChild(Constants.KEY_CATEGORY).equalTo(category).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            adList.clear();
                            for(DataSnapshot ds: dataSnapshot.getChildren()){
                                Advertisement ad = ds.getValue(Advertisement.class);
                                Log.i("ads",ad.getAdID()+"");
                                adList.add(ad);
                            }
                            adAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void prepareList() {
        adsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                adList.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    Advertisement ad = ds.getValue(Advertisement.class);
                    Log.i("ads",ad.getAdID()+"");
                    if(Constants.CURRENT_USER==null || !ad.getUserID().equals(Constants.CURRENT_USER.getUserID()))
                        adList.add(ad);
                }
                adAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        adAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.main_logout:
                if(Constants.CURRENT_USER==null)
                    Toast.makeText(getApplicationContext(),"Login to use this functionality",Toast.LENGTH_SHORT).show();
                else {
                    auth.signOut();
                    Constants.CURRENT_USER=null;
                    finish();
                    startActivity(new Intent(AdsActivity.this, MainActivity.class));
                }
                break;
            case R.id.main_place_add:
                if(Constants.CURRENT_USER==null)
                    Toast.makeText(getApplicationContext(),"Login to use this functionality",Toast.LENGTH_SHORT).show();
                else
                    startActivity(new Intent(AdsActivity.this, MakeAdActivity.class));
                break;
            case R.id.main_profile:
                if(Constants.CURRENT_USER==null)
                    Toast.makeText(getApplicationContext(),"Login to use this functionality",Toast.LENGTH_SHORT).show();
                else
                    startActivity(new Intent(AdsActivity.this,ProfileActivity.class));
                break;
            case R.id.main_my_ads:
                if(Constants.CURRENT_USER==null)
                    Toast.makeText(getApplicationContext(),"Login to use this functionality",Toast.LENGTH_SHORT).show();
                else
                    startActivity(new Intent(AdsActivity.this,MyAdsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}