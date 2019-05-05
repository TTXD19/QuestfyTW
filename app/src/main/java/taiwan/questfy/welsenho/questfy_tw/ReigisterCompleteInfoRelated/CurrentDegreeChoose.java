package taiwan.questfy.welsenho.questfy_tw.ReigisterCompleteInfoRelated;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



import taiwan.questfy.welsenho.questfy_tw.R;

public class CurrentDegreeChoose extends AppCompatActivity {

    private String realName;
    private String currentDegree;

    private TextView txtUniversityStudent;
    private TextView txtHighSchoolStudnet;
    private Button btnBackPress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_degree_choose);

        txtUniversityStudent = findViewById(R.id.currentDegree_choose_txtUniversityStudent);
        txtHighSchoolStudnet = findViewById(R.id.currentDegree_choose_txtHightSchoolStudent);
        btnBackPress = findViewById(R.id.currentDegree_chooose_btnPrevious);

        realName = getIntent().getStringExtra("realName");

        txtUniversityStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDegree = "UniversityInfo";
                Intent intent = new Intent(CurrentDegreeChoose.this, UniversityRegister.class);
                intent.putExtra("realName",realName);
                intent.putExtra("currentDegree", currentDegree);
                startActivity(intent);
                finish();
            }
        });

        txtHighSchoolStudnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentDegree = "HighSchoolInfo";
                Intent intent = new Intent(CurrentDegreeChoose.this, UniversityRegister.class);
                intent.putExtra("realName",realName);
                intent.putExtra("currentDegree", currentDegree);
                startActivity(intent);
                finish();
            }
        });

        btnBackPress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(CurrentDegreeChoose.this, RealNameRegisterActivity.class);
        intent.putExtra("realName",realName);
        startActivity(intent);
    }
}
