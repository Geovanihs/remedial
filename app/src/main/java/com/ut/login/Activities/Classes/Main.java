package com.ut.login.Activities.Classes;

import android.app.Application;
import android.util.Log;

import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class Main extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try{
            Map<String,Object> config = new HashMap<>();
            config.put("cloud_name","dr50bobnw");
            MediaManager.init(this,config);
        }
        catch (Exception e){
            Log.e("Main",e.getMessage());
        }
    }
}
