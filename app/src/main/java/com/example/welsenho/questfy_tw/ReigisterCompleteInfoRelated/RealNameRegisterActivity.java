package com.example.welsenho.questfy_tw.ReigisterCompleteInfoRelated;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RealNameRegisterActivity extends AppCompatActivity {

    private EditText editName;
    private Button btnNext;

    private RegisterCompleteInfoMethods registerCompleteInfoMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name_register);

        InitItem();
        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                if (registerCompleteInfoMethods.checkTypeInWord(name)) {
                    Toast.makeText(RealNameRegisterActivity.this, "Name can not contain [~#@*+%{}<>\\[\\]|\"\\_^]", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()){
                    Toast.makeText(RealNameRegisterActivity.this, "Name can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RealNameRegisterActivity.this, BirthdayRegisterActivity.class);
                    intent.putExtra("realName", name);
                    startActivity(intent);
                }
            }
        });

    }



    private void InitItem(){
        editName = findViewById(R.id.real_name_register_editName);
        btnNext = findViewById(R.id.real_name_register_btnNext);
        registerCompleteInfoMethods = new RegisterCompleteInfoMethods();
    }


}
