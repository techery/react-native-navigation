package com.reactnativenavigation.views;

import android.os.Bundle;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.reactnativenavigation.activities.BaseReactActivity;
import com.reactnativenavigation.core.objects.Screen;

public class RctView extends FrameLayout {

    private ReactRootView mReactRootView;

    /**
     * Interface used to run some code when the {@link ReactRootView} is visible.
     */
    public interface OnDisplayedListener {
        /**
         * This method will be invoked when the {@link ReactRootView} is visible.
         */
        public void onDisplayed();
    }

    public ReactRootView getReactRootView() {
        return mReactRootView;
    }

    public RctView(BaseReactActivity ctx, ReactInstanceManager rctInstanceManager, Screen screen) {
        this(ctx, rctInstanceManager, screen, null);
    }

    public RctView(BaseReactActivity ctx, ReactInstanceManager rctInstanceManager, Screen screen,
                   final OnDisplayedListener onDisplayedListener) {
        this(ctx, rctInstanceManager, screen, onDisplayedListener, null);

    }

    public RctView(BaseReactActivity ctx, ReactInstanceManager rctInstanceManager, Screen screen,
                   final OnDisplayedListener onDisplayedListener, Bundle passProps) {
        super(ctx);
        setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        mReactRootView = new ReactRootView(ctx);
        mReactRootView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        String componentName = screen.screenId;
        Bundle passPropsBundle = new Bundle();

        if (passProps != null) {
            passPropsBundle.putAll(passProps);
        }

        passPropsBundle.putString(Screen.KEY_SCREEN_INSTANCE_ID, screen.screenInstanceId);
        passPropsBundle.putString(Screen.KEY_NAVIGATOR_ID, screen.navigatorId);
        passPropsBundle.putString(Screen.KEY_NAVIGATOR_EVENT_ID, screen.navigatorEventId);

        mReactRootView.startReactApplication(rctInstanceManager, componentName, passPropsBundle);

        if (onDisplayedListener != null)

        {
            mReactRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    onDisplayedListener.onDisplayed();
                    mReactRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        addView(mReactRootView);
    }
}
