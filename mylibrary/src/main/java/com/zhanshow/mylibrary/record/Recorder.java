/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhanshow.mylibrary.record;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaRecorder;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static com.zhanshow.mylibrary.record.RecorderService.RECORDER_SERVICE_BROADCAST_ERROR;
import static com.zhanshow.mylibrary.record.RecorderService.RECORDER_SERVICE_BROADCAST_NAME;

public class Recorder implements OnCompletionListener, OnErrorListener {
    public static final String SAMPLE_DEFAULT_DIR = "/sdfiel";
    public static final int IDLE_STATE = 0;
    public static final int RECORDING_STATE = 1;
    public static final int PLAYING_STATE = 2;
    public static final int PLAYING_PAUSED_STATE = 3;
    public static final int NO_ERROR = 0;
    public static final int STORAGE_ACCESS_ERROR = 1;
    public static final int INTERNAL_ERROR = 2;
    public static final int IN_CALL_RECORD_ERROR = 3;
    private static final String TAG = "Recorder";
    private static final String SAMPLE_PREFIX = "recording";
    private static final String SAMPLE_PATH_KEY = "sample_path";
    private static final String SAMPLE_LENGTH_KEY = "sample_length";
    private int mState = IDLE_STATE;
    private Context mContext;
    private OnStateChangedListener mOnStateChangedListener = null;

    private int mSampleLength = 0; // length of current sample
    // operation started
    private File mSampleFile = null;
    private File mSampleDir = null;
    private MediaPlayer mPlayer = null;
    private RecorderReceiver mReceiver;
    IntentFilter filter;
    public Recorder(Context context) {
        mContext = context;
        File sampleDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + SAMPLE_DEFAULT_DIR);

        DeleteFolder(sampleDir.getAbsolutePath());

        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        mSampleDir = sampleDir;
        mReceiver = new RecorderReceiver();
        syncStateWithService();
         filter = new IntentFilter();
        filter.addAction(RecorderService.RECORDER_SERVICE_BROADCAST_NAME);
        mContext.registerReceiver(mReceiver, filter);
    }

    public void release(Context context){
        stop();
        context.unregisterReceiver(mReceiver);
    }

    public boolean DeleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public boolean syncStateWithService() {
        if (RecorderService.isRecording()) {
            mState = RECORDING_STATE;

            mSampleFile = new File(RecorderService.getFilePath());
            return true;
        } else if (mState == RECORDING_STATE) {
            // service is idle but local state is recording
            return false;
        } else if (mSampleFile != null && mSampleLength == 0) {
            // this state can be reached if there is an incoming call
            // the record service is stopped by incoming call without notifying
            // the UI
            return false;
        }
        return true;
    }



    public String getRecordDir() {
        return mSampleDir.getAbsolutePath();
    }

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        mOnStateChangedListener = listener;
    }

    public int state() {
        return mState;
    }

    public int sampleLength() {
        return mSampleLength;
    }

    public File sampleFile() {
        return mSampleFile;
    }

    public void renameSampleFile(String name) {
        if (mSampleFile != null && mState != RECORDING_STATE && mState != PLAYING_STATE) {
            if (!TextUtils.isEmpty(name)) {
                String oldName = mSampleFile.getAbsolutePath();
                String extension = oldName.substring(oldName.lastIndexOf('.'));
                File newFile = new File(mSampleFile.getParent() + "/" + name + extension);
                if (!TextUtils.equals(oldName, newFile.getAbsolutePath())) {
                    if (mSampleFile.renameTo(newFile)) {
                        mSampleFile = newFile;
                    }
                }
            }
        }
    }

    /**
     * Resets the recorder state. If a sample was recorded, the file is deleted.
     */
    public void delete() {
        stop();

        if (mSampleFile != null)
            mSampleFile.delete();

        mSampleFile = null;
        mSampleLength = 0;

        signalStateChanged(IDLE_STATE);
    }

    /**
     * Resets the recorder state. If a sample was recorded, the file is left on
     * disk and will be reused for a new recording.
     */
    public void clear() {
        stop();
        mSampleLength = 0;
        signalStateChanged(IDLE_STATE);
        mContext.registerReceiver(mReceiver, filter);
    }

    public void reset() {
        stop();

        mSampleLength = 0;
        mSampleFile = null;
        mState = IDLE_STATE;

        File sampleDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + SAMPLE_DEFAULT_DIR);
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        mSampleDir = sampleDir;

        signalStateChanged(IDLE_STATE);
    }

    public boolean isRecordExisted(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(mSampleDir.getAbsolutePath() + "/" + path);
            return file.exists();
        }
        return false;
    }

    public void startRecording(RecorderReceiver.RecorderReceiverListener l) {
        stop();
        mReceiver.addListener(l);
        if (mSampleFile == null) {
            try {
                mSampleFile = File.createTempFile(SAMPLE_PREFIX, ".amr", mSampleDir);
                renameSampleFile("dfa");
            } catch (IOException e) {
                Log.e(TAG, "startRecording: " + e.getMessage());
                setError(STORAGE_ACCESS_ERROR);
                return;
            }
        }


        RecorderService.startRecording(mContext, MediaRecorder.OutputFormat.AMR_NB, mSampleFile.getAbsolutePath(),
                false, -1);

    }

    public void stopRecording() {
        if (RecorderService.isRecording()) {
            RecorderService.stopRecording(mContext);

        }
    }



    public void pausePlayback() {
        if (mPlayer == null) {
            return;
        }

        mPlayer.pause();
        setState(PLAYING_PAUSED_STATE);
    }

    public void stop() {
        stopRecording();
    }

    public boolean onError(MediaPlayer mp, int what, int extra) {
        stop();
        setError(STORAGE_ACCESS_ERROR);
        return true;
    }

    public void onCompletion(MediaPlayer mp) {
        stop();
    }

    public void setState(int state) {
        if (state == mState)
            return;

        mState = state;
        signalStateChanged(mState);
    }

    private void signalStateChanged(int state) {
        if (mOnStateChangedListener != null)
            mOnStateChangedListener.onStateChanged(state);
    }

    public void setError(int error) {
        sendErrorBroadcast(Recorder.INTERNAL_ERROR);
    }

    private void sendErrorBroadcast(int error) {
        Intent intent = new Intent(RECORDER_SERVICE_BROADCAST_NAME);
        intent.putExtra(RECORDER_SERVICE_BROADCAST_ERROR, "start");
        mContext.sendBroadcast(intent);
    }

    public interface OnStateChangedListener {
        public void onStateChanged(int state);

        public void onError(int error);
    }
}
