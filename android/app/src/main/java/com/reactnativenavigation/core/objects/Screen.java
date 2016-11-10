package com.reactnativenavigation.core.objects;

import android.os.Bundle;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;

import java.io.Serializable;

public class Screen extends JsonObject implements Serializable {
    private static final long serialVersionUID = -1033475305421107791L;

    private static final String KEY_SCREEN = "screen";
    public static final String KEY_SCREEN_INSTANCE_ID = "screenInstanceID";
    public static final String KEY_NAVIGATOR_ID = "navigatorID";
    public static final String KEY_NAVIGATOR_EVENT_ID = "navigatorEventID";
    private static final String KEY_PASS_PROPS = "passProps";
    private static final String KEY_ORIENTATION = "orientation";

    public final String screenId;
    public final String screenInstanceId;
    public final String navigatorId;
    public final String navigatorEventId;
    public final String orientation;

    public Bundle passProps;

    public Screen(ReadableMap screen) {
        passProps = Arguments.toBundle(getMap(screen, KEY_PASS_PROPS));
        screenId = getString(screen, KEY_SCREEN);
        screenInstanceId = getString(screen, KEY_SCREEN_INSTANCE_ID);
        navigatorId = getString(screen, KEY_NAVIGATOR_ID);
        navigatorEventId = getString(screen, KEY_NAVIGATOR_EVENT_ID);
        orientation = getString(screen, KEY_ORIENTATION, "portrait");
    }
}
