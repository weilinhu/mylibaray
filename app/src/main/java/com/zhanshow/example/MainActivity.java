package com.zhanshow.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.zhanshow.download.DownloadConfig;
import com.zhanshow.download.DownloadManager;
import com.zhanshow.download.entity.DownloadEntry;
import com.zhanshow.download.notify.DataWatcher;
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
import com.zhanshow.ping.Task.InfoTask;
import com.zhanshow.ping.Task.PingTask;
import com.zhanshow.ping.Task.TraceTask;
import com.zhanshow.weilinhu_mac.cocosapi.BuildConfig;
import com.zhanshow.weilinhu_mac.cocosapi.R;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView tv_power;
    private TextView tv_network;
    private TextView tv_singal;
    private TextView ping;
    private TextView ping1;
    private Recorder mRecorder;
    DownloadEntry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_power = (TextView) findViewById(R.id.textView);
        tv_network = (TextView) findViewById(R.id.textView2);
        tv_singal = (TextView) findViewById(R.id.textView3);
        ping = (TextView) findViewById(R.id.ping);
        ping1 = (TextView) findViewById(R.id.ping1);


        //读取通讯录列表
        ArrayList<ContactEntity> contacts = ContactsUtils.getPhoneContacts(this);
        for (ContactEntity contact : contacts) {
            Log.e(TAG, "onCreate:contact.getName() =  " + contact.getName() + " contact.getNumber()  =" + contact.getNumber());
        }

        //获取手机号码
        String phoneNumber = ContactsUtils.getPhoneNumber(this);
        Log.e(TAG, "onCreate: " + phoneNumber);
        mRecorder = new Recorder(this);

//
        PingTask pingTask = new PingTask("www.baidu.com",ping);
        pingTask.doTask();


        TraceTask pingTask1 = new TraceTask(this,"www.baidu.com",ping1);
        pingTask1.doTask();

//        InfoTask pingTask = new InfoTask("www.baidu.com",ping);
//        pingTask.doTask();

        findViewById(R.id.button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int currentSignalStrength = PhoneStateUtils.getCurrentSignalStrength();
                        Log.e(TAG, "currentSignalStrength: " + currentSignalStrength);
                        int currentPower = PowerUtils.getCurrentPower();
                        Log.e(TAG, "currentPower: " + currentPower);

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



        //下载地址
        String url = "http://openbox.mobilem.360.cn/index/d/sid/3886772";
        DownloadManager.sApplicationId = BuildConfig.APPLICATION_ID;
        entry= new DownloadEntry(url);
        // 下载文件名称， 如果下载目录已.apk结尾，下载完成后自动掉起安装界面
        entry.name = "三国.apk";
//        DownloadManager.getInstance(MainActivity.this).add(entry);



        DownloadManager.getInstance(this).addObserver(new DataWatcher() {

            @Override
            public void onDataChanged(DownloadEntry data) {


                /**
                 *  idle,
                  waiting,
                  connecting,
                  downloading,
                  pause,
                  resume,
                  cancel,
                  done,
                  error;
                 */
                if (data.status == DownloadEntry.DownloadStatus.error){
                    Log.e(TAG, "error: "+ data.errorMsg);
                    return;
                }


                Log.e(TAG, "onDataChanged:status.percent =  " + data.percent+"\n"
                        +"name = " + data.name+"\n"
                        +"目录 = " +( DownloadConfig.DOWNLOAD_PATH + data.name));
            }
        });
//        UpgradeManager.getInstance().download(this,"http://openbox.mobilem.360.cn/index/d/sid/3886772", "通知名称", new UpgradeListener() {
//            @Override
//            public void onUpgradeListener(int progress, Error error) {
//
//                //进度小于o，说明下载错误
//                if (progress < 0){
//                    Log.e(TAG, "onUpgradeListener: "+error.getErrorMsg() );
//                    return;
//                }
//                //当前进度，到达 100  会自动跳往安装界面
//                Log.e(TAG, "onUpgradeListener: "+progress );
//            }
//        });
    }


    public void start(View v) {
        DownloadManager.getInstance(MainActivity.this).add(entry);

    }

    public void stop(View v) {
        DownloadManager.getInstance(MainActivity.this).pause(entry);
    }

    public void resume(View v) {
        DownloadManager.getInstance(MainActivity.this).resume(entry);
    }

    public void cancel(View v) {
        DownloadManager.getInstance(MainActivity.this).cancel(entry);
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
        DownloadManager.getInstance(this).removeObservers();
        if (mRecorder != null) {
            mRecorder.release(this);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: ");
//        DownloadManager.getInstance(this).addObserver(dataWatcher);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        DownloadManager.getInstance(this).removeObserver(dataWatcher);
    }
}
