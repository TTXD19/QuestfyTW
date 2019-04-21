package com.example.welsenho.questfy_tw.ReigisterCompleteInfoRelated;

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

public class MainCourseChooseRecyclerAdapter extends RecyclerView.Adapter<MainCourseChooseRecyclerAdapter.MainCourseViewHolder>{

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<String> arrayReturnMainCourse;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private Context context;
    private mainCourseClick mainCourseClick;

    public MainCourseChooseRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, mainCourseClick mainCourseClick){
        this.arrayList = arrayList;
        this.context = context;
        this.mainCourseClick = mainCourseClick;
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
        mainCourseViewHolder.txtMainCourse.setText(firebaseDatabaseGetSet.getCourseName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MainCourseViewHolder extends RecyclerView.ViewHolder {

        private TextView txtMainCourse;

        public MainCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMainCourse = itemView.findViewById(R.id.mainCourse_recyclerLayout_txtMainCourse);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        mainCourseClick.onMainCourseClick(position, arrayList);
                    }
                }
            });
        }
    }

    public interface mainCourseClick{
        void onMainCourseClick(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
