package test.net.fishserver.util;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;

public class LocalServer implements Runnable {
	final String TAG = "miniserver";
	int mPort = 18080;
	String HOST = "127.0.0.1";
	ServerSocket mServerSocket = null;
	boolean mRunning = false;
	ServerListener listener = null;
	static String sRootPath = null;
	Resources mResources = null;
	public LocalServer(Context context, ServerListener listener) {
		this.mResources = context.getResources();
		this.listener = listener;
		try {
			sRootPath = Environment.getExternalStorageDirectory().getPath() + "/.fish/www/assets/fishing/";
			File f = new File(sRootPath);
			if(!f.getParentFile().exists()){
				f.getParentFile().mkdirs();
			}
			mServerSocket = new ServerSocket(mPort, 1,
					InetAddress.getByName(HOST));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		new Thread(this).start();
	}

	public void stop() {
		if (mServerSocket != null) {
			try {
				mServerSocket.close();
				mServerSocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		mRunning = false;
	}

	@Override
	public void run() {
		mRunning = true;
		while (mRunning) {
			Socket socket = null;
			try {
				socket = mServerSocket.accept();
				new Response(mResources,socket, listener);
			} catch (Exception e) {
				e.printStackTrace();
				mRunning = false;
				continue;
			}
		}
	}
	
	public static interface ServerListener {
		void onReceiver(String url);
	}
}
