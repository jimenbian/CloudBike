package com.example.bstation;

import java.io.ByteArrayInputStream;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amapv2.apis.util.AMapUtil;
import com.amapv2.apis.util.ToastUtil;
import com.amap.navi.*;
import com.amap.navi.demo.activity.NaviStartActivity;
import com.example.test.*;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;




@SuppressLint("NewApi")
public class MainActivity extends Activity{
    private Button bt;
    private dbHelper db;
	private Cursor myCursor;
	private String lalti;//传输到navi的坐标
	private ListView myListView;
	private int _id;
	private String[] actions = new String[] { "设置", "关于我们", "应用介绍" };
    private long firstTime;
    private AMapLocation location;
    private LocationManagerProxy aMapLocManager;
    private AMapLocation aMapLocation;// 用于判断定位超时
    private String desc=null;
    private Handler handler = new Handler();
    private SplitLag sl=new SplitLag();//分割出经度纬度
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/*  */
		

		
		bt=new Button(getBaseContext());
		bt=(Button)findViewById(R.id.button1);
		bt.setOnClickListener(new android.view.View.OnClickListener() {
  
			@Override
			public void onClick(android.view.View arg0) {
				// TODO Auto-generated method stub
				
				Intent intent = new android.content.Intent(MainActivity.this,PartActivity.class);
                 startActivity(intent);
                 finish();
			}
         }
          );
		
		myListView=(ListView)findViewById(R.id.listView1);
		
		db=new dbHelper(MainActivity.this);
        myCursor=db.select();
          
        
        MySimpleCursorAdapter adpater=new MySimpleCursorAdapter(this
        		, R.layout.test, myCursor,
        		new String[]{dbHelper.FIELD_TITLE,dbHelper.FIELD_AD},
        		new int[]{R.id.title,R.id.info});
 
        
        myListView.setAdapter(adpater);
	    
        myListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				myCursor.moveToPosition(arg2);
				_id=myCursor.getInt(0);
				ImageView img = new ImageView(MainActivity.this);
				//img.setImageResource(R.drawable.ic_launcher);
				ByteArrayInputStream stream = new ByteArrayInputStream( 
						myCursor.getBlob(2)); 
				
				img.setImageDrawable(Drawable.createFromStream(stream, "img")); 
				android.content.DialogInterface.OnClickListener  listener= new android.content.DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						db.delete(_id);
				    	myCursor.requery();
				    	myListView.invalidateViews();
					}
					
				};
				android.content.DialogInterface.OnClickListener  NaviListener= new android.content.DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						 Intent mainIntent = new Intent(MainActivity.this,NaviStartActivity.class); 
						 
						 Bundle bundle = new Bundle(); 
//						  
						 double[] lg;
						 lg=sl.getLag(lalti);
						 double b=116.430947;
						 double a=39.991927;
						 bundle.putDouble("ati", lg[1]);	
						 bundle.putDouble("log", lg[0]);						
                          mainIntent.putExtras(bundle); 
						  
						 startActivity(mainIntent);
			             finish();
					}
					
				};
				
