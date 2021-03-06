package com.emmanuelmess.compressionpoc;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class ApplicationsOnDevice {
    public static List<Drawable> getAllAppImages(final Context context) {
        ArrayList<Drawable> images = new ArrayList<>();
        try {
            PackageManager packageManager = context.getPackageManager();
            Intent intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);

            for (ResolveInfo resolveInfo : resolveInfoList) {
                images.add(resolveInfo.activityInfo.loadIcon(packageManager));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return images;
    }
}
