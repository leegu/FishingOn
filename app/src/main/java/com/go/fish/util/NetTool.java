package com.go.fish.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.baidu.platform.comapi.map.l;
import com.go.fish.BuildConfig;
import com.go.fish.MainApplication;
import com.go.fish.R;
import com.go.fish.ui.BaseUI;
import com.go.fish.ui.HomeUI;
import com.go.fish.ui.RegisterUI;
import com.go.fish.user.User;
import com.go.fish.util.NetTool.RequestData.HttpOption;
import com.go.fish.view.ViewHelper;

public class NetTool {

	
	private static String TAG = "NetTool";
	static NetTool subimtiDataNetTool = null;
	static NetTool dataNetTool = null;
	static NetTool bitmapNetTool = null;
	private RequestQueue Handler = null;
	
	private NetTool() {
	};
	private static String UserAgent;
	static{
		String userAgent = " fish/0";
        try {
            String packageName = MainApplication.getInstance().getPackageName();
            PackageInfo info = MainApplication.getInstance().getPackageManager().getPackageInfo(packageName, 0);
            UserAgent = packageName + "/" + info.versionCode + userAgent;
        } catch (NameNotFoundException e) {
        }
	}

	public static NetTool data() {
		if (dataNetTool == null) {
			dataNetTool = new NetTool();
			dataNetTool.Handler = Volley.newRequestQueue(MainApplication.getInstance()/*,new MultiPartStack(getNewHttpClient())*/);
//			dataNetTool.Handler.start();
		}
		return dataNetTool;
	}
	public static NetTool submit() {
		if (subimtiDataNetTool == null) {
			subimtiDataNetTool = new NetTool();
			subimtiDataNetTool.Handler = Volley.newRequestQueue(MainApplication.getInstance(),new MultiPartStack(getNewHttpClient()));
			subimtiDataNetTool.Handler.start();
		}
		return subimtiDataNetTool;
	}

	static NetTool bitmap() {
		if (bitmapNetTool == null) {
			bitmapNetTool = new NetTool();
			bitmapNetTool.Handler = Volley.newRequestQueue(MainApplication.getInstance(),new MultiPartStack(getNewHttpClient()));
			bitmapNetTool.Handler.start();
		}
		return bitmapNetTool;
	}
	
	public void unhttp(String tag){
		Handler.cancelAll(tag);
	}

	public void http(RequestListener listener,JSONObject jsonObject,String url){
		RequestData rData = RequestData.newInstance(listener,jsonObject,url);
		http(rData.syncCallback());
	}
	
