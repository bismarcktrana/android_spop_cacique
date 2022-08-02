package com.sdn.bd.dao.softland;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.bd.objeto.produccion.Producto;
import com.sdn.bd.objeto.softland.Bodega;

import java.util.ArrayList;

public class TblBodega {
    private static final String table_name ="soft_bodega";
    private static final String table_fields="id,nombre";
    private static final String table_where = "id=?";

    private static final String SQLClearTable = "DELETE from "+ table_name;
    private static final String SQLObtenerRegistros = "SELECT "+table_fields+" FROM "+ table_name;
    private static final String SQLObtenerRegistro = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE "+table_where;

    public static void vaciarTabla(Context contexto) {
        BDUtil.EmptyTable(contexto,table_name,null,null);
    }

    public static boolean guardar(Context contexto, Bodega objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("id", objeto.getId());
        nuevoRegistro.put("nombre", objeto.getNombre());
        resultado = BDUtil.InsertRow(contexto,table_name,null,nuevoRegistro);

        return resultado > 0;
    }

    public static ArrayList<Bodega> obtenerRegistros(Context contexto) {
        ArrayList<Bodega> camiones = new ArrayList<Bodega>();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Bodega obj = new Bodega();
                    obj.setId(result.getString(0));
                    obj.setNombre(result.getString(1));
                    camiones.add(obj);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return camiones;
    }

    public static Bodega obtenerRegistro(Context contexto, String codigo) {
        Bodega obj = new Bodega();

        String[] args = new String[]{codigo};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistro, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    obj.setId(result.getString(0));
                    obj.setNombre(result.getString(1));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return obj;
    }
}
