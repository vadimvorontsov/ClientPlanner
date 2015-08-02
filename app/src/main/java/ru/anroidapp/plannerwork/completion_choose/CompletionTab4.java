package ru.anroidapp.plannerwork.completion_choose;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smena.clientbase.procedures.Sessions;
import com.example.smena.sendmessage.email.Email;
import com.example.smena.sendmessage.sms.SMS;
import com.example.smena.sendmessage.viber.Viber;
import com.example.smena.sendmessage.whatsapp.WhatsApp;

import java.util.ArrayList;

import ru.anroidapp.plannerwork.MainActivity;
import ru.anroidapp.plannerwork.MetaData;
import ru.anroidapp.plannerwork.R;

/**
 * Created by Артём on 22.07.2015.
 */
public class CompletionTab4 extends Fragment {

    private static final String TAG = "CompletionTab4";
    int  CURRNT_XML_FILE = 1;

    TextView clientNameTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView procedureNameTextView;
    TextView procedurePriceTextView;
    TextView procedureNoteTextView;
    Button btnClickOK;

    String[] months;

    FragmentActivity fa;
    MetaData mMetaData;

    Toast mToast;
    String mTextMsg;
    ArrayList<String> mPhonesForCall;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.completion_tab4, container, false);
        fa = super.getActivity();
        mMetaData = (MetaData) getArguments().getSerializable(MetaData.TAG);

        months = getResources().getStringArray(R.array.months);
        setupViews(relativeLayout);

        btnClickOK.setOnClickListener(oclBtnOK);

        setHasOptionsMenu(true);
        return relativeLayout;
    }

    @Override
    public void onDetach() {
        mMetaData = null;
        super.onDetach();
    }

    View.OnClickListener oclBtnOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Resources resources = getResources();

            Sessions sessions = new Sessions(fa);
            long sessinonId = sessions.addSession(mMetaData.getClientName(), mMetaData.getProcedureName(),
                    mMetaData.getProcedurePrice(), mMetaData.getProcedureNote(),
                    "" + mMetaData.getYear() + "-" + mMetaData.getMonth() + "-" + mMetaData.getDay() +
                            " " + mMetaData.getHourStart() + ":" + mMetaData.getMinuteStart(),
                    "" + mMetaData.getYear() + "-" + mMetaData.getMonth() + "-" + mMetaData.getDay() +
                            " " + mMetaData.getHourEnd() + ":" + mMetaData.getMinuteEnd(),
                    mMetaData.getClientPhones().get(0), mMetaData.getClientEmails().get(0));
            if (sessinonId != -1) {
                sendMsgView().show();
            } else {
                Toast.makeText(fa, resources.getString(R.string.end_error), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private AlertDialog.Builder sendMsgView() {

        mPhonesForCall = new ArrayList<>();
        for (String phone : mMetaData.getClientPhones()) {
            mPhonesForCall.add("+" + phone.replaceAll("\\D", ""));
        }

        LayoutInflater inflater = (LayoutInflater) fa.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.send_message, null);

        final EditText editTextMsg = (EditText) view.findViewById(R.id.send_msg_edit_text);
        final Button btnSendSms = (Button) view.findViewById(R.id.send_sms_btn);
        final Button btnSendEmail = (Button) view.findViewById(R.id.send_email_btn);
        final Button btnSendWhatsApp = (Button) view.findViewById(R.id.send_whatsapp_btn);
        final Button btnSendViber = (Button) view.findViewById(R.id.send_viber_btn);

        btnSendSms.setOnClickListener(sendSmsListener);
        btnSendEmail.setOnClickListener(sendEmailListener);
        btnSendWhatsApp.setOnClickListener(sendWhatsAppListener);
        btnSendViber.setOnClickListener(sendViberListener);

        mTextMsg = "Здравствуйте, " + mMetaData.getClientName() + "!" +
                "Вы записаны на " + mMetaData.getProcedureName() + " " +
                mMetaData.getDay() + " " + months[mMetaData.getMonth()].toLowerCase() + "," +
                "время " + mMetaData.getHourStart() + ":" + mMetaData.getMinuteStart() + "." +
                "Цена " + mMetaData.getProcedurePrice();
        editTextMsg.setText(mTextMsg);

        AlertDialog.Builder builder = new AlertDialog.Builder(fa);
        builder.setView(view)
                .setCancelable(false);
        builder.setNegativeButton(R.string.end_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startActivity(new Intent(fa, MainActivity.class));
                fa.finish();
            }
        });

        return builder;

    }

    private void setupViews(RelativeLayout relativeLayout) {
        clientNameTextView = (TextView) relativeLayout.findViewById(R.id.client_name_textview);
        clientNameTextView.setText(mMetaData.getClientName());
        dateTextView = (TextView) relativeLayout.findViewById(R.id.date_textview);
        dateTextView.setText(mMetaData.getDay() + " " + months[mMetaData.getMonth()]
                + " " + mMetaData.getYear());
        timeTextView = (TextView) relativeLayout.findViewById(R.id.time_textview);

        timeTextView.setText("c " + addedNullInt(mMetaData.getHourStart()) + ":" +
                addedNullInt(mMetaData.getMinuteStart()) + " по " + addedNullInt(mMetaData.getHourEnd()) +
                ":" + addedNullInt(mMetaData.getMinuteEnd()));
        procedureNameTextView = (TextView) relativeLayout.findViewById(R.id.procedure_name_textview);
        procedureNameTextView.setText(mMetaData.getProcedureName());
        procedurePriceTextView = (TextView) relativeLayout.findViewById(R.id.procedure_price_textview);
        procedurePriceTextView.setText("" + mMetaData.getProcedurePrice());
        procedureNoteTextView = (TextView) relativeLayout.findViewById(R.id.procedure_note_textview);
        procedureNoteTextView.setText(mMetaData.getProcedureNote());

        btnClickOK = (Button) relativeLayout.findViewById(R.id.BtnCompletionOK);
    }

    View.OnClickListener sendViberListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPhonesForCall.size() > 1) {
                Viber viber = new Viber(fa);
                viber.sendMsg(mTextMsg, /*mPhonesForCall.get(0)*/"+79254446525");
            } else if (mPhonesForCall.size() == 0) {
                showToast(fa.getString(com.example.smena.sendmessage.R.string.no_phone));
            } else {
                Viber viber = new Viber(fa);
                viber.sendMsg(mTextMsg, /*mPhonesForCall.get(0)*/"+79254446525");
            }
        }
    };

    View.OnClickListener sendWhatsAppListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPhonesForCall.size() > 1) {
                WhatsApp whatsApp = new WhatsApp(fa);
                whatsApp.sendMsg(mTextMsg, /*mPhonesForCall.get(0)*/"+79254446525");
            } else if (mPhonesForCall.size() == 0) {
                showToast(fa.getString(com.example.smena.sendmessage.R.string.no_phone));
            } else {
                WhatsApp whatsApp = new WhatsApp(fa);
                whatsApp.sendMsg(mTextMsg, /*mPhonesForCall.get(0)*/"+79254446525");
            }
        }
    };

    View.OnClickListener sendEmailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<String> emails = mMetaData.getClientEmails();
            if (emails == null || emails.isEmpty()) {
                showToast(fa.getString(com.example.smena.sendmessage.R.string.no_email));
            } else {
                if (emails.size() > 1) {
                    Email email = new Email(fa);
                    email.sendEmail("voronczov-vadim@mail.ru", mTextMsg);
                } else {
                    Email email = new Email(fa);
                    email.sendEmail("voronczov-vadim@mail.ru", mTextMsg);
                }
            }
        }
    };

    View.OnClickListener sendSmsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPhonesForCall.size() > 1) {
                SMS sms = new SMS(fa);
                sms.sendSMS(/*mPhonesForCall.get(0)*/"+79254446525", mTextMsg);
            } else if (mPhonesForCall.size() == 0) {
                showToast(fa.getString(com.example.smena.sendmessage.R.string.no_phone));
            } else {
                SMS sms = new SMS(fa);
                sms.sendSMS(/*mPhonesForCall.get(0)*/"+79254446525", mTextMsg);
            }
        }
    };

    private String addedNullInt(int cellTime) {
        String strTime;

        if (cellTime < 10)
            strTime = "0" + cellTime;
        else
            strTime = "" + cellTime;

        return strTime;
    }

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(fa, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

}
