package com.zhanshow.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SignalStrength;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhanle.upgrade.Error;
import com.zhanle.upgrade.UpgradeListener;
import com.zhanle.upgrade.UpgradeManager;
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
import com.zhanshow.weilinhu_mac.cocosapi.R;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView tv_power;
    private TextView tv_network;
    private TextView tv_singal;
    private Recorder mRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_power = (TextView) findViewById(R.id.textView);
        tv_network = (TextView) findViewById(R.id.textView2);
        tv_singal = (TextView) findViewById(R.id.textView3);


        //读取通讯录列表
        ArrayList<ContactEntity> contacts = ContactsUtils.getPhoneContacts(this);
        for (ContactEntity contact : contacts) {
            Log.e(TAG, "onCreate:contact.getName() =  " + contact.getName() + " contact.getNumber()  =" + contact.getNumber());
        }

        //获取手机号码
        String phoneNumber = ContactsUtils.getPhoneNumber(this);
        Log.e(TAG, "onCreate: " + phoneNumber);
        mRecorder = new Recorder(this);

        findViewById(R.id.button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentSignalStrength = PhoneStateUtils.getCurrentSignalStrength();
                        Log.e(TAG, "currentSignalStrength: "+currentSignalStrength);
                        int currentPower = PowerUtils.getCurrentPower();
                        Log.e(TAG, "currentPower: "+currentPower );

                        mRecorder.startRecording(new RecorderReceiver.RecorderReceiverListener() {
                            @Override
                            public void recordStartSuccess() {
                                Toast.makeText(getApplicationContext(), "recordStartSuccess",
                                        Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "recordStartSuccess: ");
                            }

                            @Override
                            public void recordStartFailed() {
                                Log.e(TAG, "recordStartFailed: ");
                                Toast.makeText(getApplicationContext(), "recordStartFailed",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void recordFiled() {
                                Toast.makeText(getApplicationContext(), "recordFiled",
                                        Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "recordFiled: ");
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


        String networkTypeName = NetWorkUtils.getNetworkTypeName(this.getApplication());


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: ");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: ");


        //监听电池电量半分比的变化
        PowerUtils.registerPowerListener(this, new PowerConnectionReceiver.PowerConnectionReceiverListener() {
            @Override
            public void currentPower(int power) {
                Log.e(TAG, "获取到currentPower: " + power);
                tv_power.setText("电量" + power);
            }
        });

        //监听网络情况
        NetWorkUtils.registerLister(this, new NetworkStateReceiver.NetworkStateReceiverListener() {
            @Override
            public void networkAvailable(String networkName) {
                Log.e("NetWorkUtils", "获取到networkAvailable: " + networkName);
                tv_network.setText(networkName);
            }
            @Override
            public void networkUnavailable() {
                Log.e("NetWorkUtils", "networkUnavailable ");
                tv_network.setText("Unavailable");

            }
        });


        PhoneStateUtils.registerPhoneStateListener(this, new MyPhoneStateListener.MyPhoneStateListenerListener() {
            @Override
            public void onSignalStrengthsChanged(int position) {
                Log.e(TAG, "获取到onSignalStrengthsChanged: " + position);
            }
        });

        Log.e(TAG, "onStart: done");
        UpgradeManager.getInstance().download(this,"http://openbox.mobilem.360.cn/index/d/sid/3886772", "ceshi", new UpgradeListener() {
            @Override
            public void onUpgradeListener(int progress, Error error) {
                Log.e(TAG, "onUpgradeListener: "+progress );
            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "onStop: ");
        NetWorkUtils.unRegisterNetWork(this);
        PhoneStateUtils.unRegisterPhoneStateListener(this);
        PowerUtils.unRegisterPowerListener(this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: ");
        if (mRecorder!=null){
            mRecorder.release(this);
        }

    }
}
