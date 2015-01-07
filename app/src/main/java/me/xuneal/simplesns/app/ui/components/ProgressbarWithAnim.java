package me.xuneal.simplesns.app.ui.components;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGBuilder;
import me.xuneal.simplesns.app.R;

/**
 * Created by xyz on 2014/12/30.
 */
public class ProgressbarWithAnim extends View {
    private Paint mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mCenterPointX;
    private float mCenterPointY;
    private float mRadius;
    private Path mClipPath;
    private Rect mCheckMarkRect;
    private int mProgress = 0;
    private RectF mArcRect;
    private boolean finished;
    private float mMove = 0.1f;
    private float mTransitionY;
    private Runnable mRunAfterSuccess;

    public ProgressbarWithAnim(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProgressbarWithAnim(Context context) {
        super(context);
    }

    public void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mArcPaint.setStrokeWidth(10f);
        mArcPaint.setColor(getResources().getColor(R.color.blue));
        mArcPaint.setStyle(Paint.Style.STROKE);

        mCirclePaint.setStrokeWidth(3f);
        mCirclePaint.setColor(getResources().getColor(R.color.lime));
        mCirclePaint.setStyle(Paint.Style.FILL);
        mCenterPointX = getWidth() / 2;
        mCenterPointY = getHeight() / 2;
        mRadius = 100;
        mClipPath = new Path();
        mClipPath.addCircle(mCenterPointX, mCenterPointY, 100, Path.Direction.CW);
        mCheckMarkRect = new Rect((int) mCenterPointX - 90, (int) mCenterPointY + 90, (int) mCenterPointX + 90, (int) mCenterPointY + 270);
        mArcRect = new RectF(mCenterPointX - 100f, mCenterPointY - 100f, mCenterPointX + 100f, mCenterPointY + 100f);
        mTransitionY = (mRadius - 10) * 2;
        postDelayed(mRunnable, 33);
    }

    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            if (!finished && mProgress >= 100) {
                invalidate();
                postDelayed(mRunnable, 33);
            }
        }
    };

    public void setProgress(int progress) {
        mProgress = progress;
        if (!finished && mProgress >= 100) {
            postDelayed(mRunnable, 33);
        }
        invalidate();
    }


    /** 设置上传完成的callBack */
    public void setCompletedListener(Runnable runAfterSuccess) {
        mRunAfterSuccess = runAfterSuccess;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.clipPath(mClipPath);

        SVG svg = new SVGBuilder()
                .readFromResource(getResources(), R.raw.svg_check_mark)
                .build();

        if (mProgress >= 100) {
            mTransitionY -= (mTransitionY * mMove);
            canvas.drawCircle(mCenterPointX, mCenterPointY + mTransitionY, mRadius - 10, mCirclePaint);

            Rect rect = new Rect(mCheckMarkRect.left, (int) (mCheckMarkRect.top - (180 * mMove)),
                    mCheckMarkRect.right, (int) (mCheckMarkRect.bottom - (180 * mMove)));
            canvas.drawPicture(svg.getPicture(), rect);
            mMove += 0.1;
            if (mMove > 1) {
                finished = true;
                if (mRunAfterSuccess != null)
                    postDelayed(mRunAfterSuccess, 500);
            }
        }
        canvas.drawArc(mArcRect, 0, 3.6f * mProgress, false, mArcPaint);
        canvas.restore();

    }
}
