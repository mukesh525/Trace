package vmc.in.mrecorder.entity;

import org.json.JSONArray;

/**
 * Created by gousebabjan on 27/5/16.
 */
public class BarModel {
    public String getEmpname() {
        return empname;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public String getInbound() {
        return inbound;
    }

    public void setInbound(String inbound) {
        this.inbound = inbound;
    }

    public String getOutbound() {
        return outbound;
    }

    public void setOutbound(String outbound) {
        this.outbound = outbound;
    }

    public String getMissed() {
        return missed;
    }

    public void setMissed(String missed) {
        this.missed = missed;
    }

    private String empname;
    private String inbound;
    private String outbound;

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JSONArray getRecords() {
        return records;
    }

    public void setRecords(JSONArray records) {
        this.records = records;
    }

    private String missed;
    private String count;
   public static String code;
    private JSONArray records;
}
