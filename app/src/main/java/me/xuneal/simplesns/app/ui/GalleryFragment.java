package me.xuneal.simplesns.app.ui;


import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.view.*;

import com.nostra13.universalimageloader.core.ImageLoader;
import me.xuneal.simplesns.app.R;
import uk.co.senab.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GalleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GalleryFragment extends DialogFragment {
    private static final String IMAGE_URLS = "image_urls";
    private static final String POSITION = "position";

    private ViewPager mViewPager;

    private ArrayList<String> mImageUrls;
    private int mPosition;


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
        View view = inflater.inflate(R.layout.fragment_gallery_container, null, false);
        mViewPager = (ViewPager) view.findViewById(R.id.view_pager);
        ImagePageAdapter adapter = new ImagePageAdapter(getChildFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(mPosition);
//        mViewPager.setOnTouchListener(new View.OnTouchListener() {
//            float oldX = 0, newX = 0, sens = 5;
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        oldX = event.getX();
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        newX = event.getX();
//                        if (Math.abs(oldX - newX) < sens) {
//                            GalleryFragment.this.dismiss();
//                            return true;
//                        }
//                        oldX = 0;
//                        newX = 0;
//                        break;
//                }
//
//                return false;
//            }
//        });

//      mViewPager.setOnClickListener(mOnClickListener);
//        final GestureDetector tapGestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {
//            @Override
//            public boolean onSingleTapConfirmed(MotionEvent e) {
//                GalleryFragment.this.dismiss();
//                return false;
//            }
//        });
//
//        mViewPager.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                tapGestureDetector.onTouchEvent(event);
//                return false;
//            }
//        });

//

        return view;
    }

    class TapGestureListener extends GestureDetector.SimpleOnGestureListener{


        @Override
        public boolean onSingleTapConfirmed (MotionEvent e) {
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
