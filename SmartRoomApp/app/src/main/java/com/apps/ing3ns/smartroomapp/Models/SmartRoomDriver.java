package com.apps.ing3ns.smartroomapp.Models;

/**
 * Created by JuanDa on 20/11/2017.
 */

public class SmartRoomDriver {
    int id;
    String user;
    String pass;
    String token;
    int led1;

    public SmartRoomDriver() {
    }

    public SmartRoomDriver(int id, String user, String pass, String token, int led1) {
        this.id = id;
        this.user = user;
        this.pass = pass;
        this.token = token;
        this.led1 = led1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getLed1() {
        return led1;
    }

    public void setLed1(int led1) {
        this.led1 = led1;
    }
}
