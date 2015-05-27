package com.hlj.net;

import android.content.Context;

import com.hlj.utils.DeviceCheck;
import com.live.video.constans.DeviceInfo;
import com.live.video.constans.HomeConstants;
import com.live.video.net.callback.IHomeCallBackRequest;

import org.json.JSONObject;

/**
 * 
 * 
 *
 */
public class GetDeviceCheckRequest extends BaseRequest implements
		IHomeCallBackRequest {

	public String wifiMac;
	public String ethernetMac;
	public String snCode;

	public String deviceUsersUniqueId;
	public String androidId;
	public String deviceTypeName = DeviceInfo.getDeviceName();
	public String androidVersion = DeviceInfo.getAndroidVersion();

	public String systemVersion = DeviceInfo.getSystemVersion();
	public String cpuId = DeviceInfo.getCpuIDHex();
	public String cpuName = DeviceInfo.getCpuName();
	public String deviceDrive = DeviceInfo.getDevice();
	public String deviceBoardName = DeviceInfo.getBoardName();
	public String deviceBootLoader = DeviceInfo.getBootloader();
	public String deviceBrand = DeviceInfo.getBrand();
	public String systemCodeName = DeviceInfo.getCodeName();
	public String deviceDisplay = DeviceInfo.getDisplay();
	public String productName = DeviceInfo.getProductName();
	public String deviceTags = DeviceInfo.getTags();
	public String deviceVersion = DeviceInfo.getId();
	public String deviceModel = DeviceInfo.getModel();
	public String deviceHost = DeviceInfo.getHost();
	public String systemIncremental = DeviceInfo.getIncremental();
	public String deviceFingerPrint = DeviceInfo.getFingerPrint();
	public String deviceUserName = DeviceInfo.getUser();
	public String deviceType = DeviceInfo.getType();
	public String deviceHardwareName = DeviceInfo.getHardware();
	public String deviceManufacturer = DeviceInfo.getManufacturer();
	public String ScreenSize = "";
	public String ExtraData = "";
	public String firmwareVersion = DeviceInfo.getFirmWare();
	public String launcherVersion;

	public String randomString = DeviceCheck.getRandomUUID();

	public GetDeviceCheckRequest(Context context, String str) {
		androidId = DeviceCheck.getAndroidId(context);
		wifiMac = DeviceCheck.getWifiMac(context);
		ethernetMac = DeviceCheck.getEthernetMac(context);
		launcherVersion = DeviceInfo.getVerName(context);
		snCode = str;
	}

	@Override
	public String getInfo() {
		API = HomeConstants.FunctionTagTable.GETDEVICECHECK.getApi();
		VER = HomeConstants.FunctionTagTable.GETDEVICECHECK.getVer();
		MODE = HomeConstants.FunctionTagTable.GETDEVICECHECK.getMode();
		JSONObject dataJson = new JSONObject();
		try {
			dataJson.put("deviceUsersUniqueId", deviceUsersUniqueId);
			dataJson.put("wifiMac", wifiMac);
			dataJson.put("ethernetMac", ethernetMac);

			dataJson.put("androidId", androidId);
			dataJson.put("androidVersion", androidVersion);
			dataJson.put("deviceTypeName", deviceTypeName);

			dataJson.put("systemVersion", systemVersion);
			dataJson.put("cpuId", cpuId);
			dataJson.put("cpuName", cpuName);
			dataJson.put("deviceDrive", deviceDrive);
			dataJson.put("deviceBoardName", deviceBoardName);
			dataJson.put("deviceBootLoader", deviceBootLoader);
			dataJson.put("deviceBrand", deviceBrand);
			dataJson.put("systemCodeName", systemCodeName);
			dataJson.put("deviceDisplay", deviceDisplay);
			dataJson.put("productName", productName);
			dataJson.put("deviceTags", deviceTags);
			dataJson.put("deviceHost", deviceHost);
			dataJson.put("deviceVersion", deviceVersion);
			dataJson.put("deviceModel", deviceModel);
			dataJson.put("systemIncremental", systemIncremental);
			dataJson.put("deviceFingerPrint", deviceFingerPrint);
			dataJson.put("deviceUserName", deviceUserName);
			dataJson.put("deviceType", deviceType);
			dataJson.put("deviceHardwareName", deviceHardwareName);
			dataJson.put("deviceManufacturer", deviceManufacturer);
			dataJson.put("ScreenSize", ScreenSize);
			dataJson.put("ExtraData", ExtraData);
			dataJson.put("firmwareVersion", firmwareVersion);
			dataJson.put("lanucherVersion", launcherVersion);
			dataJson.put("sn", snCode);

			dataJson.put("randomString", randomString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getJsonString(dataJson);
	}

	@Override
	public HomeConstants.FunctionTagTable getNetTag() {
		return HomeConstants.FunctionTagTable.GETDEVICECHECK;
	}
}
