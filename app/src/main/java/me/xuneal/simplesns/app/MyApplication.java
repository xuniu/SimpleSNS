package me.xuneal.simplesns.app;

import android.app.Application;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVAnalytics;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by xyz on 2014/11/19.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //如果使用美国节点，请加上这行代码 AVOSCloud.useAVCloudUS();
        AVOSCloud.initialize(this, "6wget8cpw4yfez0tp1txqtx6wrjelkpg898m4j7shc3xoe2e", "upfiubxw7w1vzpn8qvn305pml2p7del2k9s5npcihokvb2mq");
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);

    }
}
