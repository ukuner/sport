package com.example.sport;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
	private static Context context;  
    
    @Override  
    public void onCreate() {  
        //ªÒ»°Context  
    	super.onCreate();
        context = getApplicationContext();  
    }  
      
    //∑µªÿ  
    public static Context getContext(){ 
        return context;  
    }
    
}
