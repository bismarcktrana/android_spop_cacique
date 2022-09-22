package com.sdn.bd.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.sdn.bd.host.BDLocal;


public class Utils {

    public static String createWorkDirectory(Context pantalla, String DirectoryName) {
        String PATH_DIRECTORYWORK = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + DirectoryName.replace(" ","_") ;//+ File.separator
        File DIRECTORY = new File(PATH_DIRECTORYWORK);

        try {
            if (!DIRECTORY.exists())
                DIRECTORY.mkdirs();

            File out = new File(DIRECTORY, "README.txt");
            FileOutputStream outputStream = new FileOutputStream(out);
            outputStream.write("CARPETA PARA  SUBIR Y BAJAR ARCHIVOS".getBytes());
            outputStream.flush();
            outputStream.close();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(out);
                mediaScanIntent.setData(contentUri);
                pantalla.sendBroadcast(mediaScanIntent);
            } else {
                pantalla.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(out.getAbsolutePath())));
            }
        } catch (Exception e) {
            Toast.makeText(pantalla, "Direccion:[" + PATH_DIRECTORYWORK + "], Message:[" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
        }
        //BDLocal.copyBDFromSambox(pantalla,PATH_DIRECTORYWORK);
        return PATH_DIRECTORYWORK;
    }


    /**
     * FUNCION QUE CONVIERTE UNA CADENA yyyy-MM-dd HH:mm:ss a Date
     *
     * @param
     * @return
     */
    public static Date Convert_BDTimeStamp_To_Date(String cadena) {
        /*ConfApp.ISO8601_FORMATTER = new SimpleDateFormat(ConfApp.ISO8601_FORMATTIMESTAMP_BD);
        try {
            return cadena == null ? null : ConfApp.ISO8601_FORMATTER.parse(cadena);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;*/
        return  Convert_BDDate_To_CustomFormat(cadena,ConfApp.ISO8601_FORMATTIMESTAMP_BD,"Convert_BDTimeStamp_To_Date");
    }

    /**
     * FUNCION QUE CONVIERTE UNA CADENA yyyy-MM-dd a Date
     *
     * @param
     * @return
     */
    public static Date Convert_BDDate_To_Date(String cadena) {
        /*ConfApp.ISO8601_FORMATTER = new SimpleDateFormat(ConfApp.ISO8601_FORMATDATE_BD);

        if (cadena != null) {
            try {
                return ConfApp.ISO8601_FORMATTER.parse(cadena);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            System.out.println("Convert_BDDate_To_Date null:" +cadena);
        }
        return null;*/
        return  Convert_BDDate_To_CustomFormat(cadena,ConfApp.ISO8601_FORMATDATE_BD,"Convert_BDDate_To_Date");
    }

    public static Date Convert_BDDate_To_CustomFormat(String cadena,String format,String Metodo) {
        ConfApp.ISO8601_FORMATTER = new SimpleDateFormat(format);

        if (cadena != null) {
            try {
                return ConfApp.ISO8601_FORMATTER.parse(cadena);
            } catch (ParseException e) {
                System.out.println("Error al convertir la cadena "+ cadena +" en :" +Metodo+"="+e.getMessage());
                e.printStackTrace();
            }
        }else{
            System.out.println("la cadena en :" +Metodo+"=null ");
        }
        return null;
    }


    /**
     * ESTE METODO CONVIERTE UNA FECHA DE LA LIBRERIA java.util.Date a un formato a un formato permitido por sqlite y devuelto en forma de cadena
     *
     * @param fecha
     * @return
     */
    public static String Convert_Date_To_DBTimeStamp(Date fecha) {
        //ConfApp.ISO8601_FORMATTER = new SimpleDateFormat(ConfApp.ISO8601_FORMATTIMESTAMP_BD);
        //return fecha == null ? null : ConfApp.ISO8601_FORMATTER.format(new java.sql.Date(fecha.getTime()));
        return Convert_Date_To_DBCustomFormat(fecha,ConfApp.ISO8601_FORMATTIMESTAMP_BD,"Convert_Date_To_DBTimeStamp");
    }

    /**
     * ESTE METODO CONVIERTE UNA FECHA DE LA LIBRERIA java.util.Date a un formato a un formato permitido por sqlite y devuelto en forma de cadena
     *
     * @param fecha
     * @return
     */
    public static String Convert_Date_To_DBDate(Date fecha) {
        //ConfApp.ISO8601_FORMATTER = new SimpleDateFormat(ConfApp.ISO8601_FORMATDATE_BD);
        //return fecha == null ? null : ConfApp.ISO8601_FORMATTER.format(fecha);
        return Convert_Date_To_DBCustomFormat(fecha,ConfApp.ISO8601_FORMATDATE_BD,"Convert_Date_To_DBDate");
    }

    /**
     * ESTE METODO CONVIERTE UNA FECHA DE LA LIBRERIA java.util.Date a un formato a un formato establecido por el usuario compatible con sqlite y devuelto en forma de cadena
     *
     * @param fecha
     * @return
     */
    public static String Convert_Date_To_DBCustomFormat(Date fecha, String format) {
        //ConfApp.ISO8601_FORMATTER = new SimpleDateFormat(format);
        //return fecha == null ? null : ConfApp.ISO8601_FORMATTER.format(new java.sql.Date(fecha.getTime()));
        return Convert_Date_To_DBCustomFormat(fecha,format,"Convert_Date_To_DBCustomFormat");
    }

    private static String Convert_Date_To_DBCustomFormat(Date fecha, String format,String Metodo) {
        String resultado =null;
        ConfApp.ISO8601_FORMATTER = new SimpleDateFormat(format);

        if(fecha !=null){
            try {
                resultado = ConfApp.ISO8601_FORMATTER.format(new java.sql.Date(fecha.getTime()));
            } catch (Exception e) {
                System.out.println("Exception en el metodo " +Metodo+" "+e.getMessage());
                e.printStackTrace();
            }
        }else
            System.out.println("Error en el metodo " +Metodo+" fecha=null");

        return resultado;
    }

    public static String Convert_Date_To_AppTimeStamp(Date fecha) {
        ConfApp.ISO8601_FORMATTER = new SimpleDateFormat(ConfApp.ISO8601_FORMATTIMESTAMP_APP);
        return fecha == null ? null : ConfApp.ISO8601_FORMATTER.format(fecha);
    }

    public static String Convert_Date_To_AppDate(Date fecha) {
        ConfApp.ISO8601_FORMATTER = new SimpleDateFormat(ConfApp.ISO8601_FORMATDATE_APP);
        return fecha == null ? null : ConfApp.ISO8601_FORMATTER.format(fecha);
    }

    public static String Convert_Date_To_AppHour(Date fecha) {
        ConfApp.ISO8601_FORMATTER = new SimpleDateFormat(ConfApp.ISO8601_FORMATHOUR_APP);
        return fecha == null ? null : ConfApp.ISO8601_FORMATTER.format(fecha);
    }


    public static String convertArrayToCSV(Object[] array, String caracter) {
        String result = "";

        if (array.length > 0) {
            StringBuilder sb = new StringBuilder();

            for (Object s : array) {
                sb.append(s.toString()).append(caracter);
            }

            result = sb.deleteCharAt(sb.length() - 1).toString();
        }
        return result;
    }

    public void CopyBDFromSambox(Context pantalla, String pathDirectorywork) {
      BDLocal.copyBDFromSambox(pantalla, pathDirectorywork);
    }
}