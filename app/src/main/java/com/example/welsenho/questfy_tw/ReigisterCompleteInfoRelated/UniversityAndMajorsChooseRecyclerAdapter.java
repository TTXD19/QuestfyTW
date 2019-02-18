package com.example.welsenho.questfy_tw.ReigisterCompleteInfoRelated;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.EditActivityRelated.EditQuestionRelateRecyclerViewAdapter;
import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class UniversityAndMajorsChooseRecyclerAdapter extends RecyclerView.Adapter<UniversityAndMajorsChooseRecyclerAdapter.UniversityAndMajorsViewHolder> {


    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<String> arrayListReturnCheck;
    private Context context;

    public UniversityAndMajorsChooseRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public UniversityAndMajorsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.university_and_majors_recycler_layout, viewGroup, false);
        UniversityAndMajorsViewHolder viewHolder = new UniversityAndMajorsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UniversityAndMajorsViewHolder universityAndMajorsViewHolder, int i) {
        FirebaseDatabaseGetSet getSet = arrayList.get(i);
        universityAndMajorsViewHolder.checkBoxUniversity.setText(getSet.getSchoolName());
        getSet.setIsSelected(false);
        if (getSet.getIsSelected()) {
            universityAndMajorsViewHolder.checkBoxUniversity.setChecked(true);
        } else {
            universityAndMajorsViewHolder.checkBoxUniversity.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class UniversityAndMajorsViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBoxUniversity;

        public UniversityAndMajorsViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBoxUniversity = itemView.findViewById(R.id.uniAndMajor_Recycler_CheckBox);
            arrayListReturnCheck = new ArrayList<>();
            checkBoxUniversity.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (arrayListReturnCheck.size() >= 1) {
                        if (checkBoxUniversity.isChecked()) {
                            buttonView.setChecked(false);
                            Toast.makeText(UniversityAndMajorsChooseRecyclerAdapter.this.context, "You can only select 1 university", Toast.LENGTH_SHORT).show();
                        } else if (!checkBoxUniversity.isChecked()) {
                            arrayListReturnCheck.remove(checkBoxUniversity.getText().toString());
                        }
                    } else {
                        if (checkBoxUniversity.isChecked()) {
                            buttonView.setChecked(true);
                            arrayListReturnCheck.add(checkBoxUniversity.getText().toString());
                        } else if (!checkBoxUniversity.isChecked()) {
                            arrayListReturnCheck.remove(checkBoxUniversity.getText().toString());
                        }
                    }
                    Intent intent = new Intent("school_name");
                    intent.putExtra("schoolName", arrayListReturnCheck);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
        }
    }
}
