package com.milk.secretagent.Enum;

/**
 * Created by Milk on 2015/7/15.
 */
public enum ETaskTypes {
    RECORD_TASK("Record Task"),
    LOCATION_TASK("Location Task");

    private String _taskType;

    private ETaskTypes(String taskType) {
        this._taskType = taskType;
    }

    @Override
    public String toString()
    {
        return _taskType;
    }
}
