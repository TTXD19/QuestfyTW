package com.example.welsenho.questfy_tw.PersonAskQuestionRelated;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import com.example.welsenho.questfy_tw.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class PersonalAskReplyingActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_SELECT = 0;

    private String questionUid;
    private String imageUri;
    private String imageDate;
    private EditRelatedMethod editRelatedMethod;

    private TextView txtUserName;
    private TextView txtUploadData;
    private ImageView imageView;
    private ImageView imgPreview;
    private Button btnCancel;
    private Button btnReply;
    private EditText editContent;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_ask_replying);

        questionUid = getIntent().getStringExtra("questionUid");

        InitFirebase();
        InitItem();
        ItemClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_SELECT ){
            if (resultCode == RESULT_OK){
                Uri photoUri = data.getData();
                UploadPhoto(photoUri);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void InitFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void ItemClick() {
        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadFirebase();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PersonalAskReplyingActivity.this, PersonalAskQuestReplyActivity.class);
                intent.putExtra("questionUid", questionUid);
                startActivity(intent);
                finish();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null){
                    AlertDialog.Builder builder = new AlertDialog.Builder(PersonalAskReplyingActivity.this);
                    builder.setTitle("Delete photo").setMessage("Click yes to delete this photo").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClickToDeletePhoto();
                            dialog.dismiss();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create();
                    builder.show();
                }
            }
        });
    }

    private void InitItem() {
        txtUserName = findViewById(R.id.person_ask_replying_txtUserName);
        txtUploadData = findViewById(R.id.person_ask_replying_txtUpdateDate);
        imageView = findViewById(R.id.person_ask_replying_imgBtnAddPicture);
        imgPreview = findViewById(R.id.person_ask_replying_imgPreview);
        btnReply = findViewById(R.id.person_ask_replying_btnReply);
        btnCancel = findViewById(R.id.person_ask_replying_btnCancel);
        editContent = findViewById(R.id.person_ask_replying_editAnswer);

        progressDialog = new ProgressDialog(this);
        editRelatedMethod = new EditRelatedMethod();

        txtUserName.setText(firebaseUser.getDisplayName());
        txtUploadData.setText(editRelatedMethod.getUploadDate());
    }

    private void UploadFirebase(){
        String content = editContent.getText().toString();
        String randomUid = databaseReference.child("Personal_Ask_Question_Reply").push().getKey();
        if (content.trim().length() >= 15){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("AskQuesitonUid", questionUid);
            hashMap.put("User_Name", firebaseUser.getDisplayName());
            hashMap.put("userUid", firebaseUser.getUid());
            hashMap.put("Content", content);
            long uploadTimeStamp = System.currentTimeMillis();
            uploadTimeStamp *= -1;
            hashMap.put("uploadTimeStamp", uploadTimeStamp);
            databaseReference.child("Personal_Ask_Question_Reply").child(questionUid).child(randomUid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Intent intent = new Intent(PersonalAskReplyingActivity.this, PersonalAskQuestReplyActivity.class);
                        intent.putExtra("questionUid", questionUid);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }else {
            Toast.makeText(this, "Answer must be over 15 characters", Toast.LENGTH_SHORT).show();
        }


    }

    private void selectPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    private void UploadPhoto(final Uri photoUri){
        progressDialog.setTitle("Loading");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        EditRelatedMethod editRelatedMethod = new EditRelatedMethod();
        String date = editRelatedMethod.getDate();
        imageDate = date;
        storageReference = storageReference.child("Personal_Ask_Request_Images").child(firebaseUser.getUid()).child(date);
        storageReference.putFile(photoUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                if (task.isSuccessful()){
                    Toast.makeText(PersonalAskReplyingActivity.this, task.getResult().toString(), Toast.LENGTH_SHORT).show();
                    imageUri = task.getResult().toString();
                    Picasso.get().load(task.getResult()).fit().into(imgPreview);
                    progressDialog.dismiss();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(PersonalAskReplyingActivity.this, "Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ClickToDeletePhoto(){
        imageUri = null;
        imgPreview.setImageResource(0);
        storageReference = storageReference.child("Personal_Ask_Request_Images").child(firebaseUser.getUid()).child(imageDate);
        storageReference.delete();
    }
}
