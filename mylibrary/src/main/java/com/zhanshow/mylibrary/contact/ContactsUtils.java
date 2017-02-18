package com.zhanshow.mylibrary.contact;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * @author weilinhu
 */

public class ContactsUtils {


    private static ArrayList<ContactEntity> mContacts = new ArrayList<ContactEntity>();

    private static final String[] PHONES_PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.PHOTO_ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID };

    /** 联系人显示名称 **/
    private static final int PHONES_DISPLAY_NAME_INDEX = 0;

    /** 电话号码 **/
    private static final int PHONES_NUMBER_INDEX = 1;

    public static ArrayList<ContactEntity> getPhoneContacts(Activity activity){
        ContentResolver resolver = activity.getContentResolver();
        try {
            // 获取手机联系人
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    PHONES_PROJECTION, null, null, null);
            if (phoneCursor != null) {

                while (phoneCursor.moveToNext()) {

                    // 得到手机号码
                    String phoneNumber = phoneCursor
                            .getString(PHONES_NUMBER_INDEX);
                    // 当手机号码为空的或者为空字段 跳过当前循环
                    if (TextUtils.isEmpty(phoneNumber))
                        continue;

                    // 得到联系人名称
                    String contactName = phoneCursor
                            .getString(PHONES_DISPLAY_NAME_INDEX);



                    ContactEntity mContact = new ContactEntity(contactName,
                            phoneNumber);
                    mContacts.add(mContact);
                }
                phoneCursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            return mContacts;
        }
    }


    public static String getPhoneNumber(Activity activity){
        TelephonyManager mTelephonyMgr = (TelephonyManager)activity.getSystemService(Context.TELEPHONY_SERVICE);
        String s = mTelephonyMgr.getLine1Number();
        return  TextUtils.isEmpty(s)?"":s;


    }



}
