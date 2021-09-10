package com.example.Navsi_test_task;

public class CoordinatePoint {
    private int npp;
    private int id;
    private int lat;
    private int lng;

    public CoordinatePoint(String pointString) {
        int n = pointString.length();
        npp = Integer.parseInt(pointString.substring(0,8),2);
        id = Integer.parseInt(pointString.substring(8,16),2);
        lat = Integer.parseInt(pointString.substring(16,24),2);
        lng = Integer.parseInt(pointString.substring(24,32),2);
    }
}