//				android.content.DialogInterface.OnClickListener  listener1= new android.content.DialogInterface.OnClickListener(){
//
//					@Override
//					public void onClick(DialogInterface arg0, int arg1) {
//						// TODO Auto-generated method stub
//						ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
//						// 设置进度条风格，风格为圆形，旋转的
//						progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//						// 设置ProgressDialog 标题
//						progressDialog.setTitle("追踪单车");
//						// 设置ProgressDialog 提示信息
//						progressDialog.setMessage("如果您的爱车在附近区域，我会震动身躯提示您哦！");
//						// 设置ProgressDialog 标题图标
//						//progressDialog.setIcon(R.drawable.a);
//						// 设置ProgressDialog 的进度条是否不明确
//						progressDialog.setIndeterminate(false);			
//						// 设置ProgressDialog 是否可以按退回按键取消
//						progressDialog.setCancelable(true);			
//						Message af=null;
//						//设置ProgressDialog 的一个Button
//						progressDialog.setButton("确定", af);
//						// 让ProgressDialog显示
//						progressDialog.show();
//						
////						new Thread() 
////						{
////							public void run()
////							{
////								while(true)
////								{
////										
////									
////							
////								try {  
////									ToastUtil.show(MainActivity.this, desc);
////				                    Thread.sleep(1000);  
////				                } catch (InterruptedException e) {  
////				                    e.printStackTrace();  
////				                }  
////								}
////					}}.start();	
//					}
//					
//				};

				lalti=myCursor.getString(3);
				new AlertDialog.Builder(MainActivity.this)
				.setTitle(myCursor.getString(1)).setView(img).setMessage(myCursor.getString(3))
				
				.setPositiveButton("删除",listener).setNeutralButton("寻车导航", NaviListener).setNegativeButton("确定", null)
				.show();
				
				//myEditText.setText(myCursor.getString(1));
		
			}
		});
      
        
        /*
         * action bar
         */
        
     	ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				getBaseContext(),
				android.R.layout.simple_spinner_dropdown_item, actions);
     	ActionBar actionBar = getActionBar();
     	
     	actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("blue")));
		
     	/** Enabling dropdown list navigation for the action bar */
		
     	getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		
		OnNavigationListener navigationListener = new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
//				Toast.makeText(getBaseContext(),
//						"You selected : " + actions[itemPosition],
//						Toast.LENGTH_SHORT).show();
				switch(itemPosition)
				{case 0:Toast.makeText(getBaseContext(),
						"请开启GPS和WLAN",
						Toast.LENGTH_SHORT).show();
				break;
				case 1:	new AlertDialog.Builder(MainActivity.this)
				.setTitle("ABOUT US").setMessage("蓟门边工作室出品"+"\n"+"首款自行车停车位置记录软件"+"\n"+"email:zixiyaoren@gmail.com").setNegativeButton("确定", null)
				.show();
				break;
				case 2:	new AlertDialog.Builder(MainActivity.this)
				.setTitle("INSTRUCTION").setMessage("请确保联网及GPS可用"+"\n"+"点击按钮自动定位，更可添加图片").setNegativeButton("确定", null)
				.show();
				break;
					}
				return false;
			  
			}
		};

		/**
		 * Setting dropdown items and item navigation listener for the actionbar
		 */
		getActionBar().setListNavigationCallbacks(adapter, navigationListener);
		TextView emptyView = new TextView(MainActivity.this);  
        emptyView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));  
        emptyView.setText("还没有记录，请点击按钮新建记录");  
        emptyView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        emptyView.setVisibility(View.GONE);  
        ((ViewGroup)myListView.getParent()).addView(emptyView);  
        myListView.setEmptyView(emptyView);
	}
	public class MySimpleCursorAdapter extends SimpleCursorAdapter {
		private Cursor _cursor;
		private Context _context;
		public MySimpleCursorAdapter(Context context, int layout, Cursor c,
				String[] from, int[] to) {
			super(context, layout, c, from, to);
			_cursor = c;
			_context = context;
		}
		public void bindView(View view, Context context, Cursor cursor) {
			ImageView imageView = (ImageView) view.findViewById(R.id.img);
			if(_cursor.getBlob(2)!=null)
			{ByteArrayInputStream stream = new ByteArrayInputStream( 
					_cursor.getBlob(2)); 
			
			imageView.setImageDrawable(Drawable.createFromStream(stream, "img")); 
			}
			else{
				imageView.setImageResource(R.drawable.ic_launcher);
			}
			super.bindView(view, context, cursor);
		}
	}
	@Override 
    public boolean onKeyUp(int keyCode, KeyEvent event) { 
        if (keyCode == KeyEvent.KEYCODE_BACK) { 
            long secondTime = System.currentTimeMillis(); 
            
			if (secondTime - firstTime > 2000) {//如果两次按键时间间隔大于800毫秒，则不退出 
                Toast.makeText(MainActivity.this, "再按一次退出程序...", 
                        Toast.LENGTH_SHORT).show(); 
                firstTime = secondTime;//更新firstTime 
                return true; 
            } else { 
            	    finish();
                System.exit(0);//否则退出程序 
            } 
        } 
        return super.onKeyUp(keyCode, event); 
    }



}
