package taiwan.questfy.welsenho.questfy_tw.DailyQuestionsRelated;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.MainAdapterOnClickListener;
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.MainOnClickListener;
import taiwan.questfy.welsenho.questfy_tw.R;

public class MainDailyQuestionRecyclerAdapter extends RecyclerView.Adapter<MainDailyQuestionRecyclerAdapter.MainDailyViewHolder> implements MainAdapterOnClickListener {

    private static final int DAILY_QUESTION_LARGE = 0;
    private static final int DAILY_QUESTION_SMALL = 1;

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private MainOnClickListener onClickListener;

    public MainDailyQuestionRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    public void setOnClickListener(MainOnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }


    @NonNull
    @Override
    public MainDailyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == DAILY_QUESTION_LARGE){
            View view = LayoutInflater.from(context).inflate(R.layout.daily_question_article_large_recycler_layout, viewGroup, false);
            MainDailyViewHolder viewHolder = new MainDailyViewHolder(view, this);
            return viewHolder;
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.daily_question_article_small_recycler_layout, viewGroup, false);
            MainDailyViewHolder viewHolder = new MainDailyViewHolder(view, this);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MainDailyViewHolder mainDailyViewHolder, int i) {
        FirebaseDatabaseGetSet getSet = arrayList.get(i);
        switch (getItemViewType(i)){
            case DAILY_QUESTION_LARGE:
                mainDailyViewHolder.txtLargeTitle.setText(getSet.getDailyQuestionTitle());
                mainDailyViewHolder.txtLargeSubject.setText(getSet.getDailyQuestionSubject());
                mainDailyViewHolder.txtLargeAuthor.setText(getSet.getDailyQuestionAuthor());
                Picasso.get().load(getSet.getDailyQuestionImage()).fit().into(mainDailyViewHolder.imgLargePicture);
                break;
            case DAILY_QUESTION_SMALL:
                mainDailyViewHolder.txtSmallTitle.setText(getSet.getDailyQuestionTitle());
                mainDailyViewHolder.txtSmallSubject.setText(getSet.getDailyQuestionSubject());
                mainDailyViewHolder.txtSmallAuthor.setText(getSet.getDailyQuestionAuthor());
                Picasso.get().load(getSet.getDailyQuestionImage()).fit().into(mainDailyViewHolder.imgSmallPicture);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (arrayList.get(position).getDailyQuestionType().equals("Large")){
            return DAILY_QUESTION_LARGE;
        }else {
            return DAILY_QUESTION_SMALL;
        }
    }

    @Override
    public void onClick(int position) {
        onClickListener.onClicked(position, arrayList);
    }

    public class MainDailyViewHolder extends RecyclerView.ViewHolder {

        private TextView txtLargeTitle;
        private TextView txtLargeSubject;
        private TextView txtLargeAuthor;
        private TextView txtSmallTitle;
        private TextView txtSmallSubject;
        private TextView txtSmallAuthor;
        private ImageView imgLargePicture;
        private ImageView imgSmallPicture;

        public MainDailyViewHolder(@NonNull View itemView, final MainAdapterOnClickListener adapterOnClickListener) {
            super(itemView);

            txtLargeTitle = itemView.findViewById(R.id.daily_question_txtTitle_large_recyclerLayout);
            txtLargeSubject = itemView.findViewById(R.id.daily_question_txtSubject_large_recyclerLayout);
            txtLargeAuthor = itemView.findViewById(R.id.daily_question_txtAuthor_large_recyclerLayout);
            txtSmallTitle = itemView.findViewById(R.id.daily_question_txtTitle_small_recyclerLayout);
            txtSmallSubject = itemView.findViewById(R.id.daily_question_txtSubject_small_recyclerLayout);
            txtSmallAuthor = itemView.findViewById(R.id.daily_question_txtAuthor_small_recyclerLayout);
            imgLargePicture = itemView.findViewById(R.id.daily_question_imageView_large_recyclerLayout);
            imgSmallPicture = itemView.findViewById(R.id.daily_question_imgSmall_recyclerLayout);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        adapterOnClickListener.onClick(position);
                    }
                }
            });
        }
    }
}
