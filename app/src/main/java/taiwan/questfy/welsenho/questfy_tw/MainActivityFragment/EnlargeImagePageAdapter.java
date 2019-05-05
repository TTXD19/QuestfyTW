package taiwan.questfy.welsenho.questfy_tw.MainActivityFragment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jsibbold.zoomage.ZoomageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import taiwan.questfy.welsenho.questfy_tw.FirebaseDatabaseGetSet;
import taiwan.questfy.welsenho.questfy_tw.R;


public class EnlargeImagePageAdapter extends PagerAdapter {

    private ArrayList<FirebaseDatabaseGetSet> arrayList;
    private Context context;

    public EnlargeImagePageAdapter(ArrayList<FirebaseDatabaseGetSet> arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        ZoomageView imageView;
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.enlarge_image_list_recycler_layout, container, false);
        imageView = view.findViewById(R.id.enlargeImageRecyclerImage);
        Picasso.get().load(arrayList.get(position).getEditInitImageUploadViewUri()).fit().into(imageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
