package com.jingye.coffeemac.service;

import com.jingye.coffeemac.common.database.TAnalyzer;
import com.jingye.coffeemac.helper.cache.BaseDataCacher;
import com.jingye.coffeemac.service.action.CoffeeAction;
import com.jingye.coffeemac.service.action.SailAction;
import com.jingye.coffeemac.service.action.TActionFactory;
import com.jingye.coffeemac.service.action.UserAction;
import com.jingye.coffeemac.service.core.VendorCore;
import com.jingye.coffeemac.util.log.LogUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;

public class VendorService extends Service implements IRemoteListener {

	/** extra from */
	public static final String EXTRA_FROM = "EXTRA_FROM";

	/** from unknown */
	public static final int FROM_UNKNOWN = 0;

	/** from package */
	public static final int FROM_PACKAGE = 1;

	/** from pending */
	public static final int FROM_PENDING = 2;

	/** from boot */
	public static final int FROM_BOOT = 3;

	/** alarm interval */
	public static final int ALARM_INTERVAL = 10 * 60 * 1000;
	public static boolean active = false;
	public static boolean isBind = false;
	public final RemoteCallbackList<IRemoteConnCall> mCallbacks = new RemoteCallbackList<IRemoteConnCall>();
	private VendorCore vendorCore = VendorCore.sharedInstance();

	public VendorService() {
		active = true;
	}

	public static void restartService(Context context) {
		context = context.getApplicationContext();
		Intent intent = new Intent(context, VendorService.class);
		intent.putExtra(VendorService.EXTRA_FROM, VendorService.FROM_PACKAGE);
		context.startService(intent);
	}

	public static void stopService(Context context) {
		context = context.getApplicationContext();
		Intent intent = new Intent(context, VendorService.class);
		context.stopService(intent);
	}

	/**
	 * start pending intent with service
	 *
	 * @param context
	 * @param interval
	 */
	public static void startPending(Context context, long interval) {
		context = context.getApplicationContext();
		Intent intent = new Intent(context, VendorService.class);
		intent.putExtra(VendorService.EXTRA_FROM, VendorService.FROM_PENDING);

		AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
		if (am == null) {
			return;
		}

		PendingIntent operation = PendingIntent.getService(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		if (operation == null) {
			return;
		}

		am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + interval, interval, operation);
	}

	/**
	 * stop pending intent with service
	 *
	 * @param context
	 */
	public static void stopPending(Context context) {
		context = context.getApplicationContext();
		Intent intent = new Intent(context, VendorService.class);

		AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
		if (am == null) {
			return;
		}

		PendingIntent operation = PendingIntent.getService(context, 0, intent, 0);
		if (operation == null) {
			return;
		}

		am.cancel(operation);
	}

	public void onCreate() {
		LogUtil.vendor("VendorService onCreate()");

		// 初始化数据库
		TAnalyzer.newInstance();

		vendorCore.setListener(this);
		vendorCore.setContext(this);

		createActionFactory();

		BaseDataCacher.instance();

		// startup core
		vendorCore.startup();
	}

	/**
	 * 注意，这里只是允许写连接绑定的代码，例如绑定后初始化等，是aidl管道模式，
	 * 故服务全局性代码不可以在这里写，应该在上面的onCreate里面写，切记
	 */
	public void onStart(Intent intent, int startId) {
		LogUtil.vendor("service start()");
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		int from = FROM_UNKNOWN;
		if (intent != null) {
			from = intent.getIntExtra(EXTRA_FROM, FROM_UNKNOWN);
		}

		/**
		 * when service starts, install an alarm to wake up service in case the
		 * os sleeps cases need start: boot complete, the first startup after
		 * apk installation install alarm always. if it exists, this will just
		 * update it TODO: decide the time to cancel alarm
		 */
		if (from != FROM_PENDING) {
			startPending(this, ALARM_INTERVAL);
		}

		/**
		 * cause pending intent of service can be schedule at a fixed rate and
		 * delivery intent so no need to be sticky
		 */
		LogUtil.vendor("service start from " + from);

		return from == FROM_PENDING ? START_NOT_STICKY : START_REDELIVER_INTENT;
	}

	public void onDestroy() {
		super.onDestroy();
		active = false;
		LogUtil.vendor("VendorService onDestroy()");
		// shutdown core
		vendorCore.shutdown();
	}

	public IBinder onBind(Intent arg0) {
		vendorCore.setUiProcessAlive(true);
		TRemoteConn service = new TRemoteConn(this, mCallbacks);
		return service;
	}

	public void callback(Remote remote) {
		synchronized (mCallbacks) {
			LogUtil.vendor("-----------------------" + Thread.currentThread());
			int n = mCallbacks.beginBroadcast();
			LogUtil.vendor("mCallbacks.count = " + n);
			for (int i = 0; i < n; i++) {
				try {
					mCallbacks.getBroadcastItem(i).receive(remote);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			mCallbacks.finishBroadcast();

			vendorCore.setUiProcessAlive(n > 0);
		}
	}

	/**
	 * 发送给客户端，即绑定过service的客户端,注册回调
	 * @param remote
	 */
	@Override
	public void onReceive(Remote remote) {
		callback(remote);
	}

	/**
	 * 在这里挂接动作处理
	 */
	private void createActionFactory() {
		TActionFactory actionFactory = TActionFactory.newInstance();
		actionFactory.registerAction(new SailAction());
		actionFactory.registerAction(new UserAction());
		actionFactory.registerAction(new CoffeeAction());
	}
}
