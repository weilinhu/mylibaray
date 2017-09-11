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

        while (cursor.moveToNext()) {
            int mDownload_so_far = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
            int mDownload_all = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            int mProgress = (int) ((mDownload_so_far*100.0) / mDownload_all);
            if (upgradeListener!=null){
                upgradeListener.onUpgradeListener(mProgress,null);
            }
        }

    }
}
