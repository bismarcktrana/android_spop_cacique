package com.sdn.bd.dao.softland;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sdn.bd.dao.host.TblLectura;
import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.bd.objeto.host.Lectura;
import com.sdn.bd.objeto.softland.Pedido;
import com.sdn.bd.util.Utils;

import java.util.ArrayList;

public class TblPedido {
    private static final String table_name = "soft_pedido";
    private static final String table_fields = "id,fecha,cliente,ubicacion,direccion,tipo,marchamo,iddestino,idbodega,idcamion,idconductor,atendido,estado,idservidor,fecha_inicio,fecha_fin,idoperador";
    private static final String table_where = "id=?";
    private static final String table_order = " ORDER BY id ASC";

    private static final String SQLClearTable = "DELETE from " + table_name;
    private static final String SQLObtenerRegistros = "SELECT " + table_fields + " FROM " + table_name+table_order;
    private static final String SQLObtenerRegistro = "SELECT " + table_fields + " FROM " + table_name + "  WHERE " + table_where+table_order;
    private static final String SQLObtenerRegistrosUtilizados = "SELECT " + table_fields + " FROM " + table_name + " WHERE idservidor=0 AND fecha_inicio IS NOT NULL AND fecha_fin IS NOT NULL AND (idoperador>0 OR idoperador IS NOT NULL) AND atendido=1"+table_order;
    private static final String SQLObtenerRegistrosNoUtilizados ="SELECT " + table_fields + " FROM " + table_name + " WHERE idservidor=0 AND fecha_inicio ISNULL AND (idoperador=0 OR idoperador ISNULL)"+table_order;

//private static final String SQLObtenerRegistrosSimplificado = "SELECT orden.id, orden.fecha, orden.fecha_min_proc, orden.cliente,orden.destino,orden.atendido,orden.tiene_fecha_proceso,orden._estado,orden._idservidor, orden.fecha_inicio,orden.fecha_fin,orden.idoperador,COALESCE(destino.id,0,destino.id),COALESCE(destino.nombre,'',destino.nombre) FROM orden LEFT JOIN destino ON orden.destino=destino.id";
    //  private static final String SQLObtenerRegistrosNoUtilizados = "SELECT " + table_fields + " FROM " + table_name + " WHERE idservidor=0 AND fecha_inicio ISNULL AND (idoperador=0 OR idoperador ISNULL)";


    public static void vaciarTabla(Context contexto) {
        BDUtil.EmptyTable(contexto, table_name, null, null);
    }

    public static boolean guardar(Context contexto, Pedido objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("id", objeto.getId());

        if (objeto.getFecha() == null)
            nuevoRegistro.putNull("fecha");
        else
            nuevoRegistro.put("fecha", Utils.C_DateToDBFORMAT(objeto.getFecha()));

        nuevoRegistro.put("cliente", objeto.getCliente());
        nuevoRegistro.put("ubicacion", objeto.getUbicacion());
        nuevoRegistro.put("direccion", objeto.getDireccion());
        nuevoRegistro.put("tipo", objeto.getTipo());
        nuevoRegistro.put("marchamo", objeto.getMarchamo());
        nuevoRegistro.put("iddestino", objeto.getIddestino());
        nuevoRegistro.put("idbodega", objeto.getBodega().getId());

        if (objeto.getCamion().getId() == null)
            nuevoRegistro.putNull("idcamion");
        else
            nuevoRegistro.put("idcamion", objeto.getCamion().getId());

        if (objeto.getConductor().getId() == null)
            nuevoRegistro.putNull("idconductor");
        else
            nuevoRegistro.put("idconductor", objeto.getConductor().getId());

        nuevoRegistro.put("atendido", objeto.getAtentido());
        nuevoRegistro.put("estado", objeto.getEstado());

        nuevoRegistro.put("idservidor", objeto.getIdservidor());

        if (objeto.getFecha_inicio() == null)
            nuevoRegistro.putNull("fecha_inicio");
        else
            nuevoRegistro.put("fecha_inicio", Utils.C_TimeStampToDBFORMAT(objeto.getFecha_inicio()));

        if (objeto.getFecha_fin() == null)
            nuevoRegistro.putNull("fecha_fin");
        else
            nuevoRegistro.put("fecha_fin", Utils.C_TimeStampToDBFORMAT(objeto.getFecha_fin()));

        if (objeto.getOperador().getId() == -1)
            nuevoRegistro.putNull("idoperador");
        else
            nuevoRegistro.put("idoperador", objeto.getOperador().getId());

        if(objeto.getPedido_detalle().size()>0){ //SI EL PEDIDO TIENEN DETALLE QUE IMPORTAR
            for(int contDetalle =0; contDetalle<objeto.getPedido_detalle().size();contDetalle++)
                TblPedido_Detalle.guardar(contexto,objeto.getPedido_detalle().get(contDetalle));

            resultado = BDUtil.InsertRow(contexto, table_name, null, nuevoRegistro);
        }

        return resultado > 0;
    }

