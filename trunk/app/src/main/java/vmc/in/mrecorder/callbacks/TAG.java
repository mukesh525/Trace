package vmc.in.mrecorder.callbacks;

import java.util.Date;

/**
 * Created by gousebabjan on 4/3/16.
 */
public interface TAG {
    int MY_PERMISSIONS_CALL = 0;
    int SHARE_CALL = 14;
    public static final String UNKNOWN = "Unknown";
    // public static final String DateTimeFormat = "dd-MM-yyyy HH:mm:ss";
    public static final String DateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    public String PROJECT_NUMBER = "596968407103";
    public String MISSED = "Missed";
    public String DEFAULT = "n/a";
    public String INCOMING = "Inbound";
    public String OUTGOING = "Outbound";
    public String Outbound = "outbound";
    public String Inbound = "inbound";
    public String missed = "missed";
    public String SHOWN = "shown";


    public String NUMBER = "Number";
    public String FILEPATH = "FilePath";
    public String TIME = "Time";
    public String ID = "_id";

    public String UPLOADEDFILE = "uploadedfile";


    public String PASSWORD = "password";
    public String CODE = "code";
    public String MESSAGE = "msg";
    public String OTP = "otp";
    public String DEVICE_ID = "deviceid";
    public String GCM_KEY = "gcmkey";

    public String CONTACTNAME = "name";

    public String DURATION = "duration";

    public int NOTIFICATION_ID = 0;


    ///NEw parametre
    public String DATA = "data";

    public String BID = "bid";
    public String THEME = "theme";
    public String EID = "eid";

    public String CALLTO = "callto";
    public String STARTTIME = "starttime";

    public String ENDTIME = "endtime";
    public String PULSE = "pulse";
    public String CALLTYPEE = "calltype";
    public String CALLTYPE = "CallType";
    public String FILENAME = "filename";
    public String LOCATION = "location";
    public String NAME = "name";
    public String BUSINESS = "business";
    public String ADDRESS = "address";
    public String EMAIL = "email";
    public String USERTYPE = "usertype";
    public String REMARK = "remark";

    public String KEYWORD = "keyword";
    public String ASSIGNTO = "assignto";
    public String LEADID = "leadid";
    public String TKTID = "tktid";
    public String SOURCE = "source";
    public String LASTMODIFIED = "last_modified";
    public String EMPNAME = "empname";

    public String AUTHKEY = "authkey";
    public String TAG = "TEST_LOG";
    public String OFFSET = "offset";
    public String LIMIT = "limit";
    public String TYPE = "type";
    public String DEVICEID = "deviceid";
    public String FIRST_TYME = "firstime";

    public String TYPE_MISSED = "0";
    public String TYPE_INCOMING = "1";
    public String TYPE_OUTGOING = "2";
    public String TYPE_ALL = "all";
    public String FEEDBACK = "feedback";

   // public String GET_CALL_LIST = "http://mcube.vmctechnologies.com/mtappv3/getList";
    public String GET_CALL_LIST = "http://mcube.vmctechnologies.com/mtappv3/getList";
    //public String GET_FEED_BACK_URL = "http://mcube.vmctechnologies.com/mtappv3/feedback_mtrack";
    public String GET_FEED_BACK_URL = "http://mcube.vmctechnologies.com/mtappv3/feedback_mtrack";
    //public static final String STREAM_TRACKER = "http://mcube.vmctechnologies.com/sounds/";
    public static final String STREAM_TRACKER = "http://mcube.vmctechnologies.com/sounds/";
    //public String UPLOAD_URL = "http://mcube.vmctechnologies.com/mtappv3/insert_calldetail";
    public String UPLOAD_URL = "http://mcube.vmctechnologies.com/mtappv3/insert_calldetail";
    //public String GET_OTP = "http://mcube.vmctechnologies.com/mtappv3/login_mtrack";
    public String GET_OTP = "http://mcube.vmctechnologies.com/mtappv3/login_mtrack";
    //public String LOGIN_URL = "http://mcube.vmctechnologies.com/mtappv3/check_auth";
    public String LOGIN_URL = "http://mcube.vmctechnologies.com/mtappv3/check_auth";
    public String FORGOT_OTP_URL = "";
    public String CHANGED_PASS = "";

    public String EMPREPORT_URL = "http://mcube.vmctechnologies.com/mtappv3/reportByEmp";
    public String TYPEREPORT_URL = "http://mcube.vmctechnologies.com/mtappv3/reportBycallType";

    //TEMP
    public static final String CALLID = "callid";
    public static final String DATETIME = "datetime";
    public static final String CALLEREMAIL = "caller_email";
    public static final String CALLFROM = "callfrom";
    public static final String DATAID = "dataid";
    public static final String CALLERNAME = "callername";
    public static final String GROUPNAME = "groupname";
    public static final String CALLTIMESTRING = "calltime";
    public static final String STATUS = "status";
    public static final String RECORDS = "records";
    public static final String REPORTTYPE = "reporttype";

    public static final String GROUPS = "groups";
    public static final String VAL = "val";
    public static final String KEY = "key";
    public static final String RECORDING = "record";
    public String MCUBECALLS = "mcubecalls";
    public String WORKHOUR = "workhour";

    public static final String FIELDS = "fields";

    public static final String COUNT = "count";


    public static final String DROPDOWN = "dropdown";
    public static final String CHECKBOX = "checkbox";
    public static final String RADIO = "radio";
    public static final String OPTIONS = "options";




    //sam did:00000000-0d25-2296-ffff-ffffca494951
   // 00000000-0d25-2296-ffff-ffffca494951
    //Authkey:1.1.52636037dd2e5
    //gcm:fCghwArt-YE:APA91bEDZFbX-07Tdd_fhomOmJ30yDRANe_BQySqTE9uTlo3gn_Tnnl43bASf3XrgF45i8fpO4o8q1rgxkKQoEYmKa5fbGZ0ZcelE9x7Vcwi34P-dYzn-FpSJNuaINBVIQIuNMy7CTDB

}
