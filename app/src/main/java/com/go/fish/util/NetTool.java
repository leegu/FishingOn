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
import java.util.Iterator;
import java.util.List;
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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.go.fish.util.NetTool.RequestData.HttpOption;

public class NetTool {

	private static String TAG = "NetTool";
	static NetTool dataNetTool = null;
	static NetTool bitmapNetTool = null;

	private ExecutorService Handler = null;

	private NetTool() {
	};

	public static NetTool data() {
		if (dataNetTool == null) {
			dataNetTool = new NetTool();
			dataNetTool.Handler = Executors.newCachedThreadPool();
		}
		return dataNetTool;
	}

	public static NetTool bitmap() {
		if (bitmapNetTool == null) {
			bitmapNetTool = new NetTool();
			bitmapNetTool.Handler = Executors.newFixedThreadPool(3);
		}
		return bitmapNetTool;
	}

	/***
	 * 默认使用GET请求方式
	 * 
	 * @param rData
	 */
	public void http(final RequestData rData) {
		if (!rData.isValid()) {
			Log.e(TAG, "http, url is null");
			return;
		}
		Handler.execute(new Runnable() {
			public void run() {
				RequestListener listener = rData.mListener;
				if (rData.synCallBack) {
					MessageHandler
							.sendMessage(new MessageHandler.MessageListener<byte[]>() {
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
				HttpClient httpClient = getNewHttpClient();
				HttpUriRequest request = null;
				if (rData.option == HttpOption.GET) {
					if(rData.hasStringData()){
						String query = URLEncodedUtils.format(
								rData.getStringData(), HTTP.UTF_8);
						request = new HttpGet(rData.url + "?" + query);
					}else{
						request = new HttpGet(rData.url);
						
					}
				} else {
					request = onPost(rData);
				}
				try {
					HttpResponse resp = httpClient.execute(request);
					if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
						Log.e(TAG, "http fail, status code = "
								+ resp.getStatusLine().getStatusCode());
						listener.onEnd(null);
						return;
					}

					byte[] data = EntityUtils.toByteArray(resp.getEntity());
					if (rData.synCallBack) {
						MessageHandler
								.sendMessage(new MessageHandler.MessageListener<byte[]>() {
									byte data[] = null;

									@Override
									public MessageHandler.MessageListener init(
											byte[] data) {
										this.data = data;
										return this;
									}

									@Override
									public void onExecute() {
										rData.mListener.onEnd(this.data);
									}
								}.init(data));
					} else {
						listener.onEnd(data);
					}
				} catch (Exception e) {
					Log.e(TAG, "http exception, e = " + e.getMessage());
					e.printStackTrace();
					MessageHandler
							.sendMessage(new MessageHandler.MessageListener<byte[]>() {
								@Override
								public MessageHandler.MessageListener init(
										byte[] data) {
									return this;
								}

								@Override
								public void onExecute() {
									rData.mListener
											.onError(RequestListener.ERROR_TIMEOUT);
								}
							});
				}
			};
		});
	}

	/**
	 * 将请求参数写入输入流集合
	 */
	private long appendPostParemeter(Vector<InputStream> arr, String content,
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

	private long addPropertyInputStream(Vector<InputStream> pArr, String pKey,
			String pValue, long mContentLength) {
		mContentLength = appendPostParemeter(
				pArr,
				"Content-Disposition: form-data; name=\"" + pKey + "\"\r\n\r\n",
				mContentLength);
		mContentLength = appendPostParemeter(pArr, pValue + "\r\n",
				mContentLength);
		return mContentLength;
	}

	private long addFileInputStream(Vector<InputStream> pArr, String pKey,
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

	private long addCutoffLine(Vector<InputStream> pArr, String mainBoundry,
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

	private HttpUriRequest onPost(RequestData rData) {
		HttpPost httpPost = new HttpPost(rData.url);
		if (rData.mJsonData != null) {//json请求格式
			try {
				httpPost.setEntity(new StringEntity(rData.mJsonData.toString()));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");
		} else {//表单提交(文字数据、图片)
			String mMainBoundry = getBoundry();
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
			httpPost.addHeader("Content-Type", "multipart/form-data;boundary=" + mMainBoundry);
			// mHttpPost.addHeader("Content-Length",String.valueOf(mContentLength));
			InputStreamEntity ise = new InputStreamEntity(sis, mContentLength);
			// ProgressOutHttpEntity ProgressOutHttpEntity = new
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
		return httpPost;
	}

	private static class SSLSocketFactoryEx extends SSLSocketFactory {

		SSLContext sslContext = SSLContext.getInstance("TLS");

		public SSLSocketFactoryEx(KeyStore truststore)
				throws NoSuchAlgorithmException, KeyManagementException,
				KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}

				@Override
				public void checkServerTrusted(X509Certificate[] chain,
						String authType)
						throws java.security.cert.CertificateException {
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port,
				boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host,
					port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	private static HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

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
		private ArrayMap<String, String> mStringDataArr = null;
		private List<File> mFileDataList = null;
		RequestListener mListener = null;
		/** GET方式提交json字符串数据 */
		JSONObject mJsonData = null;
		static String sHost = Const.SIN_HOST;

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
			return mListener != null && url != null && url.length() > 0;
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

		/**
		 * 放入字符串数据(post请求有效)
		 * 
		 * @param key
		 * @param value
		 */
		public void putData(String key, String value) {
			if (mStringDataArr == null) {
				mStringDataArr = new ArrayMap<String, String>();
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
				String subUrl) {
			return new RequestData(RequestData.sHost + (subUrl != null ? subUrl : ""), listener);
		}

		public static RequestData newInstance(RequestListener listener,
				String hostUrl, String subUrl) {
			return new RequestData(hostUrl + (subUrl != null ? subUrl : ""),
					listener);
		}
	}

	public static abstract class RequestListener {
		public static final int ERROR_TIMEOUT = 0;

		public void onStart() {
		}

		public void onError(int type) {
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
				String json = new String(data, "utf-8");
				return new JSONObject(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
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

		public abstract void onEnd(byte[] data);
	}
}