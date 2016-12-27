package com.example.liuhaoyuan.phonemanager.utils;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Debug;
import android.os.StatFs;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Xml;

import com.example.liuhaoyuan.phonemanager.R;
import com.example.liuhaoyuan.phonemanager.bean.AppInfo;
import com.example.liuhaoyuan.phonemanager.bean.ProcessInfo;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhoneUtils {
    public static String getSimNumber(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSimSerialNumber();
    }

    public static void saveSimNumber(Context context, String simNumber) {
        SpUtils.putString(context, ConstantValue.CONFIG, ConstantValue.SIM_NUMBER, simNumber);
    }

    public static void sendSMS(String des, String data) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(des, null, data, null, null);
    }

    public static void startCall(Context context,String number){
        Intent intent=new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:"+number));
        context.startActivity(intent);
    }

    public static int getProcessCount(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        return processes.size();
    }

    public static void killProcess(Context context, ProcessInfo processInfo){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.killBackgroundProcesses(processInfo.packageName);
    }

    public static void killAllProcess(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            activityManager.killBackgroundProcesses(process.processName);
        }
    }

    public static long getAvailRamSpace(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem;
    }

    public static long getTotalRamSpace() {
        try {
            FileReader fileReader = new FileReader("proc/meminfo");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();
            char[] chars = line.toCharArray();
            StringBuilder buffer = new StringBuilder();
            for (char aChar : chars) {
                if ((aChar <= '9') && (aChar >= '0')) {
                    buffer.append(aChar);
                }
            }
            return Long.parseLong(buffer.toString())*1024;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static ArrayList<ProcessInfo> getProcessInfoList(Context context) {
        ArrayList<ProcessInfo> list=new ArrayList<>();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager packageManager = context.getPackageManager();

        List<ActivityManager.RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            ProcessInfo processInfo = new ProcessInfo();
            processInfo.packageName = info.processName;
            Debug.MemoryInfo[] memoryInfos = activityManager.getProcessMemoryInfo(new int[]{info.pid});
            processInfo.memSize = memoryInfos[0].getTotalPrivateDirty() * 1024;

            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(processInfo.packageName, 0);
                processInfo.name = applicationInfo.loadLabel(packageManager).toString();
                processInfo.icon = applicationInfo.loadIcon(packageManager);
                processInfo.isSystem = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                processInfo.name=info.processName;
                processInfo.icon=context.getResources().getDrawable(R.drawable.launcher_bg);
                processInfo.isSystem=true;
            }
            list.add(processInfo);
        }
        return list;
    }

    public static ArrayList<AppInfo> getAppInfoList(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        ArrayList<AppInfo> list = new ArrayList<>();
        for (PackageInfo aPackage : packages) {
            AppInfo appInfo = new AppInfo();
            appInfo.packageName = aPackage.packageName;

            ApplicationInfo applicationInfo = aPackage.applicationInfo;
            appInfo.name = applicationInfo.loadLabel(packageManager).toString();
            appInfo.icon = applicationInfo.loadIcon(packageManager);
            appInfo.versionName = aPackage.versionName;
            appInfo.isUser = (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM;
            appInfo.isSd = (applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE;
            list.add(appInfo);
        }
        return list;
    }

    public static long getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        long blocks;
        long blockSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blocks = statFs.getAvailableBlocksLong();
            blockSize = statFs.getBlockSizeLong();
        } else {
            blocks = statFs.getAvailableBlocks();
            blockSize = statFs.getBlockSize();
        }
        return blocks * blockSize;
    }

    public static void smsBackup(Context context, String dir, ProgressDialog dialog) {
        File file = new File(dir);
        FileOutputStream os = null;
        Cursor cursor = null;
        try {
            os = new FileOutputStream(file);
            cursor = context.getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"address", "date", "type", "body"}, null, null, null);

            if (cursor != null) {
                XmlSerializer serializer = Xml.newSerializer();
                serializer.setOutput(os, "utf-8");
                serializer.startDocument("utf-8", true);
                serializer.startTag(null, "sms");
                int progress = 0;
                dialog.setMax(cursor.getCount());
                while (cursor.moveToNext()) {
                    serializer.startTag(null, "message");

                    serializer.startTag(null, "address");
                    serializer.text(cursor.getString(cursor.getColumnIndex("address")));
                    serializer.endTag(null, "address");

                    serializer.startTag(null, "date");
                    serializer.text(cursor.getString(cursor.getColumnIndex("date")));
                    serializer.endTag(null, "date");

                    serializer.startTag(null, "type");
                    serializer.text(cursor.getString(cursor.getColumnIndex("type")));
                    serializer.endTag(null, "type");

                    serializer.startTag(null, "body");
                    serializer.text(cursor.getString(cursor.getColumnIndex("body")));
                    serializer.endTag(null, "body");

                    serializer.endTag(null, "message");
                    progress++;
                    dialog.setProgress(progress);
                }
                serializer.endTag(null, "sms");
                serializer.endDocument();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