    public static boolean modificar(Context contexto, Pedido objeto) {
        long resultado = -1;
        String[] args = new String[]{"" + objeto.getId()};
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("id", objeto.getId());

        if (objeto.getFecha() == null)
            nuevoRegistro.putNull("fecha");
        else
            nuevoRegistro.put("fecha", Utils.C_DateToDBFORMAT(objeto.getFecha()));

        nuevoRegistro.put("cliente", objeto.getCliente());
        nuevoRegistro.put("ubicacion", objeto.getUbicacion());
        nuevoRegistro.put("direccion", objeto.getDireccion());
        nuevoRegistro.put("tipo", objeto.getTipo());
        nuevoRegistro.put("marchamo", objeto.getMarchamo());
        nuevoRegistro.put("iddestino", objeto.getIddestino());
        nuevoRegistro.put("idbodega", objeto.getBodega().getId());

        if (objeto.getCamion().getId() == null)
            nuevoRegistro.putNull("idcamion");
        else
            nuevoRegistro.put("idcamion", objeto.getCamion().getId());

        if (objeto.getConductor().getId() == null)
            nuevoRegistro.putNull("idconductor");
        else
            nuevoRegistro.put("idconductor", objeto.getConductor().getId());

        nuevoRegistro.put("atendido", objeto.getAtentido());
        nuevoRegistro.put("estado", objeto.getEstado());

        nuevoRegistro.put("idservidor", objeto.getIdservidor());

        if (objeto.getFecha_inicio() == null)
            nuevoRegistro.putNull("fecha_inicio");
        else
            nuevoRegistro.put("fecha_inicio", Utils.C_TimeStampToDBFORMAT(objeto.getFecha_inicio()));

        if (objeto.getFecha_fin() == null)
            nuevoRegistro.putNull("fecha_fin");
        else
            nuevoRegistro.put("fecha_fin", Utils.C_TimeStampToDBFORMAT(objeto.getFecha_fin()));

        if (objeto.getOperador().getId() == -1)
            nuevoRegistro.putNull("idoperador");
        else
            nuevoRegistro.put("idoperador", objeto.getOperador().getId());

        /*if(objeto.getPedido_detalle().size()>0){ //SI EL PEDIDO TIENEN DETALLE QUE IMPORTAR
            for(int contDetalle =0; contDetalle<objeto.getPedido_detalle().size();contDetalle++)
                TblPedido_Detalle.guardar(contexto,objeto.getPedido_detalle().get(contDetalle));

            resultado = BDUtil.InsertRow(contexto, table_name, null, nuevoRegistro);

        }*/
        resultado = BDUtil.UpdateRow(contexto,table_name,"id=?",args,nuevoRegistro);

        return resultado > 0;
    }

