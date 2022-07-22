package com.oempoc;

import android.app.Application;
import android.content.Context;

import com.atom.core.exceptions.AtomException;
import com.atom.core.models.Country;
import com.atom.core.models.Protocol;
import com.atom.sdk.android.ConnectionDetails;
import com.atom.sdk.android.VPNCredentials;
import com.atom.sdk.android.VPNProperties;
import com.atom.sdk.android.VPNStateListener;
import com.atom.sdk.android.data.callbacks.CollectionCallback;
import com.facebook.react.PackageList;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.config.ReactFeatureFlags;
import com.facebook.soloader.SoLoader;
import com.oempoc.newarchitecture.MainApplicationReactNativeHost;


import android.graphics.Color;
import android.os.Handler;
import android.os.StrictMode;
import android.text.TextUtils;
import android.widget.Toast;



import com.atom.core.exceptions.AtomValidationException;
import com.atom.core.models.AtomConfiguration;
import com.atom.core.models.AtomNotification;
import com.atom.sdk.android.AtomManager;
import android.util.Log;

import java.io.Console;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainApplication extends Application implements ReactApplication {

    public static Country countryName;
    public static Protocol protocolName;


    //Atom Variables - Start
    private static MainApplication mInstance;
    public static AtomManager atomManager;


    private final int NOTIFICATION_ID = com.atom.sdk.android.common.Constants.Notification.DEFAULT_ID;

    public static MainApplication getInstance() {
        return mInstance;
    }

    public AtomManager getAtomManager() {
        return atomManager;
    }

    public void setAtomManager(AtomManager atomManager) {
        this.atomManager = atomManager;
    }

    //Atom Variables - End

    private final ReactNativeHost mReactNativeHost =
            new ReactNativeHost(this) {
                @Override
                public boolean getUseDeveloperSupport() {
                    return BuildConfig.DEBUG;
                }

                @Override
                protected List<ReactPackage> getPackages() {
                    @SuppressWarnings("UnnecessaryLocalVariable")
                    List<ReactPackage> packages = new PackageList(this).getPackages();
                    // Packages that cannot be autolinked yet can be added manually here, for example:
                     packages.add(new AtomSdkModulePackage());
                    return packages;
                }

                @Override
                protected String getJSMainModuleName() {
                    return "index";
                }
            };

    private final ReactNativeHost mNewArchitectureNativeHost =
            new MainApplicationReactNativeHost(this);

    @Override
    public ReactNativeHost getReactNativeHost() {
        if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
            return mNewArchitectureNativeHost;
        } else {
            return mReactNativeHost;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // If you opted-in for the New Architecture, we enable the TurboModule system
        ReactFeatureFlags.useTurboModules = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED;
        SoLoader.init(this, /* native exopackage */ false);
        initializeFlipper(this, getReactNativeHost().getReactInstanceManager());


        // configure the ATOM SDK
        AtomConfiguration.Builder atomConfigurationBuilder = new AtomConfiguration.Builder("9f9ff40e2ef54956465205aee3fe4d2358134db6");
        atomConfigurationBuilder.setVpnInterfaceName("Atom SDK Demo");
        AtomNotification.Builder atomNotificationBuilder = new AtomNotification.Builder(NOTIFICATION_ID, "Atom SDK Demo", "You are now secured with Atom", R.drawable.ic_stat_icn_connected, Color.BLUE);
        atomConfigurationBuilder.setNotification(atomNotificationBuilder.build());
        AtomConfiguration atomConfiguration = atomConfigurationBuilder.build();

        try {
            AtomManager.initialize(this, atomConfiguration, new AtomManager.InitializeCallback() {
                @Override
                public void onInitialized(AtomManager mAtomManager) {
                    atomManager = mAtomManager;
                }
            });
        } catch (AtomValidationException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Loads Flipper in React Native templates. Call this in the onCreate method with something like
     * initializeFlipper(this, getReactNativeHost().getReactInstanceManager());
     *
     * @param context
     * @param reactInstanceManager
     */
    private static void initializeFlipper(
            Context context, ReactInstanceManager reactInstanceManager) {
        if (BuildConfig.DEBUG) {
            try {
        /*
         We use reflection here to pick up the class that initializes Flipper,
        since Flipper library is not available in release mode
        */
                Class<?> aClass = Class.forName("com.oempoc.ReactNativeFlipper");
                aClass
                        .getMethod("initializeFlipper", Context.class, ReactInstanceManager.class)
                        .invoke(null, context, reactInstanceManager);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

}
