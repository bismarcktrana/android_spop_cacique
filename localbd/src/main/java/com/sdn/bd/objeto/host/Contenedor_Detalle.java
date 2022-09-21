package com.sdn.bd.objeto.host;

import java.util.Date;

public class Contenedor_Detalle {
    Integer id;
    Date fecha_creacion;
    Date fecha_proceso;
    String codigo;
    double peso= 0.00;
    Integer piezas;
    String serie;
    String barra;
    Integer tipo;
    String idcontenedor;
    Integer idservidor;
    Integer idoperador;

    public Contenedor_Detalle() {
        this.id = -1;
    }

    public Contenedor_Detalle(Integer id, Date fecha_creacion, Date fecha_proceso, String codigo, double peso, Integer piezas, String serie, String barra, Integer tipo, String idcontenedor, Integer idservidor, Integer idoperador) {
        this.id = id;
        this.fecha_creacion = fecha_creacion;
        this.fecha_proceso = fecha_proceso;
        this.codigo = codigo;
        this.peso = peso;
        this.piezas = piezas;
        this.serie = serie;
        this.barra = barra;
        this.tipo = tipo;
        this.idcontenedor = idcontenedor;
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

    public Date getFecha_proceso() {
        return fecha_proceso;
    }

    public void setFecha_proceso(Date fecha_proceso) {
        this.fecha_proceso = fecha_proceso;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public Integer getPiezas() {
        return piezas;
    }

    public void setPiezas(Integer piezas) {
        this.piezas = piezas;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getBarra() {
        return barra;
    }

    public void setBarra(String barra) {
        this.barra = barra;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public String getIdcontenedor() {
        return idcontenedor;
    }

    public void setIdcontenedor(String idcontenedor) {
        this.idcontenedor = idcontenedor;
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
        return barra;
    }

    public String toString2() {
        return "Contenedor_Detalle{" +
                "id=" + id +
                ", fecha_creacion=" + fecha_creacion +
                ", fecha_proceso=" + fecha_proceso +
                ", codigo='" + codigo + '\'' +
                ", peso=" + peso +
                ", piezas=" + piezas +
                ", serie='" + serie + '\'' +
                ", barra='" + barra + '\'' +
                ", tipo=" + tipo +
                ", idcontenedor='" + idcontenedor + '\'' +
                ", idservidor=" + idservidor +
                ", idoperador=" + idoperador +
                '}';
    }
}
