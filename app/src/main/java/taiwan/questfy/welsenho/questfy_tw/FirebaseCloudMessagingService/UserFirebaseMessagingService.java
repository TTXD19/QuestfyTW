package taiwan.questfy.welsenho.questfy_tw.FirebaseCloudMessagingService;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import taiwan.questfy.welsenho.questfy_tw.MainUserActivity.MainActivity;
import taiwan.questfy.welsenho.questfy_tw.R;
import taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated.ReadArticleActivity;

public class UserFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("USERCLOUDMESSAGING", s);
    }


    //When app is in foreground this method will activate
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("USERCLOUDMESSAGING", remoteMessage.getFrom());
        Log.d("USERCLOUDMESSAGING", remoteMessage.getNotification().getBody());
        //Log.d("USERCLOUDMESSAGING", remoteMessage.getData().get("ArticleID"));
        //Log.d("USERCLOUDMESSAGING", remoteMessage.getData().get("UserUid"));


        int requestID = (int) System.currentTimeMillis();
        //Detect whether notify with a article ID or not
        Intent intent;
        if (remoteMessage.getData().get("ArticleID") != null){
            intent = new Intent(this, ReadArticleActivity.class);
            intent.putExtra("ArticleID", remoteMessage.getData().get("ArticleID"));
        }else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Questfy台灣")
                .setContentText(remoteMessage.getNotification().getBody())
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.questfy_logo_style_5_4_round))
                .setSmallIcon(R.drawable.edit_pencil)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(requestID, notification);
    }
}
