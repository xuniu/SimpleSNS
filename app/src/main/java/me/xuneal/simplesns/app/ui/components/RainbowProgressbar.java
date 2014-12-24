package me.xuneal.simplesns.app.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import com.nineoldandroids.view.ViewHelper;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.util.Utils;

/**
 * Created by xyz on 2014/12/23.
 */
public class RainbowProgressbar extends View {
    private float mDegree = 0;
    private boolean mRefresh = false;
    Bitmap mDrawBitmap;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (mDegree>=360){
                mDegree=0;
            } else {
                mDegree+=10;
            }
            RainbowProgressbar.this.invalidate();
            if (mRefresh)
                RainbowProgressbar.this.postDelayed(this, 20);
        }
    };
    private int mTranslationY;
    private int mTransled;
    OnRefreshListener mOnRefreshListener;

    public OnRefreshListener getOnRefreshListener() {
        return mOnRefreshListener;
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    public RainbowProgressbar(Context context) {
        super(context);
        init();
    }

    public RainbowProgressbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();


    }

    public RainbowProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RainbowProgressbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init() {
        mDrawBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_rainbow);
        mTranslationY = Utils.dpToPx(32+50) * -1;
        ViewHelper.setTranslationY(this, mTranslationY);
    }

    public void setScrollDistance(int scrollDistance){
        if (mRefresh) return;
        if (Math.abs(mTransled + scrollDistance)<= Math.abs(mTranslationY)) {
            mTransled += (scrollDistance);
            ViewHelper.setTranslationY(this, mTranslationY + Math.abs(mTransled));

        }
        if (mDegree <= 0) {
            mDegree = 360;
        } else {
            mDegree -= 10;
        }
        invalidate();

//        } else {
//            if (mOnRefreshListener!=null) mOnRefreshListener.onRefresh();
//            if (!mRefresh) {
//                mRefresh = true;
//                postDelayed(runnable, 20);
//            }
//        }
    }

    public void touchRelease(){
        if (Math.abs(mTransled) >= Math.abs(mTranslationY) && !mRefresh) {
            if (mOnRefreshListener!=null) mOnRefreshListener.onRefresh();
            if (!mRefresh) {
                mRefresh = true;
                postDelayed(runnable, 20);
            }
        } else if (!mRefresh) {
            animate().translationY(mTranslationY);
        }
    }


    public void setRefresh(boolean refresh){
        if (refresh){
            mRefresh = refresh;
            animate().translationY(0).setDuration(100).start();
            this.postDelayed(runnable, 20);
            } else {
            animate().translationY(-1* Utils.dpToPx(32+50)).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mRefresh = false;
                }
            });
            mTransled=0;
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.rotate(mDegree,mDrawBitmap.getHeight()/2,mDrawBitmap.getWidth()/2);

        canvas.drawBitmap(mDrawBitmap,0,0,null);
        canvas.restore();


    }

    public interface OnRefreshListener{
        void onRefresh();
    }
}
