package com.example.smena.sendmessage.viber;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;


import com.example.smena.sendmessage.R;

public class Viber extends Activity {

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
//            new MaterialDialog.Builder(ctx)
//                    .iconRes(R.drawable.viber).limitIconToDefaultSize()
//                    .title(R.string.not_install)
//                    .content(R.string.wish_install)
//                    .positiveText(R.string.yes)
//                    .negativeText(R.string.no)
//                    .callback(new MaterialDialog.ButtonCallback() {
//                        @Override
//                        public void onPositive(MaterialDialog dialog) {
//                            try {
//                                ctx.startActivity(new Intent(Intent.ACTION_VIEW,
//                                        Uri.parse("market://details?id=" + packageName)));
//                            } catch (android.content.ActivityNotFoundException anfe) {
//                                ctx.startActivity(new Intent(Intent.ACTION_VIEW,
//                                        Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
//                            }
//                        }
//
//                        @Override
//                        public void onNegative(MaterialDialog dialog) {
//                            super.onNegative(dialog);
//                        }
//                    })
//                    .show();
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