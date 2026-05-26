package com.ticketsystem.dto.request;

public class ShiftRequest {
    private String name;
    private String startTime;
    private String endTime;
    private String timezone;

    public String getName() { return name; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getTimezone() { return timezone; }
    public void setName(String name) { this.name = name; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setTimezone(String timezone) { this.timezone = timezone; }
}