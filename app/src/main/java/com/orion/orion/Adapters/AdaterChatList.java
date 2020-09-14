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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.home.Chat_Activity;
import com.orion.orion.models.Chat;
import com.orion.orion.models.users;
import com.orion.orion.util.UniversalImageLoader;

import java.util.HashMap;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;

public class AdaterChatList extends RecyclerView.Adapter<AdaterChatList.MyHolder> {

        Context context;

    List<String> usersList2;
private HashMap<String,String> LastMessagemap;


    public AdaterChatList(Context context, List<String> usersList2) {
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

        DatabaseReference refer = FirebaseDatabase.getInstance().getReference(context.getString(R.string.dbname_Chats));
        Query query=   refer.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(hisUid)
                .orderByKey()
                .limitToLast(1);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.exists()){
                        Chat chat = ds.getValue(Chat.class);
                        Log.d(TAG, "onDataChange: ok"+chat.toString());
                        if (!chat.isIfseen()
                        && chat.getReceiver().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            holder.notSeen.setVisibility(View.VISIBLE);
                        }

                    }

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
                intent.putExtra("his_UID",hisUid);
                intent.putExtra("request","no");
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
                        nameTv.setText(user.getUsername());
                            UniversalImageLoader.setImage(user.getProfile_photo(), profileTv, null, "");



                        }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }



    public void setLastMessage(String userId,String lastMessage){
        LastMessagemap.put(userId,lastMessage);
    }

    @Override
    public int getItemCount() {
        return usersList2.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {


        ImageView profileTv,onlineStatusTv,notSeen;
        TextView nameTv,lastMessageTv;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            profileTv=itemView.findViewById(R.id.profileCv);
            onlineStatusTv=itemView.findViewById(R.id.onlineStatusCv);
            nameTv=itemView.findViewById(R.id.NameTv);
            lastMessageTv=itemView.findViewById(R.id.LastmessageTv);
            notSeen=itemView.findViewById(R.id.notSeen);



        }
    }
}
