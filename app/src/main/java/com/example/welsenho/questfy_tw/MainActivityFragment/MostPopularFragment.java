package com.example.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.InternetConnectionDetect;
import com.example.welsenho.questfy_tw.R;
import com.example.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MostPopularFragment extends Fragment {

    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private ArrayList<FirebaseDatabaseGetSet> orginalMostPopulatArrayList;
    private Context context;
    private list_article_recyclerView_adapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    private OnFragmentInteractionListener mListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;


    public MostPopularFragment() {
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
        View view = inflater.inflate(R.layout.fragment_most_popular, container, false);
        recyclerView = view.findViewById(R.id.most_pop_fm_recyclerView);
        progressBar = view.findViewById(R.id.most_pop_fm_progressBar);
        swipeRefreshLayout = view.findViewById(R.id.most_pop_fm_swipeRefresh);
        progressBar.setVisibility(View.VISIBLE);

        orginalMostPopulatArrayList = new ArrayList<>();
        adapter = new list_article_recyclerView_adapter(orginalMostPopulatArrayList, getContext());
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        adapter.setOnMainClickListener(new MainOnClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                String postID = arrayList.get(position).getArticle_ID();
                Intent intent = new Intent(getContext(), ReadArticleActivity.class);
                intent.putExtra("ArticleID", postID);
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadData();
            }
        });
        LoadData();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MostPopularFragment.OnFragmentInteractionListener){
            mListener = (MostPopularFragment.OnFragmentInteractionListener) context;
        }else {
            throw new RuntimeException(context.toString() +
                    " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadData();
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
        void mostPopularArticleFilter(ArrayList<FirebaseDatabaseGetSet> arrayList);
    }

    private void LoadData(){
            databaseReference.child("Users_Question_Articles").orderByChild("Article_like_count").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        orginalMostPopulatArrayList.clear();
                        for (DataSnapshot DS : dataSnapshot.getChildren()) {
                            firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                            orginalMostPopulatArrayList.add(firebaseDatabaseGetSet);
                            recyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                            mListener.mostPopularArticleFilter(orginalMostPopulatArrayList);
                            databaseReference.removeEventListener(this);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    /**
     * For returning filtered data from main activity and rebuild the recyclerview data again
     * @param returnList
     */
    public void returnFilterList(ArrayList<FirebaseDatabaseGetSet> returnList){
        adapter = new list_article_recyclerView_adapter(returnList, getContext());
        adapter.setOnMainClickListener(new MainOnClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                String postID = arrayList.get(position).getArticle_ID();
                Intent intent = new Intent(getContext(), ReadArticleActivity.class);
                intent.putExtra("ArticleID", postID);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public void NoArticleUpdateYet(){
        recyclerView.setVisibility(View.GONE);
    }
}
