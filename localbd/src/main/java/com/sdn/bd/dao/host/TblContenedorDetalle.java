package com.sdn.bd.dao.host;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.bd.objeto.host.Contenedor_Detalle;
import com.sdn.bd.objeto.host.Lectura;
import com.sdn.bd.util.Utils;

import java.util.ArrayList;

public class TblContenedorDetalle {
    public static final String table_name ="host_contenedor_detalle";
    private static final String table_fields="id,fecha_creacion,fecha_proceso,codigo,peso,piezas,serie,barra,tipo,idcontenedor,idoperador,idservidor";
    private static final String table_where = "id=?";
    private static final String table_where2 = "barra=?";

    private static final String SQLClearTable = "DELETE from "+ table_name;
    private static final String SQLObtenerRegistros = "SELECT "+table_fields+" FROM "+ table_name;
    private static final String SQLObtenerRegistroXId = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE "+table_where;
    private static final String SQLObtenerRegistroXBarra = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE "+table_where2;
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

    public static boolean guardar(Context contexto, Contenedor_Detalle objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();
        objeto.setId(objeto.getId() < 1 ? nuevoID(contexto) : objeto.getId());

        nuevoRegistro.put("id", objeto.getId());

        if (objeto.getFecha_creacion() == null)
            nuevoRegistro.putNull("fecha_creacion");
        else
            nuevoRegistro.put("fecha_creacion",Utils.C_TimeStampToDBFORMAT(objeto.getFecha_proceso()));

        if (objeto.getFecha_proceso() == null)
            nuevoRegistro.putNull("fecha_proceso");
        else
            nuevoRegistro.put("fecha_proceso", Utils.C_DateToDBFORMAT(objeto.getFecha_creacion()));

        nuevoRegistro.put("codigo", objeto.getCodigo());
        nuevoRegistro.put("peso", objeto.getPeso());
        nuevoRegistro.put("piezas", objeto.getPiezas());
        nuevoRegistro.put("serie", objeto.getSerie());
        nuevoRegistro.put("barra", objeto.getBarra());
        nuevoRegistro.put("tipo", objeto.getTipo());
        nuevoRegistro.put("idcontenedor", objeto.getIdcontenedor());
        nuevoRegistro.put("idoperador", objeto.getIdoperador());
        nuevoRegistro.put("idservidor", objeto.getIdservidor());

        resultado= BDUtil.InsertRow(contexto,table_name,null,nuevoRegistro);
        return resultado > 0;
    }

    public static boolean modificar(Context contexto, Contenedor_Detalle objeto) {
        long resultado = -1;
        String[] args = new String[]{"" + objeto.getId()};

        ContentValues nuevoRegistro = new ContentValues();

        if (objeto.getFecha_creacion() == null)
            nuevoRegistro.putNull("fecha_creacion");
        else
            nuevoRegistro.put("fecha_creacion",Utils.C_TimeStampToDBFORMAT(objeto.getFecha_proceso()));

        if (objeto.getFecha_proceso() == null)
            nuevoRegistro.putNull("fecha_proceso");
        else
            nuevoRegistro.put("fecha_proceso", Utils.C_DateToDBFORMAT(objeto.getFecha_creacion()));

        nuevoRegistro.put("codigo", objeto.getCodigo());
        nuevoRegistro.put("peso", objeto.getPeso());
        nuevoRegistro.put("piezas", objeto.getPiezas());
        nuevoRegistro.put("serie", objeto.getSerie());
        nuevoRegistro.put("barra", objeto.getBarra());
        nuevoRegistro.put("tipo", objeto.getTipo());
        nuevoRegistro.put("idcontenedor", objeto.getIdcontenedor());
        nuevoRegistro.put("idperador", objeto.getIdoperador());
        nuevoRegistro.put("idservidor", objeto.getIdservidor());

       resultado = BDUtil.UpdateRow(contexto,table_name,"id=?",args,nuevoRegistro);

        return resultado > 0;
    }

