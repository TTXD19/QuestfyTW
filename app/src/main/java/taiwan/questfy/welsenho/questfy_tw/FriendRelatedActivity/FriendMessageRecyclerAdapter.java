package taiwan.questfy.welsenho.questfy_tw.FriendRelatedActivity;

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

public class FriendMessageRecyclerAdapter extends RecyclerView.Adapter<FriendMessageRecyclerAdapter.FriendMessageViewHolder> implements onMainFriendRequestAdapterListener {

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private checkUserPorfile checkUserPorfile;
    public onMainFriendRequestClickListener listener;

    public FriendMessageRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, checkUserPorfile checkUserPorfile){
        this.arrayList = arrayList;
        this.context = context;
        this.checkUserPorfile = checkUserPorfile;
    }

    public void setOnMainClickListener(onMainFriendRequestClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public FriendMessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friend_message_recycler_layout, viewGroup, false);
        FriendMessageViewHolder viewHolder = new FriendMessageViewHolder(view, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FriendMessageViewHolder friendMessageViewHolder, int i) {
        FirebaseDatabaseGetSet getSet = arrayList.get(i);
        friendMessageViewHolder.txtUserName.setText(getSet.getFriendName());
        friendMessageViewHolder.txtChatLines.setText(getSet.getLatestMessage());
        if (getSet.getFriendImage() != null) {
            Picasso.get().load(getSet.getFriendImage()).fit().into(friendMessageViewHolder.circleImageView);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onClick(int position) {
        listener.onClicked(position, arrayList);
    }

    @Override
    public void onCancelClick(int position) {

    }

    public class FriendMessageViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView txtUserName;
        private TextView txtChatLines;

        public FriendMessageViewHolder(@NonNull View itemView, final onMainFriendRequestAdapterListener mainFriendRequestAdapterListener) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.friend_message_recyclerView_circleUserImage);
            txtUserName = itemView.findViewById(R.id.friend_message_recyclerView_txtUserName);
            txtChatLines = itemView.findViewById(R.id.friend_message_recyclerView_txtChatLines);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        mainFriendRequestAdapterListener.onClick(position);
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        checkUserPorfile.onLongClicked(position, arrayList);
                    }
                    return false;
                }
            });

            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        checkUserPorfile.getUserProfile(position, arrayList);
                    }
                }
            });
        }
    }

    public interface checkUserPorfile{
        void getUserProfile(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
        void onLongClicked(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
