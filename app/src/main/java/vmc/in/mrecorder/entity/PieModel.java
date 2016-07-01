package vmc.in.mrecorder.entity;

import org.json.JSONArray;

/**
 * Created by gousebabjan on 27/5/16.
 */
public class PieModel {

    private String calltype;
    private String count;

    public static String getCode() {
        return code;
    }

    public static void setCode(String code) {
        PieModel.code = code;
    }

    public static String code;

    private JSONArray records;

    public JSONArray getRecords() {
        return records;
    }

    public void setRecords(JSONArray records) {
        this.records = records;
    }






    public String getCalltype() {
        return calltype;
    }

    public void setCalltype(String calltype) {
        this.calltype = calltype;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
