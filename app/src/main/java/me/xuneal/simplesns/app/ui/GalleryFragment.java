package me.xuneal.simplesns.app.ui;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.*;
import android.view.animation.Animation;
import android.widget.TextView;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.util.Utils;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends DialogFragment {
    private static final String IMAGE_URLS = "image_urls";
    private static final String POSITION = "position";

    private ViewPager mViewPager;
    private TextView mTvIndicator;
    private ArrayList<String> mImageUrls;
    private int mPosition;
    private View mRootView;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GalleryFragment.
     */
    public static GalleryFragment newInstance(ArrayList<String> imageUrls, int pos) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(IMAGE_URLS, imageUrls);
        args.putInt(POSITION, pos);
        fragment.setArguments(args);
        return fragment;
    }

    public GalleryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mImageUrls = getArguments().getStringArrayList(IMAGE_URLS);
            mPosition = getArguments().getInt(POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_gallery_container, null, false);
        mTvIndicator = (TextView) mRootView.findViewById(R.id.tv_indicator);
        mRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                mRootView.setScaleX(0.1f);
                mRootView.setScaleY(0.1f);
                mRootView.setAlpha(0);

                mRootView.setPivotX(Utils.getScreenWidth(getActivity()) / 2);
                mRootView.setPivotY(Utils.getScreenHeight(getActivity()) / 2);


                mRootView.animate().scaleX(1).scaleY(1).alpha(1).setDuration(300).start();

                return true;
            }
        });


        mViewPager = (ViewPager) mRootView.findViewById(R.id.view_pager);
        mTvIndicator.setText(String.format("%d/%d", mPosition+1, mImageUrls.size()));
        ImagePageAdapter adapter = new ImagePageAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mPosition);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                mTvIndicator.setText(String.format("%d/%d", i+1, mImageUrls.size()));

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        return mRootView;
    }

    private View mOnDismissAnimationView = null;
    private Animation mOnDismissAnimation = null;
    private boolean mIsAnimationRunning = false;
    private boolean mIsDialogDismissed = false;

    @Override
    public void onStart() {
        mIsDialogDismissed = false;
        mIsAnimationRunning = false;
        super.onStart();
    }

    @Override
    public void dismiss() {
        if (!mIsDialogDismissed) {
            mIsDialogDismissed = true;
            doDismiss();
        }
    }

    private void doDismiss() {
        if (mIsAnimationRunning == false
                ) {
            mIsAnimationRunning = true;

            mRootView.animate().alpha(0).scaleX(0.1f).scaleY(0.1f).setDuration(300).withEndAction(new Runnable() {
                @Override
                public void run() {
                    parentDismiss();
                    mIsAnimationRunning = false;
                }
            }).start();
        } else {
            super.dismiss();
        }
    }

    private void parentDismiss() {
        super.dismiss();
    }


    class TapGestureListener extends GestureDetector.SimpleOnGestureListener {


        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            GalleryFragment.this.dismiss();
            return super.onSingleTapUp(e);
        }
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            GalleryFragment.this.dismiss();

        }
    };

    class ImagePageAdapter extends FragmentPagerAdapter {

        public ImagePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mImageUrls.size();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            ImageFragment fragment = ImageFragment.newInstance(mImageUrls.get(i));
            return fragment;
        }


    }


}
