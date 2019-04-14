package com.example.welsenho.questfy_tw.OtherUserProfileRelatedMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.MainActivityFragment.MainOnClickListener;
import com.example.welsenho.questfy_tw.MainActivityFragment.list_article_recyclerView_adapter;
import com.example.welsenho.questfy_tw.MainUserActivity.MainActivity;
import com.example.welsenho.questfy_tw.PersonAskQuestionRelated.PersonalAskReplyingActivity;
import com.example.welsenho.questfy_tw.R;
import com.example.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class OtherUserProfileActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_SELECT = 0;

    private String otherUserUid;
    private String SelfUid;
    private String AskQuesitonUid;
    private String uploadTimeStamp;
    private String imageUri;
    private String seldUserSchoolName;
    private String reportReason;
    private String[] reportReasons = new String[9];
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
    private ImageView imgPopAddImage;
    private ImageView imgPopPreview;
    private EditText editPopAskContent;
    private CircleImageView circleImageView;
    private Button btnFollow;
    private Button btnAddFriend;
    private Button btnSendMessageQuestion;
    private RecyclerView recyclerView;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private Dialog dialog;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private OtherUserProfileRelatedMethods otherUserProfileRelatedMethods;
    private EditRelatedMethod editRelatedMethod;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        InitItem();
        InitFirebaseItem();
        getOtherUserInfo();
        getSelfUserInfo();
        if (firebaseUser != null) {
            detectFriend();
            detectFollowing();
            onItemClick();
        }else {
            guestClick();
        }
        getFollowersCount();
        getUserArticlesData();
        setRecyclerView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_SELECT){
            if (resultCode == RESULT_OK){
                progressBar.setVisibility(View.VISIBLE);
                Uri uri = data.getData();
                UploadPhotoToFirebase(uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.read_article_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.read_article_report:
                if (firebaseUser != null){
                    createReportDialog();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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
        toolbar = findViewById(R.id.otherUser_profile_toolBar);
        otherUserUid = getIntent().getStringExtra("otherUserUid");
        otherUserProfileRelatedMethods = new OtherUserProfileRelatedMethods();
        editRelatedMethod = new EditRelatedMethod();

        arrayList = new ArrayList<>();
        adapter = new list_article_recyclerView_adapter(arrayList, this);
        recyclerView.setFocusable(false);

        dialog = new Dialog(this);

        setSupportActionBar(toolbar);
    }

    private void InitFirebaseItem(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference();
        SelfUid = firebaseAuth.getUid();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void getOtherUserInfo(){

        databaseReference.child("Users_profile").child(otherUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    firebaseDatabaseGetSet = dataSnapshot.getValue(FirebaseDatabaseGetSet.class);
                    txtUserName.setText(firebaseDatabaseGetSet.getID());
                    getSupportActionBar().setTitle(firebaseDatabaseGetSet.getID() + "'s profile");
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

    private void getSelfUserInfo() {
        databaseReference.child("Users_profile").child(firebaseUser.getUid()).child("schoolName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    seldUserSchoolName = dataSnapshot.getValue().toString();
                }else {
                    seldUserSchoolName = "為註冊";
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
        followBy.put("schoolName", seldUserSchoolName);
        followBy.put("User_image_uri", firebaseUser.getPhotoUrl().toString());

        HashMap<String, Object> following = new HashMap<>();
        following.put("userUid", otherUserUid);
        following.put("User_Name", txtUserName.getText().toString());
        if (firebaseDatabaseGetSet.getSchoolName() != null || !firebaseDatabaseGetSet.getSchoolName().isEmpty()) {
            following.put("schoolName", firebaseDatabaseGetSet.getSchoolName());
        }else {
            following.put("schoolName", "未註冊");
        }
        following.put("User_image_uri", firebaseDatabaseGetSet.getUser_image_uri());

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
                            databaseReference.child("Users_Followers_Section").child(firebaseUser.getUid()).child("FollowingInfo").child(otherUserUid).removeValue();
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
        imgPopAddImage = dialog.findViewById(R.id.pop_up_userProfile_customMessage_addPicture);
        imgPopPreview = dialog.findViewById(R.id.pop_up_userProfile_customMessage_imgPreview);
        editPopAskContent = dialog.findViewById(R.id.pop_up_userProfile_customMessage_editMessage);
        progressBar = dialog.findViewById(R.id.pop_up_userProfile_customMessage_progressBar);

        uploadTimeStamp = String.valueOf(System.currentTimeMillis());
        AskQuesitonUid = databaseReference.child("Personal_Ask_Question").child(firebaseUser.getUid()).push().getKey();
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
                    Toast.makeText(OtherUserProfileActivity.this, getString(R.string.question_can_not_be_emoty), Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgPopAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_IMAGE_SELECT);
            }
        });

        txtPopCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        imgPopPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null){
                    deletePhotoPop();
                }
            }
        });
    }

    private void uploadPersonalAsk(String question){

        String randomAskUid = databaseReference.child("Personal_Ask_Question").push().getKey();
        String AskDate = editRelatedMethod.getUploadDate();

        HashMap<String, Object> askBy = new HashMap<>();
        askBy.put("AnswerUid", otherUserUid);
        askBy.put("AnswerName", firebaseDatabaseGetSet.getID());
        askBy.put("AskerUid", firebaseUser.getUid());
        askBy.put("AskerName", firebaseUser.getDisplayName());
        askBy.put("AskDate", AskDate);
        askBy.put("AskQuestionContent", question);
        askBy.put("AskQuesitonUid", randomAskUid);
        if (imageUri != null){
            askBy.put("QuestionTumbnail", imageUri);
        }
        databaseReference.child("Personal_Ask_Question").child(otherUserUid).child("AskedBy").child(randomAskUid).updateChildren(askBy);
        databaseReference.child("Personal_Ask_Question").child(firebaseUser.getUid()).child("AskTo").child(randomAskUid).updateChildren(askBy);
    }

    private void guestClick(){
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OtherUserProfileActivity.this, "Null user", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OtherUserProfileActivity.this, "Null user", Toast.LENGTH_SHORT).show();

            }
        });

        btnSendMessageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OtherUserProfileActivity.this, "Null user", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void UploadPhotoToFirebase(Uri uri){
        storageReference = storageReference.child("Personal_Ask_Request_Images").child(AskQuesitonUid).child(firebaseUser.getUid()).child(uploadTimeStamp);
        storageReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()){
                    throw task.getException();
                }else {
                    return storageReference.getDownloadUrl();
                }
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                Picasso.get().load(task.getResult()).fit().into(imgPopPreview);
                imageUri = task.getResult().toString();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void ClickToDeletePhoto(){
        imageUri = null;
        imgPopPreview.setImageResource(0);
        storageReference.delete();
    }

    private void deletePhotoPop(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OtherUserProfileActivity.this);
        builder.setTitle(R.string.delete_photo).setMessage(R.string.click_to_delete_photo).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ClickToDeletePhoto();
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();
        builder.show();
    }

    private void reportUser(String reportCause){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Report_User_Name", firebaseUser.getDisplayName());
        hashMap.put("Report_UserUid", firebaseUser.getUid());
        hashMap.put("gotReport_User_Namw", firebaseDatabaseGetSet.getID());
        hashMap.put("getReport_UserUid", firebaseDatabaseGetSet.getUserUid());
        hashMap.put("Report_Reason", reportCause);
        String randomId = databaseReference.child("Report_user_section").child(firebaseUser.getUid()).push().getKey();
        databaseReference.child("Report_user_section").child(firebaseDatabaseGetSet.getUserUid()).child(randomId).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(OtherUserProfileActivity.this, " 檢舉成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(OtherUserProfileActivity.this, " 檢舉失敗，請再試一次", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createReportDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(OtherUserProfileActivity.this);
        createReportReasons();

        builder.setTitle(R.string.report_user).setSingleChoiceItems(reportReasons, reportReasons.length, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reportReason = reportReasons[which];
            }
        }).setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reportUser(reportReason);
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create();

        builder.show();
    }

    private void createReportReasons(){
        reportReasons[0] = "使用者圖片不當";
        reportReasons[1] = "使用者ID不當";
        reportReasons[2] = "使用者約定無故未到";
        reportReasons[3] = "惡意訊息傳送、宣傳";
        reportReasons[4] = "惡意留言、故意謾罵其他人";
        reportReasons[5] = "個人訊息充滿商業宣傳之行為";
        reportReasons[6] = "意圖不當與他人見面之行為";
        reportReasons[7] = "惡意洗版";
        reportReasons[8] = "其他不當或違規項目";

    }



}
