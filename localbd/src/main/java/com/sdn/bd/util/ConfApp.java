package com.sdn.bd.util;

import com.sdn.bd.host.BDLocal;

import java.text.SimpleDateFormat;

public class ConfApp {
    ///////////////////////////////////////////////////////  PARAMETROS DE TIEMPO Y FECHA DEL SISTEMA  ///////////////////////////////////////////

    public static SimpleDateFormat ISO8601_FORMATTER;

    public static final String ISO8601_FORMATDATE_BD = "yyyy-MM-dd";
    public static final String ISO8601_FORMATDATE_APP = "dd/MM/yyyy";

    public static final String ISO8601_FORMATTIMESTAMP_BD = "yyyy-MM-dd HH:mm:ss";//HH:MM:SS
    public static final String ISO8601_FORMATTIMESTAMP_APP = "dd/MM/yyyy hh:mm:ss aaa";

    public static final String ISO8601_FORMATHOUR_BD = "HH:MM:SS";
    public static final String ISO8601_FORMATHOUR_APP = "hh:mm:ss aaa";

}
