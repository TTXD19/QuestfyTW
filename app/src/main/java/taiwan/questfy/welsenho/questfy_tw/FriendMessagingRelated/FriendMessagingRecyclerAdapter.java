package taiwan.questfy.welsenho.questfy_tw.FriendMessagingRelated;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class FriendMessagingRecyclerAdapter extends RecyclerView.Adapter<FriendMessagingRecyclerAdapter.FriendMessagingViewHolder> {

    private static final int MESSAGE_TYPE_RIGHT = 0;
    private static final int MESSAGE_TYPE_LEFT = 1;
    private static final int MESSAGE_TYPE_RIGHT_PICTURE = 2;
    private static final int MESSAGE_TYPE_LEFT_PICTURE = 3;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private ImageClick imageClick;

    public FriendMessagingRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, ImageClick imageClick){
        this.arrayList = arrayList;
        this.context = context;
        this.imageClick = imageClick;
    }

    @NonNull
    @Override
    public FriendMessagingViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == MESSAGE_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.friend_messaging_right_text, viewGroup, false);
            FriendMessagingViewHolder friendMessagingViewHolder = new FriendMessagingViewHolder(view);
            return friendMessagingViewHolder;
        } else if(viewType == MESSAGE_TYPE_LEFT){
            View view = LayoutInflater.from(context).inflate(R.layout.friend_messaging_left_text, viewGroup, false);
            FriendMessagingViewHolder friendMessagingViewHolder = new FriendMessagingViewHolder(view);
            return friendMessagingViewHolder;
        } else if (viewType == MESSAGE_TYPE_RIGHT_PICTURE){
            View view = LayoutInflater.from(context).inflate(R.layout.friend_messaging_right_image, viewGroup, false);
            FriendMessagingViewHolder friendMessagingViewHolder = new FriendMessagingViewHolder(view);
            return friendMessagingViewHolder;
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.friend_messaging_left_image, viewGroup, false);
            FriendMessagingViewHolder friendMessagingViewHolder = new FriendMessagingViewHolder(view);
            return friendMessagingViewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FriendMessagingViewHolder friendMessagingViewHolder, int i) {
            FirebaseDatabaseGetSet getSet = arrayList.get(i);
            switch (getItemViewType(i)){
                case MESSAGE_TYPE_LEFT:
                    friendMessagingViewHolder.txtLeft.setText(getSet.getMessage());
                    if (getSet.getMessageUserImage() != null) {
                        Picasso.get().load(getSet.getMessageUserImage()).fit().into(friendMessagingViewHolder.userImage);
                    }
                    break;
                case MESSAGE_TYPE_RIGHT:
                    friendMessagingViewHolder.txtRight.setText(getSet.getMessage());
                    break;
                case MESSAGE_TYPE_LEFT_PICTURE:
                    Picasso.get().load(getSet.getMessage()).fit().into(friendMessagingViewHolder.imgLeft);
                    if (getSet.getMessageUserImage() != null) {
                        Picasso.get().load(getSet.getMessageUserImage()).fit().into(friendMessagingViewHolder.userImage);
                    }
                    break;
                case MESSAGE_TYPE_RIGHT_PICTURE:
                    Picasso.get().load(getSet.getMessage()).fit().into(friendMessagingViewHolder.imgRight);
                    break;
            }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    public class FriendMessagingViewHolder extends RecyclerView.ViewHolder {

        private TextView txtLeft;
        private TextView txtRight;
        private CircleImageView userImage;
        private ImageView imgLeft;
        private ImageView imgRight;

        public FriendMessagingViewHolder(@NonNull final View itemView) {
            super(itemView);

            txtLeft = itemView.findViewById(R.id.friendMessagingLeftText);
            txtRight = itemView.findViewById(R.id.friendMessagingRightText);
            userImage = itemView.findViewById(R.id.friendMessagingLeftUserImage);
            imgLeft = itemView.findViewById(R.id.friendMessagingLeftImage);
            imgRight = itemView.findViewById(R.id.friendMessagingRightImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getItemViewType() == MESSAGE_TYPE_LEFT_PICTURE || getItemViewType() == MESSAGE_TYPE_RIGHT_PICTURE){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            imageClick.onImageClick(position, arrayList);
                        }
                    }
                }
            });
        }
    }


    public interface ImageClick{
        void onImageClick(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }

    @Override
    public int getItemViewType(int position) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (!arrayList.get(position).getMessageUserUid().equals(firebaseUser.getUid())){
            if (arrayList.get(position).getMessageType().equals("Text")){
                return MESSAGE_TYPE_LEFT;
            } else {
                return MESSAGE_TYPE_LEFT_PICTURE;
            }
        } else {
            if (arrayList.get(position).getMessageType().equals("Text")){
                return MESSAGE_TYPE_RIGHT;
            }else {
                return MESSAGE_TYPE_RIGHT_PICTURE;
            }
        }
    }
}
