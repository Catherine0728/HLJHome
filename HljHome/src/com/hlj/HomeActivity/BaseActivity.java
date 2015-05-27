package com.hlj.HomeActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import com.hlj.net.GetTimeRequest;
import com.hlj.net.HttpHomeLoadDataTask;
import com.hlj.receiver.WeatherReceiver;
import com.hlj.receiver.WeatherReceiver.WeatherUpdateListener;
import com.hlj.utils.ACache;
import com.hlj.utils.FileUtils;
import com.hlj.utils.Logger;
import com.hlj.utils.NetUtils;
import com.hlj.utils.StringTools;
import com.hlj.utils.WeatherUtils;
import com.hlj.view.CommonTitleView;
import com.hlj.view.NetErrorDialog;
import com.home.exception.CrashHandler;
import com.live.video.constans.HomeConstants;
import com.live.video.constans.WeatherInfo;
import com.live.video.net.callback.IUpdateData;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * 基类
 * 
 * @author huangyuchao
 * 
 */
public class BaseActivity extends Activity implements WeatherUpdateListener {
	public static BaseActivity instace;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// if (!LibsChecker.checkVitamioLibs(this))
		// return;
		instace = this;
		cache = ACache.get(this);
		//
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(this);
		System.out.println("这才是进来我的这个咯哦！");

		initWeatherReceiver();
	}

	ACache cache;

	java.util.Timer timer;

	String logoUrl;

	@Override
	protected void onResume() {

		initTimerReceiver();

		// 获取锁的状态(从文件中读取)
		try {
			HomeConstants.isVersionLock = FileUtils
					.readFile(HomeConstants.isVersionLockPath);
			HomeConstants.isPrimaryLock = FileUtils
					.readFile(HomeConstants.isPrimaryLockPath);
			HomeConstants.isTempLock = FileUtils
					.readFile(HomeConstants.isTempLockPath);

		} catch (IOException e) {
			Logger.log(e.toString() + "<><><><><><>");
		}

		timer = new java.util.Timer();
		// 启动计时器
		timer.schedule(new Job(), 10 * 1000, 10 * 1000);
		super.onResume();

		logoUrl = cache.getAsString("logo");

		if (null != CommonTitleView.instance) {
			CommonTitleView.instance.setTime(StringTools.getSystemTime());
			CommonTitleView.instance.setGrageText();
			CommonTitleView.instance.setTermResource();
			CommonTitleView.instance.setLogo(logoUrl);
			CommonTitleView.instance.setTopInvisible();
		}

		// 从服务器获取时间
		getTimeFromNet();

		// MobclickAgent.onResume(this);

	}

	private void getTimeFromNet() {
		GetTimeRequest request = new GetTimeRequest(this);
		new HttpHomeLoadDataTask(this, callBack, false, "").execute(request);
	}

	String netTime;

	IUpdateData callBack = new IUpdateData() {

		@Override
		public void updateUi(Object o) {
			JSONObject json;
			try {
				json = new JSONObject(o.toString());
				String timestamp = json.getString("timestamp") + "000";
				json.getString("timeZone");

				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
				netTime = sdf.format(Long.parseLong(timestamp));
				if (!StringTools.getSystemTime().equals(netTime)) {
					CommonTitleView.instance.setTime(netTime);
				}
				long time = new Date().getTime();
				Logger.log("time:" + time);
				Logger.log("net time:" + netTime);

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void handleErrorData(String info) {

		}
	};

	public boolean pingIpAddr(String ipAddress) {
		@SuppressWarnings("unused")
		String mPingIpAddrResult = null;
		try {
			// We would need to get rid of this before release.
			Process p = Runtime.getRuntime().exec("ping -c 1 " + ipAddress);
			int status = p.waitFor();
			if (status == 0) {
				mPingIpAddrResult = "Pass";
				System.out.println("ping   success");
				return true;
			} else {
				mPingIpAddrResult = "Fail: IP addr not reachable";
				return false;
			}
		} catch (IOException e) {
			mPingIpAddrResult = "Fail: IOException";
			return false;
		} catch (InterruptedException e) {
			mPingIpAddrResult = "Fail: InterruptedException";
			return false;
		}
	}

	class Job extends TimerTask {

		@Override
		public void run() {

			// 判断网络
			if (NetUtils.getAPNType(BaseActivity.this) != -1) {
				// 有网络
				netErrorHandler.sendEmptyMessage(1);

			} else {

				netErrorHandler.sendEmptyMessage(0);

			}
		}

	}

	Handler netErrorHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {

			if (msg.what == 0) {
				showNetErrorDialog("请检查网络是否连接！");

			} else if (msg.what == 1) {
				if (dialog != null && dialog.isShowing()) {
					dialog.dismiss();
				}
			}

		};

	};

	public static NetErrorDialog dialog;

	/**
	 * 展示网络错误提示
	 */
	private void showNetErrorDialog(String str) {
		// Logger.logWarning("=====showNetErrorDialog====");

		if (dialog == null) {

			dialog = new NetErrorDialog(this, str, 0);
		}

		if (dialog != null && !dialog.isShowing()) {
			dialog.show();
		}
	}

	private TimeRecevier timeReceiver;
	private WeatherReceiver weatherReceiver;

	private void initWeatherReceiver() {
		weatherReceiver = new WeatherReceiver();
		registerReceiver(weatherReceiver, new IntentFilter("com.hlj.weather"));
		sendBroadcast(new Intent("com.hlj.weather"));
		WeatherReceiver.isForceGet = false;
	}

	private void initTimerReceiver() {
		timeReceiver = new TimeRecevier();

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.TIME_SET");
		intentFilter.addAction("android.intent.action.TIME_TICK");
		registerReceiver(timeReceiver, intentFilter);
	}

	private class TimeRecevier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
				if (null != CommonTitleView.instance) {
					CommonTitleView.instance.setTime(StringTools
							.getSystemTime());
				}
			}
		}

	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_HOME) {
	// BeforePreActivity.isRestart = false;
	// Logger.log("PreActivity.isRestart:" + BeforePreActivity.isRestart);
	// }
	// return super.onKeyDown(keyCode, event);
	// }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();

		// MobclickAgent.onPause(this);

	}

	@Override
	protected void onStop() {
		// unregisterReceiver();
		super.onStop();
		if (dialog != null && dialog.isShowing()) {
			dialog.cancel();
		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver();
		if (null != timer) {
			timer.cancel();
		}
		if (dialog != null && dialog.isShowing()) {
			dialog.cancel();
		}
		super.onDestroy();
	}

	private void unregisterReceiver() {
		if (timeReceiver != null) {
			unregisterReceiver(timeReceiver);
			timeReceiver = null;
		}
		if (weatherReceiver != null) {
			unregisterReceiver(weatherReceiver);
			weatherReceiver = null;
		}
	}

	int[] drawables;

	@Override
	public void updateWeather(WeatherInfo weatherInfo) {
		drawables = WeatherUtils.getWeaResByWeather(weatherInfo.getWeather1());
		if (drawables != null && drawables[0] != 0) {
			if (null != CommonTitleView.instance) {
				CommonTitleView.instance.setWeather1Img(drawables[0]);
				if (drawables[1] != 0) {
					CommonTitleView.instance.setWeather2Img(drawables[1]);
				}
			}
		}
	}

}
