package com.milk.secretagent.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.TaskHelper;
import com.milk.secretagent.Utility.Utils;

public class BootBroadcastReceiver extends BroadcastReceiver {
    public BootBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e(AppConstants.TAG, "[BootBroadcastReceiver][onReceive] Receive boot action: " + intent.getAction());
        if (!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            return;
        }

        Log.e(AppConstants.TAG, "[BootBroadcastReceiver][onReceive] Re-register all waiting tasks.");
        // Get all tasks
        TaskHelper.getInstance().setTaskList(Utils.loadTasks(context));
        TaskHelper.getInstance().registerWaitingTasks(context);

        Log.e(AppConstants.TAG, "[BootBroadcastReceiver][onReceive] All waiting tasks are re-add to alarm manager");
    }
}
