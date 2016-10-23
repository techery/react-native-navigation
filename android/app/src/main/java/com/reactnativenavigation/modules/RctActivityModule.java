package com.reactnativenavigation.modules;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.reactnativenavigation.activities.BaseReactActivity;
import com.reactnativenavigation.activities.SingleScreenActivity;
import com.reactnativenavigation.activities.TabActivity;
import com.reactnativenavigation.controllers.ModalController;
import com.reactnativenavigation.core.objects.Screen;
import com.reactnativenavigation.modal.RnnModal;
import com.reactnativenavigation.utils.ContextProvider;
import com.reactnativenavigation.utils.MaterialDialogUtil;

import java.util.ArrayList;

public class RctActivityModule extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "RctActivity";

    public RctActivityModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void lockToPortrait() {
        final Activity currentActivity = ContextProvider.getActivityContext();
        if (currentActivity != null) {
            currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @ReactMethod
    public void lockToLandscape() {
        final Activity currentActivity = ContextProvider.getActivityContext();
        if (currentActivity != null) {
            currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @ReactMethod
    public void lockToSensorLandscape() {
        final Activity currentActivity = ContextProvider.getActivityContext();
        if (currentActivity != null) {
            currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    @ReactMethod
    public void unlockAllOrientations() {
        final Activity currentActivity = ContextProvider.getActivityContext();
        if (currentActivity != null) {
            currentActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    @ReactMethod
    public void startTabBasedApp(ReadableArray screens) {
        Activity context = ContextProvider.getActivityContext();
        if (context != null && !context.isFinishing()) {
            Intent intent = new Intent(context, TabActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            Bundle extras = new Bundle();
            extras.putSerializable(TabActivity.EXTRA_SCREENS, createScreens(screens));
            intent.putExtras(extras);

            context.startActivity(intent);
            context.overridePendingTransition(0, 0);
        }
    }

    private ArrayList<Screen> createScreens(ReadableArray screens) {
        ArrayList<Screen> ret = new ArrayList<>();
        for (int i = 0; i < screens.size(); i++) {
            ret.add(new Screen(screens.getMap(i)));
        }
        return ret;
    }

    @ReactMethod
    public void startSingleScreenApp(ReadableMap screen) {
        BaseReactActivity context = ContextProvider.getActivityContext();
        if (context != null && !context.isFinishing()) {
            Intent intent = new Intent(context, SingleScreenActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            Bundle extras = new Bundle();
            extras.putSerializable(SingleScreenActivity.EXTRA_SCREEN, new Screen(screen));
            intent.putExtras(extras);

            context.startActivity(intent);
            context.overridePendingTransition(0, 0);
        }
    }

    @ReactMethod
    public void navigatorPush(final ReadableMap skreen) {
        final Screen screen = new Screen(skreen);
        final BaseReactActivity context = ContextProvider.getActivityContext();
        if (context == null || context.isFinishing()) {
            return;
        }

        // First, check if the screen should be pushed to a Modal
        ModalController modalController = ModalController.getInstance();
        if (modalController.isModalDisplayed()) {
            final RnnModal modal = modalController.get();
            if (modal != null) {
                context.postNavigationRunnable(new Runnable() {
                    @Override
                    public void run() {
                        modal.push(screen);
                    }
                });
            }
            return;
        }

        // No Modal is displayed, Push to activity
        context.postNavigationRunnable(new Runnable() {
            @Override
            public void run() {
                context.push(screen);
            }
        });
    }

    @ReactMethod
    public void navigatorPop(final ReadableMap navigator) {
        final String navigatorId = navigator.getString("navigatorID");
        final BaseReactActivity context = ContextProvider.getActivityContext();
        if (context == null || context.isFinishing()) {
            return;
        }

        // First, check if the screen should be popped from a Modal
        ModalController modalController = ModalController.getInstance();
        if (modalController.isModalDisplayed()) {
            final RnnModal modal = modalController.get();
            if (modal != null) {
                context.postNavigationRunnable(new Runnable() {
                    @Override
                    public void run() {
                        modal.pop();
                    }
                });
            }
            return;
        }

        context.postNavigationRunnable(new Runnable() {
            @Override
            public void run() {
                context.pop(navigatorId);
            }
        });
    }

    @ReactMethod
    public void showModal(final ReadableMap screen) {
        final BaseReactActivity activity = ContextProvider.getActivityContext();
        if (activity != null && !activity.isFinishing()) {
            activity.postNavigationRunnable(new Runnable() {
                @Override
                public void run() {
                    new RnnModal(activity, new Screen(screen)).show();
                }
            });
        }
    }

    @ReactMethod
    public void dismissAllModals(final ReadableMap params) {
        final BaseReactActivity context = ContextProvider.getActivityContext();
        if (context != null && !context.isFinishing()) {
            context.postNavigationRunnable(new Runnable() {
                @Override
                public void run() {
                    ModalController modalController = ModalController.getInstance();
                    if (modalController.isModalDisplayed()) {
                        modalController.dismissAllModals();
                    }
                }
            });
        }
    }


    /**
     * Dismisses the top modal (the last modal pushed).
     */
    @ReactMethod
    public void dismissModal() {
        ModalController modalController = ModalController.getInstance();
        if (modalController.isModalDisplayed()) {
            modalController.dismissModal();
        }
    }

    @ReactMethod
    public void switchTabInPager(final int tabIndex) {
        final TabActivity activity = (TabActivity) ContextProvider.getActivityContext();
        if (activity != null && !activity.isFinishing()) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.getViewPager().setCurrentItem(tabIndex, false);
                }
            });
        }
    }

    MaterialDialog mDialog;

    @ReactMethod
    public void showMaterialDialog(final ReadableMap options, final Callback callback) {
        final Activity mActivity = ContextProvider.getActivityContext();

        if (mActivity == null) return;

        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                if (mDialog != null)
                    mDialog.dismiss();
                mDialog = MaterialDialogUtil.buildDialog(mActivity, options, callback, mDialog);
                mDialog.show();
            }
        });
    }
}
