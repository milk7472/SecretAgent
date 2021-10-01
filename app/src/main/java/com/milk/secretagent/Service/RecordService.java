package com.milk.secretagent.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.Task;
import com.milk.secretagent.Enum.ETaskStatus;
import com.milk.secretagent.Enum.ETaskTypes;
import com.milk.secretagent.Model.AppConfig;
import com.milk.secretagent.Model.RecordTask;
import com.milk.secretagent.R;
import com.milk.secretagent.TabTask;
import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.TaskHelper;
import com.milk.secretagent.Utility.Utils;
import com.milk.secretagent.Utility.VoiceRecorderHelper;

import java.util.Calendar;

public class RecordService extends Service {
    int m_AlarmId;
    int m_RecordLength;
    boolean m_IsShowNotification;
    CountDownTimer m_CountDownTimer;
    String m_FileName;
    String m_FilePath;
    RecordTask m_RecordTask;

    public RecordService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        return null;
    }

    @Override
    public void onCreate() {
        Log.e(AppConstants.TAG, "[RecordService][onCreate]");

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(AppConstants.TAG, "[RecordService][onStartCommand]");
        final Bundle bundle = intent.getExtras();

        m_AlarmId = bundle.getInt(AppConstants.Extra.RECORD_TASK_ALARM_ID);
        m_RecordLength = bundle.getInt(AppConstants.Extra.RECORD_TASK_LENGTH);
        m_IsShowNotification = bundle.getBoolean(AppConstants.Extra.RECORD_TASK_NOTIFICATION);

        Log.e(AppConstants.TAG, String.format("[RecordService][onStartCommand] AlarmId: %d Length: %d", m_AlarmId, m_RecordLength));

        if (m_IsShowNotification) {
            Log.e(AppConstants.TAG, "[RecordService][onStartCommand] Show notification");

            setupNotification();
        }
        else {
            Log.e(AppConstants.TAG, "[RecordService][onStartCommand] Not show notification");
            setupEmptyNotification();
        }

        Toast.makeText(this, getResources().getString(R.string.record_service_start), Toast.LENGTH_SHORT).show();

        Log.e(AppConstants.TAG, "[RecordService] Record service start!");

        // Get record config from sharedPreferences
        AppConfig.getInstance().initialize(this);

        // Start recording
        m_FileName = Utils.getDefaultFileName();
        m_FilePath = VoiceRecorderHelper.getInstance().initialize(m_FileName);
        VoiceRecorderHelper.getInstance().startRecording();

        // Get tasks from sharedPreferences
        TaskHelper.getInstance().setTaskList(Utils.loadTasks(this));
        m_RecordTask = (RecordTask) TaskHelper.getInstance().updateTaskStatus(m_AlarmId, ETaskStatus.TASK_RUNNING);
        m_RecordTask.setStartTimestamp(Calendar.getInstance().getTimeInMillis());
        Utils.saveTasks(this);


        if (TabTask.adapterTabTasks != null) {
            TabTask.adapterTabTasks.notifyDataSetChanged();
        }

        int recordOffset = 1000; // addition add 1 sec to offset the service delay time
        m_CountDownTimer = new CountDownTimer(m_RecordLength * 60 * 1000 + recordOffset, 1 * 60 * 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(AppConstants.TAG, "(update every 1 min) Seconds remaining: " + millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {
                Log.e(AppConstants.TAG, "[RecordService][onFinish] Finish the timer. Save task");
                // Stop recording
                VoiceRecorderHelper.getInstance().stopRecording();
                m_RecordTask.setFinishTimestamp(Calendar.getInstance().getTimeInMillis());

//                boolean isUseAAC = false;
//                if (AppConfig.getInstance().getAudioEncoder() == MediaRecorder.AudioEncoder.AAC) {
//                    isUseAAC = true;
//                }
//
//                // Move the recording file from temp/ to Recordings/ folder
//                String filePath = Utils.moveFileToFormalFolder(m_FileName, isUseAAC);

                // Update status of this record task
                TaskHelper.getInstance().updateTaskStatus(m_AlarmId, ETaskStatus.TASK_FINISHED);
                TaskHelper.getInstance().updateRecordTaskFileInfo(getApplicationContext(), m_AlarmId, m_FileName, m_FilePath);
                Utils.saveTasks(getApplicationContext());

                stopSelf();
            }
        };
        m_CountDownTimer.start();

        //return super.onStartCommand(intent, flags, startId);
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
                .setSmallIcon(R.drawable.notification_recording)
                .setContentTitle("Secret Agent")
                .setContentText("Task running")
                .setStyle(inboxStyle)
                .build();

        startForeground(AppConstants.Service.RECORD_SERVICE_NOTIFY_ID, notification);
        notificationManager.notify(AppConstants.Service.RECORD_SERVICE_NOTIFY_ID, notification);
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
        Log.e(AppConstants.TAG, "[RecordService][onDestroy]");

        if (TabTask.adapterTabTasks != null) {
            TabTask.adapterTabTasks.notifyDataSetChanged();
        }

        // Cancel the countdown timer
        if (m_CountDownTimer != null) {
            m_CountDownTimer.cancel();
            m_CountDownTimer.onFinish();
        }

        super.onDestroy();
    }
}
