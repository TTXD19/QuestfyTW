package taiwan.questfy.welsenho.questfy_tw.PersonAskQuestionRelated;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.MainUserActivity.MainActivity;
import taiwan.questfy.welsenho.questfy_tw.R;

public class PersonalAskQuestReplyActivity extends AppCompatActivity {

    private String questionUid;
    private String questioType;
    private String AskerUid;
    private boolean notificationOpen = false;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private PersonalAskQuestReplyRecyclerAdapter adapter;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;

    private Toolbar toolbar;
    private TextView txtUser;
    private TextView txtContent;
    private TextView txtUploadData;
    private TextView txtSolveIt;
    private TextView txtMessagesCount;
    private ImageView imgContent;
    private CircleImageView circleImageView;
    private Button btnClickReply;
    private RecyclerView recyclerView;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_ask_quest_reply);

        questionUid = getIntent().getStringExtra("questionUid");
        questioType = getIntent().getStringExtra("questioType");
        AskerUid = getIntent().getStringExtra("AskerUid");
        notificationOpen = getIntent().getBooleanExtra("notificationOpen", false);

        InitFirebase();
        InitItem();
        ItemClick();
        InitQuestion();
        getAnswerData();
        getMessahgeCount();
    }


    private void InitItem(){
        toolbar = findViewById(R.id.person_ask_quest_reply_toolBar);
        txtUser = findViewById(R.id.person_ask_quest_reply_txtUserFrom);
        txtContent = findViewById(R.id.person_ask_quest_reply_txtQuestionContent);
        txtSolveIt = findViewById(R.id.person_ask_quest_reply_txtUserSolveIt);
        txtUploadData = findViewById(R.id.person_ask_quest_reply_txtUserAskDate);
        txtMessagesCount = findViewById(R.id.person_ask_quest_reply_txtMessagesCount);
        imgContent = findViewById(R.id.person_ask_quest_reply_imgQuestionImage);
        btnClickReply = findViewById(R.id.person_ask_quest_reply_btnReply);
        recyclerView = findViewById(R.id.person_ask_quest_reply_recyclerView);
        circleImageView = findViewById(R.id.person_ask_quest_reply_imgUserImage);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        arrayList = new ArrayList<>();
        adapter = new PersonalAskQuestReplyRecyclerAdapter(arrayList, getApplicationContext());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        if (notificationOpen){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }else {
            finish();
        }
        super.onBackPressed();
    }

    private void ItemClick(){
        btnClickReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalAskQuestReplyActivity.this, PersonalAskReplyingActivity.class);
                intent.putExtra("questionUid", questionUid);
                intent.putExtra("questioType", questioType);
                intent.putExtra("AskerUid", AskerUid);
                startActivity(intent);
                finish();
            }
        });
    }

    private void InitFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void InitQuestion(){
        if (questioType.equals("AskBy")) {
            databaseReference.child("Personal_Ask_Question").child(firebaseUser.getUid()).child("AskedBy").child(questionUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        firebaseDatabaseGetSet = dataSnapshot.getValue(FirebaseDatabaseGetSet.class);
                        String title = "來自" + firebaseDatabaseGetSet.getAskerName() + "的提問";
                        txtUser.setText(title);
                        txtContent.setText(firebaseDatabaseGetSet.getAskQuestionContent());
                        txtUploadData.setText(firebaseDatabaseGetSet.getAskDate());
                        Picasso.get().load(firebaseDatabaseGetSet.getQuestionTumbnail()).fit().into(imgContent);
                        loadImage(firebaseDatabaseGetSet.getAskerUid());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {
            databaseReference.child("Personal_Ask_Question").child(firebaseUser.getUid()).child("AskTo").child(questionUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        firebaseDatabaseGetSet = dataSnapshot.getValue(FirebaseDatabaseGetSet.class);
                        String title = "對" + firebaseDatabaseGetSet.getAnswerName() + "的提問";
                        txtUser.setText(title);
                        txtContent.setText(firebaseDatabaseGetSet.getAskQuestionContent());
                        txtUploadData.setText(firebaseDatabaseGetSet.getAskDate());
                        Picasso.get().load(firebaseDatabaseGetSet.getQuestionTumbnail()).fit().into(imgContent);
                        loadImage(firebaseDatabaseGetSet.getAnswerUid());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    private void loadImage(String userUid){
        databaseReference.child("Users_profile").child(userUid).child("User_image_uri").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Picasso.get().load(dataSnapshot.getValue().toString()).fit().into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMessahgeCount(){
        databaseReference.child("Personal_Ask_Question_Reply").child(questionUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.getChildren().equals(0)){
                    String messageCount = String.valueOf(dataSnapshot.getChildrenCount()) + "則答案回覆";
                    txtMessagesCount.setText(messageCount);
                    databaseReference.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getAnswerData(){
        databaseReference.child("Personal_Ask_Question_Reply").child(questionUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    arrayList.clear();
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                        recyclerView.setAdapter(adapter);
                        databaseReference.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
