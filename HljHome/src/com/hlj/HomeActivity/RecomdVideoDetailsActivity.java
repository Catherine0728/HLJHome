package com.hlj.HomeActivity;

import java.util.ArrayList;

import org.videolan.vlc.AudioService;
import org.videolan.vlc.AudioServiceController;
import org.videolan.vlc.LibVLC;
import org.videolan.vlc.LibVlcException;
import org.videolan.vlc.MediaLibrary;
import org.videolan.vlc.ThumbnailerManager;
import org.videolan.vlc.Util;
import org.videolan.vlc.VLCCallbackTask;
import org.videolan.vlc.gui.CompatErrorActivity;
import org.videolan.vlc.gui.PreferencesActivity;
import org.videolan.vlc.gui.SidebarAdapter;
import org.videolan.vlc.widget.AudioMiniPlayer;

import com.actionbarsherlock.app.ActionBar;
import com.hj.widget.CommonToast;
import com.hlj.adapter.AllPagesAdapter;
import com.hlj.adapter.DetailsKeyTabAdapter;
import com.hlj.adapter.HotVideoAdapter;
import com.hlj.net.AddFavRequest;
import com.hlj.net.BitmapWorkerTask;
import com.hlj.net.BitmapWorkerTask.ImageCallBack;
import com.hlj.net.DeleteFavRequest;
import com.hlj.net.GetContentInfoRequest;
import com.hlj.net.GetContentInfoResponse;
import com.hlj.net.GetContentRecRequest;
import com.hlj.net.GetContentRecResponse;
import com.hlj.net.GetContentInfoResponse.Item.PolymAddress;
import com.hlj.net.GetContentInfoResponse.Item.PolymAddress.Video;
import com.hlj.net.HttpHomeLoadDataTask;
import com.hlj.utils.CommonTools;
import com.hlj.utils.ImageLoader;
import com.hlj.utils.Logger;
import com.hlj.utils.Reflect3DImage;
import com.hlj.utils.StringTools;
import com.hlj.utils.StringUtils;
import com.hlj.view.CommonTitleView;
import com.hlj.view.VideoInfo;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.live.video.constans.Constants;
import com.live.video.net.callback.IUpdateData;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils.TruncateAt;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * 视频详情
 * 
 * @author huangyuchao
 * 
 */
