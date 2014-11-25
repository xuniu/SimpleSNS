package me.xuneal.simplesns.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.*;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import android.support.v7.widget.RecyclerView;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.model.Account;
import me.xuneal.simplesns.app.model.Tweet;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends BaseActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVAnalytics.trackAppOpened(getIntent());
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        if (AVUser.getCurrentUser() == null){
            startActivity(new Intent(this, LoginActivity.class));
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

        if (id == R.id.action_post){
            Intent intent = new Intent(this, PostActivity.class);
            startActivityForResult(intent, 0);
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
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    static class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        private List<Tweet> tweets;
        public MyAdapter(List<Tweet> tweets){
            this.tweets = tweets;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_tweet, viewGroup, false);

            ViewHolder viewHolder = new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {

            Tweet tweet = tweets.get(i);
        }

        @Override
        public int getItemCount() {
            return tweets.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


            public ViewHolder(View itemView) {
                super(itemView);
                findViews();
            }

            private ImageView ivAvatar;
            private TextView tvNickname;
            private TextView tvContent;
            private ImageView ivPhoto;
            private TextView tvPostTime;
            private ImageButton ibLike;
            private ImageButton ibComment;

            /**
             * Find the Views in the layout<br />
             * <br />
             * Auto-created on 2014-11-20 14:48:19 by Android Layout Finder
             * (http://www.buzzingandroid.com/tools/android-layout-finder)
             */
            private void findViews() {
                ivAvatar = (ImageView)itemView.findViewById(R.id.iv_avatar);
                tvNickname = (TextView)itemView.findViewById(R.id.tv_nickname);
                tvContent = (TextView)itemView.findViewById(R.id.tv_content);
                ivPhoto = (ImageView)itemView.findViewById(R.id.iv_photo);
                tvPostTime = (TextView)itemView.findViewById(R.id.tv_post_time);
                ibLike = (ImageButton)itemView.findViewById(R.id.ib_like);
                ibComment = (ImageButton)itemView.findViewById( R.id.ib_comment );

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
