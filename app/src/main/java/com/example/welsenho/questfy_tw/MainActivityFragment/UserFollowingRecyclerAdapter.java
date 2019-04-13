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

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFollowingRecyclerAdapter extends RecyclerView.Adapter<UserFollowingRecyclerAdapter.ViewHolder> {

    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;

    public UserFollowingRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
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
        }
    }
}
