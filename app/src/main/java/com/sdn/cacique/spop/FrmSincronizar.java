package com.sdn.cacique.spop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.sdn.bd.dao.host.TblParametro;
import com.sdn.bd.secutidad.MD5;
import com.sdn.cacique.bdremote.BDOperacion_Update;
import com.sdn.cacique.util.ConfApp;

public class FrmSincronizar extends AppCompatActivity {
    Boolean PERMITIR_REGRESAR =false;

    private Button btnejecutar;
    private EditText ListaOperaciones;
    private ProgressBar simpleProgressBar;

    private String Operacion = "";

    private int Registros;
    private TextView lblInformacion;

    @Override
    public boolean onSupportNavigateUp() {
        if (PERMITIR_REGRESAR){
            onBackPressed();
            return false;
        }else{
            Toast.makeText(getApplicationContext(),"Sincronizacion en progreso, por favor espere", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        if (PERMITIR_REGRESAR){
            super.onBackPressed();
        }else{
            Toast.makeText(getApplicationContext(),"Sincronizacion en progreso, por favor espere", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_sincronizar);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        btnejecutar = (Button)findViewById(R.id.p05_btnejecutar);

        simpleProgressBar=(ProgressBar)findViewById(R.id.p05_progressBar); // initiate the progress bar
        simpleProgressBar.setVisibility(View.INVISIBLE);

        lblInformacion = findViewById(R.id.p05_lblInfo);

        ListaOperaciones = (EditText) findViewById(R.id.P05_lvinfo);
        ListaOperaciones.setScroller(new Scroller(getApplicationContext()));
        ListaOperaciones.setKeyListener(null);
        ListaOperaciones.setVerticalScrollBarEnabled(true);
        updateLista();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnu_sincronizar, menu);
        ConfApp.PERMISOS=ConfApp.PERMISOS;

        System.out.println("ConfApp.IMPORT_POLICIES"+ConfApp.IMPORT_POLICIES);
        menu.findItem(R.id.m05_item_producto).setVisible(ConfApp.IMPORT_POLICIES);
        menu.findItem(R.id.m05_item_pedido).setVisible(ConfApp.IMPORT_POLICIES);
        menu.findItem(R.id.m05_item_bodega).setVisible(ConfApp.IMPORT_POLICIES);
        menu.findItem(R.id.m05_item_camion).setVisible(ConfApp.IMPORT_POLICIES);
        menu.findItem(R.id.m05_item_conductor).setVisible(ConfApp.IMPORT_POLICIES);
        menu.findItem(R.id.m05_item_operador).setVisible(ConfApp.IMPORT_POLICIES);

      menu.findItem(R.id.m05_item_producto).setChecked(ConfApp.PERMISOS[0]);
        menu.findItem(R.id.m05_item_pedido).setChecked(ConfApp.PERMISOS[1]);
        menu.findItem(R.id.m05_item_bodega).setChecked(ConfApp.PERMISOS[2]);
        menu.findItem(R.id.m05_item_camion).setChecked(ConfApp.PERMISOS[3]);
        menu.findItem(R.id.m05_item_conductor).setChecked(ConfApp.PERMISOS[4]);
        menu.findItem(R.id.m05_item_operador).setChecked(ConfApp.PERMISOS[5]);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.m05_item_producto)
            ConfApp.PERMISOS[0]=!item.isChecked();

        if (id == R.id.m05_item_pedido)
            ConfApp.PERMISOS[1]=!item.isChecked();

        if (id == R.id.m05_item_bodega)
            ConfApp.PERMISOS[2]=!item.isChecked();

        if (id == R.id.m05_item_camion)
            ConfApp.PERMISOS[3]=!item.isChecked();

        if (id == R.id.m05_item_conductor)
            ConfApp.PERMISOS[4]=!item.isChecked();

        if (id == R.id.m05_item_operador)
            ConfApp.PERMISOS[5]=!item.isChecked();

        item.setChecked(!item.isChecked());

        String SP= TblParametro.getClave(FrmSincronizar.this, "CARACTER_SPLIT");

        String QUERY = String.valueOf(ConfApp.PERMISOS[0])+SP;
        QUERY += String.valueOf(ConfApp.PERMISOS[1])+SP;
        QUERY += String.valueOf(ConfApp.PERMISOS[2])+SP;
        QUERY += String.valueOf(ConfApp.PERMISOS[3])+SP;
        QUERY += String.valueOf(ConfApp.PERMISOS[4])+SP;
        QUERY += String.valueOf(ConfApp.PERMISOS[5]);
        TblParametro.modificar(FrmSincronizar.this,"PARTIAL_IMPORT_POLICIES", MD5.Encriptar(FrmSincronizar.this, QUERY ));
        System.out.println("VALOR A GUARDAR:"+QUERY);
        return super.onOptionsItemSelected(item);
    }

    public void Recibir(View v) {
        if (PERMITIR_REGRESAR){
            AlertDialog.Builder builder = new AlertDialog.Builder(FrmSincronizar.this);
            builder.setIcon(R.drawable.img_logo)
                    .setTitle(Html.fromHtml("<font color='#FF7F27'>IMPORTAR DATOS<</font>"))
                    .setMessage(Html.fromHtml("Desea recibir(Importar) la datos ?"))
                    .setCancelable(false)
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("SI",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    PERMITIR_REGRESAR = false;
                                    btnejecutar.setEnabled(false);
                                    simpleProgressBar.setVisibility(View.VISIBLE);
                                    SincronizarconelServidor("RECIBIR");
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            Toast.makeText(getApplicationContext(),"Sincronizacion en progreso, por favor espere", Toast.LENGTH_LONG).show();
        }
    }

    public void Enviar(View v){
        if (PERMITIR_REGRESAR){
            AlertDialog.Builder builder = new AlertDialog.Builder(FrmSincronizar.this);
            builder.setIcon(R.drawable.img_logo)
                    .setTitle(Html.fromHtml("<font color='#FF7F27'>ENVIAR  DATOS</font>"))
                    .setMessage(Html.fromHtml("Desea enviar(Exportar) la informacion ?"))
                    .setCancelable(false)
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("SI",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    PERMITIR_REGRESAR = false;
                                    btnejecutar.setEnabled(false);
                                    simpleProgressBar.setVisibility(View.VISIBLE);
                                    SincronizarconelServidor("ENVIAR");
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }else{
            Toast.makeText(getApplicationContext(),"Sincronizacion en progreso, por favor espere", Toast.LENGTH_LONG).show();
        }
    }

    private void updateLista(){
        String temp = new String();
        ListaOperaciones.setText("");
        temp+="HH: ["+ ConfApp.UUID_DESENCRYPTED;
        temp+="] V 1.0";
        ((TextView) findViewById(R.id.p05_lblInfo)).setText(temp);
        PERMITIR_REGRESAR =true;
    }

    private void SincronizarconelServidor(final String value){

        ConfApp.BDOPERATION_SINCRONIZAR.referencia=FrmSincronizar.this;
        ConfApp.BDOPERATION_SINCRONIZAR.Tipo =value;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ConfApp.BDOPERATION_SINCRONIZAR.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        } else {
            ConfApp.BDOPERATION_SINCRONIZAR.execute();
        }

    }

    public void printToas(String mensaje) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               // Toast.makeText(FrmSplash.this, mensaje, Toast.LENGTH_SHORT).show();
                appendMessage(mensaje);
            }
        });
    }

    public void appendMessage(final String mensaje) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListaOperaciones.append(Html.fromHtml(mensaje + "<BR>"));
                ListaOperaciones.setSelection(ListaOperaciones.getText().length());
            }
        });
    }

    public void Progresbar_config(final Integer inicia, final Integer finaliza, final boolean indeterminado) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                simpleProgressBar.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    simpleProgressBar.setMin(inicia);
                }
                simpleProgressBar.setMax(finaliza);
                simpleProgressBar.setProgress(0);
                simpleProgressBar.setIndeterminate(indeterminado);
                Registros = finaliza;
                lblInformacion.setText("");
            }
        });
    }

    public void Progresbar_refres(final Integer progreso, final Integer Importados, final boolean MostrarEstadisticaFinal) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                simpleProgressBar.setProgress(progreso);
                lblInformacion.setText(( Operacion.equals("RECIBIR")?"Importando registro ":"Exportando registro " ) + progreso + " de " + Registros);
                if (MostrarEstadisticaFinal) {
                    appendMessage(( Operacion.equals("RECIBIR")?"Se recibiero ":"Se enviaron ")+ Importados + " registros de " + Registros);
                    appendMessage(ConfApp.LINE_DIVISOR);
                    lblInformacion.setText("");
                }
            }
        });
    }

    public void notificarFinalizacion(boolean CONECTION_STATE) {
        StringBuilder mensaje = new StringBuilder();
        PERMITIR_REGRESAR = true;
        btnejecutar.setEnabled(true);
        simpleProgressBar.setVisibility(View.INVISIBLE);

        if (CONECTION_STATE) {
            mensaje.append("<B><font color='#006600'>");
            mensaje.append(Operacion.equals("ENVIAR") ?"ENVIO FINALIZADO":"IMPORTACION FINALIZADO<BR>CIERRE SESION PARA APLICACION CAMBIOS");
            mensaje.append("</font></B><BR>");
        } else {
            mensaje.append("<B><font color='#AB2A3E'>");
            mensaje.append("SERVIDOR NO DISPONIBLE");
            mensaje.append("<BR>");
            mensaje.append( (Operacion.equals("ENVIAR") ? "ENVIO" : "IMPORTACION")+" LOCAL");
            mensaje.append("</font></B><BR>");
        }
        mensaje.append(ConfApp.LINE_DIVISOR);
        appendMessage(mensaje.toString());
        ConfApp.BDOPERATION_SINCRONIZAR =new BDOperacion_Update();
    }
}