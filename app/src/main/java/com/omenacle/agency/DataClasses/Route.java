package com.omenacle.agency.DataClasses;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Route{
    //Agency Key
    protected String a_k;
    //Branch Key
    protected String b_k;

    public String getR_k() {
        return r_k;
    }

    public void setR_k(String r_k) {
        this.r_k = r_k;
    }

    //route key
    protected String r_k;
    //Route booking fare
    protected long price;
    //Route (Road to travel i.e From -> To)
    protected String route;
    //Travel Time
    protected String travel_time;


    public Route(){

        // Default constructor required for calls to DataSnapshot.getValue(Route.class)

    }
    public Route(String a_k, String b_k, String r_k, long price, String route, String travel_time) {
        this.a_k = a_k;
        this.b_k = b_k;
        this.r_k = r_k;
        this.price = price;
        this.route = route;
        this.travel_time = travel_time;
    }

    public String getA_k() {
        return a_k;
    }

    public void setA_k(String a_k) {
        this.a_k = a_k;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getTravel_time() {
        return travel_time;
    }

    public void setTravel_time(String travel_time) {
        this.travel_time = travel_time;
    }
    public String getB_k() {
        return b_k;
    }

    public void setB_k(String b_k) {
        this.b_k = b_k;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("a_k", a_k);
        result.put("b_k", b_k);
        result.put("price", price);
        result.put("route", route);
        result.put("travel_time", travel_time);

        return result;
    }
}
