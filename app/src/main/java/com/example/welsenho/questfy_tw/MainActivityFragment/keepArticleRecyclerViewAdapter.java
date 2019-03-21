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
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class keepArticleRecyclerViewAdapter extends RecyclerView.Adapter<keepArticleRecyclerViewAdapter.keepArticlesViewHolder> implements MainAdapterOnClickListener{

    private Context context;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private MainOnClickListener onClickListener;

    public keepArticleRecyclerViewAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    public void setOnClickListener(MainOnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }


    @NonNull
    @Override
    public keepArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.keep_articles_list_recyclerview_layout, viewGroup, false);
        keepArticlesViewHolder viewHolder = new keepArticlesViewHolder(view, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull keepArticlesViewHolder keepArticlesViewHolder, int i) {
        firebaseDatabaseGetSet = arrayList.get(i);

        keepArticlesViewHolder.txtUserName.setText(firebaseDatabaseGetSet.getUser_Name());
        keepArticlesViewHolder.txtTitle.setText(firebaseDatabaseGetSet.getTitle());
        keepArticlesViewHolder.txtUploadDate.setText(firebaseDatabaseGetSet.getUpload_Date());
        keepArticlesViewHolder.txtContent.setText(firebaseDatabaseGetSet.getContent());
        keepArticlesViewHolder.txtCourseName.setText(firebaseDatabaseGetSet.getMajors());
        if (firebaseDatabaseGetSet.getUser_Image() != null) {
            Picasso.get().load(firebaseDatabaseGetSet.getUser_Image()).fit().into(keepArticlesViewHolder.circleImageView);
        }

        keepArticlesViewHolder.shineButton.setChecked(true);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onClick(int position) {
        onClickListener.onClicked(position, arrayList);
    }

    public class keepArticlesViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTitle;
        private TextView txtUserName;
        private TextView txtCourseName;
        private TextView txtContent;
        private TextView txtUploadDate;
        private ShineButton shineButton;
        private CircleImageView circleImageView;

        public keepArticlesViewHolder(@NonNull View itemView, final MainAdapterOnClickListener adapterOnClickListener) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.keep_articles_recycLayout_txtArticleName);
            txtUserName = itemView.findViewById(R.id.keep_articles_recycLayout_txtUserName);
            txtCourseName = itemView.findViewById(R.id.keep_articles_recycLayout_txtTag);
            txtContent = itemView.findViewById(R.id.keep_articles_recycLayout_txtArticlePreview);
            txtUploadDate = itemView.findViewById(R.id.keep_articles_recycLayout_txtUploadData);
            shineButton = itemView.findViewById(R.id.keep_articles_recycLayout_btnHeart);
            circleImageView = itemView.findViewById(R.id.keep_articles_recycLayout_circleUserImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        adapterOnClickListener.onClick(position);
                    }
                }
            });
        }
    }
}
