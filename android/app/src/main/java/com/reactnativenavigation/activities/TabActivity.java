package com.reactnativenavigation.activities;

import com.reactnativenavigation.R;
import com.reactnativenavigation.adapters.ViewPagerAdapter;
import com.reactnativenavigation.core.RctManager;
import com.reactnativenavigation.core.objects.Screen;
import com.reactnativenavigation.views.NonSwipeableViewPager;

import java.util.ArrayList;

public class TabActivity extends BaseReactActivity {
    public static final String EXTRA_SCREENS = "extraScreens";

    private NonSwipeableViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    @Override
    protected void handleOnCreate() {
        setContentView(R.layout.tab_activity);
        mViewPager = (NonSwipeableViewPager) findViewById(R.id.viewPager);

        ArrayList<Screen> screens = (ArrayList<Screen>) getIntent().getSerializableExtra(EXTRA_SCREENS);

        setupViewPager(screens);
    }

    private void setupViewPager(ArrayList<Screen> screens) {
        mAdapter = new ViewPagerAdapter(this, mViewPager, screens);
        mViewPager.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void push(Screen screen) {
        super.push(screen);
        mAdapter.push(screen, screen.passProps);
    }

    @Override
    public Screen pop(String navigatorId) {
        super.pop(navigatorId);
        return mAdapter.pop(navigatorId);
    }

    @Override
    protected Screen getCurrentScreen() {
        return mAdapter.peek(getCurrentNavigatorId());
    }

    @Override
    protected String getCurrentNavigatorId() {
        return mAdapter.getNavigatorId(mViewPager.getCurrentItem());
    }

    @Override
    public int getScreenStackSize() {
        return mAdapter.getStackSizeForNavigatorId(getCurrentNavigatorId());
    }

    public NonSwipeableViewPager getViewPager() {
        return mViewPager;
    }
}
