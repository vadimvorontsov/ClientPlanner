package ru.anroidapp.plannerwork.main_activity.slide_nearest_sessions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private Context mContext;
    private ArrayList<Long> mSessions;
    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFM;

    public ScreenSlidePagerAdapter(Context ctx, FragmentManager fm, ArrayList<Long> sessions) {
        super(fm);
        this.mContext = ctx;
        this.mFM = fm;
        mSessions = sessions;
        mFragmentTags = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        return NearestSessionFragment.newInstance(mSessions.get(position));
    }

    @Override
    public int getCount() {
        return mSessions.size();
    }

//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Object obj = super.instantiateItem(container, position);
//        if (obj instanceof Fragment) {
//            Fragment f = (Fragment) obj;
//            String tag = f.getTag();
//            mFragmentTags.put(position, tag);
//        }
//        return obj;
//    }
//
//    public Fragment getFragment(int position) {
//        String tag = mFragmentTags.get(position);
//        if (tag == null)
//            return null;
//        return mFM.findFragmentByTag(tag);
//    }
}
