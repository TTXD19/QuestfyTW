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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.LoginRelated.LoginActivity;
import taiwan.questfy.welsenho.questfy_tw.R;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;


public class KeepArticlesFragment extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private View view;
    private TextView txtNoArticlesKeep;
    private ImageView imgNoLogin;
    private keepArticleRecyclerViewAdapter adapter;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private OnFragmentInteractionListener mListener;

    public KeepArticlesFragment() {
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
        view = inflater.inflate(R.layout.fragment_keep_articles, container, false);
        InitItem();
        if (firebaseUser != null) {
            InitRecyclerView();
            getFirebaseData();
        } else {
            progressBar.setVisibility(View.GONE);
            txtNoArticlesKeep.setVisibility(View.VISIBLE);
            txtNoArticlesKeep.setText("登入享有更多服務");
            txtNoArticlesKeep.setTextColor(ContextCompat.getColor(getContext(), R.color.com_facebook_blue));
            txtNoArticlesKeep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    startActivity(intent);
                }
            });

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

    private void InitItem(){
        swipeRefreshLayout = view.findViewById(R.id.keep_article_swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.keep_article_recyclerView);
        progressBar = view.findViewById(R.id.keep_articles_progressBar);
        txtNoArticlesKeep = view.findViewById(R.id.keep_article_txtNoArticlesKeep);
        imgNoLogin = view.findViewById(R.id.keep_article_imgNotLogin);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        arrayList = new ArrayList<>();

        adapter = new keepArticleRecyclerViewAdapter(arrayList, getContext());
        adapter.setOnClickListener(new MainOnClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                String Article_ID = arrayList.get(position).getArticle_ID();
                Intent intent = new Intent(getContext() ,ReadArticleActivity.class);
                intent.putExtra("ArticleID", Article_ID);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFirebaseData();
            }
        });
    }

    private void InitRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
    }

    private void getFirebaseData(){
        databaseReference.child("Users_Keep_Articles").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    arrayList.clear();
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        recyclerView.setVisibility(View.VISIBLE);
                        txtNoArticlesKeep.setVisibility(View.GONE);
                        firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.INVISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }else {
                    recyclerView.setVisibility(View.GONE);
                    txtNoArticlesKeep.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
