package com.example.welsenho.questfy_tw.MainUserActivity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.welsenho.questfy_tw.InternetConnectionDetect;
import com.example.welsenho.questfy_tw.LoginRelated.LoginActivity;
import com.example.welsenho.questfy_tw.R;

public class WelcomeScreenActivity extends AppCompatActivity {

    private TextView txtWelcome;
    private Animation animation;
    private InternetConnectionDetect internetConnectionDetect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        txtWelcome = findViewById(R.id.welcome_screen_txtLogo);
        animation = AnimationUtils.loadAnimation(this, R.anim.starting_animation);
        txtWelcome.startAnimation(animation);
        internetConnectionDetect = new InternetConnectionDetect();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (internetConnectionDetect.isNetworkAvailable(getApplicationContext())) {
                    Intent intent = new Intent(WelcomeScreenActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(WelcomeScreenActivity.this, OutOfConnectionActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);

    }
}
