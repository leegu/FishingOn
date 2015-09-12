package com.go.fish.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

public class LocalMgr {

	private static LocalMgr instance = null;
	public static String sRootPath = null;
	private Context context = null;
	SharedPreferences db = null;
	static{
	}
	private LocalMgr(Context context){
		this.context = context;
//		sRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/." + context.getPackageName() + "/";
		sRootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + context.getPackageName() + "/";
		new File(sRootPath).mkdirs();
		db = context.getSharedPreferences("urls",Context.MODE_PRIVATE);
	}
	private void deleteFile(File file){
		try {
			if(file.isFile()){
				file.delete();
			}else if(file.isDirectory()){
				File list[] = file.listFiles();
				for(File f :list){
					deleteFile(f);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void clearCache(){
		deleteFile(new File(sRootPath));
		new File(sRootPath).mkdirs();
	}

	public void put(String key,String value){
//		if(!db.contains(key)){
			Editor e = db.edit();
			e.putString(key, value);
			e.commit();
//		}
	}
	public String getString(String key){
		String value = null;
		if(db.contains(key)){
			value = db.getString(key, null);
		}
		return value;
	}
	public File getFile(String key){
		File file = null;
		String filePath = getString(key);
		if(TextUtils.isEmpty(filePath)){
			file = new File(filePath);
		}
		return file;
	}
	public Bitmap getBitmap(String key){
		String filePath = getString(key);
		if(!TextUtils.isEmpty(filePath)){
			BitmapFactory.Options options = new BitmapFactory.Options();
//	        options.inJustDecodeBounds = true; // 设置了此属性一定要记得将值设置为false
	        Bitmap bitmap = null;
//	        bitmap = BitmapFactory.decodeFile(filePath, options);
//	        int be = (int) ((options.outHeight > options.outWidth ? options.outHeight / 150
//	                : options.outWidth / 200));
//	        if (be <= 0) // 判断200是否超过原始图片高度
//	            be = 1; // 如果超过，则不进行缩放
//	        options.inSampleSize = be;
//	        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//	        options.inPurgeable = true;
//	        options.inInputShareable = true;
//	        options.inJustDecodeBounds = false;
	        try {
	            bitmap = BitmapFactory.decodeFile(filePath, options);
	        } catch (OutOfMemoryError e) {
	            System.gc();
	            Log.e("LocalMgr", "OutOfMemoryError");
	        }
	        return bitmap;
		}
		return null;
	}
	public static void initEnv(Context context){
		if(instance == null) instance = new LocalMgr(context);
	}

	public static LocalMgr self(){
		return instance;
	}
	public File save(String url,Bitmap bitmap){
		File f = new File(LocalMgr.sRootPath + System.nanoTime());
		try {
			f.createNewFile();
			FileOutputStream fos = new FileOutputStream(f);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.close();
			put(url, f.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}
	
	public File save(String url,byte[] data){
		File f = new File(LocalMgr.sRootPath + System.nanoTime());
		try {
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(data);
			fos.close();
			put(url, f.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

	public File save(String url,InputStream is){
		File f = new File(LocalMgr.sRootPath + System.nanoTime());
		try{
			FileOutputStream fos = new FileOutputStream(f);
			byte[] buf = new byte[10240];
			int len;
			while((len = is.read(buf)) > 0){
				fos.write(buf,0,len);
			}
			fos.flush();
			fos.close();
			put(url, f.getAbsolutePath());
		}catch(Exception e){
			e.printStackTrace();
		}
		return f;
	}
}
