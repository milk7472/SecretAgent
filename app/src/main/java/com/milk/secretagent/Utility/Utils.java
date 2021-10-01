package com.milk.secretagent.Utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;

import com.milk.secretagent.Enum.ETaskTypes;
import com.milk.secretagent.Model.BaseTask;
import com.milk.secretagent.R;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Milk on 2015/7/22.
 */
public class Utils {

    public static ProgressDialog m_ProgressDialog;

    public Utils() {}

    public static String formatSeconds(int totalSeconds) {
        String result;

        int hours = totalSeconds / 3600;

        int remainder = totalSeconds - hours * 3600;

        int minutes = remainder / 60;

        remainder = remainder - minutes * 60;

        int seconds = remainder;

        if (hours > 0) {
            if (minutes > 0 && seconds > 0) {
                result = String.format("%d小時%d分鐘%d秒", hours, minutes, seconds);
            }
            else if (minutes > 0 && seconds == 0) {
                result = String.format("%d小時%d分鐘", hours, minutes);
            }
            else if (minutes == 0 && seconds > 0) {
                result = String.format("%d小時%d秒", hours, seconds);
            }
            else {
                result = String.format("%d小時", hours);
            }
        }
        else if (minutes > 0){
            if (seconds > 0) {
                result = String.format("%d分鐘%d秒", minutes, seconds);
            }
            else {
                result = String.format("%d分鐘", minutes);
            }
        }
        else {
            result = String.format("%d秒", seconds);
        }

        return result;
    }

    public static String formatMinutes(Context context, int totalMinutes) {

        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        String result;

        if (hours > 0) {
            if (minutes > 0) {
                result = String.format(context.getString(R.string.record_length_hour_minute_result), hours, minutes);
            }
            else {
                result = String.format(context.getString(R.string.record_length_hour_result), hours);
            }
        }
        else {
            result = String.format(context.getString(R.string.record_length_minute_result), minutes);
        }

        return result;
    }

    public static String getDefaultFileName() {
        Calendar calendar = Calendar.getInstance();

        String format = "yyyy-MM-dd'T'HHmm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.TAIWAN);

        return simpleDateFormat.format(calendar.getTime());
    }

    public static void saveTasks(Context context) {
        ArrayList<BaseTask> secretTasks = TaskHelper.getInstance().getTaskList();

        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SharedPreferences.KEY_SAVE_TASKS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        try {
            editor.putString(AppConstants.SharedPreferences.KEY_SAVE_TASKS_EDITOR, ObjectSerializer.serialize(secretTasks));
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor.commit();
    }

    public static ArrayList<BaseTask> loadTasks(Context context) {
        ArrayList<BaseTask> secretTasks = new ArrayList<BaseTask>();

        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstants.SharedPreferences.KEY_SAVE_TASKS, Context.MODE_PRIVATE);
        try {
            secretTasks = (ArrayList<BaseTask>) ObjectSerializer.deserialize(
                    sharedPreferences.getString(AppConstants.SharedPreferences.KEY_SAVE_TASKS_EDITOR,
                    ObjectSerializer.serialize(new ArrayList<BaseTask>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return secretTasks;
    }

    public static void getFolderFiles(String directoryName, ArrayList<File> fileList) {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();

        for (File file : fList) {
            if (file.isFile()) {
                fileList.add(file);
            }
            else if (file.isDirectory()) {
                getFolderFiles(file.getAbsolutePath(), fileList);
            }
        }
    }

    public static String moveFileToFormalFolder(String fileName, boolean isUseAAC) {
        String suffix;
        if (isUseAAC) {
            suffix = AppConstants.File.RECORDING_SUFFIX_AMR;
        }
        else {
            suffix = AppConstants.File.RECORDING_SUFFIX_AAC;
        }

        String source = VoiceRecorderHelper.getRecordingFolder(false) + fileName + suffix;
        String destination = VoiceRecorderHelper.getRecordingFolder(true) + fileName + suffix;
        File from = new File(source);
        File to = new File(destination);

        if (from.renameTo(to)) {
            // Delete the file in temp folder
            from.delete();
        }

        return to.getAbsolutePath();
    }

    public static String getCompleteAddress(Context context, double LATITUDE, double LONGITUDE) {
        String strAddress = "";
        
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                strAddress = returnedAddress.getAddressLine(0);

                Log.e(AppConstants.TAG, String.format("Address (%f, %f): %s", LATITUDE, LONGITUDE, strAddress));
            } else {
                Log.e(AppConstants.TAG, "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(AppConstants.TAG, "Cannot get Address!");
        }
        return strAddress;
    }

    public static String getFolderPath(ETaskTypes taskType) {
        String folderName = "";
        switch (taskType) {
            case RECORD_TASK:
                folderName = AppConstants.File.RECORDING_FOLDER;
                break;
            case LOCATION_TASK:
                folderName = AppConstants.File.LOCATION_FOLDER;
                break;
        }

        File SDCardPath = Environment.getExternalStorageDirectory();
        File folderPath = new File(SDCardPath.getAbsolutePath() + folderName);
        if (!folderPath.exists()) {
            folderPath.mkdirs();
        }

        return SDCardPath.getAbsolutePath() + folderName;
    }

    public static long getAvailableStorage() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return availableBlocks * blockSize;
    }

    public static String getFormattedAvailableStorage(Context context) {
        return Formatter.formatFileSize(context, getAvailableStorage());
    }

    public static void showProgressDialog(Context context, String title, String message) {
        m_ProgressDialog =  ProgressDialog.show(context, title, message, true);
    }

    public static void hideProgressDialog() {
        if (null != m_ProgressDialog) {
            m_ProgressDialog.dismiss();
        }
    }
}
