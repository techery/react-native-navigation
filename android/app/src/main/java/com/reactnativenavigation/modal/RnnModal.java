package com.reactnativenavigation.modal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.facebook.react.ReactInstanceManager;
import com.reactnativenavigation.R;
import com.reactnativenavigation.activities.BaseReactActivity;
import com.reactnativenavigation.controllers.ModalController;
import com.reactnativenavigation.core.objects.Screen;
import com.reactnativenavigation.utils.ContextProvider;
import com.reactnativenavigation.utils.SdkSupports;
import com.reactnativenavigation.views.RctView;
import com.reactnativenavigation.views.ScreenStack;

import java.util.Stack;

public class RnnModal extends Dialog implements DialogInterface.OnDismissListener {

    private ScreenStack mScreenStack;
    private View mContentView;
    private Screen mScreen;
    private ReactInstanceManager mReactInstanceManager;
    private Stack<Boolean> statusBarVisibilityStack = new Stack<>();
    private Stack<String> orientationStack = new Stack<>();

    public RnnModal(BaseReactActivity context, Screen screen) {
        super(context, R.style.Modal);
        mScreen = screen;
        mReactInstanceManager = context.getReactInstanceManager();
        ModalController.getInstance().add(this);
        init(context);
    }

    public int getScreenStackSize() {
        return mScreenStack.getStackSize();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("InflateParams")
    private void init(final Context context) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mContentView = LayoutInflater.from(context).inflate(R.layout.modal_layout, null, false);
        mScreenStack = (ScreenStack) mContentView.findViewById(R.id.screenStack);

        setContentView(mContentView);
        mScreenStack.push(mScreen, new RctView.OnDisplayedListener() {
            @Override
            public void onDisplayed() {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_up);
                mContentView.setAnimation(animation);
                mContentView.animate();
            }
        }, mScreen.passProps);
        pushStatusBarScreen(mScreen);
        pushOrientationScreen(mScreen);
    }

    public void push(Screen screen) {
        mScreenStack.push(screen);
        pushStatusBarScreen(screen);
    }

    private void pushStatusBarScreen(Screen screen) {
        final boolean showStatusBar = !screen.hideStatusBar;
        statusBarVisibilityStack.push(showStatusBar);
        changeStatusBarVisibility(showStatusBar);
    }

    private void pushOrientationScreen(Screen screen) {
        final String orientation = screen.orientation;
        orientationStack.push(orientation);
        lockOrientation(orientation);
    }

    public Screen pop() {
        final boolean showStatusBar = statusBarVisibilityStack.pop();
        changeStatusBarVisibility(showStatusBar);
        if (orientationStack.size() > 0) {
            final String orientation = orientationStack.pop();
            lockOrientation(orientation);
        }
        return mScreenStack.pop();
    }

    private void changeStatusBarVisibility(boolean showStatusBar) {
        final Window window = getWindow();
        if (showStatusBar) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            if (SdkSupports.lollipop()) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            }
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    private void lockOrientation(String orientation) {
        final Activity activity = ContextProvider.getActivityContext();

        if (activity == null) {
            return;
        }

        switch (orientation) {
            case "portrait":
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case "landscape":
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                break;
            default:
                throw new Error("Unknown orientation: " + orientation);
        }
    }

    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else if (mScreenStack.getStackSize() > 1) {
            mScreenStack.pop();
        } else {
            ModalController.getInstance().remove();
            super.onBackPressed();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        ModalController.getInstance().remove();
    }
}
