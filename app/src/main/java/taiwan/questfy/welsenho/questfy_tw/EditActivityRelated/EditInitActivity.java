package taiwan.questfy.welsenho.questfy_tw.EditActivityRelated;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


import com.github.florent37.expansionpanel.ExpansionHeader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.philliphsu.bottomsheetpickers.date.DatePickerDialog;
import com.philliphsu.bottomsheetpickers.time.BottomSheetTimePickerDialog;
import com.philliphsu.bottomsheetpickers.time.numberpad.NumberPadTimePickerDialog;
import com.squareup.picasso.Picasso;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.AnswerReplyActivityRelated.AnswerReplyActivity;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.MainOnClickListener;
import taiwan.questfy.welsenho.questfy_tw.MainUserActivity.MainActivity;
import taiwan.questfy.welsenho.questfy_tw.R;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.EnlargeImageActivity;

public class EditInitActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, BottomSheetTimePickerDialog.OnTimeSetListener{

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 3;
    private static final int PERMISSION_REQUEST_CODE = 4;
    private static final String TAG = "tag";
    private static final String PLACE_API_KEY = "AIzaSyByDLGHKUl2OutFByrDy0FgPUGU1FywsOg";

    private String getDownloadUri;
    private String articleUid;
    private boolean hasDate = false;
    private boolean hasTime = false;
    private boolean isMeet = false;
    private boolean destoryByUser = true;
    private boolean firstEdit = true;
    private int countImages;
    private SharedPreferences sharedPreferences;


    private Toolbar toolbar;
    private TextView txtChooseMajor;
    private TextView txtUserName;
    private TextView txtUploadDate;
    private TextView txtImageView;
    private TextView txtDatePick;
    private TextView txtTimePick;
    private TextView txtPlaceName;
    private TextView txtPlaceAddress;
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
    private ArrayList<String> imageList;
    private ArrayList<String> getMajors;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ExpansionHeader expansionHeader;

    private EditRelatedMethod editRelatedMethod;
    private EditOfflineImageViewRecyclerAdapter offlineImageViewRecyclerAdapter;
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

        InitItem();
        IniteRecyclerView();
        LoadUserProfile();

        //Relate to MajorChoose BroadCast Intent
        if (!checkFirstEdit()) {
            if (getIntent().getStringArrayListExtra("getMajors") != null) {
                getMajors.clear();
                getMajors = getIntent().getStringArrayListExtra("getMajors");
                if (!getMajors.isEmpty()) {
                    String major = getMajors.toString();
                    txtChooseMajor.setText(major);
                }
                showCurrentContent();
                destoryByUser = true;
            }else {
                showCurrentContent();
                destoryByUser = true;
            }
        }

        if (getIntent().getStringArrayListExtra("ImageList") != null){
            if (getIntent().getStringArrayListExtra("ImageList").size() != 0) {
                imageList = getIntent().getStringArrayListExtra("ImageList");
                expansionHeader.setVisibility(View.VISIBLE);
                offlineImageViewRecyclerAdapter = new EditOfflineImageViewRecyclerAdapter(imageList, new EditOfflineImageViewRecyclerAdapter.ImageClick() {
                    @Override
                    public void onRemoverImage(final ArrayList<String> arrayList, final int position) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(EditInitActivity.this);
                        builder.setTitle("移除照片").setMessage("點擊確定將移除照片").setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                arrayList.remove(position);
                                offlineImageViewRecyclerAdapter.notifyItemRemoved(position);
                                if (arrayList.isEmpty()){
                                    expansionHeader.setVisibility(View.GONE);
                                }else {
                                    expansionHeader.setVisibility(View.VISIBLE);
                                }
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                    }
                });
                recyclerView.setAdapter(offlineImageViewRecyclerAdapter);
            }else {
                expansionHeader.setVisibility(View.GONE);
            }
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

