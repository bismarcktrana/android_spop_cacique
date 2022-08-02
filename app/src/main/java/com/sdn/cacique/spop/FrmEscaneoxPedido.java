package com.sdn.cacique.spop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.evrencoskun.tableview.TableView;
import com.evrencoskun.tableview.filter.Filter;
import com.evrencoskun.tableview.filter.FilterChangedListener;
import com.evrencoskun.tableview.listener.ITableViewListener;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sdn.bd.dao.host.TblLectura;
import com.sdn.bd.dao.produccion.TblCamion;
import com.sdn.bd.dao.produccion.TblProducto;
import com.sdn.bd.dao.softland.TblConductor;
import com.sdn.bd.dao.softland.TblPedido;
import com.sdn.bd.dao.softland.TblPedido_Detalle;
import com.sdn.bd.objeto.host.Lectura;
import com.sdn.bd.objeto.produccion.Camion;
import com.sdn.bd.objeto.produccion.Producto;
import com.sdn.bd.objeto.softland.Conductor;
import com.sdn.bd.objeto.softland.Pedido;
import com.sdn.cacique.util.ConfApp;
import com.sdn.cacique.util.Utils;
import com.sdn.sound.SoundManager;
import com.sdn.tableview.TableViewAdapter;
import com.sdn.tableview.TableViewModel;
import com.sdn.tableview.model.Cell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class FrmEscaneoxPedido extends AppCompatActivity {
    private static final String LOG_TAG = FrmEscaneoxPedido.class.getSimpleName();

    private TextView txtLectura, txtcodigo, txtdescripcion, txtcaja, txtpeso, txtlote, txtserie, txtfechasacrificio, txtfechaproceso, txtfechavencimiento, txtpesototal, txtcantidadcajas;
    private TextView lblBodegaNombre,txtCamion,txtConductor,txtMarchamo;

    FrmEcaneoxPedidoAdapter2 adaptador;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    TabLayoutMediator mediator;
    Toolbar toolbar;
    String codigoAEliminar = "";

    PnlLectura pnlLectura = null;
    PnlConsolidado pnlconsolidado = null;
    PnlDetalle pnldetalle = null;
    //Object[] detalle = null;
    Lectura obj_lectura = new Lectura();

    SoundManager sound;

    Producto obj_producto = null;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnu_escanearpedido, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.p11_item_borrar /*&& ConfApp.USER_ESTIBADOR*/ && !ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getAtentido()) {

            if (!codigoAEliminar.isEmpty()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setIcon(R.drawable.img_logo)
                        .setTitle(Html.fromHtml("<font color='#FF7F27'><small>Borrar caja</small></font>"))
                        .setMessage(Html.fromHtml("<small>Desea eliminar la caja? <BR>" + codigoAEliminar + "</small>"))
                        .setCancelable(false)
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

                                                Thread thread = new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try  {
                                                            if(!codigoAEliminar.isEmpty()){
                                                                Lectura lectura_borrar = TblLectura.obtenerRegistroXCodigoBarra(FrmEscaneoxPedido.this,codigoAEliminar);
                                                                Producto pr = TblProducto.obtenerRegistroxCodigo(FrmEscaneoxPedido.this,lectura_borrar.getCodigo());

                                                                if(ConfApp.BDOPERATION.borrar_Caja(lectura_borrar.getBarra())){
                                                                    ConfApp.BDOPERATION.actualizar_existencia(pr.getCodigo_softland(),ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getBodega().getId(),lectura_borrar.getPeso());
                                                                    TblLectura.borrarCaja(FrmEscaneoxPedido.this, codigoAEliminar, true);
                                                                }
                                                            }

                                                            runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    codigoAEliminar = "";
                                                                    txtBuscarDetalle.setText("");
                                                                    mostrarMenuxPestana(2);
                                                                    preparePnlDetalle(pnldetalle.getView());
                                                                }
                                                            });



                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                });
                                                thread.start();
                                            }
                                        });
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                codigoAEliminar = "";
                //txtBuscarBarra_Detalle.setText("");
            }
        }

        if (id == R.id.p11_item_borrartodo && /*ConfApp.USER_ESTIBADOR &&*/ !ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getAtentido()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.img_logo)
                    .setTitle(Html.fromHtml("<font color='#FF7F27'><small>Borrar todo</small></font>"))
                    .setMessage(Html.fromHtml("<small>Desea borrar todas las cajas escaneadas?</small>"))
                    .setCancelable(false)
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })
                    .setPositiveButton("SI",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                   new AsyncTaskRunnerElimiarLecturas(FrmEscaneoxPedido.this, ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId()).execute("Borrando cajas", "Preparando datos");
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_escaneoxpedido);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId());
        ((TextView) findViewById(R.id.p11_lblClientenombre)).setText(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getCliente());
        ((TextView) findViewById(R.id.p11_lblDireccion)).setText(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getDireccion());

        configurarSonido();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);//mantenemos cargadoen memoria las pentanas

        adaptador = new FrmEcaneoxPedidoAdapter2(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adaptador);

        mediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: {
                    //tab.setIcon(R.drawable.icon_item);
                    tab.setText("LECTURA");
                    break;
                }
                case 1: {
                    tab.setText("RESUMEN");
                    //tab.setIcon(R.drawable.ic_action_add);
                    // BadgeDrawable mibagge =  tab.getOrCreateBadge();
                    // mibagge.setBackgroundColor(getResources().getColor(R.color.blue));
                    // mibagge.setNumber(10);
                    break;
                }
                case 2: {
                    tab.setText("DETALLE");
                    break;
                }
            }
        });
        mediator.attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                ocultarTeclado();
                mostrarMenuxPestana(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.selectTab(tabLayout.getTabAt(position));
                ocultarTeclado();

                if (position == 0) {
                    pnlLectura = (PnlLectura) adaptador.createFragment(position);
                    preparePnlLectura(pnlLectura.getView());
                }

                if (position == 1) {
                    pnlconsolidado = (PnlConsolidado) adaptador.createFragment(position);
                    preparePnlResumen(pnlconsolidado.getView());
                }

                if (position == 2) {
                    pnldetalle = (PnlDetalle) adaptador.createFragment(position);
                    preparePnlDetalle(pnldetalle.getView());
                }

                //mostrarMenuxPestana(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

        });

    }

    private void mostrarMenuxPestana(int position) {
       /* runOnUiThread(new Runnable() {
            @Override
            public void run() {*/
                toolbar.getMenu().findItem(R.id.p11_item_borrar).setVisible(false);
                toolbar.getMenu().findItem(R.id.p11_item_borrartodo).setVisible(false);

                ArrayList<Lectura> detalle = TblLectura.obtenerRegistrosXOrden(FrmEscaneoxPedido.this,ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId());

                switch (position){
                    case 0://lectura
                        break;
                    case 1://resumen
                        break;
                    case 2://detalle
                        toolbar.getMenu().findItem(R.id.p11_item_borrar).setVisible(detalle.size() > 0);
                        toolbar.getMenu().findItem(R.id.p11_item_borrartodo).setVisible(detalle.size()> 1);
                        break;
                }
           /* }
        });*/
    }

    public void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void configurarSonido() {
        sound = new SoundManager(FrmEscaneoxPedido.this);
        switch (ConfApp.OPERADORLOGEADO.getTipo()) {
            case 1:
                sound.confifureSound(ConfApp.SOUND_ADMIN);
                break;
            case 2:
                sound.confifureSound(ConfApp.SOUND_SUPERVISOR);
                break;
            default:
                sound.confifureSound(ConfApp.SOUND_OPERATOR);
                break;
        }
    }

    /****************************************  PANEL LECTURA *****************************************************/

    private boolean estaLeyendo = false;

    private void preparePnlLectura(View view) {
        txtcodigo = findViewById(R.id.p11_lblcodigonombre);
        txtdescripcion = findViewById(R.id.p11_lblDescripcionnombre);
        txtcaja = findViewById(R.id.p11_lblcajanombre);
        txtpeso = findViewById(R.id.p11_lblpesonombre);
        txtlote = findViewById(R.id.p11_lbllotenombre);
        txtserie = findViewById(R.id.p11_lblsublotenombre);
        txtfechasacrificio = findViewById(R.id.p11_lblfechasacrificionombre);
        txtfechaproceso = findViewById(R.id.p11_lblfechaprocesonombre);
        txtfechavencimiento = findViewById(R.id.p11_lblfechavencimientonombre);

        txtpesototal = findViewById(R.id.p11_lblPesoTotal);
        txtcantidadcajas = findViewById(R.id.p11_lblCantCajas);

        txtLectura = view.findViewById(R.id.p11_txtCodigoBarra);
        txtLectura.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if ((keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // String codigo = txtLectura.getText().toString().replaceAll("\\s", "");
                        String codigo = txtLectura.getText().toString();
                        txtLectura.setText("");
                        ocultarTeclado();

                        if ((codigo.length() == 50 || codigo.length() == 21) && !estaLeyendo && Utils.esCodigoValido(codigo)) {
                            estaLeyendo = true;
                            txtLectura.setEnabled(false);
                            if (!ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getAtentido())
                                continuar_Comprobacion(codigo);
                            else {
                                mostrarFormularioMensaje("ORDEN NO DISPONIBLE", "LA ORDEN <font color='#FF7F27'>" + ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId() + "</font> DEL CLIENTE: " + ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getCliente() + ", NO SE PUEDE MODIFICAR", sound.msg_error, R.color.red, true);
                            }
                            return true;
                        } else {
                            //System.out.println(codigo.length()+" "+Boolean.valueOf(estaLeyendo)+""+Boolean.valueOf(Utils.esCodigoValido(codigo)));
                            sound.play_error();
                            tabLayout.setBackgroundColor(getResources().getColor(R.color.red));
                            mostrarTimerInmediatamente();
                            return false;
                        }
                    }
                    return false;
                }
                return false;
            }
        });
        txtLectura.requestFocus();
    }

    String temp;
    private void continuar_Comprobacion(final String codigo_barra) {
        String idproducto = ConfApp.CODIGO_BARRA[3].toString();
        Date fecha = (Date) ConfApp.CODIGO_BARRA[5];
        Double peso = (Double) ConfApp.CODIGO_BARRA[7];
        Integer nopiezas = (Integer) ConfApp.CODIGO_BARRA[8];
        String sublote = ConfApp.CODIGO_BARRA[9].toString();
        String serie = ConfApp.CODIGO_BARRA[11].toString();
        boolean isProductoAcutorizo=false;

        temp = "El PRODUCTO" +
                "<BR><font color='#CB3234'>CODIGO:</font> " + idproducto +
                "<BR><font color='#CB3234'>FECHA:</font> " + Utils.bd.C_DateToAppFormat(fecha) +
                "<BR><font color='#CB3234'>SUBLOTE:</font> " + sublote +
                "<BR><font color='#CB3234'>PESO:</font> " + String.format("%,.2f", peso) +
                "<BR><font color='#CB3234'>SERIE:</font> " + serie +
                "<BR><font color='#CB3234'>CAJA:</font> " + codigo_barra;

        obj_lectura = new Lectura();
        obj_producto = TblProducto.obtenerRegistroxCodigo(FrmEscaneoxPedido.this, idproducto);// ConfApp.CODEBAR_IDPRODUCTO


        if (obj_producto != null) {
            Log.i(LOG_TAG, "BuscarProuducto"+ obj_producto.toString());

            isProductoAcutorizo = TblPedido_Detalle.esProductoValido(FrmEscaneoxPedido.this, ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId(), obj_producto.getCodigo_softland());

            if (isProductoAcutorizo) { //El producto esta dentro de la horden
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            double existencia = ConfApp.BDOPERATION.Get_Existecia(obj_producto.getCodigo_softland(), ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getBodega().getId());

                            if (existencia >=peso) {
                                mostrarCodigoDescompuesto(idproducto, obj_producto.getNombre(),codigo_barra,"" + String.format("%,.2f", peso),sublote,serie,Utils.bd.C_DateToAppFormat(fecha));

                                if (ConfApp.USER_DTS) {
                                    obj_lectura = new Lectura();
                                    obj_lectura.setFecha(fecha);
                                    obj_lectura.setFecha_proceso(new Date(System.currentTimeMillis()));
                                    obj_lectura.setCodigo(idproducto);
                                    obj_lectura.setPeso(peso);
                                    obj_lectura.setPiezas(nopiezas);
                                    obj_lectura.setSerie(serie);
                                    obj_lectura.setBarra(codigo_barra);
                                    obj_lectura.setIdcamion(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getCamion().getId());
                                    obj_lectura.setIdconductor(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getConductor().getId());//validar si es necesario agregar este campo aca.
                                    obj_lectura.setIdorden(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId());
                                    obj_lectura.setIdoperador(ConfApp.OPERADORLOGEADO.getId());
                                    obj_lectura.setIdservidor(0);

                                    if (!TblLectura.cajaEstaRegistrada(FrmEscaneoxPedido.this, codigo_barra)) {         //NO ESTA REGISTRADA LA CAJA LOCALMENTE
                                        if (!ConfApp.BDOPERATION.cajaEstaRegistrada(codigo_barra)) {                            //NO ESTA REGISTRADA LA CAJA EN EL SERVIDOR
                                            if (puedoagregarCajaAlaOrden(peso, idproducto)) {                                   //VALIDO SI EL PRODUCTO ESCANEADO FUE SOLICITADO EN LA ORDEN
                                                if (puedoAgregarCajaAlCamion( ConfApp.ORDEN_TRABAJADA_ACTUALMENTE,peso)) {      //DEVUELVE TRUE SI HAY ESPACIO EN EL CAMION PARA AGREGAR LA CAJA

                                                    Integer IDGENERADO = ConfApp.BDOPERATION.guardar_Caja(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE, obj_lectura,ConfApp.OPERADORLOGEADO, ConfApp.UUID_FROM_DEVICE);
                                                    obj_lectura.setIdservidor(IDGENERADO);

                                                    if(IDGENERADO>0 && ConfApp.BDOPERATION.actualizar_existencia(obj_producto.getCodigo_softland(),ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getBodega().getId(), obj_lectura.getPeso()*-1)){
                                                        if (TblLectura.guardar(FrmEscaneoxPedido.this, obj_lectura)) {
                                                            mostrarPantallaVerde();

                                                            if (ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getFecha_inicio() == null) { //REGISTRA LA APERTURA DE LA ORDEN
                                                                ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.setFecha_inicio(new Date(System.currentTimeMillis()));
                                                                ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.setOperador(ConfApp.OPERADORLOGEADO);
                                                                TblPedido.modificar(FrmEscaneoxPedido.this, ConfApp.ORDEN_TRABAJADA_ACTUALMENTE);
                                                            }
                                                        } else {//ERROR AL GUARDAR
                                                            mostrarFormularioMensaje("ERROR AL GUARDAR", "NO SE PUDO GUARDAR LA LECTURA", sound.msg_error, R.color.red, true);
                                                        }
                                                    }
                                                } else {
                                                    mostrarFormularioMensaje("PESO EXCEDIDO", "EL PRODUCTO " + obj_producto.getNombre() + ", EXCEDE EL PESO PERMITIDO POR EL CAMION", sound.msg_error, R.color.red, true);
                                                }
                                            } else {//DETALLE DE PRODUCTO COMPLETO
                                                mostrarFormularioMensaje("PESO EXCEDIDO", "EL PRODUCTO " + obj_producto.getNombre() + ", EXCEDE EL PESO PERMITIDO PARA LA ORDEN", sound.msg_error, R.color.red, true);
                                            }
                                        }else {//DETALLE DE PRODUCTO COMPLETO
                                            mostrarFormularioMensaje("CAJA REGISTRADA", "CAJA " + codigo_barra + ",<BR>YA FUE REGISTRADA POR OTRO OPERARIO", sound.msg_error, R.color.red, true);
                                        }
                                    } else {
                                        mostrarFormularioMensaje("CAJA REPETIDA", "CAJA " + codigo_barra + ",<BR>YA ESTA REGISTRADA", sound.msg_error, R.color.red, true);
                                    }
                                } else {
                                    mostrarFormularioMensaje("USUARIO ADMINISTRADOR", "ESTE USUARIO NO PUEDE REGISTRAR OPERACIONES", sound.msg_error, R.color.red, true);
                                }
                            } else {//EL PRODUCTO NO TIENE SUFICIENTE EXISTENCIA EN LA BODEGA
                                temp = "<BR>EL PRODUCTO<BR><font color='#FF7F27'>NOMBRE:</font> " + obj_producto.getNombre() + ", <BR>NO CUENTA CON EXISTENCIA SUFICIENTE";
                                mostrarFormularioMensaje("PRODUCTO SIN EXISTENCIA ", temp, sound.msg_error, R.color.red, true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            } else {//PRODUCTO NO SOLICITADO EN LA ORDEN
                temp = "<BR>EL PRODUCTO<BR><font color='#FF7F27'>NOMBRE:</font> " + obj_producto.getNombre() + ", <BR>NO FUE SOLICITADO EN ESTA ORDEN";
                mostrarFormularioMensaje("PRODUCTO NO SOLICITADO EN LA ORDEN ", temp, sound.msg_error, R.color.red, true);
            }
        } else {//EL PRODUCTO NO ESTA REGISTRADO
            temp += "<BR>EL PRODUCTO NO ESTA REGISTRADO";
            mostrarFormularioMensaje("PRODUCTO NO ESTA REGISTRADO", temp, sound.msg_error, R.color.red, true);
        }
    }

    private void mostrarPantallaVerde() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sound.play(sound.msg_ok);
                tabLayout.setBackgroundColor(getResources().getColor(R.color.lightGreed));
                viewPager.setBackgroundColor(getResources().getColor(R.color.lightGreed));
                mostrarTimer();

                txtLectura.setEnabled(true);
                txtLectura.requestFocus();
            }
        });
    }

    private void mostrarPantallaRoja() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sound.play(sound.msg_error);
                tabLayout.setBackgroundColor(getResources().getColor(R.color.red));
                viewPager.setBackgroundColor(getResources().getColor(R.color.red));
                mostrarTimer();

                txtLectura.setEnabled(true);
                txtLectura.requestFocus();
            }
        });
    }

    private boolean puedoAgregarCajaAlCamion(Pedido orden, Double PesoDeLaCaja) {
        Camion c = TblCamion.obtenerRegistro(FrmEscaneoxPedido.this, orden.getCamion().getId());
        double PesoMaximoDeProducto= ConfApp.BDOPERATION.Get_PesoCamion(c);
        return (PesoMaximoDeProducto + PesoDeLaCaja) <= c.getMax();
    }

    private boolean puedoagregarCajaAlaOrden(Double PESO_NUEVACAJA, String CODIGO_PRODUCTO) {
        TableViewModel t = new TableViewModel(FrmEscaneoxPedido.this, getSQLQUERY() + " WHERE T.CODIGO='" + CODIGO_PRODUCTO + "'");
        double PESO_SOLICITADO = 0.0, PESO_ESCANEADO = 0.0,PESO_TOTAL = 0.0;

        PESO_SOLICITADO = Double.parseDouble(t.getCellList().get(0).get(2).getData().toString());
        PESO_ESCANEADO = Double.parseDouble(t.getCellList().get(0).get(3).getData().toString()); // PESO ESCANEADO

        PESO_TOTAL = PESO_ESCANEADO + PESO_NUEVACAJA;
        return PESO_TOTAL<=PESO_SOLICITADO;
    }

    private void mostrarCodigoDescompuesto(String idproducto, String nombre, String codigo_barra, String peso, String sublote, String serie, String fecha) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtcodigo.setText(idproducto);
                txtdescripcion.setText(nombre);
                txtcaja.setText(codigo_barra);
                txtpeso.setText(peso);
                txtlote.setText(sublote);
                txtserie.setText(serie);
                txtfechasacrificio.setText(fecha);
            }
        });
    }



    //cambiarpor showMessageBox
    private void mostrarFormularioMensaje(String Titulo, String cuerpoMensaje, Integer sonido, Integer color, boolean MostrarMensaje) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sound.play(sonido);

                tabLayout.setBackgroundColor(getResources().getColor(color));
                viewPager.setBackgroundColor(getResources().getColor(color));
                mostrarTimer();

                if (MostrarMensaje) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FrmEscaneoxPedido.this);
                    builder.setIcon(R.drawable.img_logo);
                    builder.setTitle(Html.fromHtml("<font color='#FF7F27'><small>" + Titulo + "</small></font>"));
                    builder.setMessage(Html.fromHtml("<small>" + cuerpoMensaje + "</small>"))
                            .setPositiveButton("CONTINUAR",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            txtLectura.setEnabled(true);
                                            txtLectura.requestFocus();
                                            dialog.cancel();
                                        }
                                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    txtLectura.setEnabled(true);
                    txtLectura.requestFocus();
                }
            }
        });
    }

    private void mostrarTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(ConfApp.TIMER_READ_WAIT, ConfApp.TIMER_READ_WAIT) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                limpiarFormulario();
            }
        };
        countDownTimer.start();
    }

    private void mostrarTimerInmediatamente() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                CountDownTimer countDownTimer = new CountDownTimer(200, 100) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        ocultarTeclado();
                        limpiarFormulario();
                    }
                };
                countDownTimer.start();
            }
        });
    }

    public void limpiarFormulario() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tabLayout.setBackgroundColor(getResources().getColor(R.color.white));
                viewPager.setBackgroundColor(getResources().getColor(R.color.white));
                txtcodigo.setText("");
                txtdescripcion.setText("");
                txtcaja.setText("");
                txtpeso.setText("");
                txtlote.setText("");
                txtserie.setText("");
                txtfechasacrificio.setText("");
                txtfechaproceso.setText("");
                txtfechavencimiento.setText("");
                txtpesototal.setText("");
                txtcantidadcajas.setText("");
                txtLectura.setEnabled(true);
                txtLectura.setText("");
                txtLectura.requestFocus();
                estaLeyendo = false;
                codigoAEliminar = "";
            }
        });
    }

    /****************************************  PANEL RESUMEN   *****************************************************/
    private void preparePnlResumen(View view) {
        lblBodegaNombre = findViewById(R.id.p11_lblBodeganombre);
        lblBodegaNombre.setText(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getBodega().getId()+"-"+ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getBodega().getNombre());

        txtCamion= findViewById(R.id.p11_lblCamionnombre);
        txtCamion.setText("" + ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getCamion().getNoplaca());
        txtCamion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPopupSeleccionarCamion(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE);
            }
        });

        txtConductor = findViewById(R.id.p11_lblConductornombre);
        txtConductor.setText("" + ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getConductor().getNombre());
        txtConductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPopupSeleccionarConductor(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE);
            }
        });

        txtMarchamo = findViewById(R.id.p11_lblMarchamonombre);
        txtMarchamo.setText("" + ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getMarchamo());
        txtMarchamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarPopupSeleccionarMarchamo(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE);
            }
        });

        final LayoutInflater inflater = this.getLayoutInflater();
        findViewById(R.id.p11_btnFinalizar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // if (!ConfApp.USER_ESTIBADOR) return;

                if(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getMarchamo().isEmpty()){
                    mostrarFormularioMensaje("DATOS INCOMPLETO","Favor ingresar el marchamo para cerrar la orden",sound.msg_error,R.color.red,true);
                    txtMarchamo.requestFocus();
                    return ;
                }

                Object[] respuesta = estaCompletoDetalleOrden();
                AlertDialog.Builder builder = new AlertDialog.Builder(FrmEscaneoxPedido.this);
                View dialogView = inflater.inflate(R.layout.item_tabla, null);

                TableView visor = dialogView.findViewById(R.id.p11_tbldialog);

                TableViewModel modelo = (TableViewModel) respuesta[2]; //new TableViewModel(FrmEscaneoxOrden.this);
                TableViewAdapter adapter = new TableViewAdapter(FrmEscaneoxPedido.this, modelo);
                TableViewAdapter.ON_CORNER_LISTENER = true;

                visor.setAdapter(adapter);
                if (modelo.getRowHeaderList().size() > 0) {
                    adapter.setAllItems(modelo.getColumnHeaderList(), modelo.getRowHeaderList(), modelo.getCellList());
                    builder.setView(dialogView);
                }

                builder.setTitle(Boolean.valueOf(respuesta[0].toString()) ? "ORDEN COMPLETADA" : "ORDEN INCOMPLETA");
                builder.setMessage(Boolean.valueOf(respuesta[0].toString()) ? Html.fromHtml("Desea cerrar la Orden <font color='#FF7F27'>" + ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId()+ "</font>" ): Html.fromHtml(respuesta[1].toString()));

                builder.setIcon(R.drawable.img_logo);
                builder.setPositiveButton("SI",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActioncerrarOrden();
                                dialog.cancel();
                            }
                        });
                builder.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();

            }
        });

        Log.i(LOG_TAG, "preparePnlResumen Querey:"+getSQLQUERY());

        TblVisorConsolidado = findViewById(R.id.p11_tblconsolidado);
        TblModelConsolidado = new TableViewModel(FrmEscaneoxPedido.this, getSQLQUERY());
        TblAdapterConsolidado = new TableViewAdapter(FrmEscaneoxPedido.this, TblModelConsolidado);       //
        TableViewAdapter.ON_CORNER_LISTENER = true;

        if (TblModelConsolidado.getRowHeaderList().size() > 0) {
            TblVisorConsolidado.setVisibility(View.VISIBLE);
            TblVisorConsolidado.setAdapter(TblAdapterConsolidado);
            TblAdapterConsolidado.setAllItems(TblModelConsolidado.getColumnHeaderList(), TblModelConsolidado.getRowHeaderList(), TblModelConsolidado.getCellList());
        } else {
            TblVisorConsolidado.setVisibility(View.INVISIBLE);
        }
    }

    private void mostrarPopupSeleccionarCamion(Pedido obj_pedido) {
        ArrayAdapter<Camion> adapter_camion;
        TextInputEditText txtBuscarCamion;
        ListView lv_camion;

        final RelativeLayout panel = (RelativeLayout) LayoutInflater.from(FrmEscaneoxPedido.this).inflate(R.layout.frm_lista, null);
        androidx.appcompat.app.AlertDialog.Builder componente = new androidx.appcompat.app.AlertDialog.Builder(FrmEscaneoxPedido.this);
        componente.setTitle("Modificar Camion");

        final ImageButton btnAgrearUsuario = panel.findViewById(R.id.btnAgregar);
        btnAgrearUsuario.setVisibility(View.INVISIBLE);//(ConfApp.USER_DTS || ConfApp.USER_SUPERVISOR) ? View.VISIBLE : View.INVISIBLE

        componente.setCancelable(false);

        final Vector<Camion> list_camion = TblCamion.obtenerRegistros(FrmEscaneoxPedido.this);
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

        final androidx.appcompat.app.AlertDialog FrmSeleccionarCamion = componente.create();
        FrmSeleccionarCamion.show();

        lv_camion.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                Camion item = (Camion) arg0.getItemAtPosition(position);
                //comprobar si se puede pasar todo el detalle a esa orden.
                obj_pedido.setCamion(item);
                TblPedido.modificar(FrmEscaneoxPedido.this, obj_pedido);
                txtCamion.setText( obj_pedido.getCamion().getNoplaca());
                FrmSeleccionarCamion.dismiss();
            }
        });
    }

    private void mostrarPopupSeleccionarConductor(Pedido obj_pedido) {
        ArrayAdapter<Conductor> adapter_conductor;
        ListView lv_conductor;
        TextInputEditText txtBuscarConductor;


        final RelativeLayout panel = (RelativeLayout) LayoutInflater.from(FrmEscaneoxPedido.this).inflate(R.layout.frm_lista, null);
        AlertDialog.Builder componente = new AlertDialog.Builder(FrmEscaneoxPedido.this);
        componente.setTitle("Modificar conductor");

        final ImageButton btnAgrearConductor = panel.findViewById(R.id.btnAgregar);
        btnAgrearConductor.setVisibility(View.INVISIBLE);//(ConfApp.USER_DTS || ConfApp.USER_SUPERVISOR) ? View.VISIBLE : View.INVISIBLE

        componente.setCancelable(false);

        final ArrayList<Conductor> list_conductor = TblConductor.obtenerRegistros(FrmEscaneoxPedido.this);
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
                TblPedido.modificar(FrmEscaneoxPedido.this, obj_pedido);
                txtConductor.setText(obj_pedido.getConductor().getNombre());
                FrmSeleccionarConductor.dismiss();
            }
        });
    }

    private void mostrarPopupSeleccionarMarchamo(Pedido obj_pedido){
        AlertDialog.Builder componente = new AlertDialog.Builder(FrmEscaneoxPedido.this);
        componente.setTitle("Ingrese el # de marchamo");
        componente.setCancelable(false);

        final EditText input = new EditText(this);
        input.setText(obj_pedido.getMarchamo());

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        componente.setView(input);


        componente.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                obj_pedido.setMarchamo(input.getText().toString());
                TblPedido.modificar(FrmEscaneoxPedido.this,obj_pedido);
                txtMarchamo.setText(obj_pedido.getMarchamo());
            }
        });

        componente.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        componente.show();
    }

    /**
     *
     * @return Object[]
     *
     * Object[0] = Boolean              Resultado de la comprobacion.
     * Object[1] = String               Mensaje para el objeto Alert.
     * Object[]  = TableViewModel       Lista de item que no cumplieron los limites.
     */
    private Object[] estaCompletoDetalleOrden() {
        double PESO_SOLICITADO = 0.0, PESO_ESCANEADO = 0.0, PESO_LIMITE = 0.0,PESO_LIMITE_INFERIOR = 0.0,PESO_LIMITE_SUPERIOR = 0.0;
        ArrayList<ArrayList> Filas = new ArrayList<>();
        Object[] objeto = new Object[3];
        boolean respuesta = true;

        StringBuilder Mensaje = new StringBuilder();
        Mensaje.append("Desea cerrar la Orden <font color='#FF7F27'>" + ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId()+ "</font>");
        Mensaje.append("<BR>Aunque se encuenten pendiente de completar los siguiente producto:");
        TableViewModel t = new TableViewModel(FrmEscaneoxPedido.this, getSQLQUERY());

        TableViewModel Modelo_datos = new TableViewModel(FrmEscaneoxPedido.this);
        Modelo_datos.addHeader(new ArrayList<>(Arrays.asList("CODIGO", "NOMBRE", "LBS")));

        for (int it = 0; it < t.getCellList().size(); it++) {
            PESO_SOLICITADO = Double.parseDouble(t.getCellList().get(it).get(2).getData().toString());
            PESO_ESCANEADO = Double.parseDouble(t.getCellList().get(it).get(3).getData().toString());

            PESO_LIMITE_INFERIOR =PESO_SOLICITADO - ConfApp.LIMIT_WEIGHT;
            PESO_LIMITE_SUPERIOR = PESO_SOLICITADO + ConfApp.LIMIT_WEIGHT;

            if(PESO_ESCANEADO>=PESO_LIMITE_INFERIOR && PESO_ESCANEADO<=PESO_LIMITE_SUPERIOR){
                respuesta = respuesta && true;
            }else{
                String UM =  (PESO_ESCANEADO < PESO_LIMITE_INFERIOR) ? "-" +String.format("%,.2f", (PESO_SOLICITADO - PESO_ESCANEADO)) : "+" +String.format("%,.2f", (PESO_LIMITE_SUPERIOR - PESO_ESCANEADO)) ;
                Modelo_datos.addRow(new ArrayList<>(Arrays.asList(t.getCellList().get(it).get(0).getData().toString(), t.getCellList().get(it).get(1).getData().toString(), UM)));

                respuesta = respuesta && false;
            }
        }

        objeto[0] = respuesta;
        objeto[1] = Mensaje;
        objeto[2] = Modelo_datos;

        return objeto;
    }

    private void ActioncerrarOrden() {
        ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.setFecha_fin(new Date(System.currentTimeMillis()));
        ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.setAtentido(true);

        if (TblPedido.modificar(FrmEscaneoxPedido.this, ConfApp.ORDEN_TRABAJADA_ACTUALMENTE)) {
            mostrarFormularioMensaje("Cierre de Orden","Se ha cerrado la orden "+ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId(),sound.msg_ok, R.color.green, true);
        } else {
            Toast.makeText(FrmEscaneoxPedido.this, "No se modifico", Toast.LENGTH_SHORT);
        }

        if (ConfApp.USER_ESTIBADOR) {
            findViewById(R.id.p11_btnFinalizar).setVisibility(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getFecha_fin() != null ? View.INVISIBLE : View.VISIBLE);
        } else {
            findViewById(R.id.p11_btnFinalizar).setVisibility(View.INVISIBLE);
        }
    }

    /**
     *
     * @return String retorno un sqlstring en el formato
     * EJEMPLO REGORNO DE GETSQLQUERY()
     * CODIGO       DESCRIPCION,            LIBRS SOLICITADAS   LIBRS LEIDAS        CANT PIEZAS     # CAJAS
     * 50016        RECORTE DE EMBUTIDO         60.00               45.12               10              1
     *
     */
    public String getSQLQUERY() {
        return "SELECT T.CODIGO,T.DESCRIPCION,printf('%.2f', T.PESO) AS [LBR SOLICITADAS],printf('%.2f', COALESCE(B.PESO,0,B.PESO) )AS [LBS LEIDAS], COALESCE(B.PIEZAS,0,B.PIEZAS) AS [CANT PIEZAS],COALESCE(B.CAJAS,0,B.CAJAS) AS [CAJAS ESCANEADAS]\n" +
                "                 FROM(\n" +
                "                           SELECT PR.codigo AS CODIGO,PR.nombre AS DESCRIPCION,SUM(DT.cantidad) AS PESO \n" +
                "                           FROM soft_pedido_detalle DT LEFT JOIN prod_producto PR ON PR.codigo_softland=DT.idproducto \n" +
                "                           WHERE DT.idpedido='"+ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId() +"'\n" +
                "                           GROUP BY DT.idproducto\n" +
                "                     ) AS T \n" +
                "                 LEFT JOIN (\n" +
                "                           SELECT host_lectura.codigo AS CODIGO,count(host_lectura.id) AS CAJAS,sum(host_lectura.piezas) AS PIEZAS,  SUM(host_lectura.peso) AS  PESO  \n" +
                "                           FROM host_lectura \n" +
                "                           WHERE host_lectura.idorden='"+ ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId() +"'\n" +
                "                           GROUP BY host_lectura.codigo\n" +
                "                           ) AS B ON T.CODIGO=B.CODIGO\n";
    }

    /****************************************  PANEL DETALLE   *****************************************************/
    TextInputEditText txtBuscarDetalle = null;

    private TableViewAdapter TblAdapterConsolidado, TblAdapterDetalle = null;
    private TableViewModel TblModelConsolidado, TblModelDetalle = null;
    private TableView TblVisorConsolidado, TblVisorDetalle = null;
    private Filter tableViewFilter = null;
    TextInputLayout LayoutinputSearch;

    private void preparePnlDetalle(View view) {
        String  SQLQUERY = "SELECT LE.codigo AS CODIGO,PRO.nombre AS DESCRIPCION, LE.fecha AS FECHA_HORA,printf(\"%.2f\", LE.peso)  AS PESO , LE.barra AS BARRA,(CASE LE.idservidor WHEN 0  THEN 'NO' ELSE 'SI' END) AS ENVIADO \n" +
                "FROM host_lectura LE INNER JOIN prod_producto PRO ON LE.codigo=PRO.codigo \n" +
                "WHERE LE.idorden='" + ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getId() + "'  \n" +
                "ORDER BY LE.fecha DESC \n";

        Log.i(LOG_TAG, "preparePnlDetalle Query:"+SQLQUERY);

        TblVisorDetalle = findViewById(R.id.p10_tbldetalle);
        LayoutinputSearch = findViewById(R.id.p11_txtLayoutDetalle);
        txtBuscarDetalle = (TextInputEditText) view.findViewById(R.id.p11_txtBuscarCodigoBarra);

        TblModelDetalle = new TableViewModel(FrmEscaneoxPedido.this, SQLQUERY);
        TblAdapterDetalle = new TableViewAdapter(FrmEscaneoxPedido.this, TblModelDetalle);
        TableViewAdapter.ON_CORNER_LISTENER = true;

        if (TblModelDetalle.getRowHeaderList().size() > 0) {
            TblVisorDetalle.setVisibility(View.VISIBLE);
            TblVisorDetalle.setAdapter(TblAdapterDetalle);
            TblAdapterDetalle.setAllItems(TblModelDetalle.getColumnHeaderList(), TblModelDetalle.getRowHeaderList(), TblModelDetalle.getCellList());
            tableViewFilter = new Filter(TblVisorDetalle);
            TblVisorDetalle.setTableViewListener(new ITableViewListener() {
                @Override
                public void onCellClicked(@NonNull RecyclerView.ViewHolder cellview, int column, int row) {
                    try {

                        if (txtBuscarDetalle.getText().length() < 1) {
                            TblVisorDetalle.setSelectedRow(row);
                            codigoAEliminar = TblModelDetalle.getCellList().get(TblVisorDetalle.getSelectedRow()).get(4).getData().toString();
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCellLongPressed(@NonNull RecyclerView.ViewHolder cellview, int column, int row) {

                }

                @Override
                public void onColumnHeaderClicked(@NonNull RecyclerView.ViewHolder cellview, int column) {

                }

                @Override
                public void onColumnHeaderLongPressed(@NonNull RecyclerView.ViewHolder cellview, int column) {

                }

                @Override
                public void onRowHeaderClicked(@NonNull RecyclerView.ViewHolder cellview, int column) {

                }

                @Override
                public void onRowHeaderLongPressed(@NonNull RecyclerView.ViewHolder cellview, int column) {

                }
            });

            TblVisorDetalle.getFilterHandler().addFilterChangedListener(new FilterChangedListener() {
                @Override
                public void onFilterChanged(List filteredCellItems, List filteredRowHeaderItems) {
                    super.onFilterChanged(filteredCellItems, filteredRowHeaderItems);
                    List<List<Cell>> temp = filteredCellItems;
                    if (temp.size() > 0) {
                        codigoAEliminar = temp.get(0).get(4).getData().toString();
                        Log.i(LOG_TAG, "preparePnlDetalle CodigoAEliminar:"+codigoAEliminar);
                    } else {
                        codigoAEliminar = "";
                    }

                }

            });
            LayoutinputSearch.setVisibility(View.VISIBLE);
        } else {
            TblVisorDetalle.setVisibility(View.INVISIBLE);
            LayoutinputSearch.setVisibility(View.INVISIBLE);
        }

        txtBuscarDetalle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tableViewFilter.set(4, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txtBuscarDetalle.requestFocus();
    }

    /**
     * Clase segura para eliminacion de lecturas
     */
    class AsyncTaskRunnerElimiarLecturas extends AsyncTask<String, String, String> {
        Context referencia;
        ProgressDialog progressDialog;
        String idOrden;

        public AsyncTaskRunnerElimiarLecturas(Context frmImportar, String IdOrden) {
            referencia = frmImportar;
            this.idOrden = IdOrden;
        }

        @Override
        protected String doInBackground(String... strings) {
            long resultado = -1;
            String[] args = new String[]{"" + idOrden};


            ArrayList<Lectura> lecturas = TblLectura.obtenerRegistrosXOrden(referencia, idOrden);


            for (int i = 0; i < lecturas.size(); i++) {
                Producto producto = TblProducto.obtenerRegistroxCodigo(referencia,lecturas.get(i).getCodigo());

                String temp = "BORRANDO" +
                        "<BR><font color='#CB3234'># REGISTRO:</font> " + (i + 1) +
                        "<BR><font color='#CB3234'># CAJA:</font> " + lecturas.get(i).getBarra();

                publishProgress(temp, "Borrando Registros"); // Calls onProgressUpdate()

                if (lecturas.get(i).getIdservidor() != 0) {
                    //TblLecturaEliminada.guardar(referencia,new LecturaEliminada(registro.getIdservidor(), registro.getBarra(), idOrden));
                    if(ConfApp.BDOPERATION.borrar_Caja(lecturas.get(i).getBarra())){
                        ConfApp.BDOPERATION.actualizar_existencia(producto.getCodigo_softland(),ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getBodega().getId(), obj_lectura.getPeso());
                        TblLectura.borrarCaja(FrmEscaneoxPedido.this, lecturas.get(i).getBarra(), true);
                    }
                }

            }


            //TblLectura.borrarLecturaxOrden(referencia,idOrden);

            return "";
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
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            if (referencia instanceof FrmEscaneoxPedido) {
                ((FrmEscaneoxPedido) referencia).mostrarMenuxPestana(2);
                ((FrmEscaneoxPedido) referencia).preparePnlDetalle(pnldetalle.getView());
            }
        }

        @Override
        protected void onProgressUpdate(String... params) {
            super.onProgressUpdate(params);
            progressDialog.setTitle(Html.fromHtml("<font color='#FF7F27'><small>" + params[1].toString() + "</small></font>"));
            progressDialog.setMessage(Html.fromHtml("<font color='#000000'><small>" + params[0].toString() + "</small></font>"));

        }
    }

}