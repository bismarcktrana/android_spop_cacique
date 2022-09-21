package com.sdn.cacique.util;

import android.content.Context;
import android.provider.Settings;

import java.util.Arrays;

import com.sdn.bd.dao.host.TblParametro;
import com.sdn.bd.objeto.host.Contenedor;
import com.sdn.bd.objeto.host.Operador;
import com.sdn.bd.objeto.host.Parametro;
import com.sdn.bd.objeto.softland.Pedido;
import com.sdn.bd.secutidad.MD5;
import com.sdn.cacique.bdremote.BDOperacion;
import com.sdn.cacique.bdremote.BDOperacion_Update;
import com.sdn.cacique.spop.R;

/**
 * Created by Bismark Salvador Traña López on 15/8/2016.
 */

public class ConfApp {
    public static com.sdn.bd.util.ConfApp bd = new com.sdn.bd.util.ConfApp();

    public static Boolean DEBUG = false;

    /////////////////////////////////////////////////////// VARIABLES GLOBALES DE LOS SERVIDORES REMOTOES EN SQL SERVER  /////////////////////////////////////

    public static String SERVER_SOFTLANDERP;
    public static String SERVER_PRODUCCION;
    public static String SERVER_LICENCIA;

    public static String BDNAME_SOFTLANDERP;
    public static String BDNAME_PRODUCCION;
    public static String BDNAME_LICENCIA;

    //EL USUARIO Y CLAVE DEBE EXISTIR EN LAS TRES INSTANCIAS QUE UTILIZARAMOS PARA EL SISTEMA.
    public static String BDUSER;
    public static String BDPASS;

    ///////////////////////////////////////////////////////  CLASES  GLOBALES DE TRABAJO DEL SISTEMA  ///////////////////////////////////////////

    public static Pedido ORDEN_TRABAJADA_ACTUALMENTE= new Pedido();
    public static Contenedor CONTENEDOR_ACTUALMENTE= new Contenedor();
    public static Operador OPERADORLOGEADO = new Operador();
    public static BDOperacion_Update BDOPERATION_SINCRONIZAR;
    public static BDOperacion BDOPERATION;
    public static Object[] CODIGO_BARRA=null;

    ///////////////////////////////////////////////////////  VARIABLES GLOBALES DE TRABAJO DEL SISTEMA  ///////////////////////////////////////////

    public static Boolean USER_DTS = false;
    public static Boolean USER_SUPERVISOR = false;
    public static Boolean USER_ESTIBADOR = false;

    public static int SOUND_ADMIN = 0;
    public static int SOUND_SUPERVISOR = 0;
    public static int SOUND_OPERATOR = 0;

    public static  double LIMIT_WEIGHT;

   /* public static Date CODEBAR_FECHA_PROCESO = null;
    public static Double CODEBAR_PESO = 0.00;
    public static String CODEBAR_IDPRODUCTO = "";
    public static String CODEBAR_LOTE = "";
    public static String CODEBAR_SERIE = "";*/
    public static String ULTIMAACTUALIZACION = "";

    ///////////////////////////////////////////////////////  OTRAS VARIABLES GLOBALES DE PRESENTACION DEL SISTEMA  ///////////////////////////////////////////

    public static String LINE_DIVISOR = "______________________________________________";

    public static String PATH_DIRECTORYWORK;

    public static Boolean IMPORT_POLICIES;
    public static Boolean EXPORT_POLICIES;
    public static String PARTIAL_IMPORT_POLICIES;
    public static Boolean[] PERMISOS = new Boolean[6];

    public static Integer TIMER_READ_WAIT;
    public static String HOST_LOCATION;
    public static String CARACTER_SPLIT;
    public static String EXCELFILE;
    public static String SUPPLIER;


    public static Integer METHOD_BDISONLINE = 1;
    public static Integer METHOD_GETORDERS = 2;
    public static Integer METHOD_IMPORTFROMSERVER = 3;

     ///////////////////////////////////////////////////////  VARIABLES GLOBAL DE SEGURIDAD DEL SISTEMA///////////////////////////////////////////////////////

    public static String UUID_FROM_DEVICE;
    public static String UUID_ENCRYPTED;
    public static String UUID_DESENCRYPTED;
    public static boolean DEVICEAUTORIZED = false;

