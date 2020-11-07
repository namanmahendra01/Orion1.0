package com.orion.orion.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.orion.orion.Adapters.AdapterContestSearch;
import com.orion.orion.Adapters.AdapterDomain;
import com.orion.orion.R;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BottomSheetDomain extends BottomSheetDialogFragment implements AdapterDomain.OnItemClickListener{

    private static final String TAG = "BOTTOM_SHEET_DOMAIN";
    private BottomSheetListener mListener;
    RecyclerView domainRv;
    List<String> tags;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bottom_sheet_domain, container, false);

        domainRv=v.findViewById(R.id.domainRV);
        domainRv.setHasFixedSize(true);
        final GridLayoutManager linearLayoutManager1 = new GridLayoutManager(getContext(), 3);

        domainRv.setLayoutManager(linearLayoutManager1);

        tags = Arrays.asList(getResources().getStringArray(R.array.domain2));
        tags = tags.subList(1, tags.size());
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
        Log.d(TAG, "DOMAIN: "+tags.get(position));
        dismiss();
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
}