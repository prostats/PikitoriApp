package piki.example.com.loginpikiapp.pikitori.core;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by admin on 2017-01-13.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//      FACEBOOK 로그인 초기화
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // configuration for ImageLoader
        Context context = getApplicationContext();

        File cacheDir = StorageUtils.getCacheDirectory( context );
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder( context )
                .memoryCacheExtraOptions( 480, 800 )// default = device screen dimensions
                .discCacheExtraOptions( 480, 800, Bitmap.CompressFormat.JPEG, 75, null )
                //.taskExecutor(...)
                //.taskExecutorForCachedImages(...)
                .threadPoolSize( 3 )// default
                .threadPriority( Thread.NORM_PRIORITY - 1 )// default
                .tasksProcessingOrder( QueueProcessingType.FIFO )// default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache( new LruMemoryCache(2 * 1024 * 1024) )
                .memoryCacheSize( 2 * 1024 * 1024 )
                .memoryCacheSizePercentage( 13 )// default
                .discCache( new UnlimitedDiscCache( cacheDir ) )// default
                .discCacheSize( 50 * 1024 * 1024 )
                .discCacheFileCount( 100 )
                .discCacheFileNameGenerator( new HashCodeFileNameGenerator() )// default
                .imageDownloader( new BaseImageDownloader( context ) )// default
                //.imageDecoder( new BaseImageDecoder() )// default
                .defaultDisplayImageOptions( DisplayImageOptions.createSimple() )// default
                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init( config );
    }
}
