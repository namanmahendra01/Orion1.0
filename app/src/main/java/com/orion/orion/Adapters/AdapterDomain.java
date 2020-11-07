package com.orion.orion.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.orion.orion.R;

import java.util.List;

public class AdapterDomain extends RecyclerView.Adapter<AdapterDomain.ViewHolder> {

    private Context mContext;
    private List<String> domains;
    private OnItemClickListener mOnItemClickListener;

    public AdapterDomain(Context mContext, List<String> domains, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.domains = domains;
        this.mOnItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.domain_item, parent, false);
        return new ViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {
        String domain = domains.get(i);
        holder.domainTv.setText(domain);
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

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView domainTv;
        OnItemClickListener onItemClickListener;
        public ViewHolder(@NonNull View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            domainTv = itemView.findViewById(R.id.domain);
            this.onItemClickListener = onItemClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }
    public interface OnItemClickListener{
        void onItemClick(int  domain);
    }
}
