package com.example.bus;

public class StationVO {
    public String name;
    public String id;
    public String next;
    public int bookmark;
    public double latitude;
    public double longitude;

    public StationVO(String name, int id, String next, int bookmark, double latitude, double longitude){
        this.name = name;
        this.id = Integer.toString(id);
        this.next = next;
        this.bookmark = bookmark;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

