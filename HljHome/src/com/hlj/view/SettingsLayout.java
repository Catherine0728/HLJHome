package com.hlj.view;

import java.util.Random;

import com.hj.widget.CommonToast;
import com.hlj.HomeActivity.R;
import com.hlj.HomeActivity.set.SettingAboutUs;
import com.hlj.HomeActivity.set.SettingAcademic;
import com.hlj.HomeActivity.set.SettingClear;
import com.hlj.HomeActivity.set.SettingDisPlayer;
import com.hlj.HomeActivity.set.SettingGrade;
import com.hlj.HomeActivity.set.SettingServer;
import com.hlj.HomeActivity.set.SettingSpeedTest;
import com.hlj.HomeActivity.set.SettingUs;
import com.hlj.HomeActivity.set.SettingWeather;
import com.hlj.utils.CommonTools;
import com.hlj.utils.ImageUtils;
import com.live.video.constans.Constants;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class SettingsLayout extends LinearLayout implements LayoutInterface,
		View.OnFocusChangeListener, View.OnClickListener {

	private Context mContext;
	private ScaleAnimEffect animEffect;

	private FrameLayout[] fls = new FrameLayout[8];
	private ImageView[] bgs = new ImageView[8];
	private ImageView[] setLog = new ImageView[8];
	private ImageView whiteBorder;

	ImageView[] arrayOfImageView = new ImageView[4];

	private static int[] bgSelector = { R.drawable.blue_no_shadow,
			R.drawable.dark_no_shadow, R.drawable.green_no_shadow,
			R.drawable.orange_no_shadow, R.drawable.pink_no_shadow,
			R.drawable.red_no_shadow, R.drawable.yellow_no_shadow,
			R.drawable.pink_no_shadowa, R.drawable.pink_no_s1hadow,
			R.drawable.pink_no_shad2ow };

	public SettingsLayout(Context context) {
		super(context);
		this.mContext = context;
		animEffect = new ScaleAnimEffect();
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setGravity(Gravity.CENTER_HORIZONTAL);
		setOrientation(VERTICAL);
		addView(LayoutInflater.from(this.mContext).inflate(R.layout.settings,
				null));

		this.fls[0] = ((FrameLayout) findViewById(R.id.set_server_fl));
		this.fls[1] = ((FrameLayout) findViewById(R.id.set_speed_fl));
		this.fls[2] = ((FrameLayout) findViewById(R.id.set_vod_fl));
		this.fls[3] = ((FrameLayout) findViewById(R.id.set_weather_fl));
		this.fls[4] = ((FrameLayout) findViewById(R.id.set_user_fl));
		this.fls[5] = ((FrameLayout) findViewById(R.id.set_clear_fl));
		this.fls[6] = ((FrameLayout) findViewById(R.id.set_other_fl));
		this.fls[7] = ((FrameLayout) findViewById(R.id.set_about_fl));
		this.bgs[0] = ((ImageView) findViewById(R.id.set_server_bg));
		this.bgs[1] = ((ImageView) findViewById(R.id.set_speed_bg));
		this.bgs[2] = ((ImageView) findViewById(R.id.set_vod_bg));
		this.bgs[3] = ((ImageView) findViewById(R.id.set_weather_bg));
		this.bgs[4] = ((ImageView) findViewById(R.id.set_user_bg));
		this.bgs[5] = ((ImageView) findViewById(R.id.set_clear_bg));
		this.bgs[6] = ((ImageView) findViewById(R.id.set_other_bg));
		this.bgs[7] = ((ImageView) findViewById(R.id.set_about_bg));
		this.setLog[0] = ((ImageView) findViewById(R.id.set_server_iv));
		this.setLog[1] = ((ImageView) findViewById(R.id.set_speed_iv));
		this.setLog[2] = ((ImageView) findViewById(R.id.set_vod_iv));
		this.setLog[3] = ((ImageView) findViewById(R.id.set_weather_iv));
		this.setLog[4] = ((ImageView) findViewById(R.id.set_user_iv));
		this.setLog[5] = ((ImageView) findViewById(R.id.set_clear_iv));
		this.setLog[6] = ((ImageView) findViewById(R.id.set_other_iv));
		this.setLog[7] = ((ImageView) findViewById(R.id.set_about_iv));
		arrayOfImageView[0] = ((ImageView) findViewById(R.id.set_refimg_1));
		arrayOfImageView[1] = ((ImageView) findViewById(R.id.set_refimg_2));
		arrayOfImageView[2] = ((ImageView) findViewById(R.id.set_refimg_3));
		arrayOfImageView[3] = ((ImageView) findViewById(R.id.set_refimg_4));

		this.whiteBorder = ((ImageView) findViewById(R.id.white_boder));
		FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(
				220, 220);
		localLayoutParams.leftMargin = 168;
		localLayoutParams.topMargin = 20;
		this.whiteBorder.setLayoutParams(localLayoutParams);

		int size = bgSelector.length;
		int[] newRandomArray = CommonTools.getRandomList(bgSelector);

		for (int i = 0; i < 8; i++) {
			bgs[i].setVisibility(View.GONE);
			this.setLog[i].setOnFocusChangeListener(this);
			this.setLog[i].setOnClickListener(this);
			this.setLog[i].setBackgroundResource(newRandomArray[i]);

			if (i > 3 && i < 8) {
				arrayOfImageView[i - 4].setImageBitmap(ImageUtils
						.createCutReflectedImage(
								ImageUtils.convertViewToBitmap(fls[i]), 0));
			}
		}

	}

	@Override
	public void loadData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateData() {
		// TODO Auto-generated method stub

	}

	private void showLooseFocusAinimation(int paramInt) {
		this.animEffect.setAttributs(1.1F, 1.0F, 1.1F, 1.0F, 100L);
		this.setLog[paramInt].startAnimation(this.animEffect.createAnimation());
		this.bgs[paramInt].setVisibility(View.GONE);
	}

	private void showOnFocusAnimation(final int paramInt) {
		this.fls[paramInt].bringToFront();
		this.animEffect.setAttributs(1.0F, 1.1F, 1.0F, 1.1F, 100L);
		Animation localAnimation = this.animEffect.createAnimation();
		localAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				bgs[paramInt].startAnimation(animEffect.alphaAnimation(0.0F,
						1.0F, 150L, 0L));
				bgs[paramInt].setVisibility(View.VISIBLE);
			}
		});
		this.setLog[paramInt].startAnimation(localAnimation);
	}

	private void flyWhiteBorder(int paramInt1, int paramInt2,
			float paramFloat1, float paramFloat2) {
		if ((this.whiteBorder == null))
			return;
		this.whiteBorder.setVisibility(0);
		int i = this.whiteBorder.getWidth();
		int j = this.whiteBorder.getHeight();
		ViewPropertyAnimator localViewPropertyAnimator = this.whiteBorder
				.animate();
		localViewPropertyAnimator.setDuration(150L);
		localViewPropertyAnimator.scaleX(paramInt1 / i);
		localViewPropertyAnimator.scaleY(paramInt2 / j);
		localViewPropertyAnimator.x(paramFloat1);
		localViewPropertyAnimator.y(paramFloat2);
		localViewPropertyAnimator.start();
	}

	int position = -1;

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.set_server_iv:
			if (hasFocus) {
				position = 0;
				showOnFocusAnimation(position);
				flyWhiteBorder(220, 220, 218, 40);
			} else {
				showLooseFocusAinimation(position);
				this.whiteBorder.setVisibility(View.INVISIBLE);
			}
			break;

		case R.id.set_speed_iv:
			if (hasFocus) {
				position = 1;
				showOnFocusAnimation(position);
				flyWhiteBorder(220, 220, 423, 40);
			} else {
				showLooseFocusAinimation(position);
				this.whiteBorder.setVisibility(View.INVISIBLE);
			}
			break;

		case R.id.set_vod_iv:
			if (hasFocus) {
				position = 2;
				showOnFocusAnimation(position);
				flyWhiteBorder(220, 220, 628, 40);
			} else {
				showLooseFocusAinimation(position);
				this.whiteBorder.setVisibility(View.INVISIBLE);
			}
			break;

		case R.id.set_weather_iv:
			if (hasFocus) {
				position = 3;
				showOnFocusAnimation(position);
				flyWhiteBorder(220, 220, 833, 40);
			} else {
				showLooseFocusAinimation(position);
				this.whiteBorder.setVisibility(View.INVISIBLE);
			}
			break;

		case R.id.set_user_iv:
			if (hasFocus) {
				position = 4;
				showOnFocusAnimation(position);
				flyWhiteBorder(220, 220, 218, 245);
			} else {
				showLooseFocusAinimation(position);
				this.whiteBorder.setVisibility(View.INVISIBLE);
			}
			break;

		case R.id.set_clear_iv:
			if (hasFocus) {
				position = 5;
				showOnFocusAnimation(position);
				flyWhiteBorder(220, 220, 423, 245);
			} else {
				showLooseFocusAinimation(position);
				this.whiteBorder.setVisibility(View.INVISIBLE);
			}
			break;

		case R.id.set_other_iv:
			if (hasFocus) {
				position = 6;
				showOnFocusAnimation(position);
				flyWhiteBorder(220, 220, 628, 245);
			} else {
				showLooseFocusAinimation(position);
				this.whiteBorder.setVisibility(View.INVISIBLE);
			}
			break;

		case R.id.set_about_iv:
			if (hasFocus) {
				position = 7;
				showOnFocusAnimation(position);
				flyWhiteBorder(220, 220, 833, 245);
			} else {
				showLooseFocusAinimation(position);
				this.whiteBorder.setVisibility(View.INVISIBLE);
			}
			break;

		}

	}

	@Override
	public void onClick(View v) {

		Intent intent = null;
		switch (v.getId()) {
		case R.id.set_server_iv:
			intent = new Intent(mContext, SettingServer.class);
			mContext.startActivity(intent);
			break;
		case R.id.set_speed_iv:
			intent = new Intent(mContext, SettingSpeedTest.class);
			mContext.startActivity(intent);
			break;
		case R.id.set_vod_iv:
			intent = new Intent(mContext, SettingDisPlayer.class);
			mContext.startActivity(intent);
			break;
		case R.id.set_weather_iv:
			intent = new Intent(mContext, SettingWeather.class);
			mContext.startActivity(intent);
			break;
		case R.id.set_user_iv:
			intent = new Intent(mContext, SettingUs.class);
			mContext.startActivity(intent);
			break;
		case R.id.set_clear_iv:
			intent = new Intent(mContext, SettingClear.class);
			mContext.startActivity(intent);
			break;
		case R.id.set_other_iv:
			// if ("1".equals(Constants.isGrade)) {
			// CommonToast toast = new CommonToast(mContext);
			// toast.setText("此版本不支持版本切换");
			// } else {
			intent = new Intent(mContext, SettingAcademic.class);
			// intent = new Intent(mContext, SettingGrade.class);
			mContext.startActivity(intent);
			// }
			break;
		case R.id.set_about_iv:
			intent = new Intent(mContext, SettingAboutUs.class);
			mContext.startActivity(intent);
			break;
		}
	}

}
