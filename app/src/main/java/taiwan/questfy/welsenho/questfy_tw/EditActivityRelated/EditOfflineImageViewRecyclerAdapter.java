package taiwan.questfy.welsenho.questfy_tw.EditActivityRelated;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;

public class EditOfflineImageViewRecyclerAdapter extends RecyclerView.Adapter<EditOfflineImageViewRecyclerAdapter.ViewHolder> {


    private ArrayList<String> arrayList;
    private ImageClick imageClick;

    public EditOfflineImageViewRecyclerAdapter(ArrayList<String> arrayList, ImageClick imageClick) {
        this.arrayList = arrayList;
        this.imageClick = imageClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_init_relate_images_view_recycler_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Picasso.get().load(arrayList.get(i)).resize(400, 380).into(viewHolder.imgViews);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imgViews;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgViews = itemView.findViewById(R.id.edit_init_recycler_adapter_ImageView);

            imgViews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION){
                        imageClick.onRemoverImage(arrayList ,position);
                    }
                }
            });
        }
    }


    public interface ImageClick{
        void onRemoverImage(ArrayList<String> arrayList, int position);
    }
}
