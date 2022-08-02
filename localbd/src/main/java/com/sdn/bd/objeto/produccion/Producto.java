package com.sdn.bd.objeto.produccion;

        //SELECT        TOP (100) PERCENT ID, PRODUCTO AS CODIGO, NOMBRE, GENERO AS TIPO, Art_Softland AS SOFTLAND_ID, vConversion AS UNIDAD
        //FROM            dbo.CATALOGO
        //WHERE        (ESTADO = N'N')

import com.sdn.bd.util.ConfApp;

public class Producto {
    Integer id;
    String codigo;
    String codigo_softland;
    String nombre;
    String tipo;
    Integer unidad;

    public Producto() {
        this.id = -1;
    }

    public Producto(Integer id, String codigo, String nombre, String tipo, String codigo_softland, Integer unidad) {
        this.id = id;
        this.codigo = codigo;
        this.codigo_softland = codigo_softland;
        this.nombre = nombre;
        this.tipo = tipo;
        this.unidad = unidad;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo_softland() {
        return codigo_softland;
    }

    public void setCodigo_softland(String codigo_softland) {
        this.codigo_softland = codigo_softland;
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

    public Integer getUnidad() {
        return unidad;
    }

    public void setUnidad(Integer unidad) {
        this.unidad = unidad;
    }

    public String toString2() {
        return "Producto{" +
                "id=" + id +
                ", codigo='" + codigo + '\'' +
                ", codigo_softland='" + codigo_softland + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", unidad=" + unidad +
                '}';
    }

    @Override
    public String toString() {
        return nombre;
    }
}
