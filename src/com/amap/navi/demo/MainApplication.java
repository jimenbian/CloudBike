package com.amap.navi.demo;

import android.app.Activity;
import android.app.Application;

public class MainApplication extends Application { 

private Activity activity;
private static MainApplication instance; 

private MainApplication() 
{ 
} 
//单例模式中获取唯一的MyApplication实例 
public static MainApplication getInstance() 
{ 
if(null == instance) 
{ 
instance = new MainApplication(); 
} 
return instance; 
} 
//添加Activity到容器中 
public void addActivity(Activity activity) 
{ 
this.activity=activity;
} 
public void deleteActivity(){
	this.activity=null;
}
//遍历所有Activity并finish 
public void exit() 
{ 
 if(activity==null){
	 return;
 } 
activity.finish(); 
 
} 
} 
