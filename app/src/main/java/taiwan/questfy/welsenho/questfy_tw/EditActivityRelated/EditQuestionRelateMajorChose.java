package taiwan.questfy.welsenho.questfy_tw.EditActivityRelated;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import me.gujun.android.taggroup.TagGroup;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class EditQuestionRelateMajorChose extends AppCompatActivity{

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button btnDone;
    private Toolbar toolbar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<String> imageList;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<String> getMajors;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private FirebaseAuth firebaseAuth;
    private EditQuestionRelateRecyclerViewAdapter adapter;
    private EditInitMajorChooseConfirmRecyclerAdapter confirmRecyclerAdapter;
    private TagGroup tagGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_question_relate_major_chose);

        toolbar = findViewById(R.id.edit_choose_major_toolbar);
        recyclerView = findViewById(R.id.edit_choose_major_recyclerView);
        progressBar = findViewById(R.id.edit_choose_major_progressBar);
        btnDone = findViewById(R.id.edit_choose_major_btn_Done);
        tagGroup = findViewById(R.id.edit_choose_major_chipGroup);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        setSupportActionBar(toolbar);
        getMajors = new ArrayList<>();
        arrayList = new ArrayList<>();

        if (getIntent().getStringArrayListExtra("Majors") != null){
            getMajors = getIntent().getStringArrayListExtra("Majors");
            tagGroup.setTags(getMajors);
        }

        if (getIntent().getStringArrayListExtra("ImageList") != null){
            imageList = new ArrayList<>();
            imageList = getIntent().getStringArrayListExtra("ImageList");
        }

        adapter = new EditQuestionRelateRecyclerViewAdapter(arrayList, this, new EditQuestionRelateRecyclerViewAdapter.majorSelectListener() {
            @Override
            public void select(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                if (!getMajors.contains(arrayList.get(position).getMajor())) {
                    if (getMajors.size() < 5) {
                        getMajors.add(arrayList.get(position).getMajor());
                        tagGroup.setTags(getMajors);
                        confirmRecyclerAdapter = new EditInitMajorChooseConfirmRecyclerAdapter(getMajors);
                    }else {
                        Toast.makeText(EditQuestionRelateMajorChose.this, "只能選5種相關科系", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                getMajors.remove(tag);
                tagGroup.setTags(getMajors);
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        getData();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditQuestionRelateMajorChose.this, EditInitActivity.class);
                if (getMajors != null) {
                    intent.putStringArrayListExtra("getMajors", getMajors);
                }
                if (imageList != null) {
                    intent.putStringArrayListExtra("ImageList", imageList);
                }
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_activity_serarch_view, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditQuestionRelateMajorChose.this, EditInitActivity.class);
        if (getMajors != null) {
            intent.putStringArrayListExtra("getMajors", getMajors);
        }
        if (imageList != null) {
            intent.putStringArrayListExtra("ImageList", imageList);
        }
        startActivity(intent);
        finish();
        super.onBackPressed();
    }

    private void getData(){
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.child("Choose_majors_section").addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void filterList(String inputext){
        ArrayList<FirebaseDatabaseGetSet> filterList = new ArrayList<>();
        if (!filterList.isEmpty()){
            filterList.clear();
        }

        for (FirebaseDatabaseGetSet getSet:arrayList){
            if (getSet.getMajor().contains(inputext)){
                filterList.add(getSet);
                adapter = new EditQuestionRelateRecyclerViewAdapter(filterList, getApplicationContext(), new EditQuestionRelateRecyclerViewAdapter.majorSelectListener() {
                    @Override
                    public void select(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                        if (!getMajors.contains(arrayList.get(position).getMajor())) {
                            getMajors.add(arrayList.get(position).getMajor());
                            tagGroup.setTags(getMajors);
                            confirmRecyclerAdapter = new EditInitMajorChooseConfirmRecyclerAdapter(getMajors);
                        }
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        }
    }
}
