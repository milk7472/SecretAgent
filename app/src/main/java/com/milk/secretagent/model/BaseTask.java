package com.milk.secretagent.Model;

import com.milk.secretagent.Enum.ETaskStatus;
import com.milk.secretagent.Enum.ETaskTypes;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Milk on 2015/7/14.
 */
public abstract class BaseTask implements Serializable {
    private int _alarmId;
    private ETaskTypes _taskType;
    private ETaskStatus _taskStatus;
    private long m_createTimestamp;
    private long m_startTimestamp;
    private long m_finishTimestamp;
    private int _recordLength;
    private boolean _isShowNotification;
    String m_fileName;
    String m_filePath;

    public BaseTask(int alarmId, ETaskTypes taskType, long timestamp, int recordLength, boolean isShowNotification) {
        this._alarmId = alarmId;
        this._taskType = taskType;
        this.m_createTimestamp = timestamp;
        this._recordLength = recordLength;
        this._isShowNotification = isShowNotification;
        this._taskStatus = ETaskStatus.TASK_WAITING;
    }

    public void setAlarmId(int alarmId) {
        this._alarmId = alarmId;
    }

    public int getAlarmId() {
        return this._alarmId;
    }

    public void setTaskType(ETaskTypes taskType) {
        this._taskType = taskType;
    }

    public ETaskTypes getTaskType() {
        return this._taskType;
    }

    public void setTaskStatus(ETaskStatus taskStatus) {
        this._taskStatus = taskStatus;
    }

    public ETaskStatus getTaskStatus() {
        return this._taskStatus;
    }

    public void setTimestamp(long timestamp) {
        this.m_createTimestamp = timestamp;
    }

    public long getTimestamp() {
        return this.m_createTimestamp;
    }

    public void setIsShowNotification(boolean isShowNotification) {
        this._isShowNotification = isShowNotification;
    }

    public boolean isShowNotification() {
        return this._isShowNotification;
    }

    public void setRecordLength(int recordLength) {
        this._recordLength = recordLength;
    }

    public int getRecordLength() {
        return this._recordLength;
    }

    public void setFileName(String fileName) {
        this.m_fileName = fileName;
    }

    public String getFileName() {
        return this.m_fileName;
    }

    public void setFilePath(String filePath) {
        this.m_filePath = filePath;
    }

    public String getFilePath() {
        return this.m_filePath;
    }

    /* Create Time */
    public String getCreateTime() {
        String format = "yyyy/MM/dd ah:mm";

        return getFormattedTime(format, this.m_createTimestamp);
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.m_createTimestamp = createTimestamp;
    }

    public long getCreateTimestamp() {
        return this.m_createTimestamp;
    }

    /* Start Time */
    public String getStartTime() {
        String format = "yyyy/MM/dd ah:mm";

        return getFormattedTime(format, this.m_startTimestamp);
    }

    public void setStartTimestamp(long startTimestamp) {
        this.m_startTimestamp = startTimestamp;
    }

    public long getStartTimestamp() {
        return this.m_startTimestamp;
    }

    /* Finish Time */
    public void setFinishTimestamp(long finishTimestamp) {
        this.m_finishTimestamp = finishTimestamp;
    }

    public String getFinishTime() {
        String format = "yyyy/MM/dd ah:mm";

        return getFormattedTime(format, this.m_finishTimestamp);
    }

    public long getFinishTimestamp() {
        return this.m_finishTimestamp;
    }

    public String getFormattedTime(String format, long timestamp) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.TAIWAN);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);

        return simpleDateFormat.format(calendar.getTime());
    }
}
