package com.example.smena.sendmessage.sms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.smena.sendmessage.R;

public class SendReceipt extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String msg;
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                msg = context.getResources().getString(R.string.sending);
                break;
            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                msg = context.getResources().getString(R.string.send_error_unknown);
                break;
            case SmsManager.RESULT_ERROR_NO_SERVICE:
                msg = context.getResources().getString(R.string.send_error_service);
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
                msg = context.getResources().getString(R.string.send_error_module);
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
                msg = context.getResources().getString(R.string.send_error_pdu);
                break;
            default:
                msg = context.getResources().getString(R.string.send_error_unknown);
        }

        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

    }
}

