package me.xuneal.simplesns.app.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import me.xuneal.simplesns.app.R;

import java.util.ArrayList;

public class PostActivity extends BaseActivity  {

    private EditText text;
    private GridView images;

    private ArrayList<String> mImageUrls = new ArrayList<>();
    ImageAdapter mAdapter;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2014-11-21 09:47:54 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        text = (EditText)findViewById( R.id.text );
        images = (GridView)findViewById( R.id.images );
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2014-11-21 09:47:54 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        findViews();
        mImageUrls.add("assets://ic_add_photo.png");
        mAdapter = new ImageAdapter(mImageUrls);
        images.setAdapter(mAdapter);
        images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position== parent.getCount()-1){
                    Intent intent = new Intent(PostActivity.this, PickPhotos.class);
                    intent.putExtra("is_multi_selected", true);
                    startActivityForResult(intent, 0);
                }
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_post) {
            Intent data = new Intent();
            data.putExtra("content", text.getText().toString());
            mImageUrls.remove(mImageUrls.size() - 1);
            data.putStringArrayListExtra("images", mImageUrls);
            setResult(RESULT_OK, data);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 0&& data != null ){

            String[] urls = data.getStringArrayExtra("urls");
            for (String url : urls){
                mImageUrls.add(mImageUrls.size()-1, url);

            }
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }



}
