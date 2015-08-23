package com.go.fish.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

public class ImageLoader {
	static ImageLoader instance = null;
	// 图片缓存类
	private LruCache<String, Bitmap> mLruCache;
	// 记录所有正在下载或等待下载的任务
	private HashSet<DownloadBitmapAsyncTask> mDownloadBitmapAsyncTaskHashSet;

	private ImageLoader() {
		mDownloadBitmapAsyncTaskHashSet = new HashSet<DownloadBitmapAsyncTask>();

		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		// 设置图片缓存大小为maxMemory的1/6
		int cacheSize = maxMemory / 6;
		mLruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return bitmap.getRowBytes() * bitmap.getHeight();
//				return bitmap.getByteCount();
			}
		};
	}

	public static ImageLoader initEnv() {
		if (instance == null) {
			instance = new ImageLoader();
		}
		return instance;
	}

	/**
	 */
	public Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}

	/**
	 */
	public void addBitmapToLruCache(String key, Bitmap bitmap) {
		mLruCache.put(key, bitmap);
	}

	public void executeBitmapLoad(DownloadTask<ImageView> task) {
		Bitmap bitmap = getBitmapFromLruCache(task.downUrl);
		if(bitmap != null && !bitmap.isRecycled()){
			task.onEnd(task.downUrl, bitmap);
			return;
		}
//		else if((bitmap == ))
		DownloadBitmapAsyncTask downloadBitmapAsyncTask = new DownloadBitmapAsyncTask();
		mDownloadBitmapAsyncTaskHashSet.add(downloadBitmapAsyncTask);
		task.onStart();
		downloadBitmapAsyncTask.execute(task);
	}

	/**
	 */
	public void cancelAllTasks() {
		if (mDownloadBitmapAsyncTaskHashSet != null) {
			for (DownloadBitmapAsyncTask task : mDownloadBitmapAsyncTaskHashSet) {
				task.cancel(false);
			}
		}
	}
	/**
	 * 
	 * @author DCloud
	 *
	 * @param <T> 下载完成后数据的接收者
	 */
	public static class DownloadTask<T> {
		String downUrl = null;
		T receiver = null;
		public DownloadTask(String url,T receiver) {
			downUrl = url;
			this.receiver = receiver;
		}
		void onEnd(String downUrl,Bitmap bitmap){
			if(receiver instanceof ImageView){
				((ImageView)receiver).setBackgroundDrawable(new BitmapDrawable(bitmap));
//				((ImageView)receiver).setImageBitmap(bitmap);
			}
		}
		void onStart(){
			
		}
	}
	
	private class DownloadBitmapAsyncTask extends AsyncTask<DownloadTask, Void, Bitmap> {
		private DownloadTask task;

		@Override
		protected Bitmap doInBackground(DownloadTask... params) {
			task = params[0];
			Bitmap bitmap = downloadBitmap(task.downUrl);
			if (bitmap != null) {
				addBitmapToLruCache(task.downUrl, bitmap);
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			task.onEnd(task.downUrl, bitmap);
			addBitmapToLruCache(task.downUrl, bitmap);
			mDownloadBitmapAsyncTaskHashSet.remove(this);
		}
	}

	private Bitmap downloadBitmap(String imageUrl) {
		Bitmap bitmap = null;
		HttpURLConnection conn = null;
		try {
			conn = createConnection(imageUrl);
			int redirectCount = 0;
			while ((conn.getResponseCode() / 100 == 3) && (redirectCount < 5)) {
				conn = createConnection(conn.getHeaderField("Location"));
				redirectCount++;
			}

			bitmap = BitmapFactory.decodeStream(conn.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
		return bitmap;
	}

	int connectTimeout = 5 * 1000;

	protected HttpURLConnection createConnection(String url) throws IOException {
		String encodedUrl = Uri.encode(url, "@#&=*+-_.,:!?()/~'%");
		HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl)
				.openConnection();
		conn.setConnectTimeout(this.connectTimeout);
		conn.setReadTimeout(this.connectTimeout);
		// conn.setDoInput(true);
		// conn.setDoOutput(true);
		return conn;
	}
}
