package com.example.welsenho.questfy_tw.ReigisterCompleteInfoRelated;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;

import java.util.ArrayList;

public class MainCourseChooseRecyclerAdapter extends RecyclerView.Adapter<MainCourseChooseRecyclerAdapter.MainCourseViewHolder>{

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<String> arrayReturnMainCourse;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private Context context;

    public MainCourseChooseRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }


    @NonNull
    @Override
    public MainCourseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.university_and_majors_recycler_layout, viewGroup, false);
        MainCourseViewHolder viewHolder = new MainCourseViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainCourseViewHolder mainCourseViewHolder, int i) {
        firebaseDatabaseGetSet = arrayList.get(i);
        mainCourseViewHolder.checkBoxMainCourse.setText(firebaseDatabaseGetSet.getCourseName());
        firebaseDatabaseGetSet.setIsSelected(false);
        if (firebaseDatabaseGetSet.getIsSelected()) {
            mainCourseViewHolder.checkBoxMainCourse.setChecked(true);
        } else {
            mainCourseViewHolder.checkBoxMainCourse.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MainCourseViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBoxMainCourse;

        public MainCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBoxMainCourse = itemView.findViewById(R.id.uniAndMajor_Recycler_CheckBox);
            arrayReturnMainCourse = new ArrayList<>();
            checkBoxMainCourse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (arrayReturnMainCourse.size() >= 1) {
                        if (checkBoxMainCourse.isChecked()) {
                            buttonView.setChecked(false);
                            Toast.makeText(MainCourseChooseRecyclerAdapter.this.context, "You can only select 1 university", Toast.LENGTH_SHORT).show();
                        } else if (!checkBoxMainCourse.isChecked()) {
                            arrayReturnMainCourse.remove(checkBoxMainCourse.getText().toString());
                        }
                    } else {
                        if (checkBoxMainCourse.isChecked()) {
                            buttonView.setChecked(true);
                            arrayReturnMainCourse.add(checkBoxMainCourse.getText().toString());
                        } else if (!checkBoxMainCourse.isChecked()) {
                            arrayReturnMainCourse.remove(checkBoxMainCourse.getText().toString());
                        }
                    }
                    Intent intent = new Intent("main_course");
                    intent.putExtra("courseName", arrayReturnMainCourse);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            });
        }
    }
}
