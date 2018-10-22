package com.gosproj.gosproject.Structures;

public class LogStruct {

    public int id;
    public int idDept;
    public double lat;
    public double lon;
    public String text;

    public LogStruct(int id, int idDept, double lat, double lon, String text){
        this.id = id;
        this.idDept = idDept;
        this.lat = lat;
        this.lon = lon;
        this.text = text;
    }
}
