package com.example.welsenho.questfy_tw.LoginRelated;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.welsenho.questfy_tw.R;

public class SignUpAcivity extends AppCompatActivity {

    private EditText editEmail;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editID;
    private EditText editRealName;
    private Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_acivity);

        editEmail = findViewById(R.id.editEmailRegis);
        editPassword = findViewById(R.id.editPasswordRegis);
        editConfirmPassword = findViewById(R.id.editConfirmPasswordRegis);
        editID = findViewById(R.id.editIDRegis);
        editRealName = findViewById(R.id.editRealNameRegis);
    }
}
