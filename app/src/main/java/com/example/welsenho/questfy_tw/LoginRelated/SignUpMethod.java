package com.example.welsenho.questfy_tw.LoginRelated;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class SignUpMethod {

    public void firebaseProfileSignUp(DatabaseReference databaseReference, String Uid, String email, String ID, String realName, String sex, String loginType,
                                      final Context context, final ProgressDialog progressDialog){



        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Email", email);
        hashMap.put("ID", ID);
        hashMap.put("Real_Name", realName);
        hashMap.put("Sex", sex);
        hashMap.put("loginType", loginType);
        databaseReference.child("Users_profile").child(Uid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()){
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, "Register profile failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
