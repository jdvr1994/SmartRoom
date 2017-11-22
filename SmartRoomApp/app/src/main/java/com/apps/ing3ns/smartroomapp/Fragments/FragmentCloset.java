package com.apps.ing3ns.smartroomapp.Fragments;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.apps.ing3ns.smartroomapp.Listeners.ControlBasicoListener;
import com.apps.ing3ns.smartroomapp.Models.SmartRoomDriver;
import com.apps.ing3ns.smartroomapp.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;

/**
 * Created by JuanDa on 21/11/2017.
 */

public class FragmentCloset extends Fragment{
    public static final String ARG_LOGEAR = "logear";
    public static final String ARG_INTERNET = "internet";

    //Elementos de UI
    TextView tvTemperatura;
    TextView tvHumedad;
    ProgressBar pbTemperatura;
    Button luzColor;
    Switch luzPrincipal;
    SeekBar seekBarPersianas;
    Switch puerta;

    SmartRoomDriver driver;
    Context contextActivity;
    Gson gson;
    int ColorLuz;

    //Variables para comunicacion con el Web Socket Server
    String urlWebSocket = "http://ing3ns.com:8080";
    String getToken = "getTokenDriver";
    String requestToken = "requestTokenDriver";
    String lpEmit = "lp-change";

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(urlWebSocket);
        } catch (URISyntaxException e) {}
    }



    //Para Shared Preference
    private SharedPreferences preferences;
    private ControlBasicoListener listener;

    public static FragmentCloset newInstance(boolean logear, boolean internet) {
        FragmentCloset fragment = new FragmentCloset();
        Bundle args = new Bundle();
        args.putBoolean(ARG_LOGEAR, logear);
        args.putBoolean(ARG_INTERNET, internet);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentCloset() {
        // Required empty public constructor
    }

    public void bindUI(View view){
        // ----------Enlazamos objetos del View-------------
        tvTemperatura = (TextView) view.findViewById(R.id.txt_temp);
        tvHumedad = (TextView) view.findViewById(R.id.txt_humedad);
        pbTemperatura = (ProgressBar) view.findViewById(R.id.progress_bar_temp);
        luzPrincipal = (Switch) view.findViewById(R.id.switchLP);
        luzColor = (Button) view.findViewById(R.id.btnColorLuz);
        seekBarPersianas = (SeekBar) view.findViewById(R.id.seekbarPersiana);
        puerta = (Switch) view.findViewById(R.id.switchPuerta);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_basico, container, false);
        bindUI(view);

        // ---------Inicializamos el SmartRoomDriver----------
        driver = new SmartRoomDriver(0,"ESP8266","ing3ns2560",null,0);
        gson = new GsonBuilder().create();
        ColorLuz = 0;

        // ------Iniciamos metodos para el WEB SOCKET----------
        mSocket.on(getToken, getTokenDriverListener);
        mSocket.connect();

        //-----------------Recuperamos el token del controlador -------------------
        mSocket.emit(requestToken, gson.toJson(driver));

        //----------- Eventos de objetos del View -----------------
        luzPrincipal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSocket.emit(lpEmit, gson.toJson(driver));
            }
        });

        luzColor.setBackgroundColor(ColorLuz);
        luzColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAlertColor();
            }
        });

        seekBarPersianas.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvTemperatura.setText(progress+"°");
                pbTemperatura.setProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("PERSIANAS","Posicion actual "+seekBar.getProgress());
            }
        });

        puerta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSocket.emit(lpEmit, gson.toJson(driver));
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        if(context instanceof ControlBasicoListener){
            listener = (ControlBasicoListener) context;
            contextActivity = context;
        }else{
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        listener = null;
        mSocket.disconnect();
        mSocket.off(getToken, getTokenDriverListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    //--------------------Funciones Auxiliares ---------------------
    //--------------------------------------------------------------

    //--------------------------DIALOG ALERT NINGUN PEDIDO DESPACHADO ---------------------
    public void dialogAlertColor(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomDialog);
        Context context = getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View vista = inflater.inflate(R.layout.dialog_color_picker, null);

        builder.setView(vista);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button btnAceptar = (Button) vista.findViewById(R.id.boton_aceptar_no_gps_pedido);
        final ImageView colorActual = (ImageView) vista.findViewById(R.id.colorActual);
        final HSLColorPicker colorPicker = (HSLColorPicker) vista.findViewById(R.id.colorPicker);
        colorPicker.setColor(ColorLuz);
        colorActual.getBackground().setColorFilter(ColorLuz, PorterDuff.Mode.MULTIPLY);

        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(int color) {
                ColorLuz = color;
                colorActual.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luzColor.setBackgroundColor(ColorLuz);
                alertDialog.dismiss();
            }
        });

        colorActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                luzColor.setBackgroundColor(ColorLuz);
                alertDialog.dismiss();
            }
        });

    }

    //----------------------- Recepciono mensajes ----------------------------
    private Emitter.Listener getTokenDriverListener = new Emitter.Listener() {

        @Override
        public void call(final Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    driver.setToken((String) args[0]);
                }
            });
        }
    };

    public void imagePicasso(final String imageURL, final ImageView imageView){

        Picasso.with(getContext()).load(imageURL).networkPolicy(NetworkPolicy.OFFLINE).fit().placeholder(R.mipmap.ic_launcher).into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onError() {
                Picasso.with(getContext())
                        .load(imageURL)
                        .fit()
                        .placeholder(R.mipmap.ic_launcher)
                        .into(imageView, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {

                            }

                        });
            }
        });
    }

    public void setListenerContext(){
        Context actividad = contextActivity;
        if(actividad instanceof ControlBasicoListener){
            listener = (ControlBasicoListener) actividad;
        }else{
            throw new RuntimeException(actividad.toString() + " must implement OnFragmentInteractionListener");
        }
    }
}

