package com.milk.secretagent;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.milk.secretagent.Adapter.ViewPagerAdapter;
import com.milk.secretagent.Model.AppConfig;
import com.milk.secretagent.Model.LocationTask;
import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.ConnectionDetector;
import com.milk.secretagent.Utility.LocationHelper;
import com.milk.secretagent.Utility.TaskHelper;
import com.milk.secretagent.Utility.Utils;

import java.util.ArrayList;

public class MainActivity extends ActionBarActivity {
    static SwipeRefreshLayout swipeRefreshLayout;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    String Titles[];
    int NumberOfTabs = 3;
    int currentPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
    }

    private void initComponents() {
        // Get all tasks
        TaskHelper.getInstance().setTaskList(Utils.loadTasks(this));
        // Parse all location files
        TaskHelper.getInstance().getLocationList(TabLocation.m_LocationTaskList);

        for (LocationTask locationTask : TabLocation.m_LocationTaskList) {
            ArrayList<Location> locationList = new ArrayList<>();
            LocationHelper.getLocationInfoList(locationTask, locationList);
            LocationHelper.getInstance().addLocationList(locationList);
        }

        // Initialize the network detector
        ConnectionDetector.getInstance().initialize(this);

        // Get all app configurations
        AppConfig.getInstance().initialize(this);

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setTitle(getResources().getString(R.string.title_activity_main));
        //toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        // SwipeRefreshLayout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(onSwipeToRefresh);
        swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        swipeRefreshLayout.setEnabled(false);


        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        Titles = getResources().getStringArray(R.array.tab_titles);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, NumberOfTabs);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                //swipeRefreshLayout.setEnabled(state == ViewPager.SCROLL_STATE_IDLE);
            }

            @Override
            public void onPageSelected(int position) {
                //Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
                currentPageIndex = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
        });

        // Assigning the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsIndicatorColor);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);
    }

    private SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            swipeRefreshLayout.setRefreshing(true);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    // TODO: try to update tab content
                    //TabTask.adapterTabTasks.notifyDataSetChanged();

                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.swipe_refresh_done), Toast.LENGTH_SHORT).show();
                }
            }, 3500);
        }
    };

    public void addTask(View view) {

        Intent intent = null;

        switch (currentPageIndex) {
            case 0:
                intent = new Intent(MainActivity.this, AddTaskActivity.class);
                break;
            case 1:
                intent = new Intent(MainActivity.this, RecordingActivity.class);
                break;
            case 2:
                intent = new Intent(MainActivity.this, LocationActivity.class);
                break;
        }

        if (intent != null) {
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 0) {
            return;
        }

        boolean isAddOkay = false;
        if (resultCode == AppConstants.CODE.RESULT_RECORD_TASK) {
            Toast.makeText(this, getString(R.string.task_record_add_success), Toast.LENGTH_SHORT).show();
            isAddOkay = true;
        }
        else if (resultCode == AppConstants.CODE.RESULT_LOCATION_TASK) {
            Toast.makeText(this, getString(R.string.task_location_add_success), Toast.LENGTH_SHORT).show();
            isAddOkay = true;
        }

        if (isAddOkay) {
            TaskHelper.getInstance().getAvailableTaskList(TabTask.m_TaskList);
            TabTask.adapterTabTasks.notifyDataSetChanged();

            pager.setCurrentItem(0);
        }
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
            Intent intentSettings = new Intent(this, SettingsActivity.class);
            startActivity(intentSettings);
            return true;
        }
        else if (id == R.id.action_app_rate) {
            Uri uri = Uri.parse("market://details?id=" + this.getPackageName());
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://play.google.com/store/apps/details?id=" + this.getPackageName()));
                startActivity(intent);
            }
        }
        else if (id == R.id.action_app_info) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.milk.secretagent.pro"));
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
