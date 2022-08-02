package com.sdn.cacique.util;

import android.content.Context;
import android.widget.TabHost;

import com.sdn.bd.dao.TblParametro;
import com.sdn.bd.host.BDUtil;

import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;


public class Utils {
    public static com.sdn.bd.util.Utils bd = new com.sdn.bd.util.Utils();

    public static boolean esCodigoValido(String codigo) {

        boolean respuesta = false;
        //codigo = codigo.replace(" ", "");//REEMPLAZAR CUALQUIER ESPACIO.

        if(codigo.length()==50){
            respuesta = true;
            respuesta &= isNumber(codigo.substring(0, 2));           //ESTACION -2
            respuesta &= isNumber(codigo.substring(2, 10));          //DATO FIJO1 -8
            //respuesta &= codigo.substring(10, 15);                //PRODUCTO -5
            respuesta &= isNumber(codigo.substring(15, 18));         //DATO FIJO2 -3
            respuesta &= isDate(codigo.substring(18, 24),true);//FECHA - 6
            respuesta &= isNumber(codigo.substring(24, 28));         //DATO FIJO3 - 4
            respuesta &= isNumber(codigo.substring(28, 34));         //PESO -6
            respuesta &= isNumber(codigo.substring(34, 36));         //NO PIEZAS -2
            respuesta &= isNumber(codigo.substring(36, 42));         //SUBLOTE - 6
            respuesta &= isNumber(codigo.substring(42, 44));         //DATO FIJO5 -2
            respuesta &= isNumber(codigo.substring(44, 50));         //SERIE 6

            if(respuesta){
               ConfApp.CODIGO_BARRA=new Object[13];
                ConfApp.CODIGO_BARRA[0] = 1;
                ConfApp.CODIGO_BARRA[1] = codigo.substring(0, 2);                           //ESTACION -2
                ConfApp.CODIGO_BARRA[2] = codigo.substring(2, 10);                          //DATO FIJO1 -8
                ConfApp.CODIGO_BARRA[3] = codigo.substring(10, 15);                         //PRODUCTO -5
                ConfApp.CODIGO_BARRA[4] = codigo.substring(15, 18);                         //DATO FIJO2 -3
                ConfApp.CODIGO_BARRA[5] =convertToDate(codigo.substring(18, 24),true);//FECHA - 6
                ConfApp.CODIGO_BARRA[6] = codigo.substring(24, 28);                         //DATO FIJO3 - 4
                ConfApp.CODIGO_BARRA[7] =convertToWeight(codigo.substring(28, 34));         //PESO -6
                ConfApp.CODIGO_BARRA[8] =convertToInteger(codigo.substring(34, 36));        //NO PIEZAS -2
                ConfApp.CODIGO_BARRA[9] = codigo.substring(36, 42);                         //SUBLOTE - 6
                ConfApp.CODIGO_BARRA[10] = codigo.substring(42, 44);                        //DATO FIJO5 -2
                ConfApp.CODIGO_BARRA[11] = codigo.substring(44, 50);                        //SERIE 6
                ConfApp.CODIGO_BARRA[12] = codigo;                                          //El codigo leido - 50
            }else{
                ConfApp.CODIGO_BARRA=null;
            }

        }

        if(codigo.length() ==21){
            respuesta = true;
           // respuesta &= codigo.substring(0, 5);              //PRODUCTO  -5
            respuesta &= isNumber(codigo.substring(5,9));               //PESO -4
            respuesta &= isNumber(codigo.substring(9,11));              //NO PIEZAS
            respuesta &= isDate(codigo.substring(11,15),false);//FECHA-4
           // respuesta &= isNumber(codigo.substring(15,21));         //SERIE 6 - NO SE VALIDA DEBIDO AL ESPACIO QUE PUEDE TENER LA SERIE QUE GENERA UNERROR

            if(respuesta){
                ConfApp.CODIGO_BARRA=new Object[13];
                ConfApp.CODIGO_BARRA[0] = 0;
                ConfApp.CODIGO_BARRA[1] = "";                               //ESTACION -2
                ConfApp.CODIGO_BARRA[2] = "";                               //DATO FIJO1 -8
                ConfApp.CODIGO_BARRA[3] = codigo.substring(0, 5);           //PRODUCTO -5
                ConfApp.CODIGO_BARRA[4] = "";                               //DATO FIJO2 -3
                ConfApp.CODIGO_BARRA[5] =convertToDate(codigo.substring(11,15),false);          //FECHA - 6
                ConfApp.CODIGO_BARRA[6] = "";                               //DATO FIJO3 - 4
                ConfApp.CODIGO_BARRA[7] =convertToWeight(codigo.substring(5,9));            //PESO -6
                ConfApp.CODIGO_BARRA[8] =convertToInteger(codigo.substring(9,11));           //NO PIEZAS -2
                ConfApp.CODIGO_BARRA[9] = "";                               //SUBLOTE - 6
                ConfApp.CODIGO_BARRA[10] = "";                              //DATO FIJO5 -2
                ConfApp.CODIGO_BARRA[11] = codigo.substring(15,21);         //SERIE 6
                ConfApp.CODIGO_BARRA[12] = codigo;                          //El codigo leido - 21
            }else{
                ConfApp.CODIGO_BARRA=null;
            }
        }

        return respuesta;
    }

    /**
     *
     * @param valor
     * @return
     */
    private static Integer convertToInteger(String valor){
        Integer numero = null;
        try {
            numero = Integer.parseInt(valor);
        } catch (Exception e) {
            System.out.println("Error en convertToInteger:" + valor + " Info:" + e.getMessage());
        }
        return  numero;
    }

    /**
     * @param fecha recibe fecha en String diames 0102 o diamesano 010222
     * @param haveYear sirve para identifica si se paso el ano
     * @return
     */
    private static Date convertToDate(String fecha, boolean haveYear) {
        //Fecha de ejmplo 0102   ->01/02 j y se agregara el ano
        //Fecha de ejmplo 010222 ->01/02/2022
        Date fecha_F = null;
        try{
            Calendar calendario = Calendar.getInstance();
            calendario.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fecha.substring(0, 2)));
            calendario.set(Calendar.MONTH, Integer.parseInt(fecha.substring(2, 4)) - 1);
            if(haveYear)
                calendario.set(Calendar.YEAR, Integer.parseInt("20"+fecha.substring(4, 6)));
            fecha_F = calendario.getTime();
        }catch (Exception e){
        }
        return fecha_F;
    }

    private static Double convertToWeight(String codigo) {

        try {
            if(codigo.length() ==4)
               return Double.parseDouble(codigo.substring(0, 2) + "." + codigo.substring(2, 4));
            return Double.parseDouble(codigo.substring(0, 4) + "." + codigo.substring(4, 6));
        } catch (Exception e) {
            System.out.println("Error en convertToWeight:" + codigo + " Info:" + e.getMessage());
        }
        return null;
    }

    public static boolean isNumber(Object obj) {
        String valor = obj.toString();
        try {
            Integer.parseInt(valor);
            return true;
        } catch (Exception e) {
            System.out.println("Error en isNumber:" + obj.toString() + " Info:" + e.getMessage());
            return false;
        }
    }

    public static boolean isDate(String fecha, boolean haveYear){
        Date fecha_F =  convertToDate(fecha, haveYear);
        return  fecha_F!=null;
    }

}