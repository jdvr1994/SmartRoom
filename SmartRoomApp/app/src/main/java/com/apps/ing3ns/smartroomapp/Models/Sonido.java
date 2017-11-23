package com.apps.ing3ns.smartroomapp.Models;

/**
 * Created by JuanDa on 23/11/2017.
 */

public class Sonido {
    boolean power;
    int funcion;
    boolean play;
    int volumen;

    public Sonido() {
    }

    public Sonido(boolean power, int funcion, boolean play, int volumen) {
        this.power = power;
        this.funcion = funcion;
        this.play = play;
        this.volumen = volumen;
    }

    public boolean isPower() {
        return power;
    }

    public void setPower(boolean power) {
        this.power = power;
    }

    public int getFuncion() {
        return funcion;
    }

    public void setFuncion(int funcion) {
        this.funcion = funcion;
    }

    public boolean isPlay() {
        return play;
    }

    public void setPlay(boolean play) {
        this.play = play;
    }

    public int getVolumen() {
        return volumen;
    }

    public void setVolumen(int volumen) {
        this.volumen = volumen;
    }
}
