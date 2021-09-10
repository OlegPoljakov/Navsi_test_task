package com.example.Navsi_test_task;

import java.util.ArrayList;

public final class Database {

    private static Database instance;
    private ArrayList<CoordinatePoint> points = new ArrayList<CoordinatePoint>();

    private void Database() {
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public void addPointToDatabase(CoordinatePoint point){
        points.add(point);
    }

    public String printNumOfPoints(){
        return "database stores " + points.size() + " point with coordinates";
    }
}
