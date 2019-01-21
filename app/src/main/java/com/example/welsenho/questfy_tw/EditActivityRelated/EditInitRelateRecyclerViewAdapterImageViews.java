package com.example.welsenho.questfy_tw.EditActivityRelated;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EditInitRelateRecyclerViewAdapterImageViews extends RecyclerView.Adapter<EditInitRelateRecyclerViewAdapterImageViews.ViewHolder> {

    private ArrayList<FirebaseDatabaseGetSet> arrayList;

    public EditInitRelateRecyclerViewAdapterImageViews(ArrayList<FirebaseDatabaseGetSet> arrayList){
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_init_relate_images_view_recycler_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        FirebaseDatabaseGetSet getSet = arrayList.get(i);
        Picasso.get().load(getSet.getEditInitImageUploadViewUri()).resize(400,380).into(viewHolder.imgViews);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgViews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgViews = itemView.findViewById(R.id.edit_init_recycler_adapter_ImageView);
        }
    }
}