        adapterImageViews = new EditInitRelateRecyclerViewAdapterImageViews(arrayList);
        adapterImageViews.setOnMainAdapterClickListner(new MainOnClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Intent intent = new Intent(EditInitActivity.this, EnlargeImageActivity.class);
                intent.putExtra("Article_ID", articleUid);
                intent.putExtra("Position", position);
                startActivity(intent);
            }
        });

        /**
         * Handle each item click
         */
        ItemClick();
        InitGooglePlaceApi();
    }

    @Override
    public boolean onSupportNavigateUp() {
        databaseReference.child("Users_Question_Articles").child(articleUid).removeValue();
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DeleteCurrent();
        databaseReference.child("Users_Question_Articles").child(articleUid).removeValue();
        Intent intent = new Intent(EditInitActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (destoryByUser){
            DeleteCurrent();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_init_activity_next, menu);
        MenuItem menuItem = menu.findItem(R.id.EditInitItemNext);
        SpannableString title = new SpannableString(menuItem.getTitle());
        title.setSpan(new TextAppearanceSpan(this, R.style.EditInitToolBarTextApperance), 0, title.length(), 0);
        menuItem.setTitle(title);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.EditInitItemNext:
                UploadFirebaseDatabase();
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
        } else if (requestCode == AUTOCOMPLETE_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Place place = Autocomplete.getPlaceFromIntent(data);
                txtPlaceName.setText(place.getName());
                txtPlaceAddress.setText(place.getAddress());

            }else if (resultCode == AutocompleteActivity.RESULT_ERROR){
                Toast.makeText(this, "Internet not connect", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
                    SaveCurrentContent();
                    dispatchTakePictureIntent();
                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "權限不允許", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("您需允許照相機權限，以啟用這項功能",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void InitItem(){
        toolbar = findViewById(R.id.edit_init_toolbar);
        txtChooseMajor = findViewById(R.id.edit_init_txt_chooseMajor);
        txtUserName = findViewById(R.id.edit_init_txt_userName);
        txtUploadDate = findViewById(R.id.edit_init_txt_UploadDate);
        txtImageView = findViewById(R.id.edit_init_txt_imageView);
        txtDatePick = findViewById(R.id.edit_init_txt_DatePick);
        txtTimePick = findViewById(R.id.edit_init_txt_TimePick);
        txtPlaceName = findViewById(R.id.edit_init_txt_PlaceNamePick);
        txtPlaceAddress = findViewById(R.id.edit_init_txt_PlaceAddressPick);
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

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageList = new ArrayList<>();
        arrayList = new ArrayList<>();
    }

    private void IniteRecyclerView(){
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setHasFixedSize(true);
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
                        "taiwan.questfy.welsenho.provider",
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

    private void UploadFirebaseDatabase() {
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();
        long timeStamp = System.currentTimeMillis();
        timeStamp *= -1;
        String meet = "NoMeet";
        HashMap<String, Object> hashMap = new HashMap<>();
        if (!getMajors.isEmpty() && !title.isEmpty() && !content.isEmpty()) {
            hashMap.put("User_Name", txtUserName.getText().toString());
            hashMap.put("Upload_Date", txtUploadDate.getText().toString());
            hashMap.put("Title", title);
            String majors = getMajors.toString();
            majors = majors.substring(1, majors.length() - 1);
            hashMap.put("Majors", majors);
            hashMap.put("Content", content);
            hashMap.put("Article_ID", articleUid);
            hashMap.put("illegalArticle", "False");
            hashMap.put("userUid", firebaseAuth.getUid());
            hashMap.put("Article_like_count", 0);
            hashMap.put("uploadTimeStamp", timeStamp);
            hashMap.put("MostPopCount", timeStamp);
            if (firebaseUser.getPhotoUrl() != null) {
                hashMap.put("User_Image", firebaseUser.getPhotoUrl().toString());
            } else {
                String default_Image = "https://firebasestorage.googleapis.com/v0/b/questfytw.appspot.com/o/Default_Image_ForEach_Condition%2Fuser%20(1).png?alt=media&token=5122a33f-5392-4877-be3d-4f519550c9b6";
                hashMap.put("User_Image", default_Image);
            }
            if (!isMeet) {
                hashMap.put("isMeet", meet);
                databaseReference.child("Users_Question_Articles").child(articleUid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            UploadImage();
                            Toast.makeText(EditInitActivity.this, "Upload success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(EditInitActivity.this, MainActivity.class);
                            startActivity(intent);
                            DeleteCurrent();
                            finish();
                        }else {
                            buildDialog();
                        }
                    }
                });
            } else {
                if (!txtPlaceName.getText().toString().equals(getString(R.string.meet_up_place)) && !txtTimePick.getText().toString().equals(getString(R.string.set_up_time)) && !txtDatePick.getText().toString().equals(getString(R.string.date))) {
                    meet = "Meet";
                    hashMap.put("isMeet", meet);
                    hashMap.put("MeetDate", txtDatePick.getText().toString());
                    hashMap.put("MeetTime", txtTimePick.getText().toString());
                    hashMap.put("MeetPlace", txtPlaceName.getText().toString());
                    hashMap.put("MeetAddress", txtPlaceAddress.getText().toString());

                    databaseReference.child("Users_Question_Articles").child(articleUid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                UploadImage();
                                Toast.makeText(EditInitActivity.this, "Upload success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(EditInitActivity.this, MainActivity.class);
                                startActivity(intent);
                                DeleteCurrent();
                            }else {
                                buildDialog();
                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "詳細見面內容需要填寫喔", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "標題、內容、相關科系不能空白喔 : )", Toast.LENGTH_SHORT).show();
        }

    }

    private void UploadImage(){
        if (imageList != null) {
            for (int i = 0; i <= imageList.size() - 1; i++) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("editInitImageUploadViewUri", imageList.get(i));
                databaseReference.child("Users_Question_Articles").child(articleUid).child("Images").child("Image" + String.valueOf(i)).updateChildren(hashMap);
            }
        }
    }

    private void UploadPhotoFirebase(final FirebaseUser firebaseUser, final StorageReference storageReference, final Uri uri) {

        progressDialog.show();
        progressDialog.setMessage("圖片上傳中....");
        progressDialog.setCanceledOnTouchOutside(false);
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
                            addOffLineImageLink(String.valueOf(uri));
                            //addImageLink(uri);
                            progressDialog.dismiss();
                        }
                    });
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                //System.out.println("Upload is " + progress + "% done");
                progressDialog.setProgress((int) progress);
                progressDialog.setMessage("上傳進度" + (int)progress + "%");
                Log.d("TotalByte", String.valueOf(progress));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditInitActivity.this, "上傳失敗，請在試一次", Toast.LENGTH_SHORT).show();
            }
        });;
    }

    private void addOffLineImageLink(String uri){
        imageList.add(uri);
        expansionHeader.setVisibility(View.VISIBLE);
        offlineImageViewRecyclerAdapter = new EditOfflineImageViewRecyclerAdapter(imageList, new EditOfflineImageViewRecyclerAdapter.ImageClick() {
            @Override
            public void onRemoverImage(final ArrayList<String> arrayList, final int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditInitActivity.this);
                builder.setTitle("移除照片").setMessage("點擊確定將移除照片").setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        arrayList.remove(position);
                        offlineImageViewRecyclerAdapter.notifyItemRemoved(position);
                        if (arrayList.isEmpty()){
                            expansionHeader.setVisibility(View.GONE);
                        }else {
                            expansionHeader.setVisibility(View.VISIBLE);
                        }
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
            }
        });
        recyclerView.setAdapter(offlineImageViewRecyclerAdapter);
    }

    /**
     * HANDLE EACH BUTTON AND TEXTVIEW CLICK
     * Also save current information
     */
    private void ItemClick() {
        txtChooseMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                destoryByUser = false;
                Intent intent = new Intent(EditInitActivity.this, EditQuestionRelateMajorChose.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (!getMajors.isEmpty()) {
                    intent.putStringArrayListExtra("Majors", getMajors);
                }
                if (imageList != null){
                    intent.putStringArrayListExtra("ImageList", imageList);
                }
                startActivity(intent);
                SaveCurrentContent();
                finish();
            }
        });

        imgTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (checkPermission()){
                        SaveCurrentContent();
                        dispatchTakePictureIntent();
                    }else {
                        requestPermission();
                    }
                }

            }
        });

        imgChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveCurrentContent();
                selectPhotoFromMedia();
            }
        });

        imgPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveCurrentContent();
                SaveCurrentContentForPreview();
                Intent intent = new Intent(EditInitActivity.this, EditPreviewActivity.class);
                if (!imageList.isEmpty()){
                    intent.putStringArrayListExtra("ImageList", imageList);
                }
                startActivity(intent);
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

        txtDatePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgDateTimePicker.performClick();
            }
        });

        swithMeetUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    isMeet = true;
                    imgDateTimePicker.setVisibility(View.VISIBLE);
                    imgPlcePicker.setVisibility(View.VISIBLE);
                    txtDatePick.setVisibility(View.VISIBLE);
                    txtTimePick.setVisibility(View.VISIBLE);
                    txtPlaceName.setVisibility(View.VISIBLE);
                    txtPlaceAddress.setVisibility(View.VISIBLE);
                }else {
                    isMeet = false;
                    imgDateTimePicker.setVisibility(View.GONE);
                    imgPlcePicker.setVisibility(View.GONE);
                    txtDatePick.setVisibility(View.GONE);
                    txtTimePick.setVisibility(View.GONE);
                    txtPlaceName.setVisibility(View.GONE);
                    txtPlaceAddress.setVisibility(View.GONE);
                }
            }
        });

        imgPlcePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveCurrentContent();
                GoogleSearchPlace();
            }
        });

        txtPlaceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPlcePicker.performClick();
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
            txtDatePick.setText(DateFormat.getDateInstance().format(cal.getTime()));
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
                txtDatePick.setVisibility(View.VISIBLE);
                txtTimePick.setVisibility(View.VISIBLE);
                hasTime = true;
            }
        }
    }

    private void SaveCurrentContent(){
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        firstEdit = false;
        editor.putString("EditTitle", editTitle.getText().toString());
        editor.putString("EditContent", editContent.getText().toString());
        editor.putString("EditArticleUid", articleUid);
        editor.putInt("CounImages", countImages);
        editor.putBoolean("MeetUp", isMeet);
        editor.putBoolean("firstEdit", firstEdit);
        if (isMeet){
            editor.putString("EditMeetDate", txtDatePick.getText().toString());
            editor.putString("EditMeetTime", txtTimePick.getText().toString());
            editor.putString("EditMeetPlace", txtPlaceName.getText().toString());
            editor.putString("EditMeetAddress", txtPlaceAddress.getText().toString());
        }
        editor.apply();
    }

    private void showCurrentContent(){
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editTitle.setText(sharedPreferences.getString("EditTitle", "Enter title"));
        editContent.setText(sharedPreferences.getString("EditContent", "Enter question"));
        articleUid = sharedPreferences.getString("EditArticleUid", null);
        countImages = sharedPreferences.getInt("CounImages", 0);
        isMeet = sharedPreferences.getBoolean("MeetUp", false);
        firstEdit = sharedPreferences.getBoolean("firstEdit", false);
        if (isMeet){
            swithMeetUp.setChecked(true);
            txtDatePick.setText(sharedPreferences.getString("EditMeetDate", "Choose a date"));
            txtTimePick.setText(sharedPreferences.getString("EditMeetTime", "Choose a time"));
            txtPlaceName.setText(sharedPreferences.getString("EditMeetPlace", "Choose a place"));
            txtPlaceAddress.setText(sharedPreferences.getString("EditMeetAddress", "Choose a address"));
            imgDateTimePicker.setVisibility(View.VISIBLE);
            imgPlcePicker.setVisibility(View.VISIBLE);
            txtDatePick.setVisibility(View.VISIBLE);
            txtTimePick.setVisibility(View.VISIBLE);
            txtPlaceName.setVisibility(View.VISIBLE);
            txtPlaceAddress.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkFirstEdit(){
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        firstEdit = sharedPreferences.getBoolean("firstEdit", true);
        return firstEdit;
    }

    private void SaveCurrentContentForPreview(){
        SharedPreferences preferences;
        preferences = this.getSharedPreferences("Preview", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        if (editTitle.getText().toString().isEmpty()){
            editor.putString("EditTitle", getString(R.string.question_title));
        }else {
            editor.putString("EditTitle", editTitle.getText().toString());
        }
        if (editContent.getText().toString().isEmpty()){
            editor.putString("EditContent", getString(R.string.question_content));
        }else {
            editor.putString("EditContent", editContent.getText().toString());
        }
        editor.putString("EditMajor", txtChooseMajor.getText().toString());
        editor.putString("EditArticleUid", articleUid);
        editor.putString("EditUserName", txtUserName.getText().toString());
        editor.putString("EditUploadDate", txtUploadDate.getText().toString());
        editor.putString("UserImageUri", firebaseUser.getPhotoUrl().toString());
        editor.putInt("CounImages", countImages);
        editor.putBoolean("MeetUp", isMeet);
        if (isMeet){
            editor.putString("EditMeetDate", txtDatePick.getText().toString());
            editor.putString("EditMeetTime", txtTimePick.getText().toString());
            editor.putString("EditMeetPlace", txtPlaceName.getText().toString());
            editor.putString("EditMeetAddress", txtPlaceAddress.getText().toString());
        }
        editor.apply();
    }

    public void DeleteCurrent(){
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("EditTitle");
        editor.remove("EditContent");
        editor.remove("EditArticleUid");
        editor.remove("CounImages");
        editor.remove("MeetUp");
        editor.remove("EditMeetDate");
        editor.remove("EditMeetTime");
        editor.remove("EditMeetPlace");
        editor.remove("EditMeetAddress");
        editor.remove("saveList");
        editor.remove("firstEdit");
        editor.apply();
    }

    private void buildDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Error").setMessage("抱歉，目前網路有問題請稍後再試")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void InitGooglePlaceApi(){
        // Initialize Places.
        Places.initialize(getApplicationContext(), PLACE_API_KEY);
        // Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);
    }

    private void GoogleSearchPlace(){
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(EditInitActivity.this)
                .setMessage(message)
                .setPositiveButton(R.string.confirm, okListener)
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }
}
