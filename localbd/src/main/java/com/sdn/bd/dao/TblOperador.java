package com.sdn.bd.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.bd.objeto.host.Operador;

import java.util.Vector;

public class TblOperador {

    private static final String table_name ="host_operador";
    private static final String table_fields="id,usuario,clave,nombre,tipo";
    private static final String table_where = "id=?";

    private static final String SQLClearTable = "DELETE from "+ table_name;
    private static final String SQLObtenerRegistros = "SELECT "+table_fields+" FROM "+ table_name;
    private static final String SQLObtenerRegistro = "SELECT "+table_fields+" FROM "+ table_name +"  WHERE "+table_where;
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

    public static boolean guardar(Context contexto, Operador objeto) {
        long resultado = -1;
        ContentValues nuevoRegistro = new ContentValues();
        objeto.setId(objeto.getId() < 1 ? nuevoID(contexto) : objeto.getId());

        nuevoRegistro.put("id", objeto.getId());
        nuevoRegistro.put("usuario", objeto.getUsuario());
        nuevoRegistro.put("clave", objeto.getClave());
        nuevoRegistro.put("nombre", objeto.getNombre());
        nuevoRegistro.put("tipo", objeto.getTipo());

        resultado= BDUtil.InsertRow(contexto,table_name,null,nuevoRegistro);
        return resultado > 0;
    }

    public static boolean modificar(Context contexto, Operador operador) {
        long resultado = -1;
        String[] args = new String[]{"" + operador.getId()};

        ContentValues nuevoRegistro = new ContentValues();
        nuevoRegistro.put("usuario", operador.getUsuario());
        nuevoRegistro.put("clave", operador.getClave());
        nuevoRegistro.put("nombre", operador.getNombre());
        nuevoRegistro.put("tipo", operador.getTipo());

        resultado = BDUtil.UpdateRow(contexto,table_name,"id=?",args,nuevoRegistro);

        return resultado > 0;
    }

    public static Vector<Operador> obtenerRegistros(Context contexto) {
        Vector<Operador> operadores = new Vector<Operador>();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros, null);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Operador obj = new Operador();
                    obj.setId(Integer.parseInt(result.getString(0)));
                    obj.setUsuario(result.getString(1));
                    obj.setClave(result.getString(2));
                    obj.setNombre(result.getString(3));
                    obj.setTipo(Integer.parseInt(result.getString(4)));
                    operadores.add(obj);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return operadores;
    }

   /* public static Vector<Operador> obtenerRegistrosNoAdministradores(Context contexto) {
        Vector<Operador> operadores = new Vector<Operador>();

        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros+" WHERE tipo!=1", null);
        System.out.println(SQLObtenerRegistros+" WHERE tipo!=1");

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Operador obj = new Operador();
                    obj.setId(Integer.parseInt(result.getString(0)));
                    obj.setUsuario(result.getString(1));
                    obj.setClave(result.getString(2));
                    obj.setNombre(result.getString(3));
                    obj.setTipo(Integer.parseInt(result.getString(4)));
                    System.out.println("Operador"+obj.toString());
                    operadores.add(obj);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return operadores;
    }*/

    public static Vector<Operador> obtenerRegistrosXTipoOperador(Context contexto, int idtipooperador) {
        Vector<Operador> operadores = new Vector<Operador>();

        String[] args = new String[]{"" + idtipooperador};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros+" WHERE tipo=?", args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    Operador obj = new Operador();
                    obj.setId(Integer.parseInt(result.getString(0)));
                    obj.setUsuario(result.getString(1));
                    obj.setClave(result.getString(2));
                    obj.setNombre(result.getString(3));
                    obj.setTipo(Integer.parseInt(result.getString(4)));
                    operadores.add(obj);
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return operadores;
    }

    public static Operador obtenerRegistroXID(Context contexto, int idusuario) {
        Operador operador = new Operador();

        String[] args = new String[]{"" + idusuario};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistro, args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    operador.setId(Integer.parseInt(result.getString(0)));
                    operador.setUsuario(result.getString(1));
                    operador.setClave(result.getString(2));
                    operador.setNombre(result.getString(3));
                    operador.setTipo(Integer.parseInt(result.getString(4)));

                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return operador;
    }

    public static Operador obtenerRegistroXUsuario(Context contexto, String usuario) {
        Operador operador = new Operador();

        String[] args = new String[]{usuario};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros+"  WHERE usuario=?", args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    operador.setId(Integer.parseInt(result.getString(0)));
                    operador.setUsuario(result.getString(1));
                    operador.setClave(result.getString(2));
                    operador.setNombre(result.getString(3));
                    operador.setTipo(Integer.parseInt(result.getString(4)));

                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return operador;
    }

    public static Operador obtenerRegistroXUsuarioyClave(Context contexto, String usuario, String clave) {
        Operador operador = new Operador();

        String[] args = new String[]{usuario, clave};
        BDLocal.abrir(contexto);

        Cursor result = BDLocal.BD_SQLITE.rawQuery(SQLObtenerRegistros+" where usuario=? AND clave=?", args);

        if (result.getCount() != 0) {
            if (result.moveToFirst()) {
                do {
                    operador.setId(Integer.parseInt(result.getString(0)));
                    operador.setUsuario(result.getString(1));
                    operador.setClave(result.getString(2));
                    operador.setNombre(result.getString(3));
                    operador.setTipo(Integer.parseInt(result.getString(4)));
                } while (result.moveToNext());
            }
        }
        result.close();
        BDLocal.cerrar();
        return operador;
    }

}
