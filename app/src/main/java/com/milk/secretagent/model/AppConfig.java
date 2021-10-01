package com.milk.secretagent.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaRecorder;

import com.google.android.gms.location.LocationRequest;
import com.milk.secretagent.Utility.AppConstants;

/**
 * Created by Milk on 2015/8/11.
 */
public class AppConfig {
    public static int MAX_RECORD_HOUR = 2;
    public static int METRIC_UNIT = 1;
    public static int IMPERIAL_UNIT = 2;
    public static int SAMPLING_RATE_44100 = 44100;
    public static int SAMPLING_RATE_22050 = 22050;
    public static int SAMPLING_RATE_11025 = 11025;
    public static int BIT_RATE_32000 = 32000;
    public static int BIT_RATE_24000 = 24000;
    public static int BIT_RATE_16000 = 16000;

    Context m_Context;

    int m_MaxRecordLength = MAX_RECORD_HOUR; // hour
    int m_AudioEncoder = MediaRecorder.AudioEncoder.AMR_WB;
    int m_AudioSamplingRate = 22050; // 22.05 kHz
    int m_AudioBitRate = 24000; // 24 kbps
    int m_LocationPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
    int m_LocationAccuracy = 50; // meters
    int m_LocationTimeSlot = 30; // seconds
    int m_LocationUnit = METRIC_UNIT;

    private static AppConfig ourInstance = new AppConfig();

    public static AppConfig getInstance() {
        return ourInstance;
    }

    private AppConfig() {
    }

    public void initialize(Context context) {
        this.m_Context = context;

        // Get all config from shared preferences
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SharedPreferences.KEY_SETTINGS, Context.MODE_PRIVATE);

        // Max record length
        int maxRecordLength = sharedPreferences.getInt(AppConstants.Config.KEY_SETTINGS_COMMON_MAX_RECORD_LENGTH, -1);
        if (-1 != maxRecordLength) {
            this.m_MaxRecordLength = maxRecordLength;
        }

        // Audio encoder
        int audioEncoder = sharedPreferences.getInt(AppConstants.Config.KEY_SETTINGS_RECORD_ENCODER, -1);
        if (-1 != audioEncoder) {
            this.m_AudioEncoder = audioEncoder;
        }

        // Audio sampling rate
        int audioSamplingRate = sharedPreferences.getInt(AppConstants.Config.KEY_SETTINGS_RECORD_SAMPLING_RATE, -1);
        if (-1 != audioSamplingRate) {
            this.m_AudioSamplingRate = audioSamplingRate;
        }

        // Audio Bit rate
        int audioBitRate = sharedPreferences.getInt(AppConstants.Config.KEY_SETTINGS_RECORD_BIT_RATE, -1);
        if (-1 != audioBitRate) {
            this.m_AudioBitRate = audioBitRate;
        }

        // Location priority
        int locationPriority = sharedPreferences.getInt(AppConstants.Config.KEY_SETTINGS_LOCATION_PRIORITY, -1);
        if (-1 != locationPriority) {
            this.m_LocationPriority = locationPriority;
        }

        // Location accuracy
        int locationAccuracy = sharedPreferences.getInt(AppConstants.Config.KEY_SETTINGS_LOCATION_ACCURACY, -1);
        if (-1 != locationAccuracy) {
            this.m_LocationAccuracy = locationAccuracy;
        }

        // Location time slot
        int locationTimeSlot = sharedPreferences.getInt(AppConstants.Config.KEY_SETTINGS_LOCATION_TIME_SLOT, -1);
        if (-1 != locationTimeSlot) {
            this.m_LocationTimeSlot  = locationTimeSlot;
        }

        // Location unit
        int locationUnit = sharedPreferences.getInt(AppConstants.Config.KEY_SETTINGS_LOCATION_UNIT, -1);
        if (-1 != locationUnit) {
            this.m_LocationUnit = locationUnit;
        }
    }

    public void saveConfigItem(String key, int value) {
        SharedPreferences sharedPreferences = m_Context.getSharedPreferences(AppConstants.SharedPreferences.KEY_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(key, value);
        editor.commit();

        switch (key) {
            case AppConstants.Config.KEY_SETTINGS_COMMON_MAX_RECORD_LENGTH:
                setMaxRecordLength(value);
                break;
            case AppConstants.Config.KEY_SETTINGS_RECORD_ENCODER:
                setAudioEncoder(value);
                break;
            case AppConstants.Config.KEY_SETTINGS_RECORD_SAMPLING_RATE:
                setAudioSamplingRate(value);
                break;
            case AppConstants.Config.KEY_SETTINGS_RECORD_BIT_RATE:
                setAudioBitRate(value);
                break;
            case AppConstants.Config.KEY_SETTINGS_LOCATION_PRIORITY:
                setLocationPriority(value);
                break;
            case AppConstants.Config.KEY_SETTINGS_LOCATION_ACCURACY:
                setLocationAccuracy(value);
                break;
            case AppConstants.Config.KEY_SETTINGS_LOCATION_TIME_SLOT:
                setLocationTimeSlot(value);
                break;
            case AppConstants.Config.KEY_SETTINGS_LOCATION_UNIT:
                setLocationUnit(value);
                break;
        }
    }

    public int getMaxRecordLength() {
        return this.m_MaxRecordLength;
    }

    public void setMaxRecordLength(int maxRecordLength) {
        this.m_MaxRecordLength = maxRecordLength;
    }

    public int getAudioEncoder() {
        return m_AudioEncoder;
    }

    public void setAudioEncoder(int audioEncoder) {
        this.m_AudioEncoder = audioEncoder;
    }

    public int getAudioSamplingRate() {
        return m_AudioSamplingRate;
    }

    public void setAudioSamplingRate(int audioSamplingRate) {
        this.m_AudioSamplingRate = audioSamplingRate;
    }

    public int getAudioBitRate() {
        return m_AudioBitRate;
    }

    public void setAudioBitRate(int audioBitRate) {
        this.m_AudioBitRate = audioBitRate;
    }

    public int getLocationPriority() {
        return m_LocationPriority;
    }

    public void setLocationPriority(int locationPriority) {
        this.m_LocationPriority = locationPriority;
    }

    public int getLocationAccuracy() {
        return m_LocationAccuracy;
    }

    public void setLocationAccuracy(int locationAccuracy) {
        this.m_LocationAccuracy = locationAccuracy;
    }

    public int getLocationTimeSlot() {
        return m_LocationTimeSlot;
    }

    public void setLocationTimeSlot(int locationTimeSlot) {
        this.m_LocationTimeSlot = locationTimeSlot;
    }

    public int getLocationUnit() {
        return m_LocationUnit;
    }

    public void setLocationUnit(int locationUnit) {
        this.m_LocationUnit = locationUnit;
    }
}
