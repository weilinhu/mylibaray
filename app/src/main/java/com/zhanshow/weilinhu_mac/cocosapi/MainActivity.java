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

        //监听电池电量半分比的变化
        PowerUtils.registerPowerListener(this, new PowerConnectionReceiver.PowerConnectionReceiverListener() {
            @Override
            public void currentPower(int power) {
                Log.e(TAG, "currentPower: "+power );
                tv1.setText("电量"+power);
            }
        });

        //监听网络情况
        NetWorkUtils.registerLister(this, new NetworkStateReceiver.NetworkStateReceiverListener() {
            @Override
            public void networkAvailable(String networkName) {
                Log.e(TAG, "networkAvailable: "+networkName );
                tv2.setText(networkName);
            }

            @Override
            public void networkUnavailable() {
                Log.e(TAG, "networkUnavailable: " );
                tv2.setText("networkUnavailable: ");

            }
        });


        PhoneStateUtils.registerPhoneStateListener(this, new MyPhoneStateListener.MyPhoneStateListenerListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                Log.e(TAG, "onSignalStrengthsChanged: "+signalStrength
                        +"----  signalStrength.getGsmSignalStrength()"+signalStrength.getGsmSignalStrength());
                tv3.setText(signalStrength.getGsmSignalStrength()+"");
            }
        });

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
