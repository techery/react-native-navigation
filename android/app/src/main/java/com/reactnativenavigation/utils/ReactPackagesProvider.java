package com.reactnativenavigation.utils;

import com.facebook.react.ReactPackage;

import java.util.ArrayList;
import java.util.List;

public class ReactPackagesProvider {
    private static List<ReactPackage> packageList = new ArrayList<>();
    private static List<Class<? extends ReactPackage>> packagesClassesList = new ArrayList<>();

    private ReactPackagesProvider() {

    }

    public static List<ReactPackage> getPackageList() {
        return packageList;
    }

    public static void setPackageList(List<ReactPackage> packageList) {
        ReactPackagesProvider.packageList = packageList;
    }

    public static List<Class<? extends ReactPackage>> getPackagesClassesList() {
        return packagesClassesList;
    }

    public static void setPackagesClassesList(List<Class<? extends ReactPackage>> packagesClassesList) {
        ReactPackagesProvider.packagesClassesList = packagesClassesList;
    }
}
