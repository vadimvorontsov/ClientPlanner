package com.example.smena.sendmessage.whatsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.smena.sendmessage.R;

public class WhatsApp extends Activity {

    private Context ctx;
    private boolean isWhatsappInstalled;
    private String packageName = "com.whatsapp";

    public WhatsApp(Context context) {
        this.ctx = context;
        isWhatsappInstalled = whatsappInstalledOrNot(packageName);
    }

    public void sendMsg(String text, String phone) {
        if (isWhatsappInstalled) {
            Uri uri = Uri.parse("smsto:" + phone);
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.whatsapp");
            ctx.startActivity(sendIntent);
        } else {
            new MaterialDialog.Builder(ctx)
                    .iconRes(R.drawable.whatsapp).limitIconToDefaultSize()
                    .title(R.string.not_install)
                    .content(R.string.wish_install)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            try {
                                ctx.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("market://details?id=" + packageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                ctx.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                        }
                    })
                    .show();
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