    public static String SYSTEM_USER;
    public static String SYSTEM_PASS;

    ///////////////////////////////////////////////////////  PARAMETROS DE TIEMPO Y FECHA DEL SISTEMA  ///////////////////////////////////////////

   /* public static SimpleDateFormat ISO8601_FORMATTER;

    public static final String ISO8601_FORMATDATE_BD = "yyyy-MM-dd";
    public static final String ISO8601_FORMATDATE_APP = "dd/MM/yyyy";

    public static final String ISO8601_FORMATTIMESTAMP_BD = "yyyy-MM-dd HH:mm:ss";//HH:MM:SS
    public static final String ISO8601_FORMATTIMESTAMP_APP = "dd/MM/yyyy hh:mm:ss aaa";

    public static final String ISO8601_FORMATHOUR_BD = "HH:MM:SS";
    public static final String ISO8601_FORMATHOUR_APP = "hh:mm:ss aaa";*/


    /**
     * crea una carpeta de trabajo para el aplicativo actual.
     *
     * Este debe ser el primer metodo que se ejecute
     *
     * @param pantalla
     */
    public static void createDirectoryWork(Context pantalla,String directorio) {
        ConfApp.PATH_DIRECTORYWORK =Utils.bd.createWorkDirectory(pantalla,directorio);
        Utils.bd.CopyBDFromSambox(pantalla,ConfApp.PATH_DIRECTORYWORK);
        TblParametro.guardar(pantalla,new Parametro("HOST_LOCATION",ConfApp.PATH_DIRECTORYWORK));//notifica al sistema la nueva ruta de almacenamiento

    }

