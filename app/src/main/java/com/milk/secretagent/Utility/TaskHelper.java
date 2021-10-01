package com.milk.secretagent.Utility;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.milk.secretagent.Receiver.AlarmReceiver;
import com.milk.secretagent.Enum.ETaskStatus;
import com.milk.secretagent.Enum.ETaskTypes;
import com.milk.secretagent.Model.BaseTask;
import com.milk.secretagent.Model.LocationTask;
import com.milk.secretagent.Model.RecordTask;
import com.milk.secretagent.Service.RecordService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Milk on 2015/7/22.
 */
public class TaskHelper {
    private static TaskHelper ourInstance = new TaskHelper();
    private static ArrayList<BaseTask> m_SecretTasks;

    public static TaskHelper getInstance() {
        return ourInstance;
    }

    private TaskHelper() {
        this.m_SecretTasks = new ArrayList<>();
    }

    public void runTask(Context context, BaseTask task) {
        m_SecretTasks.add(0, task);

        // Save current tasks to sharedPreferences
        Utils.saveTasks(context);

        addTaskToAlarm(context, task);
    }

    public void addTaskToAlarm(Context context, BaseTask task) {
        Log.e(AppConstants.TAG, "[TaskHelper][setAlarm] Create an alarm manager.");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(task.getTimestamp());

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent();
        intent.setClass(context, AlarmReceiver.class);

        switch (task.getTaskType()) {
            case RECORD_TASK:
                intent.setAction(AppConstants.ACTION.RUN_RECORD_TASK);
                break;
            case LOCATION_TASK:
                intent.setAction(AppConstants.ACTION.RUN_LOCATION_TASK);
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putInt(AppConstants.Extra.RECORD_TASK_ALARM_ID, task.getAlarmId());
        bundle.putInt(AppConstants.Extra.RECORD_TASK_LENGTH, task.getRecordLength());
        bundle.putBoolean(AppConstants.Extra.RECORD_TASK_NOTIFICATION, task.isShowNotification());

        intent.putExtras(bundle);

        Log.e(AppConstants.TAG, String.format("[TaskHelper][runTask] AlarmId: %d", task.getAlarmId()));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, task.getAlarmId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public ArrayList<BaseTask> getTaskList() {
        return m_SecretTasks;
    }

    public void setTaskList(ArrayList<BaseTask> secretTasks) {
        this.m_SecretTasks = secretTasks;
    }

    public void removeTask(Context context, int index) {
        if (index >= m_SecretTasks.size()) {
            return ;
        }

        if (m_SecretTasks.get(index) == null) {
            return ;
        }

        m_SecretTasks.remove(index);
        Utils.saveTasks(context);
    }

    public int getAvailableAlarmId() {
        Random random = new Random();
        int alarmId;
        boolean isDuplicate = false;

        while (true) {
            int randomNumber = random.nextInt(1000); // TODO: set 1000 for test
            for (BaseTask task : m_SecretTasks) {
                if (task.getAlarmId() == randomNumber) {
                    isDuplicate = true;
                    break;
                }
            }

            if (!isDuplicate) {
                alarmId = randomNumber;
                break;
            }
            else {
                isDuplicate = false;
            }
        }

        return alarmId;
    }

    public BaseTask updateTaskStatus(int alarmId, ETaskStatus taskStatus) {
        for (BaseTask task : m_SecretTasks) {
            if (task.getAlarmId() == alarmId) {
                task.setTaskStatus(taskStatus);
                return task;
            }
        }

        return null;
    }

    public void updateRecordTaskFileInfo(Context context, int alarmId, String fileName, String filePath) {
        for (BaseTask task : m_SecretTasks) {
            if (task.getAlarmId() == alarmId) {

                File file = new File(filePath);
                MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(filePath));

                RecordTask recordTask = (RecordTask) task;
                recordTask.setFileName(fileName);
                recordTask.setFilePath(filePath);
                recordTask.setFileSize(file.length());
                recordTask.setFileLength(Math.round(mediaPlayer.getDuration() / 1000));

                break;
            }
        }
    }

    public void getAvailableTaskList(ArrayList<BaseTask> taskList) {
        if (this.m_SecretTasks.size() == 0) {
            return ;
        }

        taskList.clear();

        for (BaseTask task : this.m_SecretTasks) {
            if (task.getTaskStatus() != ETaskStatus.TASK_DELETED) {
                taskList.add(task);
            }
        }
    }

    public void getRecordList(ArrayList<RecordTask> recordTaskList) {
        if (this.m_SecretTasks.size() == 0) {
            return ;
        }

        recordTaskList.clear();

        for (BaseTask task : this.m_SecretTasks) {
            if (task.getTaskType() == ETaskTypes.RECORD_TASK) {
                if (task.getTaskStatus() == ETaskStatus.TASK_FINISHED ||
                    task.getTaskStatus() == ETaskStatus.TASK_DELETED) {
                    recordTaskList.add((RecordTask) task);
                }
            }
        }
    }

    public void getLocationList(ArrayList<LocationTask> locationTaskList) {
        if (this.m_SecretTasks.size() == 0) {
            return ;
        }

        locationTaskList.clear();

        for (BaseTask task : this.m_SecretTasks) {
            if (task.getTaskType() == ETaskTypes.LOCATION_TASK) {
                if (task.getTaskStatus() == ETaskStatus.TASK_FINISHED ||
                    task.getTaskStatus() == ETaskStatus.TASK_DELETED) {
                    locationTaskList.add((LocationTask) task);
                }
            }
        }
    }

    public void removeTaskById(Context context, int alarmId) {
        for (BaseTask task : m_SecretTasks) {
            if (task.getAlarmId() == alarmId) {
                m_SecretTasks.remove(task);
                Utils.saveTasks(context);
                break;
            }
        }
    }

    public void registerWaitingTasks(Context context) {
        for (BaseTask task : m_SecretTasks) {
            if (ETaskStatus.TASK_WAITING == task.getTaskStatus()) {
                Log.e(AppConstants.TAG, String.format("[TaskHelper][stopRunningTasks] Add this task to alarm manager. Alarm Id: %d", task.getAlarmId()));

                // Check if the alarm is expired
                if (task.getTimestamp() < System.currentTimeMillis()) {
                    Log.e(AppConstants.TAG, "[TaskHelper][stopRunningTasks] This alarm is expired");
                    continue;
                }

                addTaskToAlarm(context, task);
            }
        }
    }

    public void stopRunningTasks(Context context) {
        for (BaseTask task : m_SecretTasks) {
            if (ETaskStatus.TASK_RUNNING == task.getTaskStatus()) {
                Log.e(AppConstants.TAG, String.format("[TaskHelper][stopRunningTasks] Terminate this running service. Alarm Id: %d", task.getAlarmId()));
                Intent intentStop = new Intent(context, RecordService.class);
                context.stopService(intentStop);
            }
        }
    }

    public boolean isSetPastTime(BaseTask newTask) {
        if (newTask.getTimestamp() <= System.currentTimeMillis()) {
            return true;
        }

        return false;
    }

    public boolean isTaskAvailable(BaseTask newTask) {

        String format = "yyyy/MM/dd ah:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.TAIWAN);

        for (BaseTask task : m_SecretTasks) {
            if (task.getTaskType() != newTask.getTaskType()) {
                continue;
            }

            if (ETaskStatus.TASK_WAITING == task.getTaskStatus() ||
                ETaskStatus.TASK_RUNNING == task.getTaskStatus()) {

                long startTimestamp = task.getTimestamp();
                long stopTimestamp = startTimestamp + task.getRecordLength() * 60 * 1000; // to milliseconds

                long newStartTimestamp = newTask.getTimestamp();
                long newStopTimestamp = newStartTimestamp + newTask.getRecordLength() * 60 * 1000;

                Calendar calendarTask = Calendar.getInstance();
                calendarTask.setTimeInMillis(startTimestamp);
                Log.e(AppConstants.TAG, "Task: " + simpleDateFormat.format(calendarTask.getTime()));

                Calendar calendarNewTask = Calendar.getInstance();
                calendarNewTask.setTimeInMillis(newStartTimestamp);
                Log.e(AppConstants.TAG, "New Task: " + simpleDateFormat.format(calendarNewTask.getTime()));

                if (newStartTimestamp >= startTimestamp && newStartTimestamp <= stopTimestamp) {
                    return false;
                }

                if (newStopTimestamp >= startTimestamp && newStopTimestamp <= stopTimestamp) {
                    return false;
                }

                if (startTimestamp >= newStartTimestamp && startTimestamp <= newStopTimestamp) {
                    return false;
                }

                if (stopTimestamp >= newStartTimestamp && stopTimestamp <= newStopTimestamp) {
                    return false;
                }
            }
        }

        return true;
    }
}
