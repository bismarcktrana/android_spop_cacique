package com.sdn.bd.host;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by bismarck on 12/01/2019
 * Modify by bismarck on 18/06/2022
 */

public class BDLocal {
    private static String BD_NAME = "dbcacique.s3db";
    public static SQLiteDatabase BD_SQLITE=null;
    private static String ubicacionDeTrabajo="";


    public static void copyBDFromSambox(Context pantalla,String Path_destino) {
        File PATH_BD =new File(Path_destino,BD_NAME);

        if(!PATH_BD.exists()) {
            InputStream myInput;
            OutputStream myOutput;

            try {

                myInput = pantalla.getAssets().open(BD_NAME, Context.MODE_PRIVATE);
                myOutput = new FileOutputStream(PATH_BD);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = myInput.read(buffer)) > 0) {
                    myOutput.write(buffer, 0, length);
                }

                myOutput.flush();
                myOutput.close();
                myInput.close();

            } catch (IOException ex) {
                Toast.makeText(pantalla, "Error en copyBDFromSambox:["+ex.getMessage()+"]", Toast.LENGTH_LONG).show();
            }
        }
        Toast.makeText(pantalla, "Base de datos preparada", Toast.LENGTH_LONG).show();
        ubicacionDeTrabajo = PATH_BD.getAbsolutePath();
    }

    public static SQLiteDatabase abrir(Context pantalla) {
        if(BD_SQLITE==null)
            BD_SQLITE = pantalla.openOrCreateDatabase(ubicacionDeTrabajo, Context.MODE_PRIVATE, null);

        return BD_SQLITE;
    }

    public static String getUbicacion(){
        return BD_SQLITE.getPath();
    }

    public static void cerrar() {
        if (BD_SQLITE != null){
            BD_SQLITE.close();
            BD_SQLITE=null;
        }

    }
}
