package taiwan.questfy.welsenho.questfy_tw.MeetUpScheduleRelated;

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
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.MainAdapterOnClickListener;
import taiwan.questfy.welsenho.questfy_tw.MainActivityFragment.MainOnClickListener;
import taiwan.questfy.welsenho.questfy_tw.R;

public class MeetUpScheduleRecyclerAdapter extends RecyclerView.Adapter<MeetUpScheduleRecyclerAdapter.MeetUpViewHolder> implements MainAdapterOnClickListener{

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private MainOnClickListener onClickListener;
    private onArticleClick onArticleClick;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public MeetUpScheduleRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, onArticleClick onArticleClick){
        this.arrayList = arrayList;
        this.context = context;
        this.onArticleClick = onArticleClick;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void setOnClickListener(MainOnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }


    @NonNull
    @Override
    public MeetUpViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.meet_up_list_recycler_layout, viewGroup, false);
        MeetUpViewHolder viewHolder = new MeetUpViewHolder(view, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MeetUpViewHolder meetUpViewHolder, int i) {
        firebaseDatabaseGetSet = arrayList.get(i);
        meetUpViewHolder.txtTitle.setText(firebaseDatabaseGetSet.getTitle());
        meetUpViewHolder.txtUserName.setText(firebaseDatabaseGetSet.getUser_Name());
        meetUpViewHolder.txtPlaceName.setText(firebaseDatabaseGetSet.getMeetPlace());
        meetUpViewHolder.txtAddress.setText(firebaseDatabaseGetSet.getMeetAddress());
        String meetDateTime = firebaseDatabaseGetSet.getMeetDate() + firebaseDatabaseGetSet.getMeetTime();
        meetUpViewHolder.txtDateTime.setText(meetDateTime);

        databaseReference.child("Users_profile").child(firebaseDatabaseGetSet.getUserUid()).child("User_image_uri").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Picasso.get().load(dataSnapshot.getValue().toString()).fit().into(meetUpViewHolder.circleImageView);
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
        onClickListener.onClicked(position, arrayList);
    }

    public interface onArticleClick{
        void itemClikc(int positionArticle, ArrayList<FirebaseDatabaseGetSet> arrayListArticle);
    }


    public class MeetUpViewHolder extends RecyclerView.ViewHolder {

        private TextView txtTitle;
        private TextView txtUserName;
        private TextView txtPlaceName;
        private TextView txtAddress;
        private TextView txtDateTime;
        private CircleImageView circleImageView;
        private Button btnCheckMap;

        public MeetUpViewHolder(@NonNull View itemView, final MainAdapterOnClickListener adapterOnClickListener) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.meet_up_txtTitle);
            txtUserName = itemView.findViewById(R.id.meet_up_txtUserName);
            txtPlaceName = itemView.findViewById(R.id.meet_up_txtPlaceName);
            txtAddress = itemView.findViewById(R.id.meet_up_txtPlaceAddress);
            txtDateTime = itemView.findViewById(R.id.meet_up_txtDateTime);
            circleImageView = itemView.findViewById(R.id.meet_up_circleUserImage);
            btnCheckMap = itemView.findViewById(R.id.meet_up_btnCheckMap);

            btnCheckMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        adapterOnClickListener.onClick(position);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        onArticleClick.itemClikc(position, arrayList);
                    }
                }
            });
        }
    }
}
