package com.zhanshow.mylibrary.power;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author weilinhu
 */

public class PowerConnectionReceiver extends BroadcastReceiver {
    private static final String TAG = "PowerConnectionReceiver";
    protected List<PowerConnectionReceiver.PowerConnectionReceiverListener> listeners = new ArrayList<>();
    private Object lock = new Object();
    private int currentpower;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
            Bundle bundle = intent.getExtras();
            // 获取当前电量
            int current = bundle.getInt("level");
            // 获取总电量
            int total = bundle.getInt("scale");
            currentpower = current * 100 / total;

            notifyStateToAll();
        }

    }

    public void addListener(PowerConnectionReceiver.PowerConnectionReceiverListener l) {
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
            for (PowerConnectionReceiver.PowerConnectionReceiverListener listener : listeners) {
                notifyState(listener);
            }

        }
    }
    private void notifyState(PowerConnectionReceiver.PowerConnectionReceiverListener listener) {

        listener.currentPower(currentpower);
    }

    public interface PowerConnectionReceiverListener {
        public void currentPower(int power);
    }
}
