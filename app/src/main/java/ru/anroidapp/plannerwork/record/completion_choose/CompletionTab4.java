package ru.anroidapp.plannerwork.record.completion_choose;

import android.app.Activity;
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
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smena.clientbase.procedures.Clients;
import com.example.smena.clientbase.procedures.Sessions;
import com.example.smena.sendmessage.email.Email;
import com.example.smena.sendmessage.sms.SMS;
import com.example.smena.sendmessage.viber.Viber;
import com.example.smena.sendmessage.whatsapp.WhatsApp;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import ru.anroidapp.plannerwork.MetaData;
import ru.anroidapp.plannerwork.R;
import ru.anroidapp.plannerwork.main_activity.MainActivity;

public class CompletionTab4 extends Fragment {

    private static final String TAG = "CompletionTab4";
    int iMessage = 0;

    TextView clientNameTextView;
    TextView dateTextView;
    TextView timeTextView;
    TextView procedureNameTextView;
    TextView procedurePriceTextView;
    TextView procedureNoteTextView;
    Button btnClickOK, btnMessage;


    FragmentActivity fa;

    Toast mToast;
    String mTextMsg;
    ArrayList<String> mPhones;
    String phone;
    ArrayList<String> mEmails;
    String email;
    RelativeLayout relativeLayout;
    LayoutInflater mInflater;
    LinearLayout mDataLayout, mDataAllLayout;
    boolean mAlpha = false;
    MetaData mMetaData;
    String mClientName;
    String mYear;
    String mMonthName;
    String mMonthNumb;
    String mDay;
    String mHourStart;
    String mHourEnd;
    String mMinuteStart;
    String mMinuteEnd;
    String mProcedureName;
    int mProcedurePrice;
    String mProcedureNote;

