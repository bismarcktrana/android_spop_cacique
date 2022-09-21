package com.sdn.cacique.spop;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class AdapterContenedor extends FragmentStateAdapter {
    private static final String ARG_OBJECT = "object";
    PnlLectura pnllectura = new PnlLectura();
    PnlResumenContenedor pnlresumen = new PnlResumenContenedor();

    PnlDetalle pnldetalle = new PnlDetalle();

    public AdapterContenedor(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
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
