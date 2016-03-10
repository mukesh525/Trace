package vmc.in.mrecorder.entity;

import java.io.File;

/**
 * Created by gousebabjan on 4/3/16.
 */
public class Model {

    private String phoneNumber;
    private String time;
    private  String filePath;
    private  String callType;
    private File file;

    public String getId() {
        return id;

    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

}
