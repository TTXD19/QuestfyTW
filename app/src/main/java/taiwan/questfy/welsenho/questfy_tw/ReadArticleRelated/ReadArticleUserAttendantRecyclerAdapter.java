package taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated;

import android.content.Context;
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

public class ReadArticleUserAttendantRecyclerAdapter extends RecyclerView.Adapter<ReadArticleUserAttendantRecyclerAdapter.ViewHolder> {

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private ClickUser clickUser;

    public ReadArticleUserAttendantRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, ClickUser clickUser) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickUser = clickUser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.read_article_user_attendant_reycler_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        firebaseDatabaseGetSet = arrayList.get(i);
        //viewHolder.txtUserName.setText(firebaseDatabaseGetSet.getUser_Name());
        Picasso.get().load(firebaseDatabaseGetSet.getUser_Image()).fit().into(viewHolder.circleImageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView txtUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.user_attendant_circleImage);
            //txtUserName = itemView.findViewById(R.id.user_attendant_txtUserName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        clickUser.getUserUid(position, arrayList);
                    }
                }
            });
        }
    }

    public interface ClickUser{
        void getUserUid(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
