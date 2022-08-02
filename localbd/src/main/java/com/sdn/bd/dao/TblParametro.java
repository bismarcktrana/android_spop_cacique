package com.sdn.bd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.bd.objeto.host.Parametro;
import com.sdn.bd.util.Utils;

import java.util.Vector;

public class TblParametro {

    private static final String table_name ="host_parametro";
    private static final String table_fields="campo, valor";
    private static final String table_where = "campo=?";

    private static final String SQLClearTable = "DELETE from "+ table_name;
    private static final String SQLObtenerRegistros = "SELECT "+table_fields+" FROM "+ table_name;
    private static final String SQLObtenerRegistro = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE "+table_where;

    public static void vaciarTabla(Context contexto) {
        BDUtil.EmptyTable(contexto,table_name,null,null);
    }

    public static boolean guardar(Context contexto, Parametro objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("campo", objeto.getCampo());
        nuevoRegistro.put("valor", objeto.getClave());

        if(!existe(contexto,objeto.getCampo())){
            System.out.println("Creando parametro"+objeto.toString());
            resultado= BDUtil.InsertRow(contexto,table_name,null,nuevoRegistro);
        }else{

            resultado= modificar(contexto,objeto.getCampo(),objeto.getClave())?1:0 ;
            System.out.println(resultado+"Modificando parametro"+objeto.toString());
        }

        return resultado > 0;
    }

    public static boolean modificar(Context contexto, String campo, String valor) {
        long resultado = -1;
        String[] args = new String[]{campo};

        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("valor", valor);

        resultado= BDUtil.UpdateRow(contexto,table_name,table_where,args,nuevoRegistro);
        return resultado > 0;
    }

    public static Vector<Parametro> obtenerRegistros(Context contexto) {
        Vector<Parametro> Registros = new Vector<Parametro>();

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);
        result.moveToLast();

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Parametro objeto = new Parametro();
                    objeto.setCampo(result.getString(0));
                    objeto.setClave(result.getString(1));

                    Registros.addElement(objeto);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Registros;
    }

    public static String getClave(Context contexto, String campo) {
        String[] args = new String[]{campo};
        String Clave = "";
        Parametro parametro = new Parametro();


        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistro, args);
        result.moveToLast();

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Parametro objeto = new Parametro();
                    objeto.setCampo(result.getString(0));
                    objeto.setClave(result.getString(1));

                    Clave =objeto.getClave();
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Clave;
    }

    public static boolean existe(Context contexto, String campo) {
        String[] args = new String[]{campo};

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

}
