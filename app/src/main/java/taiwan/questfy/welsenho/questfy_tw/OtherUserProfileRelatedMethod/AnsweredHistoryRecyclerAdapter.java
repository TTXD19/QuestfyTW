package taiwan.questfy.welsenho.questfy_tw.OtherUserProfileRelatedMethod;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class AnsweredHistoryRecyclerAdapter extends RecyclerView.Adapter<AnsweredHistoryRecyclerAdapter.ViewHolder>{


    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private Context context;
    private ItemClick itemClick;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public AnsweredHistoryRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, ItemClick itemClick) {
        this.arrayList = arrayList;
        this.context = context;
        this.itemClick = itemClick;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.answered_history_recycler_layput, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseDatabaseGetSet = arrayList.get(i);
        viewHolder.txtArticlePreview.setText(firebaseDatabaseGetSet.getAnswerContent());
        viewHolder.txtUserName.setText(firebaseDatabaseGetSet.getUserName());
        viewHolder.txtUploadDate.setText(firebaseDatabaseGetSet.getUpdateDate());
        viewHolder.txtTitle.setText(firebaseDatabaseGetSet.getTitle());
        databaseReference.child("Users_profile").child(firebaseDatabaseGetSet.getUserID()).child("User_image_uri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(dataSnapshot.getValue().toString()).fit().into(viewHolder.circleImageViewUserImage);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtUserName;
        private TextView txtTitle;
        private TextView txtUploadDate;
        private TextView txtArticlePreview;
        private CircleImageView circleImageViewUserImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            txtTitle = itemView.findViewById(R.id.answered_history_recycLayout_txtArticleName);
            txtUploadDate = itemView.findViewById(R.id.answered_history_recycLayout_txtUploadData);
            txtUserName  = itemView.findViewById(R.id.answered_history_recycLayout_txtUserName);
            txtArticlePreview = itemView.findViewById(R.id.answered_history_recycLayout_txtArticlePreview);
            circleImageViewUserImage = itemView.findViewById(R.id.answered_history_recycLayout_circleUserImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        itemClick.articleClick(position, arrayList);
                    }
                }
            });
        }
    }

    public interface ItemClick{
        void articleClick(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
