package com.sdn.cacique.spop;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FrmEcaneoxPedidoAdapter2 extends FragmentStateAdapter {
    private static final String ARG_OBJECT = "object";
    PnlLectura pnllectura = new PnlLectura();
    PnlConsolidado pnlresumen = new PnlConsolidado();
    PnlDetalle pnldetalle = new PnlDetalle();

    public FrmEcaneoxPedidoAdapter2(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }
    /*public FrmEcaneoxPedidoAdapter2(FrmEscaneoxPedido frmEscaneoxPedido) {
        super(frmEscaneoxPedido);
    }*/

    @NonNull
    @Override
    public Fragment createFragment(int position) {
      switch (position){
            case 0: return  pnllectura;
            case 1: return  pnlresumen;
            case 2: return  pnldetalle;
        }
        return  null;
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    /*public Fragment getPanelLectura(){
        // Return a NEW fragment instance in createFragment(int)
        Fragment fragment = new PnlLectura();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putInt(ARG_OBJECT, position + 1);
        fragment.setArguments(args);

        return fragment;
    }*/

}
