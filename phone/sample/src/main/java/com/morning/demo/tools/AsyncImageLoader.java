package com.morning.demo.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.morning.demo.R;
import com.nostra13.universalimageloader.core.DefaultConfigurationFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public final class AsyncImageLoader {
	private static final int DISK_CACHE_SIZE = 50 * 1024 * 1024;
	private static final int DISK_CACHE_COUNT = 100;
	private static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
	private static final int DEFAULT_HTTP_READ_TIMEOUT = 5 * 1000; // milliseconds
	private static AsyncImageLoader sInstance;
	private DisplayImageOptions mRoundOptions;
	private ImageLoader mLoader;

	public synchronized static AsyncImageLoader getInstance(final Context context) {
		if (sInstance == null) {
			sInstance = new AsyncImageLoader(context.getApplicationContext());
		}
		return sInstance;
	}

	private AsyncImageLoader(final Context context) {
		initImageLoader(context);
	}

	private void initImageLoader(final Context context) {
		final int roundSize = context.getResources().getDimensionPixelOffset(
				R.dimen.round_corner_radius);
		final ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.diskCacheFileNameGenerator(
						DefaultConfigurationFactory.createFileNameGenerator())
				.diskCacheSize(DISK_CACHE_SIZE)
				.diskCacheFileCount(DISK_CACHE_COUNT)
				.imageDownloader(
						new BaseImageDownloader(context,
								DEFAULT_HTTP_CONNECT_TIMEOUT,
								DEFAULT_HTTP_READ_TIMEOUT)).build();
		mLoader = ImageLoader.getInstance();
		mLoader.init(config);

		mRoundOptions = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.search_result_fail)
				.showImageOnFail(R.drawable.search_result_fail)
				.showImageOnLoading(R.drawable.search_defaultr_pic)
				.cacheInMemory(false)
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new RoundedBitmapDisplayer(roundSize)).build();
	}

	public void loadImage(final String url, final ImageView view) {
		mLoader.displayImage(url, view, mRoundOptions);
	}

}
