package com.example.bus;

public class RouteVO {

    public String busStopName;
    public int retrunFlag;
    public String busstopID;

    public RouteVO(String busStopName, int retrunFlag, String busstopID){
        this.busStopName = busStopName;
        this.retrunFlag = retrunFlag;
        this.busstopID = busstopID;
    }
}
