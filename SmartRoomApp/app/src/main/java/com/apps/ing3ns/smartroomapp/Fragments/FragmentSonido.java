package com.apps.ing3ns.smartroomapp.Fragments;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.apps.ing3ns.smartroomapp.Models.Closet;
import com.apps.ing3ns.smartroomapp.Models.ControlPrimario;
import com.apps.ing3ns.smartroomapp.Models.SmartRoomDriver;
import com.apps.ing3ns.smartroomapp.Models.Sonido;
import com.apps.ing3ns.smartroomapp.R;
import com.apps.ing3ns.smartroomapp.Services.Constants;
import com.apps.ing3ns.smartroomapp.Services.SocketServiceProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by JuanDa on 21/11/2017.
 */

public class FragmentSonido extends Fragment implements View.OnClickListener {

    //------------------ Comunicacion con el Servicio de Web Socket------------------
    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("getTokenDriver")) {
                driver.setToken(intent.getExtras().getString("data"));
            }

            if (intent.getAction().equals("syncClient")) {
                driver = gson.fromJson(intent.getExtras().getString("data"),SmartRoomDriver.class);
                driver.setSonido(new Sonido(0));
            }
        }
    }

    private DataUpdateReceiver dataUpdateReceiver;
    SocketServiceProvider socketServiceProvider;

    //Elementos de UI
    ImageButton btnPower;
    ImageButton btnFunction;
    ImageButton btnPlay;
    ImageButton btnStop;
    ImageButton btnPause;

    ImageButton btnPrevFolder;
    ImageButton btnPreview;
    ImageButton btnNext;
    ImageButton btnNextFolder;

    ImageButton btnVolUp;
    ImageButton btnVolDown;
    ImageButton btnMute;

    ImageButton btnUp;
    ImageButton btnDown;
    ImageButton btnRight;
    ImageButton btnLeft;
    ImageButton btnEnter;

    SmartRoomDriver driver;
    Gson gson;
    boolean servicesConnection = false;


    public static FragmentSonido newInstance(boolean logear, boolean internet) {
        FragmentSonido fragment = new FragmentSonido();
        return fragment;
    }

    public FragmentSonido() {
        // Required empty public constructor
    }

    public void bindUI(View view){
        // ----------Enlazamos objetos del View-------------
        btnPower = view.findViewById(R.id.btn_power);
        btnFunction = view.findViewById(R.id.btn_function);
        btnPlay = view.findViewById(R.id.btn_play);
        btnStop = view.findViewById(R.id.btn_stop);
        btnPause = view.findViewById(R.id.btn_pause);

        btnPrevFolder = view.findViewById(R.id.btn_preview_folder);
        btnPreview = view.findViewById(R.id.btn_preview);
        btnNext = view.findViewById(R.id.btn_next);
        btnNextFolder= view.findViewById(R.id.btn_next_folder);

        btnVolUp = view.findViewById(R.id.btn_volUp);
        btnVolDown = view.findViewById(R.id.btn_volDown);
        btnMute = view.findViewById(R.id.btn_mute);

        btnUp = view.findViewById(R.id.btn_up);
        btnDown = view.findViewById(R.id.btn_down);
        btnRight = view.findViewById(R.id.btn_right);
        btnLeft = view.findViewById(R.id.btn_left);
        btnEnter = view.findViewById(R.id.btn_enter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sonido, container, false);
        bindUI(view);

        // ---------Inicializamos el SmartRoomDriver----------
        driver = new SmartRoomDriver(0,"ESP8266","ing3ns2560","",new ControlPrimario(),new Closet(),new Sonido());
        gson = new GsonBuilder().create();

        //----------- Eventos de objetos del View -----------------
        btnPower.setOnClickListener(this);
        btnFunction.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnPause.setOnClickListener(this);

        btnPrevFolder.setOnClickListener(this);
        btnPreview.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnNextFolder.setOnClickListener(this);

        btnVolUp.setOnClickListener(this);
        btnVolDown.setOnClickListener(this);
        btnMute.setOnClickListener(this);

        btnUp.setOnClickListener(this);
        btnDown.setOnClickListener(this);
        btnRight.setOnClickListener(this);
        btnLeft.setOnClickListener(this);
        btnEnter.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_power:
                driver.getSonido().setBoton(1);
                break;
            case R.id.btn_function:
                driver.getSonido().setBoton(2);
                break;
            case R.id.btn_play:
                driver.getSonido().setBoton(3);
                break;
            case R.id.btn_stop:
                driver.getSonido().setBoton(4);
                break;
            case R.id.btn_pause:
                driver.getSonido().setBoton(5);
                break;
            case R.id.btn_preview_folder:
                driver.getSonido().setBoton(6);
                break;
            case R.id.btn_preview:
                driver.getSonido().setBoton(7);
                break;
            case R.id.btn_next:
                driver.getSonido().setBoton(8);
                break;
            case R.id.btn_next_folder:
                driver.getSonido().setBoton(9);
                break;
            case R.id.btn_volUp:
                driver.getSonido().setBoton(10);
                break;
            case R.id.btn_volDown:
                driver.getSonido().setBoton(11);
                break;
            case R.id.btn_mute:
                driver.getSonido().setBoton(12);
                break;
            case R.id.btn_up:
                driver.getSonido().setBoton(13);
                break;
            case R.id.btn_down:
                driver.getSonido().setBoton(14);
                break;
            case R.id.btn_right:
                driver.getSonido().setBoton(15);
                break;
            case R.id.btn_left:
                driver.getSonido().setBoton(16);
                break;
            case R.id.btn_enter:
                driver.getSonido().setBoton(17);
                break;
        }

        if(servicesConnection)socketServiceProvider.emitSound(gson.toJson(driver));
    }
}

