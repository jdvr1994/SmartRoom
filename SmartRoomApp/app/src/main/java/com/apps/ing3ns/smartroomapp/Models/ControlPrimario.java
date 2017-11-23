package com.apps.ing3ns.smartroomapp.Models;

/**
 * Created by JuanDa on 23/11/2017.
 */

public class ControlPrimario {
    boolean luzPrincipal;
    int luzSecundaria;
    int persiana;
    boolean puerta;
    int temperatura;
    int humedad;

    public ControlPrimario() {
    }

    public ControlPrimario(boolean luzPrincipal, int luzSecundaria, int persiana, boolean puerta, int temperatura, int humedad) {
        this.luzPrincipal = luzPrincipal;
        this.luzSecundaria = luzSecundaria;
        this.persiana = persiana;
        this.puerta = puerta;
        this.temperatura = temperatura;
        this.humedad = humedad;
    }

    public boolean isLuzPrincipal() {
        return luzPrincipal;
    }

    public void setLuzPrincipal(boolean luzPrincipal) {
        this.luzPrincipal = luzPrincipal;
    }

    public int getLuzSecundaria() {
        return luzSecundaria;
    }

    public void setLuzSecundaria(int luzSecundaria) {
        this.luzSecundaria = luzSecundaria;
    }

    public int getPersiana() {
        return persiana;
    }

    public void setPersiana(int persiana) {
        this.persiana = persiana;
    }

    public boolean isPuerta() {
        return puerta;
    }

    public void setPuerta(boolean puerta) {
        this.puerta = puerta;
    }

    public int getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(int temperatura) {
        this.temperatura = temperatura;
    }

    public int getHumedad() {
        return humedad;
    }

    public void setHumedad(int humedad) {
        this.humedad = humedad;
    }
}
