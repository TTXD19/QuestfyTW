package taiwan.questfy.welsenho.questfy_tw.MainActivityFragment;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class EnlargeImageListRecyclerAdapter extends RecyclerView.Adapter<EnlargeImageListRecyclerAdapter.enlargeViewHolder>{

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private FirebaseDatabaseGetSet firebaseDatabaseGetSet;

    public EnlargeImageListRecyclerAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList){
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public enlargeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.enlarge_image_list_recycler_layout, viewGroup, false);
        enlargeViewHolder viewHolder = new enlargeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull enlargeViewHolder enlargeViewHolder, int i) {
        firebaseDatabaseGetSet = arrayList.get(i);
        SubsamplingScaleImageView subsamplingScaleImageView;
        Picasso.get().load(firebaseDatabaseGetSet.getEditInitImageUploadViewUri()).fit().into(enlargeViewHolder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class enlargeViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public enlargeViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView =  itemView.findViewById(R.id.enlargeImageRecyclerImage);
        }
    }
}
