package com.example.smena.sendmessage.sms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import app.clientplanner.sendmessage.R;

public class DeliveryReceipt extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg;
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                msg = context.getResources().getString(R.string.delivered);
                break;
            case Activity.RESULT_CANCELED:
                msg = context.getResources().getString(R.string.undelivered);
                break;
            default:
                msg = context.getResources().getString(R.string.delivery_unknown);
        }

        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
