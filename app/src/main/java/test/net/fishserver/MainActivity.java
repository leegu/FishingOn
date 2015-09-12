package test.net.fishserver;

import java.util.Random;

import test.net.fishserver.util.LocalServer;
import test.net.fishserver.util.LocalServer.ServerListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class MainActivity extends Activity implements ServerListener{

	LinearLayout rootView = null;
	LocalServer server = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LinearLayout recode = new LinearLayout(this);
		recode.setOrientation(LinearLayout.VERTICAL);
		rootView = recode;
		ScrollView sv = new ScrollView(this);
		sv.addView(recode);
		setContentView(sv);
		server = new LocalServer(this, this);
		server.start();
	}
	@Override
	public void onReceiver(String url) {
		Message m = Message.obtain();
		m.what = 0;
		m.obj = url;
		handler.sendMessage(m);
	}

	protected void onDestroy() {
		server.stop();
	};
	Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0){
				TextView t = new TextView(MainActivity.this);
				t.setText((String)(msg.obj));
				t.setBackgroundColor(new Random().nextInt(10000000) + 1000000);
				rootView.addView(t);
			}
		};
	};
}
