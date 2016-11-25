package com.reactnativenavigation.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.reactnativenavigation.controllers.ModalController;
import com.reactnativenavigation.core.RctManager;
import com.reactnativenavigation.core.objects.Screen;
import com.reactnativenavigation.modal.RnnModal;
import com.reactnativenavigation.utils.ContextProvider;

import javax.annotation.Nullable;

/**
 * Base Activity for React Native applications.
 */
public abstract class BaseReactActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler {
    private static final String TAG = "BaseReactActivity";

    private boolean mDoRefresh = false;

    private Menu mMenu;
    private Handler navigationHandler;
    private static int themeResId = -1;
    private static int defaultThemeResId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (themeResId != -1) {
            setTheme(themeResId);
        }
        super.onCreate(savedInstanceState);
        navigationHandler = new Handler(Looper.getMainLooper());

        if (savedInstanceState == null) {
            handleOnCreate();
        }
    }

    protected void handleOnCreate() {
        ReactRootView mReactRootView = createRootView();
        mReactRootView.startReactApplication(RctManager.getReactInstanceManager(), getMainComponentName(), getLaunchOptions());
        setContentView(mReactRootView);
    }

    @Override
    protected void onResume() {
        if (defaultThemeResId != -1) {
            setTheme(defaultThemeResId);
        }
        super.onResume();
        ContextProvider.setActivityContext(this);

        ReactInstanceManager reactInstanceManager = RctManager.getReactInstanceManager();
        if (reactInstanceManager != null) {
            reactInstanceManager.onHostResume(this, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ReactInstanceManager reactInstanceManager = RctManager.getReactInstanceManager();
        if (reactInstanceManager != null) {
            reactInstanceManager.onHostPause();
        }

        ContextProvider.clearActivityContext();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ModalController.getInstance().dismissAllModals();
        navigationHandler.removeCallbacksAndMessages(null);
        navigationHandler = null;

        BaseReactActivity activity = ContextProvider.getActivityContext();
        ReactInstanceManager reactInstanceManager = RctManager.getReactInstanceManager();

        if (reactInstanceManager != null && (activity == null || activity.isFinishing())) {
            Log.i(TAG, "Destroying ReactInstanceManager");
            reactInstanceManager.destroy();
            RctManager.destroy();
        } else {
            Log.d(TAG, "Not destroying ReactInstanceManager");
        }
    }

    public void postNavigationRunnable(Runnable runnable) {
        if (navigationHandler == null) {
            return;
        }
        navigationHandler.post(runnable);
    }

    /**
     * Returns the name of the bundle in assets. If this is null, and no file path is specified for
     * the bundle, the app will only work with {@code getUseDeveloperSupport} enabled and will
     * always try to load the JS bundle from the packager server.
     * e.g. "index.android.bundle"
     */
    @Nullable
    public String getBundleAssetName() {
        return "index.android.bundle";
    }

    /**
     * Returns a custom path of the bundle file. This is used in cases the bundle should be loaded
     * from a custom path. By default it is loaded from Android assets, from a path specified
     * by {getBundleAssetName}.
     * e.g. "file://sdcard/myapp_cache/index.android.bundle"
     */
    @Nullable
    public String getJSBundleFile() {
        return null;
    }

    /**
     * Returns the name of the main module. Determines the URL used to fetch the JS bundle
     * from the packager server. It is only used when dev support is enabled.
     * This is the first file to be executed once the {@link ReactInstanceManager} is created.
     * e.g. "index.android"
     */
    public String getJSMainModuleName() {
        return "index.android";
    }

    /**
     * Returns the launchOptions which will be passed to the {@link ReactInstanceManager}
     * when the application is started. By default, this will return null and an empty
     * object will be passed to your top level component as its initial props.
     * If your React Native application requires props set outside of JS, override
     * this method to return the Android.os.Bundle of your desired initial props.
     */
    @Nullable
    protected Bundle getLaunchOptions() {
        return null;
    }

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     * e.g. "MoviesApp"
     */
    protected String getMainComponentName() {
        return "";
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Intent intent = new Intent("onConfigurationChanged");
        intent.putExtra("newConfig", newConfig);
        this.sendBroadcast(intent);
    }

    /**
     * A subclass may override this method if it needs to use a custom {@link ReactRootView}.
     */
    protected ReactRootView createRootView() {
        return new ReactRootView(this);
    }

    @CallSuper
    public void push(Screen screen) {
    }

    @CallSuper
    public Screen pop(String navigatorId) {
        return null;
    }

    protected abstract String getCurrentNavigatorId();

    protected abstract Screen getCurrentScreen();

    public abstract int getScreenStackSize();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public Menu getMenu() {
        return mMenu;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ReactInstanceManager reactInstanceManager = RctManager.getReactInstanceManager();

        if (reactInstanceManager != null) {
            reactInstanceManager.onActivityResult(this, requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        ReactInstanceManager reactInstanceManager = RctManager.getReactInstanceManager();

        if (reactInstanceManager != null &&
                reactInstanceManager.getDevSupportManager().getDevSupportEnabled()) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                reactInstanceManager.showDevOptionsDialog();
                return true;
            }
            if (keyCode == KeyEvent.KEYCODE_R && !(getCurrentFocus() instanceof EditText)) {
                // Enable double-tap-R-to-reload
                if (mDoRefresh) {
                    reactInstanceManager.getDevSupportManager().handleReloadJS();
                    mDoRefresh = false;
                } else {
                    mDoRefresh = true;
                    new Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    mDoRefresh = false;
                                }
                            },
                            200);
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        ReactInstanceManager reactInstanceManager = RctManager.getReactInstanceManager();

        if (reactInstanceManager != null) {
            reactInstanceManager.onBackPressed();
        } else if (getScreenStackSize() > 1) {
            pop(getCurrentNavigatorId());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        ModalController modalController = ModalController.getInstance();
        RnnModal modal = modalController.get();
        if (modal != null) {
            if (modal.getScreenStackSize() > 1) {
                modal.pop();
            } else {
                modalController.dismissModal();
            }
        } else if (getScreenStackSize() > 1) {
            pop(getCurrentNavigatorId());
        } else {
            super.onBackPressed();
        }
    }

    public static void setThemeResId(int themeResId) {
        BaseReactActivity.themeResId = themeResId;
    }

    public static void setDefaultThemeResId(int defaultThemeResId) {
        BaseReactActivity.defaultThemeResId = defaultThemeResId;
    }
}
