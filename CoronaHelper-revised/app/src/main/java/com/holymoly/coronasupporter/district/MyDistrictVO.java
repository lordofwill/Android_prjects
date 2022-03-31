package com.holymoly.coronasupporter.district;

import java.io.Serializable;

public class MyDistrictVO implements Serializable {
    double latitude;
    double longitude;

    public MyDistrictVO(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
