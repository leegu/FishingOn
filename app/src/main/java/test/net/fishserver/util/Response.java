package test.net.fishserver.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import test.net.fishserver.util.LocalServer.ServerListener;


import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class Response implements Runnable {

	Socket mSocket = null;
	String mUrl = null;
	ServerListener listener = null;
	Resources resources = null;
	public Response(Resources resources,Socket pSocket, ServerListener listener) {
		this.resources = resources;
		mSocket = pSocket;
		this.listener = listener;
		new Thread(this).start();
		// run();
	}

	final int BUFFER_SIZE = 10240;
	public void run() {
		InputStream _is = null;
		OutputStream _os = null;
		InputStream _resIs = null;
		try {
		//	Logger.e("receiving miniserver data!!!");
			_is = mSocket.getInputStream();
			Request _request = new Request(_is);
			_request.parse();
			String data = _request.getData();
			mUrl = _request.getUri();
			if(mUrl == null){
				return;
			}else if(!mUrl.contains("png")){
				mUrl += ".json";
			}
			if(listener != null) listener.onReceiver(new Date() +"\n" + mUrl);
			Log.v("Response", "localhost request=" + mUrl);
			if(this.resources == null){
				_resIs = new FileInputStream(new File(LocalServer.sRootPath + mUrl));
			}else{
				_resIs = this.resources.getAssets().open("fishing/" + mUrl);
			}
			_os = mSocket.getOutputStream();
			byte[] bytes = new byte[BUFFER_SIZE];
			if (_resIs != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
//					long startTime = System.currentTimeMillis();
				int len;
				while ((len = _resIs.read(bytes)) > 0) {
					baos.write(bytes, 0, len);
				}
//					System.out.println("useTime=" + (System.currentTimeMillis() - startTime));
				bytes = baos.toByteArray();
				addResponseHead(bytes.length, _os);
				_os.write(bytes);
			} else {
				// file not found
				String errorMessage = "HTTP/1.1 404 File Not Found\r\n"
						+ "Content-Type: text/html\r\n"
						+ "Content-Length: 23\r\n" + "\r\n"
						+ "<h1>File Not Found</h1>";
				_os.write(errorMessage.getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	private void writeRequest(OutputStream out, String host) throws IOException {
		out.write("GET /index.html HTTP/1.1".getBytes());
		out.write(("Host: " + host).getBytes()); 
		out.write(CRLF);
		
		out.write(CRLF);		
		out.flush();
	}

	
	private void write(OutputStream out, String msg) throws IOException {
		out.write(msg.getBytes());
	}

	private void addResponseHead(long contentLength, OutputStream out)
			throws IOException {
		writeHeader(out, /*"GET " + mUrl + " " + */"HTTP/1.1 200 OK");
		writeHeader(out, "Content-Type: " + getMimeType(mUrl));
		writeHeader(out, "Access-Control-Allow-Origin: *");
		writeHeader(out, "Access-Control-Allow-Headers: *");
//		write(out,
//				"Accept: application/soap+xml, application/dime, multipart/related, text/*");
//		out.write(CRLF);
//		write(out, "User-Agent: Axis/1.2.1");
//		out.write(CRLF);
//		write(out,"Host:" + MiniServer.HOST + ":" + Integer.toString(MiniServer.PORT));
//		out.write(CRLF);
//		write(out, "Cache-Control: no-cache");
//		out.write(CRLF);
//		write(out, "Pragma: no-cache");
//		out.write(CRLF);
//		write(out, "SOAPAction: \"\"");
//		out.write(CRLF);
		writeHeader(out, "Content-Length: " + contentLength);
		out.write(CRLF);// 单独的一行CRLF表示请求头的结束  
		out.flush();
	}

	void writeHeader(OutputStream out, String msg) throws IOException{
		write(out, msg);
		out.write(CRLF);
	}
	
	public static String getMimeType(String filename) {
		MimeTypeMap map = android.webkit.MimeTypeMap.getSingleton();
		String extType = MimeTypeMap.getFileExtensionFromUrl(filename);
		if(TextUtils.isEmpty(extType) && filename.lastIndexOf(".") >= 0){
			extType =filename.substring( filename.lastIndexOf(".") + 1);
		}
		return map.getMimeTypeFromExtension(extType);
	}
	
	private static final byte CR = (byte) '\r';
	private static final byte LF = (byte) '\n';
	private static final byte[] CRLF = new byte[] { CR, LF };


}
