package ru.anroidapp.plannerwork;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    Context cntxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cntxt = this;

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        initializeNavigationDrawer(toolbar);

    }

    private void initializeNavigationDrawer(Toolbar toolbar) {

        AccountHeader resultHeder = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .build();

        Drawer resultDrawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withDisplayBelowToolbar(true)
                .withAccountHeader(resultHeder)
                .withActionBarDrawerToggleAnimated(true)
                .addDrawerItems(
                        new PrimaryDrawerItem()
                                .withName(R.string.navDraw_1)
                                .withIcon(R.drawable.ic_home_black_18dp),
                        // new DividerDrawerItem(),
                        new SecondaryDrawerItem()
                                .withName(R.string.navDraw_2)
                                .withIcon(R.drawable.ic_settings_applications_black_18dp),
                        new SecondaryDrawerItem()
                                .withName(R.string.navDraw_3)
                                .withIcon(R.drawable.ic_stars_black_18dp)
                )
                        //            .withSliderBackgroundColor(R.color.md_grey_100)
                .build();

    }

    public void onBtn1Click(View view) {
        startActivity(new Intent(this, RecordActivity.class));
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
