package taiwan.questfy.welsenho.questfy_tw.DailyQuestionsRelated;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.HashMap;

import taiwan.questfy.welsenho.questfy_tw.AnswerReplyActivityRelated.ReadAnswersActivity;
import taiwan.questfy.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.LoginRelated.LoginActivity;
import taiwan.questfy.welsenho.questfy_tw.R;

public class DailyQuestionArticleRead extends AppCompatActivity {

    private String ArticleUid;

    private TextView txtSubject;
    private TextView txtAuthor;
    private TextView txtContent;
    private TextView txtTitle;
    private TextView txtComment;
    private EditText editComment;
    private ImageView imgArticlePicture;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerView;

    private EditRelatedMethod editRelatedMethod;
    private DailyQuestionReadArticleRecyclerAdapter adapter;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet getSet;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_question_article_read);

        InitItem();
        InitFirebase();
        getArticleData();
        onItemClick();
        InitRecyclerView();
        getFirebaseCommentDate();
    }


    private void InitFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void InitItem(){
        ArticleUid = getIntent().getStringExtra("ArticleUid");
        appBarLayout = findViewById(R.id.daily_question_readArticle_appBarLayout);
        txtTitle = findViewById(R.id.daily_question_readArticle_txtTitle);
        txtSubject = findViewById(R.id.daily_question_readArticle_txtSubject);
        txtAuthor = findViewById(R.id.daily_question_readArticle_txtAuthor);
        txtContent = findViewById(R.id.daily_question_readArticle_txtContent);
        txtComment = findViewById(R.id.daily_question_readArticle_txtSendComment);
        editComment = findViewById(R.id.daily_question_readArticle_editComments);
        imgArticlePicture = findViewById(R.id.daily_question_readArticle_imgPicture);
        toolbar = findViewById(R.id.daily_question_readArticle_toolBar);
        collapsingToolbarLayout = findViewById(R.id.daily_question_readArticle_collaspingToolBar);
        recyclerView = findViewById(R.id.daily_question_readArticle_recyclerView);

        editRelatedMethod = new EditRelatedMethod();


        txtTitle.bringToFront();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("  ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arrayList = new ArrayList<>();
        adapter = new DailyQuestionReadArticleRecyclerAdapter(arrayList);
    }

    private void getArticleData(){
        databaseReference.child("DailyQuestion").child(ArticleUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    final FirebaseDatabaseGetSet getSet = dataSnapshot.getValue(FirebaseDatabaseGetSet.class);
                    Picasso.get().load(getSet.getDailyQuestionImage()).fit().into(imgArticlePicture);
                    txtSubject.setText(getSet.getDailyQuestionSubject());
                    txtAuthor.setText(getSet.getDailyQuestionAuthor());
                    txtContent.setText(getSet.getDailyQuestionContent());
                    collapsingToolbarLayout.setTitle(getSet.getDailyQuestionTitle());
                    collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(getApplicationContext(), R.color.FullWhite));


                    appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

                        boolean isShow = true;
                        int scrollRange = -1;

                        @Override
                        public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                            if (scrollRange == -1) {
                                scrollRange = appBarLayout.getTotalScrollRange();
                            }
                            if (scrollRange + i == 0) {
                                collapsingToolbarLayout.setTitle(getSet.getDailyQuestionTitle());
                                isShow = true;
                            } else if(isShow) {
                                txtTitle.setText(getSet.getDailyQuestionTitle());
                                collapsingToolbarLayout.setTitle("  ");
                                isShow = false;
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void onItemClick(){
        txtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseUser != null) {
                    SendComment();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DailyQuestionArticleRead.this);
                    builder.setMessage("登入即可回覆這篇文章").setPositiveButton("登入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(DailyQuestionArticleRead.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton("稍後登入", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
                }
            }
        });
    }

    private void SendComment(){
        String comment = editComment.getText().toString();
        String Date = editRelatedMethod.getUploadDate();
        String commentUid = databaseReference.child("DailyQuestion").push().getKey();
        if (!comment.isEmpty()) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("dailyQuestionComment", comment);
            hashMap.put("userUid", firebaseUser.getUid());
            hashMap.put("User_Image", firebaseUser.getPhotoUrl().toString());
            hashMap.put("User_Name", firebaseUser.getDisplayName());
            hashMap.put("Upload_Date", Date);
            databaseReference.child("DailyQuestion").child(ArticleUid).child("CommentSection").child(commentUid).updateChildren(hashMap);
            editComment.setText("");
        }else {
            Toast.makeText(DailyQuestionArticleRead.this, "Comment can not be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void getFirebaseCommentDate(){
        databaseReference.child("DailyQuestion").child(ArticleUid).child("CommentSection").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    arrayList.clear();
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(getSet);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitRecyclerView(){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
    }
}
