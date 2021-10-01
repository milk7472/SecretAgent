package com.milk.secretagent;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.milk.secretagent.Model.AppConfig;
import com.milk.secretagent.Utility.AppConstants;

public class SettingsActivity extends ActionBarActivity implements View.OnClickListener {
    Toolbar m_Toolbar;

    /* Common */
    LinearLayout m_LayoutMaxRecordLength;
    TextView m_TextMaxRecordLength;

    /* Record */
    LinearLayout m_LayoutEncoder;
    TextView m_TextEncoder;

    LinearLayout m_LayoutQuality;
    TextView m_TextQuality;

    /* Location */
    LinearLayout m_LayoutPriority;
    TextView m_TextPriority;

    LinearLayout m_LayoutAccuracy;
    TextView m_TextAccuracy;

    LinearLayout m_LayoutTimeSlot;
    TextView m_TextTimeSlot;

    LinearLayout m_LayoutUnit;
    TextView m_TextUnit;

    /* Others */
    TextView m_TextVersion;
    TextView m_TextLegal;
    TextView m_TextAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Creating The Toolbar and setting it as the Toolbar for the activity
        m_Toolbar = (Toolbar) findViewById(R.id.tool_bar_settings);
        m_Toolbar.setTitle(R.string.title_activity_settings);
        setSupportActionBar(m_Toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        initComponents();
        showCurrentSettings();
        setupControllers();
    }

    private void initComponents() {

        m_LayoutMaxRecordLength = getView(R.id.settings_layout_record_length);
        m_TextMaxRecordLength = getView(R.id.settings_record_length);

        m_LayoutEncoder = getView(R.id.settings_layout_encoder);
        m_TextEncoder = getView(R.id.settings_encoder);

        m_LayoutQuality = getView(R.id.settings_layout_quality);
        m_TextQuality = getView(R.id.settings_quality);

        m_LayoutPriority = getView(R.id.settings_layout_priority);
        m_TextPriority = getView(R.id.settings_priority);

        m_LayoutAccuracy = getView(R.id.settings_layout_accuracy);
        m_TextAccuracy = getView(R.id.settings_accuracy);

        m_LayoutTimeSlot = getView(R.id.settings_layout_time_slot);
        m_TextTimeSlot = getView(R.id.settings_time_slot);

        m_LayoutUnit = getView(R.id.settings_layout_unit);
        m_TextUnit = getView(R.id.settings_unit);

        m_TextVersion = getView(R.id.settings_version);
        m_TextLegal = getView(R.id.settings_legal);
        m_TextAbout = getView(R.id.settings_about);
    }

