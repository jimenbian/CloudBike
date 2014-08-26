package com.example.bstation;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import com.amapv2.apis.util.AMapUtil;
import com.amapv2.apis.util.ToastUtil;




/**
 * AMapV2地图中简单介绍混合定位
 */
public class PartActivity extends Activity implements
		AMapLocationListener, Runnable {
	private LocationManagerProxy aMapLocManager = null;
	private TextView myLocation;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler handler = new Handler();
	private Button bt;//保存
	private Button pic;//照片
	private Button back;//返回
    private dbHelper db;
    private String ad;
    private EditText title;
    private String desc;
    private String picturePath;
    private ProgressBar largeIndeterminate;
    private Bitmap bp;
    private Boolean isExit;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_part);
		ActionBar actionBar = getActionBar();
		actionBar.hide();
		largeIndeterminate = (ProgressBar)findViewById(R.id.large_indeterminate);
		largeIndeterminate.setVisibility(View.VISIBLE);
		bp= BitmapFactory.decodeResource(this.getBaseContext().getResources(), R.drawable.icon);
		myLocation = (TextView) findViewById(R.id.myLocation);
		final ByteArrayOutputStream os = new ByteArrayOutputStream();   
		
		
		title= (EditText) findViewById(R.id.et1);
		aMapLocManager = LocationManagerProxy.getInstance(this);
		/*
		 * mAMapLocManager.setGpsEnable(false);//
		 * 1.0.2版本新增方法，设置true表示混合定位中包含gps定位，false表示纯网络定位，默认是true Location
		 * API定位采用GPS和网络混合定位方式
		 * ，第一个参数是定位provider，第二个参数时间最短是2000毫秒，第三个参数距离间隔单位是米，第四个参数是定位监听者
		 */
		aMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(this, 12000);// 设置超过12秒还没有定位到就停止定位
	    db=new dbHelper(PartActivity.this);
		bt=new Button(getBaseContext());
		bt=(Button)findViewById(R.id.button1);
		
		bt.setOnClickListener(new android.view.View.OnClickListener() {
			  
			@Override
			public void onClick(android.view.View arg0) {
				// TODO Auto-generated method stub
				bp.compress(Bitmap.CompressFormat.PNG, 100, os);
				Intent intent = new android.content.Intent(PartActivity.this,MainActivity.class); 
				db.insert(title.getText().toString(),myLocation.getText().toString(),desc,os); 
	      		startActivity(intent);
                finish();
			}
         }
          );
		
		pic=new Button(getBaseContext());
		pic=(Button)findViewById(R.id.button2);
		
		pic.setOnClickListener(new android.view.View.OnClickListener() {
			  
			@Override
			public void onClick(android.view.View arg0) {
				// TODO Auto-generated method stub
				 
				Intent i = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				
				startActivityForResult(i, 1);
			}
         }
          );
		
		back=new Button(getBaseContext());
		back=(Button)findViewById(R.id.button3);
		
		back.setOnClickListener(new android.view.View.OnClickListener() {
			  
			@Override
			public void onClick(android.view.View arg0) {
				// TODO Auto-generated method stub
				 
				Intent intent = new android.content.Intent(PartActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
			}
         }
          );
		
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocation();// 停止定位
	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destroy();
		}
		aMapLocManager = null;
	}

	/**
	 * 此方法已经废弃
	 */
	@Override
	public void onLocationChanged(Location location) {
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	/**
	 * 混合定位回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			largeIndeterminate.setVisibility(View.GONE);
			this.aMapLocation = location;// 判断超时机制
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			String cityCode = "";
			
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			String str = ("定位成功:(" + geoLng + "," + geoLat + ")"
					+ "\n精    度    :" + location.getAccuracy() + "米"
					+ "\n定位方式:" + location.getProvider() + "\n定位时间:"
					+ AMapUtil.convertToTime(location.getTime())+"\n位置描述:" + desc  + location.getCity()
					+ "\n区(县):" + location.getDistrict());
			myLocation.setText(str);
		}
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			largeIndeterminate.setVisibility(View.GONE);
			ToastUtil.show(this, "12秒内还没有定位成功，停止定位");
			myLocation.setText("12秒内还没有定位成功，停止定位");
			stopLocation();// 销毁掉定位
		}
	}
	//预览图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
		if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			picturePath = cursor.getString(columnIndex);
			cursor.close();
			
		   ImageView imageView = (ImageView) findViewById(R.id.IV);
			
		   bp=resizeBitMapImage1(picturePath,100,100);
		   imageView.setImageBitmap(bp);
			
		
		}
    
    
    }
    public static Bitmap resizeBitMapImage1(String filePath, int targetWidth,
            int targetHeight) {
        Bitmap bitMapImage = null;
        // First, get the dimensions of the image
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        double sampleSize = 0;
        // Only scale if we need to
        // (16384 buffer for img processing)
        Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math
                .abs(options.outWidth - targetWidth);
            if (options.outHeight * options.outWidth * 2 >= 1638) {
            // Load, scaling to smallest power of 2 that'll get it <= desired
            // dimensions
            sampleSize = scaleByHeight ? options.outHeight / targetHeight
                    : options.outWidth / targetWidth;
            sampleSize = (int) Math.pow(2d,
                    Math.floor(Math.log(sampleSize) / Math.log(2d)));
        }
        // Do the actual decoding
        options.inJustDecodeBounds = false;
        options.inTempStorage = new byte[128];
        while (true) {
            try {
                options.inSampleSize = (int) sampleSize;
                bitMapImage = BitmapFactory.decodeFile(filePath, options);
                  break;
            } catch (Exception ex) {
                try {
                    sampleSize = sampleSize * 2;
                } catch (Exception ex1) {
                  }
            }
        }
        return bitMapImage;
    }
    
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			
			Intent intent = new android.content.Intent(PartActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
		    return false;
			}
		

		return super.onKeyDown(keyCode, event);
	}


}