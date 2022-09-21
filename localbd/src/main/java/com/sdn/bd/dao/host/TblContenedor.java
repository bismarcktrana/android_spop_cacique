package com.sdn.bd.dao.host;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.bd.objeto.host.Contenedor;
import com.sdn.bd.util.Utils;

import java.util.ArrayList;

public class TblContenedor {
    public static final String table_name ="host_contenedor";
    private static final String table_fields="id,fecha_creacion,fecha_inicio,fecha_fin,nombre,idoperador,idservidor";
    private static final String table_where = "id=?";

    private static final String SQLClearTable = "DELETE from "+ table_name;
    private static final String SQLObtenerRegistros = "SELECT "+table_fields+" FROM "+ table_name;
    private static final String SQLObtenerRegistroXId = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE "+table_where;
    private static final String SQLNuevoid = "Select (ifnull(max(id),0))+1 AS ID  from "+table_name;

    public static int nuevoID(Context contexto) {
        int idgenerado = 0;

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLNuevoid, null);
        if (result.moveToFirst()) {
            do {
                idgenerado = Integer.parseInt(result.getString(0));
            } while (result.moveToNext());
        }
        result.close();
        BDLocal.cerrar();
        return idgenerado;
    }

    public static void vaciarTabla(Context contexto) {
        BDUtil.EmptyTable(contexto,table_name,null,null);
    }

    public static boolean guardar(Context contexto, Contenedor objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();
        objeto.setId(objeto.getId() < 1 ? nuevoID(contexto) : objeto.getId());

        nuevoRegistro.put("id", objeto.getId());

        if (objeto.getFecha_creacion()== null)
            nuevoRegistro.putNull("fecha_creacion");
        else
            nuevoRegistro.put("fecha_creacion", Utils.C_DateToDBFORMAT(objeto.getFecha_creacion()));

        if (objeto.getFecha_inicio()== null)
            nuevoRegistro.putNull("fecha_inicio");
        else
            nuevoRegistro.put("fecha_inicio", Utils.C_DateToDBFORMAT(objeto.getFecha_inicio()));

        if (objeto.getFecha_fin()== null)
            nuevoRegistro.putNull("fecha_fin");
        else
            nuevoRegistro.put("fecha_fin", Utils.C_DateToDBFORMAT(objeto.getFecha_fin()));

        nuevoRegistro.put("nombre", objeto.getNombre());
        nuevoRegistro.put("idoperador", objeto.getIdoperador());
        nuevoRegistro.put("idservidor", objeto.getIdservidor());

        resultado= BDUtil.InsertRow(contexto,table_name,null,nuevoRegistro);
        return resultado > 0;
    }

    public static boolean modificar(Context contexto, Contenedor objeto) {
        long resultado = -1;
        String[] args = new String[]{"" + objeto.getId()};

        ContentValues nuevoRegistro = new ContentValues();

        if (objeto.getFecha_creacion()== null)
            nuevoRegistro.putNull("fecha_creacion");
        else
            nuevoRegistro.put("fecha_creacion", Utils.C_DateToDBFORMAT(objeto.getFecha_creacion()));

        if (objeto.getFecha_inicio()== null)
            nuevoRegistro.putNull("fecha_inicio");
        else
            nuevoRegistro.put("fecha_inicio", Utils.C_DateToDBFORMAT(objeto.getFecha_inicio()));

        if (objeto.getFecha_fin()== null)
            nuevoRegistro.putNull("fecha_fin");
        else
            nuevoRegistro.put("fecha_fin", Utils.C_DateToDBFORMAT(objeto.getFecha_fin()));

        nuevoRegistro.put("nombre", objeto.getNombre());
        nuevoRegistro.put("idservidor", objeto.getIdservidor());
        nuevoRegistro.put("idoperador", objeto.getIdoperador());

       resultado = BDUtil.UpdateRow(contexto,table_name,"id=?",args,nuevoRegistro);

        return resultado > 0;
    }

    public static ArrayList<Contenedor> obtenerRegistros(Context contexto) {
        ArrayList<Contenedor> Lista = new ArrayList<Contenedor>();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {//id,fecha_creacion,fecha_inicio,fecha_fin,nombre,idoperador,idservidor
                    Contenedor objeto = new Contenedor();
                    objeto.setId(result.getInt(0));
                    objeto.setFecha_creacion(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_inicio(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setFecha_fin(Utils.C_BDFormatToDateTime(result.getString(3)));
                    objeto.setNombre(result.getString(4));
                    objeto.setIdoperador(result.getInt(5));
                    objeto.setIdservidor(result.getInt(6));
                    Lista.add(objeto);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Lista;
    }

    public static Contenedor obtenerRegistroXId(Context contexto, String id) {
        String[] args = new String[]{id};
        Contenedor objeto = new Contenedor();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistroXId, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {//id,fecha_creacion,fecha_inicio,fecha_fin,nombre,idoperador,idservidor
                    objeto.setId(result.getInt(0));
                    objeto.setFecha_creacion(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_inicio(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setFecha_fin(Utils.C_BDFormatToDateTime(result.getString(3)));
                    objeto.setNombre(result.getString(4));
                    objeto.setIdoperador(result.getInt(5));
                    objeto.setIdservidor(result.getInt(6));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return objeto;
    }

    public static boolean borrarContenedorxID(Context contexto, Integer id) {
        String[] args = new String[]{"" + id};
        long resultado = -1;
        resultado= BDUtil.DeleteRow(contexto,table_name,"id=?",args);
        return resultado > 0;
    }

}
