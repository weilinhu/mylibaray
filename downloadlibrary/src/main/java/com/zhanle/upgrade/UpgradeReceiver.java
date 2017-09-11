package com.zhanle.upgrade;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * 升级Receiver
 */
public class UpgradeReceiver extends BroadcastReceiver {
    private static final String TAG = "UpdateReceiver";

    private static final long INVALID_DOWNLOAD_ID = -1;

    @Override
    public void onReceive(Context context, Intent intent) {

        //取消注册
        UpgradeManager.getInstance().registerContentObserver(context,false);

        String action = intent.getAction();
        // 下载完成广播
        if(DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)){
            // 得到DownloadId
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,INVALID_DOWNLOAD_ID);
            long currentId =FileManager.getInstance().getId(context);


            if(downloadId == currentId){
                installApk(context, downloadId);
            }

        }else if(DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)){
            long ids[] = intent.getLongArrayExtra(DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS);
            if(null == ids){
                return;
            }
            long currentId =FileManager.getInstance().getId(context);
            for(long id : ids){
                if(id == currentId){
                    installApk(context,currentId);
                }
            }
        }
    }

    private void installApk(Context context, long downloadId) {
        DownloadManager dm = (DownloadManager) context.getApplicationContext()
                .getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = dm.getUriForDownloadedFile(downloadId);
        if(uri == null){
            return;
        }

        String apkFile = FileManager.getInstance().getFile(context);

        Intent installIntent = new Intent(Intent.ACTION_VIEW);
        installIntent.setDataAndType(Uri.fromFile(new File(apkFile)),
                "application/vnd.android.package-archive");
        installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(installIntent);
    }
}
