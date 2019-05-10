package taiwan.questfy.welsenho.questfy_tw.AnswerReplyActivityRelated;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class ReadAnswersActivity extends AppCompatActivity {

    private TextView txtAnswerTitle;
    private RecyclerView recyclerView;
    private RelativeLayout relayReplyAnswer;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    private readAnswersRecyclerViewAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String Article_ID;
    private String Article_Title;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_answers);

        Article_ID = getIntent().getStringExtra("Article_ID");
        Article_Title = getIntent().getStringExtra("Article_Title");
        InitFirebase();
        IninItem();
        ItemClick();
        LoadDateFromFirebase();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    private void ItemClick() {
        relayReplyAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    Intent intent = new Intent(ReadAnswersActivity.this, AnswerReplyActivity.class);
                    intent.putExtra("Article_ID", Article_ID);
                    intent.putExtra("Article_Title", Article_Title);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    private void IninItem() {
        txtAnswerTitle = findViewById(R.id.read_answers_txtTitle);
        relayReplyAnswer = findViewById(R.id.read_answers_relay_2);
        recyclerView = findViewById(R.id.read_answers_recyclerView);
        progressBar = findViewById(R.id.read_answers_progressBar);
        toolbar = findViewById(R.id.read_answers_toolBar);
        txtAnswerTitle.setText(Article_Title);
        arrayList = new ArrayList<>();
        adapter = new readAnswersRecyclerViewAdapter(arrayList, getApplicationContext(), new readAnswersRecyclerViewAdapter.AnswerImage() {
            @Override
            public void getImage(String imageUri) {
                Intent intent = new Intent(ReadAnswersActivity.this, EnlargeAnswerImageActivity.class);
                intent.putExtra("imageUri", imageUri);
                startActivity(intent);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("答案區");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void InitFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void LoadDateFromFirebase() {
        databaseReference.child("ArticleAnswers").child(Article_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrayList.clear();
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}