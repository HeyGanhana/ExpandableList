package com.bilim.expandablelist.util;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.bilim.expandablelist.bean.BaseInfo;
import com.bilim.expandablelist.log.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by zhangdi on 11/16/18.
 */
public class MemoryUtil {

    private static final String TAG = "MemoryUtil";

    public static boolean isSdcardMounted() {
        //Log.e(TAG,"getExternalStorageState ="+Environment.getExternalStorageState());
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        }
        return false;
    }
    /*
    将所有扫描到的数据累加存放在sharedPreference 中，扫描完成时清零
    */
    public static void addTrashSizeToPreference(Context context, long size) {
        SharedPreferences sharedPreferences = getDefaultSharedPreference(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //long oldSize = sharedPreferences.getLong("total_trash_size", 0);
        editor.putLong("total_trash_size", size);
        editor.commit();
    }
    public static long getCurrentTrashSize(Context context){
        return getDefaultSharedPreference(context).getLong("total_trash_size",0);
    }
    public static void clearPreferenceData(Context context){
        getDefaultSharedPreference(context).edit().clear().commit();
    } 
    
    public static SharedPreferences getDefaultSharedPreference(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                "com.sagereal.memorycleaner", Context.MODE_PRIVATE);
        return sharedPreferences;
    }
    
    /*
    * string[0]:size
    * string[1]:unit kb,Mb,Gb
      notice:deprecated
    * */
    public static String[] getFormatSizeAndUnit(long size) {
        Log.e(TAG, "size==" + size);
        String[] values = new String[2];
        if(size < 1024 && size > 0){
            values[0] = String.valueOf(size);
            values[1] = "B";
            return values;
        }
        
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            values[0] = String.valueOf(size / 1024);
            values[1] = "KB";
            return values;
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            values[0] = result1.setScale(1, BigDecimal.ROUND_HALF_UP).toPlainString();
            values[1] = "KB";
            return values;
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            values[0] = result2.setScale(1, BigDecimal.ROUND_HALF_UP)
                    .toPlainString();
            values[1] = "MB";
            return values;
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            values[0] = result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString();
            values[1] = "GB";
            return values;
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        values[0] = result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
        values[1] = "TB";
        return values;
    }
    
    //get percent value int:
    public static int getPercentInt(long used,long total){
        double percent = used / (double) total;
        String numberPercent = NumberFormat.getPercentInstance().format(percent);
        String dealPercent = numberPercent.substring(0,numberPercent.indexOf("%"));
        Logger.e(TAG, "dealPercent:" + dealPercent);
        return Integer.parseInt(dealPercent);
    }
    public static long getAvailMemory(Context context) { 
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 
        MemoryInfo mi = new MemoryInfo(); 
        am.getMemoryInfo(mi); 
        return mi.availMem; 
    }
    
	//get device total ram size,unit MB
    public static long getTotalRam(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		return mi.totalMem;
    }

    /*
     * param:size
     * 格式化long size
     * */
    public static String getFormatSize(long size) {
        Log.e(TAG, "size==" + size);
        if(size < 1024 && size > 0){
            return size + "B";
        }
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return size / 1024 + "kB";
        }
        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(1, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "KB";
        }
        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(1, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "MB";
        }
        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
                + "TB";
    }


    /*
     * 获取文件夹的大小
     * */
    public static long getFolderSize(File file) {
        if (file == null || !file.exists()) {
            return 0;
        }
        long size = 0;
        try {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files == null || files.length == 0) {
                    return 0;
                }
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        size += getFolderSize(files[i]);
                    } else {
                        size += files[i].length();
                    }
                }
            } else {
                return file.length();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /*
     * 获取系统所有app对应的context
     * */
    public static List<Context> getAllAppContexts(Context mContext) {
        List<Context> contexts = new ArrayList<>();

        PackageManager packageManager = mContext.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        Context context = null;
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 0);
        //Collections.sort(resolveInfoList, ResolveInfo.DisplayNameComparator());
        for (int i = 0; i < resolveInfoList.size(); i++) {
            ResolveInfo info = resolveInfoList.get(i);
            String packageName = info.activityInfo.packageName;
            try {
                context = mContext.createPackageContext(packageName, Context
                        .CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (context != null)
                contexts.add(context);
        }
        return contexts;
    }
    public static PackageInfo getTopActivityPackageInfo(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService
                (Context.ACTIVITY_SERVICE);
        String packageName = null;
        if (Build.VERSION.SDK_INT >= 21) {
            List<ActivityManager.RunningAppProcessInfo> pis = manager.getRunningAppProcesses();
            ActivityManager.RunningAppProcessInfo topAppProcess = pis.get(0);
            if (topAppProcess != null &&
                    topAppProcess.importance == ActivityManager.RunningAppProcessInfo
                            .IMPORTANCE_FOREGROUND) {
                packageName = topAppProcess.processName;

            }
        }
        PackageInfo packageInfo = null;
        if(packageName == null || "".equals(packageName)){
            return null;
        }
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName,0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("zhangdi789","NameNotFoundException:");
            e.printStackTrace();
            return null;
        }
        return packageInfo;
    }

    public static boolean isAppBeenUninstalled(Context context,String packageName){
        
        if (packageName != null && !"".equals(packageName)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
                Log.e("zhangdi789", "packageInfo:"+packageInfo);
                //if(isSystemApp()){
                //}
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("zhangdi789", "NameNotFoundException:");
                return true;
            }
           
        }
        return false;
    }
    

    public static boolean isSystemApp(PackageInfo pInfo) {
        //is system app
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }
    public static boolean isSystemUpdateApp(PackageInfo pInfo) {
        //is system update app..
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }
    public static boolean isUserApp(Context context,String packageName) {
        //is system update app or system app
        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
            Log.e("zhangdi789", "packageInfo:"+packageInfo);
            //if(isSystemApp()){
            //}
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("zhangdi789", "NameNotFoundException:");
            return false;
        }
        return (!isSystemApp(packageInfo) && !isSystemUpdateApp(packageInfo));
    }
    
    /*create package context obj from packageName
    */
    public static Context createContextFromPackeage(Context mContext, String packageName) {
        Context context = null;
        try {
            context = mContext.createPackageContext(packageName, Context
                    .CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return context;
    }
    
    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public static byte[] bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static int getGroupIndexFromGroupType(BaseInfo.GroupType GroupType) {
        int index = -1;
        BaseInfo.GroupType[] GroupTypes = BaseInfo.GroupType.values();
        for (int i = 0; i < GroupTypes.length; i++) {
            if(GroupType.equals(GroupTypes[i])){
                index = i;
            }
        }
        return index;
    }
}
