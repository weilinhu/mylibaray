package com.zhanle.upgrade;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

/**
 * @author weilinhu
 */
public class DownloadObserver extends ContentObserver {

    private UpgradeListener upgradeListener;
    private long donwnid;
    private Context context;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public DownloadObserver(UpgradeListener upgradeListener, long downid,Context context,Handler handler) {
        super(handler);
        this.donwnid =downid;
        this.upgradeListener =upgradeListener;
        this.context= context;

    }


    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);

        //每当/data/data/com.android.providers.download/database/database.db变化后，触发onCHANGE，开始具体查询
        //实例化查询类，这里需要一个刚刚的downid
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(donwnid);
        DownloadManager downloadManager = (DownloadManager) context.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        //这个就是数据库查询啦
        Cursor cursor =null ;
        try {
            cursor = downloadManager.query(query);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (cursor == null){
            return;
        }


        if (cursor.moveToFirst()) {
            int sizeIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            int downloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            long size = cursor.getInt(sizeIndex);
            long downloaded = cursor.getInt(downloadedIndex);
            int progress = 0;
            if (size != -1) progress = (int) (downloaded*100.0/size);
            // At this point you have the progress as a percentage.

            if (upgradeListener!=null){
                upgradeListener.onUpgradeListener(progress,null);
            }
        }

    }
}
