package ru.anroidapp.plannerwork.main_activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import ru.anroidapp.plannerwork.CalendarActivity;
import ru.anroidapp.plannerwork.ProcedureActivity;
import ru.anroidapp.plannerwork.R;
import ru.anroidapp.plannerwork.animation.Circle;
import ru.anroidapp.plannerwork.animation.ZoomOutPageTransformer;
import ru.anroidapp.plannerwork.record.RecordActivity;
import ru.anroidapp.plannerwork.main_activity.slide_nearest_sessions.ScreenSlidePagerAdapter;


public class MainActivity extends AppCompatActivity {

    private Circle[] circleArray;
    private final int MAX_SESSIONS_COUNT = 5;
    private int sessionsCount;
    private LinearLayout circleTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mContext = this;

        //getDbFile();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        ViewPager mPager = (ViewPager) findViewById(R.id.pagerMain);
        ArrayList<Long> nearestSessions = getNearestSessionsCount(this);
        sessionsCount = nearestSessions.size();
        TextView notRecord = (TextView) findViewById(R.id.txtNotRecord);
        circleTable = (LinearLayout) findViewById(R.id.circles);

        initCircles();

        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
        mPager.addOnPageChangeListener(pageChangeListener);

            if (nearestSessions != null && !nearestSessions.isEmpty()) {
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
            } else {
            }
    }

    public void onBtn1Click(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    public void onBtn2Click(View view) {
       // startActivity(new Intent(this, CalendarActivity.class));
         Intent i = new Intent(MainActivity.this, CalendarActivity.class);
         i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
          startActivity(i);
    }

    public void onBtn3Click(View view) {
        startActivity(new Intent(this, ProcedureActivity.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        if (id == R.id.action_settings) {
            return true;
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

    private ArrayList<Long> getNearestSessionsCount(Context ctx) {
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

}
