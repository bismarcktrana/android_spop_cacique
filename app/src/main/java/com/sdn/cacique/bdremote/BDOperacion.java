package com.sdn.cacique.bdremote;

import android.database.Cursor;
import android.util.Log;

import com.sdn.bd.dao.softland.TblPedido;
import com.sdn.bd.host.BDLocal;
import com.sdn.bd.objeto.host.Lectura;
import com.sdn.bd.objeto.host.Operador;
import com.sdn.bd.objeto.produccion.Camion;
import com.sdn.bd.objeto.softland.Pedido;
import com.sdn.cacique.spop.FrmEscaneoxPedido;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;

public class BDOperacion  extends SGBD_Interface {
    public String Tipo;
    boolean IS_SERVER_ONLINE;

    public BDOperacion() {
    }

    public double Get_Existecia(String codigo_softland, String idbodega) {
        double cantidad_disponible = 0.0;
        V_SQLQUERYSEARCH = "SELECT CANT_DISPONIBLE FROM View_Existencia WHERE CODIGO_SOFTLAND=? AND IDBODEGA=?";

        try {
            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                V_PREPAREDSTATEMENT.setString(1, codigo_softland);
                V_PREPAREDSTATEMENT.setString(2, idbodega);

                V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                V_RESULSET.last();
                Log.i(this.getClass().getName(),"Get_ExisteciaxProducto :"+V_RESULSET.getRow()+" Registros");

                if(V_RESULSET.getRow()>0){
                    V_RESULSET.beforeFirst();
                    while (V_RESULSET.next()) {
                        cantidad_disponible = V_RESULSET.getDouble(1);
                    }

                }else{
                    cantidad_disponible=-1.00;
                }

                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            cantidad_disponible=-3.00;
            System.out.println(this.getClass().getName() + " Get_ExisteciaxProducto - SQLException" + e.getMessage());
        }

        Log.i(this.getClass().getName(),"CANTIDAD DISPONIBLE:"+cantidad_disponible);
        return cantidad_disponible;
    }

    /**
     * SE CONSULTA A LA BASE DE DATOS SQL YA QUE OTRO USUARIO PUEDE ESTAR TRABJANDO UNA ORDEN Y ALTERANDO EL PESO DEL CAMION.
     * @param camion
     * @return
     */
    public double Get_PesoCamion(Camion camion) {
        double cantidad_disponible = 0.0;
        V_SQLQUERYSEARCH = "SELECT ISNULL(SUM(peso),0.0) AS SUMA FROM detalle_lectura WHERE idcamion=? AND estado=1";

        try {
            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                V_PREPAREDSTATEMENT.setString(1,""+camion.getId());

                V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                V_RESULSET.last();

                if(V_RESULSET.getRow()>0){
                    V_RESULSET.beforeFirst();
                    while (V_RESULSET.next()) {
                        cantidad_disponible = V_RESULSET.getDouble(1);

                    }

                }else{
                    cantidad_disponible=-1.00;
                }

                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            cantidad_disponible=-3.00;
            System.out.println(this.getClass().getName() + " Get_ExisteciaxProducto - SQLException" + e.getMessage());
        }

        Log.i(this.getClass().getName(),"PESO DEL CAMION: "+cantidad_disponible+"");
        return cantidad_disponible;
    }