    private void showCurrentSettings() {
        // Max record length
        String maxRecordLength = String.format(getResources().getString(R.string.settings_max_record_length), AppConfig.getInstance().getMaxRecordLength());
        m_TextMaxRecordLength.setText(maxRecordLength);

        // Audio encoder
        int audioEncoder = AppConfig.getInstance().getAudioEncoder();
        String[] encoderArray = getResources().getStringArray(R.array.settings_encoders);
        String stringEncoder = "";
        switch (audioEncoder) {
            case MediaRecorder.AudioEncoder.AAC:
                stringEncoder = encoderArray[0];
                break;
            case MediaRecorder.AudioEncoder.AMR_WB:
                stringEncoder = encoderArray[1];
                break;
            case MediaRecorder.AudioEncoder.AMR_NB:
                stringEncoder = encoderArray[2];
                break;
        }
        m_TextEncoder.setText(stringEncoder);

        // Audio quality
        int audioSamplingRate = AppConfig.getInstance().getAudioSamplingRate();
        int audioBitRate = AppConfig.getInstance().getAudioBitRate();
        String[] qualityArray = getResources().getStringArray(R.array.settings_quality_levels);
        String stringQuality = "";
        if (AppConfig.SAMPLING_RATE_44100 == audioSamplingRate && AppConfig.BIT_RATE_32000 == audioBitRate) {
            stringQuality = qualityArray[0];
        }
        else if (AppConfig.SAMPLING_RATE_22050 == audioSamplingRate && AppConfig.BIT_RATE_24000 == audioBitRate) {
            stringQuality = qualityArray[1];
        }
        else if (AppConfig.SAMPLING_RATE_11025 == audioSamplingRate && AppConfig.BIT_RATE_16000 == audioBitRate) {
            stringQuality = qualityArray[2];
        }
        m_TextQuality.setText(stringQuality);

        // Location priority
        int locationPriority = AppConfig.getInstance().getLocationPriority();
        String[] priorityArray = getResources().getStringArray(R.array.settings_priority_level);
        String stringPriority = "";
        if (LocationRequest.PRIORITY_HIGH_ACCURACY == locationPriority) {
            stringPriority = priorityArray[0];
        }
        else if (LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY == locationPriority) {
            stringPriority = priorityArray[1];
        }
        else if (LocationRequest.PRIORITY_LOW_POWER == locationPriority) {
            stringPriority = priorityArray[2];
        }
        m_TextPriority.setText(stringPriority);

        // Location accuracy
        int locationAccuracy = AppConfig.getInstance().getLocationAccuracy();
        String[] accuracyArray = getResources().getStringArray(R.array.settings_accuracy_meters);
        String stringAccuracy = "";
        if (50 == locationAccuracy) {
            stringAccuracy = accuracyArray[0];
        }
        else if (100 == locationAccuracy) {
            stringAccuracy = accuracyArray[1];
        }
        else if (200 == locationAccuracy) {
            stringAccuracy = accuracyArray[2];
        }
        else if (500 == locationAccuracy) {
            stringAccuracy = accuracyArray[3];
        }
        else if (1000 == locationAccuracy) {
            stringAccuracy = accuracyArray[4];
        }
        m_TextAccuracy.setText(stringAccuracy);

        // Location time slot
        int locationTimeSlot = AppConfig.getInstance().getLocationTimeSlot();
        String[] timeSlotArray = getResources().getStringArray(R.array.settings_time_slots);
        String stringTimeSlot = "";
        if (locationTimeSlot == 15) {
            stringTimeSlot = timeSlotArray[0];
        }
        else if (locationTimeSlot == 20) {
            stringTimeSlot = timeSlotArray[1];
        }
        else if (locationTimeSlot == 30) {
            stringTimeSlot = timeSlotArray[2];
        }
        else if (locationTimeSlot == 60) {
            stringTimeSlot = timeSlotArray[3];
        }
        else if (locationTimeSlot == 120) {
            stringTimeSlot = timeSlotArray[4];
        }
        else if (locationTimeSlot == 180) {
            stringTimeSlot = timeSlotArray[5];
        }
        else if (locationTimeSlot == 300) {
            stringTimeSlot = timeSlotArray[6];
        }
        else if (locationTimeSlot == 600) {
            stringTimeSlot = timeSlotArray[7];
        }
        m_TextTimeSlot.setText(stringTimeSlot);

        // Location unit
        int locationUnit = AppConfig.getInstance().getLocationUnit();
        String[] unitArray = getResources().getStringArray(R.array.settings_units);
        String stringUnit = "";
        if (AppConfig.METRIC_UNIT == locationUnit) {
            stringUnit = unitArray[0];
        }
        else if (AppConfig.IMPERIAL_UNIT == locationUnit) {
            stringUnit = unitArray[1];
        }
        m_TextUnit.setText(stringUnit);

        // App version
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            m_TextVersion.setText(info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void setupControllers() {
        m_LayoutMaxRecordLength.setOnClickListener(this);

        m_LayoutEncoder.setOnClickListener(this);
        m_LayoutQuality.setOnClickListener(this);

        m_LayoutPriority.setOnClickListener(this);
        m_LayoutAccuracy.setOnClickListener(this);
        m_LayoutTimeSlot.setOnClickListener(this);
        m_LayoutUnit.setOnClickListener(this);

        m_TextLegal.setOnClickListener(this);
        m_TextAbout.setOnClickListener(this);
    }

    public final <E extends View> E
    getView(int id)
    {
        return (E)findViewById(id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings_layout_record_length:
                setMaxRecordLength();
                break;
            case R.id.settings_layout_encoder:
                setAudioEncoder();
                break;
            case R.id.settings_layout_quality:
                setAudioQuality();
                break;
            case R.id.settings_layout_priority:
                setLocationPriority();
                break;
            case R.id.settings_layout_accuracy:
                setLocationAccuracy();
                break;
            case R.id.settings_layout_time_slot:
                setLocationTimeSlot();
                break;
            case R.id.settings_layout_unit:
                setLocationUnit();
                break;
            case R.id.settings_legal:
                Intent intentLegal = new Intent(this, LegalActivity.class);
                startActivity(intentLegal);
                break;
            case R.id.settings_about:
                Intent intentAbout = new Intent(this, AboutActivity.class);
                startActivity(intentAbout);
                break;
        }
    }

    private void setMaxRecordLength() {

        String[] maxRecordLengthList = new String[AppConfig.MAX_RECORD_HOUR];
        for (int i = 0; i < maxRecordLengthList.length; i++) {
            maxRecordLengthList[i] = String.format(getResources().getString(R.string.settings_max_record_length), i + 1);
        }

        int currentMaxRecordHour = AppConfig.getInstance().getMaxRecordLength();

        final AlertDialog.Builder dialogSetMaxRecordLength = new AlertDialog.Builder(this);
        dialogSetMaxRecordLength.setTitle(getString(R.string.settings_dialog_max_record_length_title));
        dialogSetMaxRecordLength.setSingleChoiceItems(maxRecordLengthList, currentMaxRecordHour - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AppConfig.getInstance().saveConfigItem(AppConstants.Config.KEY_SETTINGS_COMMON_MAX_RECORD_LENGTH, which + 1);
                m_TextMaxRecordLength.setText(String.format(getResources().getString(R.string.settings_max_record_length), AppConfig.getInstance().getMaxRecordLength()));
                dialog.dismiss();
            }
        });

        dialogSetMaxRecordLength.create();
        dialogSetMaxRecordLength.show();
    }

