package com.nokia.tms_watch.Model;

public class OpenModel {
    public String ticketId;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String ticketStatus;
    public String remarks;
    public long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public OpenModel(String ticketId, String ticketStatus, long id,String remarks) {
        this.ticketId = ticketId;
        this.ticketStatus = ticketStatus;
        this.id=id;
        this.remarks=remarks;
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
