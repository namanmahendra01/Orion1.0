package com.orion.orion.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.orion.orion.R;

public class BottomSheetDomain extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bottom_sheet_domain, container, false);

        TextView photography = v.findViewById(R.id.photography);
        TextView filmMaker = v.findViewById(R.id.filmMaker);
        TextView musician = v.findViewById(R.id.musician);
        TextView sketchArtist = v.findViewById(R.id.sketchArtist);
        TextView writer = v.findViewById(R.id.writer);
        TextView others = v.findViewById(R.id.others);

        photography.setOnClickListener(v1 -> {
            mListener.onButtonClicked("Photography");
            dismiss();
        });
        filmMaker.setOnClickListener(v1 -> {
            mListener.onButtonClicked("Film Maker");
            dismiss();
        });
        musician.setOnClickListener(v1 -> {
            mListener.onButtonClicked("Musician");
            dismiss();
        });
        sketchArtist.setOnClickListener(v1 -> {
            mListener.onButtonClicked("Sketch Artist");
            dismiss();
        });
        writer.setOnClickListener(v1 -> {
            mListener.onButtonClicked("Writer");
            dismiss();
        });
        others.setOnClickListener(v1 -> {
            mListener.onButtonClicked("Others");
            dismiss();
        });
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