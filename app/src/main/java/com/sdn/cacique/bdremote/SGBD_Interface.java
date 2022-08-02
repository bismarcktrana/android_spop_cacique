package com.sdn.cacique.bdremote;

import android.os.AsyncTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class SGBD_Interface extends AsyncTask {
    public static SGBD_SoftlandERP V_SPOOL_ERP = new SGBD_SoftlandERP();
    public static SGBD_Produccion V_SPOOL_PRODUCTION = new SGBD_Produccion();
    public static SGBD_License V_SPOOL_LICENSE = new SGBD_License();

    public static Connection V_OBJECTCONECTION = null;
    public static PreparedStatement V_PREPAREDSTATEMENT =null;
    public static Statement V_STATEMENT;
    public static ResultSet V_RESULSET;

    public static String V_SQLQUERY;
    public static String V_SQLQUERYSELECT;
    public static String V_SQLQUERYFILTER;
    public static String V_SQLQUERYSAVE;
    public static String V_SQLQUERYEDIT;
    public static String V_SQLQUERYINSERT;
    public static String V_SQLQUERYSEARCH;
    public static String V_SQLQUERYPARAM;

    public static String V_ERROR_MESSAGE;

    @Override
    protected Object doInBackground(Object[] objects) {
        return null;
    }

}