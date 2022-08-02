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

public class TblLectura {
    public static final String table_name ="host_lectura";
    private static final String table_fields="id,fecha,fecha_proceso,codigo,peso,piezas,serie,barra,idcamion,idconductor,idorden,idservidor,idoperador";
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

    public static boolean guardar(Context contexto, Lectura objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();
        objeto.setId(objeto.getId() < 1 ? nuevoID(contexto) : objeto.getId());

        nuevoRegistro.put("id", objeto.getId());

        if (objeto.getFecha() == null)
            nuevoRegistro.putNull("fecha");
        else
            nuevoRegistro.put("fecha", Utils.C_DateToDBFORMAT(objeto.getFecha_proceso()));

        if (objeto.getFecha_proceso() == null)
            nuevoRegistro.putNull("fecha_proceso");
        else
            nuevoRegistro.put("fecha_proceso", Utils.C_TimeStampToDBFORMAT(objeto.getFecha_proceso()));

        nuevoRegistro.put("codigo", objeto.getCodigo());
        nuevoRegistro.put("peso", objeto.getPeso());
        nuevoRegistro.put("piezas", objeto.getPiezas());
        nuevoRegistro.put("serie", objeto.getSerie());
        nuevoRegistro.put("barra", objeto.getBarra());
        nuevoRegistro.put("idcamion", objeto.getIdcamion());
        nuevoRegistro.put("idconductor", objeto.getIdconductor());
        nuevoRegistro.put("idorden", objeto.getIdorden());
        nuevoRegistro.put("idservidor", objeto.getIdservidor());
        nuevoRegistro.put("idoperador", objeto.getIdoperador());

        resultado= BDUtil.InsertRow(contexto,table_name,null,nuevoRegistro);
        return resultado > 0;
    }

    public static boolean modificar(Context contexto, Lectura objeto) {
        long resultado = -1;
        String[] args = new String[]{"" + objeto.getId()};

        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("id", objeto.getId());

        if (objeto.getFecha() == null)
            nuevoRegistro.putNull("fecha");
        else
            nuevoRegistro.put("fecha", Utils.C_DateToDBFORMAT(objeto.getFecha_proceso()));

        if (objeto.getFecha_proceso() == null)
            nuevoRegistro.putNull("fecha_proceso");
        else
            nuevoRegistro.put("fecha_proceso", Utils.C_TimeStampToDBFORMAT(objeto.getFecha_proceso()));

        nuevoRegistro.put("codigo", objeto.getCodigo());
        nuevoRegistro.put("peso", objeto.getPeso());
        nuevoRegistro.put("piezas", objeto.getPiezas());
        nuevoRegistro.put("serie", objeto.getSerie());
        nuevoRegistro.put("barra", objeto.getBarra());
        nuevoRegistro.put("idcamion", objeto.getIdcamion());
        nuevoRegistro.put("idconductor", objeto.getIdconductor());
        nuevoRegistro.put("idorden", objeto.getIdorden());
        nuevoRegistro.put("idservidor", objeto.getIdservidor());
        nuevoRegistro.put("idperador", objeto.getIdoperador());

       resultado = BDUtil.UpdateRow(contexto,table_name,"id=?",args,nuevoRegistro);

        return resultado > 0;
    }

