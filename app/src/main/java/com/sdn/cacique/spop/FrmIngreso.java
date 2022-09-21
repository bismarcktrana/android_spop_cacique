package com.sdn.cacique.spop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sdn.bd.dao.host.TblOperador;
import com.sdn.bd.dao.host.TblParametro;
import com.sdn.bd.objeto.host.Operador;
import com.sdn.bd.secutidad.MD5;
import com.sdn.cacique.util.ConfApp;

public class FrmIngreso extends AppCompatActivity {

    private TextView lblCliente,lblMensaje;
    private EditText txtusuario,txtClave;
    private Button btnIngresar;

    @Override
    protected void onPostResume() {//SE EJECUTA DESPUES DE ONCREATE
        super.onPostResume();
        ConfApp.loadParameters(FrmIngreso.this);
        lblCliente.setText(Html.fromHtml("<CENTER><B>MATADERO CACIQUE</B></CENTER>"));
        actualizarMensajeLicencia();
        limpiarFormulario();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConfApp.DEBUG=true;
        ConfApp.createDirectoryWork(FrmIngreso.this,getResources().getString(R.string.app_name));
        ConfApp.WriteParameter(FrmIngreso.this);

        ConfApp.loadParameters(FrmIngreso.this);
        ConfApp.loadConection();

        setContentView(R.layout.frm_ingreso);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        lblCliente = (TextView) findViewById(R.id.p1_lblNombreCliente);
        lblMensaje =(TextView) findViewById(R.id.p1_lblMensaje);
        txtusuario = (EditText) findViewById(R.id.p1_txtUsuario);
        txtClave = (EditText) findViewById(R.id.p1_txtClave);

        btnIngresar = (Button) findViewById(R.id.p1_btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ConfApp.DEVICEAUTORIZED) {
                    continuarComprobacion();
                }else{
                    Toast.makeText(FrmIngreso.this, String.format(getResources().getString(R.string.p1_lblDispositivoNoAutorizado),ConfApp.UUID_FROM_DEVICE), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnu_ingreso, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m01_action_salir:
                cerrarApliacion();
                return true;
            case R.id.m01_action_parameter:
                Leerparametro();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void continuarComprobacion() {
        String Usuario =txtusuario.getText().toString().trim();
        String Clave = txtClave.getText().toString().trim();

        if(!Usuario.isEmpty() && !Clave.isEmpty()){

            if(Usuario.equals(ConfApp.SYSTEM_USER) && Clave.equals(ConfApp.SYSTEM_PASS)){
                ConfApp.USER_DTS=true;
                ConfApp.USER_SUPERVISOR =true;
                ConfApp.OPERADORLOGEADO = new Operador(Usuario,Clave,Usuario,1);
                Intent nuevaPantalla = new Intent(FrmIngreso.this, FrmPrincipal.class);
                startActivity(nuevaPantalla);
            }else{
                ConfApp.OPERADORLOGEADO = TblOperador.obtenerRegistroXUsuarioyClave(FrmIngreso.this, Usuario, Clave);
                txtusuario.setText("");
                txtClave.setText("");

                if (ConfApp.OPERADORLOGEADO.getId() != -1) {
                    ConfApp.USER_DTS=false;
                    ConfApp.USER_SUPERVISOR = ConfApp.OPERADORLOGEADO.getTipo() == 1;
                    ConfApp.USER_ESTIBADOR = ConfApp.OPERADORLOGEADO.getTipo() == 2;

                    Intent nuevaPantalla = new Intent(FrmIngreso.this, FrmPrincipal.class);
                    startActivity(nuevaPantalla);
                }
            }
        }
    }

    public void Leerparametro() {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(FrmIngreso.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.pnl_parametro, null);
        dialogBuilder.setView(dialogView);

        final EditText txtparametro = (EditText) dialogView.findViewById(R.id.pnl1_txtParametro);
        final EditText txtclave = (EditText) dialogView.findViewById(R.id.pnl1_txtValor);
        txtparametro.setOnClickListener(null);
        txtclave.setOnClickListener(null);

        dialogBuilder.setTitle(getResources().getString(R.string.p1_lblConfigurar).toUpperCase());
        //((TextView) findViewById(R.id.p1_lblMensaje)).setText(ConfApp.DEVICEAUTORIZED ? "" :  );

        if(!ConfApp.DEVICEAUTORIZED)
            dialogBuilder.setMessage(String.format(getResources().getString(R.string.p1_lblDispositivoNoAutorizado),ConfApp.UUID_FROM_DEVICE));

        dialogBuilder.setIcon(R.drawable.img_logo);

        dialogBuilder.setPositiveButton(getResources().getString(R.string.lblGuardar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String TEMP_PARAMETRO = txtparametro.getText().toString().trim();
                String TEMP_VALOR = txtclave.getText().toString().trim();

                if(TblParametro.getClave(getApplication(),TEMP_PARAMETRO).isEmpty()){
                    Toast.makeText(FrmIngreso.this ,String.format(getResources().getString(R.string.lblParametroNoExiste),TEMP_PARAMETRO),Toast.LENGTH_LONG).show();
                }else{
                    if(TEMP_PARAMETRO.equals("UUID") || TEMP_PARAMETRO.equals("SERVER") ||
                            TEMP_PARAMETRO.equals("BDNAMECATALOG") ||TEMP_PARAMETRO.equals("BDUSER") ||
                            TEMP_PARAMETRO.equals("BDPASS") ||TEMP_PARAMETRO.equals("SYSTEM_USER")||
                            TEMP_PARAMETRO.equals("SYSTEM_PASS")||TEMP_PARAMETRO.equals("user_policies")||
                            TEMP_PARAMETRO.equals("register_policies")||TEMP_PARAMETRO.equals("export_policies")||
                            TEMP_PARAMETRO.equals("import_policies")||TEMP_PARAMETRO.equals("partial_import_policies"))
                        TEMP_VALOR = MD5.Encriptar(FrmIngreso.this,TEMP_VALOR);

                    if(TblParametro.modificar(getApplicationContext(),TEMP_PARAMETRO,TEMP_VALOR)){
                        Toast.makeText(FrmIngreso.this ,String.format( getResources().getString(R.string.lblParametroModificad),TEMP_PARAMETRO),Toast.LENGTH_LONG).show();
                        ConfApp.loadParameters(FrmIngreso.this);
                    }

                    actualizarMensajeLicencia();
                    limpiarFormulario();
                }
            }
        });
        dialogBuilder.setNegativeButton(getResources().getString(R.string.lblCancelar), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
        txtparametro.requestFocus();
    }

    private void cerrarApliacion() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setMessage(Html.fromHtml("<small>Desea cerrar la Aplicacion..?</small>"))
                .setIcon(R.drawable.img_logo)
                .setCancelable(false)
                .setTitle(Html.fromHtml("<font color='#FF7F27'><small>Confirmar cierre de programa</small></font>"))
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("SI",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                            }
                        });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private void limpiarFormulario(){
        txtusuario.setText("");
        txtClave.setText("");
        txtusuario.requestFocus();
    }

    private void actualizarMensajeLicencia() {
        lblMensaje.setText(ConfApp.DEVICEAUTORIZED ? String.format(getResources().getString(R.string.p1_lblDispositoAutorizado),ConfApp.UUID_FROM_DEVICE)  :  String.format(getResources().getString(R.string.p1_lblDispositivoNoAutorizado),ConfApp.UUID_FROM_DEVICE));
        lblMensaje.setTextColor(ConfApp.DEVICEAUTORIZED ? getResources().getColor(R.color.green) : getResources().getColor(R.color.md_theme_light_error ));
    }
}