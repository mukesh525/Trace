package vmc.in.mrecorder.entity;

import java.util.Date;

/**
 * Created by mukesh on 3/24/2016.
 */
public class CallData {
    private String callId;
    private String callFrom;
    private String callerName;
    private String groupName;
    private Date callTime;
    private String status;
    private String dataId;
    private String callTimeString;

    public String getCallTimeString() {
        return callTimeString;
    }

    public void setCallTimeString(String callTimeString) {
        this.callTimeString = callTimeString;
    }

    public CallData() {
    }

    public CallData(String callId, String groupName) {
        this.callId = callId;
        this.groupName = groupName;
    }


    public String getCallFrom() {
        return callFrom;
    }

    public void setCallFrom(String callFrom) {
        this.callFrom = callFrom;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Date getCallTime() {
        return callTime;
    }

    public void setCallTime(Date callTime) {
        this.callTime = callTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }
}
