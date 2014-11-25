package me.xuneal.simplesns.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import me.xuneal.simplesns.app.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PostActivity extends BaseActivity implements View.OnClickListener {

    private EditText text;
    private GridView images;
    private Button post;

    private ArrayList<String> mImageUrls = new ArrayList<>();
    MyAdapter mAdapter;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2014-11-21 09:47:54 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        text = (EditText)findViewById( R.id.text );
        images = (GridView)findViewById( R.id.images );

        post = (Button)findViewById( R.id.post );

        post.setOnClickListener( this );
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2014-11-21 09:47:54 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if ( v == post ) {
            // Handle clicks for post

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        findViews();
        mImageUrls.add("assets://ic_add.png");
        mAdapter = new MyAdapter(mImageUrls);
        images.setAdapter(mAdapter);
        images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position== parent.getCount()-1){
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 0);
                }
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putExtra("content", text.getText().toString());
                mImageUrls.remove(mImageUrls.size() - 1);
                data.putStringArrayListExtra("images", mImageUrls);
                setResult(RESULT_OK, data);
                finish();
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


            mImageUrls.add(mImageUrls.size()-1, imageFilePath);
            mAdapter.notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);

    }

    class MyAdapter extends BaseAdapter {
        private List<String> mImageUrls;

        public MyAdapter(List<String> imageUrls) {
            mImageUrls = imageUrls;
        }

        @Override
        public int getCount() {
            return mImageUrls.size();
        }

        @Override
        public Object getItem(int position) {
            return mImageUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView!=null && convertView.getTag()!=null){
                viewHolder = (ViewHolder) convertView.getTag();

            } else {
                convertView = new ImageView(parent.getContext());
                viewHolder = new ViewHolder();
                viewHolder.mImageView = (ImageView) convertView;
                viewHolder.mImageView.setLayoutParams(new AbsListView.LayoutParams(100, 100));
            }
            String filename = "";
            if (position != mImageUrls.size()-1){
                filename = "file://" + mImageUrls.get(position);
            } else {
                filename = mImageUrls.get(position);
            }
            ImageLoader.getInstance().displayImage(filename, viewHolder.mImageView);

            return convertView;
        }
    }

    static class ViewHolder {
        ImageView mImageView;
    }
}
