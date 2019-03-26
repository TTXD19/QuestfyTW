package com.example.welsenho.questfy_tw.EditActivityRelated;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.example.welsenho.questfy_tw.MainActivityFragment.MainOnClickListener;
import com.example.welsenho.questfy_tw.MainUserActivity.MainActivity;
import com.example.welsenho.questfy_tw.R;
import com.github.florent37.expansionpanel.ExpansionHeader;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
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
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class EditInitActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, BottomSheetTimePickerDialog.OnTimeSetListener{

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_SELECT = 2;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 3;
    private static final String TAG = "tag";
    private static final String PLACE_API_KEY = "AIzaSyByDLGHKUl2OutFByrDy0FgPUGU1FywsOg";

    private String getDownloadUri;
    private String articleUid;
    private boolean hasDate = false;
    private boolean hasTime = false;
    private boolean isMeet = false;
    private boolean destoryByUser = true;
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

        LoadUserProfile();

        //Relate to MajorChoose BroadCast Intent
        if (getIntent().getStringArrayListExtra("getMajors") != null) {
            getMajors.clear();
            getMajors = getIntent().getStringArrayListExtra("getMajors");
            String major = getMajors.toString();
            txtChooseMajor.setText(major);
            showCurrentContent();
            destoryByUser = true;
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

    private void UploadFirebaseDatabase() {
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();
        HashMap<String, Object> hashMap = new HashMap<>();
        if (!getMajors.isEmpty() && !title.isEmpty() && !content.isEmpty()) {
            hashMap.put("User_Name", txtUserName.getText().toString());
            hashMap.put("Upload_Date", txtUploadDate.getText().toString());
            hashMap.put("Title", title);
            hashMap.put("Majors", getMajors.toString());
            hashMap.put("Content", content);
            hashMap.put("Article_ID", articleUid);
            hashMap.put("userUid", firebaseAuth.getUid());
            hashMap.put("Article_like_count", 0);
            if (firebaseUser.getPhotoUrl() != null) {
                hashMap.put("User_Image", firebaseUser.getPhotoUrl().toString());
            } else {
                String default_Image = "https://firebasestorage.googleapis.com/v0/b/questfytw.appspot.com/o/Default_Image_ForEach_Condition%2Fuser%20(1).png?alt=media&token=5122a33f-5392-4877-be3d-4f519550c9b6";
                hashMap.put("User_Image", default_Image);
            }
            if (!isMeet) {
                databaseReference.child("Users_Question_Articles").child(articleUid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
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
                if (!txtPlaceName.getText().toString().equals(getString(R.string.meet_up_place)) && !txtTimePick.getText().toString().equals(getString(R.string.set_up_time)) && !txtDatePick.getText().toString().equals(getString(R.string.date))) {
                    hashMap.put("MeetDate", txtDatePick.getText().toString());
                    hashMap.put("MeetTime", txtTimePick.getText().toString());
                    hashMap.put("MeetPlace", txtPlaceName.getText().toString());
                    hashMap.put("MeetAddress", txtPlaceAddress.getText().toString());

                    databaseReference.child("Users_Question_Articles").child(articleUid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
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
                    Toast.makeText(this, "Meet up detail can not be empty", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(this, "Something is empty", Toast.LENGTH_SHORT).show();
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
                        adapterImageViews.setOnMainAdapterClickListner(new MainOnClickListener() {
                            @Override
                            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                                Toast.makeText(EditInitActivity.this, "not set up", Toast.LENGTH_SHORT).show();
                            }
                        });
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
                startActivity(intent);
                SaveCurrentContent();
                finish();
            }
        });

        imgTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveCurrentContent();
                dispatchTakePictureIntent();
            }
        });

        imgChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveCurrentContent();
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
        editor.putString("EditTitle", editTitle.getText().toString());
        editor.putString("EditContent", editContent.getText().toString());
        editor.putString("EditArticleUid", articleUid);
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

    private void showCurrentContent(){
        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editTitle.setText(sharedPreferences.getString("EditTitle", "Enter title"));
        editContent.setText(sharedPreferences.getString("EditContent", "Enter question"));
        articleUid = sharedPreferences.getString("EditArticleUid", null);
        countImages = sharedPreferences.getInt("CounImages", 0);
        isMeet = sharedPreferences.getBoolean("MeetUp", false);
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
        editor.apply();
    }

    private void buildDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Error").setMessage("Sorry, we're having a server issue. Please try again later")
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
}
