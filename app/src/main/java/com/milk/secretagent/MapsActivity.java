package com.milk.secretagent;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.milk.secretagent.Adapter.AdapterMapMarker;
import com.milk.secretagent.Model.AppConfig;
import com.milk.secretagent.Model.LocationTask;
import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.LocationHelper;
import com.milk.secretagent.Utility.Utils;

import java.math.BigDecimal;
import java.util.ArrayList;

public class MapsActivity extends ActionBarActivity {
    Toolbar m_Toolbar;
    GoogleMap m_Map; // Might be null if Google Play services APK is not available.
    int m_Index;
    LocationTask m_LocationTask;

    boolean m_isShowInfo = true;
    LinearLayout m_LayoutMapInfo;

    TextView m_TextTotalDistance;
    TextView m_TextTotalMoveTime;
    TextView m_TextAverageSpeed;
    TextView m_TextAverageAltitude;
    TextView m_TextAverageAccuracy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initialize();

        setUpMapIfNeeded();

        Utils.m_ProgressDialog.dismiss();

        startAnimation(true);
    }

    private void initialize() {
        m_Index = getIntent().getIntExtra(AppConstants.Extra.MAP_LOCATION_INDEX, 0);
        m_LocationTask = TabLocation.m_LocationTaskList.get(m_Index);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        m_Toolbar = (Toolbar) findViewById(R.id.tool_bar_map);
        m_Toolbar.setTitle(m_LocationTask.getCreateTime());
        setSupportActionBar(m_Toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        m_LayoutMapInfo = getView(R.id.map_layout_info);
        m_TextTotalDistance = getView(R.id.map_total_distance);
        m_TextTotalMoveTime = getView(R.id.map_total_move_time);
        m_TextAverageSpeed = getView(R.id.map_average_speed);
        m_TextAverageAltitude = getView(R.id.map_average_altitude);
        m_TextAverageAccuracy = getView(R.id.map_average_accuracy);
    }

    public final <E extends View> E
    getView(int id)
    {
        return (E)findViewById(id);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #m_Map} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (m_Map == null) {
            // Try to obtain the map from the SupportMapFragment.
            m_Map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (m_Map != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #m_Map} is not null.
     */
    private void setUpMap() {
        ArrayList<LatLng> pointList = LocationHelper.getInstance().getLocationPointsByIndex(m_Index);

        m_Map.setMyLocationEnabled(true);
        m_Map.setMapType(m_Map.MAP_TYPE_NORMAL);
        m_Map.getUiSettings().setCompassEnabled(true);
        m_Map.getUiSettings().setZoomControlsEnabled(true);
        m_Map.getUiSettings().setMapToolbarEnabled(false);

        // Add start and end markers
        String startSnippet =
                String.format(getResources().getString(R.string.map_marker_snippet_address), m_LocationTask.getStartAddress()) +
                "," +
                String.format(getResources().getString(R.string.map_marker_snippet_time), m_LocationTask.getStartTime());

        m_Map.addMarker(new MarkerOptions().position(pointList.get(0))
                .title(getResources().getString(R.string.map_marker_title_start))
                .snippet(startSnippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        String finishSnippet =
                String.format(getResources().getString(R.string.map_marker_snippet_address), m_LocationTask.getEndAddress()) +
                "," +
                String.format(getResources().getString(R.string.map_marker_snippet_time), m_LocationTask.getFinishTime());

        m_Map.addMarker(new MarkerOptions().position(pointList.get(pointList.size() - 1))
                .title(getResources().getString(R.string.map_marker_title_end))
                .snippet(finishSnippet)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        // Move camera to start location
        //m_Map.animateCamera(CameraUpdateFactory.newLatLngZoom(pointList.get(0), 14));
        m_Map.moveCamera(CameraUpdateFactory.newLatLngZoom(pointList.get(0), 13));

        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(pointList);
        lineOptions.width(10);
        lineOptions.color(Color.CYAN);

        m_Map.addPolyline(lineOptions);

        m_Map.setInfoWindowAdapter(new AdapterMapMarker(getLayoutInflater()));
//        m_Map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                marker.showInfoWindow();
//                return false;
//            }
//        });

        setupTotalDistance();
        setupTotalMoveTime();
        setupAverageSpeed();
        setupAverageAltitude();
        setupAverageAccuracy();
    }

    private void setupTotalDistance() {
        int currentUnit = AppConfig.getInstance().getLocationUnit();
        String stringValue;
        String unit = "";

        float totalDistance = LocationHelper.getInstance().getTotalDistance(m_Index); // meter

        if (AppConfig.METRIC_UNIT == currentUnit) {
            // 1 kilometer = 1000 meter
            if (totalDistance >= 1000) {
                totalDistance /= 1000;
                unit = getString(R.string.map_unit_metric_km);
            }
            else {
                unit = getString(R.string.map_unit_metric_m);
            }
        }
        else if (AppConfig.IMPERIAL_UNIT == currentUnit) {
            // 1 meter = 3.28 foot
            // 1 mile = 5280 foot
            totalDistance *= 3.28; // to foot
            if (totalDistance >= 5280) {
                totalDistance /= 5280;
                unit = getString(R.string.map_unit_imperial_mi);
            }
            else {
                unit = getString(R.string.map_unit_imperial_ft);
            }
        }

        BigDecimal roundTotalDistance = new BigDecimal(totalDistance).setScale(2, BigDecimal.ROUND_HALF_UP);
        stringValue = String.format("%s %s", roundTotalDistance.toString(), unit);

        m_TextTotalDistance.setText(String.format(getString(R.string.map_total_distance), stringValue));
    }

    private void setupTotalMoveTime() {
        int totalMoveSeconds = LocationHelper.getInstance().getTotalMoveTime(m_Index);

        String stringTotalMoveTime = String.format(
                getString(R.string.map_total_move_time),
                Utils.formatSeconds(totalMoveSeconds)
        );

        m_TextTotalMoveTime.setText(stringTotalMoveTime);
    }

    private void setupAverageSpeed() {
        int currentUnit = AppConfig.getInstance().getLocationUnit();
        float averageSpeed = LocationHelper.getInstance().getAverageSpeed(m_Index); // meter/second
        String unit = getString(R.string.map_unit_metric_speed);

        if (AppConfig.METRIC_UNIT == currentUnit) {
            averageSpeed *= 3.6; // from m/sec to km/hr
        }
        else if (AppConfig.IMPERIAL_UNIT == currentUnit) {
            averageSpeed *= 2.236936; // from m/sec to mi/hr
            unit = getString(R.string.map_unit_imperial_speed);
        }

        BigDecimal roundAverageSpeed = new BigDecimal(averageSpeed).setScale(2, BigDecimal.ROUND_HALF_UP);

        String stringAverageSpeed = String.format(
                getString(R.string.map_average_speed),
                String.format("%s %s", roundAverageSpeed.toString(), unit)
        );

        m_TextAverageSpeed.setText(stringAverageSpeed);
    }

    private void setupAverageAltitude() {
        int currentUnit = AppConfig.getInstance().getLocationUnit();
        float averageAltitude = LocationHelper.getInstance().getAverageAltitude(m_Index); // meter
        String unit = getString(R.string.map_unit_metric_m);

        if (AppConfig.IMPERIAL_UNIT == currentUnit) {
            averageAltitude *= 3.28084; // meter to foot
            unit = getString(R.string.map_unit_imperial_ft);
        }

        BigDecimal roundAverageAltitude = new BigDecimal(averageAltitude).setScale(2, BigDecimal.ROUND_HALF_UP);

        String stringAverageAltitude = String.format(
                getString(R.string.map_average_altitude),
                String.format("%s %s", roundAverageAltitude.toString(), unit)
        );

        m_TextAverageAltitude.setText(stringAverageAltitude);
    }

    private void setupAverageAccuracy() {
        int currentUnit = AppConfig.getInstance().getLocationUnit();
        float averageAccuracy = LocationHelper.getInstance().getAverageAccuracy(m_Index); // meter
        String unit = getString(R.string.map_unit_metric_m);

        if (AppConfig.IMPERIAL_UNIT == currentUnit) {
            averageAccuracy *= 3.28084; // meter to foot
            unit = getString(R.string.map_unit_imperial_ft);
        }

        BigDecimal roundAverageAccuracy = new BigDecimal(averageAccuracy).setScale(2, BigDecimal.ROUND_HALF_UP);

        String stringAverageAccuracy = String.format(
                getString(R.string.map_average_accuracy),
                String.format("%s %s", roundAverageAccuracy.toString(), unit)
        );

        m_TextAverageAccuracy.setText(stringAverageAccuracy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_map_info) {

            m_isShowInfo = m_isShowInfo ? false : true;

            if (m_isShowInfo) {
                item.setIcon(R.drawable.action_map_info_white);
            }
            else {
                item.setIcon(R.drawable.action_map_info_dark);
            }

            startAnimation(m_isShowInfo);

            return true;
        }
        else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void startAnimation(boolean isShow) {

        AnimationSet animationSet = new AnimationSet(true);

        AlphaAnimation alphaAnimation;
        TranslateAnimation translateAnimation;

        if (isShow) {
            alphaAnimation = new AlphaAnimation(0, 1);
            translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, -1f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f
            );
        }
        else {
            alphaAnimation = new AlphaAnimation(1, 0);
            translateAnimation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, -1f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, 0f
            );
        }

        translateAnimation.setDuration(600);
        animationSet.addAnimation(translateAnimation);
        //animationSet.addAnimation(alphaAnimation);
        animationSet.setFillAfter(true);
        m_LayoutMapInfo.startAnimation(animationSet);
    }
}
