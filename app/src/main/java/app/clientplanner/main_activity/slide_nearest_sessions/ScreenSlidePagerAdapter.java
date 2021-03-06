package app.clientplanner.main_activity.slide_nearest_sessions;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private ArrayList<Long> mSessions;

    public ScreenSlidePagerAdapter(Context ctx, FragmentManager fm, ArrayList<Long> sessions) {
        super(fm);
        this.mContext = ctx;
        mSessions = sessions;
    }

    @Override
    public Fragment getItem(int position) {
        return NearestSessionFragment.newInstance(mSessions.get(position));
    }

    @Override
    public int getCount() {
        return mSessions.size();
    }

}
