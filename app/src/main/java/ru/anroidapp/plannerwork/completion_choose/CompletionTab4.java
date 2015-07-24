package ru.anroidapp.plannerwork.completion_choose;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.anroidapp.plannerwork.MetaData;
import ru.anroidapp.plannerwork.R;

/**
 * Created by Артём on 22.07.2015.
 */
public class CompletionTab4 extends Fragment {

    private static final String TAG = "CompletionTab4";

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

    View.OnClickListener oclBtnOK = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private void setupViews(RelativeLayout relativeLayout) {
        clientNameTextView = (TextView) relativeLayout.findViewById(R.id.client_name_textview);
        clientNameTextView.setText(mMetaData.getClientName());
        dateTextView = (TextView) relativeLayout.findViewById(R.id.date_textview);
        dateTextView.setText(mMetaData.getDay() + " " + months[mMetaData.getMonth()]
                + " " + mMetaData.getYear());
        timeTextView = (TextView) relativeLayout.findViewById(R.id.time_textview);
        timeTextView.setText("c " + mMetaData.getHourStart() + ":" + mMetaData.getMinuteStart()
                + " по " + +mMetaData.getHourEnd() + ":" + mMetaData.getMinuteEnd());
        procedureNameTextView = (TextView) relativeLayout.findViewById(R.id.procedure_name_textview);
        procedureNameTextView.setText(mMetaData.getProcedureName());
        procedurePriceTextView = (TextView) relativeLayout.findViewById(R.id.procedure_price_textview);
        procedurePriceTextView.setText("" + mMetaData.getProcedurePrice());
        procedureNoteTextView = (TextView) relativeLayout.findViewById(R.id.procedure_note_textview);
        procedureNoteTextView.setText(mMetaData.getProcedureNote());

        btnClickOK = (Button) relativeLayout.findViewById(R.id.BtnCompletionOK);
    }
}
