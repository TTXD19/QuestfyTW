package taiwan.questfy.welsenho.questfy_tw.ReigisterCompleteInfoRelated;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import taiwan.questfy.welsenho.questfy_tw.R;

public class RealNameRegisterActivity extends AppCompatActivity {

    private EditText editName;
    private Button btnNext;

    private RegisterCompleteInfoMethods registerCompleteInfoMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_name_register);

        InitItem();

        if (getIntent().getStringExtra("realName") != null){
            editName.setText(getIntent().getStringExtra("realName"));
        }

        btnNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString();
                if (registerCompleteInfoMethods.checkTypeInWord(name)) {
                    Toast.makeText(RealNameRegisterActivity.this, "Name can not contain [~#@*+%{}<>\\[\\]|\"\\_^]", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()){
                    Toast.makeText(RealNameRegisterActivity.this, "Name can not be empty", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(RealNameRegisterActivity.this, CurrentDegreeChoose.class);
                    intent.putExtra("realName", name);
                    startActivity(intent);
                    finish();
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
