package com.sdn.bd.dao.produccion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.bd.objeto.produccion.Camion;

import java.util.Vector;

public class TblCamion {

    private static final String table_name ="prod_camion";
    private static final String table_fields="id,noplaca,min,max";
    private static final String table_where = "id=?";
    private static final String table_order = " ORDER BY noplaca ASC";

    private static final String SQLClearTable = "DELETE from "+ table_name;
    private static final String SQLObtenerRegistros = "SELECT "+table_fields+" FROM "+ table_name+table_order;
    private static final String SQLObtenerRegistro = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE "+table_where+table_order;
    private static final String SQLObtenerRegistroxCodigo = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE noplaca=?"+table_order;

    public static void vaciarTabla(Context contexto) {
        BDLocal.abrir(contexto);
        BDLocal.BD_SQLITE.execSQL(SQLClearTable);
        BDLocal.cerrar();
    }

    public static boolean guardar(Context contexto, Camion objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("id", objeto.getId());
        nuevoRegistro.put("noplaca", objeto.getNoplaca());
        nuevoRegistro.put("min", objeto.getMin());
        nuevoRegistro.put("max", objeto.getMax());

        resultado=BDUtil.InsertRow(contexto,table_name,null,nuevoRegistro);

        return resultado > 0;
    }

    public static Vector<Camion> obtenerRegistros(Context contexto) {
        Vector<Camion> camiones = new Vector<Camion>();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Camion obj = new Camion();
                    obj.setId(result.getInt(0));
                    obj.setNoplaca(result.getString(1));
                    obj.setMin(result.getInt(2));
                    obj.setMax(result.getInt(3));
                    camiones.add(obj);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return camiones;
    }

    public static Camion obtenerRegistro(Context contexto, Integer id) {
        Camion camion = new Camion();

        String[] args = new String[]{""+id};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistro, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    camion.setId(result.getInt(0));
                    camion.setNoplaca(result.getString(1));
                    camion.setMin(result.getInt(2));
                    camion.setMax(result.getInt(3));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return camion;
    }

    public static Camion obtenerRegistroxCodigo(Context contexto, String codigo) {
        Camion camion = new Camion();

        String[] args = new String[]{codigo};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistroxCodigo, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    camion.setId(result.getInt(0));
                    camion.setNoplaca(result.getString(1));
                    camion.setMin(result.getInt(2));
                    camion.setMax(result.getInt(3));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return camion;
    }
}
