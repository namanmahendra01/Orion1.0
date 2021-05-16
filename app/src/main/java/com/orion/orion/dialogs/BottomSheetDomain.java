package com.orion.orion.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.annotations.NotNull;
import com.orion.orion.Adapters.AdapterDomain;
import com.orion.orion.R;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSheetDomain extends BottomSheetDialogFragment implements AdapterDomain.OnItemClickListener {

    private static final String TAG = "BOTTOM_SHEET_DOMAIN";
    private BottomSheetListener mListener;
    RecyclerView domainRv;
    List<String> tags;
    boolean fromExplore;
    String USER_DOMAIN;

    public BottomSheetDomain() {
        fromExplore = false;
    }

    public BottomSheetDomain(boolean fromExplore, String USER_DOMAIN) {
        this.fromExplore = fromExplore;
        this.USER_DOMAIN = USER_DOMAIN;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bottom_sheet_domain, container, false);

        domainRv = v.findViewById(R.id.domainRV);
        domainRv.setHasFixedSize(true);
        final GridLayoutManager linearLayoutManager1 = new GridLayoutManager(getContext(), 3);

        domainRv.setLayoutManager(linearLayoutManager1);

        if (fromExplore) {
            tags = new ArrayList<>();
            tags.add("Overall");
            tags.add(USER_DOMAIN);
        } else {
            tags = Arrays.asList(getResources().getStringArray(R.array.domain2));
            tags = tags.subList(1, tags.size());
        }
        AdapterDomain adapterDomain = new AdapterDomain(getContext(), tags, this);
        domainRv.setAdapter(adapterDomain);
        return v;
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }

    @Override
    public void onItemClick(int position) {
        mListener.onButtonClicked(tags.get(position));
        Log.d(TAG, "DOMAIN: " + tags.get(position));
        dismiss();
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
}