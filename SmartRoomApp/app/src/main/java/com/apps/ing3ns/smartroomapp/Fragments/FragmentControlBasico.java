package com.apps.ing3ns.smartroomapp.Fragments;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.ing3ns.smartroomapp.Listeners.ControlBasicoListener;
import com.apps.ing3ns.smartroomapp.Models.Closet;
import com.apps.ing3ns.smartroomapp.Models.ControlPrimario;
import com.apps.ing3ns.smartroomapp.Models.SmartRoomDriver;
import com.apps.ing3ns.smartroomapp.Models.Sonido;
import com.apps.ing3ns.smartroomapp.R;
import com.apps.ing3ns.smartroomapp.Services.Constants;
import com.apps.ing3ns.smartroomapp.Services.SocketServiceProvider;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;

import java.net.URISyntaxException;

/**
 * Created by JuanDa on 21/11/2017.
 */

public class FragmentControlBasico extends Fragment {

    //------------------ Comunicacion con el Servicio de Web Socket------------------
    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("getTokenDriver")) {
                driver.setToken(intent.getExtras().getString("data"));
            }

            if (intent.getAction().equals("syncClient")) {
                driver = gson.fromJson(intent.getExtras().getString("data"),SmartRoomDriver.class);
                tvTemperatura.setText(driver.getControlPrimario().getTemperatura()+"°");
                pbTemperatura.setProgress(driver.getControlPrimario().getTemperatura());
                tvHumedad.setText(driver.getControlPrimario().getHumedad()+"%");
                puerta.setChecked(driver.getControlPrimario().isPuerta());
            }
        }
    }

    private DataUpdateReceiver dataUpdateReceiver;
    SocketServiceProvider socketServiceProvider;

    //----------------------Elementos de UI-------------------------
    TextView tvTemperatura;
    TextView tvHumedad;
    ProgressBar pbTemperatura;
    Button luzColor;
    ImageButton luzPrincipal;
    SeekBar seekBarPersianas;
    Switch puerta;

    //--------------------Variables de proceso----------------------
    boolean servicesConnection = false;
    SmartRoomDriver driver;
    Gson gson;
    int ColorLuz;
    int progressSeekBar;
    boolean sendSeekBar = false;
    boolean sendColor = false;

    public static FragmentControlBasico newInstance(boolean logear, boolean internet) {
        FragmentControlBasico fragment = new FragmentControlBasico();
        return fragment;
    }

    public FragmentControlBasico() {
        // Required empty public constructor
    }

    public void bindUI(View view){
        // ----------Enlazamos objetos del View-------------
        tvTemperatura = view.findViewById(R.id.txt_temp);
        tvHumedad = view.findViewById(R.id.txt_humedad);
        pbTemperatura = view.findViewById(R.id.progress_bar_temp);
        luzPrincipal = view.findViewById(R.id.btn_luz_principal);
        luzColor = view.findViewById(R.id.btnColorLuz);
        seekBarPersianas = view.findViewById(R.id.seekbarPersiana);
        puerta = view.findViewById(R.id.switchPuerta);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_control_basico, container, false);
        bindUI(view);

        // ---------Inicializamos el SmartRoomDriver----------
        driver = new SmartRoomDriver(0,"ESP8266","ing3ns2560","",new ControlPrimario(),new Closet(),new Sonido());
        gson = new GsonBuilder().create();
        ColorLuz = 0;

        //----------- Eventos de objetos del View -----------------
        luzPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                driver.getControlPrimario().setLuzPrincipal(true);
                if(servicesConnection)socketServiceProvider.emitLpChange(gson.toJson(driver));
            }
        });

        luzColor.setBackgroundColor(Color.WHITE);
        luzColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAlertColor();
            }
        });

        seekBarPersianas.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressSeekBar = progress;
                if(!sendSeekBar) {
                    sendSeekBar =true;
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            driver.getControlPrimario().setPersiana(progressSeekBar);
                            if(servicesConnection)socketServiceProvider.emitPersinasChange(gson.toJson(driver));
                            sendSeekBar = false;
                        }
                    }, 500);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                driver.getControlPrimario().setPersiana(progressSeekBar);
                if(servicesConnection)socketServiceProvider.emitPersinasChange(gson.toJson(driver));
            }
        });

        puerta.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                driver.getControlPrimario().setPuerta(isChecked);
                socketServiceProvider.emitPuertaChange(gson.toJson(driver));
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

        Intent startIntent = new Intent(getActivity(), SocketServiceProvider.class);
        startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        getActivity().bindService(startIntent, mConnection,Context.BIND_AUTO_CREATE); //Binding to the service!

        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter ifSyncClient = new IntentFilter("syncClient");
        IntentFilter ifGetTokenDriver = new IntentFilter("getTokenDriver");
        getActivity().registerReceiver(dataUpdateReceiver, ifGetTokenDriver);
        getActivity().registerReceiver(dataUpdateReceiver, ifSyncClient);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (dataUpdateReceiver != null) getActivity().unregisterReceiver(dataUpdateReceiver);
        getActivity().unbindService(mConnection);
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            servicesConnection = true;
            SocketServiceProvider.LocalBinder binder = (SocketServiceProvider.LocalBinder) service;
            socketServiceProvider = binder.getServiceInstance();

            //-----------------Recuperamos el token del controlador -------------------
            socketServiceProvider.emitRequestToken(gson.toJson(driver));
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            servicesConnection = false;
        }
    };

    //--------------------Funciones Auxiliares ---------------------
    //--------------------------------------------------------------

    //--------------------------DIALOG ALERT Seleccionar Color ---------------------
    public void dialogAlertColor(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.CustomDialog);
        Context context = getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        View vista = inflater.inflate(R.layout.dialog_color_picker, null);

        builder.setView(vista);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Button btnAceptar = vista.findViewById(R.id.boton_aceptar_no_gps_pedido);
        final ImageView colorActual = vista.findViewById(R.id.colorActual);
        final HSLColorPicker colorPicker = vista.findViewById(R.id.colorPicker);
        colorPicker.setColor(ColorLuz);
        colorActual.getBackground().setColorFilter(ColorLuz, PorterDuff.Mode.MULTIPLY);

        colorPicker.setColorSelectionListener(new SimpleColorSelectionListener() {
            @Override
            public void onColorSelected(final int color) {
                if (color != driver.getControlPrimario().getLuzSecundaria()) {
                    colorActual.getBackground().setColorFilter(color, PorterDuff.Mode.MULTIPLY);

                    if (!sendColor) {
                        sendColor = true;
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                driver.getControlPrimario().setLuzSecundaria(color);
                                if(servicesConnection)socketServiceProvider.emitLsChange(gson.toJson(driver));
                                sendColor = false;
                            }
                        }, 200);
                    }
                }
            }
        });

        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorLuz = driver.getControlPrimario().getLuzSecundaria();
                luzColor.setBackgroundColor(ColorLuz);
                alertDialog.dismiss();
            }
        });

        colorActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorLuz = driver.getControlPrimario().getLuzSecundaria();
                luzColor.setBackgroundColor(ColorLuz);
                alertDialog.dismiss();
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                driver.getControlPrimario().setLuzSecundaria(ColorLuz);
                if(servicesConnection)socketServiceProvider.emitLsOK(gson.toJson(driver));
            }
        });

    }
}

