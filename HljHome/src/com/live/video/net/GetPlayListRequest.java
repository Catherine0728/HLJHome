package com.live.video.net;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.hlj.net.BaseRequest;
import com.hlj.utils.DeviceCheck;
import com.hlj.utils.Logger;
import com.live.video.constans.DeviceInfo;
import com.live.video.constans.HomeConstants.FunctionTagTable;
import com.live.video.net.callback.ICallBackRequest;
import com.live.video.net.callback.IHomeCallBackRequest;

/**
 * 获取视频播放记录列表请求
 * 
 * @author huangyuchao
 * 
 */
public class GetPlayListRequest extends BaseRequest implements
		IHomeCallBackRequest {
	public static String deviceUsersUniqueId = DeviceCheck
			.shareDeviceUsersUniqueId();
	public String userID = DeviceCheck.shareUserId();

	public String contentId;
	public String videoId;
	public String catId;
	public String grade;
	public String lessonId;
	public String versionId;
	public String subjectId;
	public String source;

	public String androidId;
	public String deviceTypeName = DeviceInfo.getDeviceName();
	public String androidVersion = DeviceInfo.getAndroidVersion();

	public GetPlayListRequest(Context context) {
		androidId = DeviceCheck.getAndroidId(context);
	}

	@Override
	public String getInfo() {
		API = FunctionTagTable.GETPLAYINFOLIST.getApi();
		VER = FunctionTagTable.GETPLAYINFOLIST.getVer();
		MODE = FunctionTagTable.GETPLAYINFOLIST.getMode();
		JSONObject dataJson = new JSONObject();
		try {
			dataJson.put("deviceUsersUniqueId", deviceUsersUniqueId);
			dataJson.put("userID", userID);

			dataJson.put("contentId", contentId);
			dataJson.put("videoId", videoId);
			dataJson.put("catId", catId);
			dataJson.put("grade", grade);
			dataJson.put("lessonId", lessonId);
			dataJson.put("versionId", versionId);
			dataJson.put("subjectId", subjectId);
			dataJson.put("source", source);

			dataJson.put("androidId", androidId);
			dataJson.put("androidVersion", androidVersion);
			dataJson.put("deviceTypeName", deviceTypeName);

		} catch (JSONException e) {
			Logger.log("getInfo error" + e.getLocalizedMessage());
		}
		return getJsonString(dataJson);
	}

	@Override
	public FunctionTagTable getNetTag() {
		return FunctionTagTable.GETPLAYINFOLIST;
	}
}