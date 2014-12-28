package me.xuneal.simplesns.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import com.nostra13.universalimageloader.core.ImageLoader;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.ui.components.CheckableRelativeLayout;
import me.xuneal.simplesns.app.ui.components.SquareImageView;

import java.util.List;

/**
 * Created by xyz on 2014/12/21.
 */
public class ImageWithPickPhotoAdapter extends BaseAdapter {
    private List<String> mImages;
    private Context mContext;
    public ImageWithPickPhotoAdapter(Context context, List<String> images){
        mImages = images;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mImages.size()+1;
    }

    @Override
    public Object getItem(int position) {
        if (position==0){
            return "assets://ic_take_photo.png";
        }
        else
            return mImages.get(position-1);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return 0;
        }
        else {
            return 1;
        }
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            SquareImageView squareImageView = new SquareImageView(mContext);
            squareImageView.setMaxHeight(150);
            squareImageView.setMaxWidth(150);
            convertView=squareImageView;

        }
        ImageLoader.getInstance().displayImage((String)getItem(position), (SquareImageView)convertView);

        return convertView;
    }


}
