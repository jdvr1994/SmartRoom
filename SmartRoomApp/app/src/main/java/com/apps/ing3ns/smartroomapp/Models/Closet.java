package com.apps.ing3ns.smartroomapp.Models;

/**
 * Created by JuanDa on 23/11/2017.
 */

public class Closet {
    int estanteUno;
    int estanteDos;
    int estanteTres;
    int estanteCuatro;
    int perchero;

    public Closet() {
    }

    public Closet(int estanteUno, int estanteDos, int estanteTres, int estanteCuatro, int perchero) {
        this.estanteUno = estanteUno;
        this.estanteDos = estanteDos;
        this.estanteTres = estanteTres;
        this.estanteCuatro = estanteCuatro;
        this.perchero = perchero;
    }

    public int getEstanteUno() {
        return estanteUno;
    }

    public void setEstanteUno(int estanteUno) {
        this.estanteUno = estanteUno;
    }

    public int getEstanteDos() {
        return estanteDos;
    }

    public void setEstanteDos(int estanteDos) {
        this.estanteDos = estanteDos;
    }

    public int getEstanteTres() {
        return estanteTres;
    }

    public void setEstanteTres(int estanteTres) {
        this.estanteTres = estanteTres;
    }

    public int getEstanteCuatro() {
        return estanteCuatro;
    }

    public void setEstanteCuatro(int estanteCuatro) {
        this.estanteCuatro = estanteCuatro;
    }

    public int getPerchero() {
        return perchero;
    }

    public void setPerchero(int perchero) {
        this.perchero = perchero;
    }
}
