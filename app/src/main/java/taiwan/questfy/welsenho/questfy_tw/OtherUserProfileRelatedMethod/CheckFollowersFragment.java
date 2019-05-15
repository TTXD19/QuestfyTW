package taiwan.questfy.welsenho.questfy_tw.OtherUserProfileRelatedMethod;

import android.content.Context;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.FriendRelatedActivity.SearchAndAddFriendRecyclerAdapter;
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.MainOnClickListener;
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.list_article_recyclerView_adapter;
import taiwan.questfy.welsenho.questfy_tw.R;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;


public class CheckFollowersFragment extends Fragment {

    private String otherUserUid;

    private View view;
    private OnCheckFollowersFragment mListener;
    private RecyclerView recyclerView;
    private TextView txtNoFollowers;
    private ProgressBar progressBar;

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet getSet;
    private CheckFollowerRecyclerAdapter adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public CheckFollowersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = mListener.getBundle();
        if (bundle != null) {
            otherUserUid = bundle.get("otherUserUid").toString();
        } else {
            Toast.makeText(getContext(), "Null", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_check_followers, container, false);
        InitFirebaseItem();
        InitItem();
        getUserArticlesData();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCheckFollowersFragment) {
            mListener = (OnCheckFollowersFragment) context;
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
    public interface OnCheckFollowersFragment {
        Bundle getBundle();
    }

    private void InitFirebaseItem() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void InitItem() {
        recyclerView = view.findViewById(R.id.checkFollower_recyclerView);
        txtNoFollowers = view.findViewById(R.id.checkFollower_txtNoFollowers);
        progressBar = view.findViewById(R.id.checkFollower_progressBar);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));

        arrayList = new ArrayList<>();
        adapter = new CheckFollowerRecyclerAdapter(arrayList, new CheckFollowerRecyclerAdapter.userClickListener() {
            @Override
            public void onUserClick(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Intent intent = new Intent(getContext(), OtherUserProfileActivity.class);
                intent.putExtra("otherUserUid", arrayList.get(position).getUserUid());
                startActivity(intent);
            }
        });
    }


    private void getUserArticlesData() {
        databaseReference.child("Users_Followers_Section").child(otherUserUid).child("Follow_by").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrayList.clear();
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(getSet);
                        recyclerView.setAdapter(adapter);
                    }
                    progressBar.setVisibility(View.GONE);
                    if (arrayList.isEmpty()) {
                        txtNoFollowers.setVisibility(View.VISIBLE);
                    } else {
                        txtNoFollowers.setVisibility(View.GONE);
                    }
                }else {
                    progressBar.setVisibility(View.GONE);
                    txtNoFollowers.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
