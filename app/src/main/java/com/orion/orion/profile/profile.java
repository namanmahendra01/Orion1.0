package com.orion.orion.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.orion.orion.R;
import com.orion.orion.models.users;

public class profile extends AppCompatActivity {
    private static final String TAG = "profile";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");

        init();
    }
    private void init() {
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.calling_activity))) {
            if (intent.hasExtra(getString(R.string.intent_user))) {
                users user = intent.getParcelableExtra(getString(R.string.intent_user));
                if (!user.getUser_id().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    Log.d(TAG, "init: qaz0"+intent.getParcelableExtra(getString(R.string.intent_user)));
                    Intent i = new Intent(profile.this,ViewProfileActivity.class);
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.intent_user), intent.getParcelableExtra(getString(R.string.intent_user)));
                    i.putExtra(getString(R.string.intent_user),args);
                   startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                   Intent i = new Intent(profile.this,ProfileActivity.class);
                   startActivity(i);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent i = new Intent(profile.this,ProfileActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        }
    }
}

