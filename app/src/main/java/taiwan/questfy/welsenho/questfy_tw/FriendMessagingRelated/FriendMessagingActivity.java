package taiwan.questfy.welsenho.questfy_tw.FriendMessagingRelated;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;

import taiwan.questfy.welsenho.questfy_tw.AnswerReplyActivityRelated.AnswerReplyActivity;
import taiwan.questfy.welsenho.questfy_tw.AnswerReplyActivityRelated.EnlargeAnswerImageActivity;
import taiwan.questfy.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.EnlargeImageActivity;

public class FriendMessagingActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_SELECT = 0;

    private String FriendUid;
    private String FriendName;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private EditRelatedMethod editRelatedMethod;

    private Toolbar toolbar;
    private TextView txtSend;
    private EditText editMessage;
    private ImageButton imgBtnPicture;
    private RecyclerView recyclerView;
    private FriendMessagingRecyclerAdapter adapter;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_messaging);

        FriendUid = getIntent().getStringExtra("FriendUid");
        FriendName = getIntent().getStringExtra("FriendName");

        InitItem();
        InitRecyclerView();
        InitFirebase();
        ItemClick();
        getMessageData();
    }

    private void InitItem(){
        toolbar = findViewById(R.id.friend_messaging_toolBar);
        txtSend = findViewById(R.id.friend_messaging_txtSend);
        editMessage = findViewById(R.id.friend_messaging_editMessage);
        imgBtnPicture = findViewById(R.id.friend_messaging_imgBtn_Picture);
        recyclerView = findViewById(R.id.friend_messaging_recyclerView);
        editRelatedMethod = new EditRelatedMethod();
        arrayList = new ArrayList<>();
        adapter = new FriendMessagingRecyclerAdapter(arrayList, this, new FriendMessagingRecyclerAdapter.ImageClick() {
            @Override
            public void onImageClick(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Intent intent = new Intent(FriendMessagingActivity.this, EnlargeAnswerImageActivity.class);
                intent.putExtra("imageUri", arrayList.get(position).getMessage());
                startActivity(intent);
            }
        });
        toolbar.setTitle(FriendName);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(valueEventListener);
    }

    private void InitFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void ItemClick(){
        detectTyping();
        txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        imgBtnPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImage();
            }
        });
    }

    private void sendMessage(){
        String message = editMessage.getText().toString();
        if (!message.isEmpty()){
            String randonMessageID = databaseReference.child("FriendMessages").push().getKey().toString();
            HashMap<String, Object> selfHashMap = new HashMap<>();
            selfHashMap.put("Message", message);
            selfHashMap.put("MessageUserUid", firebaseUser.getUid());
            selfHashMap.put("MessageType", "Text");
            selfHashMap.put("MessageUserImage", firebaseUser.getPhotoUrl().toString());

            HashMap<String, Object> updateMessage = new HashMap<>();
            updateMessage.put("LatestMessage", message);

            databaseReference.child("FriendMessages").child(firebaseUser.getUid()).child(FriendUid).child(randonMessageID).updateChildren(selfHashMap);
            databaseReference.child("FriendMessages").child(FriendUid).child(firebaseUser.getUid()).child(randonMessageID).updateChildren(selfHashMap);
            databaseReference.child("UserFriendList").child(firebaseUser.getUid()).child(FriendUid).updateChildren(updateMessage);
            databaseReference.child("UserFriendList").child(FriendUid).child(firebaseUser.getUid()).updateChildren(updateMessage);

            editMessage.setText("");
        }
    }

    private void InitRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void getMessageData(){
         valueEventListener = databaseReference.child("FriendMessages").child(firebaseUser.getUid()).child(FriendUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    arrayList.clear();
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        FirebaseDatabaseGetSet getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(getSet);
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

    private void getImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_SELECT){
            if (resultCode == RESULT_OK){
                if (data != null){
                    progressDialog.setTitle("上傳中");
                    progressDialog.setMessage("圖片上傳中...");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    UploadPhotoFirebase(storageReference, data.getData());
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void UploadPhotoFirebase(final StorageReference storageReference, final Uri uri) {

        final String date = editRelatedMethod.getDate();

        UploadTask uploadTask = storageReference.child("User_Messaging_Image").child(firebaseUser.getUid()).child(FriendUid).child(date).putFile(uri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("FirebaseTask", "Success");
                    StorageReference storageReference1 = storageReference.child("User_Messaging_Image").child(firebaseUser.getUid()).child(FriendUid).child(date);
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            progressDialog.dismiss();
                            imageUploadDatebase(uri.toString());
                            Toast.makeText(FriendMessagingActivity.this, "Success", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("圖片上傳進度" + (int)progress + "%");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(FriendMessagingActivity.this, "上傳失敗，請在試一次", Toast.LENGTH_SHORT).show();
            }
        });;
    }

    private void imageUploadDatebase(String imgLink){

        String randonMessageID = databaseReference.child("FriendMessages").push().getKey().toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Message", imgLink);
        hashMap.put("MessageUserUid", firebaseUser.getUid());
        hashMap.put("MessageType", "Image");
        hashMap.put("MessageUserImage", firebaseUser.getPhotoUrl().toString());

        HashMap<String, Object> latestMessage = new HashMap<>();
        latestMessage.put("LatestMessage", "New Image");

        databaseReference.child("FriendMessages").child(firebaseUser.getUid()).child(FriendUid).child(randonMessageID).updateChildren(hashMap);
        databaseReference.child("FriendMessages").child(FriendUid).child(firebaseUser.getUid()).child(randonMessageID).updateChildren(hashMap);
        databaseReference.child("UserFriendList").child(firebaseUser.getUid()).child(FriendUid).updateChildren(latestMessage);
        databaseReference.child("UserFriendList").child(FriendUid).child(firebaseUser.getUid()).updateChildren(latestMessage);
    }

    private void detectTyping(){
        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() < 1){
                    txtSend.setClickable(false);
                }else {
                    txtSend.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
