package com.example.welsenho.questfy_tw.AnswerReplyActivityRelated;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.welsenho.questfy_tw.R;
import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

public class EnlargeAnswerImageActivity extends AppCompatActivity {

    private String imageUri;
    private ZoomageView zoomageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enlarge_answer_image);

        zoomageView = findViewById(R.id.enlargeAnswerImage);

        imageUri = getIntent().getStringExtra("imageUri");
        Picasso.get().load(imageUri).fit().into(zoomageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
