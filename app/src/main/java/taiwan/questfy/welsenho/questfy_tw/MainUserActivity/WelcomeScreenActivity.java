package taiwan.questfy.welsenho.questfy_tw.MainUserActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import taiwan.questfy.welsenho.questfy_tw.AppIntroRelated.AppIntroActivity;
import taiwan.questfy.welsenho.questfy_tw.InternetConnectionDetect;
import taiwan.questfy.welsenho.questfy_tw.LoginRelated.LoginActivity;
import taiwan.questfy.welsenho.questfy_tw.PersonAskQuestionRelated.PersonalAskQuestReplyActivity;
import taiwan.questfy.welsenho.questfy_tw.R;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;

public class WelcomeScreenActivity extends AppCompatActivity {

    private TextView txtWelcome;
    private Animation animation;
    private InternetConnectionDetect internetConnectionDetect;
    private SharedPreferences sharedPreferences;
    private Boolean firstOpen;
    private String ArticleID = null;
    private String personalAskId = null;

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
            for (String key : bundle.keySet()) {
                String keyBundle = "";
                keyBundle += " " + key + " => " + bundle.get(key) + ";";
                //Log.d("GETBUNDLE", keyBundle);
            }
            if (bundle.getString("ArticleID") != null) {
                ArticleID = bundle.getString("ArticleID");
                //Toast.makeText(this, "ArticleID", Toast.LENGTH_SHORT).show();

            }else if (bundle.getString("questionUid") != null){
                personalAskId = bundle.getString("questionUid");
                //Toast.makeText(this, "questionUid", Toast.LENGTH_SHORT).show();

            }
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
                            Log.d("NOTIFICATIONID", ArticleID);
                            intent = new Intent(WelcomeScreenActivity.this, ReadArticleActivity.class);
                            intent.putExtra("ArticleID", ArticleID);
                            intent.putExtra("Notification", true);
                            startActivity(intent);
                            finish();
                        }else if (personalAskId != null){
                            Boolean notificationOpen = true;
                            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            intent = new Intent(WelcomeScreenActivity.this, PersonalAskQuestReplyActivity.class);
                            intent.putExtra("questionUid", personalAskId);
                            intent.putExtra("questioType", "AskTo");
                            intent.putExtra("AskerUid", firebaseUser.getUid());
                            intent.putExtra("notificationOpen", notificationOpen);
                            startActivity(intent);
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
