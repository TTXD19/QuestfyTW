package taiwan.questfy.welsenho.questfy_tw.OtherUserProfileRelatedMethod;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
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

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.MainOnClickListener;
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.list_article_recyclerView_adapter;
import taiwan.questfy.welsenho.questfy_tw.R;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;

public class OtherUserProfileActivity extends AppCompatActivity implements UserAskedQuestionsFragment.OnFragmentInteractionListener, CheckFollowersFragment.OnCheckFollowersFragment, AnsweredHitstoryFragment.OnAnsweredHistory {

    private static final int REQUEST_IMAGE_SELECT = 0;

    private String otherUserUid;
    private String SelfUid;
    private String AskQuesitonUid;
    private String uploadTimeStamp;
    private String imageUri;
    private String seldUserSchoolName;
    private String reportReason;
    private String[] reportReasons = new String[9];

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
    private TextView txtMaxCount;
    private ImageView imgPopAddImage;
    private ImageView imgPopPreview;
    private EditText editPopAskContent;
    private CircleImageView circleImageView;
    private Button btnFollow;
    private Button btnAddFriend;
    private Button btnSendMessageQuestion;
    private Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    private Dialog dialog;
    private ProgressBar progressBar;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;


    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private OtherUserProfileRelatedMethods otherUserProfileRelatedMethods;
    private EditRelatedMethod editRelatedMethod;
    private UserAskedQuestionsFragment userAskedQuestionsFragment;


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
        if (firebaseUser != null) {
            getSelfUserInfo();
            if (!firebaseUser.getUid().equals(otherUserUid)) {
                Toast.makeText(this, "different user", Toast.LENGTH_SHORT).show();
                detectFriend();
                detectFollowing();
                onItemClick();
            } else {
                getToSelfPage();
            }
        } else {
            guestClick();
        }
        getFollowersCount();
        setViewPager();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_SELECT) {
            if (resultCode == RESULT_OK) {
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
        switch (itemId) {
            case R.id.read_article_report:
                if (firebaseUser != null) {
                    createReportDialog();
                } else {
                    Toast.makeText(this, "需登入回報", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return super.onSupportNavigateUp();
    }

    private void InitItem() {
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
        coordinatorLayout = findViewById(R.id.otherUser_profile_coordinatorLayout);
        toolbar = findViewById(R.id.otherUser_profile_toolBar);
        tabLayout = findViewById(R.id.otherUser_profile_tabLayout);
        viewPager = findViewById(R.id.otherUser_profile_viewPager);
        collapsingToolbarLayout = findViewById(R.id.otherUser_profile_collaspingToolBar);
        appBarLayout = findViewById(R.id.otherUser_profile_appBayLayout);
        otherUserUid = getIntent().getStringExtra("otherUserUid");

        otherUserProfileRelatedMethods = new OtherUserProfileRelatedMethods();
        editRelatedMethod = new EditRelatedMethod();

        dialog = new Dialog(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("  ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void InitFirebaseItem() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference();
        SelfUid = firebaseAuth.getUid();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void getOtherUserInfo() {

        databaseReference.child("Users_profile").child(otherUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    firebaseDatabaseGetSet = dataSnapshot.getValue(FirebaseDatabaseGetSet.class);
                    txtUserName.setText(firebaseDatabaseGetSet.getID());
                    getSupportActionBar().setTitle(firebaseDatabaseGetSet.getID() + "的個人資訊");
                    if (firebaseDatabaseGetSet.getSchoolName() != null) {
                        txtUniversityName.setText(firebaseDatabaseGetSet.getSchoolName());
                        txtCorseName.setText(firebaseDatabaseGetSet.getCourseName());
                    } else {
                        txtUniversityName.setText("尚未審核");
                        txtCorseName.setText("尚未審核");
                    }
                    if (firebaseDatabaseGetSet.getUserSpeciality() != null) {
                        txtSpecility.setText(firebaseDatabaseGetSet.getUserSpeciality());
                    } else {
                        txtSpecility.setText("");
                    }
                    if (firebaseDatabaseGetSet.getUserStatusMessage() != null) {
                        txtStatusMessage.setText(firebaseDatabaseGetSet.getUserStatusMessage());
                    } else {
                        txtStatusMessage.setText("");
                    }
                    Picasso.get().load(firebaseDatabaseGetSet.getUser_image_uri()).fit().into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {

            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + i == 0) {
                    collapsingToolbarLayout.setTitle(firebaseDatabaseGetSet.getID());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("  ");
                    isShow = false;
                }
            }
        });

        databaseReference.child("Users_profile").child(otherUserUid).child("AnsweredCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long n;
                ArrayList<Long> answerCountList = new ArrayList<>();
                answerCountList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        n = DS.getChildrenCount();
                        Log.d("AnswerCount", String.valueOf(n));
                        answerCountList.add(n);
                        Log.d("AnswerCountSum", answerCountList.toString());

                    }

                    n = 0;
                    for (int count = 0; count <= answerCountList.size() - 1; count++) {
                        n += answerCountList.get(count);
                    }
                    txtAnsweredCount.setText(String.valueOf(n));
                } else {
                    txtAnsweredCount.setText(String.valueOf(0));
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
                if (dataSnapshot.exists()) {
                    seldUserSchoolName = dataSnapshot.getValue().toString();
                } else {
                    seldUserSchoolName = "為註冊";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void FriendAddingProcess() {

        String ProcessKey = databaseReference.child("FriendAddingProcess").push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("RecieverUid", otherUserUid);
        hashMap.put("SenderUid", SelfUid);
        hashMap.put("Status", "UnFriend");
        hashMap.put("RequestDate", otherUserProfileRelatedMethods.getRequestDate());
        hashMap.put("RequestName", firebaseUser.getDisplayName());
        hashMap.put("RequestUserImage", firebaseUser.getPhotoUrl().toString());


        databaseReference.child("FriendAddingProcess").child(otherUserUid).child(SelfUid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    createSnackBar();
                }
            }
        });
        databaseReference.child("FriendAddingProcess").child(SelfUid).child(otherUserUid).updateChildren(hashMap);
    }

    private void acceptFriendRequest(String friendUid, String friendName, String selfUid, String selfName) {
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

    private void createSnackBar() {
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

    private void detectFriend() {
        databaseReference.child("FriendAddingProcess").child(SelfUid).child(otherUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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

    private void detectAddedFriend() {
        databaseReference.child("UserFriendList").child(SelfUid).child(otherUserUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    btnAddFriend.setText(getString(R.string.friend_added));
                    btnAddFriend.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.FullWhite));
                    btnAddFriend.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.add_friend_btn_background));
                    btnAddFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(OtherUserProfileActivity.this, "已加為好友", Toast.LENGTH_SHORT).show();
                        }
                    });
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

    private void followUser() {
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
        } else {
            following.put("schoolName", "未註冊大學");
        }
        following.put("User_image_uri", firebaseDatabaseGetSet.getUser_image_uri());

        databaseReference.child("Users_Followers_Section").child(firebaseUser.getUid()).child("FollowingInfo").child(otherUserUid).updateChildren(following);
        databaseReference.child("Users_Followers_Section").child(otherUserUid).child("Follow_by").child(firebaseUser.getUid()).updateChildren(followBy);
    }

    private void onItemClick() {
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

    private void detectFollowing() {
        databaseReference.child("Users_Followers_Section").child(otherUserUid).child("Follow_by").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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
                } else {
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

    private void getFollowersCount() {
        databaseReference.child("Users_Followers_Section").child(otherUserUid).child("FollowBy").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    txtFollowesCount.setText(String.valueOf(dataSnapshot.getValue()));
                } else {
                    txtFollowesCount.setText(String.valueOf(0));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void askMeAnyQustion() {
        dialog.setContentView(R.layout.user_profile_custom_message_editing);
        txtPopTitle = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtTitle);
        txtPopCount = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtCount);
        txtMaxCount = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtMaxCount);
        txtPopAsk = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtSave);
        txtPopCancel = dialog.findViewById(R.id.pop_up_userProfile_customMessage_txtCancel);
        imgPopAddImage = dialog.findViewById(R.id.pop_up_userProfile_customMessage_addPicture);
        imgPopPreview = dialog.findViewById(R.id.pop_up_userProfile_customMessage_imgPreview);
        editPopAskContent = dialog.findViewById(R.id.pop_up_userProfile_customMessage_editMessage);
        progressBar = dialog.findViewById(R.id.pop_up_userProfile_customMessage_progressBar);

        uploadTimeStamp = String.valueOf(System.currentTimeMillis());
        AskQuesitonUid = databaseReference.child("Personal_Ask_Question").child(firebaseUser.getUid()).push().getKey();
        String title = getString(R.string.ask) + txtUserName.getText().toString() + getString(R.string.anything);
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
                if (!editPopAskContent.getText().toString().isEmpty()) {
                    uploadPersonalAsk(editPopAskContent.getText().toString());
                    dialog.dismiss();
                } else {
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
                if (imageUri != null) {
                    deletePhotoPop();
                }
            }
        });

        txtPopCount.setVisibility(View.GONE);
        txtMaxCount.setVisibility(View.GONE);
    }

    private void uploadPersonalAsk(String question) {

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
        if (imageUri != null) {
            askBy.put("QuestionTumbnail", imageUri);
        }
        databaseReference.child("Personal_Ask_Question").child(otherUserUid).child("AskedBy").child(randomAskUid).updateChildren(askBy);
        databaseReference.child("Personal_Ask_Question").child(firebaseUser.getUid()).child("AskTo").child(randomAskUid).updateChildren(askBy);
    }

    private void guestClick() {
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OtherUserProfileActivity.this, "登入即可追蹤這位使用者", Toast.LENGTH_SHORT).show();
            }
        });

        btnAddFriend.setText(R.string.add_friend);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OtherUserProfileActivity.this, "登入即可加入好友", Toast.LENGTH_SHORT).show();

            }
        });

        btnSendMessageQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OtherUserProfileActivity.this, "登入即可詢問這位使用者您的問題", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void UploadPhotoToFirebase(Uri uri) {
        storageReference = storageReference.child("Personal_Ask_Request_Images").child(AskQuesitonUid).child(firebaseUser.getUid()).child(uploadTimeStamp);
        storageReference.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                } else {
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

    private void ClickToDeletePhoto() {
        imageUri = null;
        imgPopPreview.setImageResource(0);
        storageReference.delete();
    }

    private void deletePhotoPop() {
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

    private void reportUser(String reportCause) {
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
                if (task.isSuccessful()) {
                    Toast.makeText(OtherUserProfileActivity.this, " 檢舉成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(OtherUserProfileActivity.this, " 檢舉失敗，請再試一次", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createReportDialog() {
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

    private void createReportReasons() {
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

    private void getToSelfPage() {
        btnAddFriend.setVisibility(View.GONE);
        btnSendMessageQuestion.setVisibility(View.GONE);
        btnFollow.setVisibility(View.GONE);
    }

    private void setViewPager() {
        viewPager.setAdapter(new OtherUserProfileTabAdapter(getSupportFragmentManager()));
        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                Toast.makeText(OtherUserProfileActivity.this, String.valueOf(viewPager.getCurrentItem()), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onFragmentInteraction(String postCount) {
        txtPostsCount.setText(postCount);
    }

    @Override
    public Bundle getBundle() {
        Bundle args = new Bundle();
        args.putString("otherUserUid", otherUserUid);
        return args;
    }

}
