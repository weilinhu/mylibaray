package com.zhanle.upgrade;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

/**
 * 升级管理器
 *
 *
 */
public class UpgradeManager {
    public static final String TAG = "UpdateManager";
    public static final String KEY_UPDATE_DOWNLOAD_ID = "keyUpdateDownloadId";
    public static final String KEY_UPDATE_DOWNLOAD_FILE= "key_update_download_file";
    public static final int INVALID_UPDATE_DOWNLOAD_ID = -1;

    private static final String KEY_HAS_NEW_VERSION = "keyHasNewVersion";
    public static final String KEY_FORCE_UPDATE_DOWNLOADING = "keyForceUpdateDownloading";
    public static final String KEY_DOWNLOADING = "keyDownloading";

    private DownloadObserver downloadObserver;
    private ProgressBar progressBar;
    private TextView tvOnPregressbar;

    //现在区分一个入口
    public static final int FROM_MAIN = 1;
    public static final int FROM_ABOUT = 2;

    private int mFrom = FROM_MAIN;

    private boolean isForceUpdate = false;
    private boolean dialogCancelable = true;

    private UpgradeManager(){

    }

    private static class Generator{
        private static final UpgradeManager INSTANCE = new UpgradeManager();
    }

    public static UpgradeManager getInstance(){
        return Generator.INSTANCE;
    }

    public boolean isForceUpdate() {
        return isForceUpdate;
    }

    public UpgradeManager setFrom(int from){
        this.mFrom = from;
        return getInstance();
    }

    public boolean isDialogCancelable(){
        return dialogCancelable;
    }

    /**
     *
     * @param context
     * @param url 下载地址
     * @param appName  显示在通知栏的下载名称， 一般为 appName
     * @param upgradeListener
     */
    public void download(Context context,String url,String appName,UpgradeListener upgradeListener){

        if (upgradeListener == null){
            Log.e(TAG, "download: unset UpgradeListener");
            return;
        }

        DownloadManager dm = (DownloadManager) context.getApplicationContext()
                .getSystemService(Context.DOWNLOAD_SERVICE);

        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                    | DownloadManager.Request.NETWORK_WIFI);

            File dir = null;
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                String path = FileManager.getInstance().getApkSaveDir();
                if (path == null) {
                    upgradeListener.onUpgradeListener(-1,new Error(Error.NO_SDCARD,"no_sdcard"));
                    return;
                }
                dir = new File(path);
                if (!dir.exists()) {
                    upgradeListener.onUpgradeListener(-1,new Error(Error.NO_SDCARD,"no_sdcard"));
                    return;
                }
            } else {
                upgradeListener.onUpgradeListener(-1,new Error(Error.NO_SDCARD,"no_sdcard"));
                return;
            }

            //删除已有的apk
            FileManager.getInstance().deleteApk(dir);

            File file = new File(dir, url+ ".apk");
            FileManager.getInstance().saveFile(context,file.getAbsolutePath());
            request.setDestinationUri(Uri.fromFile(file));
            request.setMimeType("application/vnd.android.package-archive");
            request.allowScanningByMediaScanner();
            // 设置为可见和可管理
            request.setVisibleInDownloadsUi(true);


            request.setTitle(appName);

            // 在下载过程中通知栏会一直显示该下载的Notification，在下载完成后该Notification会继续显示，
            // 直到用户点击该Notification或者消除该Notification
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            // 返回值是系统为当前的下载请求分配的一个唯一的ID，
            long downloadId = dm.enqueue(request);
            Handler handler = new Handler(Looper.getMainLooper());
            downloadObserver = new DownloadObserver(upgradeListener, downloadId,context,handler);
            registerContentObserver(context,true);
            FileManager.getInstance().saveId(context,  downloadId);
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            context.getApplicationContext().registerReceiver(new UpgradeReceiver(), filter);
        }catch (Exception e){
            e.printStackTrace();
            upgradeListener.onUpgradeListener(-1,new Error(Error.EXCEPTION,e.getMessage()));
        }
    }








    /**
     * 进度观察者
     * @param register
     */
    public void registerContentObserver(Context context,boolean register){
        if (downloadObserver == null){
            return;
        }
        if (register){
            context.getApplicationContext().getContentResolver()
                    .registerContentObserver(Uri.parse("content://downloads/"),true,downloadObserver);
        }else {
            context.getApplicationContext().getContentResolver()
                    .unregisterContentObserver(downloadObserver);
        }
    }





}
