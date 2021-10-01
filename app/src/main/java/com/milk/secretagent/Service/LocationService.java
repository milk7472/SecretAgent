package com.milk.secretagent.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.milk.secretagent.Enum.ETaskStatus;
import com.milk.secretagent.Enum.ETaskTypes;
import com.milk.secretagent.Model.AppConfig;
import com.milk.secretagent.Model.LocationTask;
import com.milk.secretagent.R;
import com.milk.secretagent.TabLocation;
import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.LocationHelper;
import com.milk.secretagent.Utility.TaskHelper;
import com.milk.secretagent.Utility.Utils;

import java.util.ArrayList;
import java.util.Calendar;


public class LocationService extends Service implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = AppConstants.TAG;
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 3;
    LocationRequest m_LocationRequest;
    GoogleApiClient m_GoogleApiClient;
    Location m_CurrentLocation;

    LocationTask m_LocationTask;
    int m_AlarmId;
    int m_RecordLength;
    boolean m_IsShowNotification;
    CountDownTimer m_CountDownTimer;
    ArrayList<Location> m_LocationList = new ArrayList<>();

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get task information
        Bundle bundle = intent.getExtras();

        m_AlarmId = bundle.getInt(AppConstants.Extra.RECORD_TASK_ALARM_ID);
        m_RecordLength = bundle.getInt(AppConstants.Extra.RECORD_TASK_LENGTH);
        m_IsShowNotification = bundle.getBoolean(AppConstants.Extra.RECORD_TASK_NOTIFICATION);

        Log.e(AppConstants.TAG, String.format("[LocationService][onStartCommand] AlarmId: %d Length: %d mins", m_AlarmId, m_RecordLength));

        if (m_IsShowNotification) {
            Log.e(AppConstants.TAG, "[LocationService][onStartCommand] Show notification");

            setupNotification();
        }
        else {
            Log.e(AppConstants.TAG, "[LocationService][onStartCommand] Not show notification");
            setupEmptyNotification();
        }

        // Get target location task and update properties
        TaskHelper.getInstance().setTaskList(Utils.loadTasks(this));
        m_LocationTask = (LocationTask) TaskHelper.getInstance().updateTaskStatus(m_AlarmId, ETaskStatus.TASK_RUNNING);
        m_LocationTask.setStartTimestamp(Calendar.getInstance().getTimeInMillis());

        String folderPath = Utils.getFolderPath(ETaskTypes.LOCATION_TASK);
        String fileName = Utils.getDefaultFileName();
        String filePath = folderPath + fileName + AppConstants.File.LOCATION_SUFFIX;

        m_LocationTask.setFileName(fileName);
        m_LocationTask.setFilePath(filePath);

        Utils.saveTasks(this);

        Toast.makeText(this, getResources().getString(R.string.location_service_start), Toast.LENGTH_SHORT).show();

        Log.e(AppConstants.TAG, "[LocationService] Location service start!");

        // Get record config from sharedPreferences
        AppConfig.getInstance().initialize(this);

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            Log.e(TAG, "Google play services is not available");
            stopSelf();
        }
        createLocationRequest();
        m_GoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        m_GoogleApiClient.connect();

        // Set up stop timer
        int recordOffset = 1000; // addition add 1 sec to offset the service delay time
        m_CountDownTimer = new CountDownTimer(m_RecordLength * 60 * 1000 + recordOffset, 60 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(TAG, "(update every 1 min) Seconds remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                Log.e(TAG, "[LocationService][Timer] Finish the timer. Save task");
                m_LocationTask.setFinishTimestamp(Calendar.getInstance().getTimeInMillis());
                m_LocationList.add(m_CurrentLocation);

                LocationHelper.saveLocationInfo(m_LocationTask, m_LocationList);
                TaskHelper.getInstance().updateTaskStatus(m_AlarmId, ETaskStatus.TASK_FINISHED);
                Utils.saveTasks(getApplicationContext());

                stopSelf();
            }
        };
        m_CountDownTimer.start();

        return START_REDELIVER_INTENT;
    }

    private void setupNotification() {
        // TODO: think of what the notification looks like better
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
        inboxStyle.setBigContentTitle("Recording...");
        inboxStyle.setSummaryText("More messages...");
        inboxStyle.addLine(String.format("Record length: %d", m_RecordLength));
        inboxStyle.addLine(String.format("Alarm Id: %d", m_AlarmId));

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.notification_location)
                .setContentTitle("Secret Agent")
                .setContentText("Task running")
                .setStyle(inboxStyle)
                .build();

        startForeground(AppConstants.Service.LOCATION_SERVICE_NOTIFY_ID, notification);
        notificationManager.notify(AppConstants.Service.LOCATION_SERVICE_NOTIFY_ID, notification);
    }

    private void setupEmptyNotification() {
        Notification notification = new Notification();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            notification.priority = Notification.PRIORITY_MIN;
        }
        startForeground(AppConstants.Service.RECORD_SERVICE_NOTIFY_ID, notification);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "[LocationService][onDestroy]");
        m_GoogleApiClient.disconnect();
        Log.e(TAG, "[LocationService][onDestroy] Is google api client disconnected: " + m_GoogleApiClient.isConnected());

        if (null != TabLocation.m_AdapterTabLocations) {
            TabLocation.m_AdapterTabLocations.notifyDataSetChanged();
        }

        // Cancel the countdown timer
        if (m_CountDownTimer != null) {
            m_CountDownTimer.cancel();
            m_CountDownTimer.onFinish();
        }

        super.onDestroy();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        return ConnectionResult.SUCCESS == status;
    }

    private void createLocationRequest() {
        int interval = AppConfig.getInstance().getLocationTimeSlot() * 1000;
        int fastInterval = interval / 2;
        int priority = AppConfig.getInstance().getLocationPriority();
        int smallestDisplacement = AppConfig.getInstance().getLocationAccuracy();

        m_LocationRequest = new LocationRequest();
        m_LocationRequest.setInterval(interval);
        m_LocationRequest.setFastestInterval(fastInterval);
        m_LocationRequest.setPriority(priority);
        m_LocationRequest.setSmallestDisplacement(smallestDisplacement);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "[LocationService][createLocationRequest] Is connected: " + m_GoogleApiClient.isConnected());
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                m_GoogleApiClient, m_LocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, String.format("[LocationService][onLocationChanged] latitude: %f longitude: %f", location.getLatitude(), location.getLongitude()));
        m_CurrentLocation = location;
        m_LocationList.add(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "[LocationService][onConnectionFailed] Connection failed: " + connectionResult.toString());
    }
}
