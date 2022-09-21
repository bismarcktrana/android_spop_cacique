package com.sdn.sound;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class SoundManager {
    private Context context;
    private SoundPool sndPool;
    private float rate = 1.0f;
    private float leftVolume = 1.0f;
    private float rightVolume = 1.0f;
    public Integer  msg_caja_disponible, msg_caja_invalida, msg_caja_repetida, msg_lectura_ok, msg_lectura_error;
    private static final String LOG_TAG = "SoundManager";



    //La clase SoundPool administra y ejecuta todos los recursos de audio de la aplicacion.

    //Nuestro constructor, que determina la configuracion de audio del contexto de nuestra aplicacion
    public SoundManager(Context context)
    {
        this.sndPool = new SoundPool(16, AudioManager.STREAM_MUSIC, 100);
        this.context = context;
        confifureSound(1);//Definimos Sounido por defecto
    }

    public void confifureSound(int TipoUsuario)
    {
        //unloadAll();//descargamos sonidos anteriormente configurados, para definir los nuevos sonidos

        switch (TipoUsuario){
            case 1://ADMINISTADOR
                msg_caja_disponible =load(R.raw.woman_msg_ok);
                msg_caja_invalida =load(R.raw.woman_msg_error);
                break;
            case 2://SUPERVIDOR
                msg_caja_disponible =load(R.raw.man_msg_ok);
                msg_caja_invalida =load(R.raw.man_msg_error);
                break;
            default://OPERADOR Y OTROS USUARIO
                msg_caja_disponible =load(R.raw.man_msg_ok);
                msg_caja_invalida =load(R.raw.man_msg_error);
                break;
        }
        msg_caja_repetida = load(R.raw.doblebeep_msg_error);
        msg_lectura_ok = load(R.raw.beep_msg_ok);
        msg_lectura_error = load(R.raw.beep_msg_error);
    }

    //Obtiene el sonido y retorna el id del mismo
    private int load(int idSonido)
    {
        Log.i(LOG_TAG, "load");
        return sndPool.load(context, idSonido, 1);

    }
    //Ejecuta el sonido, toma como parametro el id del sonido a ejecutar.
    public void play(int idSonido)
    {
        sndPool.play(idSonido, leftVolume, rightVolume, 1, 0, rate);
    }

    public void play_caja_disponible()
    {
        this.play(msg_caja_disponible);
        Log.i(LOG_TAG, "play_caja_disponible");
    }

    public void play_caja_invalida()
    {
        this.play(msg_caja_invalida);
        Log.i(LOG_TAG, "play_caja_invalida");
    }

    public void play_caja_repetida()
    {
        this.play(msg_caja_repetida);
        Log.i(LOG_TAG, "msg_caja_repetida");
    }

    public void play_lectura_ok()
    {
        this.play(msg_lectura_ok);
        Log.i(LOG_TAG, "msg_lectura_ok");
    }

    public void play_lectura_error()
    {
        this.play(msg_lectura_error);
        Log.i(LOG_TAG, "msg_lectura_error");
    }

    // Libera memoria de todos los objetos del sndPool que ya no son requeridos.
    public void unloadAll()
    {
        sndPool.release();
        Log.i(LOG_TAG, "unloadAll Liberar");
    }

}

