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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.AnswerReplyActivityRelated.EnlargeAnswerImageActivity;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.EnlargeImageActivity;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;


public class AnsweredHitstoryFragment extends Fragment {

    private String otherUserUid;

    private OnAnsweredHistory mListener;
    private View view;
    private TextView txtNoAnswered;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerView;
    private AnsweredHistoryRecyclerAdapter adapter;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;

    public AnsweredHitstoryFragment() {
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
        view = inflater.inflate(R.layout.fragment_answered_hitstory, container, false);
        InitItem();
        getFirebaseData();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnAnsweredHistory) {
            mListener = (OnAnsweredHistory) context;
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

    private void InitItem(){
        txtNoAnswered = view.findViewById(R.id.answered_history_txtNoAnswered);
        recyclerView = view.findViewById(R.id.answered_history_recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        arrayList = new ArrayList<>();
        adapter = new AnsweredHistoryRecyclerAdapter(arrayList, getContext(), new AnsweredHistoryRecyclerAdapter.ItemClick() {
            @Override
            public void articleClick(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Intent intent = new Intent(getContext(), ReadArticleActivity.class);
                intent.putExtra("ArticleID", arrayList.get(position).getArticle_ID());
                startActivity(intent);
            }

            @Override
            public void ImageClick(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Intent intent = new Intent(getContext(), EnlargeAnswerImageActivity.class);
                intent.putExtra("imageUri", arrayList.get(position).getEditInitImageUploadViewUri());
                startActivity(intent);
            }
        });

    }

    private void getFirebaseData(){
            databaseReference.child("UserArticleAnswers").child(otherUserUid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot DS : dataSnapshot.getChildren()) {
                            firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                            arrayList.add(firebaseDatabaseGetSet);
                        }
                        txtNoAnswered.setVisibility(View.GONE);
                        recyclerView.setAdapter(adapter);
                    } else {
                        txtNoAnswered.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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
    public interface OnAnsweredHistory{
        Bundle getBundle();
    }
}
