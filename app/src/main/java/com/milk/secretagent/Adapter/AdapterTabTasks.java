package com.milk.secretagent.Adapter;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.milk.secretagent.Receiver.AlarmReceiver;
import com.milk.secretagent.Enum.ETaskStatus;
import com.milk.secretagent.Enum.ETaskTypes;
import com.milk.secretagent.R;
import com.milk.secretagent.Service.LocationService;
import com.milk.secretagent.Service.RecordService;
import com.milk.secretagent.TabTask;
import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.TaskHelper;
import com.milk.secretagent.Utility.Utils;
import com.milk.secretagent.Model.BaseTask;

import java.util.ArrayList;

/**
 * Created by Milk on 2015/7/14.
 */
public class AdapterTabTasks extends RecyclerView.Adapter<AdapterTabTasks.ViewHolder> {
    Context m_Context;
    ArrayList<BaseTask> m_TaskList;

    public AdapterTabTasks(Context context, ArrayList<BaseTask> taskList) {
        this.m_Context = context;
        this.m_TaskList = taskList;
    }

    @Override
    public AdapterTabTasks.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                                  .inflate(R.layout.cardview_task, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterTabTasks.ViewHolder viewHolder, final int position) {
        final BaseTask task = this.m_TaskList.get(position);

        // Set task start date and time
        viewHolder.m_DateTime.setText(task.getCreateTime());

        // Set task record length
        String recordLength = String.format(m_Context.getString(R.string.task_record_length), Utils.formatMinutes(m_Context, task.getRecordLength()));
        viewHolder.m_Length.setText(recordLength);

        // Set task type
        if (task.getTaskType() == ETaskTypes.RECORD_TASK) {
            viewHolder.m_TaskType.setImageResource(R.drawable.btn_add_task_record);
        }
        else if (task.getTaskType() == ETaskTypes.LOCATION_TASK) {
            viewHolder.m_TaskType.setImageResource(R.drawable.btn_add_task_location);
        }

        // Set the task is needed to show notification
        if (task.isShowNotification()) {
            viewHolder.m_Notification.setText(m_Context.getString(R.string.task_show_notification));
        }
        else {
            viewHolder.m_Notification.setText(m_Context.getString(R.string.task_not_show_notification));
        }

        // Handle delete task event
        viewHolder.m_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dialogMessage = task.getTaskStatus() == ETaskStatus.TASK_RUNNING ?
                        m_Context.getString(R.string.task_delete_dialog_running_message) :
                        m_Context.getString(R.string.task_delete_dialog_message);

                new AlertDialog.Builder(m_Context)
                        .setTitle(R.string.task_delete_dialog_title)
                        .setMessage(dialogMessage)
                        .setPositiveButton(m_Context.getResources().getString(android.R.string.ok),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (task.getTaskStatus() == ETaskStatus.TASK_WAITING) {
                                            // Cancel this alarm
                                            Log.e(AppConstants.TAG, String.format("Cancel alarm (%s). Id: %d",task.getCreateTime(), task.getAlarmId()));
                                            AlarmManager alarmManager = (AlarmManager) m_Context.getSystemService(Context.ALARM_SERVICE);

                                            Intent intentCancel = new Intent();
                                            intentCancel.setClass(m_Context, AlarmReceiver.class);

                                            if (task.getTaskType() == ETaskTypes.RECORD_TASK) {
                                                intentCancel.setAction(AppConstants.ACTION.RUN_RECORD_TASK);
                                            }
                                            else if (task.getTaskType() == ETaskTypes.LOCATION_TASK) {
                                                intentCancel.setAction(AppConstants.ACTION.RUN_LOCATION_TASK);
                                            }

                                            PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(m_Context, task.getAlarmId(), intentCancel, PendingIntent.FLAG_NO_CREATE);
                                            if (pendingIntentCancel != null) {
                                                Log.e(AppConstants.TAG, "not null, cancel this alarm");
                                                alarmManager.cancel(pendingIntentCancel);
                                            }
                                            else {
                                                Log.e(AppConstants.TAG, "this is a null pending intent");
                                            }
                                        }
                                        else if (task.getTaskStatus() == ETaskStatus.TASK_RUNNING) {
                                            Log.e(AppConstants.TAG, "This task is running, terminate the service");
                                            Intent intentStop = null;
                                            if (task.getTaskType() == ETaskTypes.RECORD_TASK) {
                                                intentStop = new Intent(m_Context, RecordService.class);
                                            }
                                            else if (task.getTaskType() == ETaskTypes.LOCATION_TASK) {
                                                intentStop = new Intent(m_Context, LocationService.class);
                                            }

                                            m_Context.stopService(intentStop);
                                        }
                                        else if (task.getTaskStatus() == ETaskStatus.TASK_FINISHED) {
                                            Log.e(AppConstants.TAG, "This task is finished, update the status");
                                        }

                                        TaskHelper.getInstance().updateTaskStatus(task.getAlarmId(), ETaskStatus.TASK_DELETED);
                                        Utils.saveTasks(m_Context);
                                        m_TaskList.remove(position);
                                        TabTask.adapterTabTasks.notifyDataSetChanged();
                                    }
                                })
                        .setNegativeButton(m_Context.getResources().getString(android.R.string.cancel),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                        .show();
            }
        });

        // Set task status
        int statusResourceId = R.string.task_enum_status_waiting;
        switch (task.getTaskStatus()) {
            case TASK_WAITING:
                statusResourceId = R.string.task_enum_status_waiting;
                break;
            case TASK_RUNNING:
                statusResourceId = R.string.task_enum_status_running;
                viewHolder.m_Status.setTextColor(m_Context.getResources().getColor(R.color.secret_agent_red));
                break;
            case TASK_FINISHED:
                statusResourceId = R.string.task_enum_status_finished;
                break;
        }
        viewHolder.m_Status.setText(m_Context.getResources().getString(statusResourceId));
    }

    @Override
    public int getItemCount() {
        return this.m_TaskList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView m_DateTime;
        public TextView m_Length;
        public TextView m_Notification;
        public TextView m_Status;
        public ImageView m_TaskType;
        public ImageView m_Delete;

        public ViewHolder(View view) {
            super(view);
            m_DateTime = (TextView) view.findViewById(R.id.task_date_time);
            m_Length = (TextView) view.findViewById(R.id.task_minutes);
            m_Notification = (TextView) view.findViewById(R.id.task_notification);
            m_Status = (TextView) view.findViewById(R.id.task_status);
            m_TaskType = (ImageView) view.findViewById(R.id.task_icon);
            m_Delete = (ImageView) view.findViewById(R.id.task_delete);
        }
    }
}
