package com.orion.orion.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.R;
import com.orion.orion.contest.Contest_Evaluation.activity_view_media;
import com.orion.orion.models.ParticipantList;
import com.orion.orion.models.juryMarks;
import com.orion.orion.models.textFieldList;
import com.orion.orion.models.users;
import com.orion.orion.profile.profile;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;
import static com.orion.orion.util.SNTPClient.TAG;

public class AdapterJudge extends RecyclerView.Adapter<AdapterJudge.ViewHolder> {

    private Context mContext;
    private List<ParticipantList> participantLists;
    private String[] criterias;
    private String jury, comment, filetype, xj;
    private boolean ok = false;
    boolean fetchFromDB;
    private String contestKey = "";
    private ArrayList<textFieldList> markList = new ArrayList<>();


    public AdapterJudge(Context mContext, List<ParticipantList> participantLists, String[] criterias, String jury, String comment, String filetype, boolean fetchFromDB) {
        this.mContext = mContext;
        this.participantLists = participantLists;
        this.criterias = criterias;
        this.jury = jury;
        this.comment = comment;
        this.filetype = filetype;
        this.fetchFromDB = fetchFromDB;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_judge, parent, false);
        return new AdapterJudge.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        ParticipantList participant = participantLists.get(i);
        contestKey = participant.getCi();
        if (jury.equals("j1")) {
            xj = "xj1";
        } else if (jury.equals("j2")) {
            xj = "xj2";

        } else {
            xj = "xj3";

        }
        setRecyclerViews(holder);
        setMarks(participant, holder);
        getUsername(holder, participant.getUi());

        if (filetype.equals("Image")) {
            holder.image.setVisibility(View.VISIBLE);

        } else {
            holder.View.setVisibility(View.VISIBLE);
        }
        holder.count.setText(String.valueOf(i + 1) + "-");
        if (participant.getDes()!=null && !participant.getDes().equals("")){
                    holder.des.setText(participant.getDes());
        }


