package com.sdn.cacique.spop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.sdn.bd.dao.host.TblLectura;
import com.sdn.bd.dao.host.TblLecturaEliminada;
import com.sdn.bd.dao.produccion.TblCamion;
import com.sdn.bd.dao.produccion.TblProducto;
import com.sdn.bd.dao.softland.TblBodega;
import com.sdn.bd.dao.softland.TblConductor;
import com.sdn.bd.dao.softland.TblPedido;
import com.sdn.bd.dao.softland.TblPedido_Detalle;
import com.sdn.bd.objeto.host.Lectura;
import com.sdn.bd.objeto.produccion.Camion;
import com.sdn.bd.objeto.produccion.Producto;
import com.sdn.bd.objeto.softland.Conductor;
import com.sdn.bd.objeto.softland.Pedido;
import com.sdn.cacique.bdremote.BDOperacion_Update;
import com.sdn.cacique.util.ConfApp;
import com.sdn.cacique.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class FrmOrden extends AppCompatActivity {
    ArrayList<Pedido> ordenes = new ArrayList<Pedido>();
    public ArrayAdapter<Pedido> Adapterordenes;
    TextInputEditText inputSearch;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        updateLista();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnu_orden, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.m03_action_actualizar:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    new BDPedido_actualizar(FrmOrden.this).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                } else {
                    new BDPedido_actualizar(FrmOrden.this).execute();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_orden);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ListView lv_listaordenes = findViewById(R.id.p03_listadeordenes);

        lv_listaordenes.setTextFilterEnabled(true);

        Adapterordenes = new ArrayAdapter<Pedido>(this, R.layout.item_pedido, R.id.i04_lblPedidonombre, ordenes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                final Pedido obj_orden = getItem(position);
                // Check if an existing view is being reused, otherwise inflate the view
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_pedido, parent, false);
                }
                // Lookup view for data population
                ((TextView) convertView.findViewById(R.id.i04_lblClientenombre)).setText(obj_orden.getCliente());
                ((TextView) convertView.findViewById(R.id.i04_lblPedidonombre)).setText(obj_orden.getId());
                ((TextView) convertView.findViewById(R.id.i04_lblFechanombre)).setText(obj_orden.getFecha() == null ? "" : Utils.bd.C_DateToAppFormat(obj_orden.getFecha()));

                ((TextView) convertView.findViewById(R.id.i04_lblcamionnombre)).setText(obj_orden.getCamion().getId() == null ? "" : TblCamion.obtenerRegistro(FrmOrden.this, obj_orden.getCamion().getId()).getNoplaca());

                ((TextView) convertView.findViewById(R.id.i04_lblConductorNombre)).setText(obj_orden.getConductor().getId() == null ? "" : TblConductor.obtenerRegistro(FrmOrden.this, obj_orden.getConductor().getId()).getNombre());

                ((TextView) convertView.findViewById(R.id.i04_lblBodeganombre)).setText(obj_orden.getBodega().getId().length() < 1 ? "" : TblBodega.obtenerRegistro(FrmOrden.this, obj_orden.getBodega().getId()).getNombre());

                ((TextView) convertView.findViewById(R.id.i04_lblMarchamoNombre)).setText("" + obj_orden.getMarchamo());

                String PROCESS_NAME="PENDIENTE";
                Drawable PROCESS_COLOR=((TextView) convertView.findViewById(R.id.i04_lblEstadonombre)).getBackground();//getResources().getDrawable(R.color.md_theme_light_primary);
                Integer PROCESS_TEXT_COLOR=getResources().getColor(R.color.md_theme_light_primary);//getResources().getColor(R.color.md_theme_light_primary);

                if(obj_orden.getAtentido()){
                    if(obj_orden.getFecha_inicio() != null){
                        if(obj_orden.getFecha_fin()==null){
                            if(TblLectura.obtenerNoFilas(FrmOrden.this,obj_orden.getId())>0){
                                PROCESS_NAME="EN PROCESO";
                                PROCESS_COLOR = getResources().getDrawable(R.color.yellow);
                                PROCESS_TEXT_COLOR = getResources().getColor(R.color.md_theme_light_secondary);
                            }else{
                                PROCESS_NAME="ABIERTA";
                                PROCESS_COLOR = getResources().getDrawable(R.color.lightyellow);
                                PROCESS_TEXT_COLOR = getResources().getColor(R.color.md_theme_light_secondary);
                            }
                        }else{
                            PROCESS_NAME="FINALIZADO";
                            PROCESS_COLOR = getResources().getDrawable(R.color.green);
                            PROCESS_TEXT_COLOR = getResources().getColor(R.color.md_theme_light_onPrimary);
                        }
                    }else{
                        // PROCESS_NAME="PENDIENTE";
                        // PROCESS_COLOR = ((TextView) convertView.findViewById(R.id.i04_lblEstadonombre)).getBackground();
                        // PROCESS_TEXT_COLOR = getResources().getColor(R.color.md_theme_light_primary);
                    }
                }else{//NO ESTA SIENDO ATENDIDO
                   // PROCESS_NAME="PENDIENTE";
                   // PROCESS_COLOR = ((TextView) convertView.findViewById(R.id.i04_lblEstadonombre)).getBackground();
                   // PROCESS_TEXT_COLOR = getResources().getColor(R.color.md_theme_light_primary);
                }

                ((TextView) convertView.findViewById(R.id.i04_lblEstadonombre)).setText(PROCESS_NAME);
                ((TextView) convertView.findViewById(R.id.i04_lblEstadonombre)).setTextColor(PROCESS_TEXT_COLOR);
                convertView.findViewById(R.id.i04_lblEstadonombre).setBackground(PROCESS_COLOR);


                /*if (!obj_orden.getAtentido() && obj_orden.getFecha_inicio() == null && obj_orden.getFecha_fin() == null) {
                    ((TextView) convertView.findViewById(R.id.i04_lblEstadonombre)).setText("PENDIENTE");
                    convertView.findViewById(R.id.i04_lblEstadonombre).setBackground(convertView.getBackground());
                }

                if (!obj_orden.getAtentido() && obj_orden.getFecha_inicio() != null && obj_orden.getFecha_fin() == null) {
                    ((TextView) convertView.findViewById(R.id.i04_lblEstadonombre)).setText("EN PROCESO");
                    convertView.findViewById(R.id.i04_lblEstadonombre).setBackground(getResources().getDrawable(R.color.yellow));
                }

                if (obj_orden.getAtentido() && obj_orden.getFecha_fin() != null) {
                    ((TextView) convertView.findViewById(R.id.i04_lblEstadonombre)).setText("FINALIZADO");
                    ((TextView) convertView.findViewById(R.id.i04_lblEstadonombre)).setTextColor(Color.WHITE);
                    convertView.findViewById(R.id.i04_lblEstadonombre).setBackground(getResources().getDrawable(R.color.green));
                }*/

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showMenuAdapterAlertDialog(obj_orden);
                    }
                });

                return convertView;
            }
        };

        Adapterordenes.setDropDownViewResource(R.layout.item_pedido);
        lv_listaordenes.setAdapter(Adapterordenes);

        inputSearch = findViewById(R.id.p03_txtbuscarorden);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Adapterordenes.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        updateLista();
    }

    public void updateLista() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                ArrayList<Pedido> temp = TblPedido.obtenerRegistros(FrmOrden.this);//.obtenerRegistrosSimplificado(FrmOrdenes.this); //TblOrden.obtenerRegistros(FrmOrdenes.this,1, ConfApp.FrenteActual.getId());
                ordenes.clear();
                Adapterordenes.notifyDataSetChanged();

                for (int it = 0; it < temp.size(); it++)
                    ordenes.add(temp.get(it));

                Adapterordenes.notifyDataSetChanged();
                getSupportActionBar().setTitle(ordenes.size() + " ORDENES");
            }
        });
    }

    // Show how to use SimpleAdapter to customize list item in android alert dialog.
    private void showMenuAdapterAlertDialog(final Pedido obj_orden) {
        // Image and text item data's key.
        String CUSTOM_ADAPTER_IMAGE = "image";
        String CUSTOM_ADAPTER_TEXT = "text";
        ArrayList<Lectura> lectura_temp = TblLectura.obtenerRegistrosXOrden(FrmOrden.this, obj_orden.getId());

        ArrayList<String> operacion = new ArrayList<String>();
        ArrayList<Integer> Iconoperacion = new ArrayList<Integer>();

        //NO ORDEN NO ESTA FINALIZADA SE PUEDE  LA TRABAJAR ORDEN
        if (obj_orden.getFecha_fin() == null) {//obj_orden.getIdservidor() == 0 &&
            operacion.add(getResources().getString(R.string.i04_menu_orden));
            Iconoperacion.add(R.drawable.ic_action_workorden);
        }

        //BAJAR LECTURAS DE LA ORDEN
        if (obj_orden.getIdservidor() != 0 && lectura_temp.size() > 0) {//obj_orden.getIdservidor() == 0 &&
            operacion.add(getResources().getString(R.string.i04_menu_archivo));
            Iconoperacion.add(R.drawable.ic_action_reporte);
        }

        //SI LA ORDEN HA SIDO CERRADA Y NO SE A NOTIFICADO DEL CIERRE, SE PUEDE REAPERTURAR.
        if (obj_orden.getFecha_fin() != null && obj_orden.getIdservidor() != 0) {//&& obj_orden.getIdservidor() == 0
            operacion.add(getResources().getString(R.string.i04_menu_aperturar));
            Iconoperacion.add(R.drawable.ic_action_open_again);
        }

        //SI LA ORDEN ESTA ABIERTA(EN PROCESO) Y NO FINALIZADO) PUEDE REINICARLA
        if (obj_orden.getFecha_inicio() != null && obj_orden.getFecha_fin() == null && !obj_orden.getAtentido()) {
            operacion.add(getResources().getString(R.string.i04_menu_reset));
            Iconoperacion.add(R.drawable.ic_action_reset_pedido);
        }

        //SI LA ORDEN NO SE HA INICIADO PUEDE ACTUALIZARLA
        if (obj_orden.getFecha_inicio()==null && obj_orden.getFecha_fin() == null && obj_orden.getIdservidor() != 0   && lectura_temp.size() == 0) {//&& obj_orden.getIdservidor() == 0
            operacion.add(getResources().getString(R.string.i04_menu_actualizar));
            Iconoperacion.add(R.drawable.ic_action_update_from_server);
        }

        operacion.add(getResources().getString(R.string.i04_menu_detalle));
        Iconoperacion.add(R.drawable.ic_action_aditional_detail);

        // CADA IMAGEN EN EL ARREGLO SERA MOSTRADA AL INICIO DE CADA ITEM
        Integer[] imageIdArr = Iconoperacion.toArray(new Integer[Iconoperacion.size()]);// ArrayUtils.toPrimitive();

        // TEXTO DE CADA ITEM.
        final String[] listItemArr = operacion.toArray(new String[operacion.size()]);

        // SE CREA DATOS EN UN SimpleAdapter
        List<Map<String, Object>> dialogItemList = new ArrayList<Map<String, Object>>();

        for (int i = 0; i < listItemArr.length; i++) {
            Map<String, Object> itemMap = new HashMap<String, Object>();
            itemMap.put(CUSTOM_ADAPTER_IMAGE, imageIdArr[i]);
            itemMap.put(CUSTOM_ADAPTER_TEXT, listItemArr[i]);

            dialogItemList.add(itemMap);
        }

        // Create SimpleAdapter object.
        SimpleAdapter simpleAdapter = new SimpleAdapter(FrmOrden.this, dialogItemList,
                R.layout.item_mnu_operacion,
                new String[]{CUSTOM_ADAPTER_IMAGE, CUSTOM_ADAPTER_TEXT},
                new int[]{R.id.alertDialogItemImageView, R.id.alertDialogItemTextView});

        // Create a alert dialog builder.


        AlertDialog.Builder builder = new AlertDialog.Builder(FrmOrden.this);
        builder.setIcon(R.drawable.img_logo)
                .setCancelable(false)
                .setTitle(Html.fromHtml("ORDEN <font color='#FF7F27'>" + obj_orden.getId() + "</font>"))// String.format(getResources().getString(R.string.i04_menu_orden_body),obj_orden.getId()))
                .setNegativeButton(getResources().getString(R.string.lblCancelar).toString().toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int itemIndex) {

                if (listItemArr[itemIndex].equals(getResources().getString(R.string.i04_menu_orden))) {
                    mostrarPopupAprobarOrdenTrabajo(obj_orden);
                }

                if (listItemArr[itemIndex].equals(getResources().getString(R.string.i04_menu_archivo))) {
                    //ExportarArchivoLocal(obj_orden);
                }

                if (listItemArr[itemIndex].equals(getResources().getString(R.string.i04_menu_aperturar))) {
                    String Cuerpo = "<DIV  style='text-align:center'>" +
                            "<font style='color:#000000;'>Desea reaperturar la orden:</font><BR>" +
                            "<font color='#ff0000'>" + obj_orden.getId() + "</font><BR></BR></BR>" +
                            "<font color='#000000'>Cliente</font><BR>" +
                            "<font color='#ff0000'>" + obj_orden.getCliente() + "?</font>" +
                            "</DIV>";

                    AlertDialog.Builder builder = new AlertDialog.Builder(FrmOrden.this);
                    builder.setIcon(R.drawable.img_logo);
                    builder.setMessage(Html.fromHtml(Cuerpo))
                            .setCancelable(false)
                            .setTitle(Html.fromHtml("ABRIR ORDEN <font color='#FF7F27'>" + obj_orden.getId() + "</font>"))
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("SI",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //cambiamos la activaidad
                                            obj_orden.setFecha_fin(null);
                                            obj_orden.setAtentido(false);
                                            if (TblPedido.modificar(FrmOrden.this, obj_orden)) {
                                                updateLista();
                                                Toast.makeText(FrmOrden.this, "La Orden " + obj_orden.getId() + " a sido reabierta", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(FrmOrden.this, "La Orden " + obj_orden.getId() + " no se pudo reabierta", Toast.LENGTH_LONG).show();
                                            }


                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                if (listItemArr[itemIndex].equals(getResources().getString(R.string.i04_menu_reset))) {
                    String Cuerpo = "";

                    AlertDialog.Builder builder = new AlertDialog.Builder(FrmOrden.this);
                    builder.setIcon(R.drawable.img_logo);
                    builder.setCancelable(false);


                    if (TblLecturaEliminada.existenDatosDeLaOrden(FrmOrden.this,obj_orden.getId())) {
                        Cuerpo = "<DIV  style='text-align:center'>" +
                                "<font style='color:#000000;'>No se puede reiniciar la orden:</font><BR>" +
                                "<font color='#ff0000'>" + obj_orden.getId() + "</font><BR></BR></BR>" +
                                "<font color='#000000'>Cliente</font><BR>" +
                                "<font color='#ff0000'>" + obj_orden.getCliente() + "?</font></BR>" +
                                "<font style='color:#000000;'><BR>Ya que hay datos de esta orden pendiente de borrar del servidor:</font><BR>" +
                                "</DIV>";

                        builder.setMessage(Html.fromHtml(Cuerpo))
                                .setTitle("ERROR AL REINICIAR ORDEN")
                                .setNegativeButton("CONTINUAR", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                    } else {
                        Cuerpo = "<DIV  style='text-align:center'>" +
                                "<font style='color:#000000;'>Desea reiniciar la orden:</font><BR>" +
                                "<font color='#ff0000'>" + obj_orden.getId() + "</font><BR></BR></BR>" +
                                "<font color='#000000'>Cliente</font><BR>" +
                                "<font color='#ff0000'>" + obj_orden.getCliente() + "?</font></BR>" +
                                "<font style='color:#000000;'><BR>Todas las cajas leidas seran regresadas al Inventario</font><BR>" +
                                "</DIV>";

                        builder.setMessage(Html.fromHtml(Cuerpo))
                                .setTitle("REINICIAR ORDEN")
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("SI",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                ConfApp.ORDEN_TRABAJADA_ACTUALMENTE = obj_orden;
                                                BDPedido_reiniciar proceso = new BDPedido_reiniciar(FrmOrden.this, ConfApp.ORDEN_TRABAJADA_ACTUALMENTE);
                                                proceso.execute("Borrando cajas", "Preparando datos");
                                            }
                                        });
                    }

                    AlertDialog alert = builder.create();
                    alert.show();
                }

                if (listItemArr[itemIndex].equals(getResources().getString(R.string.i04_menu_actualizar))) {
                    String Cuerpo = "";

                    AlertDialog.Builder builder = new AlertDialog.Builder(FrmOrden.this);
                    builder.setIcon(R.drawable.img_logo);
                    builder.setCancelable(false);

                    if (TblLecturaEliminada.existenDatosDeLaOrden(FrmOrden.this, obj_orden.getId())) {
                        Cuerpo = "<DIV  style='text-align:center'>" +
                                "<font style='color:#000000;'>No se puede actualizar la orden:</font><BR>" +
                                "<font color='#ff0000'>" + obj_orden.getId() + "</font><BR></BR></BR>" +
                                "<font color='#000000'>Cliente</font><BR>" +
                                "<font color='#ff0000'>" + obj_orden.getCliente() + "?</font></BR>" +
                                "<font style='color:#000000;'><BR>Datos pendientes de borrar del servidor</font>" +
                                "</DIV>";

                        builder.setTitle("ERROR AL ACTUALIZAR")
                                .setMessage(Html.fromHtml(Cuerpo))
                                .setNegativeButton("CONTINUAR", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                    } else {
                        Cuerpo = "<DIV  style='text-align:center'>" +
                                "<font style='color:#000000;'>Desea actualizar la orden:</font><BR>" +
                                "<font color='#ff0000'>" + obj_orden.getId() + "</font><BR></BR></BR>" +
                                "<font color='#000000'>Cliente</font><BR>" +
                                "<font color='#ff0000'>" + obj_orden.getCliente() + "?</font></BR>" +
                                "<font style='color:#000000;'><BR>Todas las cajas leidas seran eliminadas de forma local:</font>" +
                                "</DIV>";

                        builder.setTitle("ACTUALIZAR ORDEN")
                                .setMessage(Html.fromHtml(Cuerpo))
                                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setPositiveButton("SI",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                ConfApp.ORDEN_TRABAJADA_ACTUALMENTE = obj_orden;
                                                BDPedido_importar operacion = new BDPedido_importar(FrmOrden.this,obj_orden);
                                                operacion.execute("Actualizando Orden", "Preparando datos");
                                            }
                                        });
                    }

                    AlertDialog alert = builder.create();
                    alert.show();
                }

                /*if (listItemArr[itemIndex].equals(getResources().getString(R.string.i04_menu_detalle))) {
                    ConfApp.ORDEN_TRABAJADA_ACTUALMENTE = obj_orden;

                    String direccionweb = TblParametro.getClave(FrmOrdenes.this, "url_orden");
                    direccionweb = direccionweb + ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId();

                    abrirPaginaWeb(direccionweb);
                }*/

            }
        });
        builder.create();
        builder.show();
    }

    private void mostrarPopupAprobarOrdenTrabajo(Pedido obj_orden) {
        String Cuerpo = "<DIV  style='text-align:center'>" +
                "<font style='color:#000000;'>Desea trabajar la orden:</font><BR>" +
                "<font color='#ff0000'>" + obj_orden.getId() + "</font><BR></BR></BR>" +
                "<font color='#000000'>Cliente</font><BR>" +
                "<font color='#ff0000'>" + obj_orden.getCliente() + "?</font>" +
                "</DIV>";

        AlertDialog.Builder builder = new AlertDialog.Builder(FrmOrden.this);
        builder.setIcon(R.drawable.img_logo);
        builder.setMessage(Html.fromHtml(Cuerpo))
                .setCancelable(false)
                .setTitle("TRABAJAR ORDEN")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("SI",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (obj_orden.getCamion().getId() == null || obj_orden.getCamion().getId() == 0) {
                                    mostrarPopupSeleccionarCamion(obj_orden);
                                } else {
                                    Camion camion = TblCamion.obtenerRegistro(FrmOrden.this, obj_orden.getCamion().getId());
                                    obj_orden.setCamion(camion);//CARGAMOS LA INFORMACION COMPLETA DEL CAMION, NO SOLO EL INDICE
                                    Log.i(this.getClass().getName(),"Camion seleccionado: "+ camion.toString2());

                                    if (obj_orden.getConductor().getId() == null || obj_orden.getConductor().getId().isEmpty()) {
                                        mostrarPopupSeleccionarConductor(obj_orden);
                                    }else{
                                        Conductor conductor = TblConductor.obtenerRegistro(FrmOrden.this,obj_orden.getConductor().getId());
                                        obj_orden.setConductor(conductor);
                                        Log.i(this.getClass().getName(),"Conductor seleccionado: "+ camion.toString2());
                                        mostrarFormularioExaneoxPedido(obj_orden);
                                    }
                                }

                                //cambiamos la activaidad
                               /* ArrayList<Pedido_Detalle> detalle = TblPedido_Detalle.obtenerRegistrosxPedido(FrmOrden.this, obj_orden.getId());

                                ConfApp.ORDEN_TRABAJADA_ACTUALMENTE = new Pedido();
                                ConfApp.ORDEN_TRABAJADA_ACTUALMENTE = obj_orden;
                                ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.setBodega(TblBodega.obtenerRegistro(FrmOrden.this, obj_orden.getIdBodega()));
                                ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.setPedido_detalle(detalle);

                                Log.i(this.getClass().getName(),"Orden seleccionado: "+ ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.toString2());
                                Intent nuevaPantalla = new Intent(FrmOrden.this, FrmEscaneoxPedido.class);
                                startActivity(nuevaPantalla);*/
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void mostrarPopupSeleccionarCamion(Pedido obj_pedido) {
        ArrayAdapter<Camion> adapter_camion;
        TextInputEditText txtBuscarCamion;
        ListView lv_camion;


        final RelativeLayout panel = (RelativeLayout) LayoutInflater.from(FrmOrden.this).inflate(R.layout.frm_lista, null);
        AlertDialog.Builder componente = new AlertDialog.Builder(FrmOrden.this);
        componente.setTitle("Seleccionar Camion");

        final ImageButton btnAgrearUsuario = panel.findViewById(R.id.btnAgregar);
        btnAgrearUsuario.setVisibility(View.INVISIBLE);//(ConfApp.USER_DTS || ConfApp.USER_SUPERVISOR) ? View.VISIBLE : View.INVISIBLE

        componente.setCancelable(false);

        final Vector<Camion> list_camion = TblCamion.obtenerRegistros(FrmOrden.this);
        adapter_camion = new ArrayAdapter<Camion>(this, R.layout.item_objeto, R.id.i05_lblnombre, Collections.list(list_camion.elements())) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                final Camion obj_camion = getItem(position);
                // Check if an existing view is being reused, otherwise inflate the view
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_objeto, parent, false);
                }

                ((TextView) convertView.findViewById(R.id.i05_lblnombre)).setText("PLACA " + obj_camion.getNoplaca());
                ((TextView) convertView.findViewById(R.id.i05_lbltipo)).setText("Cap. Min: " + obj_camion.getMin() + "-Cap. Max:" + obj_camion.getMax());

                return convertView;
            }
        };

        lv_camion = panel.findViewById(R.id.list_view);
        lv_camion.setTextFilterEnabled(true);
        lv_camion.setAdapter(adapter_camion);
        txtBuscarCamion = panel.findViewById(R.id.inputSearch);

        componente.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        txtBuscarCamion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter_camion.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        componente.setView(panel);

        final AlertDialog FrmSeleccionarCamion = componente.create();
        FrmSeleccionarCamion.show();

        lv_camion.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Camion item = (Camion) arg0.getItemAtPosition(position);
                obj_pedido.setCamion(item);
                TblPedido.modificar(FrmOrden.this, obj_pedido);

                if (obj_pedido.getConductor().getId() == null || obj_pedido.getConductor().getId().isEmpty()) {
                    mostrarPopupSeleccionarConductor(obj_pedido);
                }else{
                    Conductor conductor = TblConductor.obtenerRegistro(FrmOrden.this,obj_pedido.getConductor().getId());
                    obj_pedido.setConductor(conductor);
                    mostrarPopupAprobarOrdenTrabajo(obj_pedido);
                }

                FrmSeleccionarCamion.dismiss();
            }
        });
    }

    private void mostrarPopupSeleccionarConductor(Pedido obj_pedido) {
        ArrayAdapter<Conductor> adapter_conductor;
        ListView lv_conductor;
        TextInputEditText txtBuscarConductor;

        final RelativeLayout panel = (RelativeLayout) LayoutInflater.from(FrmOrden.this).inflate(R.layout.frm_lista, null);
        AlertDialog.Builder componente = new AlertDialog.Builder(FrmOrden.this);
        componente.setTitle("SELECCIONAR CONDUCTOR");

        final ImageButton btnAgrearConductor = panel.findViewById(R.id.btnAgregar);
        btnAgrearConductor.setVisibility(View.INVISIBLE);//(ConfApp.USER_DTS || ConfApp.USER_SUPERVISOR) ? View.VISIBLE : View.INVISIBLE

        componente.setCancelable(false);

        final ArrayList<Conductor> list_conductor = TblConductor.obtenerRegistros(FrmOrden.this);
        adapter_conductor = new ArrayAdapter<Conductor>(this, R.layout.item_objeto, R.id.i05_lblnombre, list_conductor) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                final Conductor obj_conductor = getItem(position);
                // Check if an existing view is being reused, otherwise inflate the view
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_objeto, parent, false);
                }

                ((TextView) convertView.findViewById(R.id.i05_lblnombre)).setText(obj_conductor.getNombre());
                ((TextView) convertView.findViewById(R.id.i05_lbltipo)).setText(obj_conductor.getCodigo());

                return convertView;
            }
        };

        lv_conductor = panel.findViewById(R.id.list_view);
        lv_conductor.setTextFilterEnabled(true);
        lv_conductor.setAdapter(adapter_conductor);
        txtBuscarConductor = panel.findViewById(R.id.inputSearch);

        componente.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        txtBuscarConductor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter_conductor.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        componente.setView(panel);

        final AlertDialog FrmSeleccionarConductor = componente.create();
        FrmSeleccionarConductor.show();

        lv_conductor.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Conductor item = (Conductor) arg0.getItemAtPosition(position);
                obj_pedido.setConductor(item);
                mostrarFormularioExaneoxPedido(obj_pedido);
                FrmSeleccionarConductor.dismiss();
            }
        });
    }

    private void mostrarFormularioExaneoxPedido(Pedido obj_orden){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    obj_orden.setBodega(TblBodega.obtenerRegistro(FrmOrden.this, obj_orden.getIdBodega()));
                    obj_orden.setAtentido(true);
                    ConfApp.BDOPERATION.guardar_orden(obj_orden);//GUARDAR O ACTUALIZAR EN EL SERVIDOR

                    if(TblPedido.modificar(FrmOrden.this, obj_orden)){
                        ConfApp.ORDEN_TRABAJADA_ACTUALMENTE = obj_orden;
                        Intent nuevaPantalla = new Intent(FrmOrden.this, FrmEscaneoxPedido.class);
                        startActivity(nuevaPantalla);
                    }else{
                        Toast.makeText(FrmOrden.this, "la orden no se puedo modificar"+obj_orden.toString2(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void notificarActualizacionDePedidos(boolean SERVER_WAS_ONLINE, int ContRegistros, String Mensaje) {
        if (SERVER_WAS_ONLINE) {
            updateLista();
            Toast.makeText(FrmOrden.this, Mensaje, Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(FrmOrden.this, Mensaje, Toast.LENGTH_LONG).show();
    }

}

class BDPedido_actualizar extends AsyncTask {
    BDOperacion_Update operacion = new BDOperacion_Update();
    int conCorrect = 0, conError = 0;
    ProgressDialog progressDialog;

    Context referencia;

    public BDPedido_actualizar(Context pantalla) {
        this.referencia = pantalla;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return getOrdenesPendientes();
    }

    private Object getOrdenesPendientes() {
        boolean SERVERS_ARE_ONLINE = operacion.GetStatusConecctionSGBD_ERP();

        publishProgress("Obteniendo Ordenes del servidor" , "Actualizando Orden"); // Calls onProgressUpdate()

        if (SERVERS_ARE_ONLINE) {
            ArrayList<Pedido> Registros = operacion.Get_ListPedidos("View_Pedido");
            System.out.println("Cantidad de registros en el servidor: " + Registros.size());

            if (Registros.size() > 0) {
                ArrayList<Pedido> pedidos_trabajados = TblPedido.obtenerPedidosTrabajados(referencia);
                //BUSCA LAS ORDENES QUE NO ESTAN SIENDO TRABAJADAS
                for (int contP = 0; contP < Registros.size(); contP++) {
                    for (int contPT = 0; contPT < pedidos_trabajados.size(); contPT++) {
                        if (Registros.get(contP).getId() == pedidos_trabajados.get(contPT).getId()) {
                            Registros.get(contP).setAtentido(pedidos_trabajados.get(contPT).getAtentido());
                        }
                    }
                }


                for (int contP = 0; contP < Registros.size(); contP++) {
                    System.out.println("Orden: " + Registros.get(contP).getId());
                    System.out.println("DetalleOrden: " + Registros.get(contP).getPedido_detalle().size());

                    if (!Registros.get(contP).getAtentido()) { //OMITIMOS ORDES QUE ESTEN SIENDO PROCESADAS.
                        if (Registros.get(contP).getPedido_detalle().size() > 0) { //OMITIMOS ORDES QUE NO TENGAN PRODUCTOS.
                            publishProgress("Borrando ordenes antiguas", "Actualizando Orden"); // Calls onProgressUpdate()

                            TblPedido.borrarPedidoYDetalle(referencia, Registros.get(contP).getId());//.BorrarPedido(referencia,Registros.get(contP));

                            publishProgress("Actualizando Orden: " + Registros.get(contP).getId(), "Actualizando Orden"); // Calls onProgressUpdate()

                            if (!TblPedido.guardar(referencia, Registros.get(contP))) {
                                conError += 1;
                                //Log.i("Pedido_error", Registros.get(contP).toString2());
                            } else {
                                conCorrect += 1;
                                //Log.i("Pedido_correcto", Registros.get(contP).toString2());
                            }
                        } else {
                            conError += 1;
                        }
                    } else {
                        conError += 1;
                    }
                }

            }
        }
        progressDialog.dismiss();
        return SERVERS_ARE_ONLINE;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (Boolean.valueOf(o.toString())) {
            ((FrmOrden) referencia).notificarActualizacionDePedidos(true, conCorrect, "Se actualizaron " + conCorrect + " ordenes");
        } else {
            ((FrmOrden) referencia).notificarActualizacionDePedidos(false, conError, "No se puedo establecer coneccion con el servidor");
        }
    }

    @Override
    protected void onPreExecute() {
        progressDialog = new ProgressDialog(referencia);
        progressDialog.setIcon(R.drawable.img_logo);
        progressDialog.setTitle(Html.fromHtml("<font color='#FF7F27'><small>Operacion en Progreso</small></font>"));
        progressDialog.setMessage(Html.fromHtml("<small>Por favor  espere....</small>"));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setOnCancelListener(null);
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Object[] values) {
        super.onProgressUpdate(values);
        progressDialog.setTitle(Html.fromHtml("<font color='#FF7F27'><small>" + values[1].toString() + "</small></font>"));
        progressDialog.setMessage(Html.fromHtml("<font color='#000000'><small>" + values[0].toString() + "</small></font>"));


    }
}

class BDPedido_reiniciar extends AsyncTask {
    Context referencia;
    ProgressDialog progressDialog;
    Pedido obj_orden;


    public BDPedido_reiniciar(Context referencia, Pedido orden) {
        this.referencia = referencia;
        this.obj_orden = orden;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        boolean resultado = false;
        String[] args = new String[]{"" + obj_orden.getId()};

        obj_orden.getCamion().setId(0);
        obj_orden.getConductor().setId("");
        obj_orden.getOperador().setId(0);
        obj_orden.setMarchamo("");
        obj_orden.setAtentido(false);
        obj_orden.setEstado(0);
        obj_orden.setFecha_inicio(null);
        obj_orden.setFecha_fin(null);

        if(ConfApp.BDOPERATION.guardar_orden(obj_orden)){
            if (TblPedido.modificar(referencia, obj_orden)) {
                ArrayList<Lectura> Registros = TblLectura.obtenerRegistrosXOrden(referencia, obj_orden.getId());
                for(int it=0 ; it< Registros.size();it++){
                    Lectura lectura = Registros.get(it);
                    Producto producto = TblProducto.obtenerRegistroxCodigo(referencia,lectura.getCodigo());
                    if(ConfApp.BDOPERATION.actualizar_existencia(producto.getCodigo_softland(),obj_orden.getBodega().getId(),lectura.getPeso())){
                        if(ConfApp.BDOPERATION.borrar_Caja(lectura.getBarra()))
                            TblLectura.borrarCaja(referencia,lectura.getBarra());
                        //TblLectura.borrarLecturaxOrden(referencia,obj_orden.getId());
                    }
                }
                ConfApp.BDOPERATION.borrar_pedido(obj_orden);
                resultado = true;
            }
        }

        return resultado;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(referencia);
        progressDialog.setIcon(R.drawable.img_logo);
        progressDialog.setTitle(Html.fromHtml("<font color='#FF7F27'><small>Operacion en Progreso</small></font>"));
        progressDialog.setMessage(Html.fromHtml("<small>Por favor  espere....</small>"));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setOnCancelListener(null);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progressDialog.dismiss();

        if (Boolean.valueOf(o.toString())){
            Toast.makeText(referencia, "Orden " + obj_orden.getId() + " einiciada", Toast.LENGTH_SHORT).show();
            ((FrmOrden) referencia).updateLista();
        }
    }

    @Override
    protected void onProgressUpdate(Object[] params) {
        super.onProgressUpdate(params);
        progressDialog.setTitle(Html.fromHtml("<font color='#FF7F27'><small>" + params[1].toString() + "</small></font>"));
        progressDialog.setMessage(Html.fromHtml("<font color='#000000'><small>" + params[0].toString() + "</small></font>"));
    }

}

class BDPedido_importar extends AsyncTask{

    BDOperacion_Update operacion = new BDOperacion_Update();
   ProgressDialog progressDialog;
    Context referencia;
    Pedido obj_orden;

    public BDPedido_importar(Context referencia, Pedido orden) {
        this.referencia = referencia;
        this.obj_orden = orden;
    }

    private boolean BorrarYCrearOden() {
        boolean SERVERS_ARE_ONLINE = operacion.GetStatusConecctionSGBD_ERP();

        publishProgress("Obteniendo Orden del servidor" , "Actualizando Orden"); // Calls onProgressUpdate()

        if (SERVERS_ARE_ONLINE) {
            Pedido pedido = operacion.Get_Pedido("View_Pedido",obj_orden.getId());

            //BORRAR PEDIDO LOCAL,BORRAR LECTURA EN EL SERVIDOR Y HACER ROLLBACK EN LOS PRESOS
            if(pedido.getId()!=null){
                publishProgress("Borrando pedido anterior", "Actualizando Orden"); // Calls onProgressUpdate()
                TblPedido.borrarPedidoYDetalle(referencia, pedido.getId());//.BorrarPedido(referencia,Registros.get(contP));
                publishProgress("Actualizando Orden: " + pedido.getId(), "Actualizando Orden"); // Calls onProgressUpdate()
                TblPedido.guardar(referencia, pedido);
            }
        }
        return SERVERS_ARE_ONLINE;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        return BorrarYCrearOden();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(referencia);
        progressDialog.setIcon(R.drawable.img_logo);
        progressDialog.setTitle(Html.fromHtml("<font color='#FF7F27'><small>Operacion en Progreso</small></font>"));
        progressDialog.setMessage(Html.fromHtml("<small>Por favor  espere....</small>"));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setOnCancelListener(null);
        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        progressDialog.dismiss();

        if (Boolean.valueOf(o.toString())){
            Toast.makeText(referencia, "Orden " + obj_orden.getId() + " actualizada desde el servidor", Toast.LENGTH_SHORT).show();
            ((FrmOrden) referencia).updateLista();
        }
    }

    @Override
    protected void onProgressUpdate(Object[] params) {
        super.onProgressUpdate(params);
        progressDialog.setTitle(Html.fromHtml("<font color='#FF7F27'><small>" + params[1].toString() + "</small></font>"));
        progressDialog.setMessage(Html.fromHtml("<font color='#000000'><small>" + params[0].toString() + "</small></font>"));
    }
}