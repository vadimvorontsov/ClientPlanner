package com.example.smena.sendmessage.sms;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import java.util.ArrayList;

public class SMS {

    private final static String SENT = "SMS_SENT", DELIVERED = "SMS_DELIVERED";
    private Context mContext;

    public SMS(Context context) {
        this.mContext = context;
    }

    public void sendSMS(String number, String text) {

        SmsManager smsManager = SmsManager.getDefault();

        SendReceipt sendReceipt = new SendReceipt();
        DeliveryReceipt deliveryReceipt = new DeliveryReceipt();
        mContext.registerReceiver(sendReceipt, new IntentFilter(SENT));
        mContext.registerReceiver(deliveryReceipt, new IntentFilter(DELIVERED));

        ArrayList<PendingIntent> sentPI = new ArrayList<>(1);
        sentPI.add(PendingIntent.getBroadcast(mContext, 0, new Intent(SENT), 0));
        ArrayList<PendingIntent> deliveredPI = new ArrayList<>(1);
        deliveredPI.add(PendingIntent.getBroadcast(mContext, 0, new Intent(DELIVERED), 0));

        ArrayList<String> messages = smsManager.divideMessage(text);

        smsManager.sendMultipartTextMessage("+" + reformatPhone(number), null, messages, sentPI, deliveredPI);
    }

    private String reformatPhone(String phone) {
        return phone.replaceAll("\\D", "");
    }

}
