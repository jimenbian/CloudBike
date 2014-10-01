package com.amap.navi.demo.activity;

import java.util.ArrayList;
import java.util.List;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.navi.demo.MainApplication;
import com.amap.navi.demo.TTSController;
import com.example.bstation.MainActivity;
import com.example.bstation.PartActivity;
import com.example.bstation.R;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * Demo初始化界面，用于设置起点终点，发起路径计算导航等
 * 
 */
@SuppressLint("NewApi")
public class NaviStartActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener, OnMapClickListener {
	// --------------View基本控件---------------------
	private MapView mMapView;// 地图控件
	private RadioGroup mNaviMethodGroup;// 步行驾车选择控件
	private AutoCompleteTextView mStartPointText;// 起点输入
	private EditText mWayPointText;// 途经点输入
	private EditText mEndPointText;// 终点输入
	private AutoCompleteTextView mStrategyText;// 行车策略输入
	private Button mRouteButton;// 路径规划按钮
	private Button mNaviButton;// 模拟导航按钮
	private ProgressDialog mProgressDialog;// 路径规划过程显示状态
	private ProgressDialog mGPSProgressDialog;// GPS过程显示状态
	private ImageView mStartImage;// 起点下拉按钮
	private ImageView mWayImage;// 途经点点击按钮
	private ImageView mEndImage;// 终点点击按钮
	private ImageView mStrategyImage;// 行车策略点击按钮
	// 地图和导航核心逻辑类
	private AMap mAmap;
	private AMapNavi mAmapNavi;
	// ---------------------变量---------------------
	private String[] mStrategyMethods;// 记录行车策略的数组
	private String[] mPositionMethods;// 记录起点我的位置、地图点选数组
	// 驾车路径规划起点，途经点，终点的list
	private List<NaviLatLng> mStartPoints = new ArrayList<NaviLatLng>();
	private List<NaviLatLng> mWayPoints = new ArrayList<NaviLatLng>();
	private List<NaviLatLng> mEndPoints = new ArrayList<NaviLatLng>();
	// 记录起点、终点、途经点位置
	private NaviLatLng mStartPoint = new NaviLatLng();
	private NaviLatLng mEndPoint = new NaviLatLng();
	private NaviLatLng mWayPoint = new NaviLatLng();
	// 记录起点、终点、途经点在地图上添加的Marker
	private Marker mStartMarker;
	private Marker mWayMarker;
	private Marker mEndMarker;
	private Marker mGPSMarker;
	private boolean mIsGetGPS = false;// 记录GPS定位是否成功
	private boolean mIsStart = false;// 记录是否已我的位置发起路径规划

	private ArrayAdapter<String> mPositionAdapter;

	private AMapNaviListener mAmapNaviListener;

	// 记录地图点击事件相应情况，根据选择不同，地图响应不同
	private int mMapClickMode = MAP_CLICK_NO;
	private static final int MAP_CLICK_NO = 0;// 地图不接受点击事件
	private static final int MAP_CLICK_START = 1;// 地图点击设置起点
	private static final int MAP_CLICK_WAY = 2;// 地图点击设置途经点
	private static final int MAP_CLICK_END = 3;// 地图点击设置终点

	// 记录导航种类，用于记录当前选择是驾车还是步行
	private int mTravelMethod = DRIVER_NAVI_METHOD;
	private static final int DRIVER_NAVI_METHOD = 0;// 驾车导航
	private static final int WALK_NAVI_METHOD = 1;// 步行导航

	private int mNaviMethod;
	private static final int NAVI_METHOD = 0;// 执行模拟导航操作
	private static final int ROUTE_METHOD = 1;// 执行计算线路操作

