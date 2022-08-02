package com.sdn.bd.dao.produccion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.bd.objeto.produccion.Producto;

import java.util.ArrayList;

public class TblProducto
{
    private static final String table_name ="prod_producto";
    private static final String table_fields="id,codigo,codigo_softland,nombre,tipo,unidad";
    private static final String table_where = "codigo=?";
    private static final String table_where2 = "codigo_softland=?";

    private static final String SQLClearTable = "DELETE from "+ table_name;
    private static final String SQLObtenerRegistros = "SELECT "+table_fields+" FROM "+ table_name;
    private static final String SQLObtenerRegistro = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE "+table_where;
    private static final String SQLObtenerRegistroSoftLand = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE "+table_where2;

    public static void vaciarTabla(Context contexto) {
        BDLocal.abrir(contexto);
        BDLocal.BD_SQLITE.execSQL(SQLClearTable);
        BDLocal.cerrar();
    }

    public static boolean guardar(Context contexto, Producto objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("id", objeto.getId());
        nuevoRegistro.put("codigo", objeto.getCodigo());
        nuevoRegistro.put("nombre", objeto.getNombre());
        nuevoRegistro.put("tipo", objeto.getTipo());
        nuevoRegistro.put("codigo_softland", objeto.getCodigo_softland());
        nuevoRegistro.put("unidad", objeto.getUnidad());


        resultado= BDUtil.InsertRow(contexto,table_name,null,nuevoRegistro);

        return resultado > 0;
    }

    public static ArrayList<Producto> obtenerRegistros(Context contexto) {
        ArrayList<Producto> camiones = new ArrayList<Producto>();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Producto obj = new Producto();
                    obj.setId(result.getInt(0));
                    obj.setCodigo(result.getString(1));
                    obj.setCodigo_softland(result.getString(2));
                    obj.setNombre(result.getString(3));
                    obj.setTipo(result.getString(4));
                    obj.setUnidad(result.getInt(5));
                    camiones.add(obj);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return camiones;
    }

    public static Producto obtenerRegistroxCodigo(Context contexto, String codigo) {
        Producto obj = null;

        String[] args = new String[]{codigo};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistro, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    obj = new Producto();
                    obj.setId(result.getInt(0));
                    obj.setCodigo(result.getString(1));
                    obj.setCodigo_softland(result.getString(2));
                    obj.setNombre(result.getString(3));
                    obj.setTipo(result.getString(4));
                    obj.setUnidad(result.getInt(5));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return obj;
    }

    public static Producto obtenerRegistroxCodigoSoftland(Context contexto, String codigo_softland) {
        Producto obj = null;

        String[] args = new String[]{codigo_softland};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistroSoftLand, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    obj = new Producto();
                    obj.setId(result.getInt(0));
                    obj.setCodigo(result.getString(1));
                    obj.setCodigo_softland(result.getString(2));
                    obj.setNombre(result.getString(3));
                    obj.setTipo(result.getString(4));
                    obj.setUnidad(result.getInt(5));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return obj;
    }
}
