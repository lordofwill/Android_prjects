package com.holymoly.coronahelper.publicmask;

public class MaskVO {
    String name;
    String addr;
    int distance;
    String remain;

    double latitude;
    double longitude;

    public MaskVO(String name, String addr, int distance, String remain, double latitude, double longitude) {
        this.name = name;
        this.addr = addr;
        this.distance = distance;
        this.remain = remain;

        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getDistance(){
        return this.distance;
    }

}
