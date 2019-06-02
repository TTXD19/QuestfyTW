package taiwan.questfy.welsenho.questfy_tw.PersonAskQuestionRelated;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class PersonalAskQuestReplyRecyclerAdapter extends RecyclerView.Adapter<PersonalAskQuestReplyRecyclerAdapter.personQuestViewHolder> {

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private EditRelatedMethod editRelatedMethod;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public PersonalAskQuestReplyRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public personQuestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.personal_ask_quest_reply_recycler_layout, viewGroup, false);
        personQuestViewHolder viewHolder = new personQuestViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final personQuestViewHolder personQuestViewHolder, int i) {
        editRelatedMethod = new EditRelatedMethod();
        firebaseDatabaseGetSet = arrayList.get(i);
        String title = "來自" + firebaseDatabaseGetSet.getUser_Name() + "的回覆";
        personQuestViewHolder.txtAskUserName.setText(title);
        personQuestViewHolder.txtAskContent.setText(firebaseDatabaseGetSet.getContent());
        personQuestViewHolder.txtAskDate.setText(editRelatedMethod.getFormattedDate(context, Math.abs(firebaseDatabaseGetSet.getUploadTimeStamp())));
        if (firebaseDatabaseGetSet.getQuestionTumbnail() != null) {
            Picasso.get().load(firebaseDatabaseGetSet.getQuestionTumbnail()).fit().into(personQuestViewHolder.imgAsk);
        }else {
            personQuestViewHolder.imgAsk.setVisibility(View.GONE);
        }

        databaseReference.child("Users_profile").child(firebaseDatabaseGetSet.getUserUid()).child("User_image_uri").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Picasso.get().load(dataSnapshot.getValue().toString()).fit().into(personQuestViewHolder.circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class personQuestViewHolder extends RecyclerView.ViewHolder {

        private TextView txtAskUserName;
        private TextView txtAskContent;
        private TextView txtAskDate;
        private ImageView imgAsk;
        private CircleImageView circleImageView;

        public personQuestViewHolder(@NonNull View itemView) {
            super(itemView);

            txtAskUserName = itemView.findViewById(R.id.personal_ask_quest_reply_recycler_txtUserFrom);
            txtAskContent = itemView.findViewById(R.id.personal_ask_quest_reply_recycler_txtQuestionContent);
            txtAskDate = itemView.findViewById(R.id.personal_ask_quest_reply_recycler_txtUserAskDate);
            imgAsk = itemView.findViewById(R.id.personal_ask_quest_reply_recycler_imgQuestionImage);
            circleImageView = itemView.findViewById(R.id.personal_ask_quest_reply_recycler_imgUserImage);
        }
    }
}
