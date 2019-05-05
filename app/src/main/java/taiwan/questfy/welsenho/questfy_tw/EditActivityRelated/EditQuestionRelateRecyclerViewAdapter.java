package taiwan.questfy.welsenho.questfy_tw.EditActivityRelated;

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

public class EditQuestionRelateRecyclerViewAdapter extends RecyclerView.Adapter<EditQuestionRelateRecyclerViewAdapter.EditQuestionViewHolder>{


    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private majorSelectListener listener;

    public EditQuestionRelateRecyclerViewAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, majorSelectListener listener) {
        this.arrayList = arrayList;
        this.context = context;
        this.listener = listener;
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
        viewHolder.txtMajor.setText(getSet.getMajor());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public interface majorSelectListener{
        void select(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }


    public class EditQuestionViewHolder extends RecyclerView.ViewHolder{

        private TextView txtMajor;

        public EditQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMajor = itemView.findViewById(R.id.edit_question_recycler_adapter_txtMajor);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        listener.select(position, arrayList);
                    }
                }
            });

        }
    }

}