    /**
     * Carga los parametros del sistema para coneccion a lo servidores y configuraciones locales.
     *
     *  Este debe ser el segund metedo que se ejecute.
     *
     * @param pantalla
     */
    public static void loadParameters(Context pantalla) {
        /*******************************CARGAR CONFIG SERVIDOR SOFTLANDERP*****************************/
        ConfApp.SERVER_SOFTLANDERP = MD5.Desencriptar(pantalla, TblParametro.getClave(pantalla, "SERVER_SOFTLANDERP"));
        ConfApp.BDNAME_SOFTLANDERP = MD5.Desencriptar(pantalla,TblParametro.getClave(pantalla, "BDNAME_SOFTLANDERP"));

        /*******************************CARGAR CONFIG SERVIDOR PRODUCCION*****************************/
        ConfApp.SERVER_PRODUCCION= MD5.Desencriptar(pantalla, TblParametro.getClave(pantalla, "SERVER_PRODUCCION"));
        ConfApp.BDNAME_PRODUCCION = MD5.Desencriptar(pantalla,TblParametro.getClave(pantalla, "BDNAME_PRODUCCION"));

        /*******************************CARGAR CONFIG SERVIDOR LICENCIAS*****************************/
        ConfApp.SERVER_LICENCIA= MD5.Desencriptar(pantalla, TblParametro.getClave(pantalla, "SERVER_LICENCIA"));
        ConfApp.BDNAME_LICENCIA = MD5.Desencriptar(pantalla,TblParametro.getClave(pantalla, "BDNAME_LICENCIA"));

        /*******************************CARGAR USUARIO Y CLAVE COMPARTIDA EN LOS TRES SERVIDORES*****************************/
        ConfApp.BDUSER =MD5.Desencriptar(pantalla, TblParametro.getClave(pantalla, "BDUSER"));
        ConfApp.BDPASS = MD5.Desencriptar(pantalla,TblParametro.getClave(pantalla, "BDPASS"));

        /*******************************CARGAR CONFIG LOCAL*****************************/

        ConfApp.SYSTEM_USER = MD5.Desencriptar(pantalla, TblParametro.getClave(pantalla, "SYSTEM_USER"));
        ConfApp.SYSTEM_PASS = MD5.Desencriptar(pantalla, TblParametro.getClave(pantalla, "SYSTEM_PASS"));

        ConfApp.UUID_FROM_DEVICE = Settings.Secure.getString(pantalla.getContentResolver(), Settings.Secure.ANDROID_ID);
        ConfApp.UUID_ENCRYPTED =TblParametro.getClave(pantalla, "UUID");
        ConfApp.UUID_DESENCRYPTED =MD5.Desencriptar(pantalla, UUID_ENCRYPTED);
        ConfApp.DEVICEAUTORIZED = ConfApp.UUID_FROM_DEVICE.trim().toString().equals(ConfApp.UUID_DESENCRYPTED.trim().toString());

        /*******************************CARGAR CONFIG APP*****************************/

        try{
            ConfApp.TIMER_READ_WAIT = Integer.parseInt(TblParametro.getClave(pantalla, "TIMER_READ_WAIT"));
        }catch (Exception e){
            ConfApp.TIMER_READ_WAIT = 5000;
        }

        try{
            ConfApp.PATH_DIRECTORYWORK = TblParametro.getClave(pantalla, "HOST_LOCATION");
        }catch (Exception e){
            createDirectoryWork(pantalla,pantalla.getResources().getString(R.string.app_name));
        }

        try{
            ConfApp.CARACTER_SPLIT = TblParametro.getClave(pantalla, "CARACTER_SPLIT");
        }catch (Exception e){
            ConfApp.CARACTER_SPLIT = ",";
        }

        try{
            String import_policies = TblParametro.getClave(pantalla, "IMPORT_POLICIES");
            ConfApp.IMPORT_POLICIES =Boolean.parseBoolean( MD5.Desencriptar(pantalla, import_policies));

        }catch (Exception e){
            ConfApp.IMPORT_POLICIES = false;
        }

        try{
            String export_policies = TblParametro.getClave(pantalla, "EXPORT_POLICIES");
            ConfApp.EXPORT_POLICIES =Boolean.parseBoolean( MD5.Desencriptar(pantalla, export_policies));
        }catch (Exception e){
            ConfApp.EXPORT_POLICIES = false;
        }

        try{
            ConfApp.PARTIAL_IMPORT_POLICIES =TblParametro.getClave(pantalla, "PARTIAL_IMPORT_POLICIES");
        }catch (Exception e){
            ConfApp.PARTIAL_IMPORT_POLICIES = "false";
        }

        try{
            String partial_policies = MD5.Desencriptar(pantalla,  TblParametro.getClave(pantalla, "PARTIAL_IMPORT_POLICIES"));
            String caracter_split=TblParametro.getClave(pantalla, "CARACTER_SPLIT");

            String[] _permisos = partial_policies.split(caracter_split);

            if(_permisos.length==1){
                Arrays.fill(PERMISOS,Boolean.parseBoolean(_permisos[0])? true:false);
            }else if(_permisos.length==PERMISOS.length){
                PERMISOS[0]= Boolean.parseBoolean(_permisos[0]);
                PERMISOS[1]= Boolean.parseBoolean(_permisos[1]);
                PERMISOS[2]= Boolean.parseBoolean(_permisos[2]);
                PERMISOS[3]= Boolean.parseBoolean(_permisos[3]);
                PERMISOS[4]= Boolean.parseBoolean(_permisos[4]);
                PERMISOS[5]= Boolean.parseBoolean(_permisos[5]);
            }else{

            }
            System.out.println("\n\n\n\n String[] _permisos"+_permisos.length+" "+MD5.Desencriptar(pantalla,  TblParametro.getClave(pantalla, "PARTIAL_IMPORT_POLICIES")));

        }catch(Exception e){
            Arrays.fill(PERMISOS, Boolean.FALSE);
        }

        try{
            ConfApp.SOUND_ADMIN =  Integer.parseInt(TblParametro.getClave(pantalla, "SOUND_ADMIN"));
        }catch(Exception e){
            ConfApp.SOUND_ADMIN = 1;
        }

        try{
            ConfApp.SOUND_SUPERVISOR =  Integer.parseInt(TblParametro.getClave(pantalla, "SOUND_SUPERVISOR"));
        }catch(Exception e){
            ConfApp.SOUND_SUPERVISOR = 2;
        }

        try{
            ConfApp.SOUND_OPERATOR =  Integer.parseInt(TblParametro.getClave(pantalla, "SOUND_OPERATOR"));
        }catch(Exception e){
            ConfApp.SOUND_OPERATOR = 1;
        }

        try{
            ConfApp.LIMIT_WEIGHT =  Integer.parseInt(TblParametro.getClave(pantalla, "LIMIT_WEIGHT"));
        }catch(Exception e){
            ConfApp.LIMIT_WEIGHT = 10.0;
        }
    }

