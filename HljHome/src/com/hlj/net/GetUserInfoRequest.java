package com.hlj.net;

import org.json.JSONObject;

import android.content.Context;

import com.hlj.utils.DeviceCheck;
import com.live.video.constans.DeviceInfo;
import com.live.video.constans.HomeConstants;
import com.live.video.net.callback.IHomeCallBackRequest;

/**
 * 获取用户信息
 * 
 * @author hyc
 * 
 */
public class GetUserInfoRequest extends BaseRequest implements
		IHomeCallBackRequest {

	public String deviceUsersUniqueId = DeviceCheck.shareDeviceUsersUniqueId();
	public String androidId;
	public String deviceTypeName = DeviceInfo.getDeviceName();
	public String androidVersion = DeviceInfo.getAndroidVersion();

	public String userID = DeviceCheck.shareUserId();

	public GetUserInfoRequest(Context context) {
		androidId = DeviceCheck.getAndroidId(context);
	}

	@Override
	public String getInfo() {
		API = HomeConstants.FunctionTagTable.GETUSERINFO.getApi();
		VER = HomeConstants.FunctionTagTable.GETUSERINFO.getVer();
		MODE = HomeConstants.FunctionTagTable.GETUSERINFO.getMode();
		JSONObject dataJson = new JSONObject();
		try {
			dataJson.put("deviceUsersUniqueId", deviceUsersUniqueId);
			//dataJson.put("userID", userID);

			dataJson.put("androidId", androidId);
			dataJson.put("androidVersion", androidVersion);
			dataJson.put("deviceTypeName", deviceTypeName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getJsonString(dataJson);
	}

	@Override
	public HomeConstants.FunctionTagTable getNetTag() {
		return HomeConstants.FunctionTagTable.GETUSERINFO;
	}

}
