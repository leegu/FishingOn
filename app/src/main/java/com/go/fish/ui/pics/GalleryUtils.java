package com.go.fish.ui.pics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.go.fish.ui.UICode;
import com.go.fish.ui.pic.ClipPicUI;
import com.go.fish.util.LocalMgr;

@SuppressLint("NewApi")
public class GalleryUtils {

    private static GalleryUtils instance = null;

    public static GalleryUtils self() {
        if (instance == null) instance = new GalleryUtils();
        return instance;
    }


    public void takePic(final Activity activity, final GalleryCallback callback){
        String IMAGE_FILE_LOCATION = "file://" + LocalMgr.sRootPath + System.currentTimeMillis() + ".jpg";//temp file
        final Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//action is capture
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        registerSysEventListener(new ISysEventListener() {
            @Override
            public boolean onReceiveResult(int requestCode, int resultCode, Intent resultData) {
                if (requestCode == UICode.RequestCode.REQUEST_CROP_IMAGE && resultCode != 0) {
                    onHandleFilePathArray(activity, callback, new String[]{imageUri.getPath()});
                }
                return false;
            }
        });
        activity.startActivityForResult(intent, UICode.RequestCode.REQUEST_PICK_CAPTURE);
    }
    public void crop(final Activity activity, final GalleryCallback callback,int w,int h) {
//        String IMAGE_FILE_LOCATION = "file://" + LocalMgr.sRootPath + "temp.jpg";//temp file
//        final Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap
//        File file = new File(imageUri.getPath());
//        if(file.exists()){
//        	file.delete();
//        }
//        Intent intent = new Intent("com.android.camera.action.CROP", null);
//        intent.setType("image/*");
//        intent.putExtra("crop", "true");
////        intent.putExtra("aspectX", 2);
////        intent.putExtra("aspectY", 1);
//        intent.putExtra("outputX", w);
//        intent.putExtra("outputY", h);
//        intent.putExtra("scale", true);
//        intent.putExtra("return-data", false);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//        intent.putExtra("noFaceDetection", true); // no face detection
//        registerSysEventListener(new ISysEventListener() {
//            @Override
//            public boolean onReceiveResult(int requestCode, int resultCode, Intent resultData) {
//                if(requestCode == UICode.RequestCode.REQUEST_CROP_IMAGE && resultCode != 0){
//                    onHandleFilePathArray(activity, callback, new String[]{imageUri.getPath()});
//                }
//                return false;
//            }
//        });
//        activity.startActivityForResult(intent, UICode.RequestCode.REQUEST_CROP_IMAGE);
        pick(activity, new GalleryCallback() {
			@Override
			public void onCompleted(String[] filePath, Bitmap bitmap0) {
					ResultReceiver resultRcrv = new ResultReceiver(new Handler(Looper.getMainLooper())) {
	                    @Override
	                    protected void onReceiveResult(int resultCode, Bundle resultData) {
	                    	super.onReceiveResult(resultCode, resultData);
	//                    	byte[] bis = resultData.getByteArray("bitmap");
	//                    	Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);
	                    	Bitmap bitmap = (Bitmap)resultData.get("bitmap");
	                    	String filePath = LocalMgr.sRootPath + "temp.jpg";
	                    	try {
								bitmap.compress(Bitmap.CompressFormat.JPEG, 100, new FileOutputStream(new File(filePath)));
							} catch (Exception e) {
								e.printStackTrace();
							}
	                    	callback.onCompleted(new String[]{filePath}, bitmap);
	                    }
	                };
	                Intent intent = new Intent();
	                intent.setClass(activity, ClipPicUI.class);
	                intent.putExtra("bitmap",filePath[0]);
	                intent.putExtra("onResult",resultRcrv);
	                activity.startActivity(intent);
			}
		}, null, false);
    }

    public void pick(Activity activity, GalleryCallback callback, String pickType, boolean multiple) {
        if (multiple) {
            selectMultipleImages(activity, callback, pickType);
        } else {
            selectSingleImage(activity, callback, pickType);
        }
    }