    /**
     * VALIDA SI LA CAJA ESCANEADA NO HA SIDO RESGISTRADO
     * @param codigo_barra
     * @return
     */
    public boolean cajaEstaRegistrada(String codigo_barra) {
        V_SQLQUERYSEARCH = "SELECT id FROM detalle_lectura WHERE barra=? AND estado=1";
        boolean respuesta = false;

        try {
            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                V_PREPAREDSTATEMENT.setString(1,codigo_barra);
                V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                V_RESULSET.last();
                respuesta = V_RESULSET.getRow()>0;
                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " cajaEstaRegistrada - SQLException" + e.getMessage());
        }
        return respuesta;
    }

    public Integer guardar_Caja(Pedido pedido, Lectura lectura, Operador operario, String iddispositivo) {
        Integer idGenerado =0;
        V_SQLQUERYSAVE = "INSERT INTO detalle_lectura(fecha,fecha_proceso,codigo,peso,piezas,serie,barra,idcamion,idconductor,idorden,idoperador,iddispositivo,estado) " +
                " VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try {

            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSAVE, Statement.RETURN_GENERATED_KEYS);
                V_PREPAREDSTATEMENT.setDate(1, new Date(lectura.getFecha().getTime()));
                V_PREPAREDSTATEMENT.setTimestamp(2, new Timestamp(lectura.getFecha_proceso().getTime()));
                V_PREPAREDSTATEMENT.setString(3, lectura.getCodigo());
                V_PREPAREDSTATEMENT.setDouble(4, lectura.getPeso());
                V_PREPAREDSTATEMENT.setInt(5, lectura.getPiezas());
                V_PREPAREDSTATEMENT.setString(6, lectura.getSerie());
                V_PREPAREDSTATEMENT.setString(7, lectura.getBarra());
                V_PREPAREDSTATEMENT.setString(8, ""+pedido.getCamion().getId());
                V_PREPAREDSTATEMENT.setString(9, pedido.getConductor().getId());
                V_PREPAREDSTATEMENT.setString(10, pedido.getId());
                V_PREPAREDSTATEMENT.setInt(11, operario.getId());
                V_PREPAREDSTATEMENT.setString(12, iddispositivo);
                V_PREPAREDSTATEMENT.setBoolean(13, true); //ESTADO ACTIVO AL GUARDA UNA NUEVA ORDEN
                V_PREPAREDSTATEMENT.execute();

                V_RESULSET = V_PREPAREDSTATEMENT.getGeneratedKeys();

                while (V_RESULSET.next())
                    idGenerado =V_RESULSET.getInt(1);//Establecer idGenerdo

                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " RegistrarCaja - SQLException" + e.getMessage());
        }
        return  idGenerado;
    }

    public boolean actualizar_existencia(String codigo_sofland,String idbodega, double peso) {
        boolean resultado = false;
        V_SQLQUERYEDIT = "UPDATE [MATCASA].[EXISTENCIA_BODEGA] SET CANT_DISPONIBLE = CANT_DISPONIBLE + ? " +
                        " WHERE ARTICULO=? AND BODEGA=?";
        System.out.println(this.getClass().getName() + " Regisrando cambio - " + codigo_sofland+" "+idbodega+" "+peso);
        try {

            V_OBJECTCONECTION = V_SPOOL_ERP.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYEDIT, Statement.RETURN_GENERATED_KEYS);
                V_PREPAREDSTATEMENT.setDouble(1, peso);
                V_PREPAREDSTATEMENT.setString(2, codigo_sofland);
                V_PREPAREDSTATEMENT.setString(3, idbodega);
                resultado = V_PREPAREDSTATEMENT.executeUpdate()>0;
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " actualizar_existencia - SQLException" + e.getMessage());
        }
        return  resultado;
    }

    public boolean borrar_Caja(String codigo_barra) {
        boolean resultado =false;
        V_SQLQUERY ="DELETE FROM detalle_lectura " +
                    "WHERE barra=? AND estado=1";
        System.out.println(this.getClass().getName() + " Borrando caja" + codigo_barra);
        try {

            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERY);
                V_PREPAREDSTATEMENT.setString(1, codigo_barra);
               resultado =  V_PREPAREDSTATEMENT.executeUpdate()>0;
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " borrar_Caja - SQLException" + e.getMessage());
        }
        return  resultado;
    }

    public boolean guardar_orden(Pedido obj_orden) {
        boolean resultado =false;
        //V_SQLQUERY = "DELETE FROM detalle_lectura  WHERE barra=? AND estado=1";
        V_SQLQUERYSAVE= "INSERT INTO pedido_detalle(idcamion,idconductor,idoperador,marchamo,atendido,estado,fecha_inicio,fecha_fin,idpedido) " +
                        " VALUES(?,?,?,?,?,?,?,?,?)";

        V_SQLQUERYEDIT= "UPDATE pedido_detalle SET idcamion=?, idconductor=?,idoperador=?,marchamo=?,atendido=?,estado=?,fecha_inicio=?,fecha_fin=?,idpedido=?" +
                        " WHERE id=?";;

        try {

            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();
            if (V_OBJECTCONECTION != null) {

                if(obj_orden.getIdservidor()>0){
                    Log.i("BDOperacion->Modificar", "guardar_orden"+ obj_orden.toString2());
                    V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYEDIT);
                    V_PREPAREDSTATEMENT.setInt(1, obj_orden.getCamion().getId());
                    V_PREPAREDSTATEMENT.setString(2, obj_orden.getConductor().getId());
                    V_PREPAREDSTATEMENT.setInt(3, obj_orden.getOperador().getId());
                    V_PREPAREDSTATEMENT.setString(4, obj_orden.getMarchamo());
                    V_PREPAREDSTATEMENT.setBoolean(5, obj_orden.getAtentido());
                    V_PREPAREDSTATEMENT.setInt(6, obj_orden.getEstado());
                    if(obj_orden.getFecha_inicio()==null){
                        V_PREPAREDSTATEMENT.setNull(7, Types.DATE);  //pst is prepared statement instance.
                    }else{
                        V_PREPAREDSTATEMENT.setDate(7, new Date(obj_orden.getFecha_inicio().getTime()));
                    }

                    if(obj_orden.getFecha_fin()==null){
                        V_PREPAREDSTATEMENT.setNull(8, Types.DATE);  //pst is prepared statement instance.
                    }else{
                        V_PREPAREDSTATEMENT.setDate(8, new Date(obj_orden.getFecha_fin().getTime()));
                    }
                    V_PREPAREDSTATEMENT.setString(9, obj_orden.getId());
                    V_PREPAREDSTATEMENT.setInt(10, obj_orden.getIdservidor());
                    resultado = V_PREPAREDSTATEMENT.executeUpdate()>0;
                    V_PREPAREDSTATEMENT.close();
                    V_OBJECTCONECTION.close();

                }else{
                    Log.i("BDOperacion->Guardar", "guardar_orden"+ obj_orden.toString2());
                    V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSAVE, Statement.RETURN_GENERATED_KEYS);
                    V_PREPAREDSTATEMENT.setInt(1, obj_orden.getCamion().getId());
                    V_PREPAREDSTATEMENT.setString(2, obj_orden.getConductor().getId());
                    V_PREPAREDSTATEMENT.setInt(3, obj_orden.getOperador().getId());
                    V_PREPAREDSTATEMENT.setString(4, obj_orden.getMarchamo());
                    V_PREPAREDSTATEMENT.setBoolean(5, obj_orden.getAtentido());
                    V_PREPAREDSTATEMENT.setInt(6, obj_orden.getEstado());

                    if(obj_orden.getFecha_inicio()==null){
                        V_PREPAREDSTATEMENT.setNull(7, Types.DATE);  //pst is prepared statement instance.
                    }else{
                        V_PREPAREDSTATEMENT.setDate(7, new Date(obj_orden.getFecha_inicio().getTime()));
                    }

                    if(obj_orden.getFecha_fin()==null){
                        V_PREPAREDSTATEMENT.setNull(8, Types.DATE);  //pst is prepared statement instance.
                    }else{
                        V_PREPAREDSTATEMENT.setDate(8, new Date(obj_orden.getFecha_fin().getTime()));
                    }

                    V_PREPAREDSTATEMENT.setString(9, obj_orden.getId());
                    V_PREPAREDSTATEMENT.execute();

                    V_RESULSET = V_PREPAREDSTATEMENT.getGeneratedKeys();
                    Integer idGenerado=0;
                    while (V_RESULSET.next()){
                        idGenerado =V_RESULSET.getInt(1);//Establecer idGenerdo
                        resultado = true;
                    }

                    obj_orden.setIdservidor(idGenerado);
                    V_RESULSET.close();
                    V_PREPAREDSTATEMENT.close();
                    V_OBJECTCONECTION.close();

                }
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " guardar_orden - SQLException" + e.getMessage());
        }
        return resultado;
    }

    public boolean borrar_pedido(Pedido obj_orden) {
        boolean resultado =false;
        V_SQLQUERY ="DELETE FROM pedido_detalle " +
                "WHERE idpedido=? AND id=?";

        System.out.println(this.getClass().getName() + " Borrando pedido de pedido_detalle");

        try {
            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERY);
                V_PREPAREDSTATEMENT.setString(1, obj_orden.getId());
                V_PREPAREDSTATEMENT.setInt(2, obj_orden.getIdservidor());
                resultado =  V_PREPAREDSTATEMENT.executeUpdate()>0;
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " borrar_pedido - SQLException" + e.getMessage());
        }
        return  resultado;
    }

}
