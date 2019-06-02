package taiwan.questfy.welsenho.questfy_tw.AppAnnouncementRelated;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class AppAnnouncementActivity extends AppCompatActivity {

    private TextView txtAnnouncement;
    private TextView txtRegisterCount;
    private TextView txtQuestionsCount;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_announcement);


        txtAnnouncement = findViewById(R.id.app_announcement_txt);
        txtRegisterCount = findViewById(R.id.add_announcement_txtRegisterCount);
        txtQuestionsCount = findViewById(R.id.add_announcement_txtQuestionsCount);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();


        databaseReference.child("AppAnnouncement").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    firebaseDatabaseGetSet = dataSnapshot.getValue(FirebaseDatabaseGetSet.class);
                    txtAnnouncement.setText(firebaseDatabaseGetSet.getAnnouncement());
                    txtRegisterCount.setText(String.valueOf(firebaseDatabaseGetSet.getRegisterCount()));
                    txtQuestionsCount.setText(String.valueOf(firebaseDatabaseGetSet.getQuestionsCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
