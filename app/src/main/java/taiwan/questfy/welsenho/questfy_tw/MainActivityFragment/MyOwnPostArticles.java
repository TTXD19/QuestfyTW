package taiwan.questfy.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.LoginRelated.LoginActivity;
import taiwan.questfy.welsenho.questfy_tw.R;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;


public class MyOwnPostArticles extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView txtNoPostArticles;
    private ImageView imgNoLogin;
    private ImageView imgNoPost;
    private View view;
    private SwipeRefreshLayout swipeRefreshLayout;

    private list_article_recyclerView_adapter adapter;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet getSet;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public MyOwnPostArticles() {
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
        view = inflater.inflate(R.layout.fragment_my_own_post_articles, container, false);
        InitItem();
        InitFirebase();
        if (firebaseUser != null) {
            setRecyclerView();
            LoadData();
            onItemClick();
        } else {
            progressBar.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            txtNoPostArticles.setVisibility(View.VISIBLE);
            txtNoPostArticles.setText("登入享有更多服務");
            txtNoPostArticles.setTextColor(ContextCompat.getColor(getContext(), R.color.com_facebook_blue));
            txtNoPostArticles.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });
            imgNoPost.setVisibility(View.GONE);
            imgNoLogin.setVisibility(View.VISIBLE);
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

    private void InitFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void InitItem() {
        recyclerView = view.findViewById(R.id.my_own_postArticle_recyclerView);
        progressBar = view.findViewById(R.id.my_own_postArticle_progressBar);
        txtNoPostArticles = view.findViewById(R.id.my_own_postArticle_txtNoPostArticles);
        imgNoLogin = view.findViewById(R.id.my_own_postArticle_imgNotLogin);
        imgNoPost = view.findViewById(R.id.my_own_postArticle_imgNoArticle);
        swipeRefreshLayout = view.findViewById(R.id.my_own_postArticle_swipeRefreshLayout);
        arrayList = new ArrayList<>();
        adapter = new list_article_recyclerView_adapter(arrayList, getContext());

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadData();
            }
        });
    }

    private void LoadData() {
        Query query = databaseReference.child("Users_Question_Articles");
        query.orderByChild("userUid").equalTo(firebaseUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    imgNoPost.setVisibility(View.GONE);
                    txtNoPostArticles.setVisibility(View.GONE);
                    arrayList.clear();
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        recyclerView.setVisibility(View.VISIBLE);
                        arrayList.add(getSet);
                    }
                    recyclerView.setAdapter(adapter);
                } else {
                    imgNoPost.setVisibility(View.VISIBLE);
                    txtNoPostArticles.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        /*
        databaseReference.child("Users_Question_Articles").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    arrayList.clear();
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        if (getSet.getUserUid().equals(firebaseUser.getUid())) {
                            txtNoPostArticles.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            arrayList.add(getSet);
                            recyclerView.setAdapter(adapter);
                        }
                    }
                } else {
                    txtNoPostArticles.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        */
    }

    private void setRecyclerView() {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        recyclerView.setLayoutManager(layoutManager);
    }

    private void onItemClick() {
        adapter.setOnMainClickListener(new MainOnClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                String Article_ID = arrayList.get(position).getArticle_ID();
                Intent intent = new Intent(getContext(), ReadArticleActivity.class);
                intent.putExtra("ArticleID", Article_ID);
                startActivity(intent);
            }
        });
    }
}