    private void setAudioEncoder() {
        final String[] encoderList = getResources().getStringArray(R.array.settings_encoders);

        int currentEncoder = 0;
        switch (AppConfig.getInstance().getAudioEncoder()) {
            case MediaRecorder.AudioEncoder.AAC:
                currentEncoder = 0;
                break;
            case MediaRecorder.AudioEncoder.AMR_WB:
                currentEncoder = 1;
                break;
            case MediaRecorder.AudioEncoder.AMR_NB:
                currentEncoder = 2;
                break;
        }

        final AlertDialog.Builder dialogSetEncoder = new AlertDialog.Builder(this);
        dialogSetEncoder.setTitle(getString(R.string.settings_dialog_encoder_title));
        dialogSetEncoder.setSingleChoiceItems(encoderList, currentEncoder, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedEncoder = MediaRecorder.AudioEncoder.AMR_NB;
                switch (which) {
                    case 0:
                        selectedEncoder = MediaRecorder.AudioEncoder.AAC;
                        break;
                    case 1:
                        selectedEncoder = MediaRecorder.AudioEncoder.AMR_WB;
                        break;
                    case 2:
                        selectedEncoder = MediaRecorder.AudioEncoder.AMR_NB;
                        break;
                }
                AppConfig.getInstance().saveConfigItem(AppConstants.Config.KEY_SETTINGS_RECORD_ENCODER, selectedEncoder);
                m_TextEncoder.setText(encoderList[which]);
                dialog.dismiss();
            }
        });

        dialogSetEncoder.create();
        dialogSetEncoder.show();
    }

    private void setAudioQuality() {
        final String[] qualityList = getResources().getStringArray(R.array.settings_quality_levels);
        int currentSamplingRate = AppConfig.getInstance().getAudioSamplingRate();
        int currentBitRate = AppConfig.getInstance().getAudioBitRate();
        int selectedIndex = 0;

        if (AppConfig.SAMPLING_RATE_44100 == currentSamplingRate && 32000 == currentBitRate) {
            selectedIndex = 0;
        }
        else if (AppConfig.SAMPLING_RATE_22050 == currentSamplingRate && 24000 == currentBitRate) {
            selectedIndex = 1;
        }
        else if (AppConfig.SAMPLING_RATE_11025 == currentSamplingRate && 16000 == currentBitRate) {
            selectedIndex = 2;
        }

        final AlertDialog.Builder dialogSetQuality = new AlertDialog.Builder(this);
        dialogSetQuality.setTitle(getString(R.string.settings_dialog_quality_title));
        dialogSetQuality.setSingleChoiceItems(qualityList, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int samplingRate = AppConfig.SAMPLING_RATE_22050;
                int bitRate = AppConfig.BIT_RATE_24000;

                if(0 == which) {
                    samplingRate = AppConfig.SAMPLING_RATE_44100;
                    bitRate = AppConfig.BIT_RATE_32000;
                }
                else if (1 == which) {
                    samplingRate = AppConfig.SAMPLING_RATE_22050;
                    bitRate = AppConfig.BIT_RATE_24000;
                }
                else if (2 == which) {
                    samplingRate = AppConfig.SAMPLING_RATE_11025;
                    bitRate = AppConfig.BIT_RATE_16000;
                }
                AppConfig.getInstance().saveConfigItem(AppConstants.Config.KEY_SETTINGS_RECORD_SAMPLING_RATE, samplingRate);
                AppConfig.getInstance().saveConfigItem(AppConstants.Config.KEY_SETTINGS_RECORD_BIT_RATE, bitRate);
                m_TextQuality.setText(qualityList[which]);
                dialog.dismiss();
            }
        });

        dialogSetQuality.create();
        dialogSetQuality.show();
    }

    private void setLocationPriority() {
        final String[] priorityList = getResources().getStringArray(R.array.settings_priority_level);

        int currentPriority = AppConfig.getInstance().getLocationPriority();
        int selectedIndex = 0;
        if (LocationRequest.PRIORITY_HIGH_ACCURACY == currentPriority) {
            selectedIndex = 0;
        }
        else if (LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY == currentPriority) {
            selectedIndex = 1;
        }
        else if (LocationRequest.PRIORITY_LOW_POWER == currentPriority) {
            selectedIndex = 2;
        }

        AlertDialog.Builder dialogSetPriority = new AlertDialog.Builder(this);
        dialogSetPriority.setTitle(getString(R.string.settings_dialog_priority));
        dialogSetPriority.setSingleChoiceItems(priorityList, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int newPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;

                switch (which) {
                    case 0:
                        newPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
                        break;
                    case 1:
                        newPriority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
                        break;
                    case 2:
                        newPriority = LocationRequest.PRIORITY_LOW_POWER;
                        break;
                }

                AppConfig.getInstance().saveConfigItem(AppConstants.Config.KEY_SETTINGS_LOCATION_PRIORITY, newPriority);
                m_TextPriority.setText(priorityList[which]);
                dialog.dismiss();
            }
        });

        dialogSetPriority.create();
        dialogSetPriority.show();
    }

    private void setLocationAccuracy() {
        final String[] accuracyList = getResources().getStringArray(R.array.settings_accuracy_meters);

        int currentAccuracy = AppConfig.getInstance().getLocationAccuracy();
        int selectedIndex = 0;
        if (50 == currentAccuracy) {
            selectedIndex = 0;
        }
        else if (100 == currentAccuracy) {
            selectedIndex = 1;
        }
        else if (200 == currentAccuracy) {
            selectedIndex = 2;
        }
        else if (500 == currentAccuracy) {
            selectedIndex = 3;
        }
        else if (1000 == currentAccuracy) {
            selectedIndex = 4;
        }

        final AlertDialog.Builder dialogSetAccuracy = new AlertDialog.Builder(this);
        dialogSetAccuracy.setTitle(getString(R.string.settings_dialog_accuracy_title));
        dialogSetAccuracy.setSingleChoiceItems(accuracyList, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                int newAccuracy = Integer.valueOf(accuracyList[which].split(" ")[0]);
                AppConfig.getInstance().saveConfigItem(AppConstants.Config.KEY_SETTINGS_LOCATION_ACCURACY, newAccuracy);
                m_TextAccuracy.setText(accuracyList[which]);
                dialog.dismiss();
            }
        });

        dialogSetAccuracy.create();
        dialogSetAccuracy.show();
    }

    private void setLocationTimeSlot() {
        final String[] timeSlotList = getResources().getStringArray(R.array.settings_time_slots);
        int currentTimeSlot = AppConfig.getInstance().getLocationTimeSlot();
        int selectedIndex = 0;

        if (currentTimeSlot == 15) {
            selectedIndex = 0;
        }
        else if (currentTimeSlot == 20) {
            selectedIndex = 1;
        }
        else if (currentTimeSlot == 30) {
            selectedIndex = 2;
        }
        else if (currentTimeSlot == 60) {
            selectedIndex = 3;
        }
        else if (currentTimeSlot == 120) {
            selectedIndex = 4;
        }
        else if (currentTimeSlot == 180) {
            selectedIndex = 5;
        }
        else if (currentTimeSlot == 300) {
            selectedIndex = 6;
        }
        else if (currentTimeSlot == 600) {
            selectedIndex = 7;
        }

        final AlertDialog.Builder dialogSetTimeSlot = new AlertDialog.Builder(this);
        dialogSetTimeSlot.setTitle(getString(R.string.settings_dialog_time_slot_title));
        dialogSetTimeSlot.setSingleChoiceItems(timeSlotList, selectedIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int timeSlot = 0;
                if (3 > which) {
                    timeSlot = Integer.valueOf(timeSlotList[which].split(" ")[0]);
                }
                else {
                    timeSlot = Integer.valueOf(timeSlotList[which].split(" ")[0]) * 60;
                }

                AppConfig.getInstance().saveConfigItem(AppConstants.Config.KEY_SETTINGS_LOCATION_TIME_SLOT, timeSlot);
                m_TextTimeSlot.setText(timeSlotList[which]);
                dialog.dismiss();
            }
        });

        dialogSetTimeSlot.create();
        dialogSetTimeSlot.show();
    }

    private void setLocationUnit() {
        final String[] unitList = getResources().getStringArray(R.array.settings_units);
        int currentUnit = AppConfig.getInstance().getLocationUnit();

        AlertDialog.Builder dialogSetUnit = new AlertDialog.Builder(this);
        dialogSetUnit.setTitle(getString(R.string.settings_dialog_unit_title));
        dialogSetUnit.setSingleChoiceItems(unitList, currentUnit - 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                AppConfig.getInstance().saveConfigItem(AppConstants.Config.KEY_SETTINGS_LOCATION_UNIT, which + 1);
                m_TextUnit.setText(unitList[which]);
                dialog.dismiss();
            }
        });

        dialogSetUnit.create();
        dialogSetUnit.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
