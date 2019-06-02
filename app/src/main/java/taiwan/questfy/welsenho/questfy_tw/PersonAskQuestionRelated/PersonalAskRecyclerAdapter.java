package taiwan.questfy.welsenho.questfy_tw.PersonAskQuestionRelated;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class PersonalAskRecyclerAdapter extends RecyclerView.Adapter<PersonalAskRecyclerAdapter.PersonalAskViewHolder> {

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private getQuestionUid clickListener;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public PersonalAskRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, getQuestionUid clickListener){
        this.arrayList = arrayList;
        this.context = context;
        this.clickListener = clickListener;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public PersonalAskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =LayoutInflater.from(context).inflate(R.layout.personal_ask_recycler_layout, viewGroup, false);
        PersonalAskViewHolder viewHolder = new PersonalAskViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PersonalAskViewHolder personalAskViewHolder, int i) {
        FirebaseDatabaseGetSet firebaseDatabaseGetSet = arrayList.get(i);
        if (firebaseDatabaseGetSet.getAskerUid().equals(firebaseUser.getUid())){
            String questionUser = "向" + firebaseDatabaseGetSet.getAnswerName() + "提問的問題";
            personalAskViewHolder.txtAskUserName.setText(questionUser);
            databaseReference.child("Users_profile").child(firebaseDatabaseGetSet.getAnswerUid()).child("User_image_uri").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Picasso.get().load(dataSnapshot.getValue().toString()).fit().into(personalAskViewHolder.circleImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else {
            String questionUser = "來自" + firebaseDatabaseGetSet.getAskerName() + "的提問";
            personalAskViewHolder.txtAskUserName.setText(questionUser);
            databaseReference.child("Users_profile").child(firebaseDatabaseGetSet.getAskerUid()).child("User_image_uri").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Picasso.get().load(dataSnapshot.getValue().toString()).fit().into(personalAskViewHolder.circleImageView);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        personalAskViewHolder.txtAskTitle.setText(firebaseDatabaseGetSet.getAskQuestionContent());
        personalAskViewHolder.txtAskDate.setText(firebaseDatabaseGetSet.getAskDate());
        if (firebaseDatabaseGetSet.getQuestionTumbnail() != null) {
            Picasso.get().load(firebaseDatabaseGetSet.getQuestionTumbnail()).fit().into(personalAskViewHolder.imgAsk);
        }else {
            personalAskViewHolder.imgAsk.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class PersonalAskViewHolder extends RecyclerView.ViewHolder {

        private TextView txtAskUserName;
        private TextView txtAskTitle;
        private TextView txtAskDate;
        private ImageView imgAsk;
        private CircleImageView circleImageView;

        public PersonalAskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAskUserName = itemView.findViewById(R.id.pesonal_ask_recycler_txtUserFrom);
            txtAskTitle = itemView.findViewById(R.id.pesonal_ask_recycler_txtQuestionContent);
            txtAskDate = itemView.findViewById(R.id.pesonal_ask_recycler_txtUserAskDate);
            imgAsk = itemView.findViewById(R.id.pesonal_ask_recycler_imgQuestionImage);
            circleImageView = itemView.findViewById(R.id.pesonal_ask_recycler_imgUserImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        clickListener.QuestionClick(position);
                    }
                }
            });

            imgAsk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        clickListener.OnImageClick(arrayList.get(position).getQuestionTumbnail());
                    }
                }
            });

            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        clickListener.OnUserImageClick(position, arrayList);
                    }
                }
            });
        }
    }

    public interface getQuestionUid{
        void QuestionClick(int position);
        void OnImageClick(String imgUrl);
        void OnUserImageClick(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
