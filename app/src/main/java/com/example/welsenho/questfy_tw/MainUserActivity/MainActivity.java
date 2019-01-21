package com.example.welsenho.questfy_tw.MainUserActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditInitActivity;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.LoginRelated.LoginActivity;
import com.example.welsenho.questfy_tw.LoginRelated.SignUpMethod;
import com.example.welsenho.questfy_tw.MainActivityFragment.MainActivityLatestArticleFragment;
import com.example.welsenho.questfy_tw.MainActivityFragment.MostPopularFragment;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MainActivityTabFragment.OnFragmentInteractionListener,
        MainActivityLatestArticleFragment.OnFragmentInteractionListener, UserProfileFragment.OnFragmentInteractionListener,
        MostPopularFragment.OnFragmentInteractionListener {

    private Boolean doubeTapExit = false;
    private int currentFilterPage;

    private TextView txtID;
    private TextView txtEmail;
    private TextView txtCheckVerified;
    private Button btnSendVerificationEmail;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private android.support.v7.widget.Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private View headerView;
    private FloatingActionButton floatingActionButton;

    private MainActivityMethods mainActivityMethods;
    private SignUpMethod signUpMethod;

    private ArrayList<FirebaseDatabaseGetSet> latestArrayList;
    private ArrayList<FirebaseDatabaseGetSet> mostPopularArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        navigationView = findViewById(R.id.main_activity_navigationView);
        drawerLayout = findViewById(R.id.main_activity_drawerLayout);
        toolbar = findViewById(R.id.main_activity_toolbar);
        floatingActionButton = findViewById(R.id.main_activity_floatingBar);

        //---------------------------------------------------- SET UP HEADER INFORMATION
        headerView = navigationView.getHeaderView(0);
        txtID = headerView.findViewById(R.id.txt_nav_header_userID);
        txtEmail = headerView.findViewById(R.id.txt_nav_header_userEmail);
        txtCheckVerified = headerView.findViewById(R.id.txt_nav_header_userVerified);
        btnSendVerificationEmail = headerView.findViewById(R.id.btn_nav_header_userVerified);
        //----------------------------------------------------


        mainActivityMethods = new MainActivityMethods();
        signUpMethod = new SignUpMethod();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        txtID.setText(firebaseUser.getDisplayName());
        txtEmail.setText(firebaseUser.getEmail());
        if (firebaseUser.isEmailVerified()) {
            txtCheckVerified.setText(R.string.verified);
            btnSendVerificationEmail.setVisibility(View.GONE);
        } else {
            txtCheckVerified.setText(R.string.not_verified);
            btnSendVerificationEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnSendVerificationEmail.setText("Sending...");
                    signUpMethod.emailVarification(firebaseUser, getApplicationContext());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnSendVerificationEmail.setText("Click to send email verification");
                        }
                    }, 2000);

                }
            });
        }

        /**
         * Lets inflate the very first fragment
         * Here , we are inflating the TabFragment as the first Fragment
         */
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_activity_frameLayout, new MainActivityTabFragment()).commit();

        /**
         * Initial set up for the navigationView and tool bar search view to work
         */
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.action_drawer_open, R.string.action_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        navigationClick();

        mainActivityButtonClick();

        /**
         * init each arrayList for searchView
         */
        initializeArrayList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_serarch_view, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (currentFilterPage == 0) {
                    searchFilter(s, latestArrayList);
                }else if (currentFilterPage == 1){
                    searchFilter(s, mostPopularArrayList);
                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void navigationClick() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case 0:
                        drawerLayout.openDrawer(GravityCompat.START);
                        break;

                    case R.id.main_activity_tab_home:
                        FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                        fragmentTransaction1.replace(R.id.main_activity_frameLayout, new MainActivityTabFragment()).commit();
                        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ＭainOrange));
                        floatingActionButton.show();
                        break;

                    case R.id.User_profile:
                        FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                        fragmentTransaction2.replace(R.id.main_activity_frameLayout, new UserProfileFragment()).commit();
                        toolbar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.user_profile_background));
                        floatingActionButton.hide();
                        break;

                    case R.id.Sign_out:
                        firebaseAuth.signOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

    }

    public void mainActivityButtonClick() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditInitActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (doubeTapExit) {
            super.onBackPressed();
        }
        doubeTapExit = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubeTapExit = false;
            }
        }, 2000);
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
    }


    /**
     * Below are all fragment interface to communicate with main activity
     * for search filtering purpose
     * Instrution steps:
     * 1 : get arrayList from fragment first
     * 2 : use search view onQueryTextListener to get input text
     * 3 : use searchFilter method to filter the arrayList and let filterList add it
     * 4 : return back to fragment and recyclerview it again
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        //do nothing
    }

    /**
     * currentFilterPage is for getting the current view page num in tab fragment
     * @param page
     */
    @Override
    public void getTabCurrentPage(int page) {
        currentFilterPage = page;
    }

    @Override
    public void latestArticleFilter(ArrayList<FirebaseDatabaseGetSet> arrayList) {
        latestArrayList = arrayList;
    }

    @Override
    public void mostPopularArticleFilter(ArrayList<FirebaseDatabaseGetSet> arrayList) {
        mostPopularArrayList = arrayList;
    }

    /**
     * searchFilter methods working process
     * 1. Get currentFilter page first to decide which page the user currently is
     * 2. Create FilterList(ArrayLise<FirebaseDatabaseGetSet>) for a referencer as a reference
     * 3. let FilterList become either latest articleList or mostPopularList
     * 4. Do Filtering process
     * 5. return to each LatestArticle & MostPopular fragment's returnFilterList(Array<List>) method
     * @param inputText
     * @param decidedFilterList
     */
    private void searchFilter(String inputText, ArrayList<FirebaseDatabaseGetSet> decidedFilterList) {

        ArrayList<FirebaseDatabaseGetSet> filterList = new ArrayList<>();
        if (!filterList.isEmpty()) {
            filterList.clear();
        }

        for (FirebaseDatabaseGetSet firebaseDatabaseGetSet : decidedFilterList) {
            if (firebaseDatabaseGetSet.getTitle().toLowerCase().contains(inputText.toLowerCase())) {
                filterList.add(firebaseDatabaseGetSet);
                if (currentFilterPage == 0) {
                    MainActivityLatestArticleFragment latestArticleFragment = (MainActivityLatestArticleFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().getFragments().get(0);
                    if (latestArticleFragment != null) {
                        if (!filterList.isEmpty()) {
                            latestArticleFragment.returnFilterList(filterList);
                        }
                    }
                }else if (currentFilterPage == 1){
                    MostPopularFragment mostPopularFragment = (MostPopularFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().getFragments().get(1);
                    if (mostPopularFragment != null){
                        if (!filterList.isEmpty()){
                            mostPopularFragment.returnFilterList(filterList);
                        }
                    }
                }
            }
        }

    }

    private void initializeArrayList(){
        latestArrayList = new ArrayList<>();
        mostPopularArrayList = new ArrayList<>();
    }
}
