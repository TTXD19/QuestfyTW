package taiwan.questfy.welsenho.questfy_tw.LoginRelated;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import taiwan.questfy.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import taiwan.questfy.welsenho.questfy_tw.MainUserActivity.MainActivity;
import taiwan.questfy.welsenho.questfy_tw.R;

public class FacebookRegister extends AppCompatActivity {

    private EditText editUserName;
    private Button btnRegister;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ProgressDialog progressDialog;
    private EditRelatedMethod editRelatedMethod;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private AccessToken accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_register);

        InitFirebase();
        InitItem();
        ItemClick();
    }

    private void InitItem(){
        editUserName = findViewById(R.id.facebook_register_editIDRegis);
        btnRegister = findViewById(R.id.facebook_register_btnRegister);
        radioGroup = findViewById(R.id.facebook_register_radioGpSignUp);
        editRelatedMethod = new EditRelatedMethod();
        progressDialog = new ProgressDialog(this);
        editUserName.setText(firebaseUser.getDisplayName());
    }

    private void InitFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        accessToken = AccessToken.getCurrentAccessToken();
    }

    private String checkSex() {
        String sex;
        //-1代表radioGroup.getCheckedRadioButtonId()未被選擇
        if (radioGroup.getCheckedRadioButtonId() != -1) {
            int checkSex = radioGroup.getCheckedRadioButtonId();
            radioButton = findViewById(checkSex);
            sex = radioButton.getText().toString();
        } else {
            sex = null;
        }
        return sex;
    }

    private void ItemClick(){
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSex() != null) {
                    FacebookRegister();
                }else {
                    Toast.makeText(FacebookRegister.this, "Must choose a gender", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void FacebookRegister(){
        if (!editUserName.getText().toString().isEmpty()) {
            progressDialog.setTitle("Account Registering");
            progressDialog.setMessage("Please hold on for a moment");
            progressDialog.show();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("Email", firebaseUser.getEmail());
            hashMap.put("ID", editUserName.getText().toString());
            hashMap.put("Sex", checkSex());
            if (firebaseUser.getPhotoUrl() == null) {
                hashMap.put("User_image_uri", "https://firebasestorage.googleapis.com/v0/b/questfytw.appspot.com/o/Default_Image_ForEach_Condition%2Fuser%20(1).png?alt=media&token=5122a33f-5392-4877-be3d-4f519550c9b6");
            }else {
                hashMap.put("User_image_uri", firebaseUser.getPhotoUrl().toString());
            }
            hashMap.put("createDate", editRelatedMethod.getUploadDate());
            hashMap.put("loginType", "Facebook");
            hashMap.put("CompleteInformationCheck", "False");
            hashMap.put("userUid", firebaseUser.getUid());
            databaseReference.child("Users_profile").child(firebaseUser.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(FacebookRegister.this, MainActivity.class);
                        startActivity(intent);
                        progressDialog.dismiss();
                        finish();
                    }
                }
            });
        }else {
            Toast.makeText(this, "Id is empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void DestoryToken(){
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(FacebookRegister.this, "User token delete", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FacebookRegister.this, LoginActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }
}
