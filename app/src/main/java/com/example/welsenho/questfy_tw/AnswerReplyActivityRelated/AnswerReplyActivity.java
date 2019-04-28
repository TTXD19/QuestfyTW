package com.example.welsenho.questfy_tw.AnswerReplyActivityRelated;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class AnswerReplyActivity extends AppCompatActivity{

    private AnserReplyRelatedMethods anserReplyRelatedMethods;

    private CircleImageView circleImageView;
    private TextView txtUserName;
    private TextView txtUpdateDate;
    private ImageView imgAddPhoto;
    private EditText editAnswer;
    private Button btnCancel;
    private Button btnReply;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private String randomID;
    private String Article_ID;
    private String Article_Title;
    private Boolean isNetworkAvaliable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_reply);


        Article_ID = getIntent().getStringExtra("Article_ID");
        Article_Title = getIntent().getStringExtra("Article_Title");
        ItemInit();
        FirebaseInit();
        LoadBasicInfo();
        ItemClick();


    }


    private void FirebaseInit(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        randomID = databaseReference.child("ArticleAnswers").child(Article_ID).push().getKey();
    }

    private void UploadFirebase(){

        int AnswerCount = editAnswer.getText().toString().trim().length();
        if (AnswerCount >= 15){
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("UserID", firebaseUser.getUid());
            hashMap.put("UserName", firebaseUser.getDisplayName());
            hashMap.put("UserImage", firebaseUser.getPhotoUrl().toString());
            hashMap.put("UpdateDate", txtUpdateDate.getText().toString());
            hashMap.put("AnswerContent", editAnswer.getText().toString());
            hashMap.put("AnswerID", randomID);
            databaseReference.child("ArticleAnswers").child(Article_ID).child(randomID).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        Intent intent = new Intent(AnswerReplyActivity.this, ReadAnswersActivity.class);
                        intent.putExtra("Article_ID", Article_ID);
                        intent.putExtra("Article_Title", Article_Title);
                        startActivity(intent);
                        finish();
                    }else {
                        progressDialog.dismiss();
                        buildDialouge("Error", "Sorry we're currently having a trouble, please try again later", "OK");
                    }
                }
            });
        }else {
            progressDialog.dismiss();
            buildDialouge("Answer Error", "Answer length must be over 15", "OK");
        }

    }

    private void DeleteFromFirebase(){
        databaseReference.child("ArticleAnswers").child(Article_ID).child(randomID).removeValue();
        Intent intent = new Intent(AnswerReplyActivity.this, ReadAnswersActivity.class);
        intent.putExtra("Article_ID", Article_ID);
        intent.putExtra("Article_Title", Article_Title);
        startActivity(intent);
        finish();
    }


    private void ItemInit(){
        circleImageView = findViewById(R.id.answer_reply_imgUser);
        txtUserName = findViewById(R.id.answer_reply_txtUserName);
        txtUpdateDate = findViewById(R.id.answer_reply_txtUpdateDate);
        imgAddPhoto = findViewById(R.id.answer_reply_imgBtnAddPicture);
        editAnswer = findViewById(R.id.read_answers_editAnswer);
        btnCancel = findViewById(R.id.answer_reply_btnCancel);
        btnReply = findViewById(R.id.answer_reply_btnReply);
        recyclerView = findViewById(R.id.answer_reply_recyclerViewImage);
        isNetworkAvaliable = false;
        progressDialog = new ProgressDialog(this);
        anserReplyRelatedMethods = new AnserReplyRelatedMethods();
    }

    private void ItemClick(){

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnetivity()) {
                    progressDialog.setTitle("Updating your answer");
                    progressDialog.setMessage("Please hold on for a moment, we're updating your answer.......");
                    progressDialog.show();
                    UploadFirebase();
                }else {
                    buildDialouge("Network error", "Please turn on your network connection", "OK");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteFromFirebase();
            }
        });
    }

    private void buildDialouge(String Title, String Message, String postiveMessage){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Title).setMessage(Message).setPositiveButton(postiveMessage, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();


    }

    private void LoadBasicInfo(){
        txtUserName.setText(firebaseUser.getDisplayName());
        Picasso.get().load(firebaseUser.getPhotoUrl()).into(circleImageView);
        txtUpdateDate.setText(anserReplyRelatedMethods.getUploadDate());
    }

    private Boolean checkConnetivity(){
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
