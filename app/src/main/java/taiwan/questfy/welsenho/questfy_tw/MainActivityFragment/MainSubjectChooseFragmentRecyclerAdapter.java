package taiwan.questfy.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class MainSubjectChooseFragmentRecyclerAdapter extends RecyclerView.Adapter<MainSubjectChooseFragmentRecyclerAdapter.SbjectHolder>{

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private MainOnClickListener listener;
    private majorClick majorClick;

    public MainSubjectChooseFragmentRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, majorClick majorClick){
        this.arrayList = arrayList;
        this.context = context;
        this.majorClick = majorClick;
    }


    @NonNull
    @Override
    public SbjectHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.main_subject_choose_recycler_layout, viewGroup, false);
        SbjectHolder viewHolder = new SbjectHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SbjectHolder sbjectHolder, int i) {
        FirebaseDatabaseGetSet getSet = arrayList.get(i);
        sbjectHolder.txtShowSubjects.setText(getSet.getMajor());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void setOnClickListener(MainOnClickListener listener){
        this.listener = listener;
    }

    public class SbjectHolder extends RecyclerView.ViewHolder {

        private TextView txtShowSubjects;

        public SbjectHolder(@NonNull View itemView) {
            super(itemView);
            txtShowSubjects = itemView.findViewById(R.id.mainSubjectChoose_layout_txtSubjects);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        majorClick.onMajorClick(position, arrayList);
                    }
                }
            });
        }
    }

    public interface  majorClick{
        void onMajorClick(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }



}
