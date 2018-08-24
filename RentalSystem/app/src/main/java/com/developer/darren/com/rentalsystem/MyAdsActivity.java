package com.developer.darren.com.rentalsystem;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.developer.darren.com.rentalsystem.data.Constants;
import com.developer.darren.com.rentalsystem.model.AdAdapter;
import com.developer.darren.com.rentalsystem.model.Advertisement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyAdsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_my_ads);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null)
            actionBar.setDisplayHomeAsUpEnabled(true);

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
                                if(ad.getUserID().equals(Constants.CURRENT_USER.getUserID()))
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
                    if(ad.getUserID().equals(Constants.CURRENT_USER.getUserID()))
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
