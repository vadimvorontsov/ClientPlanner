package lib.sendmessage.viber;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import app.sendmessage.R;


public class Viber {

    private Context mContext;
    private boolean mIsViberInstalled;
    private String mPackageName = "com.viber.voip";

    public Viber(Context context) {
        this.mContext = context;
        mIsViberInstalled = viberInstalledOrNot(mPackageName);
    }

    public void sendMsg(String text) {
        if (mIsViberInstalled) {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            sendIntent.setPackage(mPackageName);
            mContext.startActivity(sendIntent);
        } else {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.drawable.viber)
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

    private boolean viberInstalledOrNot(String uri) {
        PackageManager pm = mContext.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}