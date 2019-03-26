package com.example.welsenho.questfy_tw.EditActivityRelated;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;

import java.util.ArrayList;

public class EditInitMajorChooseConfirmRecyclerAdapter extends RecyclerView.Adapter<EditInitMajorChooseConfirmRecyclerAdapter.EditInitMajorConfirmViewHolder>{

    private ArrayList<String> arrayList;
    private FirebaseDatabaseGetSet getSet;
    private Context context;

    public EditInitMajorChooseConfirmRecyclerAdapter(ArrayList<String> arrayList){
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public EditInitMajorConfirmViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_init_major_choose_confirm_recycler_layout, viewGroup, false);
        EditInitMajorConfirmViewHolder viewHolder = new EditInitMajorConfirmViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EditInitMajorConfirmViewHolder editInitMajorConfirmViewHolder, int i) {
       editInitMajorConfirmViewHolder.txtMajor.setText(arrayList.get(i));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class EditInitMajorConfirmViewHolder extends RecyclerView.ViewHolder {

        private TextView txtMajor;

        public EditInitMajorConfirmViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMajor = itemView.findViewById(R.id.edit_int_major_confirm_txtMajor);
        }
    }
}
