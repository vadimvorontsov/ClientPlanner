package com.example.smena.sendmessage.viber;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.smena.sendmessage.R;

public class Viber extends Activity {

    private Context ctx;
    private boolean isViberInstalled = viberInstalledOrNot("com.viber.voip");

    public Viber(Context context) {
        this.ctx = context;
    }

    public void sendMsg(String text, String phone) {
        if (isViberInstalled) {
            Uri uri = Uri.parse("smsto:" + phone);
            Intent sendIntent = new Intent(Intent.ACTION_SENDTO, uri);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            sendIntent.setPackage("com.viber.voip");
            startActivity(sendIntent);
        } else {
            new MaterialDialog.Builder(ctx)
                    .iconRes(000).limitIconToDefaultSize()
                    .title(R.string.not_install)
                    .content(R.string.wish_install)
                    .positiveText(R.string.yes)
                    .negativeText(R.string.no)
                    .callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            Uri uri = Uri.parse("market://details?id=com.viber.voip");
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(goToMarket);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                        }
                    })
                    .show();
        }
    }

    private boolean viberInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
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