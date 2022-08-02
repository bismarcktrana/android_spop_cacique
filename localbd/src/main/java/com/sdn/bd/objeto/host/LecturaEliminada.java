package com.sdn.bd.objeto.host;

public class LecturaEliminada {
    int idservidor;
    String barra;
    String idorden;

    public LecturaEliminada() {
        this.idservidor = -1;
    }

    public LecturaEliminada(int idservidor, String barra,String idorden) {
        this.idservidor = idservidor;
        this.barra = barra;
        this.idorden =idorden;
    }

    public int getIdservidor() {
        return idservidor;
    }

    public void setIdservidor(int idservidor) {
        this.idservidor = idservidor;
    }

    public String getBarra() {
        return barra;
    }

    public void setBarra(String barra) {
        this.barra = barra;
    }

    public String getIdorden() {
        return idorden;
    }

    public void setIdorden(String idorden) {
        this.idorden = idorden;
    }

    @Override
    public String toString() {
        return ""+idservidor;
    }
}
