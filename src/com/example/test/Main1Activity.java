package com.example.test;

import com.example.bstation.R;
import com.example.bstation.R.layout;
import com.example.bstation.R.menu;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Main1Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main1, menu);
		return true;
	}

}
