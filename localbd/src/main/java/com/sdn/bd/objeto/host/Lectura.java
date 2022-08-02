package com.sdn.bd.objeto.host;

import java.util.Date;

public class Lectura {
    Integer id;
    Date fecha;
    Date fecha_proceso;
    String codigo;
    double peso= 0.00;
    Integer piezas;
    String serie;
    String barra;
    Integer idcamion;
    String idconductor;
    String idorden;
    Integer idservidor;
    Integer idoperador;

    public Lectura() {
        this.id = -1;
    }

    public Lectura(Integer id, Date fecha,Date fecha_proceso, String codigo, float peso, Integer piezas, String serie, String barra, Integer idcamion, String idconductor, String idorden, Integer idservidor,Integer idoperador) {
        this.id = id;
        this.fecha = fecha;
        this.fecha = fecha_proceso;
        this.codigo = codigo;
        this.peso = peso;
        this.piezas = piezas;
        this.serie = serie;
        this.barra = barra;
        this.idcamion = idcamion;
        this.idconductor = idconductor;
        this.idorden = idorden;
        this.idservidor = idservidor;
        this.idoperador =idoperador;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
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

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
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

    public Integer getIdcamion() {
        return idcamion;
    }

    public void setIdcamion(Integer idcamion) {
        this.idcamion = idcamion;
    }

    public String getIdconductor() {
        return idconductor;
    }

    public void setIdconductor(String idconductor) {
        this.idconductor = idconductor;
    }

    public String getIdorden() {
        return idorden;
    }

    public void setIdorden(String idorden) {
        this.idorden = idorden;
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
        return "Lectura{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", fecha_proceso=" + fecha_proceso +
                ", codigo='" + codigo + '\'' +
                ", peso=" + peso +
                ", piezas=" + piezas +
                ", serie='" + serie + '\'' +
                ", barra='" + barra + '\'' +
                ", idcamion=" + idcamion +
                ", idconductor='" + idconductor + '\'' +
                ", idorden='" + idorden + '\'' +
                ", idservidor=" + idservidor +
                '}';
    }


}
