package com.example.welsenho.questfy_tw.OtherUserProfileRelatedMethod;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.MainActivityFragment.MainOnClickListener;
import com.example.welsenho.questfy_tw.MainActivityFragment.list_article_recyclerView_adapter;
import com.example.welsenho.questfy_tw.R;
import com.example.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserProfileActivity extends AppCompatActivity {

    private String otherUserUid;
    private String SelfUid;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet getSet;
    private list_article_recyclerView_adapter adapter;

    private TextView txtUserName;
    private TextView txtUniversityName;
    private TextView txtFollowesCount;
    private TextView txtAnsweredCount;
    private TextView txtPostsCount;
    private TextView txtCorseName;
    private TextView txtSpecility;
    private TextView txtStatusMessage;
    private TextView txtPopTitle;
    private TextView txtPopAsk;
    private TextView txtPopCancel;
    private TextView txtPopCount;
    private EditText editPopAskContent;
    private CircleImageView circleImageView;
    private Button btnFollow;
    private Button btnAddFriend;
    private Button btnSendMessageQuestion;
    private RecyclerView recyclerView;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private Dialog dialog;

    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private OtherUserProfileRelatedMethods otherUserProfileRelatedMethods;
    private EditRelatedMethod editRelatedMethod;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        InitItem();
        InitFirebaseItem();
        getOtherUserInfo();
        detectFriend();
        detectFollowing();
        getFollowersCount();
        getUserArticlesData();
        setRecyclerView();
        onItemClick();
    }




    private void InitItem(){
        txtUserName = findViewById(R.id.otherUser_profile_txtUserName);
        txtUniversityName = findViewById(R.id.otherUser_profile_txtUniName);
        txtFollowesCount = findViewById(R.id.otherUser_profile_txtFollowersCount);
        txtAnsweredCount = findViewById(R.id.otherUser_profile_txtAnsweredCount);
        txtPostsCount = findViewById(R.id.otherUser_profile_txtPostsCount);
        txtCorseName = findViewById(R.id.otherUser_profile_txtMainCourse);
        txtSpecility = findViewById(R.id.otherUser_profile_txtSpeciality);
        txtStatusMessage = findViewById(R.id.otherUser_profile_txtStatuseMessage);
        circleImageView = findViewById(R.id.otherUser_profile_circleImgUser);
        btnFollow = findViewById(R.id.otherUser_profile_btnFollow);
        btnAddFriend = findViewById(R.id.otherUser_profile_btnAddFriend);
        btnSendMessageQuestion = findViewById(R.id.otherUser_profile_btnSendMessageQuestion);
        recyclerView = findViewById(R.id.otherUser_profile_recyclerView);
        coordinatorLayout = findViewById(R.id.otherUser_profile_coordinatorLayout);
        otherUserUid = getIntent().getStringExtra("otherUserUid");
        otherUserProfileRelatedMethods = new OtherUserProfileRelatedMethods();
        editRelatedMethod = new EditRelatedMethod();

        arrayList = new ArrayList<>();
        adapter = new list_article_recyclerView_adapter(arrayList, this);
        recyclerView.setFocusable(false);

        dialog = new Dialog(this);
    }

    private void InitFirebaseItem(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference();
        SelfUid = firebaseAuth.getUid();
    }

    private void getOtherUserInfo(){

        databaseReference.child("Users_profile").child(otherUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    firebaseDatabaseGetSet = dataSnapshot.getValue(FirebaseDatabaseGetSet.class);
                    txtUserName.setText(firebaseDatabaseGetSet.getID());
                    if (firebaseDatabaseGetSet.getSchoolName() != null){
                        txtUniversityName.setText(firebaseDatabaseGetSet.getSchoolName());
                        txtCorseName.setText(firebaseDatabaseGetSet.getCourseName());
                    }else {
                        txtUniversityName.setText("Not register");
                        txtCorseName.setText("Not register");
                    }
                    if (firebaseDatabaseGetSet.getUserSpeciality() != null) {
                        txtSpecility.setText(firebaseDatabaseGetSet.getUserSpeciality());
                    } else {
                        txtSpecility.setText("Not set yet");
                    }
                    if (firebaseDatabaseGetSet.getUserStatusMessage() != null) {
                        txtStatusMessage.setText(firebaseDatabaseGetSet.getUserStatusMessage());
                    }else {
                        txtStatusMessage.setText("Not set yet");
                    }
                    Picasso.get().load(firebaseDatabaseGetSet.getUser_image_uri()).fit().into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FriendAddingProcess(){

        String ProcessKey =  databaseReference.child("FriendAddingProcess").push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("RecieverUid", otherUserUid);
        hashMap.put("SenderUid", SelfUid);
        hashMap.put("Status", "UnFriend");
        hashMap.put("RequestDate", otherUserProfileRelatedMethods.getRequestDate());
        hashMap.put("RequestName", firebaseUser.getDisplayName());


        databaseReference.child("FriendAddingProcess").child(otherUserUid).child(SelfUid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    createSnackBar();
                }
            }
        });
        databaseReference.child("FriendAddingProcess").child(SelfUid).child(otherUserUid).updateChildren(hashMap);
    }

    private void acceptFriendRequest(String friendUid, String friendName, String selfUid, String selfName){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("FriendUid", friendUid);
        hashMap.put("FriendName", friendName);
        hashMap.put("FriendImage", "null");

        HashMap<String, Object> hashMapFriend = new HashMap<>();
        hashMapFriend.put("FriendUid", selfUid);
        hashMapFriend.put("FriendName", selfName);
        hashMap.put("FriendImage", "null");
        databaseReference.child("UserFriendList").child(selfUid).child(friendUid).updateChildren(hashMap);
        databaseReference.child("UserFriendList").child(friendUid).child(selfUid).updateChildren(hashMapFriend);
        databaseReference.child("FriendAddingProcess").child(selfUid).child(friendUid).removeValue();
        databaseReference.child("FriendAddingProcess").child(friendUid).child(selfUid).removeValue();
    }

    private void createSnackBar(){
        snackbar = Snackbar.make(coordinatorLayout, "Add friend successfully", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("FriendAddingProcess").child(otherUserUid).child(SelfUid).removeValue();
                databaseReference.child("FriendAddingProcess").child(SelfUid).child(otherUserUid).removeValue();
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void detectFriend(){
        databaseReference.child("FriendAddingProcess").child(SelfUid).child(otherUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    FirebaseDatabaseGetSet getFriend = dataSnapshot.getValue(FirebaseDatabaseGetSet.class);
                    String detectSender = getFriend.getSenderUid();
                    final String friendName = getFriend.getRequestName();
                    if (detectSender.equals(firebaseUser.getUid())) {
                        btnAddFriend.setText(getString(R.string.waiting_friend_accept));
                        btnAddFriend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.FullWhite));
                        btnAddFriend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sign_in_rectangle));

                        btnAddFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                databaseReference.child("FriendAddingProcess").child(SelfUid).child(otherUserUid).removeValue();
                                databaseReference.child("FriendAddingProcess").child(otherUserUid).child(SelfUid).removeValue();
                            }
                        });
                    } else {
                        btnAddFriend.setText(getString(R.string.accept_friend_request));
                        btnAddFriend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.FullWhite));
                        btnAddFriend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.sign_in_rectangle));

                        btnAddFriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                acceptFriendRequest(otherUserUid, friendName, firebaseUser.getUid(), firebaseUser.getDisplayName());
                            }
                        });
                    }
                } else {
                    detectAddedFriend();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void detectAddedFriend(){
        databaseReference.child("UserFriendList").child(SelfUid).child(otherUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){


                    btnAddFriend.setText(getString(R.string.friend_added));
                    btnAddFriend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.FullWhite));
                    btnAddFriend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.add_friend_btn_background));


                } else {
                    btnAddFriend.setText(getString(R.string.add_friend));
                    btnAddFriend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.FullWhite));
                    btnAddFriend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.add_friend_btn_background));

                    btnAddFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            FriendAddingProcess();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getUserArticlesData(){
        databaseReference.child("Users_Question_Articles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    arrayList.clear();
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        if (getSet.getUserUid().equals(otherUserUid)){
                            arrayList.add(getSet);
                            txtPostsCount.setText(String.valueOf(arrayList.size()));
                            recyclerView.setAdapter(adapter);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));
        adapter.setOnMainClickListener(new MainOnClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Intent intent = new Intent(OtherUserProfileActivity.this, ReadArticleActivity.class);
                intent.putExtra("ArticleID", arrayList.get(position).getArticle_ID());
                startActivity(intent);
            }
        });
    }

    private void followUser(){
        HashMap<String, Object> followBy = new HashMap<>();
        followBy.put("userUid", firebaseUser.getUid());
        followBy.put("User_Name", firebaseUser.getDisplayName());

        HashMap<String, Object> following = new HashMap<>();
        following.put("userUid", otherUserUid);
        following.put("User_Name", txtUserName.getText().toString());

        databaseReference.child("Users_Followers_Section").child(firebaseUser.getUid()).child("FollowingInfo").child(otherUserUid).updateChildren(following);
        databaseReference.child("Users_Followers_Section").child(otherUserUid).child("Follow_by").child(firebaseUser.getUid()).updateChildren(followBy);
    }

    private void onItemClick(){
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUser();
            }
        });

        btnSendMessageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askMeAnyQustion();
            }
        });
    }

    private void detectFollowing(){
        databaseReference.child("Users_Followers_Section").child(otherUserUid).child("Follow_by").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    btnFollow.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.FullWhite));
                    btnFollow.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.LightGreen));
                    btnFollow.setText(getString(R.string.following));
                    btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            databaseReference.child("Users_Followers_Section").child(otherUserUid).child("Follow_by").child(firebaseUser.getUid()).removeValue();
                        }
                    });
                }else {
                    btnFollow.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.FullWhite));
                    btnFollow.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.LightMainOrange));
                    btnFollow.setText(R.string.follow);
                    btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            followUser();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowersCount(){
        databaseReference.child("Users_Followers_Section").child(otherUserUid).child("FollowBy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    txtFollowesCount.setText(String.valueOf(dataSnapshot.getValue()));
                }else {
                    txtFollowesCount.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void askMeAnyQustion(){
        dialog.setContentView(R.layout.user_profile_custom_message_editing);
        txtPopTitle = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtTitle);
        txtPopCount = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtCount);
        txtPopAsk = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtSave);
        txtPopCancel = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtCancel);
        editPopAskContent = dialog.findViewById(R.id.pop_up_userProfile_customMessage_editMessage);

        String title  = getString(R.string.ask) + txtUserName.getText().toString() + getString(R.string.anything);
        txtPopTitle.setText(title);
        txtPopAsk.setText(getString(R.string.ask_it));
        if (Locale.getDefault().getDisplayLanguage().equals("en")) {
            editPopAskContent.setHint("What is " + txtCorseName.getText().toString() + " learning ?");
        } else {
            editPopAskContent.setHint("例如 : " + txtCorseName.getText().toString() + " 是在學什麼內容呢 ？");
        }
        dialog.show();

        txtPopAsk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editPopAskContent.getText().toString().isEmpty()){
                    uploadPersonalAsk(editPopAskContent.getText().toString());
                    dialog.dismiss();
                }else {
                    Toast.makeText(OtherUserProfileActivity.this, "Question can not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtPopCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void uploadPersonalAsk(String question){

        String randomAskUid = databaseReference.child("Personal_Ask_Question").push().getKey();
        String AskDate = editRelatedMethod.getUploadDate();

        HashMap<String, Object> askBy = new HashMap<>();
        askBy.put("personalQuestion", randomAskUid);
        askBy.put("AskerUid", firebaseUser.getUid());
        askBy.put("AskerName", firebaseUser.getDisplayName());
        askBy.put("AskDate", AskDate);
        databaseReference.child("Personal_Ask_Question").child(otherUserUid).child("AskedBy").child(firebaseUser.getUid()).updateChildren(askBy);

        HashMap<String, Object> askTo = new HashMap<>();
        askTo.put("personalQuestion", randomAskUid);
        askTo.put("AnswererUid", otherUserUid);
        askTo.put("AnswererName", txtUserName.getText().toString());
        askTo.put("AskDate", AskDate);
        databaseReference.child("Personal_Ask_Question").child(firebaseUser.getUid()).child("AskTo").child(otherUserUid).updateChildren(askTo);
    }



}
