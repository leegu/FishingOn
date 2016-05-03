package com.go.fish.util;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.go.fish.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ImageLoaderUtil {

	/**
	 * 初始化ImageLoader 默认图片地址为icons
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		if (ImageLoader.getInstance().isInited()) {
			return;
		}
		File cacheDir = new File(getIconLoaclfolder());
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				context.getApplicationContext())
		        .threadPoolSize(3)
		        //降低线程的优先级保证主UI线程不受太大影响
		        .threadPriority(Thread.NORM_PRIORITY - 2)
				.discCacheFileCount(100)
				.denyCacheImageMultipleSizesInMemory()
				.defaultDisplayImageOptions(getIconDisplayOptions())
				.discCache(new UnlimitedDiscCache(cacheDir))
				.memoryCache(new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		ImageLoader.getInstance().init(config);
	}

	public static String getIconLoaclfolder() {
		return LocalMgr.sRootPath;
	}
	
	public static void updateIcon(String uri) {
		if (TextUtils.isEmpty(uri)) {
			return;
		}
		File file = ImageLoader.getInstance().getDiscCache().get(uri);
		if (file.exists()) {
			file.delete();
		}
		ImageLoader.getInstance().loadImage(uri, null);
	}
	
	public static DisplayImageOptions getIconDisplayOptions() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.cacheOnDisc(true)
		.cacheInMemory(true)
		// 设置下载的图片是否缓存在内存中
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)
		.bitmapConfig(Bitmap.Config.RGB_565)
		// 载入图片前稍做延时可以提高整体滑动的流畅度
		//.delayBeforeLoading(100)
		.showImageOnLoading(R.drawable.pic) // 设置图片下载期间显示的图片
		.build();
		return defaultOptions;
	}
}
