package com.sdn.cacique.spop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.sdn.bd.dao.host.TblContenedor;
import com.sdn.bd.dao.host.TblContenedorDetalle;
import com.sdn.bd.dao.host.TblLectura;
import com.sdn.bd.dao.produccion.TblProducto;
import com.sdn.bd.objeto.host.Contenedor_Detalle;
import com.sdn.bd.objeto.produccion.Producto;
import com.sdn.cacique.util.ConfApp;
import com.sdn.cacique.util.Utils;
import com.sdn.sound.SoundManager;
import com.sdn.tableview.TableViewAdapter;
import com.sdn.tableview.TableViewModel;
import com.sdn.tableview.model.Cell;

import java.util.Date;
import java.util.List;

public class FrmEscaneoLibre extends AppCompatActivity {
    private static final String LOG_TAG = FrmEscaneoLibre.class.getSimpleName();

    private TextView txtLectura, txtcodigo, txtdescripcion, txtcaja, txtpeso, txtlote, txtserie, txtfechasacrificio, txtfechaproceso, txtfechavencimiento, txtpesototal, txtcantidadcajas;
    private TextView lblBodegaNombre, txtCamion, txtConductor, txtMarchamo;

    AdapterContenedor adaptador;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    TabLayoutMediator mediator;

    Toolbar toolbar;
    String codigoAEliminar = "";

    PnlLectura pnlLectura = null;
    PnlResumenContenedor pnlconsolidado = null;
    PnlDetalle pnldetalle = null;
    //Object[] detalle = null;
   Contenedor_Detalle obj_lectura = new Contenedor_Detalle();

