package com.example.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import com.example.welsenho.questfy_tw.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFollowingRecyclerAdapter extends RecyclerView.Adapter<UserFollowingRecyclerAdapter.ViewHolder> {

    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private UserItemClick userItemClick;

    public UserFollowingRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, UserItemClick userItemClick) {
        this.arrayList = arrayList;
        this.context = context;
        this.userItemClick = userItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_friend_list_recycler_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        firebaseDatabaseGetSet = arrayList.get(i);
        viewHolder.txtUserName.setText(firebaseDatabaseGetSet.getUser_Name());
        if (firebaseDatabaseGetSet.getSchoolName() != null){
            viewHolder.txtSchoolName.setText(firebaseDatabaseGetSet.getSchoolName());
        }else {
            viewHolder.txtSchoolName.setText("大學尚未設定");
        }
        if (firebaseDatabaseGetSet.getUser_image_uri() != null) {
            Picasso.get().load(firebaseDatabaseGetSet.getUser_image_uri()).fit().into(viewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserName;
        private TextView txtSchoolName;
        private CircleImageView circleImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUserName = itemView.findViewById(R.id.search_friend_recycler_txtUserName);
            txtSchoolName = itemView.findViewById(R.id.search_friend_recycler_txtUserUniversity);
            circleImageView = itemView.findViewById(R.id.search_friend_recycler_userImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        userItemClick.getPosition(position, arrayList);
                    }
                }
            });
        }
    }

    public interface UserItemClick{
        void getPosition(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
