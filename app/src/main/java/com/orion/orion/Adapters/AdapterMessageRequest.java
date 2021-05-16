package com.orion.orion.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.chat.Chat_Activity;
import com.orion.orion.models.users;

import java.util.HashMap;
import java.util.List;

public class AdapterMessageRequest extends RecyclerView.Adapter<AdapterMessageRequest.MyHolder> {
    Context context;
    List<users> usersList;
    List<String> usersList2;
    private HashMap<String,String> LastMessagemap;


    public AdapterMessageRequest(Context context, List<String> usersList2) {
        this.context = context;
        this.usersList2 = usersList2;
        LastMessagemap = new HashMap<>();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist,parent,false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {

        String hisUid=usersList2.get(i);
        String LastMessage=LastMessagemap.get(hisUid);

        getList(hisUid,holder.nameTv,holder.profileTv);


        if (LastMessage==null||LastMessage.equals("default")){
            holder.lastMessageTv.setVisibility(View.GONE);
        }else{
            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(LastMessage);

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Chat_Activity.class);
                intent.putExtra("his_uid",hisUid);
                intent.putExtra("request","yes");
                context.startActivity(intent);
            }
        });

    }

    private void getList(String hisUid, TextView nameTv, ImageView profileTv) {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(context.getString(R.string.dbname_users))
                .child(hisUid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        users user = dataSnapshot.getValue(users.class);
                        nameTv.setText(user.getU());
                        Glide.with(context.getApplicationContext().getApplicationContext())
                                .load(user.getPp())
                                .placeholder(R.drawable.load)
                                .error(R.drawable.default_image2)
                                .placeholder(R.drawable.load)
                                .thumbnail(0.5f)
                                .into(profileTv);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    public void setLastMessage(String userId,String lastMessage){
        LastMessagemap.put(userId,lastMessage);
    }
    public long getItemId(int position) {
        if (usersList==null||usersList.size()==0){
            return position;
        }else{
            return usersList.get(position).hashCode();

        }
    }
    @Override
    public int getItemCount() {
        return usersList2.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {


        ImageView profileTv,onlineStatusTv;
        TextView nameTv,lastMessageTv;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileTv=itemView.findViewById(R.id.profileCv);
            onlineStatusTv=itemView.findViewById(R.id.onlineStatusCv);
            nameTv=itemView.findViewById(R.id.NameTv);
            lastMessageTv=itemView.findViewById(R.id.LastmessageTv);



        }
    }
}
