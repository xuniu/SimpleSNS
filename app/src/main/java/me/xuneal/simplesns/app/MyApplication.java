package me.xuneal.simplesns.app;

import android.app.Application;
import android.graphics.Bitmap;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVObject;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import me.xuneal.simplesns.app.model.Account;
import me.xuneal.simplesns.app.model.Comment;
import me.xuneal.simplesns.app.model.Tweet;

/**
 * Created by xyz on 2014/11/19.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //如果使用美国节点，请加上这行代码 AVOSCloud.useAVCloudUS();
        AVObject.registerSubclass(Tweet.class);
        AVObject.registerSubclass(Comment.class);
        AVOSCloud.setDebugLogEnabled(true);
        AVOSCloud.initialize(this, "6wget8cpw4yfez0tp1txqtx6wrjelkpg898m4j7shc3xoe2e", "upfiubxw7w1vzpn8qvn305pml2p7del2k9s5npcihokvb2mq");
        // Create global configuration and initialize ImageLoader with this config
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .considerExifParams(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }



}
