package com.example.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.LoginRelated.LoginActivity;
import com.example.welsenho.questfy_tw.OtherUserProfileRelatedMethod.OtherUserProfileActivity;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class UserFollowingInfoFragment extends Fragment {

    private View view;
    private TextView txtNoFollowingUsers;
    private RecyclerView recyclerView;
    private UserFollowingRecyclerAdapter adapter;

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;

    private OnFragmentInteractionListener mListener;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public UserFollowingInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_following_info, container, false);
        InitFirebase();
        InitItem();
        if (firebaseUser != null) {
            InitRecyclerView();
            getUserFollowingData();
        }else {
            recyclerView.setVisibility(View.GONE);
            txtNoFollowingUsers.setVisibility(View.VISIBLE);
            txtNoFollowingUsers.setText("登入享有更多服務");
            txtNoFollowingUsers.setTextColor(ContextCompat.getColor(getContext(), R.color.com_facebook_blue));
            txtNoFollowingUsers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void InitItem(){
        recyclerView = view.findViewById(R.id.user_following_info_recyclerView);
        txtNoFollowingUsers = view.findViewById(R.id.user_following_info_txtNoFollowingUsers);
        arrayList = new ArrayList<>();
        adapter = new UserFollowingRecyclerAdapter(arrayList, getContext(), new UserFollowingRecyclerAdapter.UserItemClick() {
            @Override
            public void getPosition(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                String userUid = arrayList.get(position).getUserUid();
                Intent intent = new Intent(getContext(), OtherUserProfileActivity.class);
                intent.putExtra("otherUserUid", userUid);
                startActivity(intent);
            }
        });
    }

    private void InitFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void getUserFollowingData(){
        databaseReference.child("Users_Followers_Section").child(firebaseUser.getUid()).child("FollowingInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        recyclerView.setVisibility(View.VISIBLE);
                        txtNoFollowingUsers.setVisibility(View.GONE);
                        firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                    }
                    recyclerView.setAdapter(adapter);
                }else {
                    recyclerView.setVisibility(View.GONE);
                    txtNoFollowingUsers.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setHasFixedSize(true);
    }
}
