package com.milk.secretagent.Utility;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.milk.secretagent.Model.AppConfig;

import java.io.File;
import java.io.IOException;

/**
 * Created by Milk on 2015/7/27.
 */
public class VoiceRecorderHelper {
    private static VoiceRecorderHelper ourInstance = new VoiceRecorderHelper();
    private MediaRecorder m_MediaRecorder;

    public static VoiceRecorderHelper getInstance() {
        return ourInstance;
    }

    private VoiceRecorderHelper() {
    }

    public String initialize(String fileName) {
        Log.e(AppConstants.TAG, "[VoiceRecorderHelper][initialize]");

        // Make sure the MediaRecorder is null
        stopRecording();

        // Get file path
        String recordingFolder = getRecordingFolder(true);
        File fileRecording = null;// = new File(recordingFolder + fileName + AppConstants.File.RECORDING_SUFFIX);

        int encoder = AppConfig.getInstance().getAudioEncoder();
        int samplingRate = AppConfig.getInstance().getAudioSamplingRate();
        int bitRate = AppConfig.getInstance().getAudioBitRate();

        // Init media recorder
        m_MediaRecorder = new MediaRecorder();
        m_MediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        m_MediaRecorder.setAudioSamplingRate(samplingRate);
        m_MediaRecorder.setAudioEncodingBitRate(bitRate);
        m_MediaRecorder.setMaxFileSize(1000000000);

        Log.e(AppConstants.TAG, "[VoiceRecorderHelper][initialize] Sampling rate: " + String.valueOf(samplingRate));
        Log.e(AppConstants.TAG, "[VoiceRecorderHelper][initialize] Bit rate: " + String.valueOf(bitRate));

        if (MediaRecorder.AudioEncoder.AMR_NB == encoder) {
            fileRecording = new File(recordingFolder + fileName + AppConstants.File.RECORDING_SUFFIX_AMR);
            m_MediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            m_MediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            Log.e(AppConstants.TAG, "[VoiceRecorderHelper][initialize] Encoder: AMR_NB");
        } else if (MediaRecorder.AudioEncoder.AMR_WB == encoder) {
            fileRecording = new File(recordingFolder + fileName + AppConstants.File.RECORDING_SUFFIX_AMR);
            m_MediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            m_MediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            Log.e(AppConstants.TAG, "[VoiceRecorderHelper][initialize] Encoder: AMR_WB");
        } else if (MediaRecorder.AudioEncoder.AAC == encoder) {
            fileRecording = new File(recordingFolder + fileName + AppConstants.File.RECORDING_SUFFIX_AAC);
            m_MediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
            m_MediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            Log.e(AppConstants.TAG, "[VoiceRecorderHelper][initialize] Encoder: AAC_LC");
        }

        m_MediaRecorder.setOutputFile(fileRecording.getAbsolutePath());
        Log.e(AppConstants.TAG, "[VoiceRecorderHelper][initialize] File path: " + fileRecording.getAbsolutePath());

        return fileRecording.getAbsolutePath();
    }

    public static String getRecordingFolder(boolean isFormalFile) {
        String folderName = isFormalFile ? AppConstants.File.RECORDING_FOLDER : AppConstants.File.TEMP_FOLDER;

        // Get file path
        File SDCardPath = Environment.getExternalStorageDirectory();
        File RecordingFolderPath = new File(SDCardPath.getAbsolutePath() + folderName);
        if (!RecordingFolderPath.exists()) {
            RecordingFolderPath.mkdirs();
        }

        return SDCardPath.getAbsolutePath() + folderName;
    }

    public void startRecording() {
        Log.e(AppConstants.TAG, "[VoiceRecorderHelper][startRecording]");

        try {

            m_MediaRecorder.prepare();
            m_MediaRecorder.start();

        } catch (IOException e) {
            Log.e(AppConstants.TAG, "[VoiceRecorderHelper][Error] " + e.getMessage());
        }
    }

    public void stopRecording() {
        Log.e(AppConstants.TAG, "[VoiceRecorderHelper][stopRecording]");

        if (m_MediaRecorder != null) {
            m_MediaRecorder.stop();
            m_MediaRecorder.release();
            m_MediaRecorder = null;
        }
    }
}