    public static ArrayList<Pedido> obtenerRegistros(Context contexto) {
        ArrayList<Pedido> Lista = new ArrayList<Pedido>();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {//id,fecha,cliente,ubicacion,direccion,tipo,marchamo,iddestino,idbodega,idcamion,idconductor,atendido,estado,idservidor,fecha_inicio,fecha_fin,idoperador
                    Pedido objeto = new Pedido();
                    objeto.setId(result.getString(0));
                    objeto.setFecha(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setCliente(result.getString(2));
                    objeto.setUbicacion(result.getString(3));
                    objeto.setDireccion(result.getString(4));
                    objeto.setTipo(result.getString(5));
                    objeto.setMarchamo(result.getString(6));
                    objeto.setIddestino(result.getString(7));
                    objeto.getBodega().setId(result.getString(8));
                    objeto.getCamion().setId(result.getInt(9));
                    objeto.getConductor().setId(result.getString(10));
                    objeto.setAtentido(result.getInt(11) > 0);
                    objeto.setEstado(result.getInt(12));
                    objeto.setIdservidor(result.getInt(13));
                    objeto.setFecha_inicio(Utils.C_StringToObjetTimeStamp(result.getString(14)));
                    objeto.setFecha_fin(Utils.C_StringToObjetTimeStamp(result.getString(15)));
                    objeto.getOperador().setId(result.getInt(16));
                    Lista.add(objeto);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Lista;
    }

    public static ArrayList<Pedido> obtenerPedidosTrabajados(Context contexto) {
        ArrayList<Pedido> Lista = new ArrayList<Pedido>();
        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistrosUtilizados, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Pedido objeto = new Pedido();
                    objeto.setId(result.getString(0));
                    objeto.setFecha(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setCliente(result.getString(2));
                    objeto.setUbicacion(result.getString(3));
                    objeto.setDireccion(result.getString(4));
                    objeto.setTipo(result.getString(5));
                    objeto.setMarchamo(result.getString(6));
                    objeto.setIddestino(result.getString(7));
                    objeto.getBodega().setId(result.getString(8));
                    objeto.getCamion().setId(result.getInt(9));
                    objeto.getConductor().setId(result.getString(10));
                    objeto.setAtentido(result.getInt(11) > 0);
                    objeto.setEstado(result.getInt(12));
                    objeto.setIdservidor(result.getInt(13));
                    objeto.setFecha_inicio(Utils.C_StringToObjetTimeStamp(result.getString(14)));
                    objeto.setFecha_fin(Utils.C_StringToObjetTimeStamp(result.getString(15)));
                    objeto.getOperador().setId(result.getInt(16));
                    Lista.add(objeto);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Lista;
    }

    public static boolean PedidoestaRegistado(Context contexto, String id) {
        String[] args = new String[]{id};
        boolean existe = false;

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistro, args);
        result.moveToLast();

        if (result.getCount() != 0) {
            existe = true;
        }
        result.close();
        BDLocal.cerrar();

        return existe;
    }

    public static boolean borrarPedidoYDetalle(Context contexto, String idorden) {
        String[] args = new String[]{"" + idorden};
        long resultado = -1;

        TblPedido_Detalle.BorrarPedidoDetalle(contexto,idorden);

        resultado= BDUtil.DeleteRow(contexto,table_name,"id=?",args);
        return resultado > 0;
    }

    public static void borrarPedidoEnviadosAlServidor(Context contexto) {
        boolean estaEnviado;
        ArrayList<Lectura> DetalleLectura;
        ArrayList<Pedido> ordenTrabajadas = TblPedido.obtenerPedidosTrabajados(contexto);

        for (int itorden = 0; itorden < ordenTrabajadas.size(); itorden++) {
            estaEnviado = ordenTrabajadas.get(itorden).getIdservidor() > 0;

            DetalleLectura = TblLectura.obtenerRegistrosXOrden(contexto, ordenTrabajadas.get(itorden).getId());

            for (int itdetalle = 0; itdetalle < DetalleLectura.size(); itdetalle++) {
                estaEnviado &= DetalleLectura.get(itdetalle).getIdservidor() > 0;
            }

            if (estaEnviado) {
                TblPedido.borrarPedidoYDetalle(contexto,ordenTrabajadas.get(itorden).getId());
                TblLectura.borrarLecturaxOrden(contexto,ordenTrabajadas.get(itorden).getId());
            }
        }
    }

    public static ArrayList<Pedido> borrarPedidosNoTrabajados(Context contexto) {
        ArrayList<Pedido> Lista = new ArrayList<Pedido>();

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistrosNoUtilizados, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    //SELECT fecha_inicio,fecha_fin,idoperador FROM orden  WHERE id=?
                    Pedido objeto = new Pedido();
                    objeto.setId(result.getString(0));
                    objeto.setFecha(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setCliente(result.getString(2));
                    objeto.setUbicacion(result.getString(3));
                    objeto.setDireccion(result.getString(4));
                    objeto.setTipo(result.getString(5));
                    objeto.setMarchamo(result.getString(6));
                    objeto.setIddestino(result.getString(7));
                    objeto.getBodega().setId(result.getString(8));
                    objeto.getCamion().setId(result.getInt(9));
                    objeto.getConductor().setId(result.getString(10));
                    objeto.setAtentido(result.getInt(11) > 0);
                    objeto.setEstado(result.getInt(12));
                    objeto.setIdservidor(result.getInt(13));
                    objeto.setFecha_inicio(Utils.C_StringToObjetTimeStamp(result.getString(14)));
                    objeto.setFecha_fin(Utils.C_StringToObjetTimeStamp(result.getString(15)));
                    objeto.getOperador().setId(result.getInt(16));
                    Lista.add(objeto);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();

        //MANDAR A BORRAR DETALLE Y DESTINO
        for (int it = 0; it < Lista.size(); it++)
            borrarPedidoYDetalle(contexto,Lista.get(it).getId());

        return Lista;
    }

}
