package com.live.video.net;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.hlj.utils.JsonTools;
import com.hlj.utils.Logger;
import com.live.video.constans.VodRecode;
import com.live.video.net.GetFilmMetroDataResponse.MetroData;

/**
 * 收藏列表返回
 * 
 * @author huangyuchao
 * 
 */
public class GetFavListResponse extends BaseResponse {

	public ArrayList<VodRecode> list = new ArrayList<VodRecode>();

	public int count;

	@Override
	public void paseRespones(String js) {
		try {
			JSONObject json = new JSONObject(js);
			count = json.getInt("count");

			JSONArray array = json.getJSONArray("items");
			for (int i = 0; i < array.length(); i++) {
				JSONObject obj = (JSONObject) array.get(i);
				VodRecode info = new VodRecode();
				info.id = JsonTools.getInt(obj, "id");

				info.sourceid = JsonTools.getString(obj, "sourceid");
				info.title = JsonTools.getString(obj, "title");
				info.type = JsonTools.getString(obj, "type");
				info.imgUrl = JsonTools.getString(obj, "url");
				info.addTime = JsonTools.getString(obj, "addTime");

				Logger.log("id" + info.id);
				Logger.log("sourceid" + info.sourceid);
				Logger.log("title" + info.title);
				Logger.log("type" + info.type);
				Logger.log("imgUrl" + info.imgUrl);
				Logger.log("addTime" + info.addTime);

				list.add(info);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}
}
