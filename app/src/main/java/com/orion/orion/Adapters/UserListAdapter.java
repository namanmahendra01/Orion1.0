package com.orion.orion.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orion.orion.R;

import com.orion.orion.models.users;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends ArrayAdapter<users> {

    private static final String TAG = "UserListAdapter";

    private LayoutInflater mInflater;
    private List<users> mUser= null;
    private int layoutResources;
    private Context mContext;

    public UserListAdapter(@NonNull Context context, int resource, @NonNull List<users> objects) {
        super(context, resource, objects);
        mContext=context;
        mInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutResources=resource;
        this.mUser=objects;
    }
    private static class ViewHolder{
        TextView username , email;
        CircleImageView profileImage;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if(convertView==null){
            convertView=mInflater.inflate(layoutResources,parent,false);
            holder=new ViewHolder();

            holder.username=(TextView)convertView.findViewById(R.id.username);
            holder.email=(TextView)convertView.findViewById(R.id.email);
            holder.profileImage=(CircleImageView) convertView.findViewById(R.id.profile_image);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }
        holder.username.setText(mUser.get(position).getUsername());
        holder.email.setText(mUser.get(position).getEmail());

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(getItem(position).getUser_id());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot:dataSnapshot.getChildren()){

                    ImageLoader imageLoader = ImageLoader.getInstance();

                    imageLoader.displayImage(singleSnapshot.getValue(users.class).getProfile_photo(),holder.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return convertView;
    }
}
