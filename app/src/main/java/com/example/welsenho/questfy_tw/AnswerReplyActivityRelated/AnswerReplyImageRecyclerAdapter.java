package com.example.welsenho.questfy_tw.AnswerReplyActivityRelated;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditInitRelateRecyclerViewAdapterImageViews;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AnswerReplyImageRecyclerAdapter extends RecyclerView.Adapter<AnswerReplyImageRecyclerAdapter.ViewHolder>{


    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;

    public AnswerReplyImageRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_init_relate_images_view_recycler_layout, viewGroup, false);
        AnswerReplyImageRecyclerAdapter.ViewHolder viewHolder = new AnswerReplyImageRecyclerAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picasso.get().load(firebaseDatabaseGetSet.getEditInitImageUploadViewUri()).fit().into(viewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.edit_init_recycler_adapter_ImageView);
        }
    }
}
