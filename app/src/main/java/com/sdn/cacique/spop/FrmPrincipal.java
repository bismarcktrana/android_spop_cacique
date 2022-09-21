package com.sdn.cacique.spop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.sdn.bd.dao.host.TblOperador;
import com.sdn.bd.objeto.host.Operador;
import com.sdn.cacique.bdremote.BDOperacion_Update;
import com.sdn.cacique.util.ConfApp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public class FrmPrincipal extends AppCompatActivity {
    private TextView txtestado;
    private ImageView img_coneccion;
    private LinearLayout panelPresentacion;

    private ListView lv_usuario;
    ArrayAdapter<Operador> adapter_usuarios;
    TextInputEditText txtBuscarUsuario;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        System.out.println("Comienza en OnPostResume");

        mantenerBusqueda = true;
        ValidarEstadoConeccion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_principal);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ((TextView) findViewById(R.id.p02_registro)).setText(ConfApp.UUID_FROM_DEVICE);
        ((TextView) findViewById(R.id.p02_lblInfoEquipo)).setText(ConfApp.USER_DTS ?"dts": (ConfApp.OPERADORLOGEADO.getTipo()==1?"Supervisor":"Operador")  );
        ((TextView) findViewById(R.id.p02_lblinfooperador)).setText(ConfApp.USER_DTS ?"dts":ConfApp.OPERADORLOGEADO.getNombre());

        txtestado = (TextView) findViewById(R.id.p02_txt_conectado);
        img_coneccion = (ImageView) findViewById(R.id.p02_img_coneccion);

        ScrollView a =  findViewById(R.id.PnlMenuItem);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate( R.layout.pnl_modulos, null);
        a.addView (  dialogView);

        ((LinearLayout) dialogView.findViewById(R.id.optorden)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mantenerBusqueda = false;
                Intent nuevaPantalla = new Intent(FrmPrincipal.this, FrmOrden.class);
                startActivity(nuevaPantalla);
            }
        });

        ((LinearLayout) dialogView.findViewById(R.id.optlibre)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mantenerBusqueda = false;
                Intent nuevaPantalla = new Intent(FrmPrincipal.this, FrmContenedor.class);
                startActivity(nuevaPantalla);
            }
        });

        ((LinearLayout) dialogView.findViewById(R.id.optsincronizar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mantenerBusqueda = false;
                Intent nuevaPantalla = new Intent(FrmPrincipal.this, FrmSincronizar.class);
                startActivity(nuevaPantalla);
            }
        });

        ((LinearLayout) dialogView.findViewById(R.id.optusuario)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarListaUsuarios();
            }
        });

        ((LinearLayout) dialogView.findViewById(R.id.optdts)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mantenerBusqueda = false;
                Intent nuevaPantalla = new Intent(FrmPrincipal.this, FrmBaseDeDatos.class);
                startActivity(nuevaPantalla);
            }
        });

        if(!ConfApp.USER_DTS){
            RemoverVista(dialogView,R.id.optdts);
        }

        if(!ConfApp.USER_SUPERVISOR){
            RemoverVista(dialogView,R.id.optsincronizar);
            RemoverVista(dialogView,R.id.optlibre);
            RemoverVista(dialogView,R.id.optusuario);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.m02_action_salir) {
            cerrarSesion();
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarListaUsuarios() {
        final RelativeLayout panel = (RelativeLayout) LayoutInflater.from(FrmPrincipal.this).inflate(R.layout.frm_lista, null);
        AlertDialog.Builder componente = new AlertDialog.Builder(FrmPrincipal.this);

        final ImageButton btnAgrearUsuario = panel.findViewById(R.id.btnAgregar);
        btnAgrearUsuario.setVisibility((ConfApp.USER_DTS || ConfApp.USER_SUPERVISOR) ? View.VISIBLE : View.INVISIBLE);

        componente.setCancelable(false);

        final Vector<Operador> listusuario = TblOperador.obtenerRegistros(FrmPrincipal.this);
        adapter_usuarios = new ArrayAdapter<Operador>(this, R.layout.item_objeto, R.id.i05_lblnombre, Collections.list(listusuario.elements())){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                final Operador obj_orden = getItem(position);
                // Check if an existing view is being reused, otherwise inflate the view
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_objeto, parent, false);
                }

                ((TextView) convertView.findViewById(R.id.i05_lblnombre)).setText(obj_orden.getNombre());
                ((TextView) convertView.findViewById(R.id.i05_lbltipo)).setText(obj_orden.getTipo()==1?"SUPERVISOR":(obj_orden.getTipo()==0?"NO DEFINIDO":"OPERADOR"));

                return convertView;
            }
        };

        lv_usuario = panel.findViewById(R.id.list_view);
        lv_usuario.setTextFilterEnabled(true);
        lv_usuario.setAdapter(adapter_usuarios);
        txtBuscarUsuario = panel.findViewById(R.id.inputSearch);

        componente.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        txtBuscarUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FrmPrincipal.this.adapter_usuarios.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        componente.setView(panel);

        final AlertDialog FrmBuscar = componente.create();
        FrmBuscar.show();

        lv_usuario.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Operador item = (Operador) arg0.getItemAtPosition(position);
                mostrarFormularioUsuario(false, item, FrmBuscar);
                FrmBuscar.dismiss();
            }
        });

        btnAgrearUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarFormularioUsuario(true, new Operador(), FrmBuscar);
            }
        });
    }

    public void mostrarFormularioUsuario(Boolean Crear, final Operador operador, final AlertDialog frmBuscar) {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(FrmPrincipal.this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.pnl_usuario, null);
        dialogBuilder.setView(dialogView);

        final EditText txtusuario = dialogView.findViewById(R.id.pnl3_txtUsuario);
        final EditText txtclave = dialogView.findViewById(R.id.pnl3_txtClave);
        final EditText txtNombre = dialogView.findViewById(R.id.pnl3_txtNombre);
        final Spinner cbxCombo = dialogView.findViewById(R.id.pnl3_cbxTipo);

        List<String> categories = new ArrayList<String>();
        categories.add("Supervisor");
        categories.add("Operario");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbxCombo.setAdapter(dataAdapter);

        txtusuario.setText(operador.getUsuario());
        txtclave.setText(operador.getClave());
        txtNombre.setText(operador.getNombre());

        switch (operador.getTipo()) {
            case 2:
                cbxCombo.setSelection(1);
                break;
            default:
                cbxCombo.setSelection(0);
                break;
        }

        dialogBuilder.setIcon(R.drawable.img_logo);
        dialogBuilder.setTitle(Html.fromHtml("<font color='#FF7F27'>" + (operador.getId() == -1 ? "Crear Usuario" : (Crear) ? "Modificar Usuario" : "Ver Usuario") + "</font>"));
        dialogBuilder.setMessage("Informacion de Usuario");

        if (ConfApp.USER_DTS ||  ConfApp.USER_SUPERVISOR) {
            dialogBuilder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    operador.setUsuario(txtusuario.getText().toString());
                    operador.setClave(txtclave.getText().toString().trim());
                    operador.setNombre(txtNombre.getText().toString().trim());
                    operador.setTipo(cbxCombo.getSelectedItemPosition() == 0 ? 1 : 2);

                    if (operador.getId() == -1) {
                        if (txtNombre.getText().toString().trim().length() > 0 && txtusuario.getText().toString().trim().length() > 0 && txtclave.getText().toString().trim().length() > 0) {
                            if (TblOperador.guardar(getApplicationContext(), operador)) {
                                dialog.dismiss();
                                frmBuscar.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "Este usuario ya esta registrado", Toast.LENGTH_SHORT).show();
                            }
                        } else
                            Toast.makeText(getApplicationContext(), "Todos los datos son exigidos", Toast.LENGTH_SHORT).show();
                    } else {
                        if (txtNombre.getText().toString().trim().length() > 0 && txtusuario.getText().toString().trim().length() > 0 && txtclave.getText().toString().trim().length() > 0) {
                            if (TblOperador.modificar(getApplicationContext(), operador)) {
                                dialog.dismiss();
                                frmBuscar.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "Este usuario ya esta registrado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            });
        }

        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void RemoverVista(View contenedor, int vista){
        View namebar = contenedor.findViewById(vista);
        ViewGroup parent = (ViewGroup) namebar.getParent();
        if (parent != null) {
            parent.removeView(namebar);
        }
    }

    private void cerrarSesion() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Html.fromHtml("<small>Desea cerrar session?</small>"))
                .setIcon(R.drawable.img_logo)
                .setCancelable(false)
                .setTitle(Html.fromHtml("<font color='#FF7F27'><small>Confirmar cierre de sesion</small></font>"))
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("SI",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        mantenerBusqueda = false;
                                        onBackPressed();
                                    }
                                });
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////
    Boolean mantenerBusqueda = false;

    public void EstablecerIconoDeConexion(Object Value) {
        Boolean respuesta = Boolean.valueOf(Value.toString());

        ImageView img_coneccion = findViewById(R.id.p02_img_coneccion);
        img_coneccion.setImageResource(respuesta ? R.drawable.ic_action_wireless_enable : R.drawable.ic_action_wireless_disabled);
        TextView txtestado = findViewById(R.id.p02_txt_conectado);
        txtestado.setText(respuesta ? "Conectado" : "NO Conectado");
        //((TextView) findViewById(R.id.p02_lblultimaActulizacion)).setText(TblParametro.getClave(FrmPrincipal.this, "actualizado"));

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                ValidarEstadoConeccion();
            }
        }, 15000);
    }

    private void ValidarEstadoConeccion() {
        if (mantenerBusqueda) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("VALIDANDO CONECCION CON EL SERVIDOR");
                   // boolean estado = ConfApp.BDOPERATION.GetStatusConecctionSGBD_ERP();
                   // EstablecerIconoDeConexion(estado);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        new BDServidor(FrmPrincipal.this).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    } else {
                        new BDServidor(FrmPrincipal.this).execute();
                    }

                   /* BDOperacion operacion = new BDOperacion();
                    boolean respuesta =  operacion.GetStatusConecctionSGBD_PRODUCTION() && operacion.GetStatusConecctionSGBD_ERP();

                    EstablecerIconoDeConexion(Boolean.valueOf(respuesta));*/


                }
            });
        }
    }


    class BDServidor extends AsyncTask {
         Context referencia;

        public BDServidor(Context pantalla) {
            this.referencia=pantalla;

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return estatusServer();
        }

        private Object estatusServer() {
            BDOperacion_Update operacion  = new BDOperacion_Update();
            boolean respuesta =  operacion.GetStatusConecctionSGBD_PRODUCTION() && operacion.GetStatusConecctionSGBD_ERP();
            return respuesta;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            ((FrmPrincipal) referencia).EstablecerIconoDeConexion(Boolean.valueOf(o.toString()));
        }
    }

}