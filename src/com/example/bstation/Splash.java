package com.example.bstation;

import android.os.Bundle;
import android.os.Handler;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Splash extends Activity {
	private final int SPLASH_DISPLAY_LENGHT = 3000; //延迟三秒  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().requestFeature(Window.FEATURE_PROGRESS); //去标题栏 
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_start);
		
		
		 
			 new Handler().postDelayed(new Runnable(){ 
			  
	         @Override 
	         public void run() { 
	             Intent mainIntent = new Intent(Splash.this,MainActivity.class); 
	             startActivity(mainIntent); 
	             finish(); 
	         } 
	            
	        }, SPLASH_DISPLAY_LENGHT); 
		
	}


}
