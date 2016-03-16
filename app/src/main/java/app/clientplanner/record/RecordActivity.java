package app.clientplanner.record;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import app.clientplanner.MetaData;
import app.clientplanner.R;
import app.clientplanner.main_activity.MainActivity;
import app.clientplanner.record.completion_choose.CompletionTab4;
import app.clientplanner.record.contact_choose.ContactTab1;
import app.clientplanner.record.date_choose.DateTab2;
import app.clientplanner.record.procedure_choose.ProcedureTab3;


public class RecordActivity extends AppCompatActivity {

    private final String TAG = "RecordActivity";

    private CharSequence[] titles;
    private int numbOfTabs;

    private MetaData mMetaData;

    public static ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        if (savedInstanceState != null) {
            mMetaData = (MetaData) savedInstanceState.getSerializable(MetaData.TAG);
        } else {
            mMetaData = new MetaData(RecordActivity.this);
        }

        titles = getResources().getTextArray(R.array.fragment_titles);
        numbOfTabs = titles.length;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_record);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.color.record_first));
        getSupportActionBar().setTitle(R.string.record);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_home);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(RecordActivity.this, "123", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RecordActivity.this, MainActivity.class));
            }
        });

        RecViewPagerAdapter adapter = new RecViewPagerAdapter(getSupportFragmentManager());

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager_record);
        pager.setAdapter(adapter);
        SlidingTabLayout tabs = (SlidingTabLayout) findViewById(R.id.tabs_record);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
        //initializeNavigationDrawer(toolbar);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMetaData != null) {
            outState.putSerializable(MetaData.TAG, mMetaData);
        }
        super.onSaveInstanceState(outState);
    }

    public class RecViewPagerAdapter extends FragmentStatePagerAdapter {

        Bundle bundle;

        ContactTab1 tab1;
        DateTab2 tab2;
        ProcedureTab3 tab3;
        CompletionTab4 tab4;

        // Build a Constructor and assign the passed Values to appropriate values in the class
        public RecViewPagerAdapter(FragmentManager fm) {
            super(fm);
            bundle = new Bundle();
            bundle.putSerializable(MetaData.TAG, mMetaData);
        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    if (tab1 == null) {
                        //Log.i("!!", "load tab1");
                        tab1 = new ContactTab1();
                        tab1.setArguments(bundle);
                        return tab1;
                    } else {
                        //Log.i("!!", "not load tab1");
                        return tab1;
                    }
                case 1:
                    if (tab2 == null) {
                        //Log.i("!!", "load tab2");
                        tab2 = new DateTab2();
                        tab2.setArguments(bundle);
                        return tab2;
                    } else {
                        //Log.i("!!", "not load tab2");
                        return tab2;
                    }
                case 2:
                    if (tab3 == null) {
                        //Log.i("!!", "load tab3");
                        tab3 = new ProcedureTab3();
                        tab3.setArguments(bundle);
                        return tab3;
                    } else {
                        //Log.i("!!", "not load tab3");
                        return tab3;
                    }
                case 3:
                    if (tab4 == null) {
                        //Log.i("!!", "load tab3");
                        tab4 = new CompletionTab4();
                        tab4.setArguments(bundle);
                        return tab4;
                    } else {
                        //Log.i("!!", "not load tab4");
                        return tab4;
                    }
            }
            return null;
        }

        // This method return the titles for the Tabs in the Tab Strip
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        // This method return the Number of tabs for the tabs Strip
        @Override
        public int getCount() {
            return numbOfTabs;
        }
    }

    public static void showTab(int numTab){
        pager.setCurrentItem(numTab);
    }

}
