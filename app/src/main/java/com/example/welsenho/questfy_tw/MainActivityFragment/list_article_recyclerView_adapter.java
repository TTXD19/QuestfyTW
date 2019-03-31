package com.example.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class list_article_recyclerView_adapter extends RecyclerView.Adapter<list_article_recyclerView_adapter.ViewHolder> implements MainAdapterOnClickListener{

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private MainOnClickListener mainOnClickListener;
    private EditRelatedMethod editRelatedMethod;

    public list_article_recyclerView_adapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.article_list_recyclerview_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        editRelatedMethod = new EditRelatedMethod();
        FirebaseDatabaseGetSet getSet = arrayList.get(i);
            viewHolder.txtUserName.setText(getSet.getUser_Name());
            viewHolder.txtTitle.setText(getSet.getTitle());
            viewHolder.txtUploadDate.setText(editRelatedMethod.getFormattedDate(context, Math.abs(getSet.getUploadTimeStamp())));
            viewHolder.txtMajors.setText(getSet.getMajors());
            viewHolder.txtArticlePreview.setText(getSet.getContent());
            viewHolder.txtArticleCount.setText(String.valueOf(Math.abs(getSet.getArticle_like_count())));
            if (getSet.getIsMeet().equals("Meet")){
                viewHolder.txtMeetUp.setVisibility(View.VISIBLE);
                viewHolder.imgMeetUp.setVisibility(View.VISIBLE);
            }else{
                viewHolder.txtMeetUp.setVisibility(View.GONE);
                viewHolder.imgMeetUp.setVisibility(View.GONE);
            }
            if (getSet.getAnswerCount() != 0) {
                viewHolder.txtAnserCount.setText(String.valueOf(getSet.getAnswerCount()));
            }else {
                viewHolder.txtAnserCount.setText(String.valueOf(0));
            }
            Picasso.get().load(getSet.getUser_Image()).fit().into(viewHolder.circleImageViewUserImage);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public void setOnMainClickListener(MainOnClickListener mainClickListener){
        this.mainOnClickListener = mainClickListener;
    }

    @Override
    public void onClick(int position) {
        mainOnClickListener.onClicked(position, arrayList);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserName;
        private TextView txtTitle;
        private TextView txtUploadDate;
        private TextView txtMajors;
        private TextView txtArticlePreview;
        private TextView txtArticleCount;
        private TextView txtAnserCount;
        private TextView txtMeetUp;
        private ImageView imgMeetUp;
        private CircleImageView circleImageViewUserImage;

        public ViewHolder(@NonNull View itemView, final MainAdapterOnClickListener mOnClickListener) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.list_article_recycLayout_txtArticleName);
            txtMajors = itemView.findViewById(R.id.list_article_recycLayout_txtTag);
            txtUploadDate = itemView.findViewById(R.id.list_article_recycLayout_txtUploadData);
            txtUserName  = itemView.findViewById(R.id.list_article_recycLayout_txtUserName);
            txtArticlePreview = itemView.findViewById(R.id.list_article_recycLayout_txtArticlePreview);
            txtArticleCount = itemView.findViewById(R.id.list_article_recycLayout_txtViews);
            txtAnserCount = itemView.findViewById(R.id.list_article_recycLayout_txtAnswerCount);
            txtMeetUp = itemView.findViewById(R.id.list_article_recycLayout_txtWantMeetUp);
            imgMeetUp = itemView.findViewById(R.id.list_article_recycLayout_imgWantMeetUp);
            circleImageViewUserImage = itemView.findViewById(R.id.list_article_recycLayout_circleUserImage);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        mOnClickListener.onClick(position);
                    }
                }
            });
        }
    }
}
