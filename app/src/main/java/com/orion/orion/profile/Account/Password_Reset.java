package com.orion.orion.profile.Account;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.orion.orion.R;

public class Password_Reset extends AppCompatActivity {
    private static final String TAG = "Password_Reset";
    private String oldP="",newP="",confirmP="",email="";
    TextInputEditText oldE,newE,confirmE;
    Button confirm,cancel;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password__reset);

        oldE=findViewById(R.id.oldpswrd2);
        newE=findViewById(R.id.newpswrd2);
        confirmE=findViewById(R.id.confirm_password2);



        confirm=findViewById(R.id.confirm);
        cancel=findViewById(R.id.cancel);

        user= FirebaseAuth.getInstance().getCurrentUser();

        email=user.getEmail();

confirm.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        oldP=oldE.getText().toString();
        newP=newE.getText().toString();
        confirmP=confirmE.getText().toString();
        Boolean ok=checkEntries();
        if (ok){
            updatePassword();
        }
    }
});
cancel.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        finish();
    }
});


    }

    private void updatePassword() {
        AuthCredential credential= EmailAuthProvider.getCredential(email,oldP);
        Log.d(TAG, "updatePassword: "+oldP);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    user.updatePassword(newP).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(Password_Reset.this, "failed", Toast.LENGTH_SHORT).show();

                            }else{
                                Toast.makeText(Password_Reset.this, "Success", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });


                }else{
                    Toast.makeText(Password_Reset.this, "Wrong Credential", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private Boolean checkEntries() {
        if (newP.equals("")||oldP.equals("")||confirmP.equals("")){
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!newP.equals(confirmP)){
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return false;

        }else if (newP.equals(oldP)){
            Toast.makeText(this, "Please enter different password from old password", Toast.LENGTH_SHORT).show();
            return false;

        }


        else{
            return  true;
        }
    }
}