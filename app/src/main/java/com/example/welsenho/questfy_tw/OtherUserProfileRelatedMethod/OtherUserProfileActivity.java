package com.example.welsenho.questfy_tw.OtherUserProfileRelatedMethod;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private CircleImageView circleImageView;
    private Button btnFollow;
    private Button btnAddFriend;
    private Button btnSendMessage;
    private RecyclerView recyclerView;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;

    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private OtherUserProfileRelatedMethods otherUserProfileRelatedMethods;

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
        getUserArticlesData();
        setRecyclerView();
        queryFollow();
        getFollowers();
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
        btnSendMessage = findViewById(R.id.otherUser_profile_btnSendMessage);
        recyclerView = findViewById(R.id.otherUser_profile_recyclerView);
        coordinatorLayout = findViewById(R.id.otherUser_profile_coordinatorLayout);
        otherUserUid = getIntent().getStringExtra("otherUserUid");
        otherUserProfileRelatedMethods = new OtherUserProfileRelatedMethods();

        arrayList = new ArrayList<>();
        adapter = new list_article_recyclerView_adapter(arrayList, this);
        recyclerView.setFocusable(false);
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
                        btnAddFriend.setText("Waiting for friend accept (Click to cancel)");
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
                        btnAddFriend.setText("Accept friend request");
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


                    btnAddFriend.setText("Friends");
                    btnAddFriend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.FullWhite));
                    btnAddFriend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.add_friend_btn_background));


                } else {
                    btnAddFriend.setText("Add Friend");
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
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userUid", firebaseUser.getUid());
        hashMap.put("User_Name", firebaseUser.getDisplayName());

        databaseReference.child("Users_Followers_Section").child(otherUserUid).child(firebaseUser.getUid()).updateChildren(hashMap);
    }

    private void queryFollow(){
        Query query = databaseReference.child("Users_Followers_Section").child(otherUserUid).child(firebaseUser.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    btnFollow.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.LightMainOrange));
                    btnFollow.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.FullWhite));
                    btnFollow.setText(getString(R.string.following));
                    btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            databaseReference.child("Users_Followers_Section").child(otherUserUid).child(firebaseUser.getUid()).removeValue();
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

    private void getFollowers(){
        databaseReference.child("Users_Followers_Section").child(otherUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    txtFollowesCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }else {
                    txtFollowesCount.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
