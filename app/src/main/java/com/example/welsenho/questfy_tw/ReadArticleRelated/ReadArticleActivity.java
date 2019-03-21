package com.example.welsenho.questfy_tw.ReadArticleRelated;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.AnswerReplyActivityRelated.ReadAnswersActivity;
import com.example.welsenho.questfy_tw.EditActivityRelated.EditInitRelateRecyclerViewAdapterImageViews;
import com.example.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.MainActivityFragment.MainOnClickListener;
import com.example.welsenho.questfy_tw.OtherUserProfileRelatedMethod.OtherUserProfileActivity;
import com.example.welsenho.questfy_tw.R;
import com.github.florent37.expansionpanel.ExpansionHeader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReadArticleActivity extends AppCompatActivity {

    private Boolean likePress;
    private String Article_ID;
    private String otherUserUid;
    private long likeCount;

    private CircleImageView circleImageView;
    private TextView txtUserName;
    private TextView txtUploadData;
    private TextView txtTitle;
    private TextView txtMajors;
    private TextView txtContent;
    private TextView txtCheckImages;
    private TextView txtLikeCount;
    private TextView txtReadAnswers;
    private Toolbar toolbar;
    private ShineButton shineButtonHeart;
    private ShineButton shineButtonLike;
    private ExpansionHeader expansionHeader;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditInitRelateRecyclerViewAdapterImageViews adapterImageViews;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private FirebaseDatabaseGetSet getUserProfile;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private EditRelatedMethod editRelatedMethod;
    private RelativeLayout relayReadAnsers;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabas;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);

        Article_ID = getIntent().getStringExtra("ArticleID");
        InitializeItem();
        ItemCLick();

        HideItem();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        InitializeFirebase();
        getContentData();
        getLikeCount();

        queryLikeSearch();
        queryKeepSearch();


        /**
         * Initialize Image RecyclerView
         */
        editRelatedMethod = new EditRelatedMethod();
        arrayList = new ArrayList<>();
        adapterImageViews = new EditInitRelateRecyclerViewAdapterImageViews(arrayList);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setHasFixedSize(true);
        //editRelatedMethod.LoadImageFromFirebase(databaseReference, Article_ID, arrayList, expansionHeader, recyclerView, adapterImageViews, txtCheckImages);

        LoadImageFromFirebase();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void InitializeItem() {
        circleImageView = findViewById(R.id.read_article_circle_image_user);
        txtUserName = findViewById(R.id.read_article_txt_userName);
        txtTitle = findViewById(R.id.read_article_txt_title);
        txtMajors = findViewById(R.id.read_article_txt_major);
        txtContent = findViewById(R.id.read_article_txt_content);
        txtUploadData = findViewById(R.id.read_article_txt_UploadDate);
        txtCheckImages = findViewById(R.id.read_article_txt_CheckimageView);
        txtLikeCount = findViewById(R.id.read_article_txtLikeCount);
        expansionHeader = findViewById(R.id.read_article_expan_header);
        toolbar = findViewById(R.id.read_article_toolbar);
        recyclerView = findViewById(R.id.read_article_recyclerView);
        relayReadAnsers = findViewById(R.id.read_article_relaytive_ClcikToSeeAnswer);
        progressBar = findViewById(R.id.read_article_progressBar);
        shineButtonHeart = findViewById(R.id.read_article_shineBtn_heart);
        shineButtonLike = findViewById(R.id.read_article_shineBtn_like);

        shineButtonHeart.init(this);
        shineButtonLike.init(this);
        likePress = false;
    }


    private void InitializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabas = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabas.getReference();
    }

    private void HideItem() {
        txtUserName.setVisibility(View.INVISIBLE);
        txtUploadData.setVisibility(View.INVISIBLE);
        txtMajors.setVisibility(View.INVISIBLE);
        txtTitle.setVisibility(View.INVISIBLE);
        txtContent.setVisibility(View.INVISIBLE);
        txtLikeCount.setVisibility(View.INVISIBLE);
        shineButtonHeart.setVisibility(View.GONE);
        shineButtonLike.setVisibility(View.GONE);
        circleImageView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void ShowItem() {
        txtUserName.setVisibility(View.VISIBLE);
        txtUploadData.setVisibility(View.VISIBLE);
        txtMajors.setVisibility(View.VISIBLE);
        txtTitle.setVisibility(View.VISIBLE);
        txtContent.setVisibility(View.VISIBLE);
        txtLikeCount.setVisibility(View.VISIBLE);
        shineButtonHeart.setVisibility(View.VISIBLE);
        shineButtonLike.setVisibility(View.VISIBLE);
        circleImageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
    }

    private void getContentData() {

        databaseReference.child("Users_Question_Articles").child(Article_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    getUserProfile = dataSnapshot.getValue(FirebaseDatabaseGetSet.class);
                    txtUserName.setText(getUserProfile.getUser_Name());
                    txtUploadData.setText(getUserProfile.getUpload_Date());
                    txtMajors.setText(getUserProfile.getMajors());
                    txtTitle.setText(getUserProfile.getTitle());
                    txtContent.setText(getUserProfile.getContent());
                    txtLikeCount.setText(String.valueOf(Math.abs(getUserProfile.getArticle_like_count())));
                    setOtherUserUid(getUserProfile.getUserUid());
                    Picasso.get().load(getUserProfile.getUser_Image()).into(circleImageView);
                    ShowItem();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void ItemCLick() {

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadArticleActivity.this, OtherUserProfileActivity.class);
                if (otherUserUid != null) {
                    intent.putExtra("otherUserUid", getOtherUserUid());
                    startActivity(intent);
                } else {
                    Toast.makeText(ReadArticleActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                }

            }
        });

        relayReadAnsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReadArticleActivity.this, ReadAnswersActivity.class);
                intent.putExtra("Article_ID", Article_ID);
                intent.putExtra("Article_Title", txtTitle.getText().toString());
                startActivity(intent);
            }
        });
    }


    public void LoadImageFromFirebase() {
        databaseReference.child("Users_Question_Articles").child(Article_ID).child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.exists()) {
                    arrayList.clear();
                    expansionHeader.setVisibility(View.VISIBLE);
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        FirebaseDatabaseGetSet firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                        recyclerView.setAdapter(adapterImageViews);
                    }
                    adapterImageViews.setOnMainAdapterClickListner(new MainOnClickListener() {
                        @Override
                        public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                            Intent intent = new Intent(ReadArticleActivity.this, EnlargeImageActivity.class);
                            intent.putExtra("Article_ID", Article_ID);
                            intent.putExtra("Position", position);
                            startActivity(intent);
                        }
                    });
                } else {
                    expansionHeader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public String getOtherUserUid() {
        return otherUserUid;
    }

    public void setOtherUserUid(String otherUserUid) {
        this.otherUserUid = otherUserUid;
    }

    private void AddLike() {
        HashMap<String, Object> userInfo = new HashMap<>();
        userInfo.put("userUid", firebaseUser.getUid());
        databaseReference.child("Users_Question_Articles").child(Article_ID).child("User_like_count").child(firebaseUser.getUid()).updateChildren(userInfo);
    }

    private void removeLike() {
        databaseReference.child("Users_Question_Articles").child(Article_ID).child("User_like_count").child(firebaseUser.getUid()).removeValue();
    }

    private void getLikeCount() {
        databaseReference.child("Users_Question_Articles").child(Article_ID).child("User_like_count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    likeCount = dataSnapshot.getChildrenCount();
                }else {
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("Article_like_count", 0);
                    databaseReference.child("Users_Question_Articles").child(Article_ID).updateChildren(hashMap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void queryLikeSearch() {
        Query query = databaseReference.child("Users_Question_Articles").child(Article_ID).child("User_like_count").orderByChild("userUid").equalTo(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    shineButtonLike.setChecked(true);
                    shineButtonLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeLike();
                        }
                    });
                } else {
                    shineButtonLike.setChecked(false);
                    shineButtonLike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AddLike();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addToKeep() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Article_ID", Article_ID);
        hashMap.put("Title", txtTitle.getText());
        hashMap.put("Majors", txtMajors.getText().toString());
        hashMap.put("User_Name", txtUserName.getText().toString());
        hashMap.put("userUid", otherUserUid);
        hashMap.put("Upload_Date", txtUploadData.getText().toString());
        hashMap.put("Content", txtContent.getText().toString());
        hashMap.put("User_Image", getUserProfile.getUser_Image());

        databaseReference.child("Users_Keep_Articles").child(firebaseUser.getUid()).child(Article_ID).updateChildren(hashMap);
    }

    private void removeKeep(){
        databaseReference.child("Users_Keep_Articles").child(firebaseUser.getUid()).child(Article_ID).removeValue();
    }

    private void queryKeepSearch(){
        Query query = databaseReference.child("Users_Keep_Articles").child(firebaseUser.getUid()).child(Article_ID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    shineButtonHeart.setChecked(true);
                    shineButtonHeart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeKeep();
                        }
                    });
                }else {
                    shineButtonHeart.setChecked(false);
                    shineButtonHeart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addToKeep();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
