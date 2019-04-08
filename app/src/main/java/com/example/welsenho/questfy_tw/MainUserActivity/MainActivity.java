package com.example.welsenho.questfy_tw.MainUserActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
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

import com.example.welsenho.questfy_tw.DailyQuestionsRelated.MainDailyQuestionActivity;
import com.example.welsenho.questfy_tw.EditActivityRelated.EditInitActivity;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.FriendRelatedActivity.FriendMessageFragment;
import com.example.welsenho.questfy_tw.FriendRelatedActivity.FriendRequestFragment;
import com.example.welsenho.questfy_tw.FriendRelatedActivity.MainFriendFragment;
import com.example.welsenho.questfy_tw.LoginRelated.LoginActivity;
import com.example.welsenho.questfy_tw.LoginRelated.SignUpMethod;
import com.example.welsenho.questfy_tw.MainActivityFragment.KeepArticlesFragment;
import com.example.welsenho.questfy_tw.MainActivityFragment.MainActivityLatestArticleFragment;
import com.example.welsenho.questfy_tw.MainActivityFragment.MainSubjectChooseFragment;
import com.example.welsenho.questfy_tw.MainActivityFragment.MostPopularFragment;
import com.example.welsenho.questfy_tw.MainActivityFragment.MyOwnPostArticles;
import com.example.welsenho.questfy_tw.MeetUpScheduleRelated.MeetUpScheduleFragment;
import com.example.welsenho.questfy_tw.PersonAskQuestionRelated.PersonAskByFragment;
import com.example.welsenho.questfy_tw.PersonAskQuestionRelated.PersonakAskMainFragment;
import com.example.welsenho.questfy_tw.PersonAskQuestionRelated.PersonalAskToFragment;
import com.example.welsenho.questfy_tw.R;
import com.example.welsenho.questfy_tw.ReigisterCompleteInfoRelated.RealNameRegisterActivity;
import com.example.welsenho.questfy_tw.SettingPageRelated.SettingPageFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements MainActivityTabFragment.OnFragmentInteractionListener,
        MainActivityLatestArticleFragment.OnFragmentInteractionListener, UserProfileFragment.OnFragmentInteractionListener,
        MostPopularFragment.OnFragmentInteractionListener, MainFriendFragment.OnFragmentInteractionListener, FriendMessageFragment.OnFragmentInteractionListener,
        FriendRequestFragment.OnFragmentInteractionListener, MainSubjectChooseFragment.OnFragmentInteractionListener, MainDailyQuestionActivity.OnFragmentInteractionListener,
        KeepArticlesFragment.OnFragmentInteractionListener, MyOwnPostArticles.OnFragmentInteractionListener, MeetUpScheduleFragment.OnFragmentInteractionListener, SettingPageFragment.OnFragmentInteractionListener,
        PersonakAskMainFragment.OnFragmentInteractionListener, PersonalAskToFragment.OnFragmentInteractionListener, PersonAskByFragment.OnFragmentInteractionListener {

    private Boolean doubeTapExit = false;
    private String completeInfo = "False";
    private int currentFilterPage;

    private TextView txtID;
    private TextView txtEmail;
    private TextView txtCheckVerified;
    private TextView txtCheckCompleteInfo;
    private TextView txtSchoolName;
    private TextView txtGuestUser;
    private TextView txtGuestUserGreet;
    private CircleImageView circleImageView;
    private Button btnCompleteUserInfo;
    private Button btnLogin;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;
    private android.support.v7.widget.Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
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
        txtGuestUser = headerView.findViewById(R.id.txt_nav_header_guestUser);
        txtID = headerView.findViewById(R.id.txt_nav_header_userID);
        txtEmail = headerView.findViewById(R.id.txt_nav_header_userEmail);
        txtCheckVerified = headerView.findViewById(R.id.txt_nav_header_userVerified);
        txtCheckCompleteInfo = headerView.findViewById(R.id.txt_nav_header_userInfoComplete);
        txtSchoolName = headerView.findViewById(R.id.txt_nav_header_userSchoolName);
        txtGuestUserGreet = headerView.findViewById(R.id.txt_nav_header_guestUserGreet);
        btnCompleteUserInfo = headerView.findViewById(R.id.btn_nav_header_completeUserInfo);
        btnLogin = headerView.findViewById(R.id.btn_nav_header_guestUserInfo);
        circleImageView = headerView.findViewById(R.id.img_nav_header_user);
        //----------------------------------------------------

        mainActivityMethods = new MainActivityMethods();
        signUpMethod = new SignUpMethod();

        InitFirebase();
        if (firebaseUser != null) {
            InitUserPofile();
        }else {
            InitGuestUser();
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

        //Default Init fragment fragment
        FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
        fragmentTransaction1.replace(R.id.main_activity_frameLayout, new MainActivityTabFragment(), "MainHomeFragment").commit();
        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ＭainOrange));
        toolbar.setTitle("Questfy");
        floatingActionButton.show();
        Log.d("CurrentFragment", getSupportFragmentManager().getFragments().toString());

        /**
         * init each arrayList for searchView
         */
        initializeArrayList();
        ItmeClick();
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
                MainActivityTabFragment mainActivityTabFragment = (MainActivityTabFragment)getSupportFragmentManager().findFragmentByTag("MainHomeFragment");
                if (mainActivityTabFragment != null && mainActivityTabFragment.isVisible()) {
                    if (latestArrayList != null) {
                        Log.d("ArrayCheckNull", String.valueOf(latestArrayList.size()));
                        LatestSearchFilter(s, latestArrayList);
                    }
                    if (mostPopularArrayList != null) {
                        MostPopularFilter(s, mostPopularArrayList);
                    }
                }
                return false;
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
                        fragmentTransaction1.replace(R.id.main_activity_frameLayout, new MainActivityTabFragment(), "MainHomeFragment").commit();
                        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ＭainOrange));
                        toolbar.setTitle("Questfy");
                        floatingActionButton.show();
                        Log.d("CurrentFragment", getSupportFragmentManager().getFragments().toString());
                        break;

                    case R.id.Main_personal_ask:
                        FragmentTransaction personal_ask_fragment = fragmentManager.beginTransaction();
                        personal_ask_fragment.replace(R.id.main_activity_frameLayout, new PersonakAskMainFragment(), "MainPersonalAsk").commit();
                        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ＭainOrange));
                        toolbar.setTitle(R.string.person_ans_question);
                        floatingActionButton.hide();
                        Log.d("MainPersonalAsk", getSupportFragmentManager().getFragments().toString());
                        break;

                    case R.id.main_related_question_fragment:
                        FragmentTransaction dailyQuestionFragment = fragmentManager.beginTransaction();
                        dailyQuestionFragment.replace(R.id.main_activity_frameLayout, new MainDailyQuestionActivity(),"DailyQuestionFragment").commit();
                        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ＭainOrange));
                        toolbar.setTitle(getString(R.string.do_you_know));
                        floatingActionButton.hide();
                        Log.d("CurrentFragment", getSupportFragmentManager().getFragments().toString());
                        break;

                    case R.id.Meet_up:
                        FragmentTransaction meetUpFragment = fragmentManager.beginTransaction();
                        meetUpFragment.replace(R.id.main_activity_frameLayout, new MeetUpScheduleFragment(),"MeetUpFragment").commit();
                        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ＭainOrange));
                        toolbar.setTitle(getString(R.string.meet_up_schedule));
                        floatingActionButton.hide();
                        Log.d("CurrentFragment", getSupportFragmentManager().getFragments().toString());
                        break;

                    case R.id.User_profile:
                        FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                        fragmentTransaction2.replace(R.id.main_activity_frameLayout, new UserProfileFragment(), "UserProfileFragemnt").commit();
                        toolbar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.user_profile_background));
                        toolbar.setTitle(getString(R.string.personal_info));
                        floatingActionButton.hide();
                        Log.d("CurrentFragment", getSupportFragmentManager().getFragments().toString());
                        break;

                    case R.id.Main_Friend_Fragment:
                        FragmentTransaction mainFriendFragment = fragmentManager.beginTransaction();
                        mainFriendFragment.replace(R.id.main_activity_frameLayout, new MainFriendFragment(), "MainFriendFragment").commit();
                        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ＭainOrange));
                        toolbar.setTitle(getString(R.string.friends));
                        floatingActionButton.hide();
                        Log.d("CurrentFragment", getSupportFragmentManager().getFragments().toString());
                        break;

                    case R.id.setting:
                        FragmentTransaction settingFragment = fragmentManager.beginTransaction();
                        settingFragment.replace(R.id.main_activity_frameLayout, new SettingPageFragment(), "MainSettingFragment").commit();
                        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ＭainOrange));
                        toolbar.setTitle(getString(R.string.setting));
                        floatingActionButton.hide();
                        Log.d("CurrentFragment", getSupportFragmentManager().getFragments().toString());
                        break;

                    case R.id.Sign_out:
                        firebaseAuth.signOut();
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
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
     *
     * @param uri
     */
    @Override
    public void onFragmentInteraction(Uri uri) {
        //do nothing
    }

    /**
     * currentFilterPage is for getting the current view page num in tab fragment
     *
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
    public void mostPopularArticleFilter(ArrayList<FirebaseDatabaseGetSet> orginalMostPopulatArrayList) {
        mostPopularArrayList = orginalMostPopulatArrayList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (firebaseUser != null) {
            InitUserPofile();
        }
    }

    private void InitUserPofile(){
        txtID.setText(firebaseUser.getDisplayName());
        txtEmail.setText(firebaseUser.getEmail());

        if (firebaseUser.isEmailVerified()) {
            txtCheckVerified.setText(R.string.verified);
        }else {
            txtCheckVerified.setText(R.string.not_verified);
        }

        if (firebaseUser.getPhotoUrl() != null){
            Picasso.get().load(firebaseUser.getPhotoUrl()).fit().into(circleImageView);
        }

        databaseReference.child("Users_profile").child(firebaseUser.getUid()).child("CompleteInformationCheck").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getValue().equals("success")) {
                        btnCompleteUserInfo.setVisibility(View.GONE);
                    } else {
                        btnCompleteUserInfo.setVisibility(View.VISIBLE);
                    }
                    completeInfo = dataSnapshot.getValue().toString();
                    saveCompleteInfo(completeInfo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        databaseReference.child("Users_profile").child(firebaseUser.getUid()).child("schoolName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    txtSchoolName.setText(dataSnapshot.getValue().toString());
                }else {
                    txtSchoolName.setText("School not set");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void InitGuestUser(){
        txtGuestUser.setVisibility(View.VISIBLE);
        txtGuestUserGreet.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.VISIBLE);
        txtID.setVisibility(View.GONE);
        txtEmail.setVisibility(View.GONE);
        txtCheckVerified.setVisibility(View.GONE);
        txtSchoolName.setVisibility(View.GONE);
        btnCompleteUserInfo.setVisibility(View.GONE);
    }

    /**
     * searchFilter methods working process
     * 1. Get currentFilter page first to decide which page the user currently is
     * 2. Create FilterList(ArrayLise<FirebaseDatabaseGetSet>) for a referencer as a reference
     * 3. let FilterList become either latest articleList or mostPopularList
     * 4. Do Filtering process
     * 5. return to each LatestArticle & MostPopular fragment's returnFilterList(Array<List>) method
     *
     * @param inputText
     * @param decidedFilterList
     */
    private void searchFilter(String inputText, ArrayList<FirebaseDatabaseGetSet> decidedFilterList) {
        ArrayList<FirebaseDatabaseGetSet> filterList = new ArrayList<>();
        if (!filterList.isEmpty()) {
            filterList.clear();
        }

        MainActivityLatestArticleFragment latestArticleFragment;
        MostPopularFragment mostPopularFragment;
        MainSubjectChooseFragment mainSubjectChooseFragment;


        latestArticleFragment = (MainActivityLatestArticleFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().getFragments().get(1);
        mostPopularFragment = (MostPopularFragment) getSupportFragmentManager().getFragments().get(0).getChildFragmentManager().getFragments().get(2);

        for (FirebaseDatabaseGetSet firebaseDatabaseGetSet : decidedFilterList) {
            if (firebaseDatabaseGetSet.getTitle().toLowerCase().contains(inputText.toLowerCase())) {
                filterList.add(firebaseDatabaseGetSet);
                if (currentFilterPage == 1) {
                    if (latestArticleFragment != null) {
                        if (!filterList.isEmpty()) {
                            latestArticleFragment.returnFilterList(filterList);
                        }
                    }
                } else if (currentFilterPage == 2) {
                    if (mostPopularFragment != null) {
                        if (!filterList.isEmpty()) {
                            mostPopularFragment.returnFilterList(filterList);
                        }
                    }
                }
            }
        }
    }

    private void LatestSearchFilter(String inputText, ArrayList<FirebaseDatabaseGetSet> decideFilterList){
        ArrayList<FirebaseDatabaseGetSet> filterList = new ArrayList<>();
        if (!filterList.isEmpty()) {
            filterList.clear();
        }

        MainActivityLatestArticleFragment latestArticleFragment;
        latestArticleFragment = (MainActivityLatestArticleFragment) getSupportFragmentManager().findFragmentByTag("MainHomeFragment").getChildFragmentManager().getFragments().get(1);


        for (FirebaseDatabaseGetSet firebaseDatabaseGetSet : decideFilterList) {
            if (firebaseDatabaseGetSet.getTitle().toLowerCase().contains(inputText.toLowerCase()) || firebaseDatabaseGetSet.getMajors().contains(inputText)) {
                filterList.add(firebaseDatabaseGetSet);
                if (!filterList.isEmpty()) {
                    latestArticleFragment.returnFilterList(filterList);
                }
            } else {
                /**
                 * Do something when article is not exist
                 */
            }
        }
    }

    private void MostPopularFilter(String inputText, ArrayList<FirebaseDatabaseGetSet> decideFilterList){
        ArrayList<FirebaseDatabaseGetSet> filterList = new ArrayList<>();
        if (!filterList.isEmpty()) {
            filterList.clear();
        }

        MostPopularFragment mostPopularFragment;
        mostPopularFragment = (MostPopularFragment) getSupportFragmentManager().findFragmentByTag("MainHomeFragment").getChildFragmentManager().getFragments().get(2);

        for (FirebaseDatabaseGetSet firebaseDatabaseGetSet : decideFilterList) {
            if (firebaseDatabaseGetSet.getTitle().toLowerCase().contains(inputText.toLowerCase()) || firebaseDatabaseGetSet.getMajors().contains(inputText)) {
                filterList.add(firebaseDatabaseGetSet);
                if (!filterList.isEmpty()) {
                    mostPopularFragment.returnFilterList(filterList);
                }
            }else {
                /**
                 * Do something when article is not exist
                 */
            }
        }
    }

    private void initializeArrayList() {
        latestArrayList = new ArrayList<>();
        mostPopularArrayList = new ArrayList<>();
    }

    private void InitFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    private void sendEmainVerificationProcess() {
        txtCheckVerified.setText(R.string.not_verified);

        btnCompleteUserInfo.setText("Sending...");
        signUpMethod.emailVarification(firebaseUser, getApplicationContext());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                btnCompleteUserInfo.setText("Click to send email verification");
            }
        }, 2000);
    }

    private void ItmeClick(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (completeInfo.equals("success")) {
                    Intent intent = new Intent(MainActivity.this, EditInitActivity.class);
                    startActivity(intent);
                }else {
                    showUserPostDialog();
                }
            }
        });

        btnCompleteUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RealNameRegisterActivity.class);
                startActivity(intent);
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                fragmentTransaction2.replace(R.id.main_activity_frameLayout, new UserProfileFragment(), "UserProfileFragemnt").commit();
                toolbar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.user_profile_background));
                toolbar.setTitle(getString(R.string.personal_info));
                floatingActionButton.hide();
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public void onSubjectChooseFilter(String subject) {
        if (subject.equals("全部")){
            subject = "";
        }
        LatestSearchFilter(subject, latestArrayList);
        MostPopularFilter(subject, mostPopularArrayList);
        MainActivityTabFragment mainActivityTabFragment = (MainActivityTabFragment) getSupportFragmentManager().getFragments().get(0);
        mainActivityTabFragment.changePage();
    }

    private void showUserPostDialog(){
        UserInfoNotComplete dialogFragment = new UserInfoNotComplete();
        dialogFragment.show(getSupportFragmentManager(), "UserInfoNotComplete");
    }

    private void saveCompleteInfo(String userCompleteInfo){
        SharedPreferences sharedPreferences = this.getSharedPreferences(getString(R.string.keyUserCompleteInfo), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userCompleteInfo", userCompleteInfo);
        editor.apply();
    }

    public static class UserInfoNotComplete extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.user_info_not_complete).setMessage(R.string.complete_user_info_enjoy)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Intent intent = new Intent(getContext(), RealNameRegisterActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }
}
