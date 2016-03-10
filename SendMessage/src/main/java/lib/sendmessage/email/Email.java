package lib.sendmessage.email;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class Email {

    private Context mContext;

    public Email(Context context) {
        this.mContext = context;
    }

    public void sendEmail(String address, String text) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.setType("plain/text");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, address); //кому
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Запись на процедуры"); //тема
            emailIntent.putExtra(Intent.EXTRA_TEXT, text);
            mContext.startActivity(Intent.createChooser(emailIntent,
                    "Отправка письма..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "У Вас не установлен почтовый клиент", Toast.LENGTH_LONG).show();
        }

    }

}
