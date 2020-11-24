package com.orion.orion.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orion.orion.R;
import com.orion.orion.models.Chat;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class AdapterChat extends RecyclerView.Adapter<AdapterChat.MyHolder> {

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_RIGHT = 1;
    Context context;
    List<Chat> chatList;
    FirebaseUser fUser;

    public AdapterChat(Context context, List<Chat> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        if (i == MSG_TYPE_RIGHT) view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, viewGroup, false);
        else view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder myHolder, final int i) {
//        get data
        String message = chatList.get(i).getMsg();
        String timeStamp = chatList.get(i).getTim();

//        convert time stamp to date and time
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timeStamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

//        set data
        myHolder.messageTv.setText(message);
        myHolder.timeTv.setText(dateTime);

//        click to show dialogue box
        myHolder.messageLayout.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete");
            builder.setMessage(R.string.delete_message_prompt);

//                set buttons
            builder.setPositiveButton("Delete", (dialog, which) -> DeleteMessage(i));
            builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
            builder.create().show();


            return true;
        });
    }

    private void DeleteMessage(int position) {
        String msgID = chatList.get(position).getMid();
        String hisId = chatList.get(position).getRid();
        DatabaseReference dbTs1 = FirebaseDatabase.getInstance().getReference();
        dbTs1.child(context.getString(R.string.dbname_Chats))
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                .child(hisId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            dbTs1.child(context.getString(R.string.dbname_ChatList))
                                    .child(Objects.requireNonNull(snapshot.getValue()).toString())
                                    .child(msgID)
                                    .removeValue();
                            chatList.remove(chatList.get(position));
                            AdapterChat.this.notifyItemRemoved(position);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public long getItemId(int position) {
        Chat chat = chatList.get(position);
        return chat.getMid().hashCode();
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
//        get currently signed user
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSid().equals(fUser.getUid())) {
            return MSG_TYPE_RIGHT;

        } else {
            return MSG_TYPE_LEFT;
        }
    }
    static class MyHolder extends RecyclerView.ViewHolder {

        TextView messageTv, timeTv;
        LinearLayout messageLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);


            messageTv = itemView.findViewById(R.id.messagetv);
            timeTv = itemView.findViewById(R.id.TimeTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);


        }
    }


}
