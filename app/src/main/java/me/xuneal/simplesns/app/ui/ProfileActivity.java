package me.xuneal.simplesns.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import de.hdodenhof.circleimageview.*;
import me.xuneal.simplesns.app.R;
import me.xuneal.simplesns.app.model.Account;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends BaseActivity {

    private ImageView mCover;
    private CircleImageView mAvatar;
    private TextView mNickname;
    private RecyclerView mTweetList;
    Account mAccount;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2014-11-26 20:53:07 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        mCover = (ImageView)findViewById( R.id.cover );
        mAvatar = (CircleImageView)findViewById( R.id.avatar );
        mNickname = (TextView)findViewById( R.id.nickname );
//        mTweetList = (RecyclerView)findViewById( R.id.tweet_list );
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        findViews();
        mAccount = AVUser.getCurrentUser(Account.class);
        if (mAccount == null){
            startActivity(new Intent(this, LoginActivity.class));
        } else {

            ImageLoader.getInstance().displayImage(mAccount.getAvatar(), mAvatar);
            ImageLoader.getInstance().displayImage("assets://bg_default_cover.png", mCover);
            mNickname.setText(mAccount.getNickName());
        }

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0&& data != null && data.getData() != null){

            Uri _uri = data.getData();

            //User had pick an image.
            Cursor cursor = getContentResolver().query(_uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA }, null, null, null);
            cursor.moveToFirst();

            //Link to the image
            final String imageFilePath = cursor.getString(0);
            cursor.close();

            ImageLoader.getInstance().displayImage("file://" + imageFilePath, mAvatar);
            mAccount.setAvatar(imageFilePath);


        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
