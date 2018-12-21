package com.example.welsenho.questfy_tw.EditActivityRelated;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditQuestionRelateMajorChose extends AppCompatActivity{

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button btnDone;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<String> getMajors;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private FirebaseAuth firebaseAuth;
    private EditQuestionRelateRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question_relate_major_chose);

        recyclerView = findViewById(R.id.edit_choose_major_recyclerView);
        progressBar = findViewById(R.id.edit_choose_major_progressBar);
        btnDone = findViewById(R.id.edit_choose_major_btn_Done);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        getMajors = new ArrayList<>();
        arrayList = new ArrayList<>();
        adapter = new EditQuestionRelateRecyclerViewAdapter(arrayList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        getData();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditQuestionRelateMajorChose.this, EditInitActivity.class);
                if (!getMajors.isEmpty()){
                    intent.putExtra("getMajors", getMajors);
                    startActivity(intent);
                }else {
                    startActivity(intent);
                }
                finish();

            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReciever, new IntentFilter("Custom_message"));
    }

    public BroadcastReceiver mMessageReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("CheckSize", intent.getStringArrayListExtra("Majors").toString());
            getMajors = intent.getStringArrayListExtra("Majors");
        }
    };

    private void getData(){
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child("Choose_majors_section").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot DS : dataSnapshot.getChildren()){
                        Log.d("Tag", "getMessage");
                        firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                        Log.d("Tag", firebaseDatabaseGetSet.getMajor());
                        recyclerView.setAdapter(adapter);
                    }
                }else {
                    Toast.makeText(EditQuestionRelateMajorChose.this, "Failed", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
