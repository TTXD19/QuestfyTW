package com.example.welsenho.questfy_tw.FriendRelatedActivity;

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

public class SearchAndAddFriendRecyclerAdapter extends RecyclerView.Adapter<SearchAndAddFriendRecyclerAdapter.SearchFriendViewHolder> {

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private userClickListener userClickListener;

    public SearchAndAddFriendRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, userClickListener userClickListener){
        this.arrayList = arrayList;
        this.userClickListener = userClickListener;
    }


    @NonNull
    @Override
    public SearchFriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_friend_list_recycler_layout, viewGroup, false);
        SearchFriendViewHolder viewHolder = new SearchFriendViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchFriendViewHolder searchFriendViewHolder, int i) {
        firebaseDatabaseGetSet = arrayList.get(i);
        searchFriendViewHolder.txtUserName.setText(firebaseDatabaseGetSet.getID());
        if (firebaseDatabaseGetSet.getSchoolName() != null) {
            searchFriendViewHolder.txtUserUniversity.setText(firebaseDatabaseGetSet.getSchoolName());
        }else {
            searchFriendViewHolder.txtUserUniversity.setText("未註冊大學");
        }
        if (searchFriendViewHolder.circleImageView != null) {
            Picasso.get().load(firebaseDatabaseGetSet.getUser_image_uri()).fit().into(searchFriendViewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class SearchFriendViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView txtUserName;
        private TextView txtUserUniversity;

        public SearchFriendViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.search_friend_recycler_userImage);
            txtUserName = itemView.findViewById(R.id.search_friend_recycler_txtUserName);
            txtUserUniversity = itemView.findViewById(R.id.search_friend_recycler_txtUserUniversity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        userClickListener.onUserClick(position);
                    }
                }
            });
        }
    }

    public interface userClickListener{
        void onUserClick(int position);
    }
}
