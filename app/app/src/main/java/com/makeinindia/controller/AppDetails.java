package com.makeinindia.controller;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anant.y on 22-Apr-16.
 */
public class AppDetails {
    public ArrayList<PackageInfoStruct> res = new ArrayList<PackageInfoStruct>();
    public ArrayList<PackageInfoStruct> resCache = new ArrayList<PackageInfoStruct>();
    public ListView list;
    public String app_labels[];
    public String package_labels[];
    Activity mActivity;

    public AppDetails(Activity mActivity) {
        this.mActivity = mActivity;

    }

    public ArrayList<PackageInfoStruct> getPackages() {
        ArrayList<PackageInfoStruct> apps = getInstalledApps(false); /*
                                                                     * false =
                                                                     * no system
                                                                     * packages
                                                                     */
        final int max = apps.size();
        for (int i = 0; i < max; i++) {
            apps.get(i);
        }
        return apps;
    }

    private ArrayList<PackageInfoStruct> getInstalledApps(boolean getSysPackages) {

        List<PackageInfo> packs = mActivity.getPackageManager().getInstalledPackages(0);
        try {
            app_labels = new String[packs.size()];
        } catch (Exception e) {
            Toast.makeText(mActivity.getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue;
            }
            PackageInfoStruct newInfo = new PackageInfoStruct();
            newInfo.appname = p.applicationInfo.loadLabel(
                    mActivity.getPackageManager()).toString();
            newInfo.pname = p.packageName;
            newInfo.datadir = p.applicationInfo.dataDir;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(mActivity.getPackageManager());
            res.add(newInfo);

            app_labels[i] = newInfo.appname;
        }
        return res;
    }


    public List<PackageInfo> getInstalledAppCache(boolean getSystemPack) {
        List<PackageInfo> packs = mActivity.getPackageManager().getInstalledPackages(PackageManager.GET_META_DATA);
       /* try {
            package_labels = new String[packs.size()];
        } catch (Exception e) {
            Toast.makeText(mActivity.getApplicationContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }*/


        return packs;

    }


    public class PackageInfoStruct {
        String appname = "";
        String pname = "";
        String versionName = "";
        int versionCode = 0;
        Drawable icon;
        String datadir = "";
    }
}
