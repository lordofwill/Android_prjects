package com.example.bus;

public class ArriveVO {
    public String busName;
    public String busLocation;
    public String busTime;
    public String busID;

    public ArriveVO(String busName, String busLocation, String busTime, String busID){
        this.busName = busName;
        this.busLocation = busLocation;
        this.busTime = busTime;
        this.busID = busID;
    }
}
