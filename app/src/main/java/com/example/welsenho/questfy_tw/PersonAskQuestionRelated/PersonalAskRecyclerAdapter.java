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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PersonalAskRecyclerAdapter extends RecyclerView.Adapter<PersonalAskRecyclerAdapter.PersonalAskViewHolder> {

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private getQuestionUid clickListener;

    public PersonalAskRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, getQuestionUid clickListener){
        this.arrayList = arrayList;
        this.context = context;
        this.clickListener = clickListener;
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
        String questionUser = "Question from " + firebaseDatabaseGetSet.getAskerName();
        personalAskViewHolder.txtAskUserName.setText(questionUser);
        personalAskViewHolder.txtAskTitle.setText(firebaseDatabaseGetSet.getAskQuestionContent());
        personalAskViewHolder.txtAskDate.setText(firebaseDatabaseGetSet.getAskDate());
        Picasso.get().load(firebaseDatabaseGetSet.getQuestionTumbnail()).fit().into(personalAskViewHolder.imgAsk);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class PersonalAskViewHolder extends RecyclerView.ViewHolder {

        private TextView txtAskUserName;
        private TextView txtAskTitle;
        private TextView txtAskDate;
        private TextView txtAskSolver;
        private ImageView imgAsk;

        public PersonalAskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAskUserName = itemView.findViewById(R.id.pesonal_ask_recycler_txtUserFrom);
            txtAskTitle = itemView.findViewById(R.id.pesonal_ask_recycler_txtQuestionContent);
            txtAskDate = itemView.findViewById(R.id.pesonal_ask_recycler_txtUserAskDate);
            txtAskSolver = itemView.findViewById(R.id.pesonal_ask_recycler_txtUserSolveIt);
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