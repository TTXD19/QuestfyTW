package com.example.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.AppAnnouncementRelated.AppAnnouncementActivity;
import com.example.welsenho.questfy_tw.AppAnnouncementRelated.AppRulesActivity;
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

    private RelativeLayout relayAnnounce;
    private RelativeLayout relayRules;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager layoutManager;
    private NestedScrollView nestedScrollView;

    private Boolean isMaxData = false;
    private long lastNode;
    private long lastNum;


    private OnFragmentInteractionListener mListener;

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<FirebaseDatabaseGetSet> searchArrayList;

    private NewArticleListRecyclerAdapter newArticleListRecyclerAdapter;
    private list_article_recyclerView_adapter adapter;

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

        relayAnnounce = view.findViewById(R.id.latest_article_recycler1);
        relayRules = view.findViewById(R.id.latest_article_recycler2);
        recyclerView = view.findViewById(R.id.latest_article_recyclerView);
        progressBar = view.findViewById(R.id.latest_article_progressBar);
        swipeRefreshLayout = view.findViewById(R.id.latest_article_swipeRefresh);
        nestedScrollView = view.findViewById(R.id.latest_article_nestedScrollView);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        InitItem();
        setRecyclerView();
        getLastKeyFromFirebase();
        loadMoreRecyclerData();
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
        //getLastKeyFromFirebase();
    }

    private void InitItem(){
        progressBar.setVisibility(View.VISIBLE);
        searchArrayList = new ArrayList<>();
        arrayList = new ArrayList<>();

        newArticleListRecyclerAdapter = new NewArticleListRecyclerAdapter(getContext(), new NewArticleListRecyclerAdapter.ClickItem() {
            @Override
            public void getItemPosition(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Intent intent = new Intent(getContext(), ReadArticleActivity.class);
                intent.putExtra("ArticleID", arrayList.get(position).getArticle_ID());
                startActivity(intent);
            }
        });

        adapter = new list_article_recyclerView_adapter(searchArrayList, getContext());
        adapter.setOnMainClickListener(new MainOnClickListener() {
            @Override
            public void onClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Intent intent = new Intent(getContext(), ReadArticleActivity.class);
                intent.putExtra("ArticleID", arrayList.get(position).getArticle_ID());
                startActivity(intent);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLastKeyFromFirebase();
            }
        });

        relayAnnounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AppAnnouncementActivity.class);
                startActivity(intent);
            }
        });

        relayRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AppRulesActivity.class);
                startActivity(intent);
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

    public void LoadQueryData(final String inputSearch){
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);
        Query query = databaseReference.child("Users_Question_Articles").orderByChild("uploadTimeStamp");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    searchArrayList.clear();
                    if (!inputSearch.equals("")) {
                        for (DataSnapshot DS : dataSnapshot.getChildren()) {
                            FirebaseDatabaseGetSet searchGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                            if (searchGetSet.getTitle().toLowerCase().contains(inputSearch.toLowerCase()) || searchGetSet.getMajors().toLowerCase().contains(inputSearch.toLowerCase())) {
                                searchArrayList.add(searchGetSet);
                            }
                        }
                        if (!searchArrayList.isEmpty()) {
                            recyclerView.setVisibility(View.VISIBLE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                        }
                        recyclerView.setAdapter(adapter);
                        progressBar.setVisibility(View.GONE);
                    }else {
                        getLastKeyFromFirebase();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    private void getLastKeyFromFirebase() {

        isMaxData = false;
        arrayList = new ArrayList<>();
        arrayList.clear();

        newArticleListRecyclerAdapter = new NewArticleListRecyclerAdapter(getContext(), new NewArticleListRecyclerAdapter.ClickItem() {
            @Override
            public void getItemPosition(int position, ArrayList<FirebaseDatabaseGetSet> arrayList) {
                Intent intent = new Intent(getContext(), ReadArticleActivity.class);
                intent.putExtra("ArticleID", arrayList.get(position).getArticle_ID());
                startActivity(intent);
            }
        });

        Query getLastKey = databaseReference.child("Users_Question_Articles").orderByChild("uploadTimeStamp").limitToLast(1);
        getLastKey.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot DS : dataSnapshot.getChildren()) {
                    FirebaseDatabaseGetSet getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                    lastNum = getSet.getUploadTimeStamp();
                    Log.d("MOSTPOPMAXDATALastNum", String.valueOf(lastNum));
                    getFirstData();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getFirstData() {
        databaseReference.child("Users_Question_Articles").orderByChild("uploadTimeStamp").limitToFirst(100).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot DS : dataSnapshot.getChildren()) {
                        FirebaseDatabaseGetSet getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(getSet);
                    }

                    for (int i = 0; i<= arrayList.size() - 1; i++){
                        Log.d("MOSTPOPMAXDATACOUNT12", String.valueOf(arrayList.get(i).getUploadTimeStamp()));
                    }
                    /**
                     * Handle arrayList filter & load first data
                     */

                    newArticleListRecyclerAdapter.addAll(arrayList);
                    lastNode = arrayList.get(arrayList.size() - 1).getUploadTimeStamp();
                    Log.d("MOSTPOPMAXDATALastNode", String.valueOf(lastNode));

                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(newArticleListRecyclerAdapter);

                    progressBar.setVisibility(View.GONE);
                    swipeRefreshLayout.setEnabled(true);
                    swipeRefreshLayout.setRefreshing(false);

                    /**
                     * Check whether is the max data or not
                     */
                    Log.d("MOSTPOPMAXDATALastNode", String.valueOf(lastNode));
                    Log.d("MOSTPOPMAXDATALastNum", String.valueOf(lastNum));
                    if (lastNum == lastNode) {
                        isMaxData = true;
                    }else {
                        isMaxData = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadMoreRecyclerData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nestedScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    if (!nestedScrollView.canScrollVertically(1)){
                        if (!isMaxData){
                            getMoreData();
                            Log.d("GETMOREDATA", "LOADMORE");
                        }else {
                            Log.d("GETMOREDATA", "NOTLOADMORE");
                        }
                    }
                }
            });
        }
    }

    private void getMoreData(){
        if (!isMaxData){
            databaseReference.child("Users_Question_Articles").orderByChild("uploadTimeStamp").startAt(lastNode).limitToFirst(100).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Log.d("GETMOREDATA", "LOADING");
                        arrayList.clear();
                        for (DataSnapshot DS:dataSnapshot.getChildren()){
                            FirebaseDatabaseGetSet get = DS.getValue(FirebaseDatabaseGetSet.class);
                            arrayList.add(get);
                        }

                        arrayList.remove(0);
                        /*for (int i = 0; i<= arrayList.size() - 1; i++){
                            Log.d("MOSTPOPMAXDATACOUNT12", String.valueOf(arrayList.get(i).getUploadTimeStamp()));
                        }*/
                        lastNode = arrayList.get(arrayList.size() - 1).getUploadTimeStamp();
                        newArticleListRecyclerAdapter.addAll(arrayList);
                        newArticleListRecyclerAdapter.notifyDataSetChanged();
                        Log.d("GETMOREDATA", "TOTAL DATA : " + String.valueOf(arrayList.size()));
                        if (lastNum == lastNode){
                            isMaxData = true;
                        }

                    }else {
                        Log.d("GETMOREDATA", "NOTEXIST");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {
            Log.d("GETMOREDATA", "DATAMAX");
        }
    }



    private void setRecyclerView(){
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        recyclerView.setVisibility(View.VISIBLE);
    }
}
