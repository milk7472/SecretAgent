package com.milk.secretagent.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.milk.secretagent.Service.LocationService;
import com.milk.secretagent.Service.RecordService;
import com.milk.secretagent.Utility.AppConstants;

public class AlarmReceiver extends BroadcastReceiver {
    Bundle m_Bundle;
    Context m_Context;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        m_Context = context;
        m_Bundle = intent.getExtras();

        if (intent.getAction().equals(AppConstants.ACTION.RUN_RECORD_TASK)) {
            Log.e(AppConstants.TAG, "[AlarmReceiver] Record task");
            runRecordTask();
        }
        else if (intent.getAction().equals(AppConstants.ACTION.RUN_LOCATION_TASK)) {
            Log.e(AppConstants.TAG, "[AlarmReceiver] Location task");
            runLocationTask();
        }

        //Toast.makeText(context, "Received!", Toast.LENGTH_SHORT).show();
    }

    private void runRecordTask() {
        Intent intentRecord = new Intent(m_Context, RecordService.class);
        intentRecord.putExtras(m_Bundle);
        m_Context.startService(intentRecord);
    }

    private void runLocationTask() {
        Intent intentLocation = new Intent(m_Context, LocationService.class);
        intentLocation.putExtras(m_Bundle);
        m_Context.startService(intentLocation);
    }
}
