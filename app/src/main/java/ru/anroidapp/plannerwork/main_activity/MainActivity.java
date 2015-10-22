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

import com.example.smena.clientbase.procedures.Sessions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import ru.anroidapp.plannerwork.CalendarActivity;
import ru.anroidapp.plannerwork.ProcedureActivity;
import ru.anroidapp.plannerwork.R;
import ru.anroidapp.plannerwork.RecordActivity;
import ru.anroidapp.plannerwork.main_activity.slide_nearest_sessions.ScreenSlidePagerAdapter;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";
    //Context mContext;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mContext = this;

        getDbFile();
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));

        int count = getCountNearestSessions(this);
        if (count > 0) {
            mPager = (ViewPager) findViewById(R.id.pagerMain);
            mPagerAdapter = new ScreenSlidePagerAdapter(this, getSupportFragmentManager(), count);
            mPager.setAdapter(mPagerAdapter);
        } else if (count == 0) {
        }

    }


    public void onBtn1Click(View view) {
        Intent intent = new Intent(this, RecordActivity.class);
        startActivity(intent);
    }

    public void onBtn2Click(View view) {
        startActivity(new Intent(this, CalendarActivity.class));
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

    private int getCountNearestSessions(Context ctx) {
        Sessions sessions = new Sessions(ctx);
        ArrayList<Long> nearestSessionsId = sessions.getSessionsAfterTime("datetime('now')");
        if (nearestSessionsId != null)
            return nearestSessionsId.size();
        else if (nearestSessionsId.isEmpty())
            return 0;
        else
            return -1;
    }

}