    View.OnClickListener sendViberListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (phone != null || !phone.isEmpty()) {
                Viber viber = new Viber(fa);
                viber.sendMsg(mTextMsg);
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
                whatsApp.sendMsg(mTextMsg);
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
                sms.sendSMS(phone, mTextMsg);
            } else {
                showToast(fa.getString(com.example.smena.sendmessage.R.string.no_phone));
            }
        }
    };
    View.OnClickListener oclBtnOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Resources resources = getResources();

            Sessions sessions = new Sessions(fa);
            long sessinonId = sessions.addSession(mClientName, mProcedureName,
                    mProcedurePrice, mProcedureNote, mMetaData.getProcedureColor(),
                    "" + mYear + "-" + mMonthNumb + "-" + mDay +
                            " " + mHourStart + ":" + mMinuteStart,
                    "" + mYear + "-" + mMonthNumb + "-" + mDay +
                            " " + mHourEnd + ":" + mMinuteEnd,
                    mMetaData.getClientPhones().get(0), mMetaData.getClientEmails().get(0));

            Clients clients = new Clients(fa);
            clients.updateClientAddVisit(mClientName);

            if (sessinonId != -1) {
                if (iMessage == 1)
                    sendMsgView().show();
                else {
                    Toast.makeText(fa, "Запись завершена успешно", Toast.LENGTH_SHORT).show();
                    //MainActivity.refreshList = true;
                    fa.finish();
                }
            } else {
                Toast.makeText(fa, resources.getString(R.string.end_error), Toast.LENGTH_SHORT).show();
            }
        }
    };
    private View mData;
    private EditText editMsg;
    Animation.AnimationListener fadeInAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Animation animation_out = AnimationUtils.loadAnimation(fa, R.anim.scale_out);
            mDataLayout.removeAllViews();
            btnMessage.setVisibility(View.GONE);
            mTextMsg = createMsg();
            editMsg.setText(mTextMsg);
            mDataLayout.addView(editMsg);
            mDataAllLayout.startAnimation(animation_out);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };
    View.OnClickListener oclBtnMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Animation animation_in = AnimationUtils.loadAnimation(fa, R.anim.scale_in);
            animation_in.setAnimationListener(fadeInAnimationListener);
            mDataAllLayout.startAnimation(animation_in);
            iMessage = 1;
        }
    };

    @Override
    public void onAttach(Activity activity) {
        fa = super.getActivity();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mInflater = inflater;

        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.completion_tab4, container, false);

        mDataLayout = (LinearLayout) relativeLayout.findViewById(R.id.data_layout);
        mData = inflater.inflate(R.layout.layout_tab4, null);
        mDataLayout.addView(mData);
        mDataAllLayout = (LinearLayout) relativeLayout.findViewById(R.id.LayDataAll);


        mMetaData = (MetaData) getArguments().getSerializable(MetaData.TAG);
        initValues();

        editMsg = (EditText) mInflater.inflate(R.layout.edit_msg_tab4, null);

        btnClickOK = (Button) relativeLayout.findViewById(R.id.BtnCompletionOK);
        btnClickOK.setOnClickListener(oclBtnOK);
        btnMessage = (Button) relativeLayout.findViewById(R.id.BtnCompletionMessage);
        btnMessage.setOnClickListener(oclBtnMessage);

        setHasOptionsMenu(true);
        return relativeLayout;
    }

    @Override
    public void onDetach() {
        mMetaData = null;
        super.onDetach();
    }

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

        AlertDialog.Builder builder = new AlertDialog.Builder(fa);
        builder.setView(view).
                setCancelable(false);
        builder.setNegativeButton(R.string.end_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startActivity(new Intent(fa, MainActivity.class));
                Toast.makeText(fa, "Запись завершена успешно", Toast.LENGTH_SHORT).show();
                fa.finish();
            }
        });

        return builder;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        setupDataLayout();
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
                phone = mPhones.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                phone = mPhones.get(0);
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

    private void setInfoView(RelativeLayout relativeLayout) {
        clientNameTextView = (TextView) relativeLayout.findViewById(R.id.client_name_textview);
        clientNameTextView.setText(mClientName);
        dateTextView = (TextView) relativeLayout.findViewById(R.id.date_textview);
        dateTextView.setText(mDay + " " + mMonthName + " " + mYear);
        timeTextView = (TextView) relativeLayout.findViewById(R.id.time_textview);

        timeTextView.setText("c " + mHourStart + ":" + mMinuteStart +
                " по " + mHourEnd + ":" + mMinuteEnd);
        procedureNameTextView = (TextView) relativeLayout.findViewById(R.id.procedure_name_textview);
        procedureNameTextView.setText(mProcedureName);
        procedurePriceTextView = (TextView) relativeLayout.findViewById(R.id.procedure_price_textview);
        procedurePriceTextView.setText("" + mProcedurePrice);
        procedureNoteTextView = (TextView) relativeLayout.findViewById(R.id.procedure_note_textview);
        procedureNoteTextView.setText(mProcedureNote);

        mDataLayout = (LinearLayout) relativeLayout.findViewById(R.id.data_layout);

    }

    private void setupDataLayout() {
        initValues();
        mDataLayout.removeAllViews();
        mDataLayout.addView(mData);
        setInfoView(relativeLayout);
    }

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(fa, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private String createMsg() {
        return "Здравствуйте, " + mClientName + "!" +
                "Вы записаны на " + mProcedureName + " " +
                mDay + " " + mMonthName + "," +
                "время " + mHourStart + ":" + mMinuteStart + "." +
                "Цена " + mProcedurePrice + ". До встречи!";
    }

    private String getMonthName(String monthNumb) {
        return DateFormatSymbols.getInstance().getMonths()[Integer.parseInt(monthNumb)]; //вернуть к нумерации с 0
    }

    private void initValues() {
        mClientName = mMetaData.getClientName();
        mYear = "" + mMetaData.getYear();
        mMonthName = DateFormatSymbols.getInstance().getMonths()[Integer.parseInt(mMetaData.getMonth())];
        mMonthNumb = "" + (Integer.parseInt(mMetaData.getMonth()) + 1);
        mDay = mMetaData.getDay();
        mHourStart = mMetaData.getHourStart();
        mHourEnd = mMetaData.getHourEnd();
        mMinuteStart = mMetaData.getMinuteStart();
        mMinuteEnd = mMetaData.getMinuteEnd();
        mProcedureName = mMetaData.getProcedureName();
        mProcedurePrice = mMetaData.getProcedurePrice();
        mProcedureNote = mMetaData.getProcedureNote();
    }


}
