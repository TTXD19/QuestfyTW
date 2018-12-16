package com.example.welsenho.questfy_tw.LoginRelated;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpAcivity extends AppCompatActivity {


    private String Uid;
    private String sex;
    private String email;
    private String password;
    private String confirmPassword;
    private String ID;
    private String realName;
    private String loginType;


    private TextView txtMissing;
    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editID;
    private EditText editRealName;
    private Button btnRegister;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private ProgressDialog progressDialog;
    private ScrollView scrollView;

    private SignUpMethod signUpMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_acivity);

        txtMissing = findViewById(R.id.txtMissing);
        editEmail = findViewById(R.id.editEmailRegis);
        editPassword = findViewById(R.id.editPasswordRegis);
        editConfirmPassword = findViewById(R.id.editConfirmPasswordRegis);
        editID = findViewById(R.id.editIDRegis);
        editRealName = findViewById(R.id.editRealNameRegis);
        btnRegister = findViewById(R.id.btnRegister);
        scrollView = findViewById(R.id.scrollRegister);
        radioGroup = findViewById(R.id.radioGpSignUp);



        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Registering...");
        progressDialog.setMessage("Creating your account");

        signUpMethod = new SignUpMethod();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkSex();
                if (checkSex() == null){
                    txtMissing.setText("Please select your sex");
                    txtMissing.setVisibility(View.VISIBLE);
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }else {
                    register();
                }
            }
        });

    }


    private void register(){

        progressDialog.show();
        email = editEmail.getText().toString();
        password = editPassword.getText().toString();
        confirmPassword = editConfirmPassword.getText().toString();
        ID = editID.getText().toString();
        realName = editRealName.getText().toString();
        loginType = "false";

        if (!email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && !ID.isEmpty() && !realName.isEmpty()) {
            progressDialog.setMessage("Please hold on a moment while we log in your account.");
            progressDialog.setTitle("Logging in");
            progressDialog.show();
            if (password.equals(confirmPassword)) {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpAcivity.this, R.string.regis_type_success, Toast.LENGTH_SHORT).show();
                            Uid = firebaseAuth.getUid();
                            signUpMethod.firebaseProfileSignUp(databaseReference, Uid, email, ID, realName, sex, loginType, getApplicationContext());
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            signUpMethod.emailVarification(firebaseUser, getApplicationContext());
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(ID).build();
                            if (firebaseUser != null) {
                                firebaseUser.updateProfile(profileUpdates);
                            }
                        } else {
                            Toast.makeText(SignUpAcivity.this, R.string.regis_type_failed, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }else {
                progressDialog.dismiss();
                txtMissing.setText(R.string.regis_type_password_not_equal);
                txtMissing.setVisibility(View.VISIBLE);
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        }else {
            progressDialog.dismiss();
            txtMissing.setText(R.string.regis_missing);
            txtMissing.setVisibility(View.VISIBLE);
            scrollView.fullScroll(View.FOCUS_DOWN);
        }
        progressDialog.dismiss();
    }

    private String checkSex(){
        //-1代表radioGroup.getCheckedRadioButtonId()未被選擇
        if (radioGroup.getCheckedRadioButtonId() != -1){
            int checkSex = radioGroup.getCheckedRadioButtonId();
            radioButton = findViewById(checkSex);
            sex = radioButton.getText().toString();
        }else {
            sex = null;
        }
        return sex;
    }



}
