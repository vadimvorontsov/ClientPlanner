package app.clientplanner.main_activity.slide_nearest_sessions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormatSymbols;

import app.clientplanner.R;
import lib.clientbase.procedures.Sessions;

public class NearestSessionFragment extends Fragment {

    private static final String ID_SESSION = "id_session";
    //private static final String POSITION = "position";
    private Context mContext;
    private long mSessionId;
    private int mPosition;

    static NearestSessionFragment newInstance(long sessionId) {
        NearestSessionFragment pageFragment = new NearestSessionFragment();
        Bundle arguments = new Bundle();
        arguments.putLong(ID_SESSION, sessionId);
        //arguments.putInt(POSITION, position);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        mContext = activity.getApplicationContext();
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSessionId = getArguments().getLong(ID_SESSION);
        //mPosition = getArguments().getInt(POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.nearest_sessions_fragment, null);
        TextView clientNameTextView = (TextView) view.findViewById(R.id.nearest_contact_name);
        TextView dateTextView = (TextView) view.findViewById(R.id.nearest_contact_date);
        TextView timeTextView = (TextView) view.findViewById(R.id.nearest_contact_time);
        TextView clientStatusTextView = (TextView) view.findViewById(R.id.nearest_contact_status);

        Sessions sessions = new Sessions(mContext);
        Object[] session = sessions.getSessionById(mSessionId);
        Object[] procedureObject = (Object[]) session[3];
        String[] timeDayMonthStartForView = getDayMonth((String) session[4], true);
        String[] timeEndForView = getDayMonth((String) session[5], false);

        clientNameTextView.setText((String) session[0]);
        dateTextView.setText(timeDayMonthStartForView[1] + " " + timeDayMonthStartForView[2]);
        timeTextView.setText(timeDayMonthStartForView[0] + " - " + timeEndForView[0]);
        clientStatusTextView.setText((String) session[6]);

        return view;
    }

    private String[] getDayMonth(String time, boolean startTime) {

        String[] times = time.split("\\D");
        String[] result;
        if (startTime) {
            String monthResult = DateFormatSymbols.getInstance().getMonths()[Integer.parseInt(times[1])-1];
            String dayResult = times[2];
            String timeResult = times[3] + ":" + times[4];

            result = new String[]{timeResult, dayResult, monthResult};
        } else {
            result = new String[]{times[3] + ":" + times[4]};
        }
        return result;
    }

}