	private String toQueryString(JSONObject json){
		StringBuffer sb = new StringBuffer();
		JSONArray arr = json.names();
		for(int i = 0; i < arr.length(); i++){
			String k = arr.optString(i);
			sb.append(k).append("=").append(json.optString(k));
			if(i != arr.length() - 1){
				sb.append("&");
			}
		}
		return sb.toString();
	}
	/***
	 * 默认使用GET请求方式
	 * 
	 * @param rData
	 */
	public Request http(final RequestData rData) {
		if (!rData.isValid()) {
			LogUtils.d("http", "http, url is null");
			return null;
		}
		RequestListener listener = rData.mListener;
		rData.addHeader(Const.STA_USER_AGENT, UrlUtils.self().getUserAgent());
		LogUtils.d("http", UrlUtils.self().getToken());
		if(!TextUtils.isEmpty(UrlUtils.self().getToken())){
			rData.addHeader(Const.STA_CC_TOKEN, UrlUtils.self().getToken());
		}
		if(listener != null){
			listener.url = rData.url;
			if (rData.synCallBack) {
				MessageHandler.sendMessage(new MessageHandler.MessageListener<byte[]>() {
							@Override
							public MessageHandler.MessageListener init(
									byte[] data) {
								return this;
							}
	
							@Override
							public void onExecute() {
								rData.mListener.onStart();
							}
						});
			} else {
				listener.onStart();
			}
		}
		if(BuildConfig.DEBUG){
//			rData.url = rData.url + "?" + toQueryString(rData.mJsonData);
//			rData.mJsonData = null;
			Log.e("http", "onRequest request:(" + rData.url + " "+ rData.mJsonData + ")");
		}
		int M = rData.option == HttpOption.GET ? Method.GET : Method.POST; 
		BinaryRequest br = new BinaryRequest(M,rData, new Response.Listener<byte[]>() {
			@Override
			public void onResponse(byte[] response) {
				rData.mListener.onEnd(response);
			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// TODO Auto-generated method stub
				if(rData != null && error != null){
					try {
						if(BuildConfig.DEBUG){
							LogUtils.d("http", "onErrorResponse request:(" + rData.url + " "+ rData.mJsonData + ")" );
							if(error.networkResponse != null){
								LogUtils.d("http", "onErrorResponse response:(" + error.networkResponse.statusCode +" " + error.networkResponse.headers + ")");
							}
						}
						rData.mListener.onError(-1, error.getMessage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		
//		if(!rData.mHeads.containsKey("Accept")){
//			rData.mHeads.put("Accept", "application/json");
//		}
//		if(!rData.mHeads.containsKey("Content-type")){
//			rData.mHeads.put("Content-type", "application/json;charset=UTF-8");
//		}
		br.mRequest  = rData;
		Handler.add(br);
		return br;
	}

	

	static class MultiPartStack extends HttpClientStack{
		
		public MultiPartStack(HttpClient client) {
			super(client);
		}
		@Override
		protected void setEntityIfNonEmptyBody(HttpEntityEnclosingRequestBase httpRequest,
	            Request<?> request) throws AuthFailureError {
			if(request instanceof BinaryRequest){
				RequestData rd = ((BinaryRequest)request).mRequest;
				if(request.getMethod() == Method.POST){
					onPost(rd,(HttpPost)httpRequest);
				}
			}
		}

		/**
		 * 将请求参数写入输入流集合
		 */
		private static long appendPostParemeter(Vector<InputStream> arr, String content,
				long _length) {
			ByteArrayInputStream ret = null;
			try {
				ret = new ByteArrayInputStream(content.getBytes("utf-8"));
				arr.add(ret);
				return ret.available() + _length;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			return 0;
		}

		private static long addPropertyInputStream(Vector<InputStream> pArr, String pKey,
				String pValue, long mContentLength) {
			mContentLength = appendPostParemeter(
					pArr,
					"Content-Disposition: form-data; name=\"" + pKey + "\"\r\n\r\n",
					mContentLength);
			mContentLength = appendPostParemeter(pArr, pValue + "\r\n",
					mContentLength);
			return mContentLength;
		}

		private static long addFileInputStream(Vector<InputStream> pArr, String pKey,
				File pFile, long mContentLength) {
			try {
				mContentLength = appendPostParemeter(pArr,
						"Content-Disposition: form-data; name=\"" + pKey
								+ "\"; filename=\"" + pKey + "\"\r\n",
						mContentLength);
				mContentLength = appendPostParemeter(pArr, "Content-Type: "
						+ getMimeType(pFile.getName()) + "\r\n\r\n", mContentLength);
				pArr.add(new FileInputStream(pFile));
				mContentLength = appendPostParemeter(pArr, "\r\n", mContentLength);
				mContentLength = mContentLength + pFile.length();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return mContentLength;
		}

		private static long addCutoffLine(Vector<InputStream> pArr, String mainBoundry,
				long contentLength) {
			return appendPostParemeter(pArr, "--" + mainBoundry + "\r\n",
					contentLength);
		}

		public static String getMimeType(String filename) {
			MimeTypeMap map = android.webkit.MimeTypeMap.getSingleton();
			String extType = MimeTypeMap.getFileExtensionFromUrl(filename);
			if (TextUtils.isEmpty(extType) && filename.lastIndexOf(".") >= 0) {
				extType = filename.substring(filename.lastIndexOf(".") + 1);
			}
			return map.getMimeTypeFromExtension(extType);
		}

		/**
		 * 产生6位的boundary
		 */
		public static String getBoundry() {
			StringBuffer _sb = new StringBuffer("------");
			for (int t = 1; t < 7; t++) {
				long time = System.currentTimeMillis() + t;
				if (time % 3 == 0) {
					_sb.append((char) time % 9);
				} else if (time % 3 == 1) {
					_sb.append((char) (65 + time % 26));
				} else {
					_sb.append((char) (97 + time % 26));
				}
			}
			return _sb.toString();
		}

		private static HttpUriRequest onPost(RequestData rData,HttpPost httpPost) {
			LogUtils.d("http", "post url=" + rData.url);
			if (rData.mJsonData != null && rData.mJsonData.length() > 0) {//json请求格式
				try {
					httpPost.setEntity(new StringEntity(rData.mJsonData.toString()));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} else if(rData.mCommitFilePath != null){//表单提交图片
				try {
					File file = new File(rData.mCommitFilePath);
					InputStreamEntity ise = new InputStreamEntity(new FileInputStream(file), file.length());
					httpPost.setEntity(ise);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} else {//表单提交(文字数据、图片)
				String mMainBoundry = rData.mMainBoundry;
//				if(httpPost.containsHeader("Content-Type")){
//					httpPost.removeHeaders("Content-Type");
//				}
//				rData.mContentType = "multipart/form-data;boundary=" + mMainBoundry;
//				httpPost.addHeader("Content-Type", rData.mContentType);
				// 存储上传数据的输入流集合
				Vector<InputStream> _arr = new Vector<InputStream>();
				long mContentLength = 0;
				// 上传File数据
				if (rData.mFileDataList != null && rData.mFileDataList.size() > 0) {
					for (File file : rData.mFileDataList) {
						if (file.exists()) {
							mContentLength = addCutoffLine(_arr, mMainBoundry, mContentLength);
							mContentLength = addFileInputStream(_arr, file.getName(), file, mContentLength);
						}
					}
				}
				// 上传StringData数据
				if(rData.mStringDataArr != null && rData.mStringDataArr.size() > 0){
					Iterator<String> _iterator = rData.mStringDataArr.keySet().iterator();
					String _name, _value;
					while (_iterator.hasNext()) {
						_name = _iterator.next();
						_value = rData.mStringDataArr.get(_name);
						mContentLength = addCutoffLine(_arr, mMainBoundry, mContentLength);
						mContentLength = addPropertyInputStream(_arr, _name, _value, mContentLength);
					}
				}
				// 上传结尾
				mContentLength = appendPostParemeter(_arr, "--" + mMainBoundry + "--\r\n", mContentLength);

				SequenceInputStream sis = new SequenceInputStream(_arr.elements());
				
//				httpPost.addHeader("Content-Type", "multipart/form-data;boundary=" + mMainBoundry);
				// mHttpPost.addHeader("Content-Length",String.valueOf(mContentLength));
				InputStreamEntity ise = new InputStreamEntity(sis, mContentLength);
				// ProgressOutHttpEntity ProgressOutHhttpPostttpEntity = new
				// ProgressOutHttpEntity(ise, new ProgressListener() {
				// @Override
				// public void transferred(long transferedBytes) {
				// // TODO Auto-generated method stub
				// mTotalSize = mContentLength;
				// mUploadedSize = transferedBytes;
				// }
				// });
				httpPost.setEntity(ise);
			}
//			if(!rData.mHeads.containsKey("Accept")){
//				httpPost.setHeader("Accept", "application/json");
//			}
//			if(!rData.mHeads.containsKey("Content-type")){
//				httpPost.setHeader("Content-type", "application/json;charset=UTF-8");
//			}
//			Iterator<String> _iterator = rData.mHeads.keySet().iterator();
//			String _name, _value;
//			while (_iterator.hasNext()) {
//				_name = _iterator.next();
//				_value = rData.mHeads.get(_name);
//				httpPost.setHeader(_name, _value);
//			}
			return httpPost;
		}
	}
	
	private static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance("TLS");
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

	public static class RequestData {
		static enum HttpOption {
			GET, POST
		}

		HttpOption option = HttpOption.GET;
		String url;
		boolean synCallBack = false;
		private HashMap<String, String> mStringDataArr = null;
		private List<File> mFileDataList = null;
		RequestListener mListener = null;
		/** GET方式提交json字符串数据 */
		JSONObject mJsonData = null;
		String mCommitFilePath = null;
		String mContentType = null;
		String mMainBoundry = null;
		static String sHost = Const.SIN_HOST;
		HashMap<String,String> mHeads = new HashMap<String,String>();
		private RequestData(String url, RequestListener listener) {
			this(url, null, listener);
		}

		private RequestData(String url, JSONObject postData,
				RequestListener listener) {
			this.url = url;
			mListener = listener;
			this.putData(postData);
		}

		public RequestData syncCallback() {
			synCallBack = true;
			return this;
		}

		public boolean isValid() {
			return mListener != null && url != null && (url.startsWith("http://") || url.startsWith("https://"));
		}

		public void setCommitFilePath(String path){
			mCommitFilePath = path;
		}
		public void setOption(HttpOption option) {
			this.option = option;
		}

		public void putData(JSONObject jsonData) {
			if(jsonData != null){
				this.mJsonData = jsonData;
				this.option = HttpOption.POST;
			}
		}
		
		public HashMap<String, String> addHeader(String key,String value){
			mHeads.put(key, value);
			return mHeads;
		}

		/**
		 * 放入字符串数据(post请求有效)
		 * 
		 * @param key
		 * @param value
		 */
		public void putData(String key, String value) {
			if (mStringDataArr == null) {
				mStringDataArr = new HashMap<String, String>();
				this.option = HttpOption.POST;
			}
			mStringDataArr.put(key, value);
		}

		boolean hasStringData(){
			return mStringDataArr != null;
		}
		List<NameValuePair> getStringData() {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			Iterator<String> _iterator = mStringDataArr.keySet().iterator();
			String _name, _value;
			while (_iterator.hasNext()) {
				_name = _iterator.next();
				_value = mStringDataArr.get(_name);
				params.add(new BasicNameValuePair(_name, _value));
			}
			return params;
		}

		/**
		 * 放入文件数据(post请求有效)
		 */
		public void putData(String key, File file) {
			if (mFileDataList == null) {
				
				mFileDataList = new ArrayList<File>();
				this.option = HttpOption.POST;
			}
			mFileDataList.add(file);
		}

		public static RequestData newInstance(RequestListener listener,
				JSONObject json) {
			return new RequestData(RequestData.sHost, json, listener);
		}
		public static RequestData newInstance(RequestListener listener,
				JSONObject json,String url) {
			return new RequestData(url, json, listener);
		}

		public static RequestData newInstance(RequestListener listener,
				String subUrl) {
			return new RequestData(RequestData.sHost + (subUrl != null ? subUrl : ""), listener);
		}

		public static RequestData newInstance(RequestListener listener,
				String hostUrl, String subUrl) {
			return new RequestData(hostUrl + (subUrl != null ? subUrl : ""),
					listener);
		}
	}

	public static abstract class RequestListener implements OnDismissListener{
		public static final int ERROR_TIMEOUT = 0;
		private String url = null;
		boolean isOver = false;
		@Override
		public void onDismiss(DialogInterface arg0) {
			isOver = true;
		}
		public void onStart() {
		}
		/**
		 * 含有显示等待框逻辑
		 * @param context
		 * @param waitingMsg
		 */
		public void onStart(Context context,String waitingMsg) {
			ViewHelper.showGlobalWaiting(context, this,waitingMsg);
		}
		/**
		 * 含有显示等待框逻辑
		 */
		public void onStart(Context context) {
			ViewHelper.showGlobalWaiting(context, this);
		}

		public void onDataError(Context context,String msg){
			ViewHelper.showToast(context, msg);
		}
		public void onDataError(Context context){
			onDataError(context, (JSONObject)null);
		}
		
		public void onDataError(Context context,JSONObject response){
			String msg = Const.DEFT_NET_ERROR;
			if(response != null){
				msg = response.optString(Const.STA_MESSAGE);
			}
			onDataError(context, msg);
		}
		public void onError(int type, String msg) {
		}

		public void onSend(OutputStream os) {
		}

		public File toFile(byte[] data) {
			File f = new File(LocalMgr.sRootPath + System.nanoTime());
			try {
				FileOutputStream fos = new FileOutputStream(f);
				fos.write(data);
				fos.flush();
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return f;
		}

		public Bitmap toBitmap(byte[] data) {
			return BitmapFactory.decodeByteArray(data, 0, data.length);
		}

		public JSONObject toJSONObject(byte[] data) {
			try {
				if(data != null){
					String json = new String(data, "utf-8");
					return new JSONObject(json);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		public  boolean isRight(JSONObject jsonObject){
			return Const.DEFT_1.equals(jsonObject.optString(Const.STA_CODE));
		}
		public  boolean isRight(Context context,JSONObject response,boolean endWaiting){
			boolean ret = false;
			if(response != null){
				ret = Const.DEFT_1.equals(response.optString(Const.STA_CODE));
				if(!ret && endWaiting){
					onEnd();
				}
				if(!ret){
					ViewHelper.showToast(context, response.optString(Const.STA_MESSAGE));
				}
			}else{
				if(endWaiting){
					onEnd();
				}
				ViewHelper.showToast(context, Const.DEFT_NET_ERROR);
			}
			return ret;
		}
		/**
		 * 联网请求是否结束
		 * @return
		 */
		public boolean isOver(){
			return isOver;
		}

		public JSONArray toJSONArray(byte[] data) {
			try {
				String json = new String(data, "utf-8");
				return new JSONArray(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		/**
		 * 含有关闭等待快
		 */
		public void onEnd(){
			ViewHelper.closeGlobalWaiting();
		};
		public void onEnd(JSONObject data){};
		public abstract void onEnd(byte[] data);
	}
	
	public static void doo(){
		new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
//                return mTrafficFreeHelper.getSmsCode(phone);
            	return false;//
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                if (aBoolean) {
                	CountDownTimer mCountDownTimer = new CountDownTimer(100 * 1000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {
//                            mButtonGetSmsCode.setClickable(false);
//                            mButtonGetSmsCode.setText(String.valueOf(millisUntilFinished / 1000 - 1) + "秒");
                        }

                        @Override
                        public void onFinish() {
//                            mButtonGetSmsCode.setClickable(true);
//                            mButtonGetSmsCode.setText("获取验证码");
                        }
                    }.start();
                }
            }
        }.execute();
	}
	
	class BinaryRequest extends com.android.volley.toolbox.JsonRequest<byte[]>{
		
		public BinaryRequest(int method, RequestData rData,
				Listener<byte[]> listener, ErrorListener errorListener) {
			super(method, rData.url,rData.mJsonData != null ? rData.mJsonData.toString() : null, listener, errorListener);
			mRequest = rData;
			mListener = listener;
			if (rData.mJsonData != null && rData.mJsonData.length() > 0) {//json请求格式
			} else if(rData.mCommitFilePath != null){
			}else{
				rData.mMainBoundry = MultiPartStack.getBoundry();
				rData.mContentType = "multipart/form-data;boundary=" + rData.mMainBoundry;
			}
		}
		@Override
		public String getBodyContentType() {
			if(mRequest.mContentType != null){
				return mRequest.mContentType;
			}else{
				return super.getBodyContentType();
			}
		}
		public BinaryRequest(String url, String requestBody,
				Listener<byte[]> listener, ErrorListener errorListener) {
			super(url, requestBody, listener, errorListener);
			mListener = listener;
		}
		private final Response.Listener<byte[]> mListener;
		RequestData mRequest = null;
//		public BinaryRequest(String url, Response.Listener<byte[]> listener,ErrorListener errorListener) {
//			this(Method.POST,url,listener,errorListener);
//		}
//
//		public BinaryRequest(int method, String url, Response.Listener<byte[]> listener,ErrorListener errorListener) {
//			super(method, url, errorListener);
//			mListener = listener;
//		}

		@Override
		protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
			// TODO Auto-generated method stub
			return Response.success(response.data,HttpHeaderParser.parseCacheHeaders(response));
		}

		@Override
		protected void deliverResponse(byte[] response) {
			mListener.onResponse(response);
		}
		@Override
		public Map<String, String> getHeaders() throws AuthFailureError {
			// TODO Auto-generated method stub
			return mRequest.mHeads;
		}
		
	}
}
