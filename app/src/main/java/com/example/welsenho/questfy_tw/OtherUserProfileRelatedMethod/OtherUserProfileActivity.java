package com.example.welsenho.questfy_tw.OtherUserProfileRelatedMethod;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class OtherUserProfileActivity extends AppCompatActivity {

    private String otherUserUid;
    private String SelfUid;

    private TextView txtUserName;
    private TextView txtUniversityName;
    private TextView txtFollowesCount;
    private TextView txtAnsweredCount;
    private TextView txtPostsCount;
    private Button btnFollow;
    private Button btnAddFriend;
    private Button btnSendMessage;

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
        ItemClick();
    }




    private void InitItem(){
        txtUserName = findViewById(R.id.otherUser_profile_txtUserName);
        txtUniversityName = findViewById(R.id.otherUser_profile_txtUniName);
        txtFollowesCount = findViewById(R.id.otherUser_profile_txtFollowersCount);
        txtAnsweredCount = findViewById(R.id.otherUser_profile_txtAnsweredCount);
        txtPostsCount = findViewById(R.id.otherUser_profile_txtPostsCount);
        btnFollow = findViewById(R.id.otherUser_profile_btnFollow);
        btnAddFriend = findViewById(R.id.otherUser_profile_btnAddFriend);
        btnSendMessage = findViewById(R.id.otherUser_profile_btnSendMessage);
        otherUserUid = getIntent().getStringExtra("otherUserUid");
        otherUserProfileRelatedMethods = new OtherUserProfileRelatedMethods();
    }

    private void InitFirebaseItem(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void ItemClick(){

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendAddingProcess();
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


        databaseReference.child("FriendAddingProcess").child(otherUserUid).child(ProcessKey).updateChildren(hashMap);
        databaseReference.child("FriendAddingProcess").child(SelfUid).child(ProcessKey).updateChildren(hashMap);


    }
}
