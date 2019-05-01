package com.example.welsenho.questfy_tw.AnswerReplyActivityRelated;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class readAnswersRecyclerViewAdapter extends RecyclerView.Adapter<readAnswersRecyclerViewAdapter.readAnswerViewHolder>{

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private AnswerImage answerImage;

    public readAnswersRecyclerViewAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, AnswerImage answerImage){
        this.arrayList = arrayList;
        this.context = context;
        this.answerImage = answerImage;
    }


    @NonNull
    @Override
    public readAnswerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.read_answer_recycler_layout, viewGroup, false);
        readAnswerViewHolder readAnswerViewHolder = new readAnswerViewHolder(view);
        return readAnswerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull readAnswerViewHolder readAnswerViewHolder, int i) {
        FirebaseDatabaseGetSet getSet = arrayList.get(i);
        readAnswerViewHolder.txtUserName.setText(getSet.getUserName());
        readAnswerViewHolder.txtUpdateDate.setText(getSet.getUpdateDate());
        readAnswerViewHolder.txtAnswerContent.setText(getSet.getAnswerContent());
        Picasso.get().load(getSet.getUserImage()).error(R.drawable.user_default_image).fit().into(readAnswerViewHolder.userCircleImageView);
        Picasso.get().load(getSet.getUserImage()).error(R.drawable.user_default_image).fit().into(readAnswerViewHolder.userCircleImageView);
        if (getSet.getEditInitImageUploadViewUri() != null){
            Picasso.get().load(getSet.getEditInitImageUploadViewUri()).error(R.color.FullWhite).fit().into(readAnswerViewHolder.imgAnswer);
        }else {
            readAnswerViewHolder.imgAnswer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class readAnswerViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserName;
        private TextView txtUpdateDate;
        private CircleImageView userCircleImageView;
        private TextView txtAnswerContent;
        private ImageView imgAnswer;

        public readAnswerViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUserName = itemView.findViewById(R.id.recycler_layout_read_answers_txtUserName);
            txtUpdateDate = itemView.findViewById(R.id.recycler_layout_read_answers_txtUpdateDate);
            txtAnswerContent = itemView.findViewById(R.id.recycler_layout_read_answers_txtAnswerContent);
            userCircleImageView = itemView.findViewById(R.id.recycler_layout_read_answers_userImage);
            imgAnswer = itemView.findViewById(R.id.recycler_layout_read_answers_imgAnswer);

            imgAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        answerImage.getImage(arrayList.get(position).getEditInitImageUploadViewUri());
                    }
                }
            });
        }
    }

    public interface AnswerImage{
        void getImage(String imageUri);
    }


}
