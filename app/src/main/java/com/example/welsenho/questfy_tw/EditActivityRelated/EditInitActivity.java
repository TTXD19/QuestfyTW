package com.example.welsenho.questfy_tw.EditActivityRelated;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.MainUserActivity.MainActivity;
import com.example.welsenho.questfy_tw.R;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class EditInitActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;

    private String getDownloadUri;
    private String articleUid;
    private int countImages;

    private Toolbar toolbar;
    private TextView txtChooseMajor;
    private TextView txtUserName;
    private TextView txtUploadDate;
    private EditText editTitle;
    private EditText editContent;
    private ImageView imgTakePhoto;
    private ImageView imgChooseImage;
    private ImageView imgPreview;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private ArrayList<String> getMajors;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;

    private EditRelatedMethod editRelatedMethod;
    private EditInitRelateRecyclerViewAdapterImageViews adapterImageViews;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_init);

        toolbar = findViewById(R.id.edit_init_toolbar);
        txtChooseMajor = findViewById(R.id.edit_init_txt_chooseMajor);
        txtUserName = findViewById(R.id.edit_init_txt_userName);
        txtUploadDate = findViewById(R.id.edit_init_txt_UploadDate);
        editTitle = findViewById(R.id.edit_init_edit_Title);
        editContent = findViewById(R.id.edit_init_edit_Content);
        imgTakePhoto = findViewById(R.id.edit_init_img_takeImage);
        imgChooseImage = findViewById(R.id.edit_init_img_addImage);
        imgPreview = findViewById(R.id.edit_init_img_preview);
        recyclerView = findViewById(R.id.edit_init_recyclerView);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        editRelatedMethod = new EditRelatedMethod();
        storageReference = firebaseStorage.getReference();
        getMajors = new ArrayList<>();

        progressDialog = new ProgressDialog(this);

        txtUserName.setText(firebaseUser.getDisplayName());
        txtUploadDate.setText(editRelatedMethod.getUploadDate());

        //Relate to MajorChoose BroadCast Intent
        if (getIntent().getStringArrayListExtra("getMajors") != null) {
            getMajors.clear();
            getMajors = getIntent().getStringArrayListExtra("getMajors");
            String major = getMajors.toString();
            txtChooseMajor.setText(major);
            showCurrent();
        }


        //Decide to push a random key or not
        if (articleUid == null) {
            articleUid = databaseReference.push().getKey();
            countImages = 0;
        }

        /**
         * SET RECYCLERVIEW
         */
        arrayList = new ArrayList<>();
        adapterImageViews = new EditInitRelateRecyclerViewAdapterImageViews(arrayList);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setHasFixedSize(true);
        LoadImageFromFirebase();

        /**
         * SET TOOL BAR
         */
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * HANDLE EACH BUTTON AND TEXTVIEW CLICK
         */
        ItemClick();
    }

    @Override
    public boolean onSupportNavigateUp() {
        databaseReference.child("Users_Question_Articles").child(articleUid).removeValue();
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_init_activity_next, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.EditInitItemNext:
                if (UploadFirebaseDatabase()) {
                    Intent intent = new Intent(EditInitActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Something is empty", Toast.LENGTH_SHORT).show();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void ItemClick() {
        txtChooseMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRelatedMethod.saveCurrent(editTitle, editContent, EditInitActivity.this, articleUid, countImages);
                Intent intent = new Intent(EditInitActivity.this, EditQuestionRelateMajorChose.class);
                startActivity(intent);
            }
        });

        imgTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRelatedMethod.saveCurrent(editTitle, editContent, EditInitActivity.this, articleUid, countImages);
                dispatchTakePictureIntent();
            }
        });

        imgChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editRelatedMethod.saveCurrent(editTitle, editContent, EditInitActivity.this, articleUid, countImages);
                selectPhotoFromMedia();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                File imgFile = new File(editRelatedMethod.getmCurrentPhotoPath());
                if (imgFile.exists()) {
                    UploadPhotoFirebase(firebaseUser, storageReference, Uri.fromFile(imgFile));
                }
            }
        } else if (requestCode == REQUEST_IMAGE_SELECT) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    UploadPhotoFirebase(firebaseUser, storageReference, data.getData());
                }
            }
        }
        showCurrent();
    }

    /**
     * For User take an Instant photo save it and create a unique file name then upload to firebase storage
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File file = null;
            try {
                file = editRelatedMethod.createImageFile(this);
            } catch (IOException ex) {

            }
            if (file != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.welsenho.provider",
                        file);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Log.d("Photouri", photoURI.toString());
            }
        }
    }

    private void selectPhotoFromMedia() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_SELECT);
    }

    private Boolean UploadFirebaseDatabase() {
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();
        if (!getMajors.isEmpty() && !title.isEmpty() && !content.isEmpty()) {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("User_ID", firebaseUser.getUid());
            hashMap.put("User_Image", "null");
            hashMap.put("User_Name", txtUserName.getText().toString());
            hashMap.put("Upload_Date", txtUploadDate.getText().toString());
            hashMap.put("Title", title);
            hashMap.put("Majors", getMajors.toString());
            hashMap.put("Content", content);
            databaseReference.child("Users_Question_Articles").child(articleUid).updateChildren(hashMap);
            return true;
        } else {
            return false;
        }

    }

    private void UploadPhotoFirebase(final FirebaseUser firebaseUser, final StorageReference storageReference, final Uri uri) {

        progressDialog.show();
        progressDialog.setMessage("Uploading Photo...");
        final String date = editRelatedMethod.getDate();
        UploadTask uploadTask = storageReference.child("User_Article_Upload_Image").child(firebaseUser.getUid()).child(date).putFile(uri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("FirebaseTask", "Success");
                    StorageReference storageReference1 = storageReference.child("User_Article_Upload_Image").child(firebaseUser.getUid()).child(date);
                    storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            addImageLink(uri);
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        });
    }

    private void addImageLink(Uri uri) {
        /**
         * Upload Image Link to firebase
         */
        countImages++;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("editInitImageUploadViewUri", String.valueOf(uri));
        databaseReference.child("Users_Question_Articles").child(articleUid).child("Images").child("Images" + countImages).updateChildren(hashMap);
        LoadImageFromFirebase();

    }

    private void LoadImageFromFirebase(){
        databaseReference.child("Users_Question_Articles").child(articleUid).child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.exists()){
                    arrayList.clear();
                    Log.d("FirebaseDatabase", "exist");
                    for (DataSnapshot DS : dataSnapshot.getChildren()){
                        firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                        recyclerView.setAdapter(adapterImageViews);
                        Log.d("RecyclerViewTask", "Success");
                    }
                }else {
                    Log.d("FirebaseDatabase", "not exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void showCurrent(){
        editRelatedMethod.showCurrent(editTitle, editContent, EditInitActivity.this);
        articleUid = editRelatedMethod.getArticleUid();
        countImages = editRelatedMethod.getCountImages();
    }

}