public class RecomdVideoDetailsActivity extends BaseActivity implements
		OnClickListener {

	CommonTitleView commonTitleView;

	CommonToast toast;

	RadioGroup video_details_resources;

	String series;

	String from;
	int favid;

	String url;

	public final static String TAG = "VLC/MainActivity";

	protected static final String ACTION_SHOW_PROGRESSBAR = "org.videolan.vlc.gui.ShowProgressBar";
	protected static final String ACTION_HIDE_PROGRESSBAR = "org.videolan.vlc.gui.HideProgressBar";
	protected static final String ACTION_SHOW_TEXTINFO = "org.videolan.vlc.gui.ShowTextInfo";

	private static final String PREF_SHOW_INFO = "show_info";
	private static final String PREF_FIRST_RUN = "first_run";

	private static final int ACTIVITY_RESULT_PREFERENCES = 1;

	private ActionBar mActionBar;
	private SlidingMenu mMenu;
	private SidebarAdapter mSidebarAdapter;
	private AudioMiniPlayer mAudioPlayer;
	private AudioServiceController mAudioController;
	private ThumbnailerManager mThumbnailerManager;

	private View mInfoLayout;
	private ProgressBar mInfoProgress;
	private TextView mInfoText;
	private String mCurrentFragment;

	private SharedPreferences mSettings;

	private int mVersionNumber = -1;
	private boolean mFirstRun = false;
	private boolean mScanNeeded = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Logger.log("RecomdVideoDetailsActivity  ----------------执行了-----------这才是真正的进来了。。。。。-------");
		if (!Util.hasCompatibleCPU()) {
			Logger.log(Util.getErrorMsg());
			// Log.e(TAG, Util.getErrorMsg());
			Intent i = new Intent(this, CompatErrorActivity.class);
			startActivity(i);
			finish();
			super.onCreate(savedInstanceState);
			return;
		}

		/* Get the current version from package */
		PackageInfo pinfo = null;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
		} catch (NameNotFoundException e) {
			Logger.log("package info not found.");
			// Log.e(TAG, "package info not found.");
		}
		if (pinfo != null)
			mVersionNumber = pinfo.versionCode;

		/* Get settings */
		mSettings = PreferenceManager.getDefaultSharedPreferences(this);

		/* Check if it's the first run */
		mFirstRun = mSettings.getInt(PREF_FIRST_RUN, -1) != mVersionNumber;
		if (mFirstRun) {
			Editor editor = mSettings.edit();
			editor.putInt(PREF_FIRST_RUN, mVersionNumber);
			editor.commit();
		}

		try {
			// Start LibVLC
			LibVLC.getInstance();
		} catch (LibVlcException e) {
			e.printStackTrace();
			Intent i = new Intent(this, CompatErrorActivity.class);
			i.putExtra("runtimeError", true);
			i.putExtra("message",
					"LibVLC failed to initialize (LibVlcException)");
			startActivity(i);
			finish();
			super.onCreate(savedInstanceState);
			return;
		}

		super.onCreate(savedInstanceState);

		/*** Start initializing the UI ***/
		setContentView(R.layout.video_details);

		commonTitleView = (CommonTitleView) findViewById(R.id.commonTitle);
		toast = new CommonToast(this);

		/* Initialize UI variables */
		mInfoLayout = findViewById(R.id.info_layout);
		mInfoProgress = (ProgressBar) findViewById(R.id.info_progress);
		mInfoText = (TextView) findViewById(R.id.info_text);

		/* Set up the mini audio player */
		mAudioPlayer = new AudioMiniPlayer();
		mAudioController = AudioServiceController.getInstance();
		mAudioPlayer.setAudioPlayerControl(mAudioController);
		mAudioPlayer.update();

		initView();
		initData();

		/* Show info/alpha/beta Warning */
		if (mSettings.getInt(PREF_SHOW_INFO, -1) != mVersionNumber) {
			// showInfoDialog();
		} else if (mFirstRun) {
			/*
			 * The sliding menu is automatically opened when the user closes the
			 * info dialog. If (for any reason) the dialog is not shown, open
			 * the menu after a short delay.
			 */
			final Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mMenu.showMenu();
				}
			}, 500);
		}

		/* Prepare the progressBar */
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SHOW_PROGRESSBAR);
		filter.addAction(ACTION_HIDE_PROGRESSBAR);
		filter.addAction(ACTION_SHOW_TEXTINFO);
		registerReceiver(messageReceiver, filter);

		/* Reload the latest preferences */
		reloadPreferences();

		/* Load the thumbnailer */
		mThumbnailerManager = new ThumbnailerManager(this, getWindowManager()
				.getDefaultDisplay());
	}

	private void reloadPreferences() {
		SharedPreferences sharedPrefs = getSharedPreferences("MainActivity",
				MODE_PRIVATE);
		mCurrentFragment = sharedPrefs.getString("fragment", "video");
	}

	private void showInfoDialog() {
		final Dialog infoDialog = new Dialog(this, R.style.info_dialog);
		infoDialog.setContentView(R.layout.info_dialog);
		Button okButton = (Button) infoDialog.findViewById(R.id.ok);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CheckBox notShowAgain = (CheckBox) infoDialog
						.findViewById(R.id.not_show_again);
				if (notShowAgain.isChecked() && mSettings != null) {
					Editor editor = mSettings.edit();
					editor.putInt(PREF_SHOW_INFO, mVersionNumber);
					editor.commit();
				}
				/* Close the dialog */
				infoDialog.dismiss();
				/* and finally open the sliding menu if first run */
				if (mFirstRun)
					mMenu.showMenu();
			}
		});
		infoDialog.show();
	}

	private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equalsIgnoreCase(ACTION_SHOW_PROGRESSBAR)) {
				// setSupportProgressBarIndeterminateVisibility(true);
				getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			} else if (action.equalsIgnoreCase(ACTION_HIDE_PROGRESSBAR)) {
				// setSupportProgressBarIndeterminateVisibility(false);
				getWindow().clearFlags(
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			} else if (action.equalsIgnoreCase(ACTION_SHOW_TEXTINFO)) {
				String info = intent.getStringExtra("info");
				int max = intent.getIntExtra("max", 0);
				int progress = intent.getIntExtra("progress", 100);
				mInfoText.setText(info);
				mInfoProgress.setMax(max);
				mInfoProgress.setProgress(progress);
				mInfoLayout.setVisibility(info != null ? View.VISIBLE
						: View.GONE);
			}
		}
	};

	@Override
	protected void onResume() {
		CommonTitleView.instance = commonTitleView;
		super.onResume();

		mAudioController.addAudioPlayer(mAudioPlayer);
		AudioServiceController.getInstance().bindAudioService(this);

		/*
		 * FIXME: this is used to avoid having MainActivity twice in the
		 * backstack
		 */
		if (getIntent().hasExtra(AudioService.START_FROM_NOTIFICATION))
			getIntent().removeExtra(AudioService.START_FROM_NOTIFICATION);

		/* Load media items from database and storage */
		if (mScanNeeded)
			MediaLibrary.getInstance(this).loadMediaItems(this);

	}

	@Override
	protected void onPause() {
		super.onPause();

		/* Check for an ongoing scan that needs to be resumed during onResume */
		mScanNeeded = MediaLibrary.getInstance(this).isWorking();
		/* Stop scanning for files */
		MediaLibrary.getInstance(this).stop();
		/* Stop the thumbnailer */
		mThumbnailerManager.stop();
		/* Save the tab status in pref */
		SharedPreferences.Editor editor = getSharedPreferences("MainActivity",
				MODE_PRIVATE).edit();
		editor.putString("fragment", mCurrentFragment);
		editor.commit();

		mAudioController.removeAudioPlayer(mAudioPlayer);
		AudioServiceController.getInstance().unbindAudioService(this);
	};

	ImageView details_poster;

	TextView details_name, details_year, details_rate;
	TextView details_director, details_update, details_actors,
			details_playTimes;
	TextView details_video_introduce;

	Button details_replay, details_play, details_choose, details_colection;

	String contentid;

	ImageLoader loader;

	LayoutInflater inflater;

	GridView recommend_grid;

	LinearLayout details_key_tv, details_key_arts;

	public static HotVideoAdapter adapter;

	public static ArrayList<VideoInfo> recList;

	private void initView() {

		contentid = this.getIntent().getStringExtra("contentid");
		from = this.getIntent().getStringExtra("from");
		favid = this.getIntent().getIntExtra("favid", 0);
		url = this.getIntent().getStringExtra("url");

		Logger.log("contentid:" + contentid);
		Logger.log("url:" + url);
		Logger.log("from:" + from);
		Logger.log("favid:" + favid);

		details_poster = (ImageView) findViewById(R.id.details_poster);
		details_poster.setImageBitmap(Reflect3DImage.skewImage(BitmapFactory
				.decodeResource(this.getResources(), R.drawable.hao260x366),
				260, 366, 50));

		details_name = (TextView) findViewById(R.id.details_name);
		details_year = (TextView) findViewById(R.id.details_year);
		details_rate = (TextView) findViewById(R.id.details_rate);

		details_director = (TextView) findViewById(R.id.details_director);
		details_update = (TextView) findViewById(R.id.details_update);
		details_actors = (TextView) findViewById(R.id.details_actors);
		details_playTimes = (TextView) findViewById(R.id.details_playTimes);

		details_video_introduce = (TextView) findViewById(R.id.details_video_introduce);

		details_replay = (Button) findViewById(R.id.details_replay);
		details_replay.setOnClickListener(this);
		details_play = (Button) findViewById(R.id.details_play);
		details_play.setOnClickListener(this);

		details_play.setFocusable(true);
		details_play.requestFocus();

		details_choose = (Button) findViewById(R.id.details_choose);
		details_choose.setOnClickListener(this);
		details_colection = (Button) findViewById(R.id.details_colection);

		if ("Collection".equals(from)) {
			details_colection.setText("取消");
		} else {
			details_colection.setText("收藏");
		}

		details_colection.setOnClickListener(this);

		video_details_resources = (RadioGroup) findViewById(R.id.video_details_resources);

		loader = new ImageLoader(this, 0);

		inflater = LayoutInflater.from(this);

		recommend_grid = (GridView) findViewById(R.id.details_recommend);

		recommend_grid.setSelector(new ColorDrawable(0));

		recList = new ArrayList<VideoInfo>();
		adapter = new HotVideoAdapter(RecomdVideoDetailsActivity.this, recList);
		recommend_grid.setAdapter(adapter);

		recommend_grid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent it = new Intent(RecomdVideoDetailsActivity.this,
						RecomdVideoDetailsActivity.class);
				it.putExtra("contentid", recList.get(position).contentId);
				startActivity(it);
				finish();
			}
		});

		details_key_tv = (LinearLayout) findViewById(R.id.details_key_tv);
		details_key_arts = (LinearLayout) findViewById(R.id.details_key_arts);

		createSourceLayout(list);

		video_details_resources
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						if (checkedId != -1) {
							// video_details_resources.check(checkedId);
							RadioButton rb = (RadioButton) video_details_resources
									.findViewById(checkedId);
							int position = video_details_resources
									.indexOfChild(rb);
							// 切换list
							videoList = list.get(position).videoList;

							videoSourceName = list.get(position).videoSource;
						}

					}
				});

		getContentInfo();
	}

	String videoSourceName;

	private void createSourceLayout(ArrayList<PolymAddress> list) {
		video_details_resources.removeAllViews();
		int size = list.size();
		if (size > 0) {
			for (int j = 0; j < size; j++) {
				RadioButton radionButton = (RadioButton) this.inflater.inflate(
						R.layout.vediodetail_rb, null);
				radionButton
						.setCompoundDrawablesWithIntrinsicBounds(0, 0,
								StringUtils.sourceStringToResourceID(list
										.get(j).videoSource), 0);
				radionButton
						.setBackgroundResource(R.drawable.detailsource_bg_s);
				// radionButton.setFocusable(true);

				video_details_resources.addView(radionButton);
			}
			video_details_resources.check(video_details_resources.getChildAt(0)
					.getId());
		}

	}

	ArrayList<PolymAddress> list = new ArrayList<PolymAddress>();

	public static ArrayList<String> addresslist = new ArrayList<String>();

	public static ArrayList<Video> videoList = new ArrayList<Video>();

	public static String videoId, videoaddress, title, imgurl;

	private void initData() {

	}

	private void getContentInfo() {
		GetContentInfoRequest request = new GetContentInfoRequest(this);
		request.contentId = contentid;
		request.teleplayPage = 1;
		request.teleplayPageSize = 100;
		new HttpHomeLoadDataTask(this, getContentCallBack, true, url, false)
				.execute(request);
	}

	GetContentInfoResponse response = new GetContentInfoResponse();

	IUpdateData getContentCallBack = new IUpdateData() {

		@Override
		public void updateUi(Object o) {
			Logger.log(o.toString());

			response.paseRespones(o.toString());
			imgurl = response.info.images;
			// loader.displayImage(imgurl, details_poster);

			// Bitmap bitmap = ImageUtils.convertViewToBitmap(details_poster);

			new BitmapWorkerTask(RecomdVideoDetailsActivity.this,
					details_poster, true, false, imageCallBack).execute(imgurl);

			title = response.info.title;
			details_name.setText(title);
			details_year.setText(response.info.releaseDate);

			if (StringTools.isNullOrEmpty(response.info.timeLength)
					|| "0".endsWith(response.info.timeLength)) {
				details_rate.setText("");
			} else {
				details_rate.setText(response.info.timeLength + "分钟");
			}

			details_director.setText("导演：" + response.info.director);
			details_update.setText("类别：" + response.info.fileType);
			details_actors.setText("演员：" + response.info.actor);
			details_playTimes.setText("地区：" + response.info.zone);
			details_video_introduce.setText(response.info.intro);

			list = response.list;
			if (null != response.list && response.list.size() > 0) {
				videoList = response.list.get(0).videoList;
			}

			// videoId = videoList.get(0).videoId;
			System.out.println("the videolist is ====>" + videoList);
			series = response.info.series;

			if ("1".equals(series)) {
				details_choose.setVisibility(View.GONE);
			} else {
				details_choose.setVisibility(View.VISIBLE);
			}

			int totalSum = Integer.valueOf(series);
			if (totalSum > 1) {
				// for (int i = 0; i < totalSum; i++) {
				// // 有总集数返回几个块
				// detailsKeyList.add((i + 1) + "");
				// }
				getTabList(totalSum);
			}

			createSourceLayout(list);
			if (null != list && list.size() > 0) {
				videoSourceName = list.get(0).videoSource;
			}

			getRecInfo();

		}

		@Override
		public void handleErrorData(String info) {
			// TODO Auto-generated method stub

		}

	};

	ImageCallBack imageCallBack = new ImageCallBack() {

		@Override
		public void post(Bitmap bitmap) {
			details_poster.setImageBitmap(Reflect3DImage.skewImage(bitmap, 260,
					366, 50));
		}
	};

	private ArrayList<String> getTabList(int totalSum) {
		double num = Math.ceil(totalSum / 5.0);
		for (int i = 0; i < num; i++) {
			String s = new String((1 + i * 5) + "-" + (i + 1) * 5);
			if (totalSum < (i + 1) * 5) {
				s = new String((1 + i * 5) + "-" + totalSum);
			}
			detailsKeyList.add(s);
			System.out.println("the detailsKeyList is===>" + detailsKeyList);
		}
		return detailsKeyList;
	}

	private void getRecInfo() {
		GetContentRecRequest request = new GetContentRecRequest(this);
		request.title = title;
		request.type = response.info.type;
		request.actor = response.info.actor;
		request.catId = response.info.catId;
		request.contentId = response.info.contentId;
		new HttpHomeLoadDataTask(this, getRecCallBack, false, "", false)
				.execute(request);
	}

	IUpdateData getRecCallBack = new IUpdateData() {

		public void updateUi(Object o) {
			Logger.log(o.toString());
			GetContentRecResponse response = new GetContentRecResponse();
			response.paseRespones(o.toString());

			recList = response.list;
			adapter = new HotVideoAdapter(RecomdVideoDetailsActivity.this,
					recList);
			recommend_grid.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		};

		@Override
		public void handleErrorData(String info) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.details_play:

			if (null != videoList && videoList.size() > 0) {
				videoaddress = videoList.get(0).videoAddress;
				videoId = videoList.get(0).videoId;

				// 存储title
				CommonTools.saveTitle(title);

				CommonTools.setVideoName("");

				Constants.isDefaultPlay = videoList.get(0).isDefaultPlay;

				if (Constants.isDefaultPlay == 0
						|| Constants.isDefaultPlay == 1
						|| Constants.isDefaultPlay == 4) {
					androidPlay("");
					System.out.println("hello,我执行的是androidplay。。。。");
				} else if (Constants.isDefaultPlay == 2) {
					vlcPlay();
					System.out.println("hello,我执行的是vlcPlay。。。。");
				}
			} else {
				toast.setIcon(R.drawable.toast_err);
				toast.setText("暂时无此视频");
				toast.show();
			}
			break;
		case R.id.details_colection:

			String s = ((Button) v).getText().toString();

			if ("收藏".equals(s)) {
				addFav();
			}

			else if ("取消".equals(s)) {
				delFav();
			}

			break;
		case R.id.details_choose:
			// 选集
			if (details_key_tv.getVisibility() == View.VISIBLE
					|| details_key_arts.getVisibility() == View.VISIBLE) {
				recommend_grid.setVisibility(View.VISIBLE);
				details_key_tv.setVisibility(View.GONE);
				details_key_arts.setVisibility(View.GONE);
			} else {
				recommend_grid.setVisibility(View.GONE);
				details_key_tv.setVisibility(View.VISIBLE);
				details_key_arts.setVisibility(View.GONE);
				createTvLayout();
			}

			break;
		}
	}

	VLCCallbackTask task;

	/**
	 * vlc播放
	 */
	public void vlcPlay() {
		task = new VLCCallbackTask(this) {

			@Override
			public void run() {
				AudioServiceController c = AudioServiceController.getInstance();
				Logger.log("videoaddress:" + videoaddress);
				c.append(videoaddress);
			}
		};
		task.execute();
	}

	/**
	 * 添加到Viewpager
	 * 
	 * @param videoList
	 * @return
	 */
	public ArrayList<View> addViewToPager(ArrayList<Video> videoList) {
		ArrayList<View> list = new ArrayList<View>();
		int num = videoList.size();

		double number = Math.ceil(num / 5.0);

		for (int i = 0; i < number; i++) {
			int sum = (i + 1) * 5;
			if (sum > num) {
				sum = num;
			}
			LinearLayout ll = new LinearLayout(this);
			ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, 50);
			ll.setLeft(40);
			ll.setLayoutParams(params);
			for (int j = i * 5; j < sum; j++) {
				ll.addView(createSetBtn(j, videoList.get(j).name));
			}
			list.add(ll);
		}

		return list;
	}

	private Button createSetBtn(final int i, final String name) {
		Button button = new Button(this);
		button.setWidth(256);
		button.setHeight(65);

		final String numText = (i + 1) + ":" + name;

		button.setText(numText);
		button.setTextSize(17.0F);
		// button.setMaxLines(2);
		button.setSingleLine(true);
		button.setEllipsize(TruncateAt.MARQUEE);
		button.setMarqueeRepeatLimit(3);
		button.setTag(Integer.valueOf(i));
		button.setBackgroundResource(R.drawable.video_details_btn_selector);
		button.setTextColor(Color.WHITE);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				videoaddress = videoList.get(i).videoAddress;
				videoId = videoList.get(i).videoId;

				// 存储title
				CommonTools.saveTitle(title);
				CommonTools.setVideoName(name);

				Constants.isDefaultPlay = videoList.get(i).isDefaultPlay;

				if (Constants.isDefaultPlay == 0
						|| Constants.isDefaultPlay == 1
						|| Constants.isDefaultPlay == 4) {
					androidPlay("");
					System.out.println("hello,我执行的是androidPlay。。。。");
				} else if (Constants.isDefaultPlay == 2) {
					vlcPlay();
					System.out.println("hello,我执行的是vlcPlay。。。。");
				}

			}
		});

		return button;
	}

	public void androidPlay(String name) {

		Intent it = new Intent(RecomdVideoDetailsActivity.this,
				VideoPlayActivity.class);
		// it.putStringArrayListExtra("list", detailsKeyList);
		// it.putExtra("numDrama", numDrama);
		it.putExtra("name", 1);
		it.putExtra("url", videoaddress);
		it.putExtra("title", title);
		it.putExtra("contentId", contentid);
		it.putExtra("videoId", videoId);
		it.putExtra("numText", name);
		it.putExtra("videoSourceName", videoSourceName);
		startActivity(it);
	}

	ViewPager viewPager;
	Gallery gallery;

	public static DetailsKeyTabAdapter dktAdapter;
	AllPagesAdapter apAdapter;

	public static ArrayList<String> detailsKeyList = new ArrayList<String>();

	private void createTvLayout() {
		viewPager = (ViewPager) findViewById(R.id.details_key_pager);
		gallery = (Gallery) findViewById(R.id.details_key_gallery);

		dktAdapter = new DetailsKeyTabAdapter(this, detailsKeyList);
		gallery.setAdapter(dktAdapter);

		apAdapter = new AllPagesAdapter(addViewToPager(videoList));
		viewPager.setAdapter(apAdapter);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				gallery.setSelection(arg0);

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});

		gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				viewPager.setCurrentItem(position);

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void addFav() {
		AddFavRequest request = new AddFavRequest(this);
		request.title = title;
		request.url = imgurl;
		request.sourceID = contentid;
		new HttpHomeLoadDataTask(this, addFavcallBack, false, "", true)
				.execute(request);
	}

	IUpdateData addFavcallBack = new IUpdateData() {

		@Override
		public void updateUi(Object o) {
			Logger.log(o.toString());

			// details_colection.setText("取消");
			// details_colection.setTag("cancel");

			toast.setIcon(R.drawable.toast_smile);
			toast.setText("新增收藏成功！");
			toast.show();
		}

		@Override
		public void handleErrorData(String info) {
			toast.setIcon(R.drawable.toast_err);
			toast.setText("新增收藏失败！");
			toast.show();
		}

	};

	private void delFav() {
		DeleteFavRequest request = new DeleteFavRequest(this);
		request.id = favid;
		new HttpHomeLoadDataTask(this, delFavcallBack, false, "", true)
				.execute(request);
	}

	IUpdateData delFavcallBack = new IUpdateData() {

		@Override
		public void updateUi(Object o) {
			Logger.log(o.toString());

			// details_colection.setText("收藏");
			// details_colection.setTag("add");
			toast.setIcon(R.drawable.toast_smile);
			toast.setText("取消收藏成功！");
			toast.show();
		}

		@Override
		public void handleErrorData(String info) {
			toast.setIcon(R.drawable.toast_err);
			toast.setText("取消收藏失败！");
			toast.show();
		}

	};

	public boolean onKeyDown(int keyCode, android.view.KeyEvent arg1) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			if (details_key_tv.getVisibility() == View.VISIBLE
					|| details_key_arts.getVisibility() == View.VISIBLE) {
				recommend_grid.setVisibility(View.VISIBLE);
				details_key_tv.setVisibility(View.GONE);
				details_key_arts.setVisibility(View.GONE);
				return true;
			}

		}
		return super.onKeyDown(keyCode, arg1);
	};

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (task != null && !task.isCancelled()) {
			task.cancel(true);
		}

		try {
			unregisterReceiver(messageReceiver);
		} catch (IllegalArgumentException e) {
		}
		if (mThumbnailerManager != null)
			mThumbnailerManager.clearJobs();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
		/* Reload the latest preferences */
		reloadPreferences();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == ACTIVITY_RESULT_PREFERENCES) {
			if (resultCode == PreferencesActivity.RESULT_RESCAN)
				MediaLibrary.getInstance(this).loadMediaItems(this, true);
		}
	}

}
