package com.reactnativenavigation.activities;

import android.widget.FrameLayout;

import com.facebook.react.ReactInstanceManager;
import com.reactnativenavigation.R;
import com.reactnativenavigation.core.RctManager;
import com.reactnativenavigation.core.objects.Screen;
import com.reactnativenavigation.views.ScreenStack;

public class SingleScreenActivity extends BaseReactActivity {

    public static final String EXTRA_SCREEN = "extraScreen";

    private ScreenStack mScreenStack;
    private String mNavigatorId;

    @Override
    protected void handleOnCreate() {
        setContentView(R.layout.single_screen_activity);

        Screen screen = (Screen) getIntent().getSerializableExtra(EXTRA_SCREEN);
        mNavigatorId = screen.navigatorId;

        mScreenStack = new ScreenStack(this);
        FrameLayout contentFrame = (FrameLayout) findViewById(R.id.contentFrame);
        contentFrame.addView(mScreenStack);
        mScreenStack.push(screen);
    }

    @Override
    public void push(Screen screen) {
        super.push(screen);
        mScreenStack.push(screen, null, screen.passProps);
    }

    @Override
    public Screen pop(String navigatorId) {
        super.pop(navigatorId);
        return mScreenStack.pop();
    }

    @Override
    public String getCurrentNavigatorId() {
        return mNavigatorId;
    }

    @Override
    protected Screen getCurrentScreen() {
        return mScreenStack.peek();
    }

    @Override
    public int getScreenStackSize() {
        return mScreenStack.getStackSize();
    }
}
