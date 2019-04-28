package com.example.welsenho.questfy_tw.PersonAskQuestionRelated;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PersonalAskRecyclerAdapter extends RecyclerView.Adapter<PersonalAskRecyclerAdapter.PersonalAskViewHolder> {

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private getQuestionUid clickListener;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;

    public PersonalAskRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, getQuestionUid clickListener){
        this.arrayList = arrayList;
        this.context = context;
        this.clickListener = clickListener;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
    }

    @NonNull
    @Override
    public PersonalAskViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =LayoutInflater.from(context).inflate(R.layout.personal_ask_recycler_layout, viewGroup, false);
        PersonalAskViewHolder viewHolder = new PersonalAskViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PersonalAskViewHolder personalAskViewHolder, int i) {
        FirebaseDatabaseGetSet firebaseDatabaseGetSet = arrayList.get(i);
        if (firebaseDatabaseGetSet.getAskerUid().equals(firebaseUser.getUid())){
            String questionUser = "向" + firebaseDatabaseGetSet.getAnswerName() + "提問的問題";
            personalAskViewHolder.txtAskUserName.setText(questionUser);
        }else {
            String questionUser = "來自" + firebaseDatabaseGetSet.getAskerName() + "的提問";
            personalAskViewHolder.txtAskUserName.setText(questionUser);
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

        public PersonalAskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAskUserName = itemView.findViewById(R.id.pesonal_ask_recycler_txtUserFrom);
            txtAskTitle = itemView.findViewById(R.id.pesonal_ask_recycler_txtQuestionContent);
            txtAskDate = itemView.findViewById(R.id.pesonal_ask_recycler_txtUserAskDate);
            imgAsk = itemView.findViewById(R.id.pesonal_ask_recycler_imgQuestionImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        clickListener.QuestionClick(position);
                    }
                }
            });
        }
    }

    public interface getQuestionUid{
        void QuestionClick(int position);
    }
}
