package taiwan.questfy.welsenho.questfy_tw.EditActivityRelated;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.florent37.expansionpanel.ExpansionHeader;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class EditRelatedMethod {


    /**
     * Below is for user taking an instant photo getSet is for returning photo instant uri
     * step by step
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


    /**
     * get current date for the article
     * @return
     */
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

    public void LoadImageFromFirebase(DatabaseReference databaseReference, String ArticleUid, final ArrayList<FirebaseDatabaseGetSet> arrayList, final ExpansionHeader expansionHeader, final RecyclerView recyclerView,
    final EditInitRelateRecyclerViewAdapterImageViews adapterImageViews, final TextView txtImageView){
        databaseReference.child("Users_Question_Articles").child(ArticleUid).child("Images").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                arrayList.clear();
                if (dataSnapshot.exists()){
                    arrayList.clear();
                    expansionHeader.setVisibility(View.VISIBLE);
                    txtImageView.setText("Loading Image...");
                    for (DataSnapshot DS : dataSnapshot.getChildren()){
                        FirebaseDatabaseGetSet firebaseDatabaseGetSet = DS.getValue(FirebaseDatabaseGetSet.class);
                        arrayList.add(firebaseDatabaseGetSet);
                        recyclerView.setAdapter(adapterImageViews);
                        txtImageView.setText(R.string.click_to_check);
                        Log.d("RecyclerViewTask", "Success");
                    }
                }else {
                    expansionHeader.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public String getFormattedDate(Context context, long smsTimeInMilis) {
        Calendar smsTime = Calendar.getInstance();
        smsTime.setTimeInMillis(smsTimeInMilis);

        Calendar now = Calendar.getInstance();

        final String timeFormatString = "h:mm aa";
        final String dateTimeFormatString = "EEEE, MMMMd" + "日" + " yyyy";
        final long HOURS = 60 * 60 * 60;
        if (now.get(Calendar.DATE) == smsTime.get(Calendar.DATE) ) {
            return context.getString(R.string.today) + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return context.getString(R.string.yesterday) + DateFormat.format(timeFormatString, smsTime);
        } else if (now.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }
}
