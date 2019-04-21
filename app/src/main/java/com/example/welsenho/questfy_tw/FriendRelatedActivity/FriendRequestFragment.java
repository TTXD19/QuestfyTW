package com.example.welsenho.questfy_tw.FriendRelatedActivity;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class FriendRequestFragment extends Fragment {


    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet getSet;
    private FriendRequestRecyclerAdapter adapter;

    private View view;
    private TextView txtNoFreindsRequest;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private OnFragmentInteractionListener mListener;

    public FriendRequestFragment() {
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
        view = inflater.inflate(R.layout.fragment_friend_request, container, false);
        InitItem();
        InitRecyclerView();
        InitFirebase();
        if (firebaseUser != null) {
            getRequestFriendList();
        }else {
            progressBar.setVisibility(View.GONE);
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
        txtNoFreindsRequest = view.findViewById(R.id.friend_request_txtRequestFriends);
        recyclerView = view.findViewById(R.id.friend_request_recyclerView);
        progressBar = view.findViewById(R.id.friend_request_progressBar);
        arrayList = new ArrayList<>();
        adapter = new FriendRequestRecyclerAdapter(arrayList, getContext());
        adapter.setOnMainClickListener(new onMainFriendRequestClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("FriendUid", arrayList.get(position).getSenderUid());
                hashMap.put("FriendName", arrayList.get(position).getRequestName());
                hashMap.put("FriendImage", "null");

                HashMap<String, Object> hashMapFriend = new HashMap<>();
                hashMapFriend.put("FriendUid", firebaseUser.getUid());
                hashMapFriend.put("FriendName", firebaseUser.getDisplayName());
                hashMap.put("FriendImage", "null");
                databaseReference.child("UserFriendList").child(firebaseUser.getUid()).child(arrayList.get(position).getSenderUid()).updateChildren(hashMap);
                databaseReference.child("UserFriendList").child(arrayList.get(position).getSenderUid()).child(firebaseUser.getUid()).updateChildren(hashMapFriend);
                databaseReference.child("FriendAddingProcess").child(firebaseUser.getUid()).child(arrayList.get(position).getSenderUid()).removeValue();
                databaseReference.child("FriendAddingProcess").child(arrayList.get(position).getSenderUid()).child(firebaseUser.getUid()).removeValue();

            }

            @Override
            public void onCancelClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                databaseReference.child("FriendAddingProcess").child(firebaseUser.getUid()).child(arrayList.get(position).getSenderUid()).removeValue();
                databaseReference.child("FriendAddingProcess").child(arrayList.get(position).getSenderUid()).child(firebaseUser.getUid()).removeValue();
            }
        });
    }

    private void InitFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void getRequestFriendList() {
        databaseReference.child("FriendAddingProcess").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    txtNoFreindsRequest.setVisibility(View.GONE);
                    arrayList.clear();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        getSet = ds.getValue(FirebaseDatabaseGetSet.class);
                        if (!getSet.getSenderUid().equals(firebaseUser.getUid())) {
                            arrayList.add(getSet);
                        }
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    }
                }else {
                    txtNoFreindsRequest.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
    }
}
