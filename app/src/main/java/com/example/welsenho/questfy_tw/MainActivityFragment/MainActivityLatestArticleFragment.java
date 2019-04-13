package com.example.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.example.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivityLatestArticleFragment extends Fragment {

    private static final int ITEM_LOAD_COUNT = 5;

    private int positionClick;
    private int totalCount = 0;
    private int lastVisibleItem;
    private Boolean isLoading = false;
    private Boolean isMaxData = false;
    private String lastNode = "";
    private String lastKey = "";
    private NewArticleListRecyclerAdapter newArticleListRecyclerAdapter;
    private ArrayList<FirebaseDatabaseGetSet> testArrayList;

    private OnFragmentInteractionListener mListener;

    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager layoutManager;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public MainActivityLatestArticleFragment() {
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
        View view = inflater.inflate(R.layout.fragment_main_activity_latest_article, container, false);

        recyclerView = view.findViewById(R.id.latest_article_recyclerView);
        progressBar = view.findViewById(R.id.latest_article_progressBar);
        swipeRefreshLayout = view.findViewById(R.id.latest_article_swipeRefresh);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        //Testing code
        InitItem();
        getLastKeyFromFirebase();
        setRecyclerView();
        getFirstData();
        testRecyclerView();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivityLatestArticleFragment.OnFragmentInteractionListener){
            mListener = (MainActivityLatestArticleFragment.OnFragmentInteractionListener) context;
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
    }

    private void InitItem(){
        progressBar.setVisibility(View.VISIBLE);
        arrayList = new ArrayList<>();
        testArrayList = new ArrayList<>();
        newArticleListRecyclerAdapter = new NewArticleListRecyclerAdapter(getContext(), new NewArticleListRecyclerAdapter.getItemID() {
            @Override
            public void getItemID(int position) {
                String postID = arrayList.get(position).getArticle_ID();
                Intent intent = new Intent(getContext(), ReadArticleActivity.class);
                intent.putExtra("ArticleID", postID);
                positionClick = position;
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getFirstData();
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
        void latestArticleFilter(ArrayList<FirebaseDatabaseGetSet> arrayList);
    }

    /**
     * For returning filtered data from main activity and rebuild the recyclerview data again
     * @param returnList
     */
    public void returnFilterList(final ArrayList<FirebaseDatabaseGetSet> returnList){
        if (returnList != null){
            recyclerView.setVisibility(View.VISIBLE);
            list_article_recyclerView_adapter filterAdapter = new list_article_recyclerView_adapter(returnList, getContext());
            filterAdapter.setOnMainClickListener(new MainOnClickListener() {
                @Override
                public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                    String postID = arrayList.get(position).getArticle_ID();
                    Intent intent = new Intent(getContext(), ReadArticleActivity.class);
                    intent.putExtra("ArticleID", postID);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(filterAdapter);
            Log.d("FILTERLISTSIZEtitle", returnList.get(0).getTitle());
        }
    }

    public void hideRecyclerView(){
        recyclerView.setVisibility(View.GONE);
    }

    private void getLastKeyFromFirebase(){
        Query getLastKey = databaseReference.child("Users_Question_Articles").orderByChild("uploadTimeStamp").limitToLast(1);
        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot DS:dataSnapshot.getChildren()){
                    lastKey = DS.getKey();
                    Log.d("LASTITEMKEY", lastKey);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getFirstData(){
        databaseReference.child("Users_Question_Articles").orderByChild("uploadTimeStamp").limitToFirst(100).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                testArrayList.clear();
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        FirebaseDatabaseGetSet getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        testArrayList.add(getSet);
                        arrayList.add(getSet);
                        mListener.latestArticleFilter(arrayList);
                    }
                    /**
                     * Handle arrayList filter & load first data
                     */
                    newArticleListRecyclerAdapter.setGetArrayListForClick(arrayList);
                    newArticleListRecyclerAdapter.addAll(testArrayList);
                    lastNode =  newArticleListRecyclerAdapter.getLastItemId();
                    recyclerView.setAdapter(newArticleListRecyclerAdapter);
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    /**
                     * Check whether is the max data or not
                     */
                    if (lastKey.equals(lastNode)){
                        isLoading = true;
                        isMaxData = true;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMoreData(){
        databaseReference.child("Users_Question_Articles").orderByChild("uploadTimeStamp").startAt(lastNode).limitToFirst(100).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (!isMaxData) {
                        if (dataSnapshot.hasChildren()) {
                            testArrayList.clear();
                            arrayList.remove(arrayList.size() - 1);
                            for (DataSnapshot DS : dataSnapshot.getChildren()) {
                                FirebaseDatabaseGetSet getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                                testArrayList.add(getSet);
                                arrayList.add(getSet);
                            }

                            /**
                             * Load more data after 100 past
                             */
                            newArticleListRecyclerAdapter.setGetArrayListForClick(arrayList);
                            testArrayList.remove(0);
                            newArticleListRecyclerAdapter.addAll(testArrayList);
                            lastNode = newArticleListRecyclerAdapter.getLastItemId();
                            mListener.latestArticleFilter(arrayList);
                            progressBar.setVisibility(View.GONE);

                            /**
                             * Check whether is the max data or not
                             */
                            if (lastKey.equals(lastNode)) {
                                isLoading = true;
                                isMaxData = true;
                            }
                        }else {
                            Log.d("CURRENTLASTNODE", "MAXNOTDATA");
                        }
                    } else {
                        Log.d("CURRENTLASTNODE", "MAXDATA");
                    }
                }else {
                    Log.d("CURRENTLASTNODE", "STOPLOADING");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void testRecyclerView(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    Log.d("SCROLLING", "TRUE");
                    if (!isMaxData) {
                        int totalItem = layoutManager.getItemCount();
                        Log.d("CURRENTLASTNODE", String.valueOf(totalItem));
                        progressBar.setVisibility(View.VISIBLE);
                        getMoreData();
                    }else {
                        Log.d("SCROLLING", "MAXDATA");
                    }
                }else {
                    Log.d("SCROLLING", "FALSE");
                }
            }
        });
    }

    private void setRecyclerView(){
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void setOriginalRecyclerView(){
        recyclerView.setAdapter(newArticleListRecyclerAdapter);
        Toast.makeText(getContext(), "original", Toast.LENGTH_SHORT).show();
    }
}
