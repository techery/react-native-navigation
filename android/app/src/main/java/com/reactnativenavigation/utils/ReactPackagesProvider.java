package com.reactnativenavigation.utils;

import com.facebook.react.ReactPackage;

import java.util.ArrayList;
import java.util.List;

public class ReactPackagesProvider {
    private static List<ReactPackage> packageList = new ArrayList<>();

    private ReactPackagesProvider() {

    }

    public static List<ReactPackage> getPackageList() {
        return packageList;
    }

    public static void setPackageList(List<ReactPackage> packageList) {
        ReactPackagesProvider.packageList = packageList;
    }
}
