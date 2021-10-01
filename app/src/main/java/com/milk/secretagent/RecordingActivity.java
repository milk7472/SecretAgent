package com.milk.secretagent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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

import com.google.android.gms.gcm.Task;
import com.milk.secretagent.Adapter.AdapterRecordSettings;
import com.milk.secretagent.Model.AppConfig;
import com.milk.secretagent.Model.RecordTask;
import com.milk.secretagent.Utility.AppConstants;
import com.milk.secretagent.Utility.TaskHelper;
import com.milk.secretagent.Utility.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RecordingActivity extends ActionBarActivity {
    Toolbar toolbar;
    String[] settingTitles;
    String[] settingHints;
    ListView listViewRecordSettings;
    AdapterRecordSettings adapterRecordSettings;

    RecordTask m_RecordTask;
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
        setContentView(R.layout.activity_recording);

        initComponents();

        setStartDate();
    }

    private void initComponents() {
        String title = String.format(getResources().getString(R.string.title_activity_recording), Utils.getFormattedAvailableStorage(this));
        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar_record_settings);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        settingTitles = getResources().getStringArray(R.array.record_setting_titles);
        settingHints = getResources().getStringArray(R.array.record_setting_hints);
        adapterRecordSettings = new AdapterRecordSettings(this, settingTitles, settingHints);

        listViewRecordSettings = (ListView) findViewById(R.id.listViewRecordSettings);

        //設定ListView未取得內容時顯示的view, empty建構在listview_item_record_settings.xml中。
        listViewRecordSettings.setEmptyView(findViewById(R.id.listViewEmpty));
        listViewRecordSettings.setAdapter(adapterRecordSettings);
        listViewRecordSettings.setOnItemClickListener(onItemClickListener);
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

            settingHints[0] = simpleDateFormat.format(m_Calendar.getTime());
            adapterRecordSettings.notifyDataSetChanged();
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

            settingHints[1] = simpleDateFormat.format(m_Calendar.getTime());
            adapterRecordSettings.notifyDataSetChanged();
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

        textViewDescription.setText(String.format(getResources().getString(R.string.record_length_description), AppConfig.getInstance().getMaxRecordLength()));

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
        dialogSetLength.setTitle(getResources().getString(R.string.record_length_title));
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
                settingHints[2] = Utils.formatMinutes(getApplicationContext(), m_RecordLength + AppConstants.Config.MIN_RECORD_MINUTES);//convertSeekbarValue(m_RecordLength);
                adapterRecordSettings.notifyDataSetChanged();
                isSetLength = true;

                setNotification();
            }
        });

        dialogSetLength.setPositiveButton(getString(R.string.dialog_done), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Done
                settingHints[2] = Utils.formatMinutes(getApplicationContext(), m_RecordLength + AppConstants.Config.MIN_RECORD_MINUTES);
                adapterRecordSettings.notifyDataSetChanged();
                isSetLength = true;
            }
        });

        dialogSetLength.create();
        dialogSetLength.show();

    }

    private void setNotification() {
        final String[] notificationTexts = getResources().getStringArray(R.array.record_is_show_notification);
        final AlertDialog.Builder dialogIsShowNotfication = new AlertDialog.Builder(this);
        dialogIsShowNotfication.setTitle(getString(R.string.record_is_show_notification_title));
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
                settingHints[3] = notificationTexts[which];
                adapterRecordSettings.notifyDataSetChanged();
                isSetNotification = true;

                dialog.dismiss();
            }
        });

        dialogIsShowNotfication.create();
        dialogIsShowNotfication.show();
    }

    public void cancelRecordTask(View view) {
        finish();
    }

    public void submitRecordTask(View view) {

        // Check all settings are finished or not
        if (!isSetDate ||
            !isSetTime ||
            !isSetLength ||
            !isSetNotification) {
            Toast.makeText(this, getString(R.string.record_not_finish), Toast.LENGTH_SHORT).show();
            return ;
        }

        m_RecordTask = new RecordTask(TaskHelper.getInstance().getAvailableAlarmId(),  m_Calendar.getTimeInMillis(), m_RecordLength + 1, m_IsShowNotification);

        if (TaskHelper.getInstance().isSetPastTime(m_RecordTask)) {
            Toast.makeText(this, getString(R.string.task_past_time), Toast.LENGTH_SHORT).show();
            return ;
        }

        if (!TaskHelper.getInstance().isTaskAvailable(m_RecordTask)) {
            Toast.makeText(this, getString(R.string.record_time_not_available), Toast.LENGTH_SHORT).show();
            return ;
        }

        TaskHelper.getInstance().runTask(this, m_RecordTask);

        // Trigger update list event
        this.setResult(AppConstants.CODE.RESULT_RECORD_TASK);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recording, menu);
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
