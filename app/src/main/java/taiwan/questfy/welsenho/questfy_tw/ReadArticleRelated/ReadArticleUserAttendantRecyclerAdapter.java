package taiwan.questfy.welsenho.questfy_tw.ReadArticleRelated;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class ReadArticleUserAttendantRecyclerAdapter extends RecyclerView.Adapter<ReadArticleUserAttendantRecyclerAdapter.ViewHolder> {

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;
    private ClickUser clickUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public ReadArticleUserAttendantRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context, ClickUser clickUser) {
        this.arrayList = arrayList;
        this.context = context;
        this.clickUser = clickUser;
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.read_article_user_attendant_reycler_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        firebaseDatabaseGetSet = arrayList.get(i);
        databaseReference.child("Users_profile").child(firebaseDatabaseGetSet.getUserUid()).child("User_image_uri").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Picasso.get().load(dataSnapshot.getValue().toString()).fit().into(viewHolder.circleImageView);
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView circleImageView;
        private TextView txtUserName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.user_attendant_circleImage);
            //txtUserName = itemView.findViewById(R.id.user_attendant_txtUserName);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        clickUser.getUserUid(position, arrayList);
                    }
                }
            });
        }
    }

    public interface ClickUser{
        void getUserUid(int position, ArrayList<FirebaseDatabaseGetSet> arrayList);
    }
}
