package com.sdn.bd.dao.softland;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.bd.objeto.softland.Pedido;
import com.sdn.bd.objeto.softland.Pedido_Detalle;

import java.util.ArrayList;

public class TblPedido_Detalle {
    private static final String table_name ="soft_pedido_detalle";
    private static final String table_fields="id,idpedido,idproducto,cantidad";
    private static final String table_where = "id=? AND idpedido=?";

    private static final String SQLClearTable = "DELETE from "+ table_name;
    private static final String SQLObtenerRegistros = "SELECT "+table_fields+" FROM "+ table_name;
    private static final String SQLObtenerRegistroxId = "SELECT "+table_fields+" FROM "+ table_name +" WHERE "+table_where;
    private static final String SQLObtenerRegistrosxPedido = "SELECT "+table_fields+" FROM "+ table_name +" WHERE idpedido=?";

    public static void vaciarTabla(Context contexto) {
        BDUtil.EmptyTable(contexto,table_name,null,null);
    }

    public static boolean guardar(Context contexto, Pedido_Detalle objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("id", objeto.getId());
        nuevoRegistro.put("idpedido", objeto.getIdpedido());
        nuevoRegistro.put("idproducto", objeto.getIdarticulo());
        nuevoRegistro.put("cantidad", objeto.getCantidad());

        resultado = BDUtil.InsertRow(contexto,table_name,null,nuevoRegistro);

        return resultado > 0;
    }

    public static Pedido_Detalle obtenerRegistro(Context contexto, Integer id,String idpedido) {
        Pedido_Detalle obj = new Pedido_Detalle();

        String[] args = new String[]{""+id, idpedido};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistroxId, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    obj.setId(result.getInt(0));
                    obj.setIdpedido(result.getString(1));
                    obj.setIdarticulo(result.getString(2));
                    obj.setCantidad(result.getFloat(3));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return obj;
    }

    public static ArrayList<Pedido_Detalle> obtenerRegistros(Context contexto) {
        ArrayList<Pedido_Detalle> Lista = new ArrayList<Pedido_Detalle>();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Pedido_Detalle obj = new Pedido_Detalle();
                    obj.setId(result.getInt(0));
                    obj.setIdpedido(result.getString(1));
                    obj.setIdarticulo(result.getString(2));
                    obj.setCantidad(result.getFloat(3));
                    Lista.add(obj);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Lista;
    }

    public static ArrayList<Pedido_Detalle> obtenerRegistrosxPedido(Context contexto,String idpedido) {
        ArrayList<Pedido_Detalle> Lista = new ArrayList<Pedido_Detalle>();

        String[] args = new String[]{ idpedido};

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistrosxPedido, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Pedido_Detalle obj = new Pedido_Detalle();
                    obj.setId(result.getInt(0));
                    obj.setIdpedido(result.getString(1));
                    obj.setIdarticulo(result.getString(2));
                    obj.setCantidad(result.getFloat(3));
                    Lista.add(obj);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Lista;
    }

    public static boolean BorrarPedidoDetalle(Context contexto, String idorden) {
        String[] args = new String[]{""+idorden};
        Integer contR = 0;

        BDLocal.abrir(contexto);
        contR = BDLocal.BD_SQLITE.delete(table_name, "idpedido=?", args);
        BDLocal.cerrar();

        return contR > 0;
    }

    /**
     * Verifica si en el detalle de la factura, el prodcuto con codigo_softland a sido solicitado
     *
     * @param contexto
     * @param idpedido
     * @param codigo_softland
     * @return Object[]
     *
     * Object[0]  = codigo_softland
     * Object[1]  =
     */
    public static boolean esProductoValido(Context contexto, String idpedido, String codigo_softland) {
        boolean autorizado=false;
        String[] args = new String[]{"" + idpedido, "" + codigo_softland};
        String TEMP_QUERY = "SELECT IDPRODUCTO , SUM(cantidad) AS PESOTOTAL FROM "+table_name+" WHERE idpedido=? AND idproducto=? GROUP BY idproducto";

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(TEMP_QUERY, args);

        autorizado =  result.getCount()>0;

        result.close();
        BDLocal.cerrar();
        return autorizado;
    }

    public static Object[] esProductoValidocp(Context contexto, String idpedido, String codigo_softland) {
        Object[] registro = null;
        String[] args = new String[]{"" + idpedido, "" + codigo_softland};
        String TEMP_QUERY = "SELECT IDPRODUCTO , SUM(cantidad) AS PESOTOTAL FROM "+table_name+" WHERE idpedido=? AND idproducto=? GROUP BY idproducto";

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(TEMP_QUERY, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                registro = new Object[3];
                do {
                    registro[0] = result.getString(0);
                    registro[1] = result.getInt(1);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return registro;
    }
}
