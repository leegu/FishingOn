package com.go.fish.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.webkit.URLUtil;
import android.widget.ImageView;

public class ImageLoader
{
	/**
	 * 图片缓存的核心类
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private MyThreadPool mThreadPool;
	/**
	 * 线程池的线程数量，默认为1
	 */
	private int mThreadCount = 1;
	/**
	 * 队列的调度方式
	 */
	private Type mType = Type.LIFO;
//	/**
//	 * 任务队列
//	 */
//	private LinkedList<Runnable> mTasks;
//	/**
//	 * 轮询的线程
//	 */
//	private Thread mPoolThread;
//	private Handler mPoolThreadHander;

	/**
	 * 运行在UI线程的handler，用于给ImageView设置图片
	 */
	private Handler mHandler;

	/**
	 * 引入一个值为1的信号量，防止mPoolThreadHander未初始化完成
	 */
//	private volatile Semaphore mSemaphore = new Semaphore(0);

//	/**
//	 * 引入一个值为1的信号量，由于线程池内部也有一个阻塞线程，防止加入任务的速度过快，使LIFO效果不明显
//	 */
//	private volatile Semaphore mPoolSemaphore;

	private static ImageLoader mInstance;

	/**
	 * 队列的调度方式
	 * 
	 * @author zhy
	 * 
	 */
	public enum Type
	{
		FIFO, LIFO
	}


	/**
	 * 单例获得该实例对象
	 * 
	 * @return
	 */
	public static ImageLoader self()
	{

		if (mInstance == null)
		{
			synchronized (ImageLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new ImageLoader(1, Type.LIFO);
				}
			}
		}
		return mInstance;
	}

	private ImageLoader(int threadCount, Type type)
	{
		init(threadCount, type);
	}

	class MyThreadPool {
		private final static int POOL_SIZE = 2;// 线程池的大小最好设置成为CUP核数的2N  
	    private final static int MAX_POOL_SIZE = 3;// 设置线程池的最大线程数  
	    private final static int KEEP_ALIVE_TIME = 2;// 设置线程的存活时间  
	    private final Executor mExecutor;  
	    public MyThreadPool() {  
	        // 创建工作队列  
	        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<Runnable>();  
	        mExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue);  
	    }  
	    // 在线程池中执行线程  
	    public void submit(Runnable command){  
	        mExecutor.execute(command);  
	    }  
	}
	private void init(int threadCount, Type type)
	{
		// loop thread
//		mPoolThread = new Thread()
//		{
//			@Override
//			public void run()
//			{
//				Looper.prepare();
//
//				mPoolThreadHander = new Handler()
//				{
//					@Override
//					public void handleMessage(Message msg)
//					{
//						d("ImageLoader", "handleMessage msg=" + msg);
//						mThreadPool.execute(getTask());
//						try
//						{
//							mPoolSemaphore.acquire();
//						} catch (InterruptedException e)
//						{
//						}
//					}
//				};
//				// 释放一个信号量
//				mSemaphore.release();
//				Looper.loop();
//				 synchronized (mTasks){
//					 for(int i = 0; i < mTasks.size(); i++){
//						 
//					 }
//				 }
//			}
//		};
//		mPoolThread.start();

		// 获取应用程序最大可用内存
		int maxMemory = (int) Runtime.getRuntime().maxMemory();
		int cacheSize = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheSize)
		{
			@Override
			protected int sizeOf(String key, Bitmap value)
			{
				return value.getRowBytes() * value.getHeight();
			};
		};
