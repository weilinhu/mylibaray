package com.zhanshow.mylibrary.phonestate;

import android.app.Activity;
import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weilinhu
 */

public class MyPhoneStateListener extends PhoneStateListener {

    protected List<MyPhoneStateListener.MyPhoneStateListenerListener> listeners = new ArrayList<>();
    private Object lock = new Object();
    public static int sMark = -1;
    private TelephonyManager tel;
//    中国移动的是 46000
//    中国联通的是 46001
//    中国电信的是 46003
    private String STRNetworkOperator[] = { "46000", "46001", "46003" };
    private boolean is3Ghave = false;
    public static int sPosition;
    int signal;
    public MyPhoneStateListener(Activity activity) {
        tel = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
    }

    private SignalStrength signalStrength;
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        if(sMark <0)
        {
            getmark();
        }
        if (sMark == 0) {
            signal = signalStrength.getGsmSignalStrength();
            getLevel();
        } else if (sMark == 1) {
            signal = signalStrength.getCdmaDbm();
            getLevel();
        } else if (sMark == 2) {
            signal = signalStrength.getEvdoDbm();
            getLevel();
        }else {
            sPosition = 0;
        }

        notifyStateToAll();
    }


    public void addListener(MyPhoneStateListener.MyPhoneStateListenerListener l) {
        synchronized(lock) {
            listeners.add(l);
        }
    }

    public void removeListeners() {
        synchronized(lock) {
            listeners.clear();
        }
    }

    private void notifyStateToAll() {
        synchronized(lock) {
            for (MyPhoneStateListener.MyPhoneStateListenerListener listener : listeners) {
                notifyState(listener);
            }

        }
    }
    private void notifyState(MyPhoneStateListener.MyPhoneStateListenerListener listener) {
        if (listener!=null){
            listener.onSignalStrengthsChanged(sPosition);
        }

    }

    public interface MyPhoneStateListenerListener {
        public void onSignalStrengthsChanged(int singnaStrength);
    }

    private void getmark()//得到当前电话卡的归属运营商
    {
        String strNetworkOperator = tel.getNetworkOperator();
        if (strNetworkOperator != null) {
            for (int i = 0; i < 3; i++) {
                if (strNetworkOperator.equals(STRNetworkOperator[i])) {
                    sMark = i;
                    break;
                }
            }
        } else {
            sMark = -1;
        }
    }





    private void getLevel() {
        // TODO Auto-generated method stub
        if (sMark == 2) {//电信3g信号强度的分类，可以按照ui自行划分等级
            if (signal >= -65)
                sPosition = 5;
            else if (signal >= -75)
                sPosition = 4;
            else if (signal >= -85)
                sPosition = 3;
            else if (signal >= -95)
                sPosition = 2;
            else if (signal >= -105)
                sPosition = 1;
            else
                sPosition = 0;
        }
        if (sMark == 1) {//联通3g信号划分
            if (signal >= -75)
                sPosition = 5;
            else if (signal >= -80)
                sPosition = 4;
            else if (signal >= -85)
                sPosition = 3;
            else if (signal >= -95)
                sPosition = 2;
            else if (signal >= -100)
                sPosition = 1;
            else
                sPosition = 0;
        }
        if (sMark == 0) {//移动信号的划分，这个不是很确定是2g还是3g
            if (signal <= 2 || signal == 99)
                sPosition = 0;
            else if (signal >= 12)
                sPosition = 5;
            else if (signal >= 10)
                sPosition = 4;
            else if (signal >= 8)
                sPosition = 3;
            else if (signal >= 5)
                sPosition = 2;
            else
                sPosition = 1;
        }
    }


}
