package com.example.welsenho.questfy_tw.ReigisterCompleteInfoRelated;

import android.app.ProgressDialog;
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
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.MainUserActivity.MainActivity;
import com.example.welsenho.questfy_tw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainCourseRegister extends AppCompatActivity {

    private String realName;
    private String birthdayDate;
    private String schooleName;
    private String courseName;

    private Button btnNext;
    private Button btnPrevious;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    public BroadcastReceiver broadcastReceiver;

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<String> getMainCourse;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private MainCourseChooseRecyclerAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_course_register);

        realName = getIntent().getStringExtra("realName");
        birthdayDate = getIntent().getStringExtra("birthdayDate");
        schooleName = getIntent().getStringExtra("schoolName");

        Toast.makeText(this, realName, Toast.LENGTH_SHORT).show();

        initItem();
        itemClick();
        initRecyclerView();
        getFirebaseData();
        receiveCourseName();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("main_course"));
    }

    private void initItem(){
        btnNext = findViewById(R.id.mainCourse_register_btnNext);
        btnPrevious = findViewById(R.id.mainCourse_register_btnPrevious);
        recyclerView = findViewById(R.id.mainCourse_recyclerView);

        progressDialog = new ProgressDialog(this);
        arrayList = new ArrayList<>();
        adapter = new MainCourseChooseRecyclerAdapter(arrayList, this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

    }

    private void initRecyclerView(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void getFirebaseData(){
        databaseReference.child("UniversityInfo").child(schooleName).child("courseList").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        firebaseDatabaseGetSet = ds.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void  receiveCourseName(){
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                getMainCourse = intent.getStringArrayListExtra("courseName");
                if (!getMainCourse.isEmpty()) {
                    courseName = getMainCourse.get(0);
                }
            }
        };
    }

    private void itemClick(){
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setTitle("Update");
                progressDialog.setMessage("We're updating your profile, please hold on.....");
                progressDialog.show();
                if (courseName != null && !courseName.isEmpty()) {
                    UpdateUserProfile();
                }else {
                    Toast.makeText(MainCourseRegister.this, "Must choose a course", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainCourseRegister.this, UniversityRegister.class);
                intent.putExtra("realName", realName);
                intent.putExtra("birthdayDate", birthdayDate);
                startActivity(intent);
            }
        });
    }

    private void UpdateUserProfile(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("realName", realName);
        hashMap.put("birthdayDate", birthdayDate);
        hashMap.put("schoolName", schooleName);
        hashMap.put("courseName", courseName);
        hashMap.put("CompleteInformationCheck", "success");
        databaseReference.child("Users_profile").child(firebaseAuth.getUid()).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Intent intent = new Intent(MainCourseRegister.this, MainActivity.class);
                    startActivity(intent);
                }else {
                    progressDialog.dismiss();
                }
            }
        });
    }
}