    String getFilePathFromUri(Context context, Uri uri) {
        String filePath = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            String id = wholeID.split("\\:")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column,
                    sel, new String[]{id}, null);
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(column_index);
        }
        return filePath;
    }

    ISysEventListener allReceivers = null;

    private void registerSysEventListener(ISysEventListener resultListener) {
        allReceivers = resultListener;
    }

    private void unregisterSysEventListener(ISysEventListener resultListener) {
        allReceivers = null;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return allReceivers != null ? allReceivers.onReceiveResult(requestCode,resultCode,data) : false;
    }

    private void selectMultipleImages(final Activity activity, final GalleryCallback callback, String pickType) {
        try {
            registerSysEventListener(new ISysEventListener() {
                @Override
                public boolean onReceiveResult(int requestCode, int resultCode, Intent resultData) {
                    Intent data = resultData;
                    unregisterSysEventListener(this);
                    String[] filePaths = null;
                    try {
                        if (requestCode == UICode.RequestCode.REQUEST_PICK_IMAGE_MULTIPLE) {
                            filePaths = data.getStringArrayExtra("all_path");
                        } else if (requestCode == UICode.RequestCode.REQUEST_PICK_IMAGE_SYSTEM_MULTIPLE) {
                            if (data != null) {
                                //从意图中获取数据
                                ClipData clipData = data.getClipData();
                                if (clipData != null) {
                                    int count = clipData.getItemCount();
                                    filePaths = new String[count];
                                    for (int i = 0; i < count; i++) {
                                        Item item = clipData.getItemAt(i);
                                        Uri uri = item.getUri();
                                        String f = getFilePathFromUri(activity, uri);
                                        filePaths[i] = f;
                                    }
                                } else if (data.getData() != null) {
                                    String singleFilePath = null;
                                    if (isDeviceRootDir(data.getData().getPath())) {
                                        singleFilePath = data.getData().getPath();
                                    } else {
                                        singleFilePath = getFilePathFromUri(activity, data.getData());//getSingleFilePath(pWebViewImpl.getActivity(),data.getData());
                                    }
                                    if (singleFilePath != null) {
                                        filePaths = new String[1];
                                        filePaths[0] = singleFilePath;
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        //e.printStackTrace();
                    }
                    onHandleFilePathArray(activity, callback, data != null ? filePaths : null);
                    return false;
                }
            });
            Intent intent = null;
            int requestCode = UICode.RequestCode.REQUEST_PICK_IMAGE_MULTIPLE;
            intent = new Intent();
            String type = "image/*";//默认pick图片
            if (!TextUtils.isEmpty(pickType)) {
                type = pickType;//type = "image/*|video/*";
            }
            intent.setType(type);
            if (Build.VERSION.SDK_INT >= 19) {
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                requestCode = UICode.RequestCode.REQUEST_PICK_IMAGE_SYSTEM_MULTIPLE;
                try {
                    PackageManager pManager = activity.getPackageManager();
                    List<ResolveInfo> mApps = pManager.queryIntentActivities(intent,
                            PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
                    boolean useSys = false;
                    for (ResolveInfo info : mApps) {
                        if ("com.android.documentsui".equals(info.activityInfo.packageName)) {//android原生系统所带
                            useSys = true;
                            break;
                        }
                    }
                    if (!useSys) {
                        intent = new Intent();
                        intent.setType(type);
                        requestCode = UICode.RequestCode.REQUEST_PICK_IMAGE_MULTIPLE;
                        intent.setClassName(activity.getPackageName(), "io.dcloud.imagepick.CustomGalleryActivity");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                intent.setClassName(activity.getPackageName(), "io.dcloud.imagepick.CustomGalleryActivity");
            }
            activity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void onHandleFilePathArray(final Activity activity, final GalleryCallback callback, String[] paths) {
        if (callback != null) {
            callback.onCompleted(paths, null);
        }
    }

    private void selectSingleImage(final Activity activity, final GalleryCallback callback, String pickType) {
        try {
            registerSysEventListener(new ISysEventListener() {
                @Override
                public boolean onReceiveResult(int requestCode, int resultCode, Intent resultData) {
                    Intent data = resultData;
                    unregisterSysEventListener(this);
                    if (requestCode == UICode.RequestCode.REQUEST_PICK_IMAGE) {
                        boolean _suc = false;
                        String picturePath = null;
                        String msg = "data is null";
                        if (data != null) {
                            msg = "pickImage path wrong";
                            //从意图中获取数据
                            Uri selectedImage = data.getData();
                            if (selectedImage != null) {//华为EMUI系统，取消图片选择时候data有值，但selectedImage为null
                                if (isDeviceRootDir(selectedImage.getPath())) {
                                    picturePath = selectedImage.getPath();
                                } else {
                                    picturePath = getSingleFilePath(activity, selectedImage);
                                }
                            }
                        }
                        //当picturePath为null则认为选取失败
                        onHandleFilePathArray(activity, callback, picturePath != null ? new String[]{picturePath} : null);
                    }
                    return false;
                }
            });
            Intent intent = new Intent(Intent.ACTION_PICK);
            String type = "image/*";//默认pick图片
            if (!TextUtils.isEmpty(pickType)) {
                type = pickType;//type = "image/*|video/*";
            }
            intent.setType(type);
            activity.startActivityForResult(intent, UICode.RequestCode.REQUEST_PICK_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSingleFilePath(Context context, Uri uri) {
        String picturePath = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        //获取URI中的游标
        Cursor cursor = context.getContentResolver().query(uri,
                filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
//			String document_id = null;
//			// 获取读出的所有列数据
//			int colCount = cursor.getColumnCount();
//			for(int i = 0; i < colCount ;i++){
//				String str = cursor.getColumnName(i);
//				if(MediaStore.Images.Media.DATA.equals(str)){
//					picturePath = cursor.getString(i);
//				}
//				if("document_id".equals(str)){
//					document_id = cursor.getString(i);
//				}
//				System.out.println("pick str" +i+"   " + str + "=" + cursor.getString(i));
//			}
//			if(picturePath == null){
//				Cursor cursor1 = pWebViewImpl.getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//						filePathColumn, "document_id = '" + document_id + "'", null, null);
//				cursor1.moveToFirst();
//				int columnIndex = cursor1.getColumnIndex(filePathColumn[0]);
//				picturePath = cursor1.getString(columnIndex);
//				cursor1.close();
//			}
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
//			if(!PdrUtil.isEmpty(picturePath) ){// 统一规范 返回路径以file://开头
//				picturePath = _app.convert2WebviewFullPath(null, picturePath);
//			}
//			try {//测试获取的路径是否可用，如果可用不建议修改
//				boolean ret = DHFile.isExist(picturePath);
//				System.out.println(ret ? "获取的路径可用" : "获取路径不可用");
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
            //TO 成功
            cursor.close();
        }
        return picturePath;
    }

    public static boolean isDeviceRootDir(String mUrl) {
        return mUrl.startsWith("/sdcard/") || mUrl.startsWith("sdcard/");
    }

    public interface GalleryCallback {
        void onCompleted(String[] filePath, Bitmap bitmap0);
        
    }

    interface ISysEventListener {
        boolean onReceiveResult(int requestCode, int resultCode, Intent resultData);
    }
}
