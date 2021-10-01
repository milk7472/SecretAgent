package com.milk.secretagent.Enum;

/**
 * Created by Milk on 2015/7/23.
 */
public enum ETaskStatus {
    TASK_WAITING("Task Waiting"),
    TASK_RUNNING("Task Running"),
    TASK_FINISHED("Task Finished"),
    TASK_DELETED("Task Deleted");

    private String _taskStatus;
    private ETaskStatus(String taskStatus) {
        this._taskStatus = taskStatus;
    }

    @Override
    public String toString() {
        return this._taskStatus;
    }
}
