package com.orion.orion.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.local.LruDelegate;
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
                    ViewProfileFragment fragment = new ViewProfileFragment();
                    Bundle args = new Bundle();
                    args.putParcelable(getString(R.string.intent_user), intent.getParcelableExtra(getString(R.string.intent_user)));
                    fragment.setArguments(args);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.commit();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                } else {
                    ProfileFragment fragment = new ProfileFragment();
                    FragmentTransaction transaction = profile.this.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.commit();
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
            } else {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        } else {
            ProfileFragment fragment = new ProfileFragment();
            FragmentTransaction transaction = profile.this.getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment);
            transaction.commit();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}

