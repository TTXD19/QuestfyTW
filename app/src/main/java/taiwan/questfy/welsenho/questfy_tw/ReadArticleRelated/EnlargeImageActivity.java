package taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.EnlargeImagePageAdapter;
import taiwan.questfy.welsenho.questfy_tw.R;

public class EnlargeImageActivity extends AppCompatActivity {

    private String Article_ID;
    private int position;

    private Toolbar toolbar;
    private ViewPager viewPager;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private EnlargeImagePageAdapter pageAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarge_image);

        Article_ID = getIntent().getStringExtra("Article_ID");
        position = getIntent().getIntExtra("Position", 0);
        InitItem();
        InitFirebase();
        InitToolbar();
        getImageData();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void InitFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void InitItem(){
        toolbar = findViewById(R.id.enlarg_image_toolBar);
        viewPager = findViewById(R.id.enlarge_image_viewPager);
        arrayList = new ArrayList<>();
        pageAdapter = new EnlargeImagePageAdapter(arrayList, getApplicationContext());
    }

    private void InitToolbar(){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void getImageData(){
        databaseReference.child("Users_Question_Articles").child(Article_ID).child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.exists()) {
                    arrayList.clear();
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                        viewPager.setAdapter(pageAdapter);
                        viewPager.setCurrentItem(position);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
