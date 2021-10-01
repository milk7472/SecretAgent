package com.milk.secretagent.Utility;

/**
 * Created by Milk on 2015/7/21.
 */
public class AppConstants {

    public static final String TAG = "MILK";

    public static class File {
        public static final String TEMP_FOLDER = "/SecretAgent/temp/";

        public static final String RECORDING_SUFFIX_AMR = ".amr";
        public static final String RECORDING_SUFFIX_AAC = ".aac";
        public static final String RECORDING_FOLDER = "/SecretAgent/Recordings/";


        public static final String LOCATION_SUFFIX = ".txt";
        public static final String LOCATION_FOLDER = "/SecretAgent/Locations/";
    }


    public static class Config {
        // Default max record length is 8 hours = 8 * 60 minutes; min is 1 minute
        // 0~478
        public static final int MIN_RECORD_MINUTES = 1;

        public static final String KEY_SETTINGS_COMMON_MAX_RECORD_LENGTH = "KEY_SETTINGS_COMMON_MAX_RECORD_LENGTH";
        public static final String KEY_SETTINGS_RECORD_ENCODER = "KEY_SETTINGS_RECORD_ENCODER";
        public static final String KEY_SETTINGS_RECORD_SAMPLING_RATE = "KEY_SETTINGS_RECORD_SAMPLING_RATE";
        public static final String KEY_SETTINGS_RECORD_BIT_RATE = "KEY_SETTINGS_RECORD_BIT_RATE";
        public static final String KEY_SETTINGS_LOCATION_PRIORITY = "KEY_SETTINGS_LOCATION_PRIORITY";
        public static final String KEY_SETTINGS_LOCATION_ACCURACY = "KEY_SETTINGS_LOCATION_ACCURACY";
        public static final String KEY_SETTINGS_LOCATION_TIME_SLOT = "KEY_SETTINGS_LOCATION_TIME_SLOT";
        public static final String KEY_SETTINGS_LOCATION_UNIT = "KEY_SETTINGS_LOCATION_UNIT";
    }

    public static class Service {
        public static final int RECORD_SERVICE_NOTIFY_ID = 1313;
        public static final int LOCATION_SERVICE_NOTIFY_ID = 3333;
    }

    public static class SharedPreferences {
        public static final String KEY_SAVE_TASKS = "KEY_SAVE_TASKS";
        public static final String KEY_SAVE_TASKS_EDITOR = "KEY_SAVE_TASKS_EDITOR";

        public static final String KEY_SETTINGS = "KEY_SETTINGS";
    }

    public static class Extra {
        public static final String RECORD_TASK_ALARM_ID = "RECORD_TASK_ALARM_ID";
        public static final String RECORD_TASK_LENGTH = "RECORD_TASK_LENGTH";
        public static final String RECORD_TASK_NOTIFICATION = "RECORD_TASK_NOTIFICATION";

        public static final String PLAYER_RECORDING_INDEX = "PLAYER_RECORDING_INDEX";
        public static final String MAP_LOCATION_INDEX = "MAP_LOCATION_INDEX";
    }

    public static class ACTION {
        public static final String RUN_RECORD_TASK = "com.milk.secretagent.task.record";
        public static final String RUN_LOCATION_TASK = "com.milk.secretagent.task.location";
    }

    public static class CODE {
        public static final int RESULT_RECORD_TASK = 5213;
        public static final int RESULT_LOCATION_TASK = 1314;
    }
}
