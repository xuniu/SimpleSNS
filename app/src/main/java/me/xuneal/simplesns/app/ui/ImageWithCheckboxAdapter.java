package me.xuneal.simplesns.app.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import com.nostra13.universalimageloader.core.ImageLoader;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.ui.components.CheckableRelativeLayout;
import me.xuneal.simplesns.app.ui.components.SquareImageView;

import java.util.List;

/**
 * Created by xyz on 2014/12/16.
 */
public class ImageWithCheckboxAdapter extends BaseAdapter {

    private List<String> mImages;
    private Context mContext;
    public  int mMaxAnimationCount = 20;
    private int lastAnimationPosition = -1;
    public ImageWithCheckboxAdapter(Context context, List<String> images){
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
    public boolean hasStableIds() {
        return true;
    }

    private void runAnimationView(View view, int position){
        if (position>= mMaxAnimationCount -1){
            return;
        }
        if(position>lastAnimationPosition){
            lastAnimationPosition = position;
            view.setScaleX(0.3f);
            view.setScaleY(0.3f);
            view.setAlpha(0.1f);
            view.animate().scaleX(1).scaleY(1).alpha(1).setDuration(300).setInterpolator(new DecelerateInterpolator(2.0f))
                    .setStartDelay(100*position)
                    .start();

        }
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null && convertView.getTag()!=null){
            viewHolder = (ViewHolder)convertView.getTag();
        }
        else {
            convertView = ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(R.layout.item_image_with_checkbox, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mCheckableLayout = (CheckableRelativeLayout) convertView.findViewById(R.id.cl_panel);
            viewHolder.mCheckBox = (CheckBox)convertView.findViewById(R.id.cb_select);
            viewHolder.mSquareImageView = (SquareImageView)convertView.findViewById(R.id.siv_image);
            viewHolder.mCheckableLayout.setCheckbox(viewHolder.mCheckBox);
            convertView.setTag(viewHolder);
        }

        ImageLoader.getInstance().displayImage((String)getItem(position), viewHolder.mSquareImageView);
        if (getItemViewType(position)==0){
            viewHolder.mCheckBox.setVisibility(View.GONE);
        }
        runAnimationView(convertView, position);
        return convertView;
    }

    private static class ViewHolder {
        CheckBox mCheckBox;
        SquareImageView mSquareImageView;
        CheckableRelativeLayout mCheckableLayout;

    }

    public void setMaxAnimationCount(int count){
        mMaxAnimationCount = count;
    }

}
