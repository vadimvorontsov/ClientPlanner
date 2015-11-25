package com.example.smena.sendmessage.viber;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;


import com.example.smena.sendmessage.R;

public class Viber extends AppCompatActivity {

    private Context ctx;
    private boolean isViberInstalled;
    private String packageName = "com.viber.voip";

    public Viber(Context context) {
        this.ctx = context;
        isViberInstalled = viberInstalledOrNot(packageName);
    }

    public void sendMsg(String text) {
        if (isViberInstalled) {
            //Uri uri = Uri.parse("smsto:" + phone);
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            sendIntent.setPackage(packageName);
            ctx.startActivity(sendIntent);
        } else {
            new AlertDialog.Builder(ctx)
                    .setIcon(R.drawable.viber)
                    .setTitle(R.string.not_install)
                    .setMessage(R.string.wish_install)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                ctx.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=" + packageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                ctx.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                            }
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }
    }

    private boolean viberInstalledOrNot(String uri) {
        PackageManager pm = ctx.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }


}