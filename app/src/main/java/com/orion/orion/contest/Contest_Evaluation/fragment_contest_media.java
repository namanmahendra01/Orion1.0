package com.orion.orion.contest.Contest_Evaluation;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orion.orion.Adapters.AdapterGridImageContest;
import com.orion.orion.Adapters.AdapterGridImageSub;
import com.orion.orion.R;
import com.orion.orion.models.JoinForm;
import com.orion.orion.models.ParticipantList;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

import static com.android.volley.VolleyLog.TAG;

public class fragment_contest_media extends Fragment {
    private static final int NUM_GRID_COLUMNS = 3;

    RecyclerView gridRv;
    ArrayList<ParticipantList> imgURLsList;
    private AdapterGridImageSub adapterGridImage;
    //    SP
    Gson gson;
    SharedPreferences sp;
    String Conteskey;
    public fragment_contest_media(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest_media, container, false);
        Bundle b=getActivity().getIntent().getExtras();
         Conteskey=b.getString("contestId");

//          Initialize SharedPreference variables
        sp = getContext().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        gson = new Gson();


        gridRv=(RecyclerView) view.findViewById(R.id.gridRv);


        gridRv.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3);
        gridRv.setLayoutManager(linearLayoutManager);

        imgURLsList=new ArrayList<>();
        adapterGridImage = new AdapterGridImageSub(getContext(),imgURLsList);
        adapterGridImage.setHasStableIds(true);
        gridRv.setAdapter(adapterGridImage);

        getParticipantListFromSP();


        return view;

    }
    //  fetching ParticipantList  from SharedPreferences
    private void getParticipantListFromSP() {
        String json = sp.getString(Conteskey, null);

        Type type = new TypeToken<ArrayList<ParticipantList>>() {
        }.getType();
        imgURLsList = gson.fromJson(json, type);
        if (imgURLsList == null||imgURLsList.size()==0) {    //        if no arrayList is present
            imgURLsList = new ArrayList<>();
            adapterGridImage = new AdapterGridImageSub(getContext(),imgURLsList);
            adapterGridImage.setHasStableIds(true);
            gridRv.setAdapter(adapterGridImage);

        } else {

            adapterGridImage = new AdapterGridImageSub(getContext(),imgURLsList);
            adapterGridImage.setHasStableIds(true);
            gridRv.setAdapter(adapterGridImage);

        }
    }

}
