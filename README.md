**_使用方法（usage）_**

**Step 1. Add the JitPack repository to your build file**

Add it in your root build.gradle at the end of repositories:

    allprojects {
        repositories {
            ...
            maven { url "https://jitpack.io" }
        }
    }
**Step 2. Add the dependency**

    dependencies {
        compile 'com.github.weilinhu:mylibaray:1.0'
    }


- 监听电池电量半分比的变化
       
```
//power 是当前电量百分比的值
 PowerUtils.registerPowerListener(this, new PowerConnectionReceiver.PowerConnectionReceiverListener() {
            @Override
            public void currentPower(int power) {
                Log.e(TAG, "currentPower: "+power );
                
            }
        });
```

- 监听网络情况

```

        NetWorkUtils.registerLister(this, new NetworkStateReceiver.NetworkStateReceiverListener() {
            @Override
            public void networkAvailable(String networkName) {
                Log.e(TAG, "networkAvailable: "+networkName );
               
            }

            @Override
            public void networkUnavailable() {
                Log.e(TAG, "networkUnavailable: " );
                

            }
        });
        
// wifi，3g,4g,2g,wap,unknown,disconnect;
// 任意时刻获取当前网络名称，disconnect为断网
        String networkTypeName = NetWorkUtils.getNetworkTypeName(this.getApplication());
        
        
```


- 手机信号监听
```
PhoneStateUtils.registerPhoneStateListener(this, new MyPhoneStateListener.MyPhoneStateListenerListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                Log.e(TAG, "onSignalStrengthsChanged: "+signalStrength
                        +"----  signalStrength.getGsmSignalStrength()"+signalStrength.getGsmSignalStrength());
                
            }
        });
```



- 读取通讯录列表

```
ArrayList<ContactEntity> contacts = ContactsUtils.getPhoneContacts(this);
        for (ContactEntity contact : contacts) {
            Log.e(TAG, "onCreate:contact.getName() =  " +contact.getName()+" contact.getNumber()  ="+contact.getNumber());
        }
```

- 获取手机号码(只能获取部分用户的号码)
```

        String phoneNumber = ContactsUtils.getPhoneNumber(this);
```
- 录音


```
//录音初始化
mRecorder = new Recorder(this);

//开始录音
mRecorder.startRecording(new RecorderReceiver.RecorderReceiverListener() {
                            @Override
                            public void recordStartSuccess() {
                                //开始录音成功
                                Log.e(TAG, "recordStartSuccess: " );
                            }

                            @Override
                            public void recordStartFailed() {
                              //开始录音失败
                                Log.e(TAG, "recordStartFailed: " );
                                
                            }

                            @Override
                            public void recordFiled() {
                            //录音过程中失败，1.呼叫或者收到电话2.app即将被杀死
                                Log.e(TAG, "recordFiled: " );
                            }
                        });
                    }
                    
                    
    //结束录音                
      mRecorder.stopRecording(); 
      
      
      结束和开始录音，不要重复掉用一个，交替掉用
```


- 释放资源

```

如果使用了相应功能，在相应生命周期中关闭对应功能


        //取消网络监听
        NetWorkUtils.unRegisterNetWork(this);
        
        //取消手机信号监听
        PhoneStateUtils.unRegisterPhoneStateListener(this);
        //取消手机电量监听
        PowerUtils.unRegisterPowerListener(this);

        //录音释放资源
         if (mRecorder!=null){
                     mRecorder.release(this);
         }



```

