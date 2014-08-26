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
 * AMapV2��ͼ�м򵥽��ܻ�϶�λ
 */
public class PartActivity extends Activity implements
		AMapLocationListener, Runnable {
	private LocationManagerProxy aMapLocManager = null;
	private TextView myLocation;
	private AMapLocation aMapLocation;// �����ж϶�λ��ʱ
	private Handler handler = new Handler();
	private Button bt;//����
	private Button pic;//��Ƭ
	private Button back;//����
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
		 * 1.0.2�汾��������������true��ʾ��϶�λ�а���gps��λ��false��ʾ�����綨λ��Ĭ����true Location
		 * API��λ����GPS�������϶�λ��ʽ
		 * ����һ�������Ƕ�λprovider���ڶ�������ʱ�������2000���룬������������������λ���ף����ĸ������Ƕ�λ������
		 */
		aMapLocManager.requestLocationUpdates(
				LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(this, 12000);// ���ó���12�뻹û�ж�λ����ֹͣ��λ
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
		stopLocation();// ֹͣ��λ
	}

	/**
	 * ���ٶ�λ
	 */
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destroy();
		}
		aMapLocManager = null;
	}

	/**
	 * �˷����Ѿ�����
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
	 * ��϶�λ�ص�����
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			largeIndeterminate.setVisibility(View.GONE);
			this.aMapLocation = location;// �жϳ�ʱ����
			Double geoLat = location.getLatitude();
			Double geoLng = location.getLongitude();
			String cityCode = "";
			
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			String str = ("��λ�ɹ�:(" + geoLng + "," + geoLat + ")"
					+ "\n��    ��    :" + location.getAccuracy() + "��"
					+ "\n��λ��ʽ:" + location.getProvider() + "\n��λʱ��:"
					+ AMapUtil.convertToTime(location.getTime())+"\nλ������:" + desc  + location.getCity()
					+ "\n��(��):" + location.getDistrict());
			myLocation.setText(str);
		}
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			largeIndeterminate.setVisibility(View.GONE);
			ToastUtil.show(this, "12���ڻ�û�ж�λ�ɹ���ֹͣ��λ");
			myLocation.setText("12���ڻ�û�ж�λ�ɹ���ֹͣ��λ");
			stopLocation();// ���ٵ���λ
		}
	}
	//Ԥ��ͼƬ
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