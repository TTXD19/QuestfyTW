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
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.MainOnClickListener;
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.list_article_recyclerView_adapter;
import taiwan.questfy.welsenho.questfy_tw.R;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;


public class UserAskedQuestionsFragment extends Fragment {

    private String SelfUid;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet getSet;
    private list_article_recyclerView_adapter adapter;
    private String otherUserUid;


    private OnFragmentInteractionListener mListener;
    private View view;
    private RecyclerView recyclerView;
    private TextView txtNoPost;
    private ProgressBar progressBar;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    public UserAskedQuestionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = mListener.getBundle();
        if (bundle != null){
            otherUserUid = bundle.get("otherUserUid").toString();
        }else {
            Toast.makeText(getContext(), "Null", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_asked_questions, container, false);
        InitFirebaseItem();
        InitItem();
        getUserArticlesData();
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
        void onFragmentInteraction(String postCount);
        Bundle getBundle();
    }

    private void InitFirebaseItem() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference();
        SelfUid = firebaseAuth.getUid();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    private void InitItem() {
        recyclerView = view.findViewById(R.id.otherUser_profile_recyclerView);
        txtNoPost = view.findViewById(R.id.otherUser_profile_txtNoPost);
        progressBar = view.findViewById(R.id.userAsked_questions_progressBat);


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));

        arrayList = new ArrayList<>();
        adapter = new list_article_recyclerView_adapter(arrayList, getContext());
        adapter.setOnMainClickListener(new MainOnClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Intent intent = new Intent(getContext(), ReadArticleActivity.class);
                intent.putExtra("ArticleID", arrayList.get(position).getArticle_ID());
                startActivity(intent);
            }
        });
    }


    private void getUserArticlesData() {
        databaseReference.child("Users_Question_Articles").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrayList.clear();
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        if (getSet.getUserUid().equals(otherUserUid)) {
                            arrayList.add(getSet);
                            mListener.onFragmentInteraction(String.valueOf(arrayList.size()));
                            recyclerView.setAdapter(adapter);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    if (arrayList.isEmpty()) {
                        txtNoPost.setVisibility(View.VISIBLE);
                    } else {
                        txtNoPost.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
