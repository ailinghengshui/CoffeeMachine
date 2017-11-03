package com.jingye.coffeemac.net.client;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jingye.coffeemac.application.AppConfig;
import com.jingye.coffeemac.util.log.LogUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.Locale;

public class ServerAddressFetcher {

	public static final String TAG = "ServerAddressFetcher";
	private static final long PERION_TIME = 1 * 60 * 1000;
	private static ServerAddressFetcher mInstance = new ServerAddressFetcher();
	private String HOST = "https://jiankong.hzjytech.com/app/ops/getLinkServerIP" +
			"?token=jykj2015jijiakafei&node=%s";
	private String SPAREHOST = "https://jktest.hzjytech.com/app/ops/getLinkServerIP" +
			"?token=jykj2015jijiakafei&node=%s";
	private String mHttpGetString;
	private long mTime;
	private IPAddress mLinkAddr;

	private ServerAddressFetcher() {
	}

	public static ServerAddressFetcher sharedInstance() {
		return mInstance;
	}

	private boolean isAddressInvalid(IPAddress address) {
		if (address == null || TextUtils.isEmpty(address.ip))
			return true;
		else
			return false;
	}

	public synchronized IPAddress getLinkAddress() {
		if (isAddressInvalid(mLinkAddr)) {
			updateAddress();
		} else {
			if (System.currentTimeMillis() - mTime >= ServerAddressFetcher.PERION_TIME) {
					updateAddress();
			}
		}

		LogUtil.d(TAG, "Link:" + mLinkAddr.ip + ":" + mLinkAddr.port);

		return mLinkAddr;
	}

	public synchronized void resetLinkAddress() {
		mLinkAddr = null;
	}

	private void updateAddress() {
		mHttpGetString = null;
		boolean useDefault = false;
		// ONLINE
		if(AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE){
			try {
				String URL = String.format(Locale.getDefault(), HOST, "newLinkServers");
				mHttpGetString = httpGet(URL);
			}catch (Exception e1) {
				e1.printStackTrace();
				LogUtil.e(TAG, "CONNECT HOST ERROR");

			}
		}
		// TEST
		else if(AppConfig.BUILD_SERVER == AppConfig.Build.TEST){
			try {
				String URL = String.format(Locale.getDefault(), HOST, "linkServers");
				mHttpGetString = httpGet(URL);
			}catch (Exception e) {
				e.printStackTrace();
				LogUtil.e(TAG, "CONNECT HOST ERROR");
			}
		}else if(AppConfig.BUILD_SERVER==AppConfig.Build.LOCAL){
			useDefault=true;
		}
		LogUtil.vendor("SERVER ADDRESS INFO -> " + mHttpGetString);

		if(TextUtils.isEmpty(mHttpGetString)){
			// ONLINE
			if(AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE){
				try {
					String URL = String.format(Locale.getDefault(), SPAREHOST, "newLinkServers");
					mHttpGetString = httpGet(URL);
				}catch (Exception e1) {
					e1.printStackTrace();
					LogUtil.e(TAG, "CONNECT SPAREHOST ERROR");

				}
			}
			// TEST
			else if(AppConfig.BUILD_SERVER == AppConfig.Build.TEST){
				try {
					String URL = String.format(Locale.getDefault(), SPAREHOST, "linkServers");
					mHttpGetString = httpGet(URL);
				}catch (Exception e) {
					e.printStackTrace();
					LogUtil.e(TAG, "CONNECT SPAREHOST ERROR");
				}
			}
			LogUtil.vendor("SERVER ADDRESS INFO SPAREHOST -> " + mHttpGetString);
		}



		if (!TextUtils.isEmpty(mHttpGetString)) {
			try {
				JSONObject object = JSON.parseObject(mHttpGetString);
				if(object.containsKey("status") && object.getInteger("status") == 200){
					String result = object.getString("result");
					if(!TextUtils.isEmpty(result)){
						JSONObject resultJB = JSON.parseObject(result);
						if(resultJB.containsKey("server")){
							String server = resultJB.getString("server");
							if(!TextUtils.isEmpty(server)){
								mLinkAddr = new IPAddress(server);
							}else{
								useDefault = true;
							}
						}
					}else{
						useDefault = true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				LogUtil.e(TAG, "LBS JSON ERROR" + e.getMessage());
				useDefault = true;
			}
		} else {
			useDefault = true;
		}

        if (useDefault) {
            mLinkAddr = new IPAddress();
            if (AppConfig.BUILD_SERVER == AppConfig.Build.ONLINE) {
                mLinkAddr.ip = "112.124.68.166";
                mLinkAddr.port = 4440;
            } else if (AppConfig.BUILD_SERVER == AppConfig.Build.TEST) {
//				mLinkAddr.ip = "121.40.70.138";
                mLinkAddr.ip = "121.40.227.138";
                mLinkAddr.port = 4440;
            } else if (AppConfig.BUILD_SERVER == AppConfig.Build.LOCAL) {
//				mLinkAddr.ip = "192.168.0.105";
//				mLinkAddr.port = 4440;
//			}else{
//				mLinkAddr.ip = "121.40.70.171";
				mLinkAddr.ip = "192.168.0.225";
				mLinkAddr.port = 4440;
			}
		}


		mTime = System.currentTimeMillis();
	}

	private String httpGet(String url) throws Exception {
		HttpGet get = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		HttpResponse resp = client.execute(get);
		int statusCode = resp.getStatusLine().getStatusCode();
		LogUtil.vendor(TAG + "-> statusCode is " + statusCode);
		if (statusCode == 200) {
			return EntityUtils.toString(resp.getEntity(), HTTP.UTF_8);
		}
		throw new Exception("Get error");
	}

	public static class IPAddress {
		public String ip;
		public int port;

		public IPAddress() {
		}

		public IPAddress(String ip) {
			if (!TextUtils.isEmpty(ip)) {
				int index = ip.indexOf(':');
				if (index != -1) {
					this.ip = ip.substring(0, index);
					try {
						port = Integer.parseInt(ip.substring(index+1));
					} catch (Exception e) {

					}
				} else {
					this.ip = ip;
				}
			}
		}

		public IPAddress(String ip, int port) {
			this.ip = ip;
			this.port = port;
		}

		public boolean isValid() {
			return !TextUtils.isEmpty(ip);
		}

		@Override
		public String toString() {
			return isValid() ? (ip + (port > 0 ? ":" + port : "")) : "INVALID";
		}
	}
}
