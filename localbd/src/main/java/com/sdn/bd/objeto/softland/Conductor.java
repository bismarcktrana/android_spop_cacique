package com.sdn.bd.objeto.softland;

//SELECT EMPLEADO,IDENTIFICACION, NOMBRE  FROM [CtrlSoftland].[MATCASA].[EMPLEADO] WHERE  puesto IN ('0060', '0064','0029', '0015', '0016', '0051') and activo ='S' and nomina not in (999) ORDER BY NOMBRE ASC
public class Conductor {
    String id=null;
    String codigo;
    String Nombre="";

    public Conductor() {
        this.id=null;
    }

    public Conductor(String id, String codigo, String nombre) {
        this.id = id;
        this.codigo = codigo;
        Nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    @Override
    public String toString() {
        return Nombre ;
    }

    public String toString2() {
        return "Conductor{" +
                "id='" + id + '\'' +
                ", codigo='" + codigo + '\'' +
                ", Nombre='" + Nombre + '\'' +
                '}';
    }
}
