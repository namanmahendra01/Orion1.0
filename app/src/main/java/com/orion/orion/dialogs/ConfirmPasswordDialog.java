package com.orion.orion.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.orion.orion.R;

import java.util.zip.Inflater;

public class ConfirmPasswordDialog extends DialogFragment {
    private static final String TAG = "ConfirmPasswordDialog";
    public  interface OnComfirmPadsswordListener{
        public void onConfirmPassword(String password);
    }
    OnComfirmPadsswordListener mOnComfirmPadsswordListener;
    TextView mPassword;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_confirm_password,container,false);
        TextView canceldialog = (TextView) view.findViewById(R.id.dialogCancel);
        mPassword=(TextView)view.findViewById(R.id.confirm_password);
        canceldialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        TextView confirmdialog = (TextView) view.findViewById(R.id.dialogConfirm);
        confirmdialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             String password = mPassword.getText().toString();
             if(!password.equals("")){
                 mOnComfirmPadsswordListener.onConfirmPassword(password);
                 getDialog().dismiss();
             }else{
                 Toast.makeText(getActivity(), "You must enter your password", Toast.LENGTH_SHORT).show();
             }
            }
        });
        return  view;

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnComfirmPadsswordListener=(OnComfirmPadsswordListener) getTargetFragment();
        }catch (ClassCastException e){
            Log.e(TAG,"onAttach:ClassCarException:" + e.getMessage());
        }
    }
}
