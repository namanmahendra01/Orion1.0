package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.ViewPostActivity;
import com.orion.orion.models.Comment;
import com.orion.orion.models.Photo;
import com.orion.orion.util.UniversalImageLoader;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class AdapterDomain extends RecyclerView.Adapter<AdapterDomain.ViewHolder> {



    private Context mContext;
    private List<String> domains;

    public AdapterDomain(Context mContext, List<String> domains) {
        this.mContext = mContext;
        this.domains = domains;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.domain_item,parent,false);
        return new AdapterDomain.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        String domain=domains.get(i);
        holder.domainTv.setText(domain);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    public long getItemId(int position) {
        return domains.get(position).hashCode();
    }
    @Override
    public int getItemCount() {
        return domains.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView domainTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            domainTv = itemView.findViewById(R.id.domain);





        }
    }


}
