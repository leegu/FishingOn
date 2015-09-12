package com.go.fish.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.go.fish.util.NetTool.RequestData;
import com.go.fish.util.NetTool.RequestListener;

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

	public static ImageLoader self() {
		if (instance == null) {
			instance = new ImageLoader();
		}
		return instance;
	}

	/**
	 */
	public synchronized Bitmap getBitmapFromLruCache(String key) {
		return mLruCache.get(key);
	}


	public void clearCache() {
        if (mLruCache != null) {
            if (mLruCache.size() > 0) {
                Log.d("CacheUtils",
                        "mLruCache.size() " + mLruCache.size());
                mLruCache.evictAll();
                Log.d("CacheUtils", "mLruCache.size()" + mLruCache.size());
            }
            mLruCache = null;
        }
    }

    public synchronized void addBitmapToLruCache(String key, Bitmap bitmap) {
        if (mLruCache.get(key) == null) {
            if (key != null && bitmap != null)
                mLruCache.put(key, bitmap);
        } else
            Log.w("ImageLoader", "the res is aready exits");
    }

    /**
     * 移除缓存
     * 
     * @param key
     */
    public synchronized void removeImageCache(String key) {
        if (key != null) {
            if (mLruCache != null) {
                Bitmap bm = mLruCache.remove(key);
                if (bm != null)
                    bm.recycle();
            }
        }
    }
	public void loadNetImage(final DownloadTask<ImageView> task) {
//		DownloadBitmapAsyncTask downloadBitmapAsyncTask = new DownloadBitmapAsyncTask();
//		mDownloadBitmapAsyncTaskHashSet.add(downloadBitmapAsyncTask);
//		task.onStart();
//		downloadBitmapAsyncTask.execute(task);
		
		RequestData rData = RequestData.newInstance(new RequestListener() {
			@Override
			public void onStart() {
				task.onStart();
			}
			@Override
			public void onEnd(byte[] data) {
				// TODO Auto-generated method stub
				Bitmap bitmap = toBitmap(data);
				task.onEnd(task.downUrl,bitmap);
			}
		}, task.downUrl,null);
		NetTool.bitmap().http(rData.syncCallback());
	}

	/**
	 */
	private void cancelAllTasks() {
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
	public static abstract class DownloadTask<T> {
		public String downUrl = null;
		public T taskReceiver = null;
		public DownloadTask(String url,T receiver) {
			downUrl = url;
			this.taskReceiver = receiver;
		}
		public abstract  void onEnd(String downUrl,Bitmap bitmap);
		void onStart(){
			
		}
	}
	
	private class DownloadBitmapAsyncTask extends AsyncTask<DownloadTask, Void, Bitmap> {
		private DownloadTask task;

		@Override
		protected Bitmap doInBackground(DownloadTask... params) {
			task = params[0];
			return downloadBitmap(task.downUrl);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			super.onPostExecute(bitmap);
			task.onEnd(task.downUrl, bitmap);
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
