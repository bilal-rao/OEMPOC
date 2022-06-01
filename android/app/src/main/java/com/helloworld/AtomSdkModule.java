package com.helloworld;

import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

//import getCountrySlug;

public class AtomSdkModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    AtomSdkModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }


    @Override public String getName() {
        return "AtomSdkModule";
    }
    
    
    @ReactMethod
    public void getCountryName(){
//        String countryName = MainApplication.countryName;
//        Log.d("Country", countryName);
    }


}
