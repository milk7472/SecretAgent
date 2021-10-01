package com.milk.secretagent.Model;

import com.milk.secretagent.Enum.ETaskTypes;
import com.milk.secretagent.Utility.Utils;

import java.math.BigDecimal;

/**
 * Created by Milk on 2015/7/15.
 */
public class RecordTask extends BaseTask {

    private long m_FileSize;
    private int m_FileLength;

    public RecordTask(int alarmId, long timestamp, int recordLength, boolean isShowNotification) {
        super(alarmId, ETaskTypes.RECORD_TASK, timestamp, recordLength, isShowNotification);
    }

    public void setFileSize(long fileSize) {
        this.m_FileSize = fileSize;
    }

    public String getFileSize() {
        String fileSize;

        double lengthKb = this.m_FileSize / 1024.0;
        if (lengthKb > 1024.0) {
            double lengthMb = lengthKb / 1024.0;
            BigDecimal bigDecimal = new BigDecimal(lengthMb);
            bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            fileSize = String.valueOf(bigDecimal.doubleValue()) + "Mb";
        }
        else {
            BigDecimal bigDecimal = new BigDecimal(lengthKb);
            bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP);
            fileSize = String.valueOf(bigDecimal.doubleValue()) + "Kb";
        }

        return fileSize;
    }

    public void setFileLength(int fileLength) {
        this.m_FileLength = fileLength;
    }

    public int getFileLength() {
        return this.m_FileLength;
    }

    public String getFormattedFileLength() {

        return Utils.formatSeconds(this.m_FileLength);
    }
}
