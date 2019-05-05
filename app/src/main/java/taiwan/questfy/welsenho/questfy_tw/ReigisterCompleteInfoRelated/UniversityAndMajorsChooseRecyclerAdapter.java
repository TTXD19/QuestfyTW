package taiwan.questfy.welsenho.questfy_tw.ReigisterCompleteInfoRelated;

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

public class UniversityAndMajorsChooseRecyclerAdapter extends RecyclerView.Adapter<UniversityAndMajorsChooseRecyclerAdapter.UniversityAndMajorsViewHolder> {


    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private itemOnClick itemOnClick;

    public UniversityAndMajorsChooseRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, itemOnClick itemOnClick){
        this.arrayList = arrayList;
        this.context = context;
        this.itemOnClick = itemOnClick;
    }


    @NonNull
    @Override
    public UniversityAndMajorsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.university_name_recycler_layout, viewGroup, false);
        UniversityAndMajorsViewHolder viewHolder = new UniversityAndMajorsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull UniversityAndMajorsViewHolder universityAndMajorsViewHolder, int i) {
        FirebaseDatabaseGetSet getSet = arrayList.get(i);
        universityAndMajorsViewHolder.txtUniversityList.setText(getSet.getSchoolName());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class UniversityAndMajorsViewHolder extends RecyclerView.ViewHolder {

       private TextView txtUniversityList;

        public UniversityAndMajorsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUniversityList = itemView.findViewById(R.id.university_register_recycLayout_txtSchoolName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        itemOnClick.getClickItem(position, arrayList);
                    }
                }
            });
        }
    }


    public interface itemOnClick{
        void getClickItem(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
