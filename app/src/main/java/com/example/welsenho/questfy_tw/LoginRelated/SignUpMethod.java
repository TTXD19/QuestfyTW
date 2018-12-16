package com.example.welsenho.questfy_tw.LoginRelated;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.MainUserActivity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class SignUpMethod {

    public void firebaseProfileSignUp(DatabaseReference databaseReference, String Uid, final String email, String ID, String realName, String sex, String loginType,
                                      final Context context){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Email", email);
        hashMap.put("ID", ID);
        hashMap.put("Real_Name", realName);
        hashMap.put("Sex", sex);
        hashMap.put("loginType", loginType);
        databaseReference.child("Users_profile").child(Uid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(context, LoginActivity.class);
                    intent.putExtra("email", email);
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, "Register profile failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void signInMethod(FirebaseAuth mAuth, String email, String password, final Context context, final Activity activity){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                     if (task.isSuccessful()){
                         Log.d("Login : ", "success");
                         Intent intent = new Intent(context, MainActivity.class);
                         context.startActivity(intent);
                         activity.finish();
                     }else {
                         Log.d("Login : ", "not success");
                         final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                         builder.setMessage("Currently there is something wrong with the network. Please try again later.")
                         .setTitle("Oops !!!").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                             @Override
                             public void onClick(DialogInterface dialog, int which) {
                                 dialog.dismiss();
                             }
                         }).create();
                     }
            }
        });
    }


    public void autoLogin(FirebaseUser firebaseUser, Context context){
        if (firebaseUser != null){
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }
    }

    public void emailVarification(FirebaseUser firebaseUser, final Context context){
        if (firebaseUser.isEmailVerified()){
            Toast.makeText(context, "Email has verified", Toast.LENGTH_SHORT).show();
        }else {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(context, "Verification email has sent", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "Failed to send", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}
