package com.sdn.bd.objeto.softland;

//SELECT        MATCASA.PEDIDO_LINEA.PEDIDO_LINEA AS id, MATCASA.PEDIDO_LINEA.PEDIDO AS idpedido, MATCASA.PEDIDO_LINEA.ARTICULO,
//                         MATCASA.PEDIDO_LINEA.ESTADO, MATCASA.PEDIDO_LINEA.CANTIDAD_PEDIDA
//FROM            MATCASA.PEDIDO_LINEA INNER JOIN
//                         MATCASA.PEDIDO ON MATCASA.PEDIDO_LINEA.PEDIDO = MATCASA.PEDIDO.PEDIDO
//WHERE        (MATCASA.PEDIDO.ESTADO = 'A')

public class Pedido_Detalle {
    Integer id;
    float cantidad;
    String idarticulo;
    String idpedido;

    public Pedido_Detalle() {
        this.id=-1;
    }

    /**
     *
     * @param id
     * @param cantidad
     * @param idarticulo
     * @param idpedido
     */
    public Pedido_Detalle(Integer id, float cantidad, String idarticulo,String idpedido) {
        this.id = id;
        this.cantidad = cantidad;
        this.idarticulo = idarticulo;
        this.idpedido = idpedido;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIdpedido() {
        return idpedido;
    }

    public void setIdpedido(String idpedido) {
        this.idpedido = idpedido;
    }

    public String getIdarticulo() {
        return idarticulo;
    }

    public void setIdarticulo(String idarticulo) {
        this.idarticulo = idarticulo;
    }

    public float getCantidad() {
        return cantidad;
    }

    public void setCantidad(float cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public String toString() {
        return idarticulo ;
    }

    public String toString2() {
        return "Pedido_Detalle{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", idarticulo='" + idarticulo + '\'' +
                ", idpedido='" + idpedido +
                '}';
    }
}
