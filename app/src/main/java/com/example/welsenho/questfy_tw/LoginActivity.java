package com.example.welsenho.questfy_tw;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.LoginRelated.SignUpAcivity;
import com.example.welsenho.questfy_tw.LoginRelated.SignUpMethod;
import com.example.welsenho.questfy_tw.MainUserActivity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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

        firebaseAuth = FirebaseAuth.getInstance();

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
                email = editEmail.getText().toString();
                password = editPassword.getText().toString();
                if (!email.isEmpty() && !password.isEmpty()) {
                    signUpMethod.signInMethod(firebaseAuth, email, password, getApplicationContext(), progressDialog);
                }else{
                    txtWrongPassword.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "Resume", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        if (intent.getStringExtra("email") != null){
            editEmail.setText(intent.getStringExtra("email"));
        }
    }
}
