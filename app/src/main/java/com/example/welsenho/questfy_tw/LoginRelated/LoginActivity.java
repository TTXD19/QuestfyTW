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

import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private String email;
    private String password;

    private TextView txtWrongPassword;
    private EditText editEmail;
    private EditText editPassword;
    private Button btnSignUp;
    private Button btnSignIn;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private SignUpMethod signUpMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignIn = findViewById(R.id.btnSignIn);
        editEmail = findViewById(R.id.editEmailLogin);
        editPassword = findViewById(R.id.editPasswordLogin);
        txtWrongPassword = findViewById(R.id.txtWrongPassword);

        progressDialog = new ProgressDialog(this);
        signUpMethod = new SignUpMethod();

        progressDialog.setTitle("Signing your account");


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            signUpMethod.autoLogin(firebaseUser, getApplicationContext());
            finish();
        }
        btnSignUp.setOnClickListener(new View.OnClickListener() {
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
                    signUpMethod.signInMethod(firebaseAuth, email, password, getApplicationContext(), LoginActivity.this);
                }else{
                    txtWrongPassword.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = new Intent();
        if (intent.getStringExtra("email") != null){
            editEmail.setText(intent.getStringExtra("email"));
        }
    }
}
