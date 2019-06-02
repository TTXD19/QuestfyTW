package taiwan.questfy.welsenho.questfy_tw.LoginRelated;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

import taiwan.questfy.welsenho.questfy_tw.MainUserActivity.MainActivity;
import taiwan.questfy.welsenho.questfy_tw.R;

public class SignUpMethod {


    public void firebaseProfileSignUp(DatabaseReference databaseReference, String Uid, final String email, String ID, String sex, String loginType,
                                      final Context context, String createDate, String userUid){

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Email", email);
        hashMap.put("ID", ID);
        hashMap.put("Sex", sex);
        hashMap.put("User_image_uri", "https://firebasestorage.googleapis.com/v0/b/questfytw.appspot.com/o/Default_Image_ForEach_Condition%2Fuser%20(1).png?alt=media&token=5122a33f-5392-4877-be3d-4f519550c9b6");
        hashMap.put("createDate", createDate);
        hashMap.put("loginType", loginType);
        hashMap.put("CompleteInformationCheck", "False");
        hashMap.put("userUid", userUid);
        databaseReference.child("Users_profile").child(Uid).updateChildren(hashMap);
    }

    public void setUpFirebaseProfile(FirebaseUser firebaseProfile, String ID){
        Uri photoUri = Uri.parse("https://firebasestorage.googleapis.com/v0/b/questfytw.appspot.com/o/Default_Image_ForEach_Condition%2Fuser%20(1).png?alt=media&token=5122a33f-5392-4877-be3d-4f519550c9b6");
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(ID)
                .setPhotoUri(photoUri)
                .build();
        firebaseProfile.updateProfile(profileUpdate);
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
