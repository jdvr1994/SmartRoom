package com.apps.ing3ns.smartroomapp.Models;

/**
 * Created by JuanDa on 20/11/2017.
 */

public class SmartRoomDriver {
    int id;
    String user;
    String pass;
    String token;
    ControlPrimario controlPrimario;
    Closet closet;
    Sonido sonido;

    public SmartRoomDriver() {
    }

    public SmartRoomDriver(int id, String user, String pass, String token, ControlPrimario controlPrimario, Closet closet, Sonido sonido) {
        this.id = id;
        this.user = user;
        this.pass = pass;
        this.token = token;
        this.controlPrimario = controlPrimario;
        this.closet = closet;
        this.sonido = sonido;
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

    public ControlPrimario getControlPrimario() {
        return controlPrimario;
    }

    public void setControlPrimario(ControlPrimario controlPrimario) {
        this.controlPrimario = controlPrimario;
    }

    public Closet getCloset() {
        return closet;
    }

    public void setCloset(Closet closet) {
        this.closet = closet;
    }

    public Sonido getSonido() {
        return sonido;
    }

    public void setSonido(Sonido sonido) {
        this.sonido = sonido;
    }
}