    public static void WriteParameter(Context pantalla) {

        if(DEBUG){
            TblParametro.vaciarTabla(pantalla);
            TblParametro.guardar(pantalla,new Parametro("SUPPLIER","DTSolutions Nicaragua, S. A.") );//DEBE SER EL PRIMER PARAMETRO YA QUE SIRVE PARA DESENCRIPTAR LO DEMAS

            TblParametro.guardar(pantalla,new Parametro("UUID",MD5.Encriptar(pantalla,Settings.Secure.getString(pantalla.getContentResolver(), Settings.Secure.ANDROID_ID))));

            TblParametro.guardar(pantalla,new Parametro("HOST_LOCATION",Utils.bd.createWorkDirectory(pantalla,pantalla.getResources().getString(R.string.app_name))) );

            String SERVIDOR = "192.168.1.42:1433";

            TblParametro.guardar(pantalla,new Parametro("SERVER_SOFTLANDERP",MD5.Encriptar(pantalla,SERVIDOR)) );
            TblParametro.guardar(pantalla,new Parametro("BDNAME_SOFTLANDERP",MD5.Encriptar(pantalla,"CtrlSoftland")) );

            TblParametro.guardar(pantalla,new Parametro("SERVER_PRODUCCION",MD5.Encriptar(pantalla,SERVIDOR)) );
            TblParametro.guardar(pantalla,new Parametro("BDNAME_PRODUCCION",MD5.Encriptar(pantalla,"CtrlProduction")) );

            TblParametro.guardar(pantalla,new Parametro("SERVER_LICENCIA",MD5.Encriptar(pantalla,SERVIDOR)) );
            TblParametro.guardar(pantalla,new Parametro("BDNAME_LICENCIA",MD5.Encriptar(pantalla,"CtrlLicenciaCacique")) );//CtrlLicenciaCacique

            TblParametro.guardar(pantalla,new Parametro("BDUSER",MD5.Encriptar(pantalla,"sa")) );
            TblParametro.guardar(pantalla,new Parametro("BDPASS",MD5.Encriptar(pantalla,"2004-20488Aa")) );

            TblParametro.guardar(pantalla,new Parametro("SYSTEM_USER",MD5.Encriptar(pantalla,"dts")));
            TblParametro.guardar(pantalla,new Parametro("SYSTEM_PASS",MD5.Encriptar(pantalla,"1")) );

            String spliter = ",";
            TblParametro.guardar(pantalla,new Parametro("CARACTER_SPLIT",spliter) );
            TblParametro.guardar(pantalla,new Parametro("IMPORT_POLICIES",MD5.Encriptar(pantalla,"true")) );
            TblParametro.guardar(pantalla,new Parametro("EXPORT_POLICIES",MD5.Encriptar(pantalla,"true")) );
            TblParametro.guardar(pantalla,new Parametro("PARTIAL_IMPORT_POLICIES",MD5.Encriptar(pantalla,"true"+spliter+"true"+spliter+"true"+spliter+"true"+spliter+"true"+spliter+"true")) );

            TblParametro.guardar(pantalla,new Parametro("TIMER_READ_WAIT","3000") );

            TblParametro.guardar(pantalla,new Parametro("SOUND_ADMIN","1") );
            TblParametro.guardar(pantalla,new Parametro("SOUND_SUPERVISOR","1") );
            TblParametro.guardar(pantalla,new Parametro("SOUND_OPERATOR","2") );
            TblParametro.guardar(pantalla,new Parametro("LIMIT_WEIGHT","10.00") );
        }

    }
    /**
     * Carga las conecciones con los servidores
     *
     * Este debe ser el tercer metodo que debemos llamar
     *
     */
    public static void loadConection() {
        BDOPERATION_SINCRONIZAR = new BDOperacion_Update();
        BDOPERATION = new BDOperacion();
    }
}
