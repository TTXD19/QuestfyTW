package com.example.welsenho.questfy_tw.LoginRelated;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.InternetConnectionDetect;
import com.example.welsenho.questfy_tw.MainUserActivity.OutOfConnectionActivity;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private String email;
    private String password;

    private TextView txtWrongPassword;
    private TextView txtSignUp;
    private EditText editEmail;
    private EditText editPassword;
    private Button btnSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private SignUpMethod signUpMethod;
    private InternetConnectionDetect internetConnectionDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        txtSignUp = findViewById(R.id.new_login_txtSignUp);
        btnSignIn = findViewById(R.id.new_login_btnSignIn);
        editEmail = findViewById(R.id.new_login_editInputEmail);
        editPassword = findViewById(R.id.new_login_editInputPassword);
        txtWrongPassword = findViewById(R.id.new_login_txtWrongPassword);

        progressDialog = new ProgressDialog(this);
        signUpMethod = new SignUpMethod();
        internetConnectionDetect = new InternetConnectionDetect();

        progressDialog.setTitle("Signing your account");
        progressDialog.setMessage("Please hold on for a moment");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (getIntent().getStringExtra("Email") != null){
            editEmail.setText(getIntent().getStringExtra("Email"));
        }

        if (firebaseUser != null) {
            if (internetConnectionDetect.isNetworkAvailable(getApplicationContext())) {
                signUpMethod.autoLogin(firebaseUser, getApplicationContext());
                finish();
            }else {
                Intent intent = new Intent(LoginActivity.this, OutOfConnectionActivity.class);
                startActivity(intent);
            }

        }
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpAcivity.class);
                startActivity(intent);
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                email = editEmail.getText().toString();
                password = editPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    signUpMethod.signInMethod(firebaseAuth, email, password, getApplicationContext(), LoginActivity.this, progressDialog);
                }else{
                    txtWrongPassword.setText("Email and Password can not be empty");
                    txtWrongPassword.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
