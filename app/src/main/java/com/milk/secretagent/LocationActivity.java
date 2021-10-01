package com.milk.secretagent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.milk.secretagent.Adapter.AdapterLocationSettings;
import com.milk.secretagent.Model.AppConfig;
import com.milk.secretagent.Model.LocationTask;
import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.TaskHelper;
import com.milk.secretagent.Utility.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class LocationActivity extends ActionBarActivity {
    Toolbar toolbar;
    String[] m_SettingTitles;
    String[] m_SettingHints;
    ListView listviewLocationSettings;
    AdapterLocationSettings adapterLocationSettings;

    LocationTask m_LocationTask;
    Calendar m_Calendar = Calendar.getInstance();
    int m_RecordLength = 0;
    boolean m_IsShowNotification = true;
    boolean isCancelDialog = false;

    boolean isSetDate = false;
    boolean isSetTime = false;
    boolean isSetLength = false;
    boolean isSetNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        initComponents();

        if (checkGPSStatus()) {
            setStartDate();
        }
    }

    private void initComponents() {
        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar_location_settings);
        toolbar.setTitle(getResources().getString(R.string.title_activity_location));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        m_SettingTitles = getResources().getStringArray(R.array.location_setting_titles);
        m_SettingHints = getResources().getStringArray(R.array.location_setting_hints);
        adapterLocationSettings = new AdapterLocationSettings(this, m_SettingTitles, m_SettingHints);

        listviewLocationSettings = (ListView) findViewById(R.id.listViewLocationSettings);
        listviewLocationSettings.setEmptyView(findViewById(R.id.listViewEmpty));
        listviewLocationSettings.setAdapter(adapterLocationSettings);
        listviewLocationSettings.setOnItemClickListener(onItemClickListener);
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //Toast.makeText(getApplicationContext(), String.valueOf(position), Toast.LENGTH_SHORT).show();
            switch (position) {
                case 0:
                    setStartDate();
                    break;
                case 1:
                    setStartTime();
                    break;
                case 2:
                    setRecordLength();
                    break;
                case 3:
                    setNotification();
                    break;
            }
        }
    };

    private void setStartDate() {
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        onDateSetListener,
                        m_Calendar.get(Calendar.YEAR),
                        m_Calendar.get(Calendar.MONTH),
                        m_Calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel), onCancelClickListener);
        datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, getResources().getString(R.string.dialog_next), onDateNextClickListener);
        datePickerDialog.show();
    }

    DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (isCancelDialog) {
                isCancelDialog = false;
                return;
            }

            // TODO: check selected date, cannot set the date before today

            m_Calendar.set(Calendar.YEAR, year);
            m_Calendar.set(Calendar.MONTH, monthOfYear);
            m_Calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String dateFormat = getResources().getString(R.string.date_format);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.TAIWAN);

            m_SettingHints[0] = simpleDateFormat.format(m_Calendar.getTime());
            adapterLocationSettings.notifyDataSetChanged();
            isSetDate = true;
        }
    };

    DialogInterface.OnClickListener onCancelClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                isCancelDialog = true;
                dialog.cancel();
            }
        }
    };

    DialogInterface.OnClickListener onDateNextClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            setStartTime();
        }
    };

    private void setStartTime() {
        TimePickerDialog timePickerDialog =
                new TimePickerDialog(
                        this,
                        onTimeSetListener,
                        m_Calendar.get(Calendar.HOUR_OF_DAY),
                        m_Calendar.get(Calendar.MINUTE),
                        false);

        timePickerDialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel), onCancelClickListener);
        timePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, getString(R.string.dialog_next), onTimeNextClickListener);

        timePickerDialog.show();
    }

    TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (isCancelDialog) {
                isCancelDialog = false;
                return;
            }

            // TODO: check selected time, cannot set the time before now

            m_Calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            m_Calendar.set(Calendar.MINUTE, minute);

            String timeFormat = getResources().getString(R.string.time_format);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat, Locale.TAIWAN);

            m_SettingHints[1] = simpleDateFormat.format(m_Calendar.getTime());
            adapterLocationSettings.notifyDataSetChanged();
            isSetTime = true;
        }
    };

    DialogInterface.OnClickListener onTimeNextClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            setRecordLength();
        }
    };

    private void setRecordLength() {
        int maxRecordMinutes = AppConfig.getInstance().getMaxRecordLength() * 60 - 1; // minutes

        final AlertDialog.Builder dialogSetLength = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.dialog_set_length, null);

        final TextView textViewDescription = (TextView) view.findViewById(R.id.length_description);
        final TextView textViewResult = (TextView) view.findViewById(R.id.length_result);
        final ImageView imageViewPlus = (ImageView) view.findViewById(R.id.length_plus);
        final ImageView imageViewMinus = (ImageView) view.findViewById(R.id.length_minus);
        final SeekBar seekBar = (SeekBar) view.findViewById(R.id.length_seekbar);

        textViewDescription.setText(String.format(getResources().getString(R.string.location_record_length_description), AppConfig.getInstance().getMaxRecordLength()));

        imageViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(seekBar.getProgress() - 1);
            }
        });

        imageViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(seekBar.getProgress() + 1);
            }
        });

        seekBar.setMax(maxRecordMinutes);
        seekBar.setProgress(m_RecordLength);
        textViewResult.setText(Utils.formatMinutes(getApplicationContext(), m_RecordLength + AppConstants.Config.MIN_RECORD_MINUTES));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                m_RecordLength = progress;
                textViewResult.setText(Utils.formatMinutes(getApplicationContext(), m_RecordLength + AppConstants.Config.MIN_RECORD_MINUTES));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        dialogSetLength.setView(view);
        dialogSetLength.setTitle(getResources().getString(R.string.location_record_length_title));
        dialogSetLength.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Cancel
            }
        });

        dialogSetLength.setNeutralButton(getString(R.string.dialog_next), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Next
                m_SettingHints[2] = Utils.formatMinutes(getApplicationContext(), m_RecordLength + AppConstants.Config.MIN_RECORD_MINUTES);//convertSeekbarValue(m_RecordLength);
                adapterLocationSettings.notifyDataSetChanged();
                isSetLength = true;

                setNotification();
            }
        });

        dialogSetLength.setPositiveButton(getString(R.string.dialog_done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Done
                m_SettingHints[2] = Utils.formatMinutes(getApplicationContext(), m_RecordLength + AppConstants.Config.MIN_RECORD_MINUTES);
                adapterLocationSettings.notifyDataSetChanged();
                isSetLength = true;
            }
        });

        dialogSetLength.create();
        dialogSetLength.show();

    }

    private void setNotification() {
        final String[] notificationTexts = getResources().getStringArray(R.array.location_record_is_show_notification);
        final AlertDialog.Builder dialogIsShowNotfication = new AlertDialog.Builder(this);
        dialogIsShowNotfication.setTitle(getString(R.string.location_is_show_notification_title));
        dialogIsShowNotfication.setSingleChoiceItems(notificationTexts, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        m_IsShowNotification = true;
                        break;
                    case 1:
                        m_IsShowNotification = false;
                }
                m_SettingHints[3] = notificationTexts[which];
                adapterLocationSettings.notifyDataSetChanged();
                isSetNotification = true;

                dialog.dismiss();
            }
        });

        dialogIsShowNotfication.create();
        dialogIsShowNotfication.show();
    }

    public void cancelLocationTask(View view) {
        finish();
    }

    public void submitLocationTask(View view) {
        // Check all settings are finished or not
        if (!isSetDate ||
            !isSetTime ||
            !isSetLength ||
            !isSetNotification) {
            Toast.makeText(this, getString(R.string.location_not_finish), Toast.LENGTH_SHORT).show();
            return ;
        }

        m_LocationTask = new LocationTask(TaskHelper.getInstance().getAvailableAlarmId(),  m_Calendar.getTimeInMillis(), m_RecordLength + 1, m_IsShowNotification);

        if (TaskHelper.getInstance().isSetPastTime(m_LocationTask)) {
            Toast.makeText(this, getString(R.string.task_past_time), Toast.LENGTH_SHORT).show();
            return ;
        }

        if (!TaskHelper.getInstance().isTaskAvailable(m_LocationTask)) {
            Toast.makeText(this, getString(R.string.location_time_not_available), Toast.LENGTH_SHORT).show();
            return ;
        }

        // Notify to turn on GPS


        TaskHelper.getInstance().runTask(this, m_LocationTask);

        // Trigger update list event
        this.setResult(AppConstants.CODE.RESULT_LOCATION_TASK);

        finish();
    }

    private boolean checkGPSStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        else {
            AlertDialog.Builder dialogNotifyNoGPS = new AlertDialog.Builder(this);
            dialogNotifyNoGPS.setTitle(getString(R.string.location_dialog_ask_turn_on_gps_title));
            dialogNotifyNoGPS.setMessage(getString(R.string.location_dialog_ask_turn_on_gps_message));
            dialogNotifyNoGPS.setPositiveButton(getString(R.string.location_dialog_ask_turn_on_gps_positive),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });

            dialogNotifyNoGPS.setNegativeButton(getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            dialogNotifyNoGPS.create();
            dialogNotifyNoGPS.show();
        }

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
