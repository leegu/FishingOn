package test.net.fishserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class LocalServices extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
