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
import android.util.Log;
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

    private int positionClick;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;

    //New loading method
    private String lastNode = "";
    private String lastKey = "";
    private Boolean isLoading = false;
    private Boolean isMaxData = false;
    private LinearLayoutManager layoutManager;

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<FirebaseDatabaseGetSet> clickArrayList;
    private ArrayList<FirebaseDatabaseGetSet> searchArrayList;

    private list_article_recyclerView_adapter adapter;
    private NewArticleListRecyclerAdapter newArticleListRecyclerAdapter;
    //

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
        view = inflater.inflate(R.layout.fragment_most_popular, container, false);

        //New loading method
        InitItem();
        InitFirebase();
        InitRecyclerView();
        getLastKeyFromFirebase();
        loadMoreRecyclerData();

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
        //LoadData();
    }

    private void InitItem(){
        recyclerView = view.findViewById(R.id.most_pop_fm_recyclerView);
        progressBar = view.findViewById(R.id.most_pop_fm_progressBar);
        swipeRefreshLayout = view.findViewById(R.id.most_pop_fm_swipeRefresh);

        arrayList = new ArrayList<>();
        clickArrayList = new ArrayList<>();
        searchArrayList = new ArrayList<>();

        adapter = new list_article_recyclerView_adapter(searchArrayList, getContext());
        adapter.setOnMainClickListener(new MainOnClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Toast.makeText(getContext(), arrayList.get(position).getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

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

    private void InitRecyclerView(){
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
    }

    private void InitFirebase(){
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void getLastKeyFromFirebase(){
        Query getLastKey = databaseReference.child("Users_Question_Articles").orderByKey().limitToLast(1);
        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot DS:dataSnapshot.getChildren()){
                    lastKey = DS.getKey();
                    Log.d("CHECLMAXDATA22345", lastKey);
                    getFirstData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getFirstData(){
        databaseReference.child("Users_Question_Articles").orderByKey().limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (lastKey != null) {
                    Log.d("CHECLMAXDATA223456", "NOT NULL" + lastKey + "???");
                }else {
                    Log.d("CHECLMAXDATA223456", "NULL");
                }
                arrayList.clear();
                clickArrayList.clear();
                if (dataSnapshot.hasChildren()){
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        FirebaseDatabaseGetSet getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        clickArrayList.add(getSet);
                        arrayList.add(getSet);
                        mListener.mostPopularArticleFilter(arrayList);
                    }


                    /**
                     * Handle arrayList filter & load first data
                     */
                    newArticleListRecyclerAdapter.setGetArrayListForClick(arrayList);
                    newArticleListRecyclerAdapter.addAll(clickArrayList);
                    lastNode =  newArticleListRecyclerAdapter.getLastItemId();
                    Log.d("LASTITEMKEY22", lastNode);
                    recyclerView.setAdapter(newArticleListRecyclerAdapter);
                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);

                    /**
                     * Check whether is the max data or not
                     */
                    Log.d("CHECLMAXDATA2234", lastNode);
                    Log.d("CHECLMAXDATA223", lastKey);
                    if (lastKey.equals(lastNode)){
                        isLoading = true;
                        isMaxData = true;
                    }

                    Log.d("CHECLMAXDATA", isMaxData.toString() + "isMax");
                    Log.d("MostPopularMaxData", String.valueOf(arrayList.size()));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMoreData(){
        databaseReference.child("Users_Question_Articles").orderByKey().startAt(lastNode).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (!isMaxData) {
                        if (dataSnapshot.hasChildren()) {
                            clickArrayList.clear();
                            arrayList.remove(arrayList.size() - 1);
                            for (DataSnapshot DS : dataSnapshot.getChildren()) {
                                FirebaseDatabaseGetSet getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                                clickArrayList.add(getSet);
                                arrayList.add(getSet);
                            }

                            /**
                             * Load more data after 100 past
                             */
                            newArticleListRecyclerAdapter.setGetArrayListForClick(arrayList);
                            clickArrayList.remove(0);
                            newArticleListRecyclerAdapter.addAll(clickArrayList);
                            newArticleListRecyclerAdapter.notifyDataSetChanged();
                            lastNode = newArticleListRecyclerAdapter.getLastItemId();
                            mListener.mostPopularArticleFilter(arrayList);
                            progressBar.setVisibility(View.GONE);

                            /**
                             * Check whether is the max data or not
                             */
                            if (lastKey.equals(lastNode)) {
                                isLoading = true;
                                isMaxData = true;
                            }

                            Log.d("MostPopularMaxData", String.valueOf(arrayList.size()));
                        }else {
                            Log.d("MOSTPOPCURRENTLASTNODE", "DATADONOTEXIST");
                        }
                    } else {
                        Log.d("MOSTPOPCURRENTLASTNODE", "MAXDATA");
                    }
                }else {
                    Log.d("MOSTPOPCURRENTLASTNODE", "STOPLOADING");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMoreRecyclerData(){
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)){
                    Log.d("SCROLLING", "TRUE");
                    Log.d("SCROLLING", isMaxData.toString() + "isMax");
                    if (!isMaxData) {
                        Log.d("SCROLLINGFORMORE", "TRUE");
                        getMoreData();
                    }else {
                        Log.d("SCROLLINGFORMORE", "FALSE");
                    }
                }else {
                    Log.d("SCROLLING", "FALSE");
                }
            }
        });
    }

    public void setOriginalRecyclerView(){
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(newArticleListRecyclerAdapter);
        Toast.makeText(getContext(), "original", Toast.LENGTH_SHORT).show();
    }

    public void LoadQueryData(final String inputSearch){
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);
        Query query = databaseReference.child("Users_Question_Articles").orderByChild("Article_like_count");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    searchArrayList.clear();
                    for (DataSnapshot DS:dataSnapshot.getChildren()){
                        FirebaseDatabaseGetSet searchGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        if (searchGetSet.getTitle().toLowerCase().contains(inputSearch.toLowerCase())) {
                            searchArrayList.add(searchGetSet);
                        }
                    }
                    if (searchArrayList.isEmpty()){
                        recyclerView.setVisibility(View.GONE);
                    }else {
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                    recyclerView.setAdapter(adapter);
                    progressBar.setVisibility(View.GONE);
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
        void mostPopularArticleFilter(ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
