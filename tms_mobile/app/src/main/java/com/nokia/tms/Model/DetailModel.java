package com.nokia.tms.Model;

public class DetailModel {
    public long TicketId;
    public String TesterId,OperationId,OpenTime,ItemCode,ProductCode,Line,Status;

    public DetailModel(long ticketId, String testerId, String operationId, String openTime, String itemCode, String productCode, String line, String status) {
        TicketId = ticketId;
        TesterId = testerId;
        OperationId = operationId;
        OpenTime = openTime;
        ItemCode = itemCode;
        ProductCode = productCode;
        Line = line;
        Status = status;
    }

    public DetailModel() {
    }

    public long getTicketId() {
        return TicketId;
    }

    public void setTicketId(long ticketId) {
        TicketId = ticketId;
    }

    public String getTesterId() {
        return TesterId;
    }

    public void setTesterId(String testerId) {
        TesterId = testerId;
    }

    public String getOperationId() {
        return OperationId;
    }

    public void setOperationId(String operationId) {
        OperationId = operationId;
    }

    public String getOpenTime() {
        return OpenTime;
    }

    public void setOpenTime(String openTime) {
        OpenTime = openTime;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getLine() {
        return Line;
    }

    public void setLine(String line) {
        Line = line;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
