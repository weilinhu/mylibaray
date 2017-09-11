package com.zhanle.upgrade;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

/**
 * 文件管理器
 *
 * 
 */
public class FileManager {

    private static final long NO_SPACE = -1;

    // SD卡文件根目录
    private static final String ZHANLE_DIR_NAME = "ZhanLe";
    //保存图片文件夹名
    private static final String IMAGE_SAVE_DIR_NAME = "image";
    // apk保存目录
    private static final String APK_SAVE_DIR_NAME = "apk";
    // 音乐保存目录
    private static final String MUSIC_SAVE_DIR_NAME = "music";

    private File mZhanShowDir;

    public FileManager(){

    }

    /**
     * 得到SPUtils的单例
     *
     * @return 单例
     */
    public static FileManager getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * 这个类存在的唯一意义就是为了创建FileManager的单例
     *
     * 
     */
    private static final class Singleton {
        private static final FileManager INSTANCE = new FileManager();
    }

    public void init(){
        initIfNeed();
    }


    /**
     * 初始化磁盘目录
     */
    private void initIfNeed() {

        if(mZhanShowDir != null && mZhanShowDir.exists()){
            return;
        }

        long availableSDCardSpace = getExternalStorageSpace();// 获取SD卡可用空间
        if (availableSDCardSpace != NO_SPACE) {// 如果存在SD卡
            String zhanshowDirPath = Environment.getExternalStorageDirectory()
                    + File.separator + FileManager.ZHANLE_DIR_NAME;

            // 初始化根目录
            mZhanShowDir = new File(zhanshowDirPath);
            if (!mZhanShowDir.exists()) {
                mZhanShowDir.mkdir();
            }
        }
    }
    /**
     * 获取SD卡可用空间
     *
     * @return availableSDCardSpace 可用空间(MB)。-1L:没有SD卡
     */
    private long getExternalStorageSpace() {
        long availableSDCardSpace = NO_SPACE;
        // 存在SD卡
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
            long blockSize = sf.getBlockSize();// 块大小,单位byte
            long availCount = sf.getAvailableBlocks();// 可用块数量
            availableSDCardSpace = availCount * blockSize / 1024 / 1024;// 可用SD卡空间，单位MB
        }

        return availableSDCardSpace;
    }

    public String getApkSaveDir(){
        return getDirPath(APK_SAVE_DIR_NAME);
    }

    public String getImageSavePath(){
        return getDirPath(IMAGE_SAVE_DIR_NAME);
    }

    public String getMusicSavePath(){
        return getDirPath(MUSIC_SAVE_DIR_NAME);
    }

    private String getDirPath(String dirName){
        initIfNeed();

        if(mZhanShowDir == null || !mZhanShowDir.exists()){
            return null;
        }

        File dir = new File(mZhanShowDir,dirName);
        if(!dir.exists()){
            dir.mkdir();
        }

        return dir.getAbsolutePath();
    }

    /**
     * 删除存在的apk文件
     * @param file
     */
    public  void deleteApk(File file) {
        if (!file.exists()) {
            return;
        }

        if(file.isDirectory()){
            File[] childFiles = file.listFiles();

            for (int i = 0; i < childFiles.length; i++) {

                deleteApk(childFiles[i]);
                if (childFiles[i].isFile()){
                }

            }

        }
    }





    public void saveFile(Context context,String value){

        SharedPreferences sharedPreferences = context.getSharedPreferences(UpgradeManager.TAG, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(UpgradeManager.KEY_UPDATE_DOWNLOAD_FILE,value);

        editor.apply();

    }


    public void saveId(Context context,long value){

        SharedPreferences sharedPreferences = context.getSharedPreferences(UpgradeManager.TAG, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putLong(UpgradeManager.KEY_UPDATE_DOWNLOAD_ID,value);

        editor.apply();

    }



    public long getId(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(UpgradeManager.TAG, Context.MODE_PRIVATE);


        return sharedPreferences.getLong(UpgradeManager.KEY_UPDATE_DOWNLOAD_ID,0);

    }

    public String getFile(Context context){

        SharedPreferences sharedPreferences = context.getSharedPreferences(UpgradeManager.TAG, Context.MODE_PRIVATE);

        return sharedPreferences.getString(UpgradeManager.KEY_UPDATE_DOWNLOAD_FILE,"");
    }

}
