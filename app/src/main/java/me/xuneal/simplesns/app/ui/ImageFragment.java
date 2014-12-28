package me.xuneal.simplesns.app.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import me.xuneal.simplesns.app.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by xyz on 2014/12/24.
 */
public class ImageFragment extends Fragment {

    public static ImageFragment newInstance(String imageUrl){
        ImageFragment fragment = new ImageFragment();
        Bundle args = new Bundle();
        args.putString("image_url", imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    PhotoView mPhotoView;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, null,false);
        mPhotoView = (PhotoView)view.findViewById(R.id.photo_view);
        ImageLoader.getInstance().displayImage(getArguments().getString("image_url"), mPhotoView);
        mPhotoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                ((DialogFragment)getParentFragment()).dismiss();
            }
        });
        return view;
    }


}