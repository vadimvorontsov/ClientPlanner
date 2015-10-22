package ru.anroidapp.plannerwork.main_activity.slide_nearest_sessions;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private final int COUNT;
    private Context mContext;

    public ScreenSlidePagerAdapter(Context ctx, FragmentManager fm, int count) {
        super(fm);
        this.mContext = ctx;
        COUNT = count;
    }


    @Override
    public Fragment getItem(int position) {
        return NearestSessionFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return COUNT;
    }
}
