package com.example.welsenho.questfy_tw.EditActivityRelated;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;

import java.util.ArrayList;

public class EditQuestionRelateRecyclerViewAdapter extends RecyclerView.Adapter<EditQuestionRelateRecyclerViewAdapter.EditQuestionViewHolder> {


    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<String> arrayListTest;
    private Context context;

    public EditQuestionRelateRecyclerViewAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public EditQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_question_relate_major_chose_recycler_layout, viewGroup, false);
        EditQuestionViewHolder viewHolder = new EditQuestionViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final EditQuestionViewHolder viewHolder, int position) {
        FirebaseDatabaseGetSet getSet = arrayList.get(position);
        //Give a default not check
        getSet.setIsSelected(false);
        viewHolder.checkBox.setText(getSet.getMajor());
        if (getSet.getIsSelected()) {
            viewHolder.checkBox.setChecked(true);
        } else {
            viewHolder.checkBox.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class EditQuestionViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        ItemGetMajors itemGetMajors;

        public EditQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.edit_question_recycler_adapter_checkBox);
            arrayListTest = new ArrayList<>();

            /**
             * Determine what major that user has clicked and use LocalBroadCast Manager to broadCase to select activity
             */
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (arrayListTest.size() >= 3) {
                        if (checkBox.isChecked()) {
                            buttonView.setChecked(false);
                            Toast.makeText(EditQuestionRelateRecyclerViewAdapter.this.context, "MAX 3", Toast.LENGTH_SHORT).show();
                        } else if (!checkBox.isChecked()) {
                            arrayListTest.remove(checkBox.getText().toString());
                        }
                    } else {
                        if (checkBox.isChecked()) {
                            buttonView.setChecked(true);
                            arrayListTest.add(checkBox.getText().toString());
                        } else if (!checkBox.isChecked()) {
                            arrayListTest.remove(checkBox.getText().toString());
                        }
                    }
                    Intent intent = new Intent("Custom_message");
                    intent.putExtra("Majors", arrayListTest);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });

        }
    }


}
