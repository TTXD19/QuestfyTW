package com.example.welsenho.questfy_tw.EditActivityRelated;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditRelatedMethod {


    /**
     * Below is for user taking an instant photo getSet is for returning photo instant uri
     * step by strp
     */
    private String mCurrentPhotoPath;
    private String articleUid;
    private int countImages;

    public File createImageFile(Context context) throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public String getmCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }

    public void setmCurrentPhotoPath(String mCurrentPhotoPath) {
        this.mCurrentPhotoPath = mCurrentPhotoPath;
    }



    public String getDate(){
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
        Date myDate = new Date();
        String filename = timeStampFormat.format(myDate);
        return filename;
    }

    public String getUploadDate(){
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date myDate = new Date();
        return timeStampFormat.format(myDate);
    }






    public void saveCurrent(EditText editTitle, EditText editContent, Activity activity, String articleUid, int CountImages){
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Title", title);
        editor.putString("Content", content);
        editor.putString("ArticleUid", articleUid);
        editor.putInt("CountImage", CountImages);
        editor.apply();
    }



    public void showCurrent(EditText editTitle, EditText editContent, Activity activity){
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        articleUid = sharedPreferences.getString("ArticleUid", null);
        countImages = sharedPreferences.getInt("CountImage", 0);
        editTitle.setText(sharedPreferences.getString("Title", "Title"));
        editContent.setText(sharedPreferences.getString("Content", "Description"));
    }

    public void DeleteCurrent(Activity activity){
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().apply();
    }

    public String getArticleUid() {
        return articleUid;
    }

    public void setArticleUid(String articleUid) {
        this.articleUid = articleUid;
    }

    public int getCountImages() {
        return countImages;
    }

    public void setCountImages(int countImages) {
        this.countImages = countImages;
    }
}
