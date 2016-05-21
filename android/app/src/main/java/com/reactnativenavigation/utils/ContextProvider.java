package com.reactnativenavigation.utils;

import com.reactnativenavigation.activities.BaseReactActivity;

import java.lang.ref.WeakReference;

public class ContextProvider {
    private static WeakReference<BaseReactActivity> sActivityWR;

    public static void setActivityContext(BaseReactActivity activity) {
        if (sActivityWR == null) {
            sActivityWR = new WeakReference<>(activity);
        }
    }

    /**
     * Returns the currently resumed activity or {@code null} if there is none.
     */
    public static BaseReactActivity getActivityContext() {
        return sActivityWR != null ? sActivityWR.get() : null;
    }

    public static void clearActivityContext() {
        if (sActivityWR != null) {
            sActivityWR.clear();
        }
        sActivityWR = null;
    }
}
