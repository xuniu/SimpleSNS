package me.xuneal.simplesns.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
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
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.model.Account;
import me.xuneal.simplesns.app.model.Tweet;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVAnalytics.trackAppOpened(getIntent());
        setContentView(R.layout.activity_main);

        mRecyclerView = (ObservableRecyclerView) findViewById(R.id.my_recycler_view);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mTweetAdapter = new TweetAdapter(this, new ArrayList<Tweet>());

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
        int icon[] = { R.drawable.ic_profile, R.drawable.ic_setting, R.drawable.ic_about};

        for (int i = 0; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            item.setTag(i);
            item.setOnClickListener(this);
            mResideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }

        getActionBarToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mResideMenu.isOpened()){
                    mResideMenu.closeMenu();
                } else {
                    mResideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
                }

            }
        });


         mPtrFrame = (PtrFrameLayout) findViewById(R.id.ptr_frame);

        // header
        final MaterialHeader header = new MaterialHeader(this);
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, Utils.dpToPx(15), 0, Utils.dpToPx(10));
        header.setPtrFrameLayout(mPtrFrame);

        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh(true);
            }
        }, 100);

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                if (AVUser.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    loadData();
                }
            }
        });
    }

    private void loadData(){


        AVQuery<Tweet> query = AVObject.getQuery(Tweet.class);
        query.include("images");
        query.orderByDescending("updatedAt");
        query.include(Tweet.POSTER);
        query.findInBackground(new FindCallback<Tweet>() {
            @Override
            public void done(final List<Tweet> tweets, AVException e) {
                mPtrFrame.refreshComplete();
                if (tweets==null) return;
                AVUser user = AVUser.getCurrentUser();
                AVRelation<AVObject> relation = user.getRelation("likes");
                relation.getQuery().whereContainedIn("likeUser", tweets).findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        for (AVObject post : list){

                            for (Tweet tweet : tweets){
                                if (post.getObjectId().equals(tweet.getObjectId())){
                                    tweet.setLike(true);
                                }
                            }
                        }
                    }
                });
                mTweetAdapter.getTweets().addAll(tweets);
                mTweetAdapter.notifyDataSetChanged();
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
                break;
            default:
                break;
        }
    }
}
