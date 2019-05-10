package taiwan.questfy.welsenho.questfy_tw.MainActivityFragment;

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
import taiwan.questfy.welsenho.questfy_tw.EditActivityRelated.EditRelatedMethod;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class NewArticleListRecyclerAdapter extends RecyclerView.Adapter<NewArticleListRecyclerAdapter.ViewHolder>{

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private ArrayList<FirebaseDatabaseGetSet> clickArratyListSize;
    private Context context;
    private EditRelatedMethod editRelatedMethod;
    private ClickItem clickItem;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public NewArticleListRecyclerAdapter(Context context, ClickItem clickItem) {
        arrayList = new ArrayList<>();
        clickArratyListSize = new ArrayList<>();
        this.context = context;
        this.clickItem = clickItem;

        firebaseDatabase =FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    public void addAll(ArrayList<FirebaseDatabaseGetSet> arrayList1){
        int initList = arrayList.size();
        arrayList.addAll(arrayList1);
        notifyItemChanged(initList, arrayList1.size());
    }

    public void clear2() {
        final int size = arrayList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                arrayList.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }

    public String getLastItemId(){
        return arrayList.get(arrayList.size() - 1).getArticle_ID();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.article_list_recyclerview_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        editRelatedMethod = new EditRelatedMethod();
        final FirebaseDatabaseGetSet getSet = arrayList.get(i);
        viewHolder.txtUserName.setText(getSet.getUser_Name());
        viewHolder.txtTitle.setText(getSet.getTitle());
        viewHolder.txtUploadDate.setText(editRelatedMethod.getFormattedDate(context, Math.abs(getSet.getUploadTimeStamp())));
        viewHolder.txtMajors.setText(getSet.getMajors());
        viewHolder.txtArticlePreview.setText(getSet.getContent());
        viewHolder.txtArticleCount.setText(String.valueOf(Math.abs(getSet.getArticle_like_count())));
        if (getSet.getIsMeet().equals("Meet")){
            viewHolder.txtMeetUp.setVisibility(View.VISIBLE);
            viewHolder.imgMeetUp.setVisibility(View.VISIBLE);
        }else{
            viewHolder.txtMeetUp.setVisibility(View.GONE);
            viewHolder.imgMeetUp.setVisibility(View.GONE);
        }
        if (getSet.getAnswerCount() != 0) {
            viewHolder.txtAnserCount.setText(String.valueOf(getSet.getAnswerCount()));
        }else {
            viewHolder.txtAnserCount.setText(String.valueOf(0));
        }

        databaseReference.child("Users_profile").child(getSet.getUserUid()).child("User_image_uri").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Picasso.get().load(dataSnapshot.getValue().toString()).fit().into(viewHolder.circleImageViewUserImage);
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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtUserName;
        private TextView txtTitle;
        private TextView txtUploadDate;
        private TextView txtMajors;
        private TextView txtArticlePreview;
        private TextView txtArticleCount;
        private TextView txtAnserCount;
        private TextView txtMeetUp;
        private ImageView imgMeetUp;
        private CircleImageView circleImageViewUserImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.list_article_recycLayout_txtArticleName);
            txtMajors = itemView.findViewById(R.id.list_article_recycLayout_txtTag);
            txtUploadDate = itemView.findViewById(R.id.list_article_recycLayout_txtUploadData);
            txtUserName  = itemView.findViewById(R.id.list_article_recycLayout_txtUserName);
            txtArticlePreview = itemView.findViewById(R.id.list_article_recycLayout_txtArticlePreview);
            txtArticleCount = itemView.findViewById(R.id.list_article_recycLayout_txtViews);
            txtAnserCount = itemView.findViewById(R.id.list_article_recycLayout_txtAnswerCount);
            txtMeetUp = itemView.findViewById(R.id.list_article_recycLayout_txtWantMeetUp);
            imgMeetUp = itemView.findViewById(R.id.list_article_recycLayout_imgWantMeetUp);
            circleImageViewUserImage = itemView.findViewById(R.id.list_article_recycLayout_circleUserImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        clickItem.getItemPosition(position, arrayList);
                    }
                }
            });
        }
    }

    public interface ClickItem{
        void getItemPosition(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
