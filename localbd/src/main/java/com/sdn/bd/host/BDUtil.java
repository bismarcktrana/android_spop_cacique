package com.sdn.bd.host;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

public class BDUtil {
    private static final String LOG_TAG = BDUtil.class.getSimpleName();

    /**
     * Limpiar el contenido de una tabla.
     *
     * @param contexto pantallaActual
     * @param tabla tabla a limpiar
     * @param whereClause filtro en la consulta
     * @param whereArgs valores de la consulta
     */
    public static void EmptyTable(Context contexto,String tabla, String whereClause,String[] whereArgs ) {
        BDLocal.abrir(contexto);
        BDLocal.BD_SQLITE.delete(tabla,whereClause, whereArgs);
        BDLocal.cerrar();
    }

    /**
     * Guardar el registro a la tabla solicitada
     * @param contexto pantallaActual
     * @param table_name tabla destino
     * @param nullColumnHack    columna de
     * @param values valores a insertar en la fila creada.
     * @return
     */
    public static Long InsertRow(Context contexto, String table_name,String nullColumnHack, android.content.ContentValues values) {
        Long resultado;
        BDLocal.abrir(contexto);
        resultado = BDLocal.BD_SQLITE.insert(table_name, nullColumnHack, values);
        BDLocal.cerrar();
        return resultado;
    }

    public static long UpdateRow(Context contexto, String table_name,String whereClause,   String[] whereArgs, ContentValues values) {
        long resultado = -1;
        BDLocal.abrir(contexto);
        resultado = BDLocal.BD_SQLITE.update(table_name, values, whereClause,  whereArgs);
        BDLocal.cerrar();
        return resultado;
    }

    public static long DeleteRow(Context contexto, String table_name,String whereClause,   String[] whereArgs) {
        long resultado = -1;
        BDLocal.abrir(contexto);
        resultado = BDLocal.BD_SQLITE.delete(table_name, whereClause,  whereArgs);
        BDLocal.cerrar();
        return resultado;
    }

    public static void EmptyBD(Context contexto) {
        ArrayList<String> tablas = getTableNames(contexto);

        BDLocal.abrir(contexto);
        for(int i=0; i<tablas.size();i++){
            BDLocal.BD_SQLITE.delete(tablas.get(i),null, null);
        }
        BDLocal.cerrar();
    }

    public static String getBDLocation(Context contexto) {
       String path_ubicacion;
        BDLocal.abrir(contexto);
        path_ubicacion = BDLocal.getUbicacion();
        BDLocal.cerrar();
        return path_ubicacion;
    }

    public static ArrayList<ArrayList> executeQuery(Context contexto, String SQLQuery){
        ArrayList<ArrayList> Registros = new ArrayList<ArrayList>();
        ArrayList<String> valor = new ArrayList<String>();;
        String[] columnas;

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLQuery, null);
        columnas = result.getColumnNames();

        for(int i=0; i<columnas.length;i++){
            valor.add(columnas[i].toString());
        }
        Registros.add(valor);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    valor = new ArrayList<String>();
                    for(int i=0; i<result.getColumnCount();i++){
                        valor.add(result.getString(i));
                    }
                    Registros.add(valor);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Registros;
    }

    public static ArrayList<ArrayList> getData(Context contexto, String SQLQuery){
        ArrayList<ArrayList> Registros = new ArrayList<ArrayList>();
        ArrayList<String> Celdas = new ArrayList<String>();;
        Integer CantColumnas=0;

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLQuery, null);
        CantColumnas = result.getColumnCount();


        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Celdas = new ArrayList<String>();
                    for(int i=0; i<CantColumnas;i++){
                        Celdas.add(result.getString(i));
                    }
                    //Log.e(LOG_TAG, Celdas.toString());
                    Registros.add(Celdas);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Registros;
    }

    public static ArrayList<ArrayList> get_RowsFromTable(Context contexto, String tabla){
        ArrayList<ArrayList> Registros = new ArrayList<ArrayList>();
        ArrayList<String> valor = new ArrayList<String>();;
        String[] columnas;

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery("SELECT * FROM "+tabla, null);
        columnas = result.getColumnNames();

        for(int i=0; i<columnas.length;i++){
            valor.add(columnas[i].toString());
        }
        Registros.add(valor);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {

                    valor = new ArrayList<String>();
                    for(int i=0; i<result.getColumnCount();i++){
                        valor.add(result.getString(i));
                    }
                    Registros.add(valor);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Registros;
    }

    public static ArrayList<String> getTableNames(Context contexto) {
        ArrayList<String> columnas = new ArrayList<String>();

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery("SELECT tbl_name FROM sqlite_master WHERE type ='table' ORDER BY tbl_name", null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    columnas.add(result.getString(0));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return columnas;
    }

    public static ArrayList<String> getColumnNames(Context contexto, String SQLQuery){
        ArrayList<String> registros = new ArrayList<String>();
        try{
            BDLocal.abrir(contexto);
            Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLQuery, null);
            String[] columnas = result.getColumnNames();
            registros.addAll(Arrays.asList(columnas));

            for(int i=0; i<columnas.length;i++){
                Log.i(LOG_TAG, columnas[i].toString());
            }

             result.close();
            BDLocal.cerrar();
        }catch (RuntimeException e){
            Log.i(LOG_TAG, "getColumnName Error:"+e.getMessage());
        }

        return  registros;
    }
}
