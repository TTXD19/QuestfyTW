package com.example.welsenho.questfy_tw.FriendRelatedActivity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.OtherUserProfileRelatedMethod.OtherUserProfileActivity;
import com.example.welsenho.questfy_tw.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAndAddFriendActivity extends AppCompatActivity{

    private ImageButton imgBtnSearch;
    private RecyclerView recyclerView;
    private EditText editFriends;
    private ProgressBar progressBar;
    private Toolbar toolbar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Query query;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private SearchAndAddFriendRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_and_add_friend);

        InitFirebase();
        InitItem();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        imgBtnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String search = editFriends.getText().toString();
                if (!search.isEmpty()) {
                    fetchData(search);
                }else{
                    arrayList.clear();
                    recyclerView.setAdapter(adapter);
                    Toast.makeText(SearchAndAddFriendActivity.this, "Search field can not be empty", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

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
        toolbar = findViewById(R.id.search_addFriend_toolBar);
        imgBtnSearch = findViewById(R.id.search_addFriend_imgSearch);
        recyclerView = findViewById(R.id.search_addFriend_recyclerView);
        editFriends = findViewById(R.id.search_addFriend_editSearchFriend);
        progressBar = findViewById(R.id.search_addFriend_progressBar);


        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arrayList = new ArrayList<>();
        adapter = new SearchAndAddFriendRecyclerAdapter(arrayList, new SearchAndAddFriendRecyclerAdapter.userClickListener() {
            @Override
            public void onUserClick(int position) {
                Intent intent = new Intent(SearchAndAddFriendActivity.this, OtherUserProfileActivity.class);
                intent.putExtra("otherUserUid", arrayList.get(position).getUserUid());
                startActivity(intent);
            }
        });
    }

    private void fetchData(String search){
        query = databaseReference.child("Users_profile").orderByChild("ID").startAt(search).endAt(search + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrayList.clear();
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    }
                } else {
                    arrayList.clear();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(SearchAndAddFriendActivity.this, getString(R.string.user_not_exist), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
