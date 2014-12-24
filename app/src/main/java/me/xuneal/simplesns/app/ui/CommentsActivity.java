package me.xuneal.simplesns.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.avos.avoscloud.*;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.model.Account;
import me.xuneal.simplesns.app.model.Comment;
import me.xuneal.simplesns.app.model.Tweet;
import me.xuneal.simplesns.app.ui.components.SendCommentButton;
import me.xuneal.simplesns.app.util.Utils;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

public class CommentsActivity extends BaseActivity implements View.OnClickListener, SendCommentButton.OnSendClickListener  {
    public static final String ARG_DRAWING_START_LOCATION = "arg_drawing_start_location";
    public static final String ARG_POST_ID = "arg_post_id";

    private List<Comment> mComments = new ArrayList<>();

    private RecyclerView mRvComments;
    private EditText mComment;
    private SendCommentButton mSend;
    private CommentAdapter mCommentAdapter;

    private int drawingStartLocation;
    private LinearLayout mRootPanel;
    private LinearLayout mSendPanel;
    private Tweet mTweet;
    private long mPostId;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2014-12-11 13:48:44 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mRootPanel = (LinearLayout) findViewById(R.id.root_panel);
        mRvComments = (RecyclerView) findViewById(R.id.rv_comments);
        mComment = (EditText) findViewById(R.id.comment);
        mSendPanel = (LinearLayout) findViewById(R.id.send_panel);
        mSend = (SendCommentButton) findViewById(R.id.scb_send);
        mSend.setOnSendClickListener(this);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2014-12-11 13:48:44 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == mSend) {
            // Handle clicks for mSend
            if (TextUtils.isEmpty(mComment.getText().toString())) return;
            Comment comment = new Comment();
            comment.setPoster(AVUser.getCurrentUser(Account.class));
            comment.setPostTime(LocalDateTime.now().toString("yyyy-MM-dd HH:mm"));
            comment.setContent(mComment.getText().toString());
            comment.setTweet(mTweet);
            comment.saveInBackground();
            mCommentAdapter.addItem(comment);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        findViews();
        mRvComments.setLayoutManager(new LinearLayoutManager(this));
        mCommentAdapter = new CommentAdapter(mComments);
        mRvComments.setAdapter(mCommentAdapter);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawingStartLocation = getIntent().getIntExtra(ARG_DRAWING_START_LOCATION, 0);
        AVQuery<Tweet> query = new AVQuery<Tweet>(Tweet.TABLE_NAME);
        mRootPanel.setVisibility(View.VISIBLE);
        mRootPanel.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRootPanel.getViewTreeObserver().removeOnPreDrawListener(this);
                startIntroAnimation();
                return true;
            }
        });
        query.include("comments").getInBackground(getIntent().getStringExtra(ARG_POST_ID), new GetCallback<Tweet>() {
            @Override
            public void done(Tweet tweet, AVException e) {
                mTweet = tweet;
                mTweet.getComments(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        if (list !=null) {
                            for (AVObject comment : list) {
                                mComments.add((Comment) comment);
                            }
                        }

                    }
                });
            }
        });



    }


    private void startIntroAnimation() {
        ViewCompat.setElevation(getActionBarToolbar(), 0);
        mRootPanel.setScaleY(0.1f);
        mRootPanel.setPivotY(drawingStartLocation);
        mSendPanel.setTranslationY(100);

        mRootPanel.animate()
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
        mCommentAdapter.updateItems();

        mSendPanel.animate()
                .translationY(0)
                .setDuration(200)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    @Override
    public void onBackPressed() {
        ViewCompat.setElevation(getActionBarToolbar(), 0);
        mRootPanel.animate()
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
            comment.setPoster(AVUser.getCurrentUser(Account.class));
            comment.setPostTime(LocalDateTime.now().toString("yyyy-MM-dd HH:mm"));
            comment.setContent(mComment.getText().toString());
            comment.setTweet(mTweet);
            comment.saveInBackground();
            mCommentAdapter.addItem(comment);
            mCommentAdapter.setAnimationsLocked(false);
            mCommentAdapter.setDelayEnterAnimation(false);
            if (mRvComments.getChildCount()>1)
                mRvComments.smoothScrollBy(0, mRvComments.getChildAt(0).getHeight() * mCommentAdapter.getItemCount());

            mComment.setText(null);
            mSend.setCurrentState(SendCommentButton.STATE_DONE);
        }
    }

    private boolean validateComment() {
        if (TextUtils.isEmpty(mComment.getText())) {
            mSend.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_error));
            return false;
        }

        return true;
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_comments, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}



