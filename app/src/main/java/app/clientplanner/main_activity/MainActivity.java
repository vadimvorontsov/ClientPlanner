package app.clientplanner.main_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import app.clientplanner.LangUpdateText;
import app.clientplanner.LanguageManager;
import app.clientplanner.ProcedureActivity;
import app.clientplanner.R;
import app.clientplanner.animation.Circle;
import app.clientplanner.animation.ZoomOutPageTransformer;
import app.clientplanner.calendar.CalendarActivity;
import app.clientplanner.main_activity.slide_nearest_sessions.ScreenSlidePagerAdapter;
import app.clientplanner.record.RecordActivity;
import lib.clientbase.procedures.Sessions;


public class MainActivity extends ActionBarActivity implements
        LoaderCallbacks<ArrayList<Long>>,
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        LangUpdateText {

    private static final int MAX_SESSIONS_COUNT = 5;
    private final int GET_NEAREST_SESSIONS = 0;
    private Circle[] circleArray;
    private int sessionsCount;
    private LinearLayout circleTable;
    private LanguageManager langManager;
    private TextView startRecordTextView;
    private TextView startCalendarTextView;
    private TextView startProcedureTextView;
    private TextView startRecordDescriptionTextView;
    private TextView startCalendarDescriptionTextView;
    private TextView startProcedureDescriptionTextView;
    private TextView noNearestRecordsTextView;
    private MenuItem changeLangMenuItem;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));

//        FragmentManager fm = getSupportFragmentManager();
//        mNavigationDrawerFragment = new NavigationDrawerFragment();
//        FragmentTransaction ftr = fm.beginTransaction();
//        ftr.add(R.id.navigation_drawer, mNavigationDrawerFragment, "tag");
//        ftr.commit();


        startRecordTextView = (TextView) findViewById(R.id.start_record_textview);
        startRecordDescriptionTextView = (TextView)
                findViewById(R.id.start_record_description_textview);

        startCalendarTextView = (TextView) findViewById(R.id.start_calendar_textview);
        startCalendarDescriptionTextView = (TextView)
                findViewById(R.id.start_calendar_description_textview);

        startProcedureTextView = (TextView) findViewById(R.id.start_procedure_textview);
        startProcedureDescriptionTextView = (TextView)
                findViewById(R.id.start_procedure_description_textview);

        noNearestRecordsTextView = (TextView) findViewById(R.id.no_records_textview);

        LinearLayout startRecordLayout = (LinearLayout) findViewById(R.id.start_record_layout);
        startRecordLayout.setOnClickListener(new StartRecordListener());

        LinearLayout startCalendarView = (LinearLayout) findViewById(R.id.start_calendar_view);
        startCalendarView.setOnClickListener(new StartCalendarViewListener());

        LinearLayout startProcedureView = (LinearLayout) findViewById(R.id.start_procedure_view);
        startProcedureView.setOnClickListener(new StartProceduresViewListener());


        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.main_activity));
//
        langManager = new LanguageManager(this);
        langManager.loadLocale();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getSupportLoaderManager().initLoader(GET_NEAREST_SESSIONS, null, this);
        getSupportLoaderManager().getLoader(GET_NEAREST_SESSIONS).forceLoad();
    }

    private void startRecord() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    private void startCalendarView() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    private void startProceduresView() {
        Intent intent = new Intent(this, ProcedureActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        changeLangMenuItem = item;
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            case R.id.change_language:
                if (!langManager.isRus)
                    langManager.changeLang("ru");
                else
                    langManager.changeLang("en");
                updateTexts();
                break;
            case R.id.menu_data:
                showPopup(R.id.menu_data);
        }

        return super.onOptionsItemSelected(item);
    }

    public void showPopup(int id) {
        View menuItemView = findViewById(id);
        PopupMenu popup = new PopupMenu(this, menuItemView);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.popup_main_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.save_data:
                        dbSaveToBackup();
                        break;
                    case R.id.load_data:
                        dbLoadFromBackup();
                        break;
                }
                return false;
            }
        });
        popup.show();
    }


    private class StartCalendarViewListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startCalendarView();
        }
    }

    private class StartProceduresViewListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startProceduresView();
        }
    }

    private class StartRecordListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startRecord();
        }
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            setCircleChoose(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    private void dbSaveToBackup() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = data + "/data/" + getPackageName()
                        + "/databases/sessions.db";
                String backupDBPath = data + "/data/" + getPackageName()
                        + "/databases/backup_sessions.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }

    private void dbLoadFromBackup() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String dbPath = data + "/data/" + getPackageName()
                        + "/databases/sessions.db";
                String backupDBPath = data + "/data/" + getPackageName()
                        + "/databases/backup_sessions.db";
                File db = new File(dbPath);
                File backupDB = new File(sd, backupDBPath);

                if (backupDB.exists()) {
                    FileChannel src = new FileInputStream(backupDB).getChannel();
                    FileChannel dst = new FileOutputStream(db).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {

        }
    }

    private void setCircleChoose(int position) {
        for (int i = 0; i < sessionsCount && i < MAX_SESSIONS_COUNT; i++) {
            if (i == position)
                circleArray[i].setColor(getResources().getColor(android.R.color.holo_purple));
            else
                circleArray[i].setColor(getResources().getColor(android.R.color.white));
        }
    }

    private void setCircleVisibility(int sessionCount) {
        if (sessionCount == 1) {
            circleTable.setVisibility(View.GONE);
            return;
        }

        circleArray[0].setColor(getResources().getColor(android.R.color.holo_purple));
        for (int i = 0; i < MAX_SESSIONS_COUNT; i++) {
            if (i < sessionCount) {
                circleArray[i].setVisibility(View.VISIBLE);
                if (i != 0)
                    circleArray[i].setColor(getResources().getColor(android.R.color.white));
            } else {
                circleArray[i].setVisibility(View.GONE);
            }

        }
    }

    private void initCircles() {
        Circle circle_0 = (Circle) findViewById(R.id.circle_0);
        Circle circle_1 = (Circle) findViewById(R.id.circle_1);
        Circle circle_2 = (Circle) findViewById(R.id.circle_2);
        Circle circle_3 = (Circle) findViewById(R.id.circle_3);
        Circle circle_4 = (Circle) findViewById(R.id.circle_4);

        circleArray = new Circle[]{circle_0, circle_1, circle_2, circle_3, circle_4};
    }

    private void loadNearestSessions(ArrayList<Long> nearestSessions) {
        ViewPager mPager = (ViewPager) findViewById(R.id.preview_sessions_pager);
        sessionsCount = nearestSessions.size();
        TextView notRecord = (TextView) findViewById(R.id.no_records_textview);
        circleTable = (LinearLayout) findViewById(R.id.circles);

        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.addOnPageChangeListener(new PageChangeListener());

        if (nearestSessions != null && !nearestSessions.isEmpty()) {
            initCircles();
            PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter
                    (this, getSupportFragmentManager(), nearestSessions);
            mPager.setAdapter(mPagerAdapter);
            notRecord.setVisibility(View.GONE);
            mPager.setVisibility(View.VISIBLE);
            circleTable.setVisibility(View.VISIBLE);

            setCircleVisibility(sessionsCount);

        } else if (nearestSessions != null && nearestSessions.isEmpty()) {
            notRecord.setVisibility(View.VISIBLE);
            mPager.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateTexts() {
        this.startRecordTextView.setText(R.string.start_record);
        this.startRecordDescriptionTextView.setText(R.string.start_record_description);
        this.startCalendarTextView.setText(R.string.calendar);
        this.startCalendarDescriptionTextView.setText(R.string.start_calendar_description);
        this.startProcedureTextView.setText(R.string.services);
        this.startProcedureDescriptionTextView.setText(R.string.start_procedure_description);
        this.noNearestRecordsTextView.setText(R.string.no_records);
        this.changeLangMenuItem.setTitle(R.string.change_language);

        getLoaderManager().getLoader(GET_NEAREST_SESSIONS).forceLoad();
    }

    @Override
    public Loader<ArrayList<Long>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case GET_NEAREST_SESSIONS:
                return new NearestSessionLoader(getApplicationContext());
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Long>> loader, ArrayList<Long> data) {
        loadNearestSessions(data);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Long>> loader) {

    }

    private static class NearestSessionLoader extends AsyncTaskLoader<ArrayList<Long>> {
        private Context context;

        public NearestSessionLoader(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public ArrayList<Long> loadInBackground() {
            Sessions sessions = new Sessions(context);
            ArrayList<Long> nearestSessionsId = sessions.getSessionsAfterTime("datetime('now')",
                    MAX_SESSIONS_COUNT);
            return nearestSessionsId;
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
//        // update the main content by replacing fragments
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.main_without_navigation, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "Serction 1";
                break;
            case 2:
                mTitle = "Serction 2";
                break;
            case 3:
                mTitle = "Serction 3";
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
