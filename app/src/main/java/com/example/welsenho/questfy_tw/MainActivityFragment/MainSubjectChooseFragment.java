package com.example.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;


public class MainSubjectChooseFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View view;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<FirebaseDatabaseGetSet> arrayListHistory;
    private FirebaseDatabaseGetSet getSet;
    private FirebaseDatabaseGetSet getSetHistory;
    private RecyclerView recyclerViewCurrent;
    private RecyclerView recyclerViewHistory;
    private MainSubjectChooseFragmentRecyclerAdapter adapter;
    private MainSubjectChooseFragmentRecyclerAdapter adapterHistory;
    private EditRelatedMethod editRelatedMethod;


    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public MainSubjectChooseFragment() {
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
        view = inflater.inflate(R.layout.fragment_main_subject_choose, container, false);
        InitItemID();
        InitItem();
        InitRecyclerView(recyclerViewCurrent);
        InitRecyclerView(recyclerViewHistory);
        InitFirebase();
        if (firebaseUser != null) {
            getFirebaseHistorySearch();
        }
        onItemClick();
        getFirebaseData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainSubjectChooseFragment.OnFragmentInteractionListener) {
            mListener = (MainSubjectChooseFragment.OnFragmentInteractionListener) context;
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
        void onSubjectChooseFilter(String subject);
    }

    private void InitItemID(){
        recyclerViewCurrent = view.findViewById(R.id.MainSubjectChoose_recyclerView_currentItemClick);
        recyclerViewHistory = view.findViewById(R.id.MainSubjectChoose_recyclerView_history);
    }

    private void InitRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
    }

    private void InitFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void InitItem(){
        arrayList = new ArrayList<>();
        arrayListHistory = new ArrayList<>();
        adapter = new MainSubjectChooseFragmentRecyclerAdapter(arrayList, getContext());
        adapterHistory = new MainSubjectChooseFragmentRecyclerAdapter(arrayListHistory, getContext());
        editRelatedMethod = new EditRelatedMethod();
    }

    private void getFirebaseData(){
        databaseReference.child("Choose_majors_section").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    arrayList.clear();
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(getSet);
                        recyclerViewCurrent.setAdapter(adapter);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFirebaseHistorySearch(){
        databaseReference.child("UserSearchHistory").child(firebaseUser.getUid()).child("Choose_majors_section_history").orderByChild("writeTime").limitToFirst(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    arrayListHistory.clear();
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        getSetHistory = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayListHistory.add(getSetHistory);
                        recyclerViewHistory.setAdapter(adapterHistory);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onItemClick(){
        adapter.setOnClickListener(new MainOnClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                mListener.onSubjectChooseFilter(arrayList.get(position).getMajor());
                if (firebaseUser != null) {
                    writeHistory(arrayList.get(position).getMajor());
                }
            }
        });

        adapterHistory.setOnClickListener(new MainOnClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                mListener.onSubjectChooseFilter(arrayList.get(position).getMajor());
            }
        });
    }

    private void writeHistory(String subject){
        EditRelatedMethod editRelatedMethod = new EditRelatedMethod();
        if (!subject.equals("全部")) {
            HashMap<String, Object> hashMap = new HashMap<>();
            long writeTime = System.currentTimeMillis();
            writeTime *= -1;
            hashMap.put("writeTime", writeTime);
            hashMap.put("Major", subject);
            hashMap.put("DateSequence", editRelatedMethod.getDate());
            databaseReference.child("UserSearchHistory").child(firebaseUser.getUid()).child("Choose_majors_section_history").child(subject).updateChildren(hashMap);
        }
    }
}
