package me.xuneal.simplesns.app.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.text.format.DateUtils;
import android.view.*;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.*;
import com.avos.avoscloud.*;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.hdodenhof.circleimageview.CircleImageView;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.model.Account;
import me.xuneal.simplesns.app.model.Tweet;
import me.xuneal.simplesns.app.util.Utils;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends BaseActivity {

    private static final int ANIM_DURATION_TOOLBAR = 300;
    private static final int ANIM_DURATION_FAB = 400;

    private ObservableRecyclerView mRecyclerView;
    private MyAdapter mMyAdapter;
    private MenuItem profileMenuItem;
    private ImageView mLogo;

    private FloatingActionButton fab;

    boolean pendingIntroAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVAnalytics.trackAppOpened(getIntent());
        setContentView(R.layout.activity_main);

        mRecyclerView = (ObservableRecyclerView) findViewById(R.id.my_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mMyAdapter = new MyAdapter(this, new ArrayList<Tweet>());

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

        mRecyclerView.setAdapter(mMyAdapter);
        if (AVUser.getCurrentUser() == null){
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            loadData();
        }

    }

    private void loadData(){
        AVQuery<Tweet> query = AVObject.getQuery(Tweet.class);
        query.include("images");
        query.findInBackground(new FindCallback<Tweet>() {
            @Override
            public void done(List<Tweet> tweets, AVException e) {
                if (tweets==null) return;
                mMyAdapter.tweets.addAll(tweets);
                mMyAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        profileMenuItem = menu.findItem(R.id.action_profile);
        profileMenuItem.setActionView(R.layout.menu_item_view);

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

        mMyAdapter.updateItems();
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
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
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

            mMyAdapter.tweets.add(0, tweet);
            mMyAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private static final int ANIMATED_ITEMS_COUNT = 2;

        private Context context;
        private int lastAnimatedPosition = -1;
        private int itemsCount = 0;

        private List<Tweet> tweets;
        public MyAdapter(Context context, List<Tweet> tweets){
            this.tweets = tweets;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_tweet, viewGroup, false);

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }


        private void runEnterAnimation(View view, int position) {
            if (position >= ANIMATED_ITEMS_COUNT - 1) {
                return;
            }

            if (position > lastAnimatedPosition) {
                lastAnimatedPosition = position;
                view.setTranslationY(Utils.getScreenHeight(context));
                view.animate()
                        .translationY(0)
                        .setInterpolator(new DecelerateInterpolator(3.f))
                        .setDuration(700)
                        .start();
            }
        }
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            runEnterAnimation(viewHolder.itemView, i);
            long now = LocalDateTime.now().toDateTime().getMillis();
            Tweet tweet = tweets.get(i);
            ImageLoader.getInstance().displayImage(tweet.getPoster().getAvatar(), viewHolder.ivAvatar);
            viewHolder.tvNickname.setText(tweet.getPoster().getNickName());
            viewHolder.tvContent.setText(tweet.getContent());
            viewHolder.tvPostTime.setText(DateUtils.getRelativeTimeSpanString(
                    LocalDateTime.parse(tweet.getPostTime()).toDateTime().getMillis(),
                    now, 0, DateUtils.FORMAT_ABBREV_RELATIVE));
            viewHolder.ivPhoto.setAdapter(new ImageAdapter(tweet.getImageUrl()));



        }

        @Override
        public int getItemCount() {
            return itemsCount;
        }

        private void updateItems(){
            itemsCount = tweets.size();
            notifyDataSetChanged();
        }
        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            public ViewHolder(View itemView) {
                super(itemView);
                findViews();
            }

            private CircleImageView ivAvatar;
            private TextView tvNickname;
            private TextView tvContent;
            private GridView ivPhoto;
            private TextView tvPostTime;
            private ImageView ibLike;
            private ImageView ibComment;

            /**
             * Find the Views in the layout<br />
             * <br />
             * Auto-created on 2014-11-20 14:48:19 by Android Layout Finder
             * (http://www.buzzingandroid.com/tools/android-layout-finder)
             */
            private void findViews() {
                ivAvatar = (CircleImageView)itemView.findViewById(R.id.iv_avatar);
                tvNickname = (TextView)itemView.findViewById(R.id.tv_nickname);
                tvContent = (TextView)itemView.findViewById(R.id.tv_content);
                ivPhoto = (GridView)itemView.findViewById(R.id.iv_photo);
                tvPostTime = (TextView)itemView.findViewById(R.id.tv_post_time);
                ibLike = (ImageView)itemView.findViewById(R.id.ib_like);
                ibComment = (ImageView)itemView.findViewById( R.id.ib_comment );

                ibLike.setOnClickListener( this );
                ibComment.setOnClickListener( this );
            }

            /**
             * Handle button click events<br />
             * <br />
             * Auto-created on 2014-11-20 14:48:19 by Android Layout Finder
             * (http://www.buzzingandroid.com/tools/android-layout-finder)
             */
            @Override
            public void onClick(View v) {
                if ( v == ibLike ) {
                    // Handle clicks for ibLike
                } else if ( v == ibComment ) {
                    // Handle clicks for ibComment
                }
            }



        }
    }


}
