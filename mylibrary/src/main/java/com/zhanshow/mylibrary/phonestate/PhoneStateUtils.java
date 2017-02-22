package com.zhanshow.mylibrary.phonestate;

import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;

/**
 * @author weilinhu
 */

public class PhoneStateUtils {

    private static  MyPhoneStateListener myPhoneStateListener;

    public static void registerPhoneStateListener(Activity activity,MyPhoneStateListener.MyPhoneStateListenerListener listener ){
        myPhoneStateListener = new MyPhoneStateListener(activity);
        myPhoneStateListener.addListener(listener);
        TelephonyManager telephonyManager = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(myPhoneStateListener , PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
    }



    public static void unRegisterPhoneStateListener(Activity activity){
        TelephonyManager telephonyManager = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(myPhoneStateListener , PhoneStateListener.LISTEN_NONE);
    }

    public static int getCurrentSignalStrength(){
        return MyPhoneStateListener.sPosition;
    }

}
