package com.developer.darren.com.rentalsystem.model;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.developer.darren.com.rentalsystem.ProductDetailsActivity;
import com.developer.darren.com.rentalsystem.R;
import com.developer.darren.com.rentalsystem.data.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdAdapter extends RecyclerView.Adapter<AdAdapter.MyViewHolder> {

    private Context mContext;
    private List<Advertisement> adList;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ads = storage.getReference(Constants.KEY_S_ADS);

    public AdAdapter(Context mContext, List<Advertisement> adList) {
        this.mContext = mContext;
        this.adList = adList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView productNameTV,category,price;
        private LinearLayout ll;
        private ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            productNameTV = itemView.findViewById(R.id.product_name);
            category=itemView.findViewById(R.id.category);
            price=itemView.findViewById(R.id.price);
            ll=itemView.findViewById(R.id.addLL);
            imageView=itemView.findViewById(R.id.imageView);
        }
    }

    @Override
    public AdAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ad_view, parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdAdapter.MyViewHolder holder, int position) {
        Log.i("adapter list size"," "+adList.size());
        final Advertisement a = adList.get(position);
        holder.category.setText(a.getCategory());
        holder.price.setText(String.valueOf(a.getPrice()));
        holder.productNameTV.setText(a.getProductName());
        if(a.getUrl()!=null) {
            ads.child(a.getAdID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(mContext)
                            .load(uri)
                            .into(holder.imageView);
                }
            });
        }
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.SELECTED_AD=a;
                mContext.startActivity(new Intent(mContext, ProductDetailsActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return adList.size();
    }
}