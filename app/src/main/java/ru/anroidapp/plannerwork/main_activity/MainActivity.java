package ru.anroidapp.plannerwork.main_activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.smena.clientbase.procedures.Sessions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Locale;

import ru.anroidapp.plannerwork.CalendarActivity;
import ru.anroidapp.plannerwork.LangUpdateText;
import ru.anroidapp.plannerwork.LanguageManager;
import ru.anroidapp.plannerwork.ProcedureActivity;
import ru.anroidapp.plannerwork.R;
import ru.anroidapp.plannerwork.animation.Circle;
import ru.anroidapp.plannerwork.animation.ZoomOutPageTransformer;
import ru.anroidapp.plannerwork.record.RecordActivity;
import ru.anroidapp.plannerwork.main_activity.slide_nearest_sessions.ScreenSlidePagerAdapter;


public class MainActivity extends AppCompatActivity implements LangUpdateText {

    private Circle[] circleArray;
    private final int MAX_SESSIONS_COUNT = 5;
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


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //getDbFile();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));

        LinearLayout startRecordLayout = (LinearLayout) findViewById(R.id.start_record_layout);
        startRecordLayout.setOnClickListener(startRecordListener);

        LinearLayout startCalendarView = (LinearLayout) findViewById(R.id.start_calendar_view);
        startCalendarView.setOnClickListener(startCalendarViewListener);

        LinearLayout startProcedureView = (LinearLayout) findViewById(R.id.start_procedure_view);
        startProcedureView.setOnClickListener(startProceduresViewListener);

        langManager = new LanguageManager(this);
        langManager.loadLocale();

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        loadNearestSessions();
    }

    public void startRecord() {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    public void startCalendarView() {
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    public void startProceduresView() {
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
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        if (id == R.id.change_language) {
            if (!langManager.isRus)
                langManager.changeLang("ru");
            else
                langManager.changeLang("en");
            updateTexts();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getDbFile() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = data + "/data/" + getPackageName() + "/databases/sessions.db";
                String backupDBPath = "2.db";
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

    private ArrayList<Long> getNearestSessions(Context ctx) {
        Sessions sessions = new Sessions(ctx);
        ArrayList<Long> nearestSessionsId = sessions.getSessionsAfterTime("datetime('now')",
                MAX_SESSIONS_COUNT);
        return nearestSessionsId;
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
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
    };

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
        for (int i = 0; i < sessionCount && i < MAX_SESSIONS_COUNT; i++) {
            circleArray[i].setVisibility(View.VISIBLE);
            if (i != 0)
                circleArray[i].setColor(getResources().getColor(android.R.color.white));
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

    private View.OnClickListener startRecordListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startRecord();
        }
    };

    private View.OnClickListener startCalendarViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startCalendarView();
        }
    };

    private View.OnClickListener startProceduresViewListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startProceduresView();
        }
    };

    private void loadNearestSessions() {
        ViewPager mPager = (ViewPager) findViewById(R.id.preview_sessions_pager);
        ArrayList<Long> nearestSessions = getNearestSessions(this);
        sessionsCount = nearestSessions.size();
        TextView notRecord = (TextView) findViewById(R.id.no_records_textview);
        circleTable = (LinearLayout) findViewById(R.id.circles);

        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.addOnPageChangeListener(pageChangeListener);

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

        loadNearestSessions();
    }

//    private void changeLang(String lang) {
//        if (lang.equalsIgnoreCase(""))
//            return;
//        myLocale = new Locale(lang);
//        saveLocale(lang);
//        Locale.setDefault(myLocale);
//        android.content.res.Configuration config = new android.content.res.Configuration();
//        config.locale = myLocale;
//
//        if (lang.equals("en")) {
//            isRus = false;
//        }
//        else {
//            isRus = true;
//        }
//
//        getBaseContext().getResources().updateConfiguration(config,
//                getBaseContext().getResources().getDisplayMetrics());
//        updateTexts();
//    }
//
//
//    private void saveLocale(String lang)
//    {
//        String langPref = "Language";
//        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = prefs.edit();
//        editor.putString(langPref, lang);
//        editor.commit();
//    }
//
//
//    private void loadLocale()
//    {
//        String langPref = "Language";
//        SharedPreferences prefs = getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
//        String language = prefs.getString(langPref, "");
//        changeLang(language);
//    }

//    public void updateTexts() {
//        this.startRecordTextView.setText(R.string.start_record);
//        this.startRecordDescriptionTextView.setText(R.string.start_record_description);
//        this.startCalendarTextView.setText(R.string.calendar);
//        this.startCalendarDescriptionTextView.setText(R.string.start_calendar_description);
//        this.startProcedureTextView.setText(R.string.procedure);
//        this.startProcedureDescriptionTextView.setText(R.string.start_procedure_description);
//    }

}
