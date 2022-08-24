package com.oempoc;

import android.util.Log;

import com.atom.core.exceptions.AtomException;
import com.atom.core.models.AtomConfiguration;
import com.atom.core.models.Country;
import com.atom.core.models.Protocol;

import com.atom.sdk.android.AtomManager;
import com.atom.sdk.android.ConnectionDetails;
import com.atom.sdk.android.VPNCredentials;
import com.atom.sdk.android.VPNProperties;
import com.atom.sdk.android.VPNStateListener;
import com.atom.sdk.android.data.callbacks.CollectionCallback;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class AtomSdkModule extends ReactContextBaseJavaModule implements VPNStateListener {

    private static ReactApplicationContext reactContext;

    //Callback callback;
    List<Country> countries;
    List<Protocol> protocols;

    AtomSdkModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
        AtomManager.addVPNStateListener(this);
        AtomManager.getInstance().bindIKEVStateService(reactContext);
    }

    @ReactMethod
    public void atomInitialize(Callback callback) {

        AtomManager.addVPNStateListener(this);
        AtomManager.getInstance().bindIKEVStateService(reactContext);
        MainApplication.atomManager.getCountries(new CollectionCallback<Country>() {

            @Override
            public void onError(AtomException e) {
                callback.invoke(false);
            }

            @Override
            public void onNetworkError(AtomException e) {
                callback.invoke(false);
            }

            @Override
            public void onSuccess(List<Country> list) {
                countries = list;

                MainApplication.atomManager.getProtocols(new CollectionCallback<Protocol>() {

                    @Override
                    public void onError(AtomException e) {
                        callback.invoke(false);
                    }

                    @Override
                    public void onNetworkError(AtomException e) {
                        callback.invoke(false);
                    }

                    @Override
                    public void onSuccess(List<Protocol> list) {
                        protocols = list;
                        callback.invoke(true);
                    }
                });
            }
        });

    }



    @ReactMethod
    public void getCurrentVpnStatus(Callback callback) {
        callback.invoke(AtomManager.getInstance().getCurrentVpnStatus(reactContext));
    }

    @ReactMethod
    public void connectVPN(Callback callback) {
        try {
            callback.invoke("Connection Starting");
            VPNProperties.Builder vpnPropertiesBuilder = new VPNProperties.Builder(countries.get(1), protocols.get(0));
            MainApplication.atomManager.setVPNCredentials(new VPNCredentials("basit@gaditek.com", "Qwertyuiop"));
            MainApplication.atomManager.connect(reactContext, vpnPropertiesBuilder.build());
        } catch (Exception ex) {
            Log.d("BBBBBBB", ex.getMessage());

            Map<String, Object> payload = new HashMap<String, Object>();
            payload.put("exception", ex);
            invokeEvent("onDialError", payload);
        }
    }

    @ReactMethod
    public void disconnectVPN() {
        MainApplication.atomManager.disconnect(reactContext);
    }



    @Override
    public String getName() {
        return "AtomSdkModule";
    }

    private void invokeEvent(String eventName, @Nullable Map<String, Object> payload) {
        WritableMap map = new WritableNativeMap();
        map.putString("data", new Gson().toJson(payload));

        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, map);
    }

   @Override
   public void onConnected() {   }

    @Override
    public void onConnected(ConnectionDetails connectionDetails) {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("connectionDetails", connectionDetails);

        invokeEvent("onConnected", payload);
    }

    @Override
    public void onConnecting() {
        invokeEvent("onConnecting", null);
    }

    @Override
    public void onConnecting(VPNProperties vpnProperties, AtomConfiguration atomConfiguration) {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("vpnProperties", vpnProperties);
        payload.put("atomConfiguration", atomConfiguration);

        invokeEvent("onConnecting", payload);
    }

    @Override
    public void onRedialing(AtomException e, ConnectionDetails connectionDetails) {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("e", e);
        payload.put("connectionDetails", connectionDetails);

        invokeEvent("onRedialing", payload);
    }

    @Override
    public void onDialError(AtomException e, ConnectionDetails connectionDetails) {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("e", e);
        payload.put("connectionDetails", connectionDetails);

        invokeEvent("onDialError", payload);
    }

    @Override
    public void onStateChange(String s) {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("s", s);

        invokeEvent("onStateChange", payload);
    }

    @Override
    public void onDisconnected(boolean b) {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("b", b);

        invokeEvent("onStateChange", payload);
    }

    @Override
    public void onDisconnected(ConnectionDetails connectionDetails) {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("connectionDetails", connectionDetails);

        invokeEvent("onDisconnected", payload);
    }

    @Override
    public void onUnableToAccessInternet(AtomException e, ConnectionDetails connectionDetails) {
        Map<String, Object> payload = new HashMap<String, Object>();
        payload.put("e", e);
        payload.put("onUnableToAccessInternet", connectionDetails);

        invokeEvent("onUnableToAccessInternet", payload);
    }

    @Override
    public void onPacketsTransmitted(String s, String s1, String s2, String s3) {

    }


}
