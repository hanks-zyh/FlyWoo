package com.zjk.wifiproject.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;

import com.zjk.wifiproject.app.AppModle;

/**
 * App工具类
 */
public class AppUtils {

    /**
     * 返回用户已安装应用列表
     */
    public static List<AppModle> getAppList(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<AppModle> appInfos = new ArrayList<AppModle>();
        for (PackageInfo packageInfo : packageInfos) {

            ApplicationInfo app = packageInfo.applicationInfo;
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // 非系统应用
                File apkfile = new File(app.sourceDir);
                PackageStats stats = new PackageStats(packageInfo.packageName);

                AppModle appInfo = new AppModle();
                appInfo.setApkPath(app.sourceDir);
                appInfo.setPackageName(packageInfo.packageName);
                appInfo.setVersionCode(packageInfo.versionCode);
                appInfo.setVersionName(packageInfo.versionName);
                appInfo.setApkSize(apkfile.length());
                appInfo.setUid(app.uid);
                appInfo.setIcon(app.loadIcon(pm));
                appInfo.setName(app.loadLabel(pm).toString());
                appInfo.setCacheSize(stats.cacheSize);
                appInfo.setDataSize(stats.dataSize);
                appInfos.add(appInfo);
            }

        }

        return appInfos;
    }
}