    SoundManager sound;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //updateLista();
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
        inflater.inflate(R.menu.mnu_escaneolibre, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.p12_item_borrar /*&& ConfApp.USER_ESTIBADOR && !ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getAtentido()*/) {

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
                                        TblContenedorDetalle.borrarCaja(FrmEscaneoLibre.this,codigoAEliminar);
                                        mostrarMenuxPestana(2);
                                        preparePnlDetalle(pnldetalle.getView());
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                codigoAEliminar = "";
                //txtBuscarBarra_Detalle.setText("");
            }
        }

        if (id == R.id.p12_item_borrartodo /*&& ConfApp.USER_ESTIBADOR && !ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getAtentido()*/) {
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
                                    TblContenedorDetalle.borrarLecturaxContenedor(FrmEscaneoLibre.this ,ConfApp.CONTENEDOR_ACTUALMENTE.getId());
                                    mostrarMenuxPestana(2);
                                    preparePnlDetalle(pnldetalle.getView());
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
        setContentView(R.layout.frm_escaneo_libre);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(ConfApp.CONTENEDOR_ACTUALMENTE.getNombre());
        ((TextView) findViewById(R.id.p12_lblClientenombre)).setText(ConfApp.CONTENEDOR_ACTUALMENTE.getNombre());
       // ((TextView) findViewById(R.id.p12_lblDireccion)).setText(ConfApp.ORDEN_TRABAJADA_ACTUALMENTE.getDireccion());

        configurarSonido();

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);//mantenemos cargadoen memoria las pentanas

        adaptador = new AdapterContenedor(getSupportFragmentManager(), getLifecycle());
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
                    pnlconsolidado = (PnlResumenContenedor) adaptador.createFragment(position);
                    preparePnlResumen(pnlconsolidado.getView());
                }

                if (position == 2) {
                    pnldetalle = (PnlDetalle) adaptador.createFragment(position);
                    preparePnlDetalle(pnldetalle.getView());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }

        });
    }

    private void mostrarMenuxPestana(int position) {
        Integer NoRegistros =  TblContenedorDetalle.obtenerNoFilas(FrmEscaneoLibre.this,ConfApp.CONTENEDOR_ACTUALMENTE.getId());

        toolbar.getMenu().findItem(R.id.p12_item_borrar).setVisible(false);
        toolbar.getMenu().findItem(R.id.p12_item_borrartodo).setVisible(false);

        switch (position) {
            case 0://lectura
                break;
            case 1://resumen
                break;
            case 2://detalle
                toolbar.getMenu().findItem(R.id.p12_item_borrar).setVisible(NoRegistros > 0);
                toolbar.getMenu().findItem(R.id.p12_item_borrartodo).setVisible(NoRegistros > 1);
                break;
        }
    }

    private void configurarSonido() {
        sound = new SoundManager(FrmEscaneoLibre.this);
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

    public void ocultarTeclado() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /****************************************  PANEL LECTURA *****************************************************/
    private boolean estaLeyendo = false;
    Producto obj_producto = null;
    String temp;

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
                        String codigo = txtLectura.getText().toString();
                        txtLectura.setText("");
                        ocultarTeclado();

                        if ((codigo.length() == 50 || codigo.length() == 21) && !estaLeyendo && Utils.esCodigoValido(codigo)) {
                            estaLeyendo = true;
                            txtLectura.setEnabled(false);
                            continuar_Comprobacion(codigo);
                            return true;
                        } else {
                            sound.play_lectura_error();
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

    private void continuar_Comprobacion(final String codigo_barra) {
        String idproducto = ConfApp.CODIGO_BARRA[3].toString();
        Date fecha = (Date) ConfApp.CODIGO_BARRA[5];
        Double peso = (Double) ConfApp.CODIGO_BARRA[7];
        Integer nopiezas = (Integer) ConfApp.CODIGO_BARRA[8];
        String sublote = ConfApp.CODIGO_BARRA[9].toString();
        String serie = ConfApp.CODIGO_BARRA[11].toString();
        boolean isProductoAcutorizo = false;

        temp = "El PRODUCTO" +
                "<BR><font color='#CB3234'>CODIGO:</font> " + idproducto +
                "<BR><font color='#CB3234'>FECHA:</font> " + Utils.bd.C_DateToAppFormat(fecha) +
                "<BR><font color='#CB3234'>SUBLOTE:</font> " + sublote +
                "<BR><font color='#CB3234'>PESO:</font> " + String.format("%,.2f", peso) +
                "<BR><font color='#CB3234'>SERIE:</font> " + serie +
                "<BR><font color='#CB3234'>CAJA:</font> " + codigo_barra;

        obj_lectura = new Contenedor_Detalle();
        obj_producto = TblProducto.obtenerRegistroxCodigo(FrmEscaneoLibre.this, idproducto);// ConfApp.CODEBAR_IDPRODUCTO

        mostrarCodigoDescompuesto(idproducto, obj_producto.getNombre(), codigo_barra, "" + String.format("%,.2f", peso), sublote, serie, Utils.bd.C_DateToAppFormat(fecha));

        if (ConfApp.USER_DTS) {
            obj_lectura = new Contenedor_Detalle();
            obj_lectura.setFecha_creacion(new Date(System.currentTimeMillis()));
            obj_lectura.setFecha_proceso(fecha);
            obj_lectura.setCodigo(idproducto);
            obj_lectura.setPeso(peso);
            obj_lectura.setPiezas(nopiezas);
            obj_lectura.setSerie(serie);
            obj_lectura.setBarra(codigo_barra);
            obj_lectura.setTipo((codigo_barra.length()==50)?0:1);  //0=CARNICO     1=DERIVADO
            obj_lectura.setIdcontenedor(""+ConfApp.CONTENEDOR_ACTUALMENTE.getId());
            obj_lectura.setIdoperador(ConfApp.OPERADORLOGEADO.getId());
            obj_lectura.setIdservidor(0);

            if (!TblContenedorDetalle.cajaEstaRegistrada(FrmEscaneoLibre.this, codigo_barra)) {  //NO ESTA REGISTRADA LA CAJA LOCALMENTE
                if (TblContenedorDetalle.guardar(FrmEscaneoLibre.this, obj_lectura)) {
                    mostrarPantallaVerde();

                    if (ConfApp.CONTENEDOR_ACTUALMENTE.getFecha_inicio() == null) { //REGISTRA LA APERTURA DE LA ORDEN
                        ConfApp.CONTENEDOR_ACTUALMENTE.setFecha_inicio(new Date(System.currentTimeMillis()));
                        TblContenedor.modificar(FrmEscaneoLibre.this,ConfApp.CONTENEDOR_ACTUALMENTE);
                    }
                } else {//ERROR AL GUARDAR
                    mostrarFormularioMensaje("ERROR AL GUARDAR", "NO SE PUDO GUARDAR LA LECTURA", sound.msg_lectura_error, R.color.red, true);
                }
            } else {
                mostrarFormularioMensaje("CAJA REPETIDA", "CAJA " + codigo_barra + ",<BR>YA ESTA REGISTRADA", sound.msg_caja_repetida, R.color.red, true);
            }
        } else {
            mostrarFormularioMensaje("USUARIO ADMINISTRADOR", "ESTE USUARIO NO PUEDE REGISTRAR OPERACIONES", sound.msg_lectura_ok, R.color.red, true);
        }
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

    private void mostrarFormularioMensaje(String Titulo, String cuerpoMensaje, Integer sonido, Integer color, boolean MostrarMensaje) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sound.play(sonido);

                tabLayout.setBackgroundColor(getResources().getColor(color));
                viewPager.setBackgroundColor(getResources().getColor(color));
                mostrarTimer();

                if (MostrarMensaje) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(FrmEscaneoLibre.this);
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

    private void mostrarPantallaVerde() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                sound.play(sound.msg_caja_disponible);
                tabLayout.setBackgroundColor(getResources().getColor(R.color.lightGreed));
                viewPager.setBackgroundColor(getResources().getColor(R.color.lightGreed));
                mostrarTimer();

                txtLectura.setEnabled(true);
                txtLectura.requestFocus();
            }
        });
    }

    /****************************************  PANEL RESUMEN   *****************************************************/

    private void preparePnlResumen(View view) {
        final LayoutInflater inflater = this.getLayoutInflater();
        findViewById(R.id.p12_btnFinalizar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // if (!ConfApp.USER_ESTIBADOR) return;

                if (TblContenedorDetalle.obtenerNoFilas(FrmEscaneoLibre.this,ConfApp.CONTENEDOR_ACTUALMENTE.getId())<1) {
                    mostrarFormularioMensaje("DATOS INCOMPLETO", "Favor ingresar el cajas al documento", sound.msg_lectura_error, R.color.red, true);
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(FrmEscaneoLibre.this);
                View dialogView = inflater.inflate(R.layout.item_tabla, null);

                TableView visor = dialogView.findViewById(R.id.p11_tbldialog);

                TableViewModel modelo = new TableViewModel(FrmEscaneoLibre.this, getSQLQUERY());
                TableViewAdapter adapter = new TableViewAdapter(FrmEscaneoLibre.this, modelo);
                TableViewAdapter.ON_CORNER_LISTENER = true;

                visor.setAdapter(adapter);
                if (modelo.getRowHeaderList().size() > 0) {
                    adapter.setAllItems(modelo.getColumnHeaderList(), modelo.getRowHeaderList(), modelo.getCellList());
                    builder.setView(dialogView);
                }

                builder.setTitle("Cerrar documento");
                builder.setMessage(Html.fromHtml("Desea cerrar la documento <font color='#FF7F27'>" + ConfApp.CONTENEDOR_ACTUALMENTE.getNombre() + "</font>") );

                builder.setIcon(R.drawable.img_logo);
                builder.setPositiveButton("SI",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ActioncerrarDocumento();
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

        Log.i(LOG_TAG, "preparePnlResumen Querey:" + getSQLQUERY());

        TblVisorConsolidado = findViewById(R.id.p12_tblconsolidado);
        TblModelConsolidado = new TableViewModel(FrmEscaneoLibre.this, getSQLQUERY());
        TblAdapterConsolidado = new TableViewAdapter(FrmEscaneoLibre.this, TblModelConsolidado);       //
        TableViewAdapter.ON_CORNER_LISTENER = true;

        if (TblModelConsolidado.getRowHeaderList().size() > 0) {
            TblVisorConsolidado.setVisibility(View.VISIBLE);
            TblVisorConsolidado.setAdapter(TblAdapterConsolidado);
            TblAdapterConsolidado.setAllItems(TblModelConsolidado.getColumnHeaderList(), TblModelConsolidado.getRowHeaderList(), TblModelConsolidado.getCellList());
        } else {
            TblVisorConsolidado.setVisibility(View.INVISIBLE);
        }
    }

    private void ActioncerrarDocumento() {
        ConfApp.CONTENEDOR_ACTUALMENTE.setFecha_fin(new Date(System.currentTimeMillis()));

        if (TblContenedor.modificar(FrmEscaneoLibre.this, ConfApp.CONTENEDOR_ACTUALMENTE)) {
            mostrarFormularioMensaje("Cierre de Documento", "Se ha cerrado el documento " + ConfApp.CONTENEDOR_ACTUALMENTE.getId(), sound.msg_lectura_ok, R.color.green, true);
        } else {
            Toast.makeText(FrmEscaneoLibre.this, "No se modifico", Toast.LENGTH_SHORT);
        }

        if (ConfApp.USER_ESTIBADOR ) {
            findViewById(R.id.p12_btnFinalizar).setVisibility(ConfApp.CONTENEDOR_ACTUALMENTE.getFecha_fin() != null ? View.INVISIBLE : View.VISIBLE);
        } else {
            findViewById(R.id.p12_btnFinalizar).setVisibility(View.INVISIBLE);
        }

    }

    /**
     * @return String retorno un sqlstring en el formato
     * EJEMPLO REGORNO DE GETSQLQUERY()
     * CODIGO       DESCRIPCION,            LIBRS SOLICITADAS   LIBRS LEIDAS        CANT PIEZAS     # CAJAS
     * 50016        RECORTE DE EMBUTIDO         60.00               45.12               10              1
     */
    public String getSQLQUERY() {
        return "SELECT T.CODIGO,T.DESCRIPCION,printf('%.2f', T.PESO) AS [LBR SOLICITADAS],printf('%.2f', COALESCE(B.PESO,0,B.PESO) )AS [LBS LEIDAS], COALESCE(B.PIEZAS,0,B.PIEZAS) AS [CANT PIEZAS],COALESCE(B.CAJAS,0,B.CAJAS) AS [CAJAS ESCANEADAS]\t\t\n" +
                "FROM (\tSELECT PR.codigo AS CODIGO,PR.nombre AS DESCRIPCION,SUM(DT.peso) AS PESO\n" +
                "       FROM host_contenedor_detalle DT LEFT JOIN prod_producto PR ON PR.codigo=DT.codigo\n" +
                "       WHERE DT.idcontenedor="+ ConfApp.CONTENEDOR_ACTUALMENTE.getId() +"\n" +
                "       GROUP BY DT.codigo) AS T\n" +
                "       LEFT JOIN\n" +
                "               (SELECT codigo AS CODIGO,count(id) AS CAJAS,sum(piezas) AS PIEZAS,  SUM(peso) AS  PESO  \n" +
                "               FROM host_contenedor_detalle  \n" +
                "               WHERE idcontenedor="+ ConfApp.CONTENEDOR_ACTUALMENTE.getId() +"\n" +
                "               GROUP BY codigo) AS B \n" +
                "       ON T.codigo = B.codigo";
    }
    /****************************************  PANEL DETALLE   *****************************************************/

    TextInputEditText txtBuscarDetalle = null;

    private TableViewAdapter TblAdapterConsolidado, TblAdapterDetalle = null;
    private TableViewModel TblModelConsolidado, TblModelDetalle = null;
    private TableView TblVisorConsolidado, TblVisorDetalle = null;
    private Filter tableViewFilter = null;
    TextInputLayout LayoutinputSearch;

    private void preparePnlDetalle(View view) {
        String SQLQUERY = "SELECT LE.codigo AS CODIGO,PRO.nombre AS DESCRIPCION, LE.fecha_creacion AS FECHA_HORA,printf(\"%.2f\", LE.peso)  AS PESO , LE.barra AS BARRA,(CASE LE.idservidor WHEN 0  THEN 'NO' ELSE 'SI' END) AS ENVIADO \n" +
                "FROM host_contenedor_detalle LE LEFT JOIN prod_producto PRO ON LE.codigo=PRO.codigo \n" +
                "WHERE LE.idcontenedor=" + ConfApp.CONTENEDOR_ACTUALMENTE.getId() + "  \n" +
                "ORDER BY LE.fecha_creacion DESC \n";

        Log.i(LOG_TAG, "preparePnlDetalle Query:" + SQLQUERY);

        TblVisorDetalle = findViewById(R.id.p10_tbldetalle);
        LayoutinputSearch = findViewById(R.id.p11_txtLayoutDetalle);
        txtBuscarDetalle = (TextInputEditText) view.findViewById(R.id.p11_txtBuscarCodigoBarra);

        TblModelDetalle = new TableViewModel(FrmEscaneoLibre.this, SQLQUERY);
        TblAdapterDetalle = new TableViewAdapter(FrmEscaneoLibre.this, TblModelDetalle);
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
                        Log.i(LOG_TAG, "preparePnlDetalle CodigoAEliminar:" + codigoAEliminar);
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
}