package com.example.smena.sendmessage.whatsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.example.smena.sendmessage.R;

public class WhatsApp extends AppCompatActivity {

    private Context ctx;
    private boolean isWhatsappInstalled;
    private String packageName = "com.whatsapp";

    public WhatsApp(Context context) {
        this.ctx = context;
        isWhatsappInstalled = whatsappInstalledOrNot(packageName);
    }

    public void sendMsg(String text) {
        if (isWhatsappInstalled) {
            //Uri uri = Uri.parse("smsto:" + reformatPhone(phone));
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/plain");
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setPackage(packageName);
            ctx.startActivity(sendIntent);
        } else {
            new AlertDialog.Builder(ctx)
                    .setIcon(R.drawable.whatsapp)
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
//                    .iconRes(R.drawable.whatsapp).limitIconToDefaultSize()
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

    private boolean whatsappInstalledOrNot(String uri) {
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
