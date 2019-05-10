package taiwan.questfy.welsenho.questfy_tw.OtherUserProfileRelatedMethod;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import taiwan.questfy.welsenho.questfy_tw.FriendRelatedActivity.SearchAndAddFriendRecyclerAdapter;
import taiwan.questfy.welsenho.questfy_tw.R;

public class CheckFollowerRecyclerAdapter extends RecyclerView.Adapter<CheckFollowerRecyclerAdapter.viewHolder> {

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private userClickListener userClickListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public CheckFollowerRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, userClickListener userClickListener){
        this.arrayList = arrayList;
        this.userClickListener = userClickListener;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.search_friend_list_recycler_layout, viewGroup, false);
        viewHolder viewHolder = new viewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final viewHolder viewHolder, int i) {
        firebaseDatabaseGetSet = arrayList.get(i);
        viewHolder.txtUserName.setText(firebaseDatabaseGetSet.getUser_Name());
        if (firebaseDatabaseGetSet.getSchoolName() != null) {
            viewHolder.txtUserUniversity.setText(firebaseDatabaseGetSet.getSchoolName());
        }else {
            viewHolder.txtUserUniversity.setText("未註冊大學");
        }

        //Testing code not confirm yet this is for auto getting user profile image
        databaseReference.child("Users_profile").child(firebaseDatabaseGetSet.getUserUid()).child("User_image_uri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.getValue().toString()).fit().into(viewHolder.circleImageView);
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

    public class viewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView txtUserName;
        private TextView txtUserUniversity;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.search_friend_recycler_userImage);
            txtUserName = itemView.findViewById(R.id.search_friend_recycler_txtUserName);
            txtUserUniversity = itemView.findViewById(R.id.search_friend_recycler_txtUserUniversity);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        userClickListener.onUserClick(position, arrayList);
                    }
                }
            });
        }
    }

    public interface userClickListener{
        void onUserClick(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
