package app.clientplanner.record.completion_choose;

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

import app.clientplanner.MetaData;
import app.clientplanner.R;
import app.clientplanner.main_activity.MainActivity;

public class CompletionTab4 extends Fragment {

    private static final String TAG = "CompletionTab4";
    private int mMessage = 0;

    private Button mMessageBtn;

    private FragmentActivity mContext;
    View.OnClickListener oclBtnMessage = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Animation animation_in = AnimationUtils.loadAnimation(mContext, R.anim.scale_in);
            animation_in.setAnimationListener(fadeInAnimationListener);
            mDataAllLayout.startAnimation(animation_in);
            mMessage = 1;
        }
    };
    private String mTextMsg;
    private ArrayList<String> mPhones;
    private String phone;
    private ArrayList<String> mEmails;
    private String emailAddress;
    private RelativeLayout relativeLayout;
    private LinearLayout mDataLayout, mDataAllLayout;
    private MetaData mMetaData;
    private String mClientName;
    private String mYear;
    private String mMonthName;
    private String mMonthNumb;
    private String mDay;
    private String mHourStart;
    private String mHourEnd;
    private String mMinuteStart;
    private String mMinuteEnd;
    private String mProcedureName;
    private int mProcedurePrice;
    private String mProcedureNote;
    private int contactSelect;
    View.OnClickListener sendViberListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (phone != null || !phone.isEmpty()) {
                Viber viber = new Viber(mContext);
                viber.sendMsg(mTextMsg);
                contactSelect = 1;
            } else {
                showToast(mContext.getString(R.string.no_phone));
            }
        }
    };
    View.OnClickListener sendWhatsAppListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (phone != null || !phone.isEmpty()) {
                WhatsApp whatsApp = new WhatsApp(mContext);
                whatsApp.sendMsg(mTextMsg);
                contactSelect = 2;
            } else {
                showToast(mContext.getString(R.string.no_phone));
            }
        }
    };
    View.OnClickListener sendEmailListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //ArrayList<String> emails = mMetaData.getClientEmails();
            if (emailAddress != null || !emailAddress.isEmpty()) {
                Email email = new Email(mContext);
                email.sendEmail(emailAddress, mTextMsg);
                contactSelect = 3;
            } else {
                showToast(mContext.getString(R.string.no_email));
            }
        }
    };
    View.OnClickListener sendSmsListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (phone != null || !phone.isEmpty()) {
                SMS sms = new SMS(mContext);
                sms.sendSMS(phone, mTextMsg);
                contactSelect = 4;
            } else {
                showToast(mContext.getString(R.string.no_phone));
            }
        }
    };
    View.OnClickListener oclBtnOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Resources resources = getResources();
            long sessionId = 0;
            if (mMessage == 0) {
                Sessions sessions = new Sessions(mContext);
                sessionId = sessions.addSession(mClientName, mProcedureName,
                        mProcedurePrice, mProcedureNote, mMetaData.getProcedureColor(),
                        "" + mYear + "-" + mMonthNumb + "-" + mDay +
                                " " + mHourStart + ":" + mMinuteStart,
                        "" + mYear + "-" + mMonthNumb + "-" + mDay +
                                " " + mHourEnd + ":" + mMinuteEnd,
                        mMetaData.getClientPhones().get(0), mMetaData.getClientEmails().get(0),
                        contactSelect);

                Clients clients = new Clients(mContext);
                clients.updateClientAddVisit(mClientName);
            }
            if (sessionId != -1) {
                if (mMessage == 1)
                    sendMsgView().show();
                else {
                    Toast.makeText(mContext,
                            mContext.getResources().getString(R.string.record_complete),
                            Toast.LENGTH_SHORT).show();
                    mContext.finish();
                }
            } else {
                Toast.makeText(mContext, resources.getString(R.string.end_error), Toast.LENGTH_SHORT).show();
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
            Animation animation_out = AnimationUtils.loadAnimation(mContext, R.anim.scale_out);
            mDataLayout.removeAllViews();
            mMessageBtn.setVisibility(View.GONE);
            mTextMsg = createMsg();
            editMsg.setText(mTextMsg);
            mDataLayout.addView(editMsg);
            mDataAllLayout.startAnimation(animation_out);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    @Override
    public void onAttach(Activity activity) {
        mContext = super.getActivity();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.completion_tab4, container, false);
        contactSelect = -1;

        mDataLayout = (LinearLayout) relativeLayout.findViewById(R.id.data_layout);
        mData = inflater.inflate(R.layout.layout_tab4, null);
        mDataLayout.addView(mData);
        mDataAllLayout = (LinearLayout) relativeLayout.findViewById(R.id.LayDataAll);


        mMetaData = (MetaData) getArguments().getSerializable(MetaData.TAG);
        initValues();

        editMsg = (EditText) inflater.inflate(R.layout.edit_msg_tab4, null);

        Button btnClickOK = (Button) relativeLayout.findViewById(R.id.BtnCompletionOK);
        btnClickOK.setOnClickListener(oclBtnOK);
        mMessageBtn = (Button) relativeLayout.findViewById(R.id.BtnCompletionMessage);
        mMessageBtn.setOnClickListener(oclBtnMessage);

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

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view).
                setCancelable(false);
        builder.setNegativeButton(R.string.end_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Resources resources = getResources();
                Sessions sessions = new Sessions(mContext);
                long sessinonId = sessions.addSession(mClientName, mProcedureName,
                        mProcedurePrice, mProcedureNote, mMetaData.getProcedureColor(),
                        "" + mYear + "-" + mMonthNumb + "-" + mDay +
                                " " + mHourStart + ":" + mMinuteStart,
                        "" + mYear + "-" + mMonthNumb + "-" + mDay +
                                " " + mHourEnd + ":" + mMinuteEnd,
                        mMetaData.getClientPhones().get(0), mMetaData.getClientEmails().get(0), contactSelect);

                Clients clients = new Clients(mContext);
                clients.updateClientAddVisit(mClientName);
                if (sessinonId != -1) {
                    if (mMessage == 1)
                        sendMsgView().show();
                    else {
                        Toast.makeText(mContext,
                                mContext.getResources().getString(R.string.record_complete),
                                Toast.LENGTH_SHORT).show();
                        //MainActivity.refreshList = true;
                        mContext.finish();
                    }
                } else {
                    Toast.makeText(mContext, resources.getString(R.string.end_error), Toast.LENGTH_SHORT).show();
                }
                dialog.cancel();
                startActivity(new Intent(mContext, MainActivity.class));
                Toast.makeText(mContext,
                        mContext.getResources().getString(R.string.record_complete),
                        Toast.LENGTH_SHORT).show();
                mContext.finish();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                R.layout.my_spinner, mPhones);
        adapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);
        Spinner spinner = new Spinner(mContext);
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mContext,
                R.layout.my_spinner, mEmails);
        adapter.setDropDownViewResource(R.layout.my_spinner_dropdown_item);
        Spinner spinner = new Spinner(mContext);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                emailAddress = mEmails.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                emailAddress = mEmails.get(0);
            }
        });

        return spinner;
    }

    private TextView setupPhoneTextView() {

        TextView phoneTextView = new TextView(mContext);
        if (mPhones == null || mPhones.isEmpty()) {
            phoneTextView.setText(mContext.getResources().getString(R.string.unknown));
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

        TextView emailTextView = new TextView(mContext);
        if (mPhones == null || mEmails.isEmpty()) {
            emailTextView.setText(mContext.getResources().getString(R.string.unknown));
            emailAddress = null;
        } else {
            emailAddress = mEmails.get(0);
            emailTextView.setText(emailAddress);
        }
        emailTextView.setTextColor(getResources().getColor(R.color.ColorPrimary));
        emailTextView.setTextSize(16);

        return emailTextView;
    }

    private void setInfoView(RelativeLayout relativeLayout) {
        TextView clientNameTextView = (TextView) relativeLayout.findViewById(R.id.client_name_textview);
        clientNameTextView.setText(mClientName);
        TextView dateTextView = (TextView) relativeLayout.findViewById(R.id.date_textview);
        dateTextView.setText(mDay + " " + mMonthName + " " + mYear);
        TextView timeTextView = (TextView) relativeLayout.findViewById(R.id.time_textview);

        timeTextView.setText(mContext.getResources().getString(R.string.from) + " " + mHourStart +
                ":" + mMinuteStart + " " +
                mContext.getResources().getString(R.string.to) + " " + mHourEnd + ":" + mMinuteEnd);
        TextView procedureNameTextView = (TextView) relativeLayout.findViewById(R.id.procedure_name_textview);
        procedureNameTextView.setText(mProcedureName);
        TextView procedurePriceTextView = (TextView) relativeLayout.findViewById(R.id.procedure_price_textview);
        procedurePriceTextView.setText("" + mProcedurePrice);
        TextView procedureNoteTextView = (TextView) relativeLayout.findViewById(R.id.procedure_note_textview);
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
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private String createMsg() {
        return mContext.getResources().getString(R.string.hello) + " " + mClientName + "!"
                + mContext.getResources().getString(R.string.you_appointment) + " " + mProcedureName
                + " " + mDay + " " + mMonthName + "," +
                mContext.getResources().getString(R.string.time_) + mHourStart + ":" + mMinuteStart
                + "." + mContext.getResources().getString(R.string.price_) + " " + mProcedurePrice
                + "."  + mContext.getResources().getString(R.string.bye);
    }

    private String getMonthName(String monthNumb) {
        return DateFormatSymbols.getInstance().getMonths()[Integer.parseInt(monthNumb)];
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
