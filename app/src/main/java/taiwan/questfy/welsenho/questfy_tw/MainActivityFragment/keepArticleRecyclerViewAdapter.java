package taiwan.questfy.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
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
import com.sackcentury.shinebuttonlib.ShineButton;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class keepArticleRecyclerViewAdapter extends RecyclerView.Adapter<keepArticleRecyclerViewAdapter.keepArticlesViewHolder> implements MainAdapterOnClickListener{

    private Context context;
    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private MainOnClickListener onClickListener;
    private EditRelatedMethod editRelatedMethod;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public keepArticleRecyclerViewAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void setOnClickListener(MainOnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }


    @NonNull
    @Override
    public keepArticlesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.keep_articles_list_recyclerview_layout, viewGroup, false);
        keepArticlesViewHolder viewHolder = new keepArticlesViewHolder(view, this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final keepArticlesViewHolder keepArticlesViewHolder, int i) {
        firebaseDatabaseGetSet = arrayList.get(i);
        editRelatedMethod = new EditRelatedMethod();
        keepArticlesViewHolder.txtUserName.setText(firebaseDatabaseGetSet.getUser_Name());
        keepArticlesViewHolder.txtTitle.setText(firebaseDatabaseGetSet.getTitle());
        keepArticlesViewHolder.txtUploadDate.setText(editRelatedMethod.getFormattedDate(context, Math.abs(firebaseDatabaseGetSet.getUploadTimeStamp())));
        keepArticlesViewHolder.txtContent.setText(firebaseDatabaseGetSet.getContent());
        keepArticlesViewHolder.txtCourseName.setText(firebaseDatabaseGetSet.getMajors());
        if (firebaseDatabaseGetSet.getUser_Image() != null) {

        }

        databaseReference.child("Users_profile").child(firebaseDatabaseGetSet.getUserUid()).child("User_image_uri").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Picasso.get().load(dataSnapshot.getValue().toString()).fit().into(keepArticlesViewHolder.circleImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        keepArticlesViewHolder.shineButton.setChecked(true);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public void onClick(int position) {
        onClickListener.onClicked(position, arrayList);
    }

    public class keepArticlesViewHolder extends RecyclerView.ViewHolder{

        private TextView txtTitle;
        private TextView txtUserName;
        private TextView txtCourseName;
        private TextView txtContent;
        private TextView txtUploadDate;
        private ShineButton shineButton;
        private CircleImageView circleImageView;

        public keepArticlesViewHolder(@NonNull View itemView, final MainAdapterOnClickListener adapterOnClickListener) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.keep_articles_recycLayout_txtArticleName);
            txtUserName = itemView.findViewById(R.id.keep_articles_recycLayout_txtUserName);
            txtCourseName = itemView.findViewById(R.id.keep_articles_recycLayout_txtTag);
            txtContent = itemView.findViewById(R.id.keep_articles_recycLayout_txtArticlePreview);
            txtUploadDate = itemView.findViewById(R.id.keep_articles_recycLayout_txtUploadData);
            shineButton = itemView.findViewById(R.id.keep_articles_recycLayout_btnHeart);
            circleImageView = itemView.findViewById(R.id.keep_articles_recycLayout_circleUserImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        adapterOnClickListener.onClick(position);
                    }
                }
            });
        }
    }
}
