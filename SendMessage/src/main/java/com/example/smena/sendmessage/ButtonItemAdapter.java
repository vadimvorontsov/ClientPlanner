package com.example.smena.sendmessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ArrayRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;


/**
 * Created by Вадим on 17.06.2015.
 */
class ButtonItemAdapter extends BaseAdapter implements View.OnClickListener {

    private Toast mToast;
    private final Context mContext;
    private final CharSequence[] mItems;

    static String phone;
    static String mail;

    ArrayList<String> phones = new ArrayList<>();
    ArrayList<String> emails = new ArrayList<>();

    public ButtonItemAdapter(Context context, @ArrayRes int arrayResId) {
        this(context, context.getResources().getTextArray(arrayResId));
    }

    private ButtonItemAdapter(Context context, CharSequence[] items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.length;
    }

    @Override
    public CharSequence getItem(int position) {
        return mItems[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = View.inflate(mContext, R.layout.dialog_customlistitem, null);
        ((TextView) convertView.findViewById(R.id.title)).setText(mItems[position]/* + " (" + position + ")"*/);
        Button button = (Button) convertView.findViewById(R.id.button);
        button.setTag(position);
        button.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        Integer index = (Integer) v.getTag();

        switch (index) {
            case 0:
                if (phones.size() > 1) {
                    showSingleChoice(phones, "sms");
//                  SMS sms = new SMS(mContext);
//                  sms.sendSMS(phone, "привет");
                } else if (phones.size() == 0) {
                    showToast(mContext.getString(R.string.no_phone));
                } else {
                    phone = phones.get(0);
//                  SMS sms = new SMS(mContext);
//                  sms.sendSMS(phone, "привет");

                }
                //showToast("SMS");
                break;
            case 1:
                if (phones.size() > 1) {
                    showSingleChoice(emails, "email");
//                    Email email = new Email(mContext);
//                    email.sendEmail(mail, "привет");
                } else if (emails.size() == 0) {
                    showToast(mContext.getString(R.string.no_email));
                } else {
                    mail = emails.get(0);
//                    Email email = new Email(mContext);
//                    email.sendEmail(mail, "привет");
                }

//                showToast("EMAIL");
                break;
            case 2:
                if (phones.size() > 1) {
                    showSingleChoice(phones, "whatsapp");
//                    WhatsApp whatsApp = new WhatsApp(mContext);
//                    whatsApp.sendMsg("привет", phone);
                } else if (phones.size() == 0) {
                    showToast(mContext.getString(R.string.no_phone));
                } else {
                    phone = phones.get(0);
//                    WhatsApp whatsApp = new WhatsApp(mContext);
//                    whatsApp.sendMsg("привет", phone);
                }

//                showToast("WHATSAPP");
                break;
            case 3:
                if (phones.size() > 1) {
                    showSingleChoice(phones, "viber");
//                    Viber viber = new Viber(mContext);
//                    viber.sendMsg("привет", phone);
                } else if (phones.size() == 0) {
                    showToast(mContext.getString(R.string.no_phone));
                } else {
                    phone = phones.get(0);
//                    Viber viber = new Viber(mContext);
//                    viber.sendMsg("привет", phone);
                }

//                showToast("VIBER");
                break;
        }
    }

    private void showSingleChoice(ArrayList<String> items, final String mode) {

        CharSequence[] charSequence = items.toArray(new CharSequence[items.size()]);

        new MaterialDialog.Builder(mContext)
                .title(R.string.choose_to_send)
                .items(charSequence)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {

                        if (mode.equals("sms") || mode.equals("whatsapp") || mode.equals("viber")) {
                            phone = text.toString();
                        } else if (mode.equals("email")) {
                            mail = text.toString();
                        }

                        showToast(which + ": " + text);
                        return true; // allow selection
                    }
                })
                .positiveText(R.string.choose)
                .show();
    }

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

}

