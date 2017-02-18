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

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;

public class RecorderService extends Service implements MediaRecorder.OnErrorListener {
    private static final String TAG = "RecorderService";

    public final static String ACTION_NAME = "action_type";

    public final static int ACTION_INVALID = 0;

    public final static int ACTION_START_RECORDING = 1;

    public final static int ACTION_STOP_RECORDING = 2;

    public final static int ACTION_ENABLE_MONITOR_REMAIN_TIME = 3;

    public final static int ACTION_DISABLE_MONITOR_REMAIN_TIME = 4;

    public final static String ACTION_PARAM_FORMAT = "format";

    public final static String ACTION_PARAM_PATH = "path";

    public final static String ACTION_PARAM_HIGH_QUALITY = "high_quality";

    public final static String ACTION_PARAM_MAX_FILE_SIZE = "max_file_size";

    public final static String RECORDER_SERVICE_BROADCAST_NAME = "com.android.soundrecorder.broadcast";

    public final static String RECORDER_SERVICE_BROADCAST_STATE = "is_recording";

    public final static String RECORDER_SERVICE_BROADCAST_ERROR = "error_code";


    private static MediaRecorder mRecorder = null;

    private static String mFilePath = null;

    private static long mStartTime = 0;


    private NotificationManager mNotifiManager;

    private Notification mLowStorageNotification;

    private TelephonyManager mTeleManager;



    private KeyguardManager mKeyguardManager;

    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state != TelephonyManager.CALL_STATE_IDLE) {
                localStopRecording("else");
            }
        }
    };

    private final Handler mHandler = new Handler();



    private boolean mNeedUpdateRemainingTime;

    @Override
    public void onCreate() {
        super.onCreate();
        mRecorder = null;
        mTeleManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTeleManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey(ACTION_NAME)) {
            switch (bundle.getInt(ACTION_NAME, ACTION_INVALID)) {
                case ACTION_START_RECORDING:
                    localStartRecording(bundle.getInt(ACTION_PARAM_FORMAT),
                            bundle.getString(ACTION_PARAM_PATH),
                            bundle.getBoolean(ACTION_PARAM_HIGH_QUALITY),
                            bundle.getLong(ACTION_PARAM_MAX_FILE_SIZE));
                    break;
                case ACTION_STOP_RECORDING:
                    localStopRecording("stop");
                    break;
                case ACTION_ENABLE_MONITOR_REMAIN_TIME:
                    if (mRecorder != null) {
                        mNeedUpdateRemainingTime = true;
                    }
                    break;
                case ACTION_DISABLE_MONITOR_REMAIN_TIME:
                    mNeedUpdateRemainingTime = false;
                    if (mRecorder != null) {
                    }
                    break;
                default:
                    break;
            }
            return START_STICKY;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mTeleManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLowMemory() {
        localStopRecording("else");
        super.onLowMemory();
    }

    private void localStartRecording(int outputfileformat, String path, boolean highQuality,
                                     long maxFileSize) {
        if (mRecorder == null) {



            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            if (outputfileformat == MediaRecorder.OutputFormat.THREE_GPP) {

                mRecorder.setAudioSamplingRate(highQuality ? 44100 : 22050);
                mRecorder.setOutputFormat(outputfileformat);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            } else {

                mRecorder.setAudioSamplingRate(highQuality ? 16000 : 8000);
                mRecorder.setOutputFormat(outputfileformat);
                mRecorder.setAudioEncoder(highQuality ? MediaRecorder.AudioEncoder.AMR_WB
                        : MediaRecorder.AudioEncoder.AMR_NB);
            }
            mRecorder.setOutputFile(path);
            mRecorder.setOnErrorListener(this);

            // Handle IOException
            try {
                mRecorder.prepare();
            } catch (IOException exception) {
                Log.e(TAG, "localStartRecording: "+exception.getMessage() );
                sendErrorBroadcast("start");
                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                return;
            }
            // Handle RuntimeException if the recording couldn't start
            try {
                mRecorder.start();
            } catch (RuntimeException exception) {

                sendErrorBroadcast("start");

                mRecorder.reset();
                mRecorder.release();
                mRecorder = null;
                return;
            }
            mFilePath = path;
            mStartTime = System.currentTimeMillis();

            mNeedUpdateRemainingTime = false;
            sendStateBroadcast("start");
        }
    }

    private void localStopRecording(String success) {
        if (mRecorder != null) {
            mNeedUpdateRemainingTime = false;
            try {
                mRecorder.stop();
            } catch (RuntimeException e) {
                Log.e(TAG, "localStopRecording: "+e.getMessage() );
            }
            mRecorder.release();
            mRecorder = null;

            sendStateBroadcast(success);

        }
        stopSelf();
    }







    private void sendStateBroadcast(String success) {
        Intent intent = new Intent(RECORDER_SERVICE_BROADCAST_NAME);
        intent.putExtra(RECORDER_SERVICE_BROADCAST_STATE, success);
        sendBroadcast(intent);
    }

    private void sendErrorBroadcast(String success) {
        Intent intent = new Intent(RECORDER_SERVICE_BROADCAST_NAME);
        intent.putExtra(RECORDER_SERVICE_BROADCAST_ERROR, success);
        sendBroadcast(intent);
    }



    public static boolean isRecording() {
        return mRecorder != null;
    }

    public static String getFilePath() {
        return mFilePath;
    }



    public static void startRecording(Context context, int outputfileformat, String path,
                                      boolean highQuality, long maxFileSize) {
        Intent intent = new Intent(context, RecorderService.class);
        intent.putExtra(ACTION_NAME, ACTION_START_RECORDING);
        intent.putExtra(ACTION_PARAM_FORMAT, outputfileformat);
        intent.putExtra(ACTION_PARAM_PATH, path);
        intent.putExtra(ACTION_PARAM_HIGH_QUALITY, highQuality);
        intent.putExtra(ACTION_PARAM_MAX_FILE_SIZE, maxFileSize);
        context.startService(intent);
    }

    public static void stopRecording(Context context) {
        Intent intent = new Intent(context, RecorderService.class);
        intent.putExtra(ACTION_NAME, ACTION_STOP_RECORDING);
        context.startService(intent);
    }

    public static int getMaxAmplitude() {
        return mRecorder == null ? 0 : mRecorder.getMaxAmplitude();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        localStopRecording("else");
    }
}
