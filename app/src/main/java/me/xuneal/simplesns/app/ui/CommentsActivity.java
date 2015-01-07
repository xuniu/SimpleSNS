package me.xuneal.simplesns.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.avos.avoscloud.*;
import it.gmariotti.recyclerview.itemanimator.SlideInOutBottomItemAnimator;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.model.Account;
import me.xuneal.simplesns.app.model.Comment;
import me.xuneal.simplesns.app.model.Tweet;
import me.xuneal.simplesns.app.ui.components.SendCommentButton;
import me.xuneal.simplesns.app.util.AccountUtils;
import me.xuneal.simplesns.app.util.Utils;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends BaseActivity implements View.OnClickListener, SendCommentButton.OnSendClickListener {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    public static final String ARG_POST_ID = "arg_post_id";

    private List<Comment> mComments = new ArrayList<>();

    private RecyclerView mRvComments;
    private EditText mTvCommentContent;
    private SendCommentButton mScbSend;
    private CommentAdapter mCommentAdapter;

    private int drawingStartLocation;
    private LinearLayout mLlRootPanel;
    private LinearLayout mLlSendPanel;
    private Tweet mTweet;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2014-12-11 13:48:44 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mLlRootPanel = (LinearLayout) findViewById(R.id.ll_root_panel);
        mRvComments = (RecyclerView) findViewById(R.id.rv_comments);
        mTvCommentContent = (EditText) findViewById(R.id.tv_comment_content);
        mLlSendPanel = (LinearLayout) findViewById(R.id.ll_send_panel);
        mScbSend = (SendCommentButton) findViewById(R.id.scb_send);
        mScbSend.setOnSendClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2014-12-11 13:48:44 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == mScbSend) {
            // Handle clicks for mScbSend
            if (TextUtils.isEmpty(mTvCommentContent.getText().toString())) return;
            Comment comment = new Comment();
            comment.setPoster(AVUser.getCurrentUser(Account.class));
            comment.setPostTime(LocalDateTime.now().toString("yyyy-MM-dd HH:mm"));
            comment.setContent(mTvCommentContent.getText().toString());
            comment.setTweet(mTweet);
            comment.saveInBackground();
            mCommentAdapter.addItem(comment);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem profileMenuItem = menu.findItem(R.id.action_profile);
        profileMenuItem.setActionView(R.layout.menu_item_view);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        findViews();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRvComments.setLayoutManager(new LinearLayoutManager(this));
        mRvComments.setHasFixedSize(true);

        mCommentAdapter = new CommentAdapter(mComments);

        mRvComments.setAdapter(mCommentAdapter);
        mRvComments.setItemAnimator(new SlideInOutBottomItemAnimator(mRvComments));

        mRvComments.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRvComments.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    mCommentAdapter.setAnimationsLocked(true);
                }
            }
        });


        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        mLlRootPanel.setVisibility(View.VISIBLE);
        mLlRootPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mLlRootPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                startIntroAnimation();
                return true;
            }
        });


    }


    private void startIntroAnimation() {
        ViewCompat.setElevation(getActionBarToolbar(), 0);
        mLlRootPanel.setScaleY(0.1f);
        mLlRootPanel.setPivotY(drawingStartLocation);
        mLlSendPanel.setTranslationY(100);
        mLlRootPanel.animate()
                .scaleY(1)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(getActionBarToolbar(), Utils.dpToPx(8));
                        animationContent();
                    }
                })
                .start();

    }

    private void animationContent() {
        mLlSendPanel.animate()
                .translationY(0)
                .setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .start();
        AVQuery<Tweet> tweetQuery = new AVQuery<>(Tweet.TABLE_NAME);
        tweetQuery.getInBackground(getIntent().getStringExtra(ARG_POST_ID), new GetCallback<Tweet>() {
            @Override
            public void done(Tweet tweet, AVException e) {
                mTweet = tweet;
                AVQuery<Comment> query = new AVQuery<Comment>(Comment.TABLE_NAME);
                query.whereEqualTo("tweet", tweet);
                query.include("poster").findInBackground(new FindCallback<Comment>() {
                    @Override
                    public void done(List<Comment> list, AVException e) {
                        for (Comment comment : list) {
                            mComments.add(comment);
                        }
                        mCommentAdapter.updateItems();
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(getActionBarToolbar(), 0);
        mLlRootPanel.animate()
                .translationY(Utils.getScreenHeight(this))
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        CommentsActivity.super.onBackPressed();
                        overridePendingTransition(0, 0);
                    }
                })
                .start();
    }

    @Override
    public void onSendClickListener(View v) {
        if (validateComment()) {
            Comment comment = new Comment();
            comment.setPoster(AccountUtils.getDefaultAccount());
            comment.setPostTime(LocalDateTime.now().toString("yyyy-MM-dd HH:mm"));
            comment.setContent(mTvCommentContent.getText().toString());
            comment.setTweet(mTweet);
            comment.saveInBackground();

            mCommentAdapter.addItem(comment);

            if (mRvComments.getChildCount() > 1)
                mRvComments.smoothScrollBy(0, mRvComments.getChildAt(0).getHeight() * mCommentAdapter.getItemCount());

            mTvCommentContent.setText(null);
            mScbSend.setCurrentState(SendCommentButton.STATE_DONE);
        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(mTvCommentContent.getText())) {
            mScbSend.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }


}



