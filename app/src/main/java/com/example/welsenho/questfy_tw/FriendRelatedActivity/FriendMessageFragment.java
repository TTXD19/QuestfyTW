package com.example.welsenho.questfy_tw.FriendRelatedActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.FriendMessagingRelated.FriendMessagingActivity;
import com.example.welsenho.questfy_tw.LoginRelated.LoginActivity;
import com.example.welsenho.questfy_tw.MainUserActivity.MainActivity;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class FriendMessageFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet getSet;
    private FriendMessageRecyclerAdapter adapter;

    private Button btnAddFriend;

    private View view;
    private TextView txtNoMessage;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public FriendMessageFragment() {
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
        view = inflater.inflate(R.layout.fragment_friend_message, container, false);
        InitItem();
        progressBar.setVisibility(View.VISIBLE);
        InitRecyclerView();
        InitFirebase();
        if (firebaseUser != null) {
            getFirebaseData();
        }else {
            progressBar.setVisibility(View.GONE);
            btnAddFriend.setVisibility(View.GONE);
            txtNoMessage.setVisibility(View.VISIBLE);
            txtNoMessage.setText("登入享有更多服務");
            txtNoMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.com_facebook_blue));
            txtNoMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    private void InitItem() {
        txtNoMessage = view.findViewById(R.id.friendMessagetxtMessages);
        recyclerView = view.findViewById(R.id.friendMessageRecyclerView);
        progressBar = view.findViewById(R.id.friendMessageProgressBar);
        btnAddFriend = view.findViewById(R.id.friendMessageBtnAddFriend);

        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchAndAddFriendActivity.class);
                startActivity(intent);
            }
        });

        arrayList = new ArrayList<>();
        adapter = new FriendMessageRecyclerAdapter(arrayList, getContext());
        adapter.setOnMainClickListener(new onMainFriendRequestClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Intent intent = new Intent(getContext(), FriendMessagingActivity.class);
                intent.putExtra("FriendUid", arrayList.get(position).getFriendUid());
                intent.putExtra("FriendName", arrayList.get(position).getFriendName());
                startActivity(intent);
            }

            @Override
            public void onCancelClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {

            }
        });
    }


    private void InitFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void InitRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
    }

    private void getFirebaseData() {
        databaseReference.child("UserFriendList").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    txtNoMessage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    arrayList.clear();
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        FirebaseDatabaseGetSet getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(getSet);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                        databaseReference.removeEventListener(this);
                    }
                } else {
                    txtNoMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
