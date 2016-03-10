package com.example.smena.sendmessage.whatsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import app.clientplanner.sendmessage.R;

public class WhatsApp {

    private Context mContext;
    private String mPackageName = "com.whatsapp";

    public WhatsApp(Context context) {
        this.mContext = context;
    }

    public void sendMsg(String text) {
        if (whatsappInstalledOrNot(mPackageName)) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setPackage(mPackageName);
            mContext.startActivity(sendIntent);
        } else {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.drawable.whatsapp)
                    .setTitle(R.string.not_install)
                    .setMessage(R.string.wish_install)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=" + mPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                mContext.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=" + mPackageName)));
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

    private boolean whatsappInstalledOrNot(String uri) {
        PackageManager pm = mContext.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
