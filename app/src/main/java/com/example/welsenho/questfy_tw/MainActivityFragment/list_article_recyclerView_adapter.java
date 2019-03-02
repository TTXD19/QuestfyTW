package com.example.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class list_article_recyclerView_adapter extends RecyclerView.Adapter<list_article_recyclerView_adapter.ViewHolder> implements MainAdapterOnClickListener{

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private MainOnClickListener mainOnClickListener;

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
        FirebaseDatabaseGetSet getSet = arrayList.get(i);
        viewHolder.txtUserName.setText(getSet.getUser_Name());
        viewHolder.txtTitle.setText(getSet.getTitle());
        viewHolder.txtUploadDate.setText(getSet.getUpload_Date());
        viewHolder.txtMajors.setText(getSet.getMajors());
        viewHolder.txtArticlePreview.setText(getSet.getContent());
        viewHolder.txtArticleCount.setText(String.valueOf(Math.abs(getSet.getArticle_like_count())));
        Picasso.get().load(getSet.getUser_Image()).into(viewHolder.circleImageViewUserImage);
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
        private CircleImageView circleImageViewUserImage;

        public ViewHolder(@NonNull View itemView, final MainAdapterOnClickListener mOnClickListener) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.list_article_recycLayout_txtArticleName);
            txtMajors = itemView.findViewById(R.id.list_article_recycLayout_txtTag);
            txtUploadDate = itemView.findViewById(R.id.list_article_recycLayout_txtUploadData);
            txtUserName  = itemView.findViewById(R.id.list_article_recycLayout_txtUserName);
            txtArticlePreview = itemView.findViewById(R.id.list_article_recycLayout_txtArticlePreview);
            txtArticleCount = itemView.findViewById(R.id.list_article_recycLayout_txtViews);
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
