package taiwan.questfy.welsenho.questfy_tw.ReigisterCompleteInfoRelated;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import taiwan.questfy.welsenho.questfy_tw.R;

public class BirthdayRegisterActivity extends AppCompatActivity implements DatePickerFragment.returnDate{

    private String realName;
    private String birthdayDate;

    private EditText editBirthday;
    private Button btnNext;
    private Button btnPrevious;

    private RegisterCompleteInfoMethods registerCompleteInfoMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_register);

        realName = getIntent().getStringExtra("realName");
        Toast.makeText(this, realName, Toast.LENGTH_SHORT).show();
        InitItem();
        ItemClick();


    }


    private void InitItem(){
        editBirthday = findViewById(R.id.birthday_register_editName);
        btnNext = findViewById(R.id.birthday_register_btnNext);
        btnPrevious = findViewById(R.id.birthday_register_btnPrevious);
        registerCompleteInfoMethods = new RegisterCompleteInfoMethods();
    }

    private void ItemClick(){

        editBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (birthdayDate != null) {
                    Intent intent = new Intent(BirthdayRegisterActivity.this, UniversityRegister.class);
                    intent.putExtra("realName", realName);
                    intent.putExtra("birthdayDate", birthdayDate);
                    startActivity(intent);
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void NextStep(){
        String birthday = editBirthday.getText().toString();
        Intent intent = new Intent(BirthdayRegisterActivity.this, RealNameRegisterActivity.class);
        intent.putExtra("Birthday", birthday);
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void getDate(String date) {
        editBirthday.setText(date);
        birthdayDate = date;
    }
}
