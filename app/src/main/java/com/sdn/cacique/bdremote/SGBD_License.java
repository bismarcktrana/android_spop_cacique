package com.sdn.cacique.bdremote;

import android.os.NetworkOnMainThreadException;

import com.sdn.cacique.util.ConfApp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SGBD_License {
    private static Connection V_OBJECTCONECTION = null;

    public SGBD_License() {
    }

    public static Connection getConnection() {
        try {
            if (V_OBJECTCONECTION == null || V_OBJECTCONECTION.isClosed())
                if(!abrir())
                    V_OBJECTCONECTION = null;
        } catch (SQLException e) {
            V_OBJECTCONECTION=null;
            //e.printStackTrace();
        }
        return V_OBJECTCONECTION;
    }

    public static boolean abrir(){
        // Create a variable for the connection string.
        String connectionUrl = "jdbc:jtds:sqlserver://"+ ConfApp.SERVER_LICENCIA +"/"+ConfApp.BDNAME_LICENCIA;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            DriverManager.setLoginTimeout(50);
            V_OBJECTCONECTION = DriverManager.getConnection(connectionUrl,ConfApp.BDUSER,ConfApp.BDPASS);

            return  (V_OBJECTCONECTION!=null);
        }// Handle any errors that may have occurred.
        catch (SQLException e) {
            System.out.println("Error Metodo abrir- SQLException BDSPollCOnexion "+e.getMessage());
            V_OBJECTCONECTION =null;
        }catch ( NetworkOnMainThreadException e){
            V_OBJECTCONECTION =null;
            System.out.println("Error  Metodo abrir - NetworkOnMainThreadException BDSPollCOnexion L45 "+e.getMessage());
        } catch (ClassNotFoundException e) {
            V_OBJECTCONECTION =null;
            e.printStackTrace();
        }
        return false;
    }

    public static void cerrar() {
        try {
            if (V_OBJECTCONECTION != null)
                V_OBJECTCONECTION.close();
        } catch (Exception e) {
            V_OBJECTCONECTION = null;
            System.out.println("Error Metodo Cerrar- Exception BDSPollCOnexion L55 " + e.getMessage());
        }
    }
}
