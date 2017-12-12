package com.apps.ing3ns.smartroomapp.Fragments;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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
import android.widget.ImageButton;
import android.widget.ImageView;

import com.apps.ing3ns.smartroomapp.Models.Closet;
import com.apps.ing3ns.smartroomapp.Models.ControlPrimario;
import com.apps.ing3ns.smartroomapp.Models.SmartRoomDriver;
import com.apps.ing3ns.smartroomapp.Models.Sonido;
import com.apps.ing3ns.smartroomapp.R;
import com.apps.ing3ns.smartroomapp.Services.Constants;
import com.apps.ing3ns.smartroomapp.Services.SocketServiceProvider;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.madrapps.pikolo.HSLColorPicker;
import com.madrapps.pikolo.listeners.SimpleColorSelectionListener;

/**
 * Created by JuanDa on 21/11/2017.
 */

public class FragmentCloset extends Fragment {

    //------------------ Comunicacion con el Servicio de Web Socket------------------
    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("getTokenDriver")) {
                driver.setToken(intent.getExtras().getString("data"));
            }

            if (intent.getAction().equals("syncClient")) {
                driver = gson.fromJson(intent.getExtras().getString("data"),SmartRoomDriver.class);
                int co = ~driver.getCloset().getEstanteUno();
                //FFFFFFFFFFF90028 = -458712
                //000000000006FFD8 = 458712
                //0000000000FAB600 = 16430592

                btnEstanteUno.setBackgroundColor(driver.getCloset().getEstanteUno());
                btnEstanteDos.setBackgroundColor(driver.getCloset().getEstanteDos());
                btnEstanteTres.setBackgroundColor(driver.getCloset().getEstanteTres());
                btnEstanteCuatro.setBackgroundColor(driver.getCloset().getEstanteCuatro());
                btnPerchero.setBackgroundColor(driver.getCloset().getPerchero());
                for(int i=0;i<5;i++) ColorLuz[i] = getColorEstante(i);
            }
        }
    }

    private DataUpdateReceiver dataUpdateReceiver;
    SocketServiceProvider socketServiceProvider;

    //Elementos de UI
    ImageButton btnEstanteUno;
    ImageButton btnEstanteDos;
    ImageButton btnEstanteTres;
    ImageButton btnEstanteCuatro;
    ImageButton btnPerchero;


    SmartRoomDriver driver;
    Gson gson;
    boolean servicesConnection = false;
    int [] ColorLuz = new int[5];
    boolean sendColor = false;


    public static FragmentCloset newInstance(boolean logear, boolean internet) {
        FragmentCloset fragment = new FragmentCloset();
        return fragment;
    }

    public FragmentCloset() {
        // Required empty public constructor
    }

    public void bindUI(View view){
        // ----------Enlazamos objetos del View-------------
         btnEstanteUno = view.findViewById(R.id.btn_estante1);
         btnEstanteDos = view.findViewById(R.id.btn_estante2);
         btnEstanteTres = view.findViewById(R.id.btn_estante3);
         btnEstanteCuatro = view.findViewById(R.id.btn_estante4);
         btnPerchero = view.findViewById(R.id.btn_perchero);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_closet, container, false);
        bindUI(view);

        // ---------Inicializamos el SmartRoomDriver----------
        driver = new SmartRoomDriver(0,"ESP8266","ing3ns2560","",new ControlPrimario(),new Closet(),new Sonido());
        gson = new GsonBuilder().create();

        btnEstanteUno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlertColor(0, btnEstanteUno);
            }
        });

        btnEstanteDos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlertColor(1, btnEstanteDos);
            }
        });

        btnEstanteTres.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlertColor(2, btnEstanteTres);
            }
        });

        btnEstanteCuatro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlertColor(3, btnEstanteCuatro);
            }
        });

        btnPerchero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlertColor(4, btnPerchero);
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
    public void dialogAlertColor(final int pos, final ImageButton imageButton){
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
        colorPicker.setColor(ColorLuz[pos]);
        colorActual.getBackground().setColorFilter(ColorLuz[pos], PorterDuff.Mode.MULTIPLY);

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
                                setColorEstante(pos,color,false);
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
                ColorLuz[pos] = getColorEstante(pos);
                imageButton.setBackgroundColor(ColorLuz[pos]);
                alertDialog.dismiss();
            }
        });

        colorActual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorLuz[pos] = getColorEstante(pos);
                imageButton.setBackgroundColor(ColorLuz[pos]);
                alertDialog.dismiss();
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                setColorEstante(pos,ColorLuz[pos],true);

            }
        });

    }

    int getColorEstante(int pos){
        int color=0;
        switch (pos){
            case 0:
                color = driver.getCloset().getEstanteUno();
                break;

            case 1:
                color = driver.getCloset().getEstanteDos();
                break;

            case 2:
                color = driver.getCloset().getEstanteTres();
                break;

            case 3:
                color = driver.getCloset().getEstanteCuatro();
                break;

            case 4:
                color = driver.getCloset().getPerchero();
                break;
            default:
                break;
        }
        return color;
    }

    void setColorEstante(int pos, int color, boolean ok){
        switch (pos){
            case 0:
                driver.getCloset().setEstanteUno(color);
                if(ok) {
                    if(servicesConnection)socketServiceProvider.emitEstanteUnoOK(gson.toJson(driver));
                }else {
                    if (servicesConnection) socketServiceProvider.emitEstanteUnoChange(gson.toJson(driver));
                }
                break;

            case 1:
                driver.getCloset().setEstanteDos(color);
                if(ok) {
                    if(servicesConnection)socketServiceProvider.emitEstanteDosOK(gson.toJson(driver));
                }else {
                    if (servicesConnection) socketServiceProvider.emitEstanteDosChange(gson.toJson(driver));
                }
                break;

            case 2:
                driver.getCloset().setEstanteTres(color);
                if(ok) {
                    if(servicesConnection)socketServiceProvider.emitEstanteTresOK(gson.toJson(driver));
                }else {
                    if (servicesConnection) socketServiceProvider.emitEstanteTresChange(gson.toJson(driver));
                }
                break;

            case 3:
                driver.getCloset().setEstanteCuatro(color);
                if(ok) {
                    if(servicesConnection)socketServiceProvider.emitEstanteCuatroOK(gson.toJson(driver));
                }else {
                    if (servicesConnection) socketServiceProvider.emitEstanteCuatroChange(gson.toJson(driver));
                }
                break;

            case 4:
                driver.getCloset().setPerchero(color);
                if(ok) {
                    if(servicesConnection)socketServiceProvider.emitPercheroOK(gson.toJson(driver));
                }else {
                    if (servicesConnection) socketServiceProvider.emitPercheroChange(gson.toJson(driver));
                }
                break;
        }
    }
}

