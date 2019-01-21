package com.example.welsenho.questfy_tw.EditActivityRelated;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.MainUserActivity.MainActivity;
import com.example.welsenho.questfy_tw.R;
import com.github.florent37.expansionpanel.ExpansionHeader;
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
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.numberpad.NumberPadTimePickerDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class EditInitActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, BottomSheetTimePickerDialog.OnTimeSetListener{

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;
    private static final String TAG = "tag";

    private String getDownloadUri;
    private String articleUid;
    private boolean hasDate = false;
    private boolean hasTime = false;
    private boolean isMeet = false;
    private int countImages;

    private Toolbar toolbar;
    private TextView txtChooseMajor;
    private TextView txtUserName;
    private TextView txtUploadDate;
    private TextView txtImageView;
    private TextView txtShowMeetUp;
    private TextView txtTimePick;
    private EditText editTitle;
    private EditText editContent;
    private ImageView imgDateTimePicker;
    private ImageView imgPlcePicker;
    private ImageView imgTakePhoto;
    private ImageView imgChooseImage;
    private ImageView imgPreview;
    private Switch swithMeetUp;
    private CircleImageView circleImageView;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private ArrayList<String> getMajors;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ExpansionHeader expansionHeader;

    private EditRelatedMethod editRelatedMethod;
    private EditInitRelateRecyclerViewAdapterImageViews adapterImageViews;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    private DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_init);

        toolbar = findViewById(R.id.edit_init_toolbar);
        txtChooseMajor = findViewById(R.id.edit_init_txt_chooseMajor);
        txtUserName = findViewById(R.id.edit_init_txt_userName);
        txtUploadDate = findViewById(R.id.edit_init_txt_UploadDate);
        txtImageView = findViewById(R.id.edit_init_txt_imageView);
        txtShowMeetUp = findViewById(R.id.edit_init_txt_DatePick);
        txtTimePick = findViewById(R.id.edit_init_txt_TimePick);
        editTitle = findViewById(R.id.edit_init_edit_Title);
        editContent = findViewById(R.id.edit_init_edit_Content);
        imgTakePhoto = findViewById(R.id.edit_init_img_takeImage);
        imgChooseImage = findViewById(R.id.edit_init_img_addImage);
        imgPreview = findViewById(R.id.edit_init_img_preview);
        imgDateTimePicker = findViewById(R.id.edit_init_img_dateTimePicker);
        imgPlcePicker = findViewById(R.id.edit_init_img_placePicker);
        swithMeetUp = findViewById(R.id.edit_init_switch_meetUp);
        circleImageView = findViewById(R.id.edit_init_circle_image_user);
        recyclerView = findViewById(R.id.edit_init_recyclerView);
        expansionHeader = findViewById(R.id.edit_init_expan_header);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        editRelatedMethod = new EditRelatedMethod();
        storageReference = firebaseStorage.getReference();

        getMajors = new ArrayList<>();
        progressDialog = new ProgressDialog(this);

        LoadUserProfile();

        //Relate to MajorChoose BroadCast Intent
        if (getIntent().getStringArrayListExtra("getMajors") != null) {
            getMajors.clear();
            getMajors = getIntent().getStringArrayListExtra("getMajors");
            String major = getMajors.toString();
            txtChooseMajor.setText(major);
            showCurrent();
        }

        /**
         * Decide to push a random key or not
         * (Initially will push a random key)
         */
        if (articleUid == null) {
            articleUid = databaseReference.push().getKey();
            expansionHeader.setVisibility(View.GONE);
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
         * Handle each item click
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
     * PROCESS SEQUENCE FROM LINE 259-->238-->307-->331-->343
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
            hashMap.put("User_Name", txtUserName.getText().toString());
            hashMap.put("Upload_Date", txtUploadDate.getText().toString());
            hashMap.put("Title", title);
            hashMap.put("Majors", getMajors.toString());
            hashMap.put("Content", content);
            hashMap.put("Article_ID", articleUid);
            if (firebaseUser.getPhotoUrl() != null){
                hashMap.put("User_Image", firebaseUser.getPhotoUrl().toString());
            }else {
                String default_Image = "https://firebasestorage.googleapis.com/v0/b/questfytw.appspot.com/o/Default_Image_ForEach_Condition%2Fuser%20(1).png?alt=media&token=5122a33f-5392-4877-be3d-4f519550c9b6";
                hashMap.put("User_Image", default_Image);
            }
            if (isMeet){
                hashMap.put("MeetUpDate", txtShowMeetUp.getText());
                hashMap.put("MeetUpTime", txtTimePick.getText());
            }
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
        editRelatedMethod.LoadImageFromFirebase(databaseReference, articleUid, arrayList, expansionHeader, recyclerView, adapterImageViews, txtImageView);
        //LoadImageFromFirebase();

    }

    private void LoadImageFromFirebase(){
        databaseReference.child("Users_Question_Articles").child(articleUid).child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.exists()){
                    arrayList.clear();
                    expansionHeader.setVisibility(View.VISIBLE);
                    txtImageView.setText("Loading Image...");
                    for (DataSnapshot DS : dataSnapshot.getChildren()){
                        firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                        recyclerView.setAdapter(adapterImageViews);
                        txtImageView.setText(R.string.click_to_check);
                        Log.d("RecyclerViewTask", "Success");
                    }
                }else {
                    expansionHeader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    /**
     * To show what is save in storageReference
     * Save when jump out from current activity
     * specially related to ItemClick
     */
    private void showCurrent(){
        editRelatedMethod.showCurrent(editTitle, editContent, EditInitActivity.this);
        editRelatedMethod.showMeetUpDateTime(EditInitActivity.this, txtShowMeetUp, txtTimePick);
        articleUid = editRelatedMethod.getArticleUid();
        countImages = editRelatedMethod.getCountImages();
    }

    /**
     * HANDLE EACH BUTTON AND TEXTVIEW CLICK
     * Also save current information
     */
    private void ItemClick() {
        txtChooseMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeetDateTime();
                editRelatedMethod.saveCurrent(editTitle, editContent, EditInitActivity.this, articleUid, countImages);
                Intent intent = new Intent(EditInitActivity.this, EditQuestionRelateMajorChose.class);
                startActivity(intent);
            }
        });

        imgTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeetDateTime();
                editRelatedMethod.saveCurrent(editTitle, editContent, EditInitActivity.this, articleUid, countImages);
                dispatchTakePictureIntent();
            }
        });

        imgChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeetDateTime();
                editRelatedMethod.saveCurrent(editTitle, editContent, EditInitActivity.this, articleUid, countImages);
                selectPhotoFromMedia();
            }
        });

        /**
         * For choosing Date & Time
         */
        imgDateTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPadTimePickerDialog pad = NumberPadTimePickerDialog.newInstance(EditInitActivity.this, true);
                pad.show(getSupportFragmentManager(), TAG);

                Calendar now = Calendar.getInstance();
                datePickerDialog = DatePickerDialog.newInstance(
                        EditInitActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show(getSupportFragmentManager(), TAG);
            }
        });

        swithMeetUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isMeet = true;
                    imgDateTimePicker.setVisibility(View.VISIBLE);
                    imgPlcePicker.setVisibility(View.VISIBLE);
                }else {
                    isMeet = false;
                    imgDateTimePicker.setVisibility(View.GONE);
                    imgPlcePicker.setVisibility(View.GONE);
                }
            }
        });
    }

    private void LoadUserProfile(){
        txtUserName.setText(firebaseUser.getDisplayName());
        Picasso.get().load(firebaseUser.getPhotoUrl()).into(circleImageView);
        txtUploadDate.setText(editRelatedMethod.getUploadDate());
    }


    /**
     To retrieve the date or time set in the pickers, implement an appropriate callback interface.
     */
    @Override
    public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
        Log.d("TimeDateTimeFirst", "1");
        Calendar cal = new java.util.GregorianCalendar();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        if (DateFormat.getDateInstance().format(cal.getTime()) != null){
            txtShowMeetUp.setText("Meet up on " + DateFormat.getDateInstance().format(cal.getTime()));
            hasDate = true;
        }
    }

    @Override
    public void onTimeSet(ViewGroup viewGroup, int hourOfDay, int minute) {
        Log.d("TimeDateDateFirst", "1");
        Calendar cal = new java.util.GregorianCalendar();
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        if (hasDate) {
            if (DateFormat.getTimeInstance().format(cal.getTime()) != null) {
                txtTimePick.setText(DateFormat.getTimeInstance().format(cal.getTime()));
                txtShowMeetUp.setVisibility(View.VISIBLE);
                txtTimePick.setVisibility(View.VISIBLE);
                hasTime = true;
            }
        }
    }

    /**
     * For saving meet up date and time
     */
    private void saveMeetDateTime(){
        if (hasTime) {
            editRelatedMethod.saveMeetUpDateTime(EditInitActivity.this, txtShowMeetUp, txtTimePick);
            Toast.makeText(this, "SaveDateTime", Toast.LENGTH_SHORT).show();
        }
    }
}
