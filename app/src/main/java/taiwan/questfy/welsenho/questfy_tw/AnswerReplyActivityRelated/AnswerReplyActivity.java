package taiwan.questfy.welsenho.questfy_tw.AnswerReplyActivityRelated;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import taiwan.questfy.welsenho.questfy_tw.R;

public class AnswerReplyActivity extends AppCompatActivity{




    private String imageUri;

    private AnserReplyRelatedMethods anserReplyRelatedMethods;

    private CircleImageView circleImageView;
    private TextView txtUserName;
    private TextView txtUpdateDate;
    private ImageView imgAddPhoto;
    private ImageView imageShowImage;
    private EditText editAnswer;
    private Button btnCancel;
    private Button btnReply;
    private ProgressDialog progressDialog;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                progressDialog.show();
                progressDialog.setMessage("Loading...");
                Toast.makeText(this, data.getData().toString(), Toast.LENGTH_SHORT).show();
                UploadPhotoFirebase(data.getData());
            }else {
                Toast.makeText(this, "RESULT NULL", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "REQUEST NULL", Toast.LENGTH_SHORT).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void FirebaseInit(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
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
            hashMap.put("Title", Article_Title);
            hashMap.put("AnswerID", randomID);
            hashMap.put("Article_ID", Article_ID);
            if (imageUri != null){
                hashMap.put("editInitImageUploadViewUri", imageUri);
            }
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
                        buildDialouge("網路錯誤", "請稍候在試一次", "OK");
                    }
                }
            });

            databaseReference.child("Users_profile").child(firebaseUser.getUid()).child("AnsweredCount").child(Article_ID).child(randomID).updateChildren(hashMap);
            databaseReference.child("UserArticleAnswers").child(firebaseUser.getUid()).child(randomID).updateChildren(hashMap);
        }else {
            progressDialog.dismiss();
            buildDialouge("喔喔 ！！！", "答案回覆需超過15字元", "OK");
        }

    }

    private void UploadPhotoFirebase(Uri uri) {
        EditRelatedMethod editRelatedMethod = new EditRelatedMethod();
        String date = editRelatedMethod.getDate();

       storageReference = storageReference.child("Question_Answer_Reply_Images").child(Article_ID).child(firebaseUser.getUid()).child(date);
        storageReference.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("FirebaseTask", "Success");
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUri = uri.toString();
                            Toast.makeText(AnswerReplyActivity.this, imageUri, Toast.LENGTH_SHORT).show();
                            Picasso.get().load(imageUri).fit().into(imageShowImage);
                            progressDialog.dismiss();
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
                Toast.makeText(AnswerReplyActivity.this, "上傳失敗，請在試一次", Toast.LENGTH_SHORT).show();
            }
        });
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
        imageShowImage = findViewById(R.id.read_answers_editShowImage);
        isNetworkAvaliable = false;
        progressDialog = new ProgressDialog(this);
        anserReplyRelatedMethods = new AnserReplyRelatedMethods();
    }

    private void ItemClick(){

        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkConnetivity()) {
                    progressDialog.setTitle("正在更新您的回覆中");
                    progressDialog.setMessage("請稍候，更新即將完成");
                    progressDialog.show();
                    UploadFirebase();
                }else {
                    buildDialouge("網路錯誤", "請稍候在是一次", "OK");
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteFromFirebase();
            }
        });

        imgAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPhoto();
            }
        });

        imageShowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null){
                    imageShowImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            askDeletePhoto();
                        }
                    });
                }
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

    private void addPhoto(){
       Intent intent = new Intent(Intent.ACTION_PICK);
       intent.setType("image/*");
       startActivityForResult(intent, 1);
    }

    private void askDeletePhoto(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("移除照片 ？").setMessage("按下確定將移除照片").setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                imageUri = null;
                imageShowImage.setImageResource(0);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

}
