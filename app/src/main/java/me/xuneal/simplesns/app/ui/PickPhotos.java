package me.xuneal.simplesns.app.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import me.xuneal.simplesns.app.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PickPhotos extends BaseActivity {

    private GridView mGridView;
    private ListAdapter mListAdapter;
    private List<String> mImageUrls = new ArrayList<>();
    private boolean mIsSupportMultiSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_photos);

        mGridView = (GridView)findViewById(R.id.gv_photos);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);



        Iterator<HashMap<String, String>> iterator = getImages().iterator();
        while (iterator.hasNext()){
            mImageUrls.add("file://" + iterator.next().get("data"));
        }

        mIsSupportMultiSelected = getIntent().getBooleanExtra("is_multi_selected", false);
        if (mIsSupportMultiSelected){
            mListAdapter = new ImageWithCheckboxAdapter(this, mImageUrls);
            mGridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        } else {
          mListAdapter = new ImageWithPickPhotoAdapter(this, mImageUrls);
        }
        mGridView.setAdapter(mListAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position==0){
                    dispatchTakePictureIntent();
                }
                if (!mIsSupportMultiSelected) {
                    mImageUrls.add((String) mListAdapter.getItem(position));
                    Intent intent = getIntent();
                    intent.putExtra("url", (String) mListAdapter.getItem(position));
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pick_photos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sure) {
            Intent intent = new Intent();
            String[] urls = new String[mGridView.getCheckedItemCount()];
            int i=0;
            for (long itemId : mGridView.getCheckedItemIds()){
                if (itemId ==0) continue;
                urls[i] = mImageUrls.get((int)itemId-1);
                i++;
            }
            intent.putExtra("urls", urls);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private List<HashMap<String, String>> getImages() {
        // 指定要查询的uri资源
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        // 获取ContentResolver
        ContentResolver contentResolver = getContentResolver();
        // 查询的字段
        String[] projection = { MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE };
        // 条件
        String selection = MediaStore.Images.Media.MIME_TYPE + "=?";
        // 条件值(這裡的参数不是图片的格式，而是标准，所有不要改动)
        String[] selectionArgs = { "image/jpeg"};
        // 排序
        String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " desc";
        // 查询sd卡上的图片
        Cursor cursor = contentResolver.query(uri, projection, selection,
                selectionArgs, sortOrder);
        List<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
        if (cursor != null) {
            HashMap<String, String> imageMap = null;
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                imageMap = new HashMap<String, String>();
                // 获得图片的id
                imageMap.put("imageID", cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media._ID)));
                // 获得图片显示的名称
                imageMap.put("imageName", cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME)));
                // 获得图片的信息
                imageMap.put(
                        "imageInfo",
                        ""
                                + cursor.getLong(cursor
                                .getColumnIndex(MediaStore.Images.Media.SIZE) / 1024)
                                + "kb");
                // 获得图片所在的路径(可以使用路径构建URI)
                imageMap.put("data", cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA)));
                imageList.add(imageMap);
            }
            // 关闭cursor
            cursor.close();
        }
        return imageList;
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }
}
