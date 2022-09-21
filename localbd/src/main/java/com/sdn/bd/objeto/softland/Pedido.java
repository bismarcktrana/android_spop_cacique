package com.sdn.bd.objeto.softland;

import java.util.ArrayList;
import java.util.Date;

import com.sdn.bd.objeto.host.Operador;
import com.sdn.bd.objeto.produccion.Camion;
import com.sdn.bd.util.Utils;


//SELECT PEDIDO AS id, FECHA_PEDIDO AS fecha, EMBARCAR_A AS cliente, NOMBRE_CLIENTE AS ubicacion, UPPER(REPLACE(DIRECCION_FACTURA, 'DETALLE: ', ''))
// AS direccion, RUTA AS tipo, CLIENTE AS iddestino, BODEGA AS idbodega
//FROM MATCASA.PEDIDO
//WHERE (ESTADO = 'a')

public class Pedido {
    String Id =null;
    Date fecha=null;
    String cliente;
    String ubicacion="";
    String direccion="";
    String tipo;
    String marchamo="";
    String iddestino;
    Bodega bodega = new Bodega();

    Boolean atentido=false;
    Integer estado=0;
    Integer idservidor=0;
    Date fecha_inicio=null;
    Date fecha_fin=null;

    ArrayList<Pedido_Detalle> pedido_detalle = new ArrayList<Pedido_Detalle>();
    Camion camion = new Camion();
    Conductor conductor = new Conductor();
    Operador operador = new Operador();

    public Pedido() {
        this.Id = null;
    }

    public Pedido(String id, Date fecha, String cliente, String ubicacion, String direccion, String tipo, String iddestino, String idbodega) {
        this.Id = id;
        this.fecha = fecha;
        this.cliente = cliente;
        this.ubicacion = ubicacion;
        this.direccion = direccion;
        this.tipo = tipo;
        this.iddestino = iddestino;
        this.bodega.setId(idbodega);
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIddestino() {
        return iddestino;
    }

    public void setIddestino(String iddestino) {
        this.iddestino = iddestino;
    }

    public String getIdBodega() {
        return bodega.getId();
    }

    public void setIdBodega(String idbodega) {
        this.bodega.setId(idbodega);
    }

/***********************************METODOS PROPIOS DEL OBJETO PEDIDO***********************/

    public String getMarchamo() {
        return marchamo;
    }

    public void setMarchamo(String marchamo) {
        this.marchamo = marchamo;
    }

    public Boolean getAtentido() {
        return atentido;
    }

    public void setAtentido(Boolean atentido) {
        this.atentido = atentido;
    }

    public Integer getIdservidor() {
        return idservidor;
    }

    public void setIdservidor(Integer idservidor) {
        this.idservidor = idservidor;
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

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    /***********************************METODOS PROPIOS DEL OBJETO PEDIDO PARA OBTENER OTRO OBJETO***********************/

    public Bodega getBodega() {
        return bodega;
    }

    public void setBodega(Bodega bodega) {
        this.bodega = bodega;
    }

    public Camion getCamion() {
        return camion;
    }

    public void setCamion(Camion camion) {
        this.camion = camion;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public void setConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public Operador getOperador() {
        return operador;
    }

    public void setOperador(Operador operador) {
        this.operador = operador;
    }

    public ArrayList<Pedido_Detalle> getPedido_detalle() {
        return pedido_detalle;
    }

    public void setPedido_detalle(ArrayList<Pedido_Detalle> pedido_detalle) {
        this.pedido_detalle = pedido_detalle;
    }

    /***********************************DETALLE DE ARTICULO***********************/
   /* @Override
    public String toString() {
        return "Pedido{" +
                "Id='" + Id + '\'' +
                ", fecha=" + fecha +
                ", cliente='" + cliente + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", direccion='" + direccion + '\'' +
                ", tipo='" + tipo + '\'' +
                ", marchamo='" + marchamo + '\'' +
                ", iddestino='" + iddestino + '\'' +
                ", bodega=" + bodega +
                ", atentido=" + atentido +
                ", estado=" + estado +
                ", idservidor=" + idservidor +
                ", fecha_inicio=" + fecha_inicio +
                ", fecha_fin=" + fecha_fin +
                ", pedido_detalle=" + pedido_detalle +
                ", camion=" + camion +
                ", conductor=" + conductor +
                ", operador=" + operador +
                '}';
    }*/


    @Override
    public String toString() {
        return Id ;
    }

    public String toString2() {
        StringBuffer items = new StringBuffer();
        for (int it = 0; it < pedido_detalle.size(); it++)
            items.append("Item #"+(it+1) + pedido_detalle.get(it).toString2());

        return "Pedido{" +
                "Id='" + Id + '\'' +
                ", fecha=" + Utils.C_DateToDBFORMAT(fecha) +
                ", cliente='" + cliente + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                ", direccion='" + direccion + '\'' +
                ", tipo='" + tipo + '\'' +
                ", marchamo='" + marchamo + '\'' +
                ", iddestino='" + iddestino + '\'' +
                ", bodega=" + bodega.toString2() +
                ", atentido=" + atentido +
                ", estado=" + estado +
                ", idservidor=" + idservidor +
                ", fecha_inicio=" + fecha_inicio +
                ", fecha_fin=" + fecha_fin +
                ", pedido_detalle=" + pedido_detalle.size() +
                ", camion=" + camion.toString2() +
                ", conductor=" + conductor.toString2() +
                ", operador=" + operador.toString2() +
                '}'+"\n"+items.toString();
    }
}
