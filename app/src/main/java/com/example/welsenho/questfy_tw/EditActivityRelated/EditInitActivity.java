package com.example.welsenho.questfy_tw.EditActivityRelated;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.welsenho.questfy_tw.R;

import java.util.ArrayList;

public class EditInitActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView txtChooseMajor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_init);

        toolbar = findViewById(R.id.edit_init_toolbar);
        txtChooseMajor = findViewById(R.id.edit_init_txt_chooseMajor);

        if (getIntent().getStringArrayListExtra("getMajors") != null) {
            String major = getIntent().getStringArrayListExtra("getMajors").toString();
            txtChooseMajor.setText(major);
        }

        /**
         * SET TOOL BAR
         */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * HANDLE EACH BUTTON AND TEXTVIEW CLICK
         */
        ItemClick();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_init_activity_next, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.EditInitItemNext:
                Intent intent = new Intent(EditInitActivity.this, EditDetailMeetUpActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void ItemClick(){
        txtChooseMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditInitActivity.this, EditQuestionRelateMajorChose.class);
                startActivity(intent);
            }
        });
    }


}
