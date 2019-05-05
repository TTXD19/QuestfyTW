package taiwan.questfy.welsenho.questfy_tw.EditActivityRelated;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.florent37.expansionpanel.ExpansionHeader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class EditPreviewActivity extends AppCompatActivity {

    private String Article_Uid;
    private boolean isMeet;
    private ArrayList<String> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private EditOfflineImageViewRecyclerAdapter adapterImageViews;

    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private CircleImageView circleImageView;
    private TextView txtUserName;
    private TextView txtUploadData;
    private TextView txtTitle;
    private TextView txtMajors;
    private TextView txtContent;
    private TextView txtCheckImages;
    private TextView txtMeetdate;
    private TextView txtMeetTime;
    private TextView txtMeetPlace;
    private TextView txtMeetAdress;
    private RecyclerView recyclerView;
    private ExpansionHeader expansionHeader;


    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_preview);
        InitItem();
        InitRecycleView();
        InitFirebase();
        showCurrentContent();
        if (getIntent().getStringArrayListExtra("ImageList") != null){
            arrayList = getIntent().getStringArrayListExtra("ImageList");
            showImages();
        }
    }

    @Override
    public void onBackPressed() {
        removerCurrentPreview();
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void showCurrentContent(){
        sharedPreferences = this.getSharedPreferences("Preview", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        txtUserName.setText(sharedPreferences.getString("EditUserName", "User name"));
        txtUploadData.setText(sharedPreferences.getString("EditUploadDate", "Upload date"));
        txtTitle.setText(sharedPreferences.getString("EditTitle", "Enter title"));
        txtContent.setText(sharedPreferences.getString("EditContent", "Enter question"));
        txtMajors.setText(sharedPreferences.getString("EditMajor", getString(R.string.click_to_select_a_major_related)));
        Article_Uid = sharedPreferences.getString("EditArticleUid", null);
        Picasso.get().load(sharedPreferences.getString("UserImageUri", "")).fit().into(circleImageView);
        isMeet = sharedPreferences.getBoolean("MeetUp", false);

        if (isMeet){
            txtMeetdate.setText(sharedPreferences.getString("EditMeetDate", "Choose a date"));
            txtMeetTime.setText(sharedPreferences.getString("EditMeetTime", "Choose a time"));
            txtMeetPlace.setText(sharedPreferences.getString("EditMeetPlace", "Choose a place"));
            txtMeetAdress.setText(sharedPreferences.getString("EditMeetAddress", "Choose a address"));
        }else {
            txtMeetdate.setVisibility(View.GONE);
            txtMeetTime.setVisibility(View.GONE);
            txtMeetPlace.setVisibility(View.GONE);
            txtMeetAdress.setVisibility(View.GONE);
        }
    }

    private void removerCurrentPreview(){
        editor.remove("EditUserName");
        editor.remove("EditUploadDate");
        editor.remove("EditTitle");
        editor.remove("EditContent");
        editor.remove("EditMajor");
        editor.remove("EditArticleUid");
        editor.remove("MeetUp");
        editor.remove("EditMeetDate");
        editor.remove("EditMeetTime");
        editor.remove("EditMeetPlace");
        editor.remove("EditMeetAddress");
        editor.remove("UserImageUri");
    }

    private void InitItem(){
        toolbar = findViewById(R.id.edit_preview_toolbar);
        txtTitle = findViewById(R.id.edit_preview_txt_title);
        txtUserName = findViewById(R.id.edit_preview_txt_userName);
        txtUploadData = findViewById(R.id.edit_preview_txt_UploadDate);
        txtMajors = findViewById(R.id.edit_preview_txt_major);
        txtContent = findViewById(R.id.edit_preview_txt_content);
        txtMeetPlace = findViewById(R.id.edit_preview_txt_meetUpPlace);
        txtMeetAdress = findViewById(R.id.edit_preview_txt_meetUpAddress);
        txtMeetdate = findViewById(R.id.edit_preview_txt_Date);
        txtMeetTime = findViewById(R.id.edit_preview_txt_Time);
        txtCheckImages = findViewById(R.id.edit_preview_txt_CheckimageView);
        recyclerView = findViewById(R.id.edit_preview_recyclerView);
        circleImageView = findViewById(R.id.edit_preview_circle_image_user);
        expansionHeader = findViewById(R.id.edit_preview_expan_header);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        arrayList = new ArrayList<>();
    }

    private void InitFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void InitRecycleView(){
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setHasFixedSize(true);
    }

    private void showImages(){
        if (arrayList != null){
            adapterImageViews = new EditOfflineImageViewRecyclerAdapter(arrayList);
            recyclerView.setAdapter(adapterImageViews);
        }
    }
}