        Glide.with(mContext.getApplicationContext())
                .load(participant.getMl())
                .placeholder(R.drawable.load)
                .error(R.drawable.default_image2)
                .placeholder(R.drawable.load)
                .thumbnail(0.5f)
                .override(1024, 1024)
                .into(holder.image);

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, profile.class);
                i.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home));

                i.putExtra(mContext.getString(R.string.intent_user), participant.getUi());
                mContext.startActivity(i);

            }
        });

        holder.View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse(participant.getMl());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mContext.startActivity(intent);

                } catch (ActivityNotFoundException e) {
                    Toast.makeText(mContext, "Invalid Link", Toast.LENGTH_SHORT).show();
                }
            }
        });
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, activity_view_media.class);
                i.putExtra("imageLink", participant.getMl());
                i.putExtra("contestkey", participant.getCi());
                i.putExtra("joiningkey", participant.getJi());
                i.putExtra("view", "No");
                mContext.startActivity(i);
            }
        });
        holder.feedBack.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    markList.get(holder.getLayoutPosition()).setFeedback(s.toString());

                } catch (IndexOutOfBoundsException e) {
                    textFieldList textFieldList = new textFieldList();
                    textFieldList.setFeedback(s.toString());
                    markList.add(holder.getLayoutPosition(), textFieldList);
                }

                SharedPreferences sp = mContext.getSharedPreferences("markTF", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = sp.edit();
                Gson gson = new Gson();
                String json = gson.toJson(markList);
                prefsEditor.putString(contestKey, json);
                prefsEditor.apply();
            }
        });
        holder.ts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    markList.get(holder.getLayoutPosition()).setTotal(s.toString());

                } catch (IndexOutOfBoundsException e) {
                    textFieldList textFieldList = new textFieldList();
                    textFieldList.setTotal(s.toString());
                    markList.add(holder.getLayoutPosition(), textFieldList);
                }

                SharedPreferences sp = mContext.getSharedPreferences("markTF", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = sp.edit();
                Gson gson = new Gson();
                String json = gson.toJson(markList);
                prefsEditor.putString(contestKey, json);
                prefsEditor.apply();
            }
        });

        textListener(holder.ec1, holder);
        textListener(holder.ec2, holder);
        textListener(holder.ec3, holder);
        textListener(holder.ec4, holder);
        textListener(holder.ec5, holder);
        textListener(holder.ec6, holder);
        textListener(holder.ec7, holder);
        textListener(holder.ec8, holder);
        textListener(holder.ec9, holder);
        textListener(holder.ec10, holder);


    }

    private void textListener(EditText ec, ViewHolder holder) {
        final int[] prev = {0};
        final int[] nxt = {0};
        final int[] dif = {0};
        ec.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                try {
                    prev[0] = Integer.parseInt(s.toString());

                } catch (NumberFormatException e) {
                    prev[0] = 0;
                }


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {



                try {
                    if (Integer.parseInt(s.toString()) > 0 && Integer.parseInt(s.toString()) <= 10) {
                        ec.setError(null);

                    } else {
                        ec.setError("Marks must be in 1-10 range.");
                    }
                    nxt[0] = Integer.parseInt(s.toString());
                } catch (NumberFormatException e) {
                    ec.setError("Marks must be in 1-10 range.");

                    nxt[0] = 0;
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


                dif[0] = nxt[0] - prev[0];
                try {
                    holder.ts.setText(String.valueOf(Integer.parseInt(holder.ts.getText().toString()) + dif[0]));

                } catch (NumberFormatException e) {
                    holder.ts.setText(String.valueOf(0 + dif[0]));

                }
                switch (ec.getId()) {

                    case R.id.markEt1:
                        try {
                            markList.get(holder.getLayoutPosition()).setEt1(s.toString());

                        } catch (IndexOutOfBoundsException e) {
                            textFieldList textFieldList = new textFieldList();
                            textFieldList.setEt1(s.toString());
                            markList.add(holder.getLayoutPosition(), textFieldList);
                        }
                        break;
                    case R.id.markEt2:
                        try {
                            markList.get(holder.getLayoutPosition()).setEt2(s.toString());

                        } catch (IndexOutOfBoundsException e) {
                            textFieldList textFieldList = new textFieldList();
                            textFieldList.setEt2(s.toString());
                            markList.add(holder.getLayoutPosition(), textFieldList);
                        }
                        break;
                    case R.id.markEt3:
                        try {
                            markList.get(holder.getLayoutPosition()).setEt3(s.toString());

                        } catch (IndexOutOfBoundsException e) {
                            textFieldList textFieldList = new textFieldList();
                            textFieldList.setEt3(s.toString());
                            markList.add(holder.getLayoutPosition(), textFieldList);
                        }
                        break;
                    case R.id.markEt4:
                        try {
                            markList.get(holder.getLayoutPosition()).setEt4(s.toString());

                        } catch (IndexOutOfBoundsException e) {
                            textFieldList textFieldList = new textFieldList();
                            textFieldList.setEt4(s.toString());
                            markList.add(holder.getLayoutPosition(), textFieldList);
                        }
                        break;
                    case R.id.markEt5:
                        try {
                            markList.get(holder.getLayoutPosition()).setEt5(s.toString());

                        } catch (IndexOutOfBoundsException e) {
                            textFieldList textFieldList = new textFieldList();
                            textFieldList.setEt5(s.toString());
                            markList.add(holder.getLayoutPosition(), textFieldList);
                        }
                        break;
                    case R.id.markEt6:
                        try {
                            markList.get(holder.getLayoutPosition()).setEt6(s.toString());

                        } catch (IndexOutOfBoundsException e) {
                            textFieldList textFieldList = new textFieldList();
                            textFieldList.setEt6(s.toString());
                            markList.add(holder.getLayoutPosition(), textFieldList);
                        }
                        break;
                    case R.id.markEt7:
                        try {
                            markList.get(holder.getLayoutPosition()).setEt7(s.toString());

                        } catch (IndexOutOfBoundsException e) {
                            textFieldList textFieldList = new textFieldList();
                            textFieldList.setEt7(s.toString());
                            markList.add(holder.getLayoutPosition(), textFieldList);
                        }
                        break;
                    case R.id.markEt8:
                        try {
                            markList.get(holder.getLayoutPosition()).setEt8(s.toString());

                        } catch (IndexOutOfBoundsException e) {
                            textFieldList textFieldList = new textFieldList();
                            textFieldList.setEt8(s.toString());
                            markList.add(holder.getLayoutPosition(), textFieldList);
                        }
                        break;
                    case R.id.markEt9:
                        try {
                            markList.get(holder.getLayoutPosition()).setEt9(s.toString());

                        } catch (IndexOutOfBoundsException e) {
                            textFieldList textFieldList = new textFieldList();
                            textFieldList.setEt9(s.toString());
                            markList.add(holder.getLayoutPosition(), textFieldList);
                        }
                        break;
                    case R.id.markEt10:
                        try {
                            markList.get(holder.getLayoutPosition()).setEt10(s.toString());

                        } catch (IndexOutOfBoundsException e) {
                            textFieldList textFieldList = new textFieldList();
                            textFieldList.setEt10(s.toString());
                            markList.add(holder.getLayoutPosition(), textFieldList);
                        }
                        break;
                    default:
                        break;


                }
                SharedPreferences sp = mContext.getSharedPreferences("markTF", Context.MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = sp.edit();
                Gson gson = new Gson();
                String json = gson.toJson(markList);
                prefsEditor.putString(contestKey, json);
                prefsEditor.apply();


            }
        });

    }

    private void getUsername(ViewHolder holder, String ui) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(mContext.getString(R.string.dbname_users))
                .child(ui)
                .child(mContext.getString(R.string.field_username))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            holder.username.setText(snapshot.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void setMarks(ParticipantList participant, ViewHolder holder) {


        if (fetchFromDB) {

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child(mContext.getString(R.string.dbname_participantList))
                    .child(participant.getCi())
                    .child(participant.getJi())
                    .child(mContext.getString(R.string.juryMarks))
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {

                                String[] marksArray = new String[10];
                                marksArray = snapshot.child(xj).getValue().toString().split("///");
                                holder.ts.setText(snapshot.child(jury).getValue().toString());
                                holder.feedBack.setText(snapshot.child(comment).getValue().toString());
                                putMarks(marksArray, holder);


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } else {
            SharedPreferences sp = mContext.getSharedPreferences("markTF", Context.MODE_PRIVATE);
            Gson gson = new Gson();
            String json = sp.getString(contestKey, null);
            Type type = new TypeToken<ArrayList<textFieldList>>() {
            }.getType();
            ArrayList<textFieldList> textFieldList1 = gson.fromJson(json, type);
            try {


                markList = new ArrayList<>(textFieldList1);
                holder.ts.setText(textFieldList1.get(holder.getLayoutPosition()).getTotal());
                holder.feedBack.setText(textFieldList1.get(holder.getLayoutPosition()).getFeedback());

                String[] marksArray = new String[10];
                for (int x = 0; x < 10; x++) {
                    if (x == 0) {
                        String num = textFieldList1.get(holder.getLayoutPosition()).getEt1();
                        if (num != null) {
                            marksArray[x] = num;
                        }

                    } else if (x == 1) {
                        String num = textFieldList1.get(holder.getLayoutPosition()).getEt2();
                        if (num != null) {

                            marksArray[x] = num;
                        }
                    } else if (x == 2) {
                        String num = textFieldList1.get(holder.getLayoutPosition()).getEt3();
                        if (num != null) {

                            marksArray[x] = num;
                        }
                    } else if (x == 3) {
                        String num = textFieldList1.get(holder.getLayoutPosition()).getEt4();
                        if (num != null) {

                            marksArray[x] = num;
                        }
                    } else if (x == 4) {
                        String num = textFieldList1.get(holder.getLayoutPosition()).getEt5();
                        if (num != null) {

                            marksArray[x] = num;
                        }
                    } else if (x == 5) {
                        String num = textFieldList1.get(holder.getLayoutPosition()).getEt6();
                        if (num != null) {

                            marksArray[x] = num;
                        }
                    } else if (x == 6) {
                        String num = textFieldList1.get(holder.getLayoutPosition()).getEt7();
                        if (num != null) {

                            marksArray[x] = num;
                        }
                    } else if (x == 7) {
                        String num = textFieldList1.get(holder.getLayoutPosition()).getEt8();
                        if (num != null) {

                            marksArray[x] = num;
                        }
                    } else if (x == 8) {
                        String num = textFieldList1.get(holder.getLayoutPosition()).getEt9();
                        if (num != null) {

                            marksArray[x] = num;
                        }
                    } else if (x == 9) {
                        String num = textFieldList1.get(holder.getLayoutPosition()).getEt10();
                        if (num != null) {

                            marksArray[x] = num;
                        }
                    }
                }
                putMarks(marksArray, holder);

            } catch (IndexOutOfBoundsException e) {


            }

        }


    }

    private void putMarks(String[] marksArray, ViewHolder holder) {
        Log.d(TAG, "putMarks: " + Arrays.toString(marksArray));

        if (marksArray.length > 0) {
            for (int x = 1; x <= marksArray.length; x++) {

                if (x == 1) {
                    holder.ec1.setText(marksArray[x - 1]);
                } else if (x == 2) {
                    holder.ec2.setText(marksArray[x - 1]);

                } else if (x == 3) {
                    holder.ec3.setText(marksArray[x - 1]);

                } else if (x == 4) {
                    holder.ec4.setText(marksArray[x - 1]);

                } else if (x == 5) {
                    holder.ec5.setText(marksArray[x - 1]);

                } else if (x == 6) {
                    holder.ec6.setText(marksArray[x - 1]);

                } else if (x == 7) {
                    holder.ec7.setText(marksArray[x - 1]);

                } else if (x == 8) {
                    holder.ec8.setText(marksArray[x - 1]);

                } else if (x == 9) {
                    holder.ec9.setText(marksArray[x - 1]);

                } else if (x == 10) {
                    holder.ec10.setText(marksArray[x - 1]);

                }
            }
        }
    }

    private void setRecyclerViews(ViewHolder holder) {
        for (int x = 1; x <= criterias.length; x++) {
            if (x == 1) {
                holder.lec1.setVisibility(View.VISIBLE);
                holder.c1.setText(criterias[x - 1]);
            } else if (x == 2) {
                holder.lec2.setVisibility(View.VISIBLE);
                holder.c2.setText(criterias[x - 1]);
            } else if (x == 3) {
                holder.lec3.setVisibility(View.VISIBLE);
                holder.c3.setText(criterias[x - 1]);
            } else if (x == 4) {
                holder.lec4.setVisibility(View.VISIBLE);
                holder.c4.setText(criterias[x - 1]);
            } else if (x == 5) {
                holder.lec5.setVisibility(View.VISIBLE);
                holder.c5.setText(criterias[x - 1]);
            } else if (x == 6) {
                holder.lec6.setVisibility(View.VISIBLE);
                holder.c6.setText(criterias[x - 1]);
            } else if (x == 7) {
                holder.lec7.setVisibility(View.VISIBLE);
                holder.c7.setText(criterias[x - 1]);
            } else if (x == 8) {
                holder.lec8.setVisibility(View.VISIBLE);
                holder.c8.setText(criterias[x - 1]);
            } else if (x == 9) {
                holder.lec9.setVisibility(View.VISIBLE);
                holder.c9.setText(criterias[x - 1]);
            } else if (x == 10) {
                holder.lec10.setVisibility(View.VISIBLE);
                holder.c10.setText(criterias[x - 1]);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public long getItemId(int position) {

        return participantLists.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return participantLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView username, c1, c2, c3, c4, c5, c6, c7, c8, c9, c10, count, des, View, ts;
        private EditText ec1, ec2, ec3, ec4, ec5, ec6, ec7, ec8, ec9, ec10, feedBack;
        private LinearLayout lec1, lec2, lec3, lec4, lec5, lec6, lec7, lec8, lec9, lec10;


        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            username = itemView.findViewById(R.id.username);
            image = itemView.findViewById(R.id.img);
            count = itemView.findViewById(R.id.count);
            des = itemView.findViewById(R.id.des);
            View = itemView.findViewById(R.id.view);
            ts = itemView.findViewById(R.id.tsEt);
            feedBack = itemView.findViewById(R.id.feedback);


            lec1 = itemView.findViewById(R.id.markLL1);
            lec2 = itemView.findViewById(R.id.markLL2);
            lec3 = itemView.findViewById(R.id.markLL3);
            lec4 = itemView.findViewById(R.id.markLL4);
            lec5 = itemView.findViewById(R.id.markLL5);
            lec6 = itemView.findViewById(R.id.markLL6);
            lec7 = itemView.findViewById(R.id.markLL7);
            lec8 = itemView.findViewById(R.id.markLL8);
            lec9 = itemView.findViewById(R.id.markLL9);
            lec10 = itemView.findViewById(R.id.markLL10);

            ec1 = itemView.findViewById(R.id.markEt1);
            ec2 = itemView.findViewById(R.id.markEt2);
            ec3 = itemView.findViewById(R.id.markEt3);
            ec4 = itemView.findViewById(R.id.markEt4);
            ec5 = itemView.findViewById(R.id.markEt5);
            ec6 = itemView.findViewById(R.id.markEt6);
            ec7 = itemView.findViewById(R.id.markEt7);
            ec8 = itemView.findViewById(R.id.markEt8);
            ec9 = itemView.findViewById(R.id.markEt9);
            ec10 = itemView.findViewById(R.id.markEt10);

            c1 = itemView.findViewById(R.id.cr1);
            c2 = itemView.findViewById(R.id.cr2);
            c3 = itemView.findViewById(R.id.cr3);
            c4 = itemView.findViewById(R.id.cr4);
            c5 = itemView.findViewById(R.id.cr5);
            c6 = itemView.findViewById(R.id.cr6);
            c7 = itemView.findViewById(R.id.cr7);
            c8 = itemView.findViewById(R.id.cr8);
            c9 = itemView.findViewById(R.id.cr9);
            c10 = itemView.findViewById(R.id.cr10);


        }
    }


}


