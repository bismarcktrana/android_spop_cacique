package com.sdn.cacique.spop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.sdn.bd.dao.host.TblContenedor;
import com.sdn.bd.dao.host.TblContenedorDetalle;
import com.sdn.bd.dao.host.TblLecturaEliminada;
import com.sdn.bd.dao.softland.TblPedido;
import com.sdn.bd.objeto.host.Contenedor;
import com.sdn.bd.objeto.host.Contenedor_Detalle;
import com.sdn.cacique.util.ConfApp;
import com.sdn.cacique.util.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrmContenedor extends AppCompatActivity {
    ArrayList<Contenedor> ordenes = new ArrayList<Contenedor>();
    public ArrayAdapter<Contenedor> Adapterordenes;
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
        inflater.inflate(R.menu.mnu_contenedor, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (item.getItemId()) {
            case R.id.m06_action_nuevo:
                Contenedor ctn =new Contenedor();
                ctn.setIdservidor(0);
                ctn.setIdoperador(ConfApp.OPERADORLOGEADO.getId());
                ctn.setFecha_creacion(new Date());
                mostrarPopupActualizarContenedor(ctn);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_contenedor);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ListView lv_listaordenes = findViewById(R.id.p06_listadeordenes);

        lv_listaordenes.setTextFilterEnabled(true);

        Adapterordenes = new ArrayAdapter<Contenedor>(this, R.layout.item_contenedor, R.id.i06_txtContenedor, ordenes) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                final Contenedor obj_orden = getItem(position);
                // Check if an existing view is being reused, otherwise inflate the view
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_contenedor, parent, false);
                }
                // Lookup view for data population
                ((TextView) convertView.findViewById(R.id.i06_txtContenedor)).setText(obj_orden.getNombre());
                ((TextView) convertView.findViewById(R.id.i06_txtFechaCreacion)).setText(obj_orden.getFecha_creacion() == null ? "" : Utils.bd.C_DateToAppFormat(obj_orden.getFecha_creacion()));
                ((TextView) convertView.findViewById(R.id.i06_txtFechaInicio)).setText(obj_orden.getFecha_inicio() == null ? "" : Utils.bd.C_DateToAppFormat(obj_orden.getFecha_inicio()));
                ((TextView) convertView.findViewById(R.id.i06_txtFechaFin)).setText(obj_orden.getFecha_fin() == null ? "" : Utils.bd.C_DateToAppFormat(obj_orden.getFecha_fin()));

                String PROCESS_NAME="PENDIENTE";
                Drawable PROCESS_COLOR=((TextView) convertView.findViewById(R.id.i06_txtEstado)).getBackground();//getResources().getDrawable(R.color.md_theme_light_primary);
                Integer PROCESS_TEXT_COLOR=getResources().getColor(R.color.md_theme_light_primary);//getResources().getColor(R.color.md_theme_light_primary);

                    if(obj_orden.getFecha_inicio() != null){
                        if(obj_orden.getFecha_fin()==null){
                            if(TblContenedorDetalle.obtenerNoFilas(FrmContenedor.this,obj_orden.getId())>0){
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
                    }

                ((TextView) convertView.findViewById(R.id.i06_txtEstado)).setText(PROCESS_NAME);
                ((TextView) convertView.findViewById(R.id.i06_txtEstado)).setTextColor(PROCESS_TEXT_COLOR);
                convertView.findViewById(R.id.i06_txtEstado).setBackground(PROCESS_COLOR);

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

        inputSearch = findViewById(R.id.p06_txtbuscarorden);
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

                ArrayList<Contenedor> temp = TblContenedor.obtenerRegistros(FrmContenedor.this);//.obtenerRegistrosSimplificado(FrmOrdenes.this); //TblOrden.obtenerRegistros(FrmOrdenes.this,1, ConfApp.FrenteActual.getId());
                ordenes.clear();
                Adapterordenes.notifyDataSetChanged();

                for (int it = 0; it < temp.size(); it++)
                    ordenes.add(temp.get(it));

                Adapterordenes.notifyDataSetChanged();
                getSupportActionBar().setTitle(ordenes.size() + " DOCUMENTOS");
            }
        });
    }

    // Show how to use SimpleAdapter to customize list item in android alert dialog.
    private void showMenuAdapterAlertDialog(final Contenedor obj_orden) {
        // Image and text item data's key.
        String CUSTOM_ADAPTER_IMAGE = "image";
        String CUSTOM_ADAPTER_TEXT = "text";
        ArrayList<Contenedor_Detalle> Cajas_leidas = TblContenedorDetalle.obtenerRegistrosXContenedor(FrmContenedor.this, obj_orden.getId());

        ArrayList<String> operacion = new ArrayList<String>();
        ArrayList<Integer> Iconoperacion = new ArrayList<Integer>();

        //NO ORDEN NO ESTA FINALIZADA SE PUEDE  LA TRABAJAR ORDEN
        if (obj_orden.getFecha_fin() == null) {//obj_orden.getIdservidor() == 0 &&
            operacion.add(getResources().getString(R.string.i06_menu_contenedor));
            Iconoperacion.add(R.drawable.ic_action_workorden);
        }

        //SI LA ORDEN HA SIDO CERRADA Y NO SE A NOTIFICADO DEL CIERRE, SE PUEDE REAPERTURAR.
        if (obj_orden.getFecha_fin() != null && obj_orden.getIdservidor() != 0) {//&& obj_orden.getIdservidor() == 0
            operacion.add(getResources().getString(R.string.i06_menu_aperturar));
            Iconoperacion.add(R.drawable.ic_action_open_again);
        }

        //SI LA ORDEN ESTA ABIERTA(EN PROCESO) Y NO FINALIZADO) PUEDE REINICARLA
        if (obj_orden.getFecha_inicio() != null && obj_orden.getFecha_fin() == null && Cajas_leidas.size()>0) {
            operacion.add(getResources().getString(R.string.i06_menu_reset));
            Iconoperacion.add(R.drawable.ic_action_reset_pedido);
        }

        //ACTIVAR EL MENU RENOMBRAR
        operacion.add(getResources().getString(R.string.i06_menu_renombrar));
        Iconoperacion.add(R.drawable.ic_action_rename);

        //SI LA ORDEN NO SE HA INICIADO PUEDE ELIMINARLA
        if (obj_orden.getFecha_inicio()==null && obj_orden.getFecha_fin() == null && obj_orden.getIdservidor() == 0   && Cajas_leidas.size() == 0) {//&& obj_orden.getIdservidor() == 0
            operacion.add(getResources().getString(R.string.i06_menu_eliminar));
            Iconoperacion.add(R.drawable.ic_action_delete);
        }


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
        SimpleAdapter simpleAdapter = new SimpleAdapter(FrmContenedor.this, dialogItemList,
                R.layout.item_mnu_operacion,
                new String[]{CUSTOM_ADAPTER_IMAGE, CUSTOM_ADAPTER_TEXT},
                new int[]{R.id.alertDialogItemImageView, R.id.alertDialogItemTextView});

        // Create a alert dialog builder.


        AlertDialog.Builder builder = new AlertDialog.Builder(FrmContenedor.this);
        builder.setIcon(R.drawable.img_logo)
                .setCancelable(false)
                .setTitle(Html.fromHtml("Documento <font color='#FF7F27'>" + obj_orden.getNombre() + "</font>"))// String.format(getResources().getString(R.string.i04_menu_orden_body),obj_orden.getId()))
                .setNegativeButton(getResources().getString(R.string.lblCancelar).toString().toUpperCase(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder.setAdapter(simpleAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int itemIndex) {

                if (listItemArr[itemIndex].equals(getResources().getString(R.string.i06_menu_contenedor))) {
                    mostrarPopupAprobarDocumento(obj_orden);
                }

                if (listItemArr[itemIndex].equals(getResources().getString(R.string.i06_menu_aperturar))) {
                    String Cuerpo = "<DIV  style='text-align:center'>" +
                            "<font style='color:#000000;'>Desea seguir editando el documento:</font><BR>" +
                            "<font color='#ff0000'>" + obj_orden.getNombre() + "</font>" +
                            "</DIV>";

                    AlertDialog.Builder builder = new AlertDialog.Builder(FrmContenedor.this);
                    builder.setIcon(R.drawable.img_logo);
                    builder.setMessage(Html.fromHtml(Cuerpo))
                            .setCancelable(false)
                            .setTitle(Html.fromHtml("EDITAR DOCUMENTO <font color='#FF7F27'>" + obj_orden.getId() + "</font>"))
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

                                            if (TblContenedor.modificar(FrmContenedor.this,obj_orden)) {
                                                updateLista();
                                                Toast.makeText(FrmContenedor.this, "El documento " + obj_orden.getNombre() + " puede ser editado", Toast.LENGTH_LONG).show();
                                            } else {
                                                Toast.makeText(FrmContenedor.this, "El documento " + obj_orden.getNombre() + " no puede ser editado", Toast.LENGTH_LONG).show();
                                            }


                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                if (listItemArr[itemIndex].equals(getResources().getString(R.string.i06_menu_reset))) {
                    String Cuerpo = "<DIV  style='text-align:center'>" +
                            "<font style='color:#000000;'>Desea eliminar las cajas leidas</font><BR>" +
                            "<font color='#ff0000'>Cantidad de cajas: " + Cajas_leidas.size() + "</font>" +
                            "</DIV>";

                    AlertDialog.Builder builder = new AlertDialog.Builder(FrmContenedor.this);
                    builder.setIcon(R.drawable.img_logo);
                    builder.setMessage(Html.fromHtml(Cuerpo))
                            .setCancelable(false)
                            .setTitle("LIMPIAR LECTURAS DEL DOCUMENTO")
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("SI",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            obj_orden.setFecha_inicio(null);
                                            obj_orden.setFecha_fin(null);
                                            TblContenedor.modificar(FrmContenedor.this,obj_orden);
                                            TblContenedorDetalle.borrarLecturaxContenedor(FrmContenedor.this, obj_orden.getId());
                                            updateLista();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();

                }

                if (listItemArr[itemIndex].equals(getResources().getString(R.string.i06_menu_eliminar))) {
                    String Cuerpo = "<DIV  style='text-align:center'>" +
                            "<font style='color:#000000;'>Desea elimiar el documento:</font><BR>" +
                            "<font color='#ff0000'>" + obj_orden.getNombre() + "</font>" +
                            "</DIV>";

                    AlertDialog.Builder builder = new AlertDialog.Builder(FrmContenedor.this);
                    builder.setIcon(R.drawable.img_logo);
                    builder.setMessage(Html.fromHtml(Cuerpo))
                            .setCancelable(false)
                            .setTitle("ELIMINAR DOCUMENTO")
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("SI",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            TblContenedor.borrarContenedorxID(FrmContenedor.this,obj_orden.getId());
                                            updateLista();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                if (listItemArr[itemIndex].equals(getResources().getString(R.string.i06_menu_renombrar))) {
                    ConfApp.CONTENEDOR_ACTUALMENTE = obj_orden;
                    mostrarPopupActualizarContenedor(obj_orden);
                }

            }
        });
        builder.create();
        builder.show();
    }

    private void mostrarPopupActualizarContenedor(Contenedor obj_pedido) {
        AlertDialog.Builder componente = new AlertDialog.Builder(FrmContenedor.this);
        componente.setTitle("Ingrese el nombre del Documento");
        componente.setCancelable(false);

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(obj_pedido.getNombre());
        componente.setView(input);
        input.requestFocus();

        componente.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("Se creara "+input.getText());

                if(!input.getText().toString().isEmpty()){
                    obj_pedido.setNombre(input.getText().toString());



                    if(obj_pedido.getId()<1)
                        TblContenedor.guardar(FrmContenedor.this, obj_pedido);
                    else
                        TblContenedor.modificar(FrmContenedor.this, obj_pedido);

                    updateLista();
                }
            }
        });

        componente.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                updateLista();
            }
        });
        componente.show();
    }

    private void mostrarPopupAprobarDocumento(Contenedor obj_orden) {
        String Cuerpo = "<DIV  style='text-align:center'>" +
                "<font style='color:#000000;'>Desea trabajar el documento:</font><BR>" +
                "<font color='#ff0000'>" + obj_orden.getNombre() + "</font>" +
                "</DIV>";

        AlertDialog.Builder builder = new AlertDialog.Builder(FrmContenedor.this);
        builder.setIcon(R.drawable.img_logo);
        builder.setMessage(Html.fromHtml(Cuerpo))
                .setCancelable(false)
                .setTitle("TRABAJAR DOCUMENTO")
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("SI",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ConfApp.CONTENEDOR_ACTUALMENTE = obj_orden;
                                Intent nuevaPantalla = new Intent(FrmContenedor.this, FrmEscaneoLibre.class);
                                startActivity(nuevaPantalla);
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


}