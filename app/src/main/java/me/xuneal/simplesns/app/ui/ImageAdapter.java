package me.xuneal.simplesns.app.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import me.xuneal.simplesns.app.ui.components.SquareImageView;

import java.util.List;

/**
* Created by xyz on 2014/11/25.
*/
class ImageAdapter extends BaseAdapter {
    private List<String> mImageUrls;

    public ImageAdapter(List<String> imageUrls) {
        mImageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return mImageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView!=null && convertView.getTag()!=null){
            viewHolder = (ViewHolder) convertView.getTag();

        } else {
            convertView = new SquareImageView(parent.getContext());
            AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(layoutParams);
                    ((SquareImageView) convertView).setScaleType(ImageView.ScaleType.CENTER_CROP);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (SquareImageView) convertView;
            viewHolder.mImageView.setClickable(false);
            viewHolder.mImageView.setFocusable(false);
            convertView.setTag(viewHolder);

        }
        String filename = mImageUrls.get(position);

        ImageLoader.getInstance().displayImage(filename, viewHolder.mImageView);

        return convertView;
    }

    static class ViewHolder {
        SquareImageView mImageView;
    }
}
