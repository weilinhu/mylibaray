package com.zhanshow.weilinhu_mac.cocosapi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SignalStrength;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.zhanshow.mylibrary.contact.ContactEntity;
import com.zhanshow.mylibrary.contact.ContactsUtils;
import com.zhanshow.mylibrary.network.NetWorkUtils;
import com.zhanshow.mylibrary.network.NetworkStateReceiver;
import com.zhanshow.mylibrary.phonestate.MyPhoneStateListener;
import com.zhanshow.mylibrary.phonestate.PhoneStateUtils;
import com.zhanshow.mylibrary.power.PowerConnectionReceiver;
import com.zhanshow.mylibrary.power.PowerUtils;
import com.zhanshow.mylibrary.record.Recorder;
import com.zhanshow.mylibrary.record.RecorderReceiver;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tv1 = (TextView) findViewById(R.id.textView);
        final TextView tv2 = (TextView) findViewById(R.id.textView2);
        final TextView tv3 = (TextView) findViewById(R.id.textView3);



        //读取通讯录列表
        ArrayList<ContactEntity> contacts = ContactsUtils.getPhoneContacts(this);
        for (ContactEntity contact : contacts) {
            Log.e(TAG, "onCreate:contact.getName() =  " +contact.getName()+" contact.getNumber()  ="+contact.getNumber());
        }

        //获取手机号码
        String phoneNumber = ContactsUtils.getPhoneNumber(this);
        Log.e(TAG, "onCreate: "+phoneNumber );
        final Recorder mRecorder = new Recorder(this);

        findViewById(R.id.button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mRecorder.startRecording(new RecorderReceiver.RecorderReceiverListener() {
                            @Override
                            public void recordStartSuccess() {
                                Toast.makeText(getApplicationContext(), "recordStartSuccess",
                                        Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "recordStartSuccess: " );
                            }

                            @Override
                            public void recordStartFailed() {
                                Log.e(TAG, "recordStartFailed: " );
                                Toast.makeText(getApplicationContext(), "recordStartFailed",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void recordFiled() {
                                Toast.makeText(getApplicationContext(), "recordFiled",
                                        Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "recordFiled: " );
                            }
                        });
                    }
                });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecorder.stopRecording();
            }
        });
//        public static final String NETWORK_TYPE_WIFI = "wifi";
//        public static final String NETWORK_TYPE_3G = "3g";
//        public static final String NETWORK_TYPE_4G = "4g";
//        public static final String NETWORK_TYPE_2G = "2g";
//        public static final String NETWORK_TYPE_WAP = "wap";
//        public static final String NETWORK_TYPE_UNKNOWN = "unknown";
//        public static final String NETWORK_TYPE_DISCONNECT = "disconnect";

        String networkTypeName = NetWorkUtils.getNetworkTypeName(this.getApplication());


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: " );


        Log.e(TAG, "onStart: 注册电量回调" );
        //监听电池电量半分比的变化
        PowerUtils.registerPowerListener(this, new PowerConnectionReceiver.PowerConnectionReceiverListener() {
            @Override
            public void currentPower(int power) {
                Log.e(TAG, "获取到currentPower: "+power );
//                tv1.setText("电量"+power);
            }
        });

        Log.e(TAG, "onStart: 注册网络回调" );
        //监听网络情况
        NetWorkUtils.registerLister(this, new NetworkStateReceiver.NetworkStateReceiverListener() {
            @Override
            public void networkAvailable(String networkName) {
                Log.e(TAG, "获取到networkAvailable: "+networkName );
//                tv2.setText(networkName);
            }

            @Override
            public void networkUnavailable() {
                Log.e(TAG, "获取到networkUnavailable: " );
//                tv2.setText("networkUnavailable: ");

            }
        });


        Log.e(TAG, "onStart: 注册手机信号回调" );
        PhoneStateUtils.registerPhoneStateListener(this, new MyPhoneStateListener.MyPhoneStateListenerListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                Log.e(TAG, "获取到onSignalStrengthsChanged: "+signalStrength
                        +"----  signalStrength.getGsmSignalStrength()"+signalStrength.getGsmSignalStrength());
//                tv3.setText(signalStrength.getGsmSignalStrength()+"");
            }
        });

        Log.e(TAG, "onStart: done" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: " );
        NetWorkUtils.unRegisterNetWork(this);
        PhoneStateUtils.unRegisterPhoneStateListener(this);
        PowerUtils.unRegisterPowerListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );
        NetWorkUtils.unRegisterNetWork(this);
        PhoneStateUtils.unRegisterPhoneStateListener(this);
        PowerUtils.unRegisterPowerListener(this);

    }
}
