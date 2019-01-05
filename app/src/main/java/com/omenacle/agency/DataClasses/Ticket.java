package com.omenacle.agency.DataClasses;

import android.support.annotation.NonNull;

public class Ticket {


    //Agency (agency Key)
    @NonNull
    private String a;
    //Branch (branch key)
    @NonNull
    private String b;
    //Ticket Code
    @NonNull
    private long code;
    //Travel date
    @NonNull
    private String date;
    //Name of ticket bearer
    @NonNull
    private String name;
    //Number of ticket bearer
    @NonNull
    private long num;
    //Id card number of bearer
    private long id;
    //Travel route (e.g limbe->bamenda)
    @NonNull
    private String r;
    @NonNull
    //Status (Not Used: n, Used: u)
    private String status;
    //Search key = route+"_"+time
    private String r_time;

    public String getR_time() {
        return r_time;
    }

    public void setR_time(String r_time) {
        this.r_time = r_time;
    }

    @NonNull
    public String getStatus() {
        return status;
    }

    public void setStatus(@NonNull String status) {
        this.status = status;
    }

    //Travel time (e.g Morning, Afternoon, Night)
    private String time;

    public Ticket(@NonNull String a, @NonNull String b, @NonNull long code, @NonNull String date, @NonNull String name, @NonNull long num, long id, @NonNull String r, String time, String status, String r_time) {
        this.a = a;
        this.b = b;
        this.code = code;
        this.date = date;
        this.name = name;
        this.num = num;
        this.id = id;
        this.r = r;
        this.time = time;
        this.status = status;
        this.r_time = r_time;
    }

    public Ticket() {

    }

    //Public getters
    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public long getCode() {
        return code;
    }

    public String getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public long getNum() {
        return num;
    }

    public long getId() {
        return id;
    }

    public String getR() {
        return r;
    }

    public String getTime() {
        return time;
    }


    //Public Setters
    public void setA(String a) {
        this.a = a;
    }

    public void setB(String b) { this.b = b; }

    public void setCode(@NonNull long code) {
        this.code = code;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setR(String r) {
        this.r = r;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
