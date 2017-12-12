package com.apps.ing3ns.smartroomapp.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import com.apps.ing3ns.smartroomapp.Actividades.MainActivity;
import com.apps.ing3ns.smartroomapp.R;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.net.URISyntaxException;

/**
 * Created by JuanDa on 27/08/2017.
 */

public class SocketServiceProvider extends Service {

    private final IBinder mBinder = new LocalBinder();

    private static final String LOG_TAG = "SocketServiceProvider";
    Notification notification;

    //Variables para comunicacion con el Web Socket Server
    //--------------------Eventos ----------------------
    String urlWebSocket = "http://ing3ns.com:8080";
    String getToken = "getTokenDriver";
    String syncClient = "syncClient";
    String motionDetected = "motionDetected";

    //------------------- Emits --------------------
    String requestToken = "requestTokenDriver";
    String lpEmit = "lp-change";
    String lsEmit = "ls-change";
    String lsOKEmit = "ls-ok";
    String persianasEmit = "persianas-change";
    String puertaEmit = "puerta-change";

    String soundEmit = "sound-change";

    String estanteUnoEmit = "estante1-change";
    String estanteUnoOKEmit = "estante1-ok";
    String estanteDosEmit = "estante2-change";
    String estanteDosOKEmit = "estante2-ok";
    String estanteTresEmit = "estante3-change";
    String estanteTresOKEmit = "estante3-ok";
    String estanteCuatroEmit = "estante4-change";
    String estanteCuatroOKEmit = "estante4-ok";
    String percheroEmit = "perchero-change";
    String percheroOKEmit = "perchero-ok";



    private Socket webSocket;
    {
        try {
            webSocket = IO.socket(urlWebSocket);
        } catch (URISyntaxException e) {}
    }

    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        prefs = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            Gson gson = new GsonBuilder().create();
            Log.i(LOG_TAG, "Received Start Foreground Intent ");
            //Log.i(LOG_TAG, gson.fromJson(intent.getExtras().getString("usuario"), Usuario.class).getToken());


            // ------Iniciamos metodos para el WEB SOCKET----------
            webSocket.on(getToken, getTokenDriverListener);
            webSocket.on(syncClient, synClientListener);
            webSocket.on("disconnect", disconnect);
            webSocket.on(motionDetected, motionDetectedListener);

            webSocket.connect();

            //webSocket.emit(requestToken, gson.toJson(driver));
            //------------------ DEFINIMOS INTENT ---------------------------------
            //---------------------------------------------------------------------
            Intent intent2 = new Intent(this, MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent2.putExtra("tipo","nopromo");
            intent2.putExtra("estado","1");
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0, intent2, PendingIntent.FLAG_ONE_SHOT);

            notification = new NotificationCompat.Builder(this)
                    .setContentTitle("SmartRoom")
                    .setContentText("Comunicacion Activa")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true).build();

            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);

        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.i(LOG_TAG, "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "In onDestroy");

        webSocket.disconnect();
        webSocket.off("disconnect", disconnect);
        webSocket.off(getToken, getTokenDriverListener);
        webSocket.off(syncClient,synClientListener);
        webSocket.off(motionDetected,motionDetectedListener);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Used only in case of bound services.
        return mBinder;
    }

    //returns the instance of the service
    public class LocalBinder extends Binder {
        public SocketServiceProvider getServiceInstance(){
            return SocketServiceProvider.this;
        }
    }


    //------------------------------ Functions for Emit Web Socket -----------------------
    public void emitRequestToken(String driverJson){
        webSocket.emit(requestToken, driverJson);
    }

    public void emitLpChange(String driverJson){
        webSocket.emit(lpEmit, driverJson);
    }

    public void emitPersinasChange(String driverJson){
        webSocket.emit(persianasEmit, driverJson);
    }

    public void emitPuertaChange(String driverJson){
        webSocket.emit(puertaEmit, driverJson);
    }

    public void emitLsChange(String driverJson){
        webSocket.emit(lsEmit, driverJson);
    }

    public void emitLsOK(String driverJson){
        webSocket.emit(lsOKEmit, driverJson);
    }

    public void emitSound(String driverJson){
        webSocket.emit(soundEmit,driverJson);
    }

    public void emitEstanteUnoOK(String driverJson){
        webSocket.emit(estanteUnoOKEmit,driverJson);
    }

    public void emitEstanteUnoChange(String driverJson){
        webSocket.emit(estanteUnoEmit,driverJson);
    }

    public void emitEstanteDosOK(String driverJson){
        webSocket.emit(estanteDosOKEmit,driverJson);
    }

    public void emitEstanteDosChange(String driverJson){
        webSocket.emit(estanteDosEmit,driverJson);
    }

    public void emitEstanteTresOK(String driverJson){
        webSocket.emit(estanteTresOKEmit,driverJson);
    }

    public void emitEstanteTresChange(String driverJson){
        webSocket.emit(estanteTresEmit,driverJson);
    }

    public void emitEstanteCuatroOK(String driverJson){
        webSocket.emit(estanteCuatroOKEmit,driverJson);
    }

    public void emitEstanteCuatroChange(String driverJson){
        webSocket.emit(estanteCuatroEmit,driverJson);
    }

    public void emitPercheroOK(String driverJson){
        webSocket.emit(percheroOKEmit,driverJson);
    }

    public void emitPercheroChange(String driverJson){
        webSocket.emit(percheroEmit,driverJson);
    }
    //---------------------------- Listeners of Events WebSocket --------------------------------
    private Emitter.Listener disconnect = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            notification = new NotificationCompat.Builder(getApplicationContext()).setContentTitle("Desconectado").setOngoing(true).build();
            startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
        }
    };

    //--------------------- EVENT LISTENER WEB SOCKET CONTROL PRINCIPAL --------------------
    //----------------------- Recepciono mensajes ----------------------------
    private Emitter.Listener getTokenDriverListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            sendBroadcast(new Intent("getTokenDriver").putExtra("data",(String) args[0]));
        }
    };

    private Emitter.Listener synClientListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            sendBroadcast(new Intent("syncClient").putExtra("data",(String) args[0]));
        }
    };

    private Emitter.Listener motionDetectedListener = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Intent intent2 = new Intent(getApplicationContext(), MainActivity.class);
            intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent2, PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent)
                    .setLargeIcon(BitmapFactory.decodeResource( getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(ResourcesCompat.getColor(getResources(), R.color.accentColor, null))
                    .setContentTitle("Smart Room")
                    .setStyle(new NotificationCompat.BigTextStyle().bigText("Intruso Detectado"))
                    .setContentText("Intruso Detectado")
                    .setVibrate(new long[] {100, 250, 100, 500})
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notificationBuilder.build());
        }
    };

}