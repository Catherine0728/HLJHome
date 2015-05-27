package com.hlj.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.hlj.net.GetContentInfoResponse.Item.PolymAddress;
import com.hlj.net.GetContentInfoResponse.Item.PolymAddress.Video;
import com.hlj.utils.JsonTools;
import com.hlj.utils.Logger;
import com.hlj.view.VideoInfo;
import com.live.video.constans.ChannelInfo;
import com.live.video.net.BaseResponse;

/**
 * 获取影视内容信息的返回
 * 
 * @author huangyuchao
 * 
 */
public class GetContentInfoResponse extends BaseResponse {

	public int teleplayPage;
	public int teleplayPageSize;
	public String contentId;

	public Item info;

	public PolymAddress address;

	public class Item {
		public String contentId;
		public int catId;
		public String title;
		public String images;
		public String intro;
		public String actor;
		public String timeLength;
		public String language;
		public String price;
		public String fileType;
		public String type;
		public String director;
		public String releaseDate;
		public String zone;
		public String series;
		public String sum;

		public class PolymAddress {
			public String videoSource;

			public class Video {
				public String videoId;
				public String videoAddress;
				public String name;
				public int progression;
				public int isDefaultPlay;
			}

			public HashMap<String, ArrayList> hashMap;
			public ArrayList<Video> videoList;
		}
	}

	public ArrayList<PolymAddress> list = new ArrayList<PolymAddress>();

	@Override
	public void paseRespones(String js) {

		try {
			JSONObject json = new JSONObject(js);

			teleplayPage = json.getInt("teleplayPage");
			teleplayPageSize = json.getInt("teleplayPageSize");
			contentId = json.getString("contentId");

			JSONObject obj = json.getJSONObject("item");
			info = new Item();
			info.contentId = JsonTools.getString(obj, "contentId");
			info.catId = JsonTools.getInt(obj, "catId");
			info.title = JsonTools.getString(obj, "title");
			info.images = JsonTools.getString(obj, "images");
			info.intro = JsonTools.getString(obj, "intro");
			info.actor = JsonTools.getString(obj, "actor");
			info.timeLength = JsonTools.getString(obj, "timeLength");
			info.language = JsonTools.getString(obj, "language");
			info.price = JsonTools.getString(obj, "price");
			info.fileType = JsonTools.getString(obj, "fileType");
			info.type = JsonTools.getString(obj, "type");
			info.director = JsonTools.getString(obj, "director");
			info.releaseDate = JsonTools.getString(obj, "releaseDate");
			info.zone = JsonTools.getString(obj, "zone");
			info.series = JsonTools.getString(obj, "series");

			JSONObject movieObj = obj.getJSONObject("movieurls");

			info.series = JsonTools.getString(movieObj, "sum");

			JSONArray jsonArray = movieObj.getJSONArray("polymAddress");
			for (int j = 0; j < jsonArray.length(); j++) {
				JSONObject polyJson = (JSONObject) jsonArray.get(j);
				address = info.new PolymAddress();
				address.videoSource = polyJson.getString("videoSource");
				address.videoList = new ArrayList<Video>();
				address.hashMap = new HashMap<String, ArrayList>();
				JSONArray videoArray = polyJson.getJSONArray("videos");
				for (int i = 0; i < videoArray.length(); i++) {
					JSONObject videoJson = (JSONObject) videoArray.get(i);
					Video video = address.new Video();
					video.videoId = JsonTools.getString(videoJson, "videoid");
					video.videoAddress = JsonTools.getString(videoJson,
							"videoAddress");
					video.progression = JsonTools.getInt(videoJson,
							"progression");
					video.name = JsonTools.getString(videoJson, "name");
					video.isDefaultPlay=JsonTools.getInt(videoJson, "isDefaultPlay");
					address.videoList.add(video);
				}
				address.hashMap.put(address.videoSource, address.videoList);

				list.add(address);
			}

		} catch (JSONException e) {
			Logger.e(e.toString()+"");
		}

	}

}