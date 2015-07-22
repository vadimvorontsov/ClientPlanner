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

import ru.anroidapp.plannerwork.completion_choose.CompletionTab4;
import ru.anroidapp.plannerwork.contact_choose.ContactTab1;
import ru.anroidapp.plannerwork.date_choose.DateTab2;
import ru.anroidapp.plannerwork.procedure_choose.ProcedureTab3;


public class RecordActivity extends AppCompatActivity {

    Toolbar toolbar;
    ViewPager pager;
    RecViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    CharSequence titles[] = {"Контакт", "Дата", "Процедура", "Завершение"};
    int numbOfTabs = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        toolbar = (Toolbar) findViewById(R.id.tool_record);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.rec_title);
        //getSupportActionBar().hide();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RecordActivity.this, MainActivity.class));
                //finish();
            }
        });

        adapter = new RecViewPagerAdapter(getSupportFragmentManager(), titles, numbOfTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager_record);
        pager.setAdapter(adapter);
        tabs = (SlidingTabLayout) findViewById(R.id.tabs_record);
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

    }

    public class RecViewPagerAdapter extends FragmentStatePagerAdapter {

        ContactTab1 tab1;
        DateTab2 tab2;
        ProcedureTab3 tab3;
        CompletionTab4 tab4;

        CharSequence Titles[]; // This will Store the titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
        int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

        // Build a Constructor and assign the passed Values to appropriate values in the class
        public RecViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
            super(fm);
            this.Titles = mTitles;
            this.NumbOfTabs = mNumbOfTabsumb;
        }

        //This method return the fragment for the every position in the View Pager
        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                if (tab1 == null) {
                    //Log.i("!!", "load tab1");
                    return tab1 = new ContactTab1();
                } else {
                    //Log.i("!!", "not load tab1");
                    return tab1;
                }
            } else if (position == 1) {
                if (tab2 == null) {
                    //Log.i("!!", "load tab2");
                    return tab2 = new DateTab2();
                } else {
                    //Log.i("!!", "not load tab2");
                    return tab2;
                }
            } else if (position == 2) {
                if (tab3 == null) {
                    //Log.i("!!", "load tab3");
                    return tab3 = new ProcedureTab3();
                } else {
                    //Log.i("!!", "not load tab3");
                    return tab3;
                }
            }else if (position == 3) {
                if (tab4 == null) {
                    //Log.i("!!", "load tab3");
                    return tab4 = new CompletionTab4();
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
            return Titles[position];
        }

        // This method return the Number of tabs for the tabs Strip
        @Override
        public int getCount() {
            return NumbOfTabs;
        }
    }

}