	private int mStartPointMethod = BY_MY_POSITION;
	private static final int BY_MY_POSITION = 0;// 以我的位置作为起点
	private static final int BY_MAP_POSITION = 1;// 以地图点选的点为起点
	// 计算路的状态
	private final static int GPSNO = 0;// 使用我的位置进行计算、GPS定位还未成功状态
	private final static int CALCULATEERROR = 1;// 启动路径计算失败状态
	private final static int CALCULATESUCCESS = 2;// 启动路径计算成功状态

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navistart);
		// 初始化所需资源、控件、事件监听
        ActionBar actionBar = getActionBar();    	
     	actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("blue")));
		initResources();
		initView(savedInstanceState);
		initListener();
		initMapAndNavi();
	
		//double a=39.991927;
		Bundle bundle = this.getIntent().getExtras();
		double b=bundle.getDouble("log");
		double a=bundle.getDouble("ati");
		//double c=116.430947;
		LatLng position1=new LatLng(a, b);
		mEndMarker.setPosition(position1);
		NaviLatLng naviLatLng1 = new NaviLatLng(position1.latitude,
				position1.longitude);
		mEndPoints.clear();
		mEndPoint = naviLatLng1;
		mEndPoints.add(naviLatLng1);
		setTextDescription(mEndPointText, "单车停靠点");
		
	}
	// ----------具体处理方法--------------
		/**
		 * 算路的方法，根据选择可以进行行车和步行两种方式进行路径规划
		 */
		private void calculateRoute() {
			switch (mTravelMethod) {
			// 驾车导航
			case DRIVER_NAVI_METHOD:
				int driverIndex = calculateDriverRoute();
				if (driverIndex == CALCULATEERROR) {
					showToast("路线计算失败,检查参数情况");
					return;
				} else if (driverIndex == GPSNO) {
					return;
				}
				break;
			// 步行导航
			case WALK_NAVI_METHOD:
				int walkIndex = calculateWalkRoute();
				if (walkIndex == CALCULATEERROR) {
					showToast("路线计算失败,检查参数情况");
					return;
				} else if (walkIndex == GPSNO) {
					return;
				}
				break;
			}
			// 显示路径规划的窗体
			showProgressDialog();
		}

		/**
		 * 对行车路线进行规划
		 */
		private int calculateDriverRoute() {
			int driveMode = getDriveMode();
			int code = CALCULATEERROR;
			switch (mStartPointMethod) {
			// 地图点选方式
			case BY_MAP_POSITION:
				// 支持多个起点，起点列表的尾点为导航起点，起点列表按车行方向排列，带有方向信息，可有效避免算路到马路的另一侧；
				// 支持多个终点，终点列表的尾点为导航终点，终点列表按车行方向排列，带有方向信息，可有效避免算路到马路的另一侧；
				// Demo中起点、终点、途经点只以一个点为例进行计算
				if (mAmapNavi.calculateDriveRoute(mStartPoints, mEndPoints,
						mWayPoints, driveMode)) {
					code = CALCULATESUCCESS;
				} else {

					code = CALCULATEERROR;
				}
				break;
			// 我的位置
			case BY_MY_POSITION:
				if (!mIsGetGPS) {
					showGPSProgressDialog();
					code = GPSNO;
					mIsStart = true;
					break;
				}
				// 支持多个终点，终点列表的首点为导航终点，终点列表按车行方向排列，带有方向信息，可有效避免算路到马路的另一侧；
				if (mAmapNavi
						.calculateDriveRoute(mEndPoints, mWayPoints, driveMode)) {
					code = CALCULATESUCCESS;
				} else {

					code = CALCULATEERROR;
				}
				break;
			}
			return code;
		}

		/**
		 * 对步行路线进行规划
		 */
		private int calculateWalkRoute() {
			int code = CALCULATEERROR;
			switch (mStartPointMethod) {
			//地图点选方式
			case BY_MAP_POSITION:
				if (mAmapNavi.calculateWalkRoute(mStartPoint, mEndPoint)) {
					code = CALCULATESUCCESS;
				} else {
					 
					code = CALCULATEERROR;
				}
				break;
				//我的位置方式
			case BY_MY_POSITION:

				if (!mIsGetGPS) {
					showGPSProgressDialog();
					mIsStart = true;
					code = GPSNO;
					break;
				}
				if (mAmapNavi.calculateWalkRoute(mEndPoint)) {
					code = CALCULATESUCCESS;
				} else {
					 
					code = CALCULATEERROR;
				}
				break;
			}
			return code;
		}

		

		/**
		 * 根据选择，获取行车策略
		 */
		private int getDriveMode() {
			String strategyMethod = mStrategyText.getText().toString();
			// 速度优先
			if (mStrategyMethods[0].equals(strategyMethod)) {
				return AMapNavi.DrivingDefault;
			}
			// 花费最少
			else if (mStrategyMethods[1].equals(strategyMethod)) {
				return AMapNavi.DrivingSaveMoney;

			}
			// 距离最短
			else if (mStrategyMethods[2].equals(strategyMethod)) {
				return AMapNavi.DrivingShortDistance;
			}
			// 不走高速
			else if (mStrategyMethods[3].equals(strategyMethod)) {
				return AMapNavi.DrivingNoExpressways;
			}
			// 时间最短且躲避拥堵
			else if (mStrategyMethods[4].equals(strategyMethod)) {
				return AMapNavi.DrivingFastestTime;
			} else if (mStrategyMethods[5].equals(strategyMethod)) {
				return AMapNavi.DrivingAvoidCongestion;
			} else {
				return AMapNavi.DrivingDefault;
			}
		}
	
	// -----------------初始化-------------------

	/**
	 * 初始化界面所需View控件
	 * 
	 * @param savedInstanceState
	 */
	@SuppressLint("NewApi")
	private void initView(Bundle savedInstanceState) {
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		mAmap=mMapView.getMap();
		mStartPointText = (AutoCompleteTextView) findViewById(R.id.navi_start_edit);
		 
		mStartPointText
				.setDropDownBackgroundResource(R.drawable.whitedownborder);
		mWayPointText = (EditText) findViewById(R.id.navi_way_edit);
		mEndPointText = (EditText) findViewById(R.id.navi_end_edit);
		mStrategyText = (AutoCompleteTextView) findViewById(R.id.navi_strategy_edit);
		mStrategyText.setDropDownBackgroundResource(R.drawable.whitedownborder);
		mStartPointText.setInputType(InputType.TYPE_NULL);
		mWayPointText.setInputType(InputType.TYPE_NULL);
		mEndPointText.setInputType(InputType.TYPE_NULL);
		mStrategyText.setInputType(InputType.TYPE_NULL);

		ArrayAdapter<String> strategyAdapter = new ArrayAdapter<String>(this,
				R.layout.strategy_inputs, mStrategyMethods);
		mStrategyText.setAdapter(strategyAdapter);

		mPositionAdapter = new ArrayAdapter<String>(this,
				R.layout.strategy_inputs, mPositionMethods);
		mStartPointText.setAdapter(mPositionAdapter);

		mRouteButton = (Button) findViewById(R.id.navi_route_button);
		mNaviButton = (Button) findViewById(R.id.navi_navi_button);
		mNaviMethodGroup = (RadioGroup) findViewById(R.id.navi_method_radiogroup);

		mStartImage = (ImageView) findViewById(R.id.navi_start_image);
		mWayImage = (ImageView) findViewById(R.id.navi_way_image);
		mEndImage = (ImageView) findViewById(R.id.navi_end_image);
		mStrategyImage = (ImageView) findViewById(R.id.navi_strategy_image);
	  
	}

	/**
	 * 初始化资源文件，主要是字符串
	 */
	private void initResources() {
		Resources res = getResources();
		mStrategyMethods = new String[] {
				res.getString(R.string.navi_strategy_speed),
				res.getString(R.string.navi_strategy_cost),
				res.getString(R.string.navi_strategy_distance),
				res.getString(R.string.navi_strategy_nohighway),
				res.getString(R.string.navi_strategy_timenojam),
				res.getString(R.string.navi_strategy_costnojam) };
		mPositionMethods = new String[] { res.getString(R.string.mypoistion),
				res.getString(R.string.mappoistion) };

	}

	/**
	 * 初始化所需监听
	 */
	private void initListener() {
		// 控件点击事件
		mStartPointText.setOnClickListener(this);
		mWayPointText.setOnClickListener(this);
		mEndPointText.setOnClickListener(this);
		mStrategyText.setOnClickListener(this);
		mRouteButton.setOnClickListener(this);
		mNaviButton.setOnClickListener(this);
		mStartImage.setOnClickListener(this);
		mWayImage.setOnClickListener(this);
		mEndImage.setOnClickListener(this);
		mStrategyImage.setOnClickListener(this);
		mNaviMethodGroup.setOnCheckedChangeListener(this);
		// 设置地图点击事件
		mAmap.setOnMapClickListener(this);
		// 起点下拉框点击事件监听
		mStartPointText.setOnItemClickListener(getOnItemClickListener());
	}

	/**
	 * 初始化地图和导航相关内容
	 */
	private void initMapAndNavi() {
		// 初始语音播报资源
		setVolumeControlStream(AudioManager.STREAM_MUSIC);// 设置声音控制
		TTSController ttsManager = TTSController.getInstance(this);// 初始化语音模块
		ttsManager.init();
		mAmapNavi = AMapNavi.getInstance(this);// 初始化导航引擎
		mAmapNavi.setAMapNaviListener(ttsManager);// 设置语音模块播报
		// 初始化Marker添加到地图
		mStartMarker = mAmap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
						.decodeResource(getResources(), R.drawable.start))));
		mWayMarker = mAmap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
						.decodeResource(getResources(), R.drawable.way))));
		mEndMarker = mAmap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
						.decodeResource(getResources(), R.drawable.end))));
		mGPSMarker = mAmap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
						.decodeResource(getResources(),
								R.drawable.location_marker))));
	
	}

	// ----------------------事件处理---------------------------

	/**
	 * 控件点击事件监听
	 * */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 路径规划按钮处理事件
		case R.id.navi_route_button:
			mNaviMethod = ROUTE_METHOD;
			calculateRoute();
			break;
		// 模拟导航处理事件
		case R.id.navi_navi_button:
			mNaviMethod = NAVI_METHOD;
			calculateRoute();
			break;
		// 起点点击事件
		case R.id.navi_start_image:
		case R.id.navi_start_edit:
			setTextDescription(mStartPointText, null);
			mPositionAdapter = new ArrayAdapter<String>(this,
					R.layout.strategy_inputs, mPositionMethods);
			mStartPointText.setAdapter(mPositionAdapter);
			mStartPoints.clear();
			mStartMarker.setPosition(null);
			mStartPointText.showDropDown();
			break;
		// 途经点点击事件
		case R.id.navi_way_image:
		case R.id.navi_way_edit:
			mMapClickMode = MAP_CLICK_WAY;
			mWayPoints.clear();
			mWayMarker.setPosition(null);
			setTextDescription(mWayPointText, "点击地图设置途经点");
			showToast("点击地图添加途经点");
			break;
		// 终点点击事件
		case R.id.navi_end_image:
		case R.id.navi_end_edit:
			mMapClickMode = MAP_CLICK_END;
			mEndPoints.clear();
			mEndMarker.setPosition(null);
			setTextDescription(mEndPointText, "点击地图设置终点");
			showToast("点击地图添加终点");
			break;
		// 策略点击事件
		case R.id.navi_strategy_image:
		case R.id.navi_strategy_edit:
			mStrategyText.showDropDown();
			break;
		}
	}

	/**
	 * 驾车、步行选择按钮事件监听
	 * */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		// 驾车模式
		case R.id.navi_driver_button:
			mTravelMethod = DRIVER_NAVI_METHOD;
			// 驾车可以途经点和策略的设置
			mWayPointText.setEnabled(true);
			mStrategyText.setEnabled(true);
			mWayImage.setEnabled(true);
			mStrategyImage.setEnabled(true);
			break;
		// 步行导航模式
		case R.id.navi_walk_button:
			mTravelMethod = WALK_NAVI_METHOD;
			mWayPointText.setEnabled(false);
			mStrategyText.setEnabled(false);
			mWayImage.setEnabled(false);
			mStrategyImage.setEnabled(false);
			mWayMarker.setPosition(null);
			mWayPoints.clear();
			break;
		}
	}

	/**
	 * 地图点击事件监听
	 * */
	@Override
	public void onMapClick(LatLng latLng) {
		// 默认不接受任何操作
		if (mMapClickMode == MAP_CLICK_NO) {
			return;
			
		}
		// 其他情况根据起点、途经点、终点不同逻辑处理不同
		//Log.v(latLng.latitude+"dddd",latLng.longitude+"fwfe");
		addPointToMap(latLng);

	}
	/**
	 * 地图点击事件核心处理逻辑
	 * @param position
	 */
	private void addPointToMap(LatLng position) {
		NaviLatLng naviLatLng = new NaviLatLng(position.latitude,
				position.longitude);
	
		
		switch (mMapClickMode) {
		//起点
		case MAP_CLICK_START:
			mStartMarker.setPosition(position);
			mStartPoint = naviLatLng;
			mStartPoints.clear();
			mStartPoints.add(mStartPoint);
			setTextDescription(mStartPointText, "已成功设置起点");
			break;
		//途经点	
		case MAP_CLICK_WAY:
			mWayMarker.setPosition(position);
			mWayPoints.clear();
			mWayPoint = naviLatLng;
			mWayPoints.add(mWayPoint);
			setTextDescription(mWayPointText, "已成功设置途经点");
			break;
			//终点
		case MAP_CLICK_END:
			break;
		}

	}
	/**
	 * 起点下拉框点击事件监听
	 * */
	private OnItemClickListener getOnItemClickListener() {
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				switch (index) {
				// 我的位置为起点进行导航或路径规划
				case 0:
					mStartPointMethod = BY_MY_POSITION;
					break;
				// 地图点选起点进行导航或路径规划
				case 1:
					mStartPointMethod = BY_MAP_POSITION;
					mMapClickMode = MAP_CLICK_START;
					showToast("点击地图添加起点");
					break;
				}

			}

		};
	}

	/**
	 * 导航回调函数
	 * 
	 * @return
	 */
	private AMapNaviListener getAMapNaviListener() {
		if (mAmapNaviListener == null) {

			mAmapNaviListener = new AMapNaviListener() {
          
				@Override
				public void onTrafficStatusUpdate() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onStartNavi(int arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onReCalculateRouteForYaw() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onReCalculateRouteForTrafficJam() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onLocationChange(AMapNaviLocation location) {
					//GPS位置更新回调函数
					mIsGetGPS = true;
					NaviLatLng naviLatLang = location.getCoord();
					mGPSMarker.setPosition(new LatLng(
							naviLatLang.getLatitude(), naviLatLang
									.getLongitude()));
					dissmissGPSProgressDialog();
					if (mIsStart) {
						calculateRoute();
						mIsStart = false;
					}
				}

				@Override
				public void onInitNaviSuccess() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onInitNaviFailure() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGetNavigationText(int arg0, String arg1) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onEndEmulatorNavi() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onCalculateRouteSuccess() {
					dissmissProgressDialog();
					switch (mNaviMethod) {
					case ROUTE_METHOD:
						Intent intent = new Intent(NaviStartActivity.this,
								NaviRouteActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						startActivity(intent);

						break;
					case NAVI_METHOD:
						Intent standIntent = new Intent(NaviStartActivity.this,
								NaviEmulatorActivity.class);
						standIntent
								.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
						startActivity(standIntent);

						break;
					}
				}

				@Override
				public void onCalculateRouteFailure(int arg0) {
					dissmissProgressDialog();
					showToast("路径规划出错");
				}

				@Override
				public void onArrivedWayPoint(int arg0) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onArriveDestination() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onGpsOpenStatus(boolean arg0) {
					// TODO Auto-generated method stub

				}
			};
		}
		return mAmapNaviListener;
	}

	/**
	 * 返回键处理事件
	 * */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
		//	MainApplication.getInstance().exit(); 
			Intent intent = new android.content.Intent(NaviStartActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
			
		}
		return super.onKeyDown(keyCode, event);
	}

	// ---------------UI操作----------------
	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	private void setTextDescription(TextView view, String description) {
		view.setText(description);
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (mProgressDialog == null)
			mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setMessage("线路规划中");
		mProgressDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
	}

	/**
	 * 显示GPS进度框
	 */
	private void showGPSProgressDialog() {
		if (mGPSProgressDialog == null)
			mGPSProgressDialog = new ProgressDialog(this);
		mGPSProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mGPSProgressDialog.setIndeterminate(false);
		mGPSProgressDialog.setCancelable(true);
		mGPSProgressDialog.setMessage("GPS定位中");
		mGPSProgressDialog.show();
	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissGPSProgressDialog() {
		if (mGPSProgressDialog != null) {
			mGPSProgressDialog.dismiss();
		}
	}

	

	// -------------生命周期必须重写方法----------------
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}
	@Override
	public void onResume() {
		super.onResume();
		mMapView.onResume();
		// 以上两句必须重写
		// 以下两句逻辑是为了保证进入首页开启定位和加入导航回调
		AMapNavi.getInstance(this).setAMapNaviListener(getAMapNaviListener());
		mAmapNavi.startGPS();
	}

	@Override
	public void onPause() {
		super.onPause();
		mMapView.onPause();
		// 以上两句必须重写
		// 下边逻辑是移除监听
		AMapNavi.getInstance(this)
				.removeAMapNaviListener(getAMapNaviListener());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		// 以上逻辑是必须重写的
		// 这是最后退出页，所以销毁导航和播报资源
		AMapNavi.getInstance(this).destroy();// 销毁导航
		TTSController.getInstance(this).stopSpeaking();// 停止播报
		TTSController.getInstance(this).destroy();// 销毁播报模块

	}
	


}