    public static ArrayList<Lectura> obtenerRegistros(Context contexto) {
        ArrayList<Lectura> Lista = new ArrayList<Lectura>();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {//id,fecha,codigo,peso,piezas,serie,barra,idcamion,iconductor,idorden,idservidor,idoperador
                    Lectura objeto = new Lectura();
                    objeto.setId(result.getInt(0));
                    objeto.setFecha(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_proceso(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setCodigo(result.getString(3));
                    objeto.setPeso(result.getDouble(4) );
                    objeto.setPiezas(result.getInt(5));
                    objeto.setSerie(result.getString(6));
                    objeto.setBarra(result.getString(7));
                    objeto.setIdcamion(result.getInt(8));
                    objeto.setIdconductor(result.getString(9));
                    objeto.setIdorden(result.getString(10));
                    objeto.setIdservidor(result.getInt(11));
                    objeto.setIdoperador(result.getInt(12));
                    Lista.add(objeto);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return Lista;
    }

    public static Lectura obtenerRegistroXId(Context contexto, String id) {
        String[] args = new String[]{id};
        Lectura objeto = new Lectura();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistroXId, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {//id,fecha,codigo,peso,piezas,serie,barra,idcamion,iconductor,idorden,idservidor,idoperador
                    objeto.setId(result.getInt(0));
                    objeto.setFecha(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_proceso(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setCodigo(result.getString(3));
                    objeto.setPeso(result.getDouble(4) );
                    objeto.setPiezas(result.getInt(5));
                    objeto.setSerie(result.getString(6));
                    objeto.setBarra(result.getString(7));
                    objeto.setIdcamion(result.getInt(8));
                    objeto.setIdconductor(result.getString(9));
                    objeto.setIdorden(result.getString(10));
                    objeto.setIdservidor(result.getInt(11));
                    objeto.setIdoperador(result.getInt(12));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return objeto;
    }

    public static Lectura obtenerRegistroXCodigoBarra(Context contexto, String barra) {
        String[] args = new String[]{barra};
        Lectura objeto = new Lectura();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistroXBarra, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {//id,fecha,codigo,peso,piezas,serie,barra,idcamion,iconductor,idorden,idservidor,idoperador
                    objeto.setId(result.getInt(0));
                    objeto.setFecha(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_proceso(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setCodigo(result.getString(3));
                    objeto.setPeso(result.getDouble(4) );
                    objeto.setPiezas(result.getInt(5));
                    objeto.setSerie(result.getString(6));
                    objeto.setBarra(result.getString(7));
                    objeto.setIdcamion(result.getInt(8));
                    objeto.setIdconductor(result.getString(9));
                    objeto.setIdorden(result.getString(10));
                    objeto.setIdservidor(result.getInt(11));
                    objeto.setIdoperador(result.getInt(12));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return objeto;
    }

    public static ArrayList<Lectura> obtenerRegistrosNOEnviados(Context contexto, int idorden) {
        String table_where =" WHERE _idservidor=0";
        String SQLQUERY = SQLObtenerRegistros+ table_where;
        ArrayList<Lectura> registros = new ArrayList<Lectura>();
        String[] args = null;

        if (idorden != 0 ) {
            SQLQUERY += " AND idorden=?";
            args = new String[]{"" + idorden};
        }

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLQUERY, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Lectura objeto = new Lectura();
                    objeto.setId(result.getInt(0));
                    objeto.setFecha(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_proceso(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setCodigo(result.getString(3));
                    objeto.setPeso(result.getDouble(4) );
                    objeto.setPiezas(result.getInt(5));
                    objeto.setSerie(result.getString(6));
                    objeto.setBarra(result.getString(7));
                    objeto.setIdcamion(result.getInt(8));
                    objeto.setIdconductor(result.getString(9));
                    objeto.setIdorden(result.getString(10));
                    objeto.setIdservidor(result.getInt(11));
                    objeto.setIdoperador(result.getInt(12));
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

    public static ArrayList<Lectura> obtenerRegistrosXOrden(Context contexto, String idorden) {
        ArrayList<Lectura> registros = new ArrayList<Lectura>();
        String[] args = new String[]{"" + idorden};

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros+" WHERE idorden=?", args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Lectura objeto = new Lectura();
                    objeto.setId(result.getInt(0));
                    objeto.setFecha(Utils.C_BDFormatToDate(result.getString(1)));
                    objeto.setFecha_proceso(Utils.C_BDFormatToDateTime(result.getString(2)));
                    objeto.setCodigo(result.getString(3));
                    objeto.setPeso(result.getDouble(4) );
                    objeto.setPiezas(result.getInt(5));
                    objeto.setSerie(result.getString(6));
                    objeto.setBarra(result.getString(7));
                    objeto.setIdcamion(result.getInt(8));
                    objeto.setIdconductor(result.getString(9));
                    objeto.setIdorden(result.getString(10));
                    objeto.setIdservidor(result.getInt(11));
                    objeto.setIdoperador(result.getInt(12));
                    registros.add(objeto);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();

        return registros;
    }



    public static boolean borrarLecturaxOrden(Context contexto, String idorden) {
        String[] args = new String[]{"" + idorden};
        long resultado = -1;
        resultado= BDUtil.DeleteRow(contexto,table_name,"idorden=?",args);
        return resultado > 0;
    }

    public static boolean borrarCaja(Context contexto, String barra,Boolean BORRAR_ALLSERVER) {
        String[] args = new String[]{barra};
        long resultado = -1;


        /*if(BORRAR_ALLSERVER){
            Lectura registro = TblLectura.obtenerRegistroXId(contexto, barra);
            if (registro.getId() != -1)
                if (registro.getIdservidor() != 0)
                    TblLecturaEliminada.guardar(contexto, new LecturaEliminada(registro.getIdservidor(), registro.getBarra(),registro.getIdorden()));
        }*/

        resultado= BDUtil.DeleteRow(contexto,table_name,"barra=?",args);
        return resultado > 0;
    }

    public static Integer obtenerNoFilas(Context contexto, String idorden) {
        ArrayList<Lectura> registros = new ArrayList<Lectura>();
        String[] args = new String[]{"" + idorden};
        Integer cantidad =0;

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros+" WHERE idorden=?", args);

        if (result.getCount() != 0) {
           cantidad = result.getCount();
        }
        result.close();
        BDLocal.cerrar();

        return cantidad;
    }

   /* public static Double obtenerPesoXCamion(Context contexto, Integer idCamion) {
        String[] args = new String[]{"" + idCamion};
        Boolean resultado = false;
        String TMP_SQL ="SELECT IFNULL(SUM(peso),0) AS SUMA FROM " + table_name +" WHERE idcamion=?";
        Double peso =0.0;

        BDLocal.abrir(contexto);
        Cursor result = BDLocal.BD_SQLITE.rawQuery(TMP_SQL, args);
        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    peso = result.getDouble(0);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return peso;
    }*/
}
