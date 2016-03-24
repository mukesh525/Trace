package vmc.in.mrecorder.callbacks;

/**
 * Created by gousebabjan on 4/3/16.
 */
public interface TAG {
    public String PROJECT_NUMBER = "596968407103";
    public String MISSED = "missed";
    public String DEFAULT = "n/a";
    public String INCOMING = "incoming";
    public String OUTGOING = "outgoing";
    public String CALLTYPE = "CallType";
    public String NUMBER = "Number";
    public String FILEPATH = "FilePath";
    public String TIME = "Time";
    public String ID = "_id";
    public String TYPE = "type";
    public String UPLOADEDFILE ="uploadedfile";
    public String ENDTIME ="endtime";

    public String EMAIL = "email";
    public String PASSWORD = "password";
    public String CODE = "code";
    public String MESSAGE = "msg";
    public String OTP = "otp";
    public String DEVICE_ID = "deviceid";
    public String GCM_KEY = "gcmkey";
    public String AUTHKEY = "authkey";
    public String CALLTO = "callto";
    public String STARTTIME = "starttime";
    public String CALLTYPEE = "calltype";
    public String DURATION = "duration";

    public int NOTIFICATION_ID = 0;



    //public  String UPLOAD_URL="http://192.168.1.118/mconnect/upload.php";
    public String UPLOAD_URL = "http://192.168.1.118/newdesigen/mtrackapp/insert_calldetail";
    public String GET_OTP = "http://192.168.1.118/newdesigen/mtrackapp/login_mtrack";
    public String LOGIN_URL = "http://192.168.1.118/newdesigen/mtrackapp/check_auth";
    public String FORGOT_OTP_URL = "";
    public String CHANGED_PASS = "";

}
