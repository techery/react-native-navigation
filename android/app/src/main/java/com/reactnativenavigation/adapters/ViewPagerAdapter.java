package com.reactnativenavigation.adapters;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.reactnativenavigation.activities.BaseReactActivity;
import com.reactnativenavigation.core.RctManager;
import com.reactnativenavigation.core.objects.Screen;
import com.reactnativenavigation.views.ScreenStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewPagerAdapter extends PagerAdapter implements TabLayout.OnTabSelectedListener, ViewPager.OnPageChangeListener {

    private static final String EVENT_ON_TAB_SELECTED = "OnTabSelected";

    private ViewPager mViewPager;
    private final ArrayList<ScreenStack> mScreenStacks;
    private final ArrayList<String> mNavigatorIds;
    private final Map<String, ScreenStack> mStackByNavigatorId;
    private int mCurrentPage = 0;

    public ViewPagerAdapter(BaseReactActivity context, ViewPager viewPager,
                            ArrayList<Screen> screens) {
        mViewPager = viewPager;
        mScreenStacks = new ArrayList<>();
        mNavigatorIds = new ArrayList<>();
        mStackByNavigatorId = new HashMap<>();
        for (Screen screen : screens) {
            ScreenStack stack = new ScreenStack(context);
            stack.push(screen);
            mScreenStacks.add(stack);
            mNavigatorIds.add(screen.navigatorId);
            mStackByNavigatorId.put(screen.navigatorId, stack);
        }
    }

    public void push(Screen screen, Bundle passProps) {
        ScreenStack stack = mStackByNavigatorId.get(screen.navigatorId);
        Screen prevScreen = mScreenStacks.get(mCurrentPage).peek();
        stack.push(screen, null, passProps);
    }

    public Screen pop(String navigatorId) {
        ScreenStack stack = mStackByNavigatorId.get(navigatorId);
        return stack != null ? stack.pop() : null;
    }

    public Screen peek(String navigatorId) {
        ScreenStack stack = mStackByNavigatorId.get(navigatorId);
        return stack != null ? stack.peek() : null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ScreenStack view = mScreenStacks.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object view) {
        container.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mScreenStacks.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        // Set the viewPager's current item
        int position = tab.getPosition();
        mViewPager.setCurrentItem(position);

        // Send tab selected event
        WritableMap params = Arguments.createMap();
        Screen screen = mScreenStacks.get(position).peek();
        RctManager.getInstance().sendEvent(EVENT_ON_TAB_SELECTED, screen, params);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public String getNavigatorId(int position) {
        return mNavigatorIds.get(position);
    }

    public int getStackSizeForNavigatorId(String activeNavigatorID) {
        return mStackByNavigatorId.get(activeNavigatorID).getStackSize();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public void unmountViews() {
        if (mScreenStacks != null && !mScreenStacks.isEmpty()) {
            for (ScreenStack screenStack : mScreenStacks) {
                screenStack.unmountView();
            }
        }
    }
}
