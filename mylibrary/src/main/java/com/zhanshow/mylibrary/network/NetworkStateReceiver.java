package com.zhanshow.mylibrary.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qyvideo on 10/8/16.
 */
public class NetworkStateReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkStateReceiver";
    public static final String CONNECTIVITY_ACTION_LOLLIPOP = "com.example.CONNECTIVITY_ACTION_LOLLIPOP";

    protected List<NetworkStateReceiverListener> listeners = new ArrayList<>();
    protected Boolean connected;
    private Object lock = new Object();

    public NetworkStateReceiver() {
        connected = null;
    }

    public void onReceive(Context context, Intent intent) {


        final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            connected = true;
        }else {
            connected =false;

        }


        notifyStateToAll();
    }



    private void notifyStateToAll() {
        synchronized(lock) {
            for (NetworkStateReceiverListener listener : listeners) {
                notifyState(listener);
            }

        }
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if(connected == null || listener == null)
            return;

        if(connected)
            listener.networkAvailable(NetWorkUtils.getNetworkTypeName(LibraryConfig.getInstance().getContext()));
        else
            listener.networkUnavailable();
    }

    public void addListener(NetworkStateReceiverListener l) {
        synchronized(lock) {
            listeners.add(l);
            notifyState(l);
        }
    }

    public void removeListeners() {
        synchronized(lock) {
            listeners.clear();
        }
    }

    public interface NetworkStateReceiverListener {
        public void networkAvailable(String networkName);
        public void networkUnavailable();
    }
}