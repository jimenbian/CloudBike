package com.amap.navi.demo.activity;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.example.bstation.R;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;

/**
 * 模拟导航显示界面
 * 
 */
public class NaviEmulatorActivity extends Activity implements
		AMapNaviViewListener {
	private AMapNaviView mAmapAMapNaviView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navistander);
        ActionBar actionBar = getActionBar();
     	
     	actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("blue")));
		init(savedInstanceState);
	}

	/**
	 * 初始化
	 * 
	 * @param savedInstanceState
	 */
	private void init(Bundle savedInstanceState) {
		mAmapAMapNaviView = (AMapNaviView) findViewById(R.id.standernavimap);
		mAmapAMapNaviView.onCreate(savedInstanceState);
		mAmapAMapNaviView.setAMapNaviViewListener(this);
		// 设置模拟速度
		AMapNavi.getInstance(this).setEmulatorNaviSpeed(100);
		// 开启模拟导航
		AMapNavi.getInstance(this).startNavi(AMapNavi.EmulatorNaviMode);
		// 开启实时导航
		// AMapNavi.getInstance(this).startNavi(AMapNavi.GPSNaviMode);
	}

	/**
	 * 导航界面返回按钮监听
	 * */
	@Override
	public void onNaviCancel() {
		Intent intent = new Intent(NaviEmulatorActivity.this,
				NaviStartActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(intent);
		finish();
	}
	 

	@Override
	public void onNaviSetting() {

	}

	@Override
	public void onNaviMapMode(int arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * 返回键监听事件
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(NaviEmulatorActivity.this,
					NaviStartActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	// ------------------------------生命周期方法--------------------------- 
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mAmapAMapNaviView.onSaveInstanceState(outState);
	}

	@Override
	public void onResume() {
		super.onResume();
		mAmapAMapNaviView.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
		mAmapAMapNaviView.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mAmapAMapNaviView.onDestroy();
	}

	

}
