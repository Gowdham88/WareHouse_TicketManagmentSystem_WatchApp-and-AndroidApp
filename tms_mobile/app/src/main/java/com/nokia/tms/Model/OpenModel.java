package com.nokia.tms.Model;

public class OpenModel {
    public String ticketId;
    public String ticketStatus;

    public String getDownTime() {
        return downTime;
    }

    public void setDownTime(String downTime) {
        this.downTime = downTime;
    }

    public String downTime;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String remarks;
    public long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OpenModel(String ticketId, String ticketStatus, long id,String remarks,String downTime) {
        this.ticketId = ticketId;
        this.ticketStatus = ticketStatus;
        this.id=id;
        this.remarks=remarks;
        this.downTime=downTime;
    }

    public OpenModel() {
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
}
