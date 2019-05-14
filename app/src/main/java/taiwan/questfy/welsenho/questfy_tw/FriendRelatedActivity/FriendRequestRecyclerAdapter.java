package taiwan.questfy.welsenho.questfy_tw.FriendRelatedActivity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class FriendRequestRecyclerAdapter extends RecyclerView.Adapter<FriendRequestRecyclerAdapter.FiriendRequestViewHolder> implements onMainFriendRequestAdapterListener{

    private onMainFriendRequestClickListener listener;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private userImageClick userImageClick;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public FriendRequestRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, userImageClick userImageClick){
        this.arrayList = arrayList;
        this.context = context;
        this.userImageClick = userImageClick;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void setOnMainClickListener(onMainFriendRequestClickListener listener){
        this.listener = listener;
    }


    @NonNull
    @Override
    public FiriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.firend_request_recycler_layout, viewGroup, false);
        FiriendRequestViewHolder viewHolder = new FiriendRequestViewHolder(view, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final FiriendRequestViewHolder firiendRequestViewHolder, int i) {

        final FirebaseDatabaseGetSet getSet = arrayList.get(i);
        firiendRequestViewHolder.txtUserName.setText(getSet.getRequestName());
        databaseReference.child("Users_profile").child(getSet.getSenderUid()).child("User_image_uri").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Picasso.get().load(dataSnapshot.getValue().toString()).fit().into(firiendRequestViewHolder.circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
        listener.onCancelClicked(position, arrayList);
    }

    public class FiriendRequestViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView txtUserName;
        private Button btnConfirm;
        private Button btnCancel;

        public FiriendRequestViewHolder(@NonNull View itemView, final onMainFriendRequestAdapterListener mainFriendRequestAdapterListener) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.friend_request_recyclerView_circleUserImage);
            txtUserName = itemView.findViewById(R.id.friend_request_recyclerView_txtUserName);
            btnConfirm = itemView.findViewById(R.id.friend_request_recyclerView_btnConfirm);
            btnCancel = itemView.findViewById(R.id.friend_request_recyclerView_btnCancel);

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        mainFriendRequestAdapterListener.onClick(position);
                    }
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        mainFriendRequestAdapterListener.onCancelClick(position);
                    }
                }
            });

            circleImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                       userImageClick.checkUserProfile(position, arrayList);
                    }
                }
            });
        }
    }


    public interface userImageClick{
        void checkUserProfile(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
