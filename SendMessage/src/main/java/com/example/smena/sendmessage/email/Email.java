package com.example.smena.sendmessage.email;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by smena on 12.06.2015.
 */
public class Email extends Activity {

    Context ctx;
    final Intent emailIntent = new Intent(Intent.ACTION_SEND);

    public Email(Context context) {
        this.ctx = context;
    }

    public void sendEmail(String address, String text) {
        try {
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, address); //кому
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Запись на процедуры"); //тема
            emailIntent.putExtra(Intent.EXTRA_TEXT, text);
            emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //???

            ctx.startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(ctx, "У Вас не установлен почтовый клиент", Toast.LENGTH_LONG).show();
        }

    }

}
