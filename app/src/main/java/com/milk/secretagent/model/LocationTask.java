package com.milk.secretagent.Model;

import com.milk.secretagent.Enum.ETaskTypes;

/**
 * Created by Milk on 2015/7/31.
 */
public class LocationTask extends BaseTask {
    String m_startAddress = "";
    String m_endAddress = "";

    public LocationTask(int alarmId, long timestamp, int recordLength, boolean isShowNotification) {
        super(alarmId, ETaskTypes.LOCATION_TASK, timestamp, recordLength, isShowNotification);
    }

    public String getStartAddress() {
        return m_startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.m_startAddress = startAddress;
    }

    public String getEndAddress() {
        return m_endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.m_endAddress = endAddress;
    }
}
