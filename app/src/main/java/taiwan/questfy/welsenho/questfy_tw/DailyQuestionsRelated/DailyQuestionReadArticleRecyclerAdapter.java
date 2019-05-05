package taiwan.questfy.welsenho.questfy_tw.DailyQuestionsRelated;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class DailyQuestionReadArticleRecyclerAdapter extends RecyclerView.Adapter<DailyQuestionReadArticleRecyclerAdapter.DailyQuestionReadArticleViewHolder>{

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet getSet;

    public DailyQuestionReadArticleRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList){
        this.arrayList = arrayList;
    }


    @NonNull
    @Override
    public DailyQuestionReadArticleViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.daily_question_comments_recycler_layout, viewGroup, false);
        DailyQuestionReadArticleViewHolder viewHolder = new DailyQuestionReadArticleViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DailyQuestionReadArticleViewHolder dailyQuestionReadArticleViewHolder, int i) {
        getSet = arrayList.get(i);
        dailyQuestionReadArticleViewHolder.txtUserName.setText(getSet.getUser_Name());
        dailyQuestionReadArticleViewHolder.txtDate.setText(getSet.getUpload_Date());
        dailyQuestionReadArticleViewHolder.txtContent.setText(getSet.getDailyQuestionComment());
        Picasso.get().load(getSet.getUser_Image()).fit().into(dailyQuestionReadArticleViewHolder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class DailyQuestionReadArticleViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserName;
        private TextView txtDate;
        private TextView txtContent;
        private CircleImageView circleImageView;

        public DailyQuestionReadArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUserName = itemView.findViewById(R.id.recycler_layout_daily_questions_readArticle_txtUserName);
            txtDate = itemView.findViewById(R.id.recycler_layout_daily_questions_txtCommentDate);
            txtContent = itemView.findViewById(R.id.recycler_layout_daily_questions_readArticle_txtCommentContent);
            circleImageView = itemView.findViewById(R.id.recycler_layout_daily_questions_readArticle_userImage);
        }
    }
}
