package com.sdn.bd.objeto.softland;

//SELECT BODEGA AS Id,NOMBRE, TIPO FROM MATCASA.BODEGA;
public class Bodega {
    String id;
    String nombre="";
    String tipo;

    public Bodega() {
        this.id=null;
    }

    public Bodega(String id, String nombre, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

      @Override
    public String toString() {
        return  nombre;
    }

    public String toString2() {
        return "Bodega{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
