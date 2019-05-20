package taiwan.questfy.welsenho.questfy_tw.MainUserActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;


import taiwan.questfy.welsenho.questfy_tw.AppIntroRelated.AppIntroActivity;
import taiwan.questfy.welsenho.questfy_tw.InternetConnectionDetect;
import taiwan.questfy.welsenho.questfy_tw.LoginRelated.LoginActivity;
import taiwan.questfy.welsenho.questfy_tw.R;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;

public class WelcomeScreenActivity extends AppCompatActivity {

    private TextView txtWelcome;
    private Animation animation;
    private InternetConnectionDetect internetConnectionDetect;
    private SharedPreferences sharedPreferences;
    private Boolean firstOpen;
    private String ArticleID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);


        txtWelcome = findViewById(R.id.welcome_screen_txtLogo);
        animation = AnimationUtils.loadAnimation(this, R.anim.starting_animation);
        txtWelcome.startAnimation(animation);
        internetConnectionDetect = new InternetConnectionDetect();


        sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        firstOpen = sharedPreferences.getBoolean("FirstOpen", true);


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("FirstOpen", false);
        editor.apply();

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            ArticleID = bundle.getString("ArticleID");
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (internetConnectionDetect.isNetworkAvailable(getApplicationContext())) {
                    if (firstOpen) {
                        Intent intent = new Intent(WelcomeScreenActivity.this, AppIntroActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent;
                        //Detect if this a answerReplay notification
                        if (ArticleID != null) {
                            intent = new Intent(WelcomeScreenActivity.this, ReadArticleActivity.class);
                            intent.putExtra("ArticleID", ArticleID);
                            intent.putExtra("Notification", true);
                            startActivity(intent);
                            finish();
                        }else {
                            intent = new Intent(WelcomeScreenActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } else {
                    Intent intent = new Intent(WelcomeScreenActivity.this, OutOfConnectionActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 4000);

    }
}
