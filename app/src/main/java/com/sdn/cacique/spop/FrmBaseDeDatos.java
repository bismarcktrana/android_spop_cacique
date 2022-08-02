package com.sdn.cacique.spop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.evrencoskun.tableview.TableView;
import com.sdn.bd.host.BDLocal;
import com.sdn.bd.host.BDUtil;
import com.sdn.tableview.TableViewAdapter;
import com.sdn.tableview.TableViewModel;

import java.util.ArrayList;

public class FrmBaseDeDatos extends AppCompatActivity {
    Spinner cbxtabla;
    ArrayList<String> tablas;

    private TableViewAdapter TblAdapterConsolidado;
    private TableViewModel TblModelConsolidado;
    private TableView TblVisorConsolidado;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frm_base_de_datos);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tablas = BDUtil.getTableNames(FrmBaseDeDatos.this);

        TblVisorConsolidado = findViewById(R.id.tblmostrarRegistros);
        ((TextView) findViewById(R.id.p03_lblubicacionbd)).setText(BDUtil.getBDLocation(FrmBaseDeDatos.this));

        cbxtabla = (Spinner) findViewById(R.id.p03_Cbxtabla);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tablas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cbxtabla.setAdapter(adapter);

        cbxtabla.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
                // TODO Auto-generated method stub
                initTableConsolidado(tablas.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mnu_basedatos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m03_mnu_borrar:
                if(cbxtabla.getSelectedItem().toString().equals("info") || cbxtabla.getSelectedItem().toString().equals("Parametro") || cbxtabla.getSelectedItem().toString().equals("android_metadata")|| cbxtabla.getSelectedItem().toString().equals("sqlite_sequence")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(Html.fromHtml("<small>La tabla </small><font color='#008F39'>"+cbxtabla.getSelectedItem().toString()+"</font><small>, no puede ser reseteada</small>"))
                            .setIcon(R.drawable.img_logo)
                            .setCancelable(false)
                            .setTitle(Html.fromHtml("<font color='#FF7F27'><small>BORRAR DATOS DE TABLA</small></font>"))
                            .setPositiveButton("ENTIENDO",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(Html.fromHtml("<small>Desea eliminar todos los datos de la tabla</small><BR><BR><font color='#008F39'>"+cbxtabla.getSelectedItem().toString()+"</font>"))
                            .setIcon(R.drawable.img_logo)
                            .setCancelable(false)
                            .setTitle(Html.fromHtml("<font color='#FF7F27'><small>BORRAR DATOS</small></font>"))
                            .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setPositiveButton("SI",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            BDUtil.EmptyTable(FrmBaseDeDatos.this,cbxtabla.getSelectedItem().toString(),null,null);
                                            initTableConsolidado(cbxtabla.getSelectedItem().toString());
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initTableConsolidado(String tabla) {
        TblModelConsolidado= new TableViewModel(getApplicationContext(),"SELECT * FROM "+tabla);
        TblAdapterConsolidado = new TableViewAdapter(getApplicationContext(), TblModelConsolidado);

        if (TblModelConsolidado.getRowHeaderList().size() > 0) {
            TblVisorConsolidado.setVisibility(View.VISIBLE);
            TblAdapterConsolidado.ON_CORNER_LISTENER = true;
            TblVisorConsolidado.setAdapter(TblAdapterConsolidado);
            TblAdapterConsolidado.setAllItems(TblModelConsolidado.getColumnHeaderList(), TblModelConsolidado.getRowHeaderList(), TblModelConsolidado.getCellList());
        }else{
            TblVisorConsolidado.setVisibility(View.INVISIBLE);
        }


    }
}