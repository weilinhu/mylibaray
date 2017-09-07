package com.zhanshow.mylibrary.record;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author weilinhu
 */

public class RecorderReceiver extends BroadcastReceiver {
    private static final String TAG = "RecorderReceiver";

    protected RecorderReceiver.RecorderReceiverListener listener;
    private Object lock = new Object();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(RecorderService.RECORDER_SERVICE_BROADCAST_STATE)) {
            String isRecording = intent.getStringExtra(
                    RecorderService.RECORDER_SERVICE_BROADCAST_STATE);
            Log.e(TAG, "onReceive: "+isRecording );
            if (listener == null){
                return;
            }

           if ("start".equals(isRecording)){
               listener.recordStartSuccess();
           }else if ("else".equals(isRecording)){
               listener.recordFiled();
           }

//            mRecorder.setState(isRecording ? Recorder.RECORDING_STATE : Recorder.IDLE_STATE);
        } else if (intent.hasExtra(RecorderService.RECORDER_SERVICE_BROADCAST_ERROR)) {
            String error = intent.getStringExtra(RecorderService.RECORDER_SERVICE_BROADCAST_ERROR);
            if (listener == null){
                return;
            }
            Log.e(TAG, "onReceive: "+error );
            if ("start".equals(error)){
                listener.recordStartFailed();
            }else if ("else".equals(error)){
                listener.recordFiled();
            }

        }
    }


    public void addListener(RecorderReceiver.RecorderReceiverListener l) {
        this.listener = l;
    }




    public interface RecorderReceiverListener {
        public void recordStartSuccess();
        public void recordStartFailed();
        public void recordFiled();
    }
}
