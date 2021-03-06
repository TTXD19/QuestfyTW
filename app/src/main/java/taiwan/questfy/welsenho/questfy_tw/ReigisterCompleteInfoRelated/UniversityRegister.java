package taiwan.questfy.welsenho.questfy_tw.ReigisterCompleteInfoRelated;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class UniversityRegister extends AppCompatActivity {

    private String realName;
    private String currentDegree;
    private String schoolName;


    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<FirebaseDatabaseGetSet> searchArrayList;
    private ArrayList<String> getSchoolName;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private UniversityAndMajorsChooseRecyclerAdapter adapter;
    public BroadcastReceiver broadcastReceiver;

    private Button btnPrevious;
    private RecyclerView recyclerView;
    private TextView txtChooseInDegree;
    private EditText editSearchUniversity;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_university_register);


        realName = getIntent().getStringExtra("realName");
        currentDegree = getIntent().getStringExtra("currentDegree");


        InitItem();
        ItemClick();
        InitRecyclerView();
        getFirebaseData();
        searchUniversity();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UniversityRegister.this, CurrentDegreeChoose.class);
        intent.putExtra("realName", realName);
        startActivity(intent);
    }

    private void InitItem() {
        btnPrevious = findViewById(R.id.university_register_btnPrevious);
        recyclerView = findViewById(R.id.university_register_recyclerView);
        editSearchUniversity = findViewById(R.id.universityName_register_editSearchUniversity);
        txtChooseInDegree = findViewById(R.id.university_register_txtProcess);


        if (currentDegree.equals("HighSchoolInfo")) {
            txtChooseInDegree.setText("請選擇您的高中");
        }


        searchArrayList = new ArrayList<>();
        arrayList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        adapter = new UniversityAndMajorsChooseRecyclerAdapter(arrayList, this, new UniversityAndMajorsChooseRecyclerAdapter.itemOnClick() {
            @Override
            public void getClickItem(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {

                Intent intent = new Intent(UniversityRegister.this, MainCourseRegister.class);
                intent.putExtra("realName", realName);
                intent.putExtra("currentDegree", currentDegree);
                intent.putExtra("schoolName", arrayList.get(position).getSchoolName());
                startActivity(intent);
                finish();

            }
        });
    }

    private void ItemClick() {
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void getFirebaseData() {
        databaseReference.child(currentDegree).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                    }
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }

    private void searchUniversity() {
        editSearchUniversity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchArrayList.clear();
                final String searchQuery = s.toString();
                databaseReference.child(currentDegree).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot DS : dataSnapshot.getChildren()) {
                                FirebaseDatabaseGetSet getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                                if (getSet.getSchoolName().contains(searchQuery)) {
                                    searchArrayList.add(getSet);
                                }
                            }

                            adapter = new UniversityAndMajorsChooseRecyclerAdapter(searchArrayList, getApplicationContext(), new UniversityAndMajorsChooseRecyclerAdapter.itemOnClick() {
                                @Override
                                public void getClickItem(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                                    Intent intent = new Intent(UniversityRegister.this, MainCourseRegister.class);
                                    intent.putExtra("realName", realName);
                                    intent.putExtra("currentDegree", currentDegree);
                                    intent.putExtra("schoolName", arrayList.get(position).getSchoolName());
                                    startActivity(intent);
                                    finish();
                                }
                            });
                            recyclerView.setAdapter(adapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
