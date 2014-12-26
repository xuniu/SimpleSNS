package me.xuneal.simplesns.app.ui;

import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.model.Account;
import me.xuneal.simplesns.app.model.Tweet;
import me.xuneal.simplesns.app.util.AccountUtils;

import java.util.List;

public class ProfileActivity extends BaseActivity implements ObservableScrollViewCallbacks {

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;
    private ImageView mCover;
    private CircleImageView mAvatar;
    private TextView mNickname;
    private ObservableRecyclerView mTweetList;
    Account mAccount;
    private View mImagePanel;
    private int mParallaxImageHeight;
    private TweetAdapter mTweetAdapter;
    private int mFlexibleSpaceImageHeight;
    private int mActionBarSize;
    private TextView mTitleView;
    private View mListBackgroundView;
    private View mOverlayView;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2014-11-26 20:53:07 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mCover = (ImageView) findViewById(R.id.cover);
        mAvatar = (CircleImageView) findViewById(R.id.avatar);
        mNickname = (TextView) findViewById(R.id.nickname);
        mImagePanel = findViewById(R.id.image_panel);
        mTweetList = (ObservableRecyclerView) findViewById(R.id.tweet_list);
    }

    private void setBackgroundAlpha(View view, float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int) (alpha * 255))) << 24;
        int rgb = 0x00ffffff & baseColor;
        view.setBackgroundColor(a + rgb);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getActionBarSize();
        getActionBarToolbar().setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mTweetList.setScrollViewCallbacks(this);

        mTweetList.setLayoutManager(new LinearLayoutManager(this));
        setBackgroundAlpha(getActionBarToolbar(), 0, getResources().getColor(R.color.color_primary));

        mOverlayView = findViewById(R.id.overlay);
        // Set padding view for ListView. This is the flexible space.
        final View paddingView = new View(this);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                mFlexibleSpaceImageHeight);
        paddingView.setLayoutParams(lp);

        // This is required to disable header's list selector effect
        paddingView.setClickable(true);

        mTitleView = (TextView) findViewById(R.id.title);
        mTitleView.setText(getTitle());
        setTitle(null);


        mAccount = AccountUtils.getDefaultAccount();
        if (mAccount == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {

            ImageLoader.getInstance().displayImage(mAccount.getAvatar(), mAvatar);
            ImageLoader.getInstance().displayImage("assets://bg_default_cover.png", mCover);
            mNickname.setText(mAccount.getNickName());
        }


        AVQuery<Tweet> query = new AVQuery<Tweet>(Tweet.TABLE_NAME);
        query.include("images");
        query.include(Tweet.POSTER);
        query.orderByDescending("updatedAt");
        query.whereEqualTo(Tweet.POSTER, AVUser.getCurrentUser());
        query.findInBackground(new FindCallback<Tweet>() {
            @Override
            public void done(List<Tweet> list, AVException e) {
                mTweetAdapter = new TweetAdapter(ProfileActivity.this, list, true);
                mTweetAdapter.setHeader(paddingView);
                mTweetList.setAdapter(mTweetAdapter);
                mTweetAdapter.updateItems();
            }
        });


        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ProfileActivity.this, PickPhotos.class), 0);
            }
        });

        // mListBackgroundView makes ListView's background except header view.
        mListBackgroundView = findViewById(R.id.list_background);
        final View contentView = getWindow().getDecorView().findViewById(android.R.id.content);
        contentView.post(new Runnable() {
            @Override
            public void run() {
                // mListBackgroundView's should fill its parent vertically
                // but the height of the content view is 0 on 'onCreate'.
                // So we should get it with post().
                mListBackgroundView.getLayoutParams().height = contentView.getHeight();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_login_out) {
            AVUser.logOut();
            startActivity(new Intent(this, LoginActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0 && data != null) {

            String imageFilePath = data.getStringExtra("url");

            ImageLoader.getInstance().displayImage(imageFilePath, mAvatar);
            mAccount.setAvatar(imageFilePath.replace("file://", ""));

            mAccount.saveInBackground();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean b, boolean b1) {
// Translate overlay and image
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mOverlayView, Math.max(minOverlayTransitionY, Math.min(0, -scrollY)));
        ViewHelper.setTranslationY(mImagePanel, Math.max(minOverlayTransitionY, Math.min(0, -scrollY / 2)));

        // Translate list background
        ViewHelper.setTranslationY(mListBackgroundView, Math.max(0, -scrollY + mFlexibleSpaceImageHeight));

        // Change alpha of overlay
        ViewHelper.setAlpha(mOverlayView, Math.max(0, Math.min(1, (float) scrollY / flexibleRange)));

        // Scale title text
        float scale = 1 + Math.max(0, Math.min(MAX_TEXT_SCALE_DELTA, (flexibleRange - scrollY) / flexibleRange));
        ViewHelper.setPivotX(mTitleView, 0);
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);

        // Translate title text
        int maxTitleTranslationY = (int) (mFlexibleSpaceImageHeight - mTitleView.getHeight() * scale);
        int titleTranslationY = maxTitleTranslationY - scrollY;
            titleTranslationY = Math.max(0, titleTranslationY);
        ViewHelper.setTranslationY(mTitleView, titleTranslationY);

    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
