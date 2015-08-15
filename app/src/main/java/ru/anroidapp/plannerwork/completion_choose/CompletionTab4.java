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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
    ArrayList<String> mPhones;
    String phone;
    ArrayList<String> mEmails;
    String email;
    RelativeLayout relativeLayout;
    private View mData;
    private EditText editMsg;
    LayoutInflater mInflater;

    Animation fadeIn, fadeOut;
    LinearLayout mDataLayout;
    boolean mAlpha = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mInflater = inflater;

        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.completion_tab4, container, false);

        mDataLayout = (LinearLayout) relativeLayout.findViewById(R.id.data_layout);
        mData = inflater.inflate(R.layout.layout_tab4, null);
        mDataLayout.addView(mData);

        fa = super.getActivity();
        mMetaData = (MetaData) getArguments().getSerializable(MetaData.TAG);

        months = getResources().getStringArray(R.array.months);

        editMsg = (EditText) mInflater.inflate(R.layout.edit_msg_tab4, null);

//        fadeIn = AnimationUtils.loadAnimation(fa, R.anim.fade_in);
//        fadeOut = AnimationUtils.loadAnimation(fa, R.anim.fade_out);
//        fadeIn.setAnimationListener(fadeInAnimationListener);
//        fadeOut.setAnimationListener(fadeOutAnimationListener);

        btnClickOK = (Button) relativeLayout.findViewById(R.id.BtnCompletionOK);
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

        mPhones = mMetaData.getClientPhones();
        mEmails = mMetaData.getClientEmails();

        LayoutInflater inflater = (LayoutInflater) fa.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.send_message, null);

        LinearLayout choosePhoneEmail = (LinearLayout) view.findViewById(R.id.choose_phone_email_layout);
        LinearLayout choosePhone = (LinearLayout) choosePhoneEmail.findViewById(R.id.choose_phone_layout);
        LinearLayout chooseEmail = (LinearLayout) choosePhoneEmail.findViewById(R.id.choose_email_layout);

        if (mPhones.size() > 1) {
            choosePhone.addView(setupPhonesSpinner());
        } else {
            choosePhone.addView(setupPhoneTextView());
        }

        if (mEmails.size() > 1) {
            chooseEmail.addView(setupEmailsSpinner());
        } else {
            chooseEmail.addView(setupEmailTextView());
        }

        //final EditText editTextMsg = (EditText) view.findViewById(R.id.send_msg_edit_text);
        final Button btnSendSms = (Button) view.findViewById(R.id.send_sms_btn);
        final Button btnSendEmail = (Button) view.findViewById(R.id.send_email_btn);
        final Button btnSendWhatsApp = (Button) view.findViewById(R.id.send_whatsapp_btn);
        final Button btnSendViber = (Button) view.findViewById(R.id.send_viber_btn);

        btnSendSms.setOnClickListener(sendSmsListener);
        btnSendEmail.setOnClickListener(sendEmailListener);
        btnSendWhatsApp.setOnClickListener(sendWhatsAppListener);
        btnSendViber.setOnClickListener(sendViberListener);


        //editTextMsg.setText(mTextMsg);

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        setupData(relativeLayout);
        super.onCreateOptionsMenu(menu, inflater);

    }

    private Spinner setupPhonesSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(fa,
                R.layout.my_spinner, mPhones);
        adapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);
        Spinner spinner = new Spinner(fa);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                phone = reformatPhone(mPhones.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                phone = reformatPhone(mPhones.get(0));
            }
        });

        return spinner;
    }

    private Spinner setupEmailsSpinner() {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(fa,
                R.layout.my_spinner, mEmails);
        adapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);
        Spinner spinner = new Spinner(fa);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                email = mEmails.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                email = mEmails.get(0);
            }
        });

        return spinner;
    }

    private TextView setupPhoneTextView() {

        TextView phoneTextView = new TextView(fa);
        if (mPhones == null || mPhones.isEmpty()) {
            phoneTextView.setText("неизвестно");
            phone = null;
        } else {
            phone = mPhones.get(0);
            phoneTextView.setText(phone);
        }
        phoneTextView.setTextColor(getResources().getColor(R.color.ColorPrimary));
        phoneTextView.setTextSize(16);

        return phoneTextView;
    }

    private TextView setupEmailTextView() {

        TextView emailTextView = new TextView(fa);
        if (mPhones == null || mEmails.isEmpty()) {
            emailTextView.setText("неизвестно");
            email = null;
        } else {
            email = mEmails.get(0);
            emailTextView.setText(email);
        }
        emailTextView.setTextColor(getResources().getColor(R.color.ColorPrimary));
        emailTextView.setTextSize(16);

        return emailTextView;
    }

    private void setupData(RelativeLayout relativeLayout) {
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

        mDataLayout = (LinearLayout) relativeLayout.findViewById(R.id.data_layout);
        LinearLayout viewMessageBtn = (LinearLayout) relativeLayout.findViewById(R.id.view_message_layout);
        viewMessageBtn.setOnClickListener(viewMsgBtnListener);

    }

    private void setupDataLayout() {
        mDataLayout.removeAllViews();
        mDataLayout.addView(mData);
        setupData(relativeLayout);
    }

    private void setupViewMsgLayout() {
        mDataLayout.removeAllViews();

        mTextMsg = "Здравствуйте, " + mMetaData.getClientName() + "!" +
                "Вы записаны на " + mMetaData.getProcedureName() + " " +
                mMetaData.getDay() + " " + months[mMetaData.getMonth()].toLowerCase() + "," +
                "время " + mMetaData.getHourStart() + ":" + mMetaData.getMinuteStart() + "." +
                "Цена " + mMetaData.getProcedurePrice();
        editMsg.setText(mTextMsg);
        mDataLayout.addView(editMsg);
    }


    View.OnClickListener viewMsgBtnListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            if (!mAlpha) {
                //mData.startAnimation(fadeIn);
                setupViewMsgLayout();
                mAlpha = true;
            } else {
                //editMsg.startAnimation(fadeOut);
                setupDataLayout();
                mAlpha = false;
            }

        }
    };

    Animation.AnimationListener fadeInAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    Animation.AnimationListener fadeOutAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    View.OnClickListener sendViberListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (phone != null || !phone.isEmpty()) {
                Viber viber = new Viber(fa);
                viber.sendMsg(mTextMsg, mPhonesForCall.get(0));
            } else {
                showToast(fa.getString(com.example.smena.sendmessage.R.string.no_phone));
            }
        }
    };

    View.OnClickListener sendWhatsAppListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (phone != null || !phone.isEmpty()) {
                WhatsApp whatsApp = new WhatsApp(fa);
                whatsApp.sendMsg(mTextMsg, mPhonesForCall.get(0));
            } else {
                showToast(fa.getString(com.example.smena.sendmessage.R.string.no_phone));
            }
        }
    };

    View.OnClickListener sendEmailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ArrayList<String> emails = mMetaData.getClientEmails();
            if (email != null || !email.isEmpty()) {
                Email email = new Email(fa);
                email.sendEmail("voronczov-vadim@mail.ru", mTextMsg);
            } else {
                showToast(fa.getString(com.example.smena.sendmessage.R.string.no_email));
            }
        }
    };

    View.OnClickListener sendSmsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (phone != null || !phone.isEmpty()) {
                SMS sms = new SMS(fa);
                sms.sendSMS(mPhonesForCall.get(0), mTextMsg);
            } else if (mPhonesForCall.size() == 0) {
                showToast(fa.getString(com.example.smena.sendmessage.R.string.no_phone));
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

    private String reformatPhone(String phone) {
        if (phone.startsWith("+7")) {
            phone = "+" + phone.replaceAll("\\D", "");
        } else if (phone.startsWith("8")) {
            phone = "+7" + phone.substring(0, 1).replaceAll("\\D", "");
        }
        return phone;
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
