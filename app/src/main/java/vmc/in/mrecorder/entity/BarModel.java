package vmc.in.mrecorder.entity;

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
    private String missed;
}
