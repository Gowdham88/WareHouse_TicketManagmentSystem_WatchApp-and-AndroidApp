package com.nokia.tms.Model;

public class OpenModelResponce {
    public String time;


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

    public OpenModelResponce(String time, long id, String remarks) {
        this.time = time;
        this.id=id;
        this.remarks=remarks;
    }

    public OpenModelResponce() {
    }



    public String getTime() {
        return time;
    }

    public void setTime(String ticketStatus) {
        this.time = time;
    }
}
