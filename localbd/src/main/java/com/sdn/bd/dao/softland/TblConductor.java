package com.sdn.bd.dao.softland;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.bd.objeto.softland.Conductor;

import java.util.ArrayList;

public class TblConductor {
    private static final String table_name ="soft_conductor";
    private static final String table_fields="id,codigo,nombre";
    private static final String table_where = "id=?";
    private static final String table_order = " ORDER BY nombre DESC";

    private static final String SQLClearTable = "DELETE from "+ table_name;
    private static final String SQLObtenerRegistros = "SELECT "+table_fields+" FROM "+ table_name+table_order;
    private static final String SQLObtenerRegistro = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE "+table_where+table_order;
    private static final String SQLObtenerRegistroXCodigo = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE codigo=?"+table_order;

    public static void vaciarTabla(Context contexto) {
        BDUtil.EmptyTable(contexto,table_name,null,null);
    }

    public static boolean guardar(Context contexto, Conductor objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("id", objeto.getId());
        nuevoRegistro.put("codigo", objeto.getCodigo());
        nuevoRegistro.put("nombre", objeto.getNombre());

        resultado= BDUtil.InsertRow(contexto,table_name,null,nuevoRegistro);

        return resultado > 0;
    }

    public static ArrayList<Conductor> obtenerRegistros(Context contexto) {
        ArrayList<Conductor> camiones = new ArrayList<Conductor>();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Conductor obj = new Conductor();
                    obj.setId(result.getString(0));
                    obj.setCodigo(result.getString(1));
                    obj.setNombre(result.getString(2));
                    camiones.add(obj);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return camiones;
    }

    public static Conductor obtenerRegistro(Context contexto, String id) {
        Conductor obj = new Conductor();

        String[] args = new String[]{id};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistro, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    obj.setId(result.getString(0));
                    obj.setCodigo(result.getString(1));
                    obj.setNombre(result.getString(2));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return obj;
    }

    public static Conductor obtenerRegistroxCodigo(Context contexto, String codigo) {
        Conductor obj = new Conductor();

        String[] args = new String[]{codigo};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistroXCodigo, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    obj.setId(result.getString(0));
                    obj.setCodigo(result.getString(1));
                    obj.setNombre(result.getString(2));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return obj;
    }
}
