package me.xuneal.simplesns.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.*;
import android.view.*;
import android.view.animation.OvershootInterpolator;
import android.widget.*;
import com.avos.avoscloud.*;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.melnykov.fab.FloatingActionButton;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import in.srain.cube.views.ptr.PtrFrameLayout;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.model.Account;
import me.xuneal.simplesns.app.model.Tweet;
import me.xuneal.simplesns.app.ui.components.RainbowProgressbar;
import me.xuneal.simplesns.app.ui.components.ResizeAnimation;
import me.xuneal.simplesns.app.util.Utils;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity
implements TweetAdapter.OnFeedItemClickListener, View.OnClickListener {

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private ObservableRecyclerView mRecyclerView;
    private TweetAdapter mTweetAdapter;
    private MenuItem profileMenuItem;
    private ImageView mLogo;
    private FloatingActionButton fab;

    boolean pendingIntroAnimation;
    private ResideMenu mResideMenu;
    private PtrFrameLayout mPtrFrame;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RainbowProgressbar mRainbowProgressBar;
    private ImageView mIvHeader;
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVAnalytics.trackAppOpened(getIntent());
        setContentView(R.layout.activity_main);

        mRecyclerView = (ObservableRecyclerView) findViewById(R.id.my_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mTweetAdapter = new TweetAdapter(this, new ArrayList<Tweet>());
        mRainbowProgressBar = (RainbowProgressbar) findViewById(R.id.rpb);
        FrameLayout frameLayout = (FrameLayout) LayoutInflater.from(this).inflate(R.layout.item_header, null);
        mIvHeader = (ImageView) frameLayout.findViewById(R.id.iv_header);
        mTweetAdapter.setHeader(frameLayout);
        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    ResizeAnimation animation = new ResizeAnimation(mIvHeader, Utils.dpToPx(300));
                    animation.setDuration(200);
                    mIvHeader.startAnimation(animation);
                    mRainbowProgressBar.touchRelease();

                } else {
                    mGestureDetector.onTouchEvent(event);
                }
                return false;
            }
        });

        mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (e2==null || e1==null) return false;
                if (e2.getRawY()> e1.getRawY()){
                    FrameLayout.LayoutParams layoutParams = new FrameLayout
                            .LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                            mIvHeader.getHeight()-(int)(distanceY/2));
                    mIvHeader.setLayoutParams(layoutParams);
                    mRainbowProgressBar.setScrollDistance((int)distanceY);

                    return true;
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PostActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        fab.attachToRecyclerView(mRecyclerView);

        mLogo = (ImageView) findViewById(R.id.logo);

        mRainbowProgressBar.setOnRefreshListener(new RainbowProgressbar.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRainbowProgressBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRainbowProgressBar.setRefresh(false);
                    }
                }, 4000);
            }
        });


        getActionBarToolbar().setNavigationIcon(R.drawable.ic_menu_white);

        if (savedInstanceState == null) {
            pendingIntroAnimation = true;
        }

        mRecyclerView.setAdapter(mTweetAdapter);

        mTweetAdapter.setOnFeedItemClickListener(this);

        // attach to current activity;
        mResideMenu = new ResideMenu(this);
        mResideMenu.setBackground(R.drawable.bg_resize_menu);
        mResideMenu.attachToActivity(this);

        // create menu items;
        String titles[] = { "Profile", "Setting", "About Me",  };
        int icon[] = { R.drawable.ic_profile, R.drawable.ic_settings, R.drawable.ic_about_me};

        for (int i = 0; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            item.setTag(i);
            item.setOnClickListener(this);
            mResideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }

        getActionBarToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mResideMenu.isOpened()) {
                    mResideMenu.closeMenu();
                } else {
                    mResideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                }

            }
        });

        loadData();
    }

    private void loadData(){


        AVQuery<Tweet> query = AVObject.getQuery(Tweet.class);
        query.include("images");
        query.orderByDescending("updatedAt");
        query.include(Tweet.POSTER);
        query.findInBackground(new FindCallback<Tweet>() {
            @Override
            public void done(final List<Tweet> tweets, AVException e) {

                mRainbowProgressBar.setRefresh(false);
                if (tweets==null) return;

                List<String> tweetIds = new ArrayList<String>(tweets.size());
                for (Tweet tweet: tweets){
                    tweetIds.add(tweet.getObjectId());
                }
                try {
                AVUser user = AVUser.getCurrentUser();
                AVRelation<AVObject> relation = user.getRelation("likes");
                relation.getQuery().whereContainedIn("objectId", tweetIds).findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        for (AVObject post : list){

                            for (Tweet tweet : tweets){
                                if (post.getObjectId().equals(tweet.getObjectId())){
                                    tweet.setLike(true);
                                }
                            }
                        }

                        mTweetAdapter.getTweets().addAll(tweets);
                        mTweetAdapter.updateItems();
                    }
                });}
                catch (Exception ignored){}

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        profileMenuItem = menu.findItem(R.id.action_profile);
        profileMenuItem.setActionView(R.layout.menu_item_view);
        profileMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            }
        });
        if (pendingIntroAnimation) {
            pendingIntroAnimation = false;
            startIntroAnimation();
        }
        return true;
    }

    private void startIntroAnimation() {
        fab.setTranslationY(2 * getResources().getDimensionPixelOffset(R.dimen.btn_fab_size));

        android.support.v7.widget.Toolbar toolbar = getActionBarToolbar();


        int actionbarSize = Utils.dpToPx(56);
        getActionBarToolbar().setTranslationY(-actionbarSize);
        mLogo.setTranslationY(-actionbarSize);
        profileMenuItem.getActionView().setTranslationY(-actionbarSize);

        toolbar.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(300);
        mLogo.animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(400);
        profileMenuItem.getActionView().animate()
                .translationY(0)
                .setDuration(ANIM_DURATION_TOOLBAR)
                .setStartDelay(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startContentAnimation();
                    }
                })
                .start();
    }

    private void startContentAnimation() {
        fab.animate()
                .translationY(0)
                .setInterpolator(new OvershootInterpolator(1.f))
                .setStartDelay(300)
                .setDuration(ANIM_DURATION_FAB)
                .start();

        mTweetAdapter.updateItems();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        if (id==R.id.action_profile){

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==0 && resultCode== RESULT_OK) {
            Tweet tweet = new Tweet();
            tweet.setContent(data.getStringExtra("content"));
            tweet.setImageUrl(data.getStringArrayListExtra("images"));
            tweet.setPoster(AVUser.getCurrentUser(Account.class));
            tweet.setPostTime(LocalDateTime.now().toString());
            tweet.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if(e!=null){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Save ok", Toast.LENGTH_SHORT).show();

                    }
                }
            });

            mTweetAdapter.getTweets().add(0, tweet);
            mTweetAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onImageClick(List<String> imageUrls, int pos) {
        GalleryFragment fragment = GalleryFragment.newInstance(new ArrayList<String>(imageUrls), pos);
        fragment.setCancelable(true);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE | DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_NoTitleBar_Fullscreen );
        fragment.show(getSupportFragmentManager(), "gallery");
    }

    @Override
    public void onCommentsClick(View v, int position) {
        Intent intent = new Intent(this, CommentsActivity.class);
        int[] startingLocation = new int[2];
        v.getLocationOnScreen(startingLocation);
        intent.putExtra(CommentsActivity.ARG_DRAWING_START_LOCATION, startingLocation[1]);
        intent.putExtra(CommentsActivity.ARG_POST_ID, mTweetAdapter.getTweets().get(position).getObjectId());
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    @Override
    public void onClick(View v) {
        int id = (int)v.getTag();
        switch (id ){
            case 0:
                startActivity(new Intent(this, ProfileActivity.class));
                mResideMenu.closeMenu();
                break;
            case 2:
                startActivity(new Intent(this, AboutMeActivity.class));
                mResideMenu.closeMenu();
            default:
                break;
        }
    }
}
