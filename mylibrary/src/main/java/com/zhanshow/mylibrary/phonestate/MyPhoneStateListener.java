package com.zhanshow.mylibrary.phonestate;

import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weilinhu
 */

public class MyPhoneStateListener extends PhoneStateListener {

    protected List<MyPhoneStateListener.MyPhoneStateListenerListener> listeners = new ArrayList<>();
    private Object lock = new Object();

    private SignalStrength signalStrength;
    private static final String TAG = "MyPhoneStateListener";
    @Override
    public void onSignalStrengthsChanged(SignalStrength signalStrength) {
        super.onSignalStrengthsChanged(signalStrength);
        this.signalStrength = signalStrength;
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
        if (signalStrength!=null){
            listener.onSignalStrengthsChanged(signalStrength);
        }

    }

    public interface MyPhoneStateListenerListener {
        public void onSignalStrengthsChanged(SignalStrength signalStrength);
    }
}
