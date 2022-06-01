package com.helloworld;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.atom.core.exceptions.AtomException;
import com.atom.core.exceptions.AtomValidationException;
import com.atom.core.models.AtomConfiguration;
import com.atom.core.models.Country;
import com.atom.core.models.Protocol;
import com.atom.sdk.android.AtomManager;
import com.atom.sdk.android.ConnectionDetails;
import com.atom.sdk.android.VPNCredentials;
import com.atom.sdk.android.VPNProperties;
import com.atom.sdk.android.VPNStateListener;
import com.atom.sdk.android.data.callbacks.CollectionCallback;
import com.facebook.react.ReactActivity;
import com.facebook.react.ReactActivityDelegate;
import com.facebook.react.ReactRootView;

import java.util.List;

public class MainActivity extends ReactActivity implements VPNStateListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

//    AtomManager.getInstance().getVPNServicePermission(this);


    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        AtomManager.addVPNStateListener(MainActivity.this);
        MainApplication.atomManager.getCountries(new CollectionCallback<Country>() {

          //                     @Override
          public void onSuccess(List<Country> countries) {
            // Printing the values
            MainApplication.countryName = countries.get(0);

            Log.d("CountryName", MainApplication.countryName.getName());
            MainApplication.atomManager.getProtocols(new CollectionCallback<Protocol>() {

              @Override
              public void onSuccess(List<Protocol> protocols) {

                // Printing the values
                MainApplication.protocolName = protocols.get(2);
//                Log.d("Protocol", protocols.get(0).getName());
//                Log.d("Protocol", protocols.get(1).getName());
//                Log.d("Protocol", protocols.get(2).getName());
//                Log.d("Protocol", protocols.get(3).getName());
//
//                Log.d("Protocols", protocols.toString());


                Log.d("countryName", MainApplication.countryName.getCountry());
                Log.d("protocolName", MainApplication.protocolName.getProtocol());
                VPNProperties.Builder vpnPropertiesBuilder = null;
                try {
                  vpnPropertiesBuilder = new VPNProperties.Builder(MainApplication.countryName, MainApplication.protocolName);

                  MainApplication.atomManager.setVPNCredentials(new VPNCredentials("basit@gaditek.com", "Qwertyuiop"));

                  MainApplication.atomManager.connect(MainActivity.this, vpnPropertiesBuilder.build());
                } catch (AtomValidationException e) {
                  Log.d("e", String.valueOf(e));
                  e.printStackTrace();
                }
              }



              @Override
              public void onError(AtomException exception) {
                Log.d("ProtocolError", exception.getErrorMessage());
              }

              @Override
              public void onNetworkError(AtomException exception) {
                Log.d("ProtocolNetworkError", exception.getErrorMessage());

              }
            });

          }

          //                     @Override
          public void onError(AtomException exception) {
            Log.d("CountryError", exception.getErrorMessage());
          }

          //                     @Override
          public void onNetworkError(AtomException exception) {
            Log.d("NetworkError", exception.getErrorMessage());
          }
        });
      }
    }, 5000);
  }

  /**
   * Returns the name of the main component registered from JavaScript. This is used to schedule
   * rendering of the component.
   */
  @Override
  protected String getMainComponentName() {
    return "helloworld";
  }

  /**
   * Returns the instance of the {@link ReactActivityDelegate}. There the RootView is created and
   * you can specify the rendered you wish to use (Fabric or the older renderer).
   */
  @Override
  protected ReactActivityDelegate createReactActivityDelegate() {
    return new MainActivityDelegate(this, getMainComponentName());
  }

  @Override
  public void onConnected() {
      Log.d("onConnected", "onConnected");
  }

  @Override
  public void onConnected(ConnectionDetails connectionDetails) {
    Log.d("onConnected", "onConnected");
  }

  @Override
  public void onConnecting() {
    Log.d("onConnecting", "onConnecting");
  }

  @Override
  public void onConnecting(VPNProperties vpnProperties, AtomConfiguration atomConfiguration) {
    Log.d("onConnecting", "onConnecting");

  }

  @Override
  public void onRedialing(AtomException e, ConnectionDetails connectionDetails) {
    Log.e("onRedialing", e.getErrorMessage());

  }

  @Override
  public void onDialError(AtomException e, ConnectionDetails connectionDetails) {
    Log.e("onDialError", e.getErrorMessage() + e.getCode());
    Log.e("onDialError", e.getException().getMessage() + e.getException().getLocalizedMessage());

  }

  @Override
  public void onStateChange(String s) {
    Log.d("onStateChange", s);
  }

  @Override
  public void onDisconnected(boolean b) {
    Log.d("onDisconnected", b+"");

  }

  @Override
  public void onDisconnected(ConnectionDetails connectionDetails) {
    Log.d("onDisconnected", "onDisconnected");

  }

  @Override
  public void onUnableToAccessInternet(AtomException e, ConnectionDetails connectionDetails) {
    Log.e("onUnableToAccess", e.getErrorMessage());

  }

  @Override
  public void onPacketsTransmitted(String s, String s1, String s2, String s3) {
    Log.d("onDisconnected",s+s1+s2+s3);

  }

  public static class MainActivityDelegate extends ReactActivityDelegate {
    public MainActivityDelegate(ReactActivity activity, String mainComponentName) {
      super(activity, mainComponentName);
    }

    @Override
    protected ReactRootView createRootView() {
      ReactRootView reactRootView = new ReactRootView(getContext());
      // If you opted-in for the New Architecture, we enable the Fabric Renderer.
      reactRootView.setIsFabric(BuildConfig.IS_NEW_ARCHITECTURE_ENABLED);
      return reactRootView;
    }
  }
}
