package com.example.smena.sendmessage.sms;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;

import java.util.List;

/**
 * Created by smena on 12.06.2015.
 */
public class SMS extends Activity {

    private final static String SENT = "SMS_SENT", DELIVERED = "SMS_DELIVERED";
    Context ctx;

    public SMS(Context context) {
        this.ctx = context;
    }

    public void sendSMS(String number, String text) {

        SmsManager smsManager = SmsManager.getDefault();

        SendReceipt sendReceipt = new SendReceipt();
        DeliveryReceipt deliveryReceipt = new DeliveryReceipt();
        registerReceiver(sendReceipt, new IntentFilter(SENT));
        registerReceiver(deliveryReceipt, new IntentFilter(DELIVERED));

        PendingIntent sentPI = PendingIntent.getBroadcast(ctx, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(ctx, 0, new Intent(DELIVERED), 0);

        List<String> messages = smsManager.divideMessage(text);

        for (String message : messages) {
            smsManager.sendTextMessage(number, null, message, sentPI, deliveredPI);
        }
    }

}
