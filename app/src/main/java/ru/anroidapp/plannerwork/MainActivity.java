package ru.anroidapp.plannerwork;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smena.clientbase.procedures.Sessions;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;
import java.util.Calendar;

import ru.anroidapp.plannerwork.ScreenSlide.ScreenSlidePageFragment1;
import ru.anroidapp.plannerwork.ScreenSlide.ScreenSlidePageFragment2;
import ru.anroidapp.plannerwork.ScreenSlide.ScreenSlidePageFragment3;
import ru.anroidapp.plannerwork.ScreenSlide.ScreenSlidePageFragment4;
import ru.anroidapp.plannerwork.ScreenSlide.ScreenSlidePageFragment5;


public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    Toolbar toolbar;
    Context cntxt;
    TextView textView;
    LinearLayout LayCircle1;
    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    int closeId[] = {-1, -1, -1, -1, -1};
    int  maxYear, maxMonth, maxDay, maxHourStart, maxMinuteStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cntxt = this;

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setBackgroundColor(getResources().getColor(R.color.ColorPrimary));

        mPager = (ViewPager) findViewById(R.id.pagerMain);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        LayCircle1 = (LinearLayout) findViewById(R.id.LayCircle1);

        mPager.setPageTransformer(true, new ZoomOutPageTransformer());

        Sessions sessions = new Sessions(MainActivity.this);
        ArrayList<Long> sessionsId = sessions.getAllSessionsId();

       // String currentDateTimeString = DateFormat.format("yyyy-MM-dd kk:mm:ss", new Date());

       // Calendar calendar = Calendar.getInstance();

       // int year_close = calendar.get(calendar.YEAR);
       // int month_close = calendar.get(calendar.MONTH)+1;
      //  int day_close = calendar.get(calendar.DAY_OF_MONTH);
      //  int hour_close = calendar.get(calendar.HOUR_OF_DAY);
       // int minute_close = calendar.get(calendar.MINUTE);

    }


    public class ZoomOutPageTransformer implements ViewPager.PageTransformer{
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        public void transformPage(View view, float position){
            int pageWidth = view.getWidth();
            int pageHeight = view.getHeight();

            if(position < -1){
                view.setAlpha(0);
            }else if (position <=1 ){
                float scaleFactor = Math.max(MIN_SCALE, 1- Math.abs(position));
                float vertMargin = pageHeight * (1 - scaleFactor)/2;
                float horzMargin = pageWidth * (1 - scaleFactor)/2;
                if(position < 0){
                    view.setTranslationX(horzMargin - vertMargin/2);
                }else{
                    view.setTranslationX(-horzMargin + vertMargin/2);
                }

                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)/(1 - MIN_SCALE)*(1 - MIN_ALPHA));
            }else {
                view.setAlpha(0);
            }
        }
    }

    @Override
    public void onBackPressed(){
        if(mPager.getCurrentItem() == 0){
            super.onBackPressed();
        }else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm2) {
            super(fm2);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new ScreenSlidePageFragment1();
            }
            if (position == 1) {
                return new ScreenSlidePageFragment2();
            }
            if (position == 2) {
                return new ScreenSlidePageFragment3();
            }
            if (position == 3) {
                return new ScreenSlidePageFragment4();
            }
            if (position == 4) {
                return new ScreenSlidePageFragment5();
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
