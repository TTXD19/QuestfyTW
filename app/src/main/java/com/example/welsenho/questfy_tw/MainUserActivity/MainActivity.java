package com.example.welsenho.questfy_tw.MainUserActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditInitActivity;
import com.example.welsenho.questfy_tw.LoginRelated.LoginActivity;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements MainActivityTabFragment.OnFragmentInteractionListener,
    MainActivityLatestArticleFragment.OnFragmentInteractionListener, UserProfileFragment.OnFragmentInteractionListener,
    MostPopularFragment.OnFragmentInteractionListener{

    private Boolean doubeTapExit = false;

    private TextView txtID;
    private TextView txtEmail;
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
        //----------------------------------------------------


        mainActivityMethods = new MainActivityMethods();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        txtID.setText(firebaseUser.getDisplayName());
        txtEmail.setText(firebaseUser.getEmail());


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



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_serarch_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //do nothing
    }

    public void navigationClick(){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case 0: drawerLayout.openDrawer(GravityCompat.START);
                        break;

                    case R.id.main_activity_tab_home:
                        FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                        fragmentTransaction1.replace(R.id.main_activity_frameLayout, new MainActivityTabFragment()).commit();
                        toolbar.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.ＭainOrange));
                        break;

                    case R.id.User_profile:
                        FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                        fragmentTransaction2.replace(R.id.main_activity_frameLayout, new UserProfileFragment()).commit();
                        toolbar.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.user_profile_background));
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

    public void mainActivityButtonClick(){
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
                doubeTapExit =  false;
            }
        }, 2000);
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
    }
}
