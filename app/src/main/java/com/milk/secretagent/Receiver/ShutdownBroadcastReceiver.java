package com.milk.secretagent.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.TaskHelper;
import com.milk.secretagent.Utility.Utils;

public class ShutdownBroadcastReceiver extends BroadcastReceiver {
    public ShutdownBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(AppConstants.TAG, "[ShutdownBroadcastReceiver][onReceive] Receive shutdown action: " + intent.getAction());

        Log.e(AppConstants.TAG, "[ShutdownBroadcastReceiver][onReceive] Stop all running tasks...");

        // Get all tasks
        TaskHelper.getInstance().setTaskList(Utils.loadTasks(context));
        TaskHelper.getInstance().stopRunningTasks(context);

        Log.e(AppConstants.TAG, "[ShutdownBroadcastReceiver][onReceive] All running tasks are terminated");
    }
}
