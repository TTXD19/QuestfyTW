package com.example.welsenho.questfy_tw.ReadArticleRelated;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditInitRelateRecyclerViewAdapterImageViews;
import com.example.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.github.florent37.expansionpanel.ExpansionHeader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReadArticleActivity extends AppCompatActivity {

    private String Article_ID;

    private CircleImageView circleImageView;
    private TextView txtUserName;
    private TextView txtUploadData;
    private TextView txtTitle;
    private TextView txtMajors;
    private TextView txtContent;
    private TextView txtCheckImages;
    private Toolbar toolbar;
    private ExpansionHeader expansionHeader;
    private RecyclerView recyclerView;
    private EditInitRelateRecyclerViewAdapterImageViews adapterImageViews;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private EditRelatedMethod editRelatedMethod;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabas;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);

        Article_ID = getIntent().getStringExtra("ArticleID");
        InitializeItem();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        InitializeFirebase();
        getContentData();

        /**
         * Initialize Image RecyclerView
         */
        editRelatedMethod = new EditRelatedMethod();
        arrayList = new ArrayList<>();
        adapterImageViews = new EditInitRelateRecyclerViewAdapterImageViews(arrayList);
        LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        recyclerView.setHasFixedSize(true);
        editRelatedMethod.LoadImageFromFirebase(databaseReference, Article_ID, arrayList, expansionHeader, recyclerView, adapterImageViews, txtCheckImages);




    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }






    private void InitializeItem(){
        circleImageView = findViewById(R.id.read_article_circle_image_user);
        txtUserName = findViewById(R.id.read_article_txt_userName);
        txtTitle = findViewById(R.id.read_article_txt_title);
        txtMajors = findViewById(R.id.read_article_txt_major);
        txtContent = findViewById(R.id.read_article_txt_content);
        txtUploadData = findViewById(R.id.read_article_txt_UploadDate);
        txtCheckImages = findViewById(R.id.read_article_txt_CheckimageView);
        expansionHeader = findViewById(R.id.read_article_expan_header);
        toolbar = findViewById(R.id.read_article_toolbar);
        recyclerView = findViewById(R.id.read_article_recyclerView);
    }


    private void InitializeFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabas = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabas.getReference();
    }

    private void getContentData(){

        databaseReference.child("Users_Question_Articles").child(Article_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    FirebaseDatabaseGetSet firebaseDatabaseGetSet = dataSnapshot.getValue(FirebaseDatabaseGetSet.class);
                    txtUserName.setText(firebaseDatabaseGetSet.getUser_Name());
                    txtUploadData.setText(firebaseDatabaseGetSet.getUpload_Date());
                    txtMajors.setText(firebaseDatabaseGetSet.getMajors());
                    txtTitle.setText(firebaseDatabaseGetSet.getTitle());
                    txtContent.setText(firebaseDatabaseGetSet.getContent());
                    Picasso.get().load(firebaseDatabaseGetSet.getUser_Image()).into(circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getImageData(){

    }


}
