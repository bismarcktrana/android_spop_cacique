package com.sdn.cacique.bdremote;

import android.util.Log;

import com.sdn.bd.dao.host.TblOperador;
import com.sdn.bd.dao.host.TblParametro;
import com.sdn.bd.dao.produccion.TblCamion;
import com.sdn.bd.dao.produccion.TblProducto;
import com.sdn.bd.dao.softland.TblBodega;
import com.sdn.bd.dao.softland.TblConductor;
import com.sdn.bd.dao.softland.TblPedido;
import com.sdn.bd.objeto.host.Operador;
import com.sdn.bd.objeto.produccion.Camion;
import com.sdn.bd.objeto.produccion.Producto;
import com.sdn.bd.objeto.softland.Bodega;
import com.sdn.bd.objeto.softland.Conductor;
import com.sdn.bd.objeto.softland.Pedido;
import com.sdn.bd.objeto.softland.Pedido_Detalle;
import com.sdn.bd.secutidad.MD5;
import com.sdn.cacique.spop.FrmSincronizar;
import com.sdn.cacique.spop.R;
import com.sdn.cacique.util.ConfApp;
import com.sdn.cacique.util.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class BDOperacion_Update extends SGBD_Interface {
    public FrmSincronizar referencia;
    public String Tipo;
    boolean IS_SERVER_ONLINE;

    public BDOperacion_Update() {
    }

    public boolean GetStatusConecctionSGBD_ERP() {
        Boolean respuesta = false;
        try {

            V_OBJECTCONECTION = V_SPOOL_ERP.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_OBJECTCONECTION.close();
                respuesta = true;
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " Metodo GetStatusConecction - SQLException" + e.getMessage());
        }
        return respuesta;
    }

    public boolean GetStatusConecctionSGBD_PRODUCTION() {
        Boolean respuesta = false;
        try {

            V_OBJECTCONECTION = V_SPOOL_PRODUCTION.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_OBJECTCONECTION.close();
                respuesta = true;
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " Metodo GetStatusConecction - SQLException" + e.getMessage());
        }
        return respuesta;
    }

    public boolean GetStatusConecctionSGBD_LICENSE() {
        Boolean respuesta = false;
        try {

            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_OBJECTCONECTION.close();
                respuesta = true;
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " Metodo GetStatusConecction - SQLException" + e.getMessage());
        }
        return respuesta;
    }

    public boolean estaAutorizado(String Usuario, String Clave) {
        V_SQLQUERYSEARCH = "SELECT id FROM usuario WHERE  usuario=?  and  clave=? and tipo=1";//OR CODIGO_BARRA like ?
        boolean existe = false;

        try {
            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                V_PREPAREDSTATEMENT.setString(1, Usuario);
                V_PREPAREDSTATEMENT.setString(2, Clave);

                V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                V_RESULSET.last();
                existe = V_RESULSET.getRow() > 0;
                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(this.getClass().getName() + " GetProducto - SQLException" + e.getMessage());
        }
        return existe;
    }

    public boolean GetMyLicense() {
        V_SQLQUERYSEARCH = "SELECT TOP 1 activo FROM dispositivo WHERE id=? AND licencia=?";
        boolean DEVICE_REGISTER = false;

        try {
            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                V_PREPAREDSTATEMENT.setString(1, ConfApp.UUID_FROM_DEVICE);
                V_PREPAREDSTATEMENT.setString(2, ConfApp.UUID_ENCRYPTED);

                V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();

                int size = 0;
                V_RESULSET.last();
                size = V_RESULSET.getRow();
                V_RESULSET.beforeFirst();
                while (V_RESULSET.next()) {
                    DEVICE_REGISTER = V_RESULSET.getBoolean(1);
                }

                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " GetStatusConecction - SQLException" + e.getMessage());
        }
        return DEVICE_REGISTER;
    }

    public boolean SetDeviceLicense() {
        V_SQLQUERYINSERT = "IF NOT EXISTS(SELECT * FROM DISPOSITIVO WHERE id=?) INSERT INTO DISPOSITIVO(id,licencia,numero_serie,activo) VALUES (?,?,'',0) ELSE  UPDATE DISPOSITIVO SET licencia=? WHERE id=?";
        int resultado_query = 0;

        try {
            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            // referencia.appendMessage("<B>REGISTRANDO DISPOSITO EL SERVIDOR...</B>");

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYINSERT);
                V_PREPAREDSTATEMENT.setString(1, ConfApp.UUID_FROM_DEVICE);
                V_PREPAREDSTATEMENT.setString(2, ConfApp.UUID_FROM_DEVICE);
                V_PREPAREDSTATEMENT.setString(3, ConfApp.UUID_ENCRYPTED);
                V_PREPAREDSTATEMENT.setString(4, ConfApp.UUID_ENCRYPTED);
                V_PREPAREDSTATEMENT.setString(5, ConfApp.UUID_FROM_DEVICE);
                resultado_query = V_PREPAREDSTATEMENT.executeUpdate();

                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " GetMyLicense - SQLException" + e.getMessage());
        }
        return resultado_query > 0;
    }

    public boolean GetStatusLicense() {
        //FrmSplash Pantalla = (FrmSplash) referencia;

        V_SQLQUERYSEARCH = "DECLARE @t TABLE (i uniqueidentifier default newsequentialid(),  t as LOWER(i))";
        V_SQLQUERYSEARCH += "INSERT INTO @t default values;";
        V_SQLQUERYSEARCH += "SELECT (SELECT VALOR FROM PARAMETER_INV WHERE CAMPO='UUID') as UUID_PARAM, (select top 1 [file_guid] from [sys].[database_files]) as UUID_BD, (SELECT VALOR FROM PARAMETER_INV WHERE CAMPO='MACADDRESS') as MACADDRESS_PARAM,substring(t,25,2) + '-' + substring(t,27,2) + '-' + substring(t,29,2) + '-' + substring(t,31,2) + '-' + substring(t,33,2) + '-' +  substring(t,35,2) AS MACADRESS_BD, (SELECT VALOR FROM PARAMETER_INV WHERE CAMPO='LICENSE') as LICENSE_TOTAL, (SELECT COUNT(*) FROM dispositivo) AS LICENSE_USED, @@servername AS SERVER_NAME, (SELECT VALOR FROM PARAMETER_INV WHERE CAMPO='SERVER_NAME') as SERVER_NAME_PARAM, DB_NAME() AS BD_NAME, (SELECT VALOR FROM PARAMETER_INV WHERE CAMPO='SERVICE') as BD_NAME_PARAM from @t";

        String BD_UUID_PARAM = "", BD_UUID = " ", SERVER_MACADDRESS_PARAM = "", SERVER_MACADDRESS = " ", BD_NAME = "", BD_NAME_PARAM = "", SERVER_NAME = "", SERVER_NAME_PARAM = "";
        Integer LICENSE_TOTAL = 0, LICENSE_USED = 0;

        boolean STATUS_LICENSE = false;

        try {
            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH);
                V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();

                while (V_RESULSET.next()) {

                    BD_UUID_PARAM = MD5.Desencriptar(referencia, V_RESULSET.getString(1));
                    BD_UUID = V_RESULSET.getString(2);
                    SERVER_MACADDRESS_PARAM = MD5.Desencriptar(referencia, V_RESULSET.getString(3));
                    SERVER_MACADDRESS = V_RESULSET.getString(4);
                    LICENSE_TOTAL = Integer.parseInt(MD5.Desencriptar(referencia, V_RESULSET.getString(5)));
                    LICENSE_USED = V_RESULSET.getInt(6);
                    SERVER_NAME = V_RESULSET.getString(7);
                    SERVER_NAME_PARAM = MD5.Desencriptar(referencia, V_RESULSET.getString(8));
                    BD_NAME = V_RESULSET.getString(9);
                    BD_NAME_PARAM = MD5.Desencriptar(referencia, V_RESULSET.getString(10));
                }

                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " GetStatusLicense - SQLException" + e.getMessage());
            e.printStackTrace();
        }

        if (SERVER_NAME.equals(SERVER_NAME_PARAM)) {// && SERVER_MACADDRESS.equals(SERVER_MACADDRESS_PARAM)
            if (BD_NAME.equals(BD_NAME_PARAM) && BD_UUID.equals(BD_UUID_PARAM)) {
                if (LICENSE_USED <= LICENSE_TOTAL) {
                    if (LICENSE_USED < LICENSE_TOTAL)
                        SetDeviceLicense();
                    STATUS_LICENSE = GetMyLicense();
                    if (!STATUS_LICENSE)
                        referencia.printToas(referencia.getResources().getString(R.string.dipositivo_noautorizado));
                } else
                    referencia.appendMessage(referencia.getResources().getString(R.string.licencias_excedidas));
            } else
                referencia.printToas(referencia.getResources().getString(R.string.bd_noconcuerda));  //System.out.println("<B><font color='#AB2A3E'>LA LICENCIA DE LA BD NO CONCUERDA</FONT><B>");
        } else
            referencia.printToas(referencia.getResources().getString(R.string.servidor_noconcuerda));  //System.out.println("<B><font color='#AB2A3E'>LA DIRECCION DEL SERVIDOR NO CONCUERDA</FONT><B>");

        return STATUS_LICENSE;
    }

    private String showMensajeError(String msgbox) {
        StringBuilder mensaje = new StringBuilder();
        mensaje.append("<B><font color='#AB2A3E'>");
        mensaje.append(msgbox);
        mensaje.append("</font></B>");
        return mensaje.toString();
    }

    private void initSincronizacion() {
        referencia.appendMessage(ConfApp.LINE_DIVISOR);
        referencia.appendMessage((ConfApp.DEVICEAUTORIZED) ? "<B><font color='#006600'>DISPOSITIVO LICENCIADO</FONT><B>" : "<B><font color='#AB2A3E'>DISPOSITIVO NO LICENCIADO</FONT><B>");
        referencia.appendMessage(ConfApp.LINE_DIVISOR);

        if (ConfApp.DEVICEAUTORIZED) {

            IS_SERVER_ONLINE = GetStatusConecctionSGBD_ERP();
            if (IS_SERVER_ONLINE)
                referencia.appendMessage("<B>CONECTANDO CON EL SERVIDOR ERP</B>");
            else
                referencia.appendMessage(showMensajeError("SERVIDOR ERP NO DISPONIBLE"));

            IS_SERVER_ONLINE = GetStatusConecctionSGBD_PRODUCTION();
            if (IS_SERVER_ONLINE)
                referencia.appendMessage("<B>CONECTANDO CON EL SERVIDOR DE PRODUCCION</B>");
            else
                referencia.appendMessage(showMensajeError("SERVIDOR DE PRODUCCION NO DISPONIBLE"));

            IS_SERVER_ONLINE = GetStatusConecctionSGBD_LICENSE();
            if (IS_SERVER_ONLINE)
                referencia.appendMessage("<B>CONECTANDO CON EL SERVIDOR DE LICENCIAS</B>");
            else
                referencia.appendMessage(showMensajeError("SERVIDOR DE LICENCIAS NO DISPONIBLE"));

            if (IS_SERVER_ONLINE)
                referencia.appendMessage("<B>FECHA/HORA INICIO: " + GetCurrentTimeStamp() + "</B>");
            else {
                if (Tipo.equals("ENVIAR"))
                    referencia.appendMessage("<B>CREANDO ARCHIVO LOCAL PARA EXPORTAR</B>");
                else
                    referencia.appendMessage("<B>BUSCANDO ARCHIVO LOCAL PARA IMPORTAR</B>");

            }

            referencia.appendMessage(ConfApp.LINE_DIVISOR);

            if (GetStatusLicense()) {
                if (Tipo.equals("ENVIAR"))
                    EnviarDatos();
                else
                    RecibirDatos();
            }
        }
    }

    private void EnviarDatos() {
        /*Set_Table_Detalle();*/
    }

    private void RecibirDatos() {
        if (ConfApp.PERMISOS[0]) //PRODUCTO
            Get_Table_Producto("View_Producto", "prod_producto");

        if (ConfApp.PERMISOS[1]) //PEDIDO
            Get_Table_Pedido("View_Pedido", "soft_pedido");

        if (ConfApp.PERMISOS[2])//BODEGA
            Get_Table_Bodega("View_Bodega", "soft_bodega");

        if (ConfApp.PERMISOS[3])//CAMIONES
            Get_Table_Camion("View_Camion", "prod_camion");

        if (ConfApp.PERMISOS[4])//CONDUCTORES
            Get_Table_Conductor("View_Conductor", "soft_conductor");

        if (ConfApp.PERMISOS[5])
            Get_Table_Operador("View_Operador", "Operador");
    }

    public void Get_Table_Producto(String TableName, String TableTarget) {
        V_SQLQUERYSEARCH = "SELECT id,codigo,nombre,tipo,softland_id,unidad  FROM " + TableName;

        String sFileName = "Tabla " + TableName;
        ArrayList<Producto> Registros = new ArrayList<Producto>();

        try {

            if (IS_SERVER_ONLINE) {
                V_OBJECTCONECTION = V_SPOOL_PRODUCTION.getConnection();

                if (V_OBJECTCONECTION != null) {
                    V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH);
                    V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                    while (V_RESULSET.next()) {
                        Registros.add(new Producto(V_RESULSET.getInt(1), V_RESULSET.getString(2), V_RESULSET.getString(3), V_RESULSET.getString(4), V_RESULSET.getString(5).trim(), V_RESULSET.getInt(6)));
                    }
                    V_RESULSET.close();
                    V_PREPAREDSTATEMENT.close();
                    V_OBJECTCONECTION.close();
                }
            } else if (Boolean.parseBoolean(MD5.Desencriptar(referencia, TblParametro.getClave(referencia, "IMPORT_POLICIES")))) {
                sFileName = TableName + ".txt";
                BufferedReader br = null;
                File gpxfile = new File(new File(ConfApp.PATH_DIRECTORYWORK), sFileName);

                if (gpxfile.exists()) {   //PROCEDEMOS A IMPORTAR
                    try {
                        br = new BufferedReader(new FileReader(gpxfile));
                        String line;

                        while ((line = br.readLine()) != null) {
                            String[] campo = line.split(",");
                            if (campo.length == 6) {
                                Registros.add(new Producto(Integer.parseInt(campo[0].toString().trim()), campo[1].toString().trim(), campo[2].toString().trim(), campo[3].toString().trim(), campo[4].toString().trim(), Integer.parseInt(campo[5].toString().trim())));
                            }
                        }
                        br.close();
                    } catch (IOException e) {
                        //You'll need to add proper error handling here
                    }
                }
            }

            referencia.Progresbar_config(0, Registros.size(), false);

            if (Registros.size() > 0) {
                TblProducto.vaciarTabla(referencia);

                referencia.appendMessage(sFileName);
                referencia.appendMessage(Registros.size() + " Registros disponibles para importar ");

                int conError = 0;
                for (int i = 0; i < Registros.size(); i++) {
                    referencia.Progresbar_refres(i + 1, 0, false);
                    if (!TblProducto.guardar(referencia, Registros.get(i))) {
                        conError += 1;
                        referencia.appendMessage("> Error en Registro # " + (i + 1) + " " + Registros.get(i).toString2());
                    }
                }
                referencia.Progresbar_refres(Registros.size(), Registros.size() - conError, true);
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " GetStatusConecction - SQLException" + e.getMessage());
        }
    }

    public void Get_Table_Pedido(String TableName, String TableTarget) {
        V_SQLQUERYSEARCH = "SELECT id,fecha,cliente,ubicacion,direccion,tipo,iddestino,idbodega FROM " + TableName;

        String sFileName = "Tabla " + TableName;
        ArrayList<Pedido> Registros = Get_ListPedidos(TableName);


        if (!IS_SERVER_ONLINE)
            if (Boolean.parseBoolean(MD5.Desencriptar(referencia, TblParametro.getClave(referencia, "IMPORT_POLICIES"))))
                referencia.appendMessage(" IMPORTACION DE ORDENES LOCALES NO DISPONIBLE ");

        referencia.Progresbar_config(0, Registros.size(), false);

        if (Registros.size() > 0) {
            ArrayList<Pedido> pedidos_trabajados = TblPedido.obtenerPedidosTrabajados(referencia);

            //BUSCA LAS ORDENES QUE NO ESTAN SIENDO TRABAJADAS
            for (int contP = 0; contP < Registros.size(); contP++) {
                for (int contPT = 0; contPT < pedidos_trabajados.size(); contPT++) {
                    if (Registros.get(contP).getId() == pedidos_trabajados.get(contPT).getId()) {
                        Registros.get(contP).setAtentido(pedidos_trabajados.get(contPT).getAtentido());
                    }
                }
            }


            referencia.appendMessage(sFileName);
            referencia.appendMessage(Registros.size() + " Registros disponibles para importar ");


            int conError = 0;
            for (int contP = 0; contP < Registros.size(); contP++) {
                referencia.Progresbar_refres(contP + 1, 0, false);

                if (!Registros.get(contP).getAtentido()) { //OMITIMOS ORDES QUE ESTEN SIENDO PROCESADAS.
                    if (Registros.get(contP).getPedido_detalle().size() > 0) { //OMITIMOS ORDES QUE NO TENGAN PRODUCTOS.
                        TblPedido.borrarPedidoYDetalle(referencia, Registros.get(contP).getId());//.BorrarPedido(referencia,Registros.get(contP));

                        if (!TblPedido.guardar(referencia, Registros.get(contP))) {
                            conError += 1;
                            Log.i("Pedido_error", Registros.get(contP).toString2());
                        } else {
                            Log.i("Pedido_correcto", Registros.get(contP).toString2());
                            referencia.appendMessage("pedido #" + (contP + 1) + " Orden #:" + Registros.get(contP).getId() + " Ok");
                        }
                    } else {
                        conError += 1;
                        referencia.appendMessage(showMensajeError("> Error en Registro # " + (contP + 1) + " Orden #: " + Registros.get(contP).toString() + " Orden no tiene articulos"));
                    }
                } else {
                    conError += 1;
                    referencia.appendMessage(showMensajeError("Se omitio el pedido " + Registros.get(contP).getId() + ",esta siendo trabajado"));
                }
            }
            referencia.Progresbar_refres(Registros.size(), Registros.size() - conError, true);
        }
    }

    public ArrayList<Pedido_Detalle> Get_Table_Pedido_Detalle(String TableName, String NoPedido) {
        ArrayList<Pedido_Detalle> Registros = Get_ListPedidoDetalle(TableName, NoPedido);
        return Registros;
    }

    public void Get_Table_Bodega(String TableName, String TableTarget) {
        V_SQLQUERYSEARCH = "SELECT id,nombre,tipo  FROM " + TableName;

        String sFileName = "Tabla " + TableName;
        ArrayList<Bodega> Registros = new ArrayList<Bodega>();

        try {

            if (IS_SERVER_ONLINE) {
                V_OBJECTCONECTION = V_SPOOL_ERP.getConnection();

                if (V_OBJECTCONECTION != null) {
                    V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH);
                    V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                    while (V_RESULSET.next()) {
                        Registros.add(new Bodega(V_RESULSET.getString(1), V_RESULSET.getString(2), V_RESULSET.getString(3)));
                    }
                    V_RESULSET.close();
                    V_PREPAREDSTATEMENT.close();
                    V_OBJECTCONECTION.close();
                }
            } else if (Boolean.parseBoolean(MD5.Desencriptar(referencia, TblParametro.getClave(referencia, "IMPORT_POLICIES")))) {
                sFileName = TableName + ".txt";
                BufferedReader br = null;
                File gpxfile = new File(new File(ConfApp.PATH_DIRECTORYWORK), sFileName);

                if (gpxfile.exists()) {   //PROCEDEMOS A IMPORTAR
                    try {
                        br = new BufferedReader(new FileReader(gpxfile));
                        String line;

                        while ((line = br.readLine()) != null) {
                            String[] campo = line.split(",");
                            if (campo.length == 3) {
                                Registros.add(new Bodega(campo[0].toString().trim(), campo[1].toString().trim(), campo[2].toString().trim()));
                            }
                        }
                        br.close();
                    } catch (IOException e) {
                        //You'll need to add proper error handling here
                    }
                }
            }

            referencia.Progresbar_config(0, Registros.size(), false);

            if (Registros.size() > 0) {
                TblBodega.vaciarTabla(referencia);

                referencia.appendMessage(sFileName);
                referencia.appendMessage(Registros.size() + " Registros disponibles para importar ");

                int conError = 0;
                for (int i = 0; i < Registros.size(); i++) {
                    referencia.Progresbar_refres(i + 1, 0, false);
                    if (!TblBodega.guardar(referencia, Registros.get(i))) {
                        conError += 1;
                        referencia.appendMessage("> Error en Registro # " + (i + 1) + " " + Registros.get(i).toString2());
                    }
                }
                referencia.Progresbar_refres(Registros.size(), Registros.size() - conError, true);
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " GetStatusConecction - SQLException" + e.getMessage());
        }
    }

    public void Get_Table_Camion(String TableName, String TableTarget) {
        V_SQLQUERYSEARCH = "SELECT id,noplaca,min,max  FROM " + TableName;

        String sFileName = "Tabla " + TableName;
        ArrayList<Camion> Registros = new ArrayList<Camion>();

        try {

            if (IS_SERVER_ONLINE) {
                V_OBJECTCONECTION = V_SPOOL_PRODUCTION.getConnection();

                if (V_OBJECTCONECTION != null) {
                    V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH);
                    V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                    while (V_RESULSET.next()) {
                        Registros.add(new Camion(V_RESULSET.getInt(1), V_RESULSET.getString(2), V_RESULSET.getInt(3), V_RESULSET.getInt(4)));
                    }
                    V_RESULSET.close();
                    V_PREPAREDSTATEMENT.close();
                    V_OBJECTCONECTION.close();
                }
            } else if (Boolean.parseBoolean(MD5.Desencriptar(referencia, TblParametro.getClave(referencia, "IMPORT_POLICIES")))) {
                sFileName = TableName + ".txt";
                BufferedReader br = null;
                File gpxfile = new File(new File(ConfApp.PATH_DIRECTORYWORK), sFileName);

                if (gpxfile.exists()) {   //PROCEDEMOS A IMPORTAR
                    try {
                        br = new BufferedReader(new FileReader(gpxfile));
                        String line;

                        while ((line = br.readLine()) != null) {
                            String[] campo = line.split(",");
                            if (campo.length == 3) {
                                Registros.add(new Camion(Integer.parseInt(campo[0].toString().trim()), campo[1].toString().trim(), Integer.parseInt(campo[2].toString().trim()), Integer.parseInt(campo[3].toString().trim())));
                            }
                        }
                        br.close();
                    } catch (IOException e) {
                        //You'll need to add proper error handling here
                    }
                }
            }

            referencia.Progresbar_config(0, Registros.size(), false);

            if (Registros.size() > 0) {
                TblCamion.vaciarTabla(referencia);

                referencia.appendMessage(sFileName);
                referencia.appendMessage(Registros.size() + " Registros disponibles para importar ");

                int conError = 0;
                for (int i = 0; i < Registros.size(); i++) {
                    referencia.Progresbar_refres(i + 1, 0, false);
                    if (!TblCamion.guardar(referencia, Registros.get(i))) {
                        conError += 1;
                        referencia.appendMessage("> Error en Registro # " + (i + 1) + " " + Registros.get(i).toString2());
                    }
                }
                referencia.Progresbar_refres(Registros.size(), Registros.size() - conError, true);
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " GetStatusConecction - SQLException" + e.getMessage());
        }
    }

    public void Get_Table_Conductor(String TableName, String TableTarget) {
        V_SQLQUERYSEARCH = "SELECT id,codigo,nombre FROM " + TableName;

        String sFileName = "Tabla " + TableName;
        ArrayList<Conductor> Registros = new ArrayList<Conductor>();

        try {

            if (IS_SERVER_ONLINE) {
                V_OBJECTCONECTION = V_SPOOL_ERP.getConnection();

                if (V_OBJECTCONECTION != null) {
                    V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH);
                    V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                    while (V_RESULSET.next()) {
                        Registros.add(new Conductor(V_RESULSET.getString(1), V_RESULSET.getString(2), V_RESULSET.getString(3)));
                    }
                    V_RESULSET.close();
                    V_PREPAREDSTATEMENT.close();
                    V_OBJECTCONECTION.close();
                }
            } else if (Boolean.parseBoolean(MD5.Desencriptar(referencia, TblParametro.getClave(referencia, "IMPORT_POLICIES")))) {
                sFileName = TableName + ".txt";
                BufferedReader br = null;
                File gpxfile = new File(new File(ConfApp.PATH_DIRECTORYWORK), sFileName);

                if (gpxfile.exists()) {   //PROCEDEMOS A IMPORTAR
                    try {
                        br = new BufferedReader(new FileReader(gpxfile));
                        String line;

                        while ((line = br.readLine()) != null) {
                            String[] campo = line.split(",");
                            if (campo.length == 3) {
                                Registros.add(new Conductor(campo[0].toString().trim(), campo[1].toString().trim(), campo[2].toString().trim()));
                            }
                        }
                        br.close();
                    } catch (IOException e) {
                        //You'll need to add proper error handling here
                    }
                }
            }

            referencia.Progresbar_config(0, Registros.size(), false);

            if (Registros.size() > 0) {
                TblConductor.vaciarTabla(referencia);

                referencia.appendMessage(sFileName);
                referencia.appendMessage(Registros.size() + " Registros disponibles para importar ");

                int conError = 0;
                for (int i = 0; i < Registros.size(); i++) {
                    referencia.Progresbar_refres(i + 1, 0, false);
                    if (!TblConductor.guardar(referencia, Registros.get(i))) {
                        conError += 1;
                        referencia.appendMessage("> Error en Registro # " + (i + 1) + " " + Registros.get(i).toString2());
                    }
                }
                referencia.Progresbar_refres(Registros.size(), Registros.size() - conError, true);
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " GetStatusConecction - SQLException" + e.getMessage());
        }
    }

    public void Get_Table_Operador(String TableName, String TableTarget) {
        V_SQLQUERYSEARCH = "SELECT id, usuario, clave, nombre,tipo  FROM " + TableName;

        String sFileName = "Tabla " + TableName;
        ArrayList<Operador> Registros = new ArrayList<Operador>();

        try {

            if (IS_SERVER_ONLINE) {
                V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

                if (V_OBJECTCONECTION != null) {
                    V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH);
                    V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                    while (V_RESULSET.next()) {
                        Registros.add(new Operador(V_RESULSET.getInt(1), V_RESULSET.getString(2), V_RESULSET.getString(3), V_RESULSET.getString(4), V_RESULSET.getInt(5)));
                    }
                    V_RESULSET.close();
                    V_PREPAREDSTATEMENT.close();
                    V_OBJECTCONECTION.close();
                }
            } else if (Boolean.parseBoolean(MD5.Desencriptar(referencia, TblParametro.getClave(referencia, "IMPORT_POLICIES")))) {
                sFileName = TableName + ".txt";
                BufferedReader br = null;
                File gpxfile = new File(new File(ConfApp.PATH_DIRECTORYWORK), sFileName);

                if (gpxfile.exists()) {   //PROCEDEMOS A IMPORTAR
                    try {
                        br = new BufferedReader(new FileReader(gpxfile));
                        String line;

                        while ((line = br.readLine()) != null) {
                            String[] campo = line.split(",");
                            if (campo.length == 3) {
                                Registros.add(new Operador(Integer.parseInt(campo[0].toString().trim()), campo[2].toString().trim(), campo[3].toString().trim(), campo[1].toString().trim(), 0));
                            }
                            if (campo.length == 4) {
                                Registros.add(new Operador(Integer.parseInt(campo[0].toString().trim()), campo[2].toString().trim(), campo[3].toString().trim(), campo[1].toString().trim(), Integer.parseInt(campo[4].toString().trim())));
                            }
                        }
                        br.close();
                    } catch (IOException e) {
                        //You'll need to add proper error handling here
                    }
                }
            }

            referencia.Progresbar_config(0, Registros.size(), false);

            if (Registros.size() > 0) {
                TblOperador.vaciarTabla(referencia);

                referencia.appendMessage(sFileName);
                referencia.appendMessage(Registros.size() + " Registros disponibles para importar ");

                int conError = 0;
                for (int i = 0; i < Registros.size(); i++) {
                    referencia.Progresbar_refres(i + 1, 0, false);
                    if (!TblOperador.guardar(referencia, Registros.get(i))) {
                        conError += 1;
                        referencia.appendMessage("Error, " + Registros.get(i) + " #:" + Registros.get(i).toString());
                    }
                }
                referencia.Progresbar_refres(Registros.size(), Registros.size() - conError, true);
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " GetStatusConecction - SQLException" + e.getMessage());
        }
    }

    public String GetCurrentTimeStamp() {
        V_SQLQUERYSEARCH = "SELECT GETDATE()";
        String valor = "";

        try {
            V_OBJECTCONECTION = V_SPOOL_LICENSE.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH);
                V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                while (V_RESULSET.next()) {
                    java.sql.Date fechaOrigen = new java.sql.Date(V_RESULSET.getTimestamp(1).getTime());
                    java.util.Date fechaDestino = new java.util.Date(fechaOrigen.getTime());

                    valor = Utils.bd.C_DateToDBFORMAT(fechaDestino);// Utils.C_TimeStampToCustom(fecha, ConfApp.ISO8601_FORMATTIMESTAMP_APP);
                }
                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " GetStatusConecction - SQLException" + e.getMessage());
        }
        return valor;
    }

    public ArrayList<Pedido> Get_ListPedidos(String TableName) {
        V_SQLQUERYSEARCH = "SELECT id,fecha,cliente,ubicacion,direccion,tipo,iddestino,idbodega FROM " + TableName;
        ArrayList<Pedido> Registros = new ArrayList<Pedido>();

        try {
            V_OBJECTCONECTION = V_SPOOL_ERP.getConnection();
            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH);
                V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                while (V_RESULSET.next()) {
                    Pedido pedido = new Pedido();
                    pedido.setId(V_RESULSET.getString(1));
                    pedido.setFecha(new Date(V_RESULSET.getDate(2).getTime()));
                    pedido.setCliente(V_RESULSET.getString(3));
                    pedido.setUbicacion(V_RESULSET.getString(4));
                    pedido.setDireccion(V_RESULSET.getString(5));
                    pedido.setTipo(V_RESULSET.getString(6));
                    pedido.setIddestino(V_RESULSET.getString(7));
                    pedido.setIdBodega(V_RESULSET.getString(8));

                    Registros.add(pedido);
                }
                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }

            for (int contP = 0; contP < Registros.size(); contP++) {
                Registros.get(contP).setPedido_detalle(Get_ListPedidoDetalle("View_Pedido_Detalle", Registros.get(contP).getId()));
            }

        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " Get_ListPedidos - SQLException" + e.getMessage());
        }

        return Registros;
    }

    public Pedido Get_Pedido(String TableName,String idpedido) {
        V_SQLQUERYSEARCH = "SELECT id,fecha,cliente,ubicacion,direccion,tipo,iddestino,idbodega FROM " + TableName+" WHERE id='"+idpedido+"'";
        Pedido pedido = new Pedido();
        try {
            V_OBJECTCONECTION = V_SPOOL_ERP.getConnection();
            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH);
                V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                while (V_RESULSET.next()) {

                    pedido.setId(V_RESULSET.getString(1));
                    pedido.setFecha(new Date(V_RESULSET.getDate(2).getTime()));
                    pedido.setCliente(V_RESULSET.getString(3));
                    pedido.setUbicacion(V_RESULSET.getString(4));
                    pedido.setDireccion(V_RESULSET.getString(5));
                    pedido.setTipo(V_RESULSET.getString(6));
                    pedido.setIddestino(V_RESULSET.getString(7));
                    pedido.setIdBodega(V_RESULSET.getString(8));
                }
                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }

            pedido.setPedido_detalle(Get_ListPedidoDetalle("View_Pedido_Detalle", pedido.getId()));

        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " Get_ListPedidos - SQLException" + e.getMessage());
        }

        return pedido;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        initSincronizacion();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        referencia.notificarFinalizacion(IS_SERVER_ONLINE);
    }

    public ArrayList<Pedido_Detalle> Get_ListPedidoDetalle(String TableName, String NoPedido){
        ArrayList<Pedido_Detalle> Registros = new ArrayList<Pedido_Detalle>();
        V_SQLQUERYSEARCH = "SELECT id,cantidad,idarticulo  FROM " + TableName + " WHERE idpedido=?";

        try {

            V_OBJECTCONECTION = V_SPOOL_ERP.getConnection();

            if (V_OBJECTCONECTION != null) {
                V_PREPAREDSTATEMENT = (PreparedStatement) V_OBJECTCONECTION.prepareStatement(V_SQLQUERYSEARCH);
                V_PREPAREDSTATEMENT.setString(1, NoPedido);

                V_RESULSET = V_PREPAREDSTATEMENT.executeQuery();
                while (V_RESULSET.next()) {
                    Registros.add(new Pedido_Detalle(V_RESULSET.getInt(1), V_RESULSET.getFloat(2), V_RESULSET.getString(3), NoPedido));
                }
                V_RESULSET.close();
                V_PREPAREDSTATEMENT.close();
                V_OBJECTCONECTION.close();
            }
        } catch (SQLException e) {
            System.out.println(this.getClass().getName() + " Get_ListPedidoDetalle - SQLException" + e.getMessage());
        }
        return  Registros;
    }

}
