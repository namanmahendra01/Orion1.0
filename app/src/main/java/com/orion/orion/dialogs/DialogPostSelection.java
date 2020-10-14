package com.orion.orion.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.orion.orion.R;
import com.orion.orion.profile.PostPhotoActivity;
import com.orion.orion.profile.PostVideoActivity;

public class DialogPostSelection extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public LinearLayout btnPhoto, btnVideo;

    public DialogPostSelection(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_post_type);
        btnPhoto = (LinearLayout) findViewById(R.id.btnPhoto);
        btnVideo = (LinearLayout) findViewById(R.id.btnVideo);
        btnPhoto.setOnClickListener(this);
        btnVideo.setOnClickListener(this);

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPhoto:
                c.startActivity(new Intent(c, PostPhotoActivity.class));
                break;
            case R.id.btnVideo:
                c.startActivity(new Intent(c, PostVideoActivity.class));
                break;
            default:
                break;
        }
        dismiss();
    }
}