    public static ArrayList<Contenedor_Detalle> obtenerRegistros(Context contexto) {
        ArrayList<Contenedor_Detalle> Lista = new ArrayList<Contenedor_Detalle>();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {//id,fecha_creacion,fecha_proceso,codigo,peso,piezas,serie,barra,tipo,idcontenedor,idoperador,idservidor
                    Contenedor_Detalle objeto = new Contenedor_Detalle();
                    objeto.setId(result.getInt(0));
                    objeto.setFecha_creacion(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_proceso(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setCodigo(result.getString(3));
                    objeto.setPeso(result.getDouble(4) );
                    objeto.setPiezas(result.getInt(5));
                    objeto.setSerie(result.getString(6));
                    objeto.setBarra(result.getString(7));
                    objeto.setTipo(result.getInt(8));
                    objeto.setIdcontenedor(result.getString(9));
                    objeto.setIdservidor(result.getInt(10));
                    objeto.setIdoperador(result.getInt(11));
                    Lista.add(objeto);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Lista;
    }

    public static Contenedor_Detalle obtenerRegistroXId(Context contexto, String id) {
        String[] args = new String[]{id};
        Contenedor_Detalle objeto = new Contenedor_Detalle();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistroXId, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {//id,fecha_creacion,fecha_proceso,codigo,peso,piezas,serie,barra,tipo,idcontenedor,idoperador,idservidor
                    objeto.setId(result.getInt(0));
                    objeto.setFecha_creacion(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_proceso(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setCodigo(result.getString(3));
                    objeto.setPeso(result.getDouble(4) );
                    objeto.setPiezas(result.getInt(5));
                    objeto.setSerie(result.getString(6));
                    objeto.setBarra(result.getString(7));
                    objeto.setTipo(result.getInt(8));
                    objeto.setIdcontenedor(result.getString(9));
                    objeto.setIdservidor(result.getInt(10));
                    objeto.setIdoperador(result.getInt(11));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return objeto;
    }

    public static Contenedor_Detalle obtenerRegistroXCodigoBarra(Context contexto, String barra) {
        String[] args = new String[]{barra};
        Contenedor_Detalle objeto = new Contenedor_Detalle();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistroXBarra, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {//id,fecha_creacion,fecha_proceso,codigo,peso,piezas,serie,barra,tipo,idcontenedor,idoperador,idservidor
                    objeto.setId(result.getInt(0));
                    objeto.setFecha_creacion(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_proceso(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setCodigo(result.getString(3));
                    objeto.setPeso(result.getDouble(4) );
                    objeto.setPiezas(result.getInt(5));
                    objeto.setSerie(result.getString(6));
                    objeto.setBarra(result.getString(7));
                    objeto.setTipo(result.getInt(8));
                    objeto.setIdcontenedor(result.getString(9));
                    objeto.setIdservidor(result.getInt(10));
                    objeto.setIdoperador(result.getInt(11));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return objeto;
    }

    public static ArrayList<Contenedor_Detalle> obtenerRegistrosNOEnviados(Context contexto, int idcontenedor) {
        String table_where =" WHERE idservidor=0";
        String SQLQUERY = SQLObtenerRegistros+ table_where;
        ArrayList<Contenedor_Detalle> registros = new ArrayList<Contenedor_Detalle>();
        String[] args = null;

        if (idcontenedor != 0 ) {
            SQLQUERY += " AND idcontenedor=?";
            args = new String[]{"" + idcontenedor};
        }

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLQUERY, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Contenedor_Detalle objeto = new Contenedor_Detalle();
                    objeto.setId(result.getInt(0));
                    objeto.setFecha_creacion(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_proceso(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setCodigo(result.getString(3));
                    objeto.setPeso(result.getDouble(4) );
                    objeto.setPiezas(result.getInt(5));
                    objeto.setSerie(result.getString(6));
                    objeto.setBarra(result.getString(7));
                    objeto.setTipo(result.getInt(8));
                    objeto.setIdcontenedor(result.getString(9));
                    objeto.setIdservidor(result.getInt(10));
                    objeto.setIdoperador(result.getInt(11));
                    registros.add(objeto);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();

        return registros;
    }

    public static Boolean cajaEstaRegistrada(Context contexto, String barra) {
        String[] args = new String[]{"" + barra};
        Boolean resultado = false;

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros+" WHERE barra=?", args);

        if (result.getCount() != 0) {
            resultado = true;
        }
        result.close();
        BDLocal.cerrar();
        return resultado;
    }

    public static ArrayList<Contenedor_Detalle> obtenerRegistrosXContenedor(Context contexto, Integer idcontenedor) {
        ArrayList<Contenedor_Detalle> registros = new ArrayList<Contenedor_Detalle>();
        String[] args = new String[]{"" + idcontenedor};

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros+" WHERE idcontenedor=?", args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Contenedor_Detalle objeto = new Contenedor_Detalle();
                    objeto.setId(result.getInt(0));
                    objeto.setFecha_creacion(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_proceso(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setCodigo(result.getString(3));
                    objeto.setPeso(result.getDouble(4) );
                    objeto.setPiezas(result.getInt(5));
                    objeto.setSerie(result.getString(6));
                    objeto.setBarra(result.getString(7));
                    objeto.setTipo(result.getInt(8));
                    objeto.setIdcontenedor(result.getString(9));
                    objeto.setIdservidor(result.getInt(10));
                    objeto.setIdoperador(result.getInt(11));
                    registros.add(objeto);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();

        return registros;
    }

    public static boolean borrarLecturaxContenedor(Context contexto, Integer idcontenedor) {
        String[] args = new String[]{ ""+idcontenedor};
        long resultado = -1;
        resultado= BDUtil.DeleteRow(contexto,table_name,"idcontenedor=?",args);
        return resultado > 0;
    }

    public static boolean borrarCaja(Context contexto, String barra) {
        String[] args = new String[]{barra};
        long resultado = -1;

        resultado= BDUtil.DeleteRow(contexto,table_name,"barra=?",args);
        return resultado > 0;
    }

    public static Integer obtenerNoFilas(Context contexto, Integer idcontenedor) {
        ArrayList<Lectura> registros = new ArrayList<Lectura>();
        String[] args = new String[]{"" + idcontenedor};
        Integer cantidad =0;

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros+" WHERE idcontenedor=?", args);

        if (result.getCount() != 0) {
           cantidad = result.getCount();
        }
        result.close();
        BDLocal.cerrar();

        return cantidad;
    }

}
