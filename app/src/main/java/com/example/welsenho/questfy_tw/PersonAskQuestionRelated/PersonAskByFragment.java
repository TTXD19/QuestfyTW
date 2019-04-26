package com.example.welsenho.questfy_tw.PersonAskQuestionRelated;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.LoginRelated.LoginActivity;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PersonAskByFragment extends Fragment {

    private View view;
    private TextView txtNoQuestion;
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private PersonalAskRecyclerAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public PersonAskByFragment() {
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
        view = inflater.inflate(R.layout.fragment_person_ask_by, container, false);
        InitItem();
        InitRecyclerView();
        InitFirebase();
        if (firebaseUser == null){
            showDefaultPage();
            txtNoQuestion.setVisibility(View.VISIBLE);
            txtNoQuestion.setText("登入享有更多服務");
            txtNoQuestion.setTextColor(ContextCompat.getColor(getContext(), R.color.com_facebook_blue));
            txtNoQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }else {
            getData();
        }
        return view;
    }

    private void showDefaultPage() {
        recyclerView.setVisibility(View.GONE);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void InitItem(){
        recyclerView = view.findViewById(R.id.personal_ask_by_recyclerView);
        txtNoQuestion = view.findViewById(R.id.personal_ask_by_txtNoPostArticles);
        arrayList = new ArrayList<>();
        adapter = new PersonalAskRecyclerAdapter(arrayList, getContext(), new PersonalAskRecyclerAdapter.getQuestionUid() {
            @Override
            public void QuestionClick(int position) {
                String questionUid = arrayList.get(position).getAskQuesitonUid();
                Intent intent = new Intent(getContext(), PersonalAskQuestReplyActivity.class);
                intent.putExtra("questionUid", questionUid);
                intent.putExtra("questioType", "AskBy");
                startActivity(intent);
            }
        });
    }

    private void InitRecyclerView(){
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
    }

    private void InitFirebase(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void getData(){
        databaseReference.child("Personal_Ask_Question").child(firebaseUser.getUid()).child("AskedBy").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    arrayList.clear();
                    recyclerView.setVisibility(View.VISIBLE);
                    txtNoQuestion.setVisibility(View.GONE);
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        FirebaseDatabaseGetSet getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(getSet);
                        recyclerView.setAdapter(adapter);
                    }
                }else {
                    recyclerView.setVisibility(View.GONE);
                    txtNoQuestion.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
