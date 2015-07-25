package ru.anroidapp.plannerwork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import ru.anroidapp.plannerwork.completion_choose.CompletionTab4;
import ru.anroidapp.plannerwork.contact_choose.ContactTab1;
import ru.anroidapp.plannerwork.date_choose.DateTab2;
import ru.anroidapp.plannerwork.procedure_choose.ProcedureTab3;

public class RecordActivity extends AppCompatActivity {

    private final String TAG = "RecordActivity";

    CharSequence[] titles;
    int numbOfTabs;

    MetaData mMetaData;

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_record);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.rec_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_home);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RecordActivity.this, "123", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RecordActivity.this, MainActivity.class));
            }
        });

        RecViewPagerAdapter adapter = new RecViewPagerAdapter(getSupportFragmentManager());

        // Assigning ViewPager View and setting the adapter
        ViewPager pager = (ViewPager) findViewById(R.id.pager_record);
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

//    private void initializeNavigationDrawer(Toolbar toolbar) {
//
//        AccountHeader resultHeder = new AccountHeaderBuilder()
//                .withActivity(this)
//                .withHeaderBackground(R.drawable.header)
//                .build();
//
//        Drawer resultDrawer = new DrawerBuilder()
//                .withActivity(this)
//                .withToolbar(toolbar)
//                .withDisplayBelowToolbar(true)
//                .withAccountHeader(resultHeder)
//                .withActionBarDrawerToggleAnimated(true)
//                .addDrawerItems(
//                        new PrimaryDrawerItem()
//                                .withName(R.string.navDraw_1)
//                                .withIcon(R.drawable.ic_home_black_18dp),
//                        // new DividerDrawerItem(),
//                        new SecondaryDrawerItem()
//                                .withName(R.string.navDraw_2)
//                                .withIcon(R.drawable.ic_settings_applications_black_18dp),
//                        new SecondaryDrawerItem()
//                                .withName(R.string.navDraw_3)
//                                .withIcon(R.drawable.ic_stars_black_18dp)
//                )
//                        //            .withSliderBackgroundColor(R.color.md_grey_100)
//                .build();
//
//    }

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mMetaData != null) {
            outState.putSerializable(MetaData.TAG, mMetaData);
        }
        super.onSaveInstanceState(outState);
    }

}