//		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mThreadPool = new MyThreadPool();
//		mPoolSemaphore = new Semaphore(threadCount);
//		mTasks = new LinkedList<Runnable>();
		mType = type == null ? Type.LIFO : type;
	}

	public static interface ImageLoaderListener{
		public void onStart();
		public void onEnd(String downUrl, Bitmap bitmap);
	}
	public void loadImage(final String path, final ImageView imageView){
		loadNetImage(path, imageView, null,false,false);
	}
	public void loadImage(final String path, final ImageView imageView,final boolean allowNetLoad){
		loadNetImage(path, imageView, null,allowNetLoad,false);
	}
	public static final int LOADING = 0;
	public static final int LOADED = 1;
	/**
	 * 加载图片
	 * @param url
	 * @param imageView
	 */
	public void loadNetImage(final String url, final ImageView imageView,final ImageLoaderListener listener,final boolean allowNetLoad,boolean forBg)
	{
		d("ImageLoader", "loadNetImage url=" + url);
//		// set tag
//		imageView.setTag(url);
		// UI线程
		if (mHandler == null)
		{
			mHandler = new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					switch (msg.what) {
					case LOADED:{
						ImageView imageView = holder.imageView;
						Bitmap bm = holder.bitmap;
						String path = holder.path;
						d("ImageLoader", "handleMessage url=" + path + ";" + holder.forBg);
//						if (imageView.getTag().toString().equals(path))
						{
							d("ImageLoader", "handleMessage will set Bitmap ");
							if(holder.forBg){
								imageView.setBackgroundDrawable(new BitmapDrawable(bm));
							}else{
								imageView.setImageBitmap(bm);
							}
						}
						if(holder.listener != null){
							holder.listener.onEnd(path, bm);
						}
						break;
					}
					case LOADING:
						if(holder.listener != null){
							holder.listener.onStart();
						}
						break;

					default:
						break;
					}
				}
			};
		}

		Bitmap bm = getBitmapFromLruCache(url);
		ImgBeanHolder holder = new ImgBeanHolder();
		holder.bitmap = bm;
		holder.imageView = imageView;
		holder.path = url;
		holder.forBg = forBg;
		holder.listener = listener;
		holder.allowNetLoad = allowNetLoad;
		
		if (bm != null)
		{
//			Message message = Message.obtain();
			Message message = new Message();
			message.obj = holder;
			message.what = LOADED;
			mHandler.sendMessage(message);
		} else {
			Message message = new Message();
			message.obj = holder;
			message.what = LOADING;
			mHandler.sendMessage(message);
			
			d("ImageLoader", "loadNetImage 0 addTask url=" + url);
			LoadTask task = new LoadTask();
			task.mImgBeanHolder = holder;
			addTask(task);
			d("ImageLoader", "loadNetImage 1 addTask url=" + url);
		}

	}
	public static void d(String tag,String msg){
		LogUtils.d(tag, msg);
	}
	class LoadTask implements Runnable{
		ImgBeanHolder mImgBeanHolder = null;
		@Override
		public void run()
		{
			d("ImageLoader", "NetTask 0 run =" + mImgBeanHolder.path);
			ImageSize imageSize = getImageViewWidth(mImgBeanHolder.imageView);
			int reqWidth = imageSize.width;
			int reqHeight = imageSize.height;
			
			String path = LocalMgr.self().getString(mImgBeanHolder.path);
			Bitmap bm = null;
			if(!TextUtils.isEmpty(path)){
				bm = decodeSampledBitmapFromResource(path, reqWidth, reqHeight);
			}
			d("ImageLoader", "NetTask 1 url=" + mImgBeanHolder.path + ";filePath=" + path);
			if(bm == null && mImgBeanHolder.allowNetLoad && URLUtil.isNetworkUrl(mImgBeanHolder.path) ){
				d("ImageLoader", "NetTask 2 url=" + mImgBeanHolder.path);
				String filePath = downloadBitmap(mImgBeanHolder.path);
				bm = decodeSampledBitmapFromResource(filePath, reqWidth, reqHeight);
				d("ImageLoader", "NetTask 3 url=" + mImgBeanHolder.path);
			}
			if(bm != null){
				mImgBeanHolder.bitmap = bm;
				d("ImageLoader", "NetTask 4 url=" + mImgBeanHolder.path);
				end(bm);
			}
		}
		
		private String downloadBitmap(String imageUrl) {
			String bitmapFilePath = null;
			HttpURLConnection conn = null;
			try {
				conn = createConnection(imageUrl);
				int redirectCount = 0;
				while ((conn.getResponseCode() / 100 == 3) && (redirectCount < 5)) {
					conn = createConnection(conn.getHeaderField("Location"));
					redirectCount++;
				}
				;
				bitmapFilePath = LocalMgr.self().save(mImgBeanHolder.path, conn.getInputStream()).getAbsolutePath();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
			return bitmapFilePath;
		}

		static final int connectTimeout = 5 * 1000;

		protected HttpURLConnection createConnection(String url) throws IOException {
			String encodedUrl = Uri.encode(url, "@#&=*+-_.,:!?()/~'%");
			HttpURLConnection conn = (HttpURLConnection) new URL(encodedUrl)
					.openConnection();
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(connectTimeout);
			// conn.setDoInput(true);
			// conn.setDoOutput(true);
			return conn;
		}
		private void end(Bitmap bm){
			addBitmapToLruCache(mImgBeanHolder.path, bm);
			Message message = new Message();
			message.obj = mImgBeanHolder;
			message.what = LOADED;
			// Log.e("TAG", "mHandler.sendMessage(message);");
			mHandler.sendMessage(message);
//			mPoolSemaphore.release();
		}
		
	}
	/**
	 * 添加一个任务
	 * 
	 * @param runnable
	 */
	private synchronized void addTask(Runnable runnable)
	{
		
		mThreadPool.submit(runnable);
//		try
//		{
//			// 请求信号量，防止mPoolThreadHander为null
//			if (mPoolThreadHander == null)
//				mSemaphore.acquire();
//		} catch (InterruptedException e)
//		{
//		}
//		mTasks.add(runnable);
//		d("ImageLoader", "addTask to mTasks");
//		mPoolThreadHander.sendEmptyMessage(0x110);
	}

//	/**
//	 * 取出一个任务
//	 * 
//	 * @return
//	 */
//	private synchronized Runnable getTask()
//	{
//		if (mType == Type.FIFO)
//		{
//			return mTasks.removeFirst();
//		} else if (mType == Type.LIFO)
//		{
//			return mTasks.removeLast();
//		}
//		return null;
//	}
	
	/**
	 * 单例获得该实例对象
	 * 
	 * @return
	 */
	public static ImageLoader getInstance(int threadCount, Type type)
	{

		if (mInstance == null)
		{
			synchronized (ImageLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new ImageLoader(threadCount, type);
				}
			}
		}
		return mInstance;
	}


	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	private ImageSize getImageViewWidth(ImageView imageView)
	{
		ImageSize imageSize = new ImageSize();
		final DisplayMetrics displayMetrics = imageView.getContext()
				.getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();
		int width = 0;
		int height = 0;
		if(params != null ){
			width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView.getWidth(); // Get actual image width
			if (width <= 0)
				width = params.width; // Get layout width parameter
			
			height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
					.getHeight(); // Get actual image height
			if (height <= 0)
				height = params.height; // Get layout height parameter
		}
		if (width <= 0)
			width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
																	// maxWidth
																	// parameter
		height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
		// maxHeight
		// parameter
		if(width == LayoutParams.MATCH_PARENT){
			
		}
		if (width <= 0)
			width = displayMetrics.widthPixels;
		
		
		if (height <= 0)
		if (height <= 0)
			height = displayMetrics.heightPixels;
		
		
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;

	}

	/**
	 * 从LruCache中获取一张图片，如果不存在就返回null。
	 */
	public Bitmap getBitmapFromLruCache(String key)
	{
		return mLruCache.get(key);
	}

	/**
	 * 往LruCache中添加一张图片
	 * 
	 * @param key
	 * @param bitmap
	 */
	public void addBitmapToLruCache(String key, Bitmap bitmap)
	{
		if (getBitmapFromLruCache(key) == null)
		{
			if (bitmap != null)
				mLruCache.put(key, bitmap);
		}
	}

	/**
	 * 计算inSampleSize，用于压缩图片
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight)
	{
		// 源图片的宽度
		int width = options.outWidth;
		int height = options.outHeight;
		int inSampleSize = 1;

		if (width > reqWidth && height > reqHeight)
		{
			// 计算出实际宽度和目标宽度的比率
			int widthRatio = Math.round((float) width / (float) reqWidth);
			int heightRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = Math.max(widthRatio, heightRatio);
		}
		return inSampleSize;
	}

	/**
	 * 根据计算的inSampleSize，得到压缩后图片
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private Bitmap decodeSampledBitmapFromResource(String pathName,
			int reqWidth, int reqHeight)
	{
		// 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathName, options);
		// 调用上面定义的方法计算inSampleSize值
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);
		// 使用获取到的inSampleSize值再次解析图片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(pathName, options);

		return bitmap;
	}

	private class ImgBeanHolder
	{
		Bitmap bitmap;
		ImageView imageView;
		String path;
		int w,h;
		boolean forBg = false;
		boolean allowNetLoad = false;
		ImageLoaderListener listener = null;
	}

	private class ImageSize
	{
		int width;
		int height;
	}

	/**
	 * 反射获得ImageView设置的最大宽度和高度
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName)
	{
		int value = 0;
		try
		{
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE)
			{
				value = fieldValue;

				Log.e("TAG", value + "");
			}
		} catch (Exception e)
		{
		}
		return value;
	}

}
