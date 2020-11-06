package com.orion.orion.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.orion.orion.Adapters.AdapterContestSearch;
import com.orion.orion.Adapters.AdapterDomain;
import com.orion.orion.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BottomSheetDomain extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;
    RecyclerView domainRv;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bottom_sheet_domain, container, false);

        domainRv=v.findViewById(R.id.domainRV);
        domainRv.setHasFixedSize(true);
        final GridLayoutManager linearLayoutManager1 = new GridLayoutManager(getContext(), 3);

        domainRv.setLayoutManager(linearLayoutManager1);

        List<String> tags = Arrays.asList(getResources().getStringArray(R.array.domain2));
        AdapterDomain adapterDomain = new AdapterDomain(getContext(), tags);
        domainRv.setAdapter(adapterDomain);


//        photography.setOnClickListener(v1 -> {
//            mListener.onButtonClicked("Photography");
//            dismiss();
//        });
//        filmMaker.setOnClickListener(v1 -> {
//            mListener.onButtonClicked("Film Maker");
//            dismiss();
//        });
//        musician.setOnClickListener(v1 -> {
//            mListener.onButtonClicked("Musician");
//            dismiss();
//        });
//        sketchArtist.setOnClickListener(v1 -> {
//            mListener.onButtonClicked("Sketch Artist");
//            dismiss();
//        });
//        writer.setOnClickListener(v1 -> {
//            mListener.onButtonClicked("Writer");
//            dismiss();
//        });
//        others.setOnClickListener(v1 -> {
//            mListener.onButtonClicked("Others");
//            dismiss();
//        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    public interface BottomSheetListener {
        void onButtonClicked(String text);
    }
}