package com.sdn.bd.objeto.produccion;

//SELECT Id AS ID, PlacaNo AS NOPLACA, Min AS MIN, Max AS MAX
//FROM CAMIONES
//WHERE (Valor = 1)
//ORDER BY NOPLACA

import android.util.Log;

public class Camion {
    Integer id=null;
    String noplaca="";
    int min;
    int max;

    public Camion() {
        this.id = null;
    }

    public Camion(int id, String noplaca, int min, int max ) {
        this.id = id;
        this.noplaca = noplaca;
        this.min = min;
        this.max = max;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNoplaca() {
        return noplaca;
    }

    public void setNoplaca(String noplaca) {
        this.noplaca = noplaca;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Override
    public String toString() {
        return noplaca ;
    }

    public String toString2() {
        return "Camion{" +
                "id=" + id +
                ", noplaca='" + noplaca + '\'' +
                ", min=" + min +
                ", max=" + max +
                '}';
    }

    public void printLog(String classname) {
        Log.i(classname,"Camion seleccionado: "+ toString2());
    }
}
