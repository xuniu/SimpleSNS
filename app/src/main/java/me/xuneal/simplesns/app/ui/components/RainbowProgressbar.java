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
            RainbowProgressbar.this.invalidate();
            if (mRefresh)
                RainbowProgressbar.this.postDelayed(this, 20);
        }
    };
    private int mTranslationY;

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
        mTranslationY = Utils.dpToPx(32+50+48) * -1;
        ViewHelper.setTranslationY(this, mTranslationY);
    }

    public void setTranslationY(int translationY){
        Log.d("RAINBOW", String.valueOf(translationY));
        if (Math.abs(translationY*3)<= Math.abs(mTranslationY)) {
            ViewHelper.setTranslationY(this, mTranslationY + Math.abs(translationY*3));
            if (mDegree<=0){
                mDegree=360;
            } else {
                mDegree -=10;
            }
        } else {
            if (!mRefresh) {
                mRefresh = true;
                postDelayed(runnable, 20);
            }
        }
    }


    public void setRefresh(boolean refresh){
        mRefresh = refresh;
        if (refresh){
        this.postDelayed(runnable, 20);
            } else {
            animate().translationY(-1* Utils.dpToPx(32+50+48));
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.rotate(mDegree,mDrawBitmap.getHeight()/2,mDrawBitmap.getWidth()/2);

        canvas.drawBitmap(mDrawBitmap,0,0,null);
        canvas.restore();
        if (mDegree>=360){
            mDegree=0;
        } else {
            mDegree+=10;
        }

    }
}
