package com.example.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.CustomScrollView;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.InternetConnectionDetect;
import com.example.welsenho.questfy_tw.MainUserActivity.MainActivity;
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

    private NestedScrollView nestedScrollView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;

    //New loading method
    private long lastNum;
    private long lastNode;
    private Boolean isMaxData = false;
    private LinearLayoutManager layoutManager;

    private int testInitNum = 3;
    private long testLastNum;


    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<FirebaseDatabaseGetSet> searchArrayList;

    private ArrayList<FirebaseDatabaseGetSet> testArrayList;

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
        //getLastNum();

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MostPopularFragment.OnFragmentInteractionListener) {
            mListener = (MostPopularFragment.OnFragmentInteractionListener) context;
        } else {
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

    private void InitItem() {
        nestedScrollView = view.findViewById(R.id.most_pop_fm_nestedScrollView);
        recyclerView = view.findViewById(R.id.most_pop_fm_recyclerView);
        progressBar = view.findViewById(R.id.most_pop_fm_progressBar);
        swipeRefreshLayout = view.findViewById(R.id.most_pop_fm_swipeRefresh);

        searchArrayList = new ArrayList<>();
        testArrayList = new ArrayList<>();

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
    }

    private void InitRecyclerView() {
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
    }

    private void InitFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
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
                    //newArticleListRecyclerAdapter.setGetArrayListForClick(clickArrayList);
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
                            //testData();
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
                        for (int i = 0; i<= arrayList.size() - 1; i++){
                            Log.d("MOSTPOPMAXDATACOUNT12", String.valueOf(arrayList.get(i).getUploadTimeStamp()));
                        }
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

    public void LoadQueryData(final String inputSearch) {
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);
        Query query = databaseReference.child("Users_Question_Articles").orderByChild("Article_like_count");
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
                        if (searchArrayList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.VISIBLE);
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






    //---------------------------------------------------------------------------------------------------------------------------------------

    private void getLastNum2(){
        databaseReference.child("VTEST").orderByChild("NUMBER").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot DS:dataSnapshot.getChildren()){
                    FirebaseDatabaseGetSet getSet  = DS.getValue(FirebaseDatabaseGetSet.class);
                    testLastNum = getSet.getNUMBER();
                    Log.d("MOSTPOPCURRENTLASTNODE", String.valueOf(testLastNum));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void testData2() {
        databaseReference = firebaseDatabase.getReference().child("VTEST");
        Query query = databaseReference.orderByChild("NUMBER").startAt(testInitNum).limitToFirst(3);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (testInitNum != testLastNum) {
                    if (dataSnapshot.exists()) {
                        Log.d("MOSTPOPCURRENTLASTNODE", "LOAINGMOREDATA");
                        for (DataSnapshot DS : dataSnapshot.getChildren()) {
                            FirebaseDatabaseGetSet getSet = DS.getValue(FirebaseDatabaseGetSet.class);
                            testArrayList.add(getSet);
                        }

                        Log.d("MOSTPOPCURRENTLASTNODE", String.valueOf(testInitNum));
                        for (int i = 0; i <= testArrayList.size() - 1; i++){
                            Log.d("MOSTPOPCURRENTLASTNODE" + String.valueOf(i), String.valueOf(testArrayList.get(i).getNUMBER()));
                        }

                        /*
                        Log.d("MOSTPOPCURRENTLASTNODE1", String.valueOf(testArrayList.get(0).getNUMBER()));
                        Log.d("MOSTPOPCURRENTLASTNODE2", String.valueOf(testArrayList.get(1).getNUMBER()));
                        if (testArrayList.size() == 4 || testArrayList.size() == 6) {
                            Log.d("MOSTPOPCURRENTLASTNODE3", String.valueOf(testArrayList.get(2).getNUMBER()));
                            Log.d("MOSTPOPCURRENTLASTNODE4", String.valueOf(testArrayList.get(3).getNUMBER()));
                        }
                        if (testArrayList.size() == 6) {
                            Log.d("MOSTPOPCURRENTLASTNODE5", String.valueOf(testArrayList.get(4).getNUMBER()));
                            Log.d("MOSTPOPCURRENTLASTNODE6", String.valueOf(testArrayList.get(5).getNUMBER()));
                        }*/
                        testInitNum = testArrayList.get(testArrayList.size() -1).getNUMBER();
                        testArrayList.remove(testArrayList.size() - 1);

                    } else {
                        Log.d("MOSTPOPCURRENTLASTNODE", "NOLOAINGMOREDATA");
                    }
                }else {
                    Log.d("MOSTPOPCURRENTLASTNODE", "DATAISMAX");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void UnderSdk23Scroll(){
        nestedScrollView.setNestedScrollingEnabled(false);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!recyclerView.canScrollVertically(1)) {
                    Log.d("SCROLLING", "TRUE");
                    Log.d("SCROLLING", isMaxData.toString() + "isMax");
                    if (!isMaxData) {
                        Log.d("SCROLLINGFORMORE", "TRUE");
                        //getMoreData();
                        //testData();
                    } else {
                        Log.d("SCROLLINGFORMORE", "FALSE");
                    }
                } else {
                    Log.d("SCROLLING", "FALSE");
                }
            }
        });
    }
}
