package com.reactnativenavigation.core;

import android.support.annotation.Nullable;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.reactnativenavigation.core.objects.Screen;
import com.reactnativenavigation.utils.ContextProvider;

import static com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;

public class RctManager {
    private static final String KEY_EVENT_ID = "id";
    private static RctManager sInstance;

    private static ReactInstanceManager reactInstanceManager;

    private RctManager() {
        // Singleton
    }

    public static synchronized RctManager getInstance() {
        if (sInstance == null) {
            sInstance = new RctManager();
        }
        return sInstance;
    }

    public static void destroy() {
        if (sInstance == null) return;

        sInstance = null;
    }

    @Nullable
    public static ReactInstanceManager getReactInstanceManager() {
        return reactInstanceManager;
    }

    public static void setReactInstanceManager(ReactInstanceManager instanceManager) {
        reactInstanceManager = instanceManager;
    }

    public boolean isInitialized() {
        return reactInstanceManager != null
                && reactInstanceManager.getCurrentReactContext() != null
                && ContextProvider.getActivityContext() != null;
    }

    public void sendEvent(String eventName, Screen screen, WritableMap params) {
        RCTDeviceEventEmitter eventEmitter = getEventEmitter();
        if (eventEmitter == null) {
            return;
        }

        params.putString(KEY_EVENT_ID, eventName);
        params.putString(Screen.KEY_NAVIGATOR_EVENT_ID, screen.navigatorEventId);
        eventEmitter.emit(screen.navigatorEventId, params);
    }

    public void sendNativeEvent(String eventName, WritableMap params) {
        RCTDeviceEventEmitter eventEmitter = getEventEmitter();
        if (eventEmitter == null) {
            return;
        }
        eventEmitter.emit(eventName, params);
    }

    private RCTDeviceEventEmitter getEventEmitter() {
        if (reactInstanceManager == null) {
            return null;
        }

        ReactContext currentReactContext = reactInstanceManager.getCurrentReactContext();
        if (currentReactContext == null) {
            return null;
        }

        return currentReactContext.getJSModule(RCTDeviceEventEmitter.class);
    }
}

