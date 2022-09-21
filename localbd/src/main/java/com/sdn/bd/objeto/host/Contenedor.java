package com.sdn.bd.objeto.host;

import java.util.Date;

public class Contenedor {
    Integer id;
    Date fecha_creacion;
    Date fecha_inicio;
    Date fecha_fin;
    String nombre;
    Integer idservidor;
    Integer idoperador;

    public Contenedor() {
        this.id =-1;
    }

    public Contenedor(Integer id, Date fecha_creacion, Date fecha_inicio, Date fecha_fin, String nombre, Integer idservidor, Integer idoperador) {
        this.id = id;
        this.fecha_creacion = fecha_creacion;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.nombre = nombre;
        this.idservidor = idservidor;
        this.idoperador = idoperador;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(Date fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }

    public Date getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(Date fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public Date getFecha_fin() {
        return fecha_fin;
    }

    public void setFecha_fin(Date fecha_fin) {
        this.fecha_fin = fecha_fin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdservidor() {
        return idservidor;
    }

    public void setIdservidor(Integer idservidor) {
        this.idservidor = idservidor;
    }

    public Integer getIdoperador() {
        return idoperador;
    }

    public void setIdoperador(Integer idoperador) {
        this.idoperador = idoperador;
    }

    @Override
    public String toString() {
        return  nombre ;
    }

    public String toString2() {
        return "Contenedor{" +
                "id=" + id +
                ", fecha_creacion=" + fecha_creacion +
                ", fecha_inicio=" + fecha_inicio +
                ", fecha_fin=" + fecha_fin +
                ", nombre='" + nombre + '\'' +
                ", idservidor=" + idservidor +
                ", idoperador=" + idoperador +
                '}';
    }
}
