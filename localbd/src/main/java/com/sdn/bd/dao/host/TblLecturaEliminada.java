package com.sdn.bd.dao.host;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.bd.objeto.host.Lectura;
import com.sdn.bd.objeto.host.LecturaEliminada;
import com.sdn.bd.util.Utils;

import java.util.ArrayList;

public class TblLecturaEliminada {
    private static final String table_name ="host_lectura_eliminada";
    private static final String table_fields="idservidor,barra,idorden";
    //private static final String table_where = "id=?";

    private static final String SQLClearTable = "DELETE from "+ table_name;
    private static final String SQLObtenerRegistros = "SELECT "+table_fields+" FROM "+ table_name;
    //private static final String SQLObtenerRegistro = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE "+table_where;
    private static final String SQLNuevoid = "Select (ifnull(max(id),0))+1 AS ID  from "+table_name;

    public static void vaciarTabla(Context contexto) {
        BDUtil.EmptyTable(contexto,table_name,null,null);
    }

    public static boolean guardar(Context contexto, LecturaEliminada objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();

        nuevoRegistro.put("idservidor", objeto.getIdservidor());
        nuevoRegistro.put("barra", objeto.getBarra());
        nuevoRegistro.put("idorden", objeto.getIdorden());

        resultado= BDUtil.InsertRow(contexto,table_name,null,nuevoRegistro);
        return resultado > 0;
    }

    public static boolean borrarXbarra(Context contexto, String barra) {
        String[] args = new String[]{barra};
        long resultado = -1;

        resultado= BDUtil.DeleteRow(contexto,table_name,"barra=?",args);
        return resultado > 0;
    }

    public static boolean borrarXidservidor(Context contexto, int idservidor) {
        String[] args = new String[]{""+idservidor};
        long resultado = -1;

        resultado= BDUtil.DeleteRow(contexto,table_name,"idservidor=?",args);
        return resultado > 0;
    }

    public static boolean borrarXidOrden(Context contexto, int idorden) {
        String[] args = new String[]{""+idorden};
        long resultado = -1;

        resultado= BDUtil.DeleteRow(contexto,table_name,"idorden=?",args);
        return resultado > 0;
    }

    public static Boolean existenDatos(Context contexto) {
        Boolean resultado = false;

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            resultado = true;
        }

        result.close();
        BDLocal.cerrar();
        return resultado;
    }

    public static Boolean existenDatosDeLaOrden(Context contexto, String idorden) {
        String[] args = new String[]{""+idorden};
        Boolean resultado = false;

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros+" WHERE idorden=?", args);
        if (result.getCount() != 0) {
            resultado = true;
        }
        result.close();
        BDLocal.cerrar();
        return resultado;
    }

    public static ArrayList<LecturaEliminada> obtenerRegistros(Context contexto) {
        ArrayList<LecturaEliminada> Lista = new ArrayList<LecturaEliminada>();

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    LecturaEliminada obj = new LecturaEliminada();
                    obj.setIdservidor(Integer.parseInt(result.getString(0)));
                    obj.setBarra(result.getString(1));
                    obj.setIdorden(result.getString(2));
                    Lista.add(obj);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();

        return Lista;
    }

    public static ArrayList<LecturaEliminada> obtenerRegistrosXOrden(Context contexto,int idorden) {
        ArrayList<LecturaEliminada> Lista = new ArrayList<LecturaEliminada>();
        String[] args = new String[]{""+idorden};

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros + "  WHERE idorden=? ", args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    LecturaEliminada obj = new LecturaEliminada();
                    obj.setIdservidor(Integer.parseInt(result.getString(0)));
                    obj.setBarra(result.getString(1));
                    obj.setIdorden(result.getString(2));
                    Lista.add(obj);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();

        return Lista;
    }


}
