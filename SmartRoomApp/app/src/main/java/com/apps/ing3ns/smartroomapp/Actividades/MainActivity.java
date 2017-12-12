package com.apps.ing3ns.smartroomapp.Actividades;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.apps.ing3ns.smartroomapp.Fragments.FragmentCloset;
import com.apps.ing3ns.smartroomapp.Fragments.FragmentControlBasico;
import com.apps.ing3ns.smartroomapp.Fragments.FragmentSonido;
import com.apps.ing3ns.smartroomapp.R;
import com.apps.ing3ns.smartroomapp.Services.Constants;
import com.apps.ing3ns.smartroomapp.Services.SocketServiceProvider;

import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    //----------------Fragments----------------------------
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentControlBasico fragmentControlBasico = new FragmentControlBasico();
    FragmentSonido fragmentSonido = new FragmentSonido();
    FragmentCloset fragmentCloset = new FragmentCloset();

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Menu menuToolbar;

    private final int REQ_CODE_SPEECH_INPUT = 100;

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(), "no soportado", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Toast.makeText(this,"" + result.get(0),Toast.LENGTH_SHORT).show();
                }
                break;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--------------------------- TOOLBAR AND MENU ------------------------
        setToolbar();
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navview);
        configColorSubTitleMenu();
        if (navigationView != null) setupDrawerContent(navigationView);
        navigationView.getMenu().findItem(R.id.menu_control_primario).setChecked(true);
        setControlBasicoView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    //----------------------------PRESIONAR BOTON ATRAS --------------------------------
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawers();
        else if(!fragmentControlBasico.isVisible()){
            MenuItem menuItem = navigationView.getMenu().findItem(R.id.menu_control_primario);
            selectItem(menuItem);
        }else super.onBackPressed();
    }

    //------------------------- FUNCIONES MENU Y TOOLBAR -----------------------------
    //----------------------MENU TOOLBAR-----------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuToolbar = menu;
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //---------------------------ABRIR MENU LATERAL-------------------------------
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarWithOutLogo();
        //toolbarWithLogo();
    }

    private void toolbarWithOutLogo(){
        getSupportActionBar().setLogo(null);
        getSupportActionBar().setTitle(R.string.app_name);
    }

    private void toolbarWithLogo(){
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle("");
    }

    private void configColorSubTitleMenu() {
        Menu menu = navigationView.getMenu();
        MenuItem tools= menu.findItem(R.id.otras_opciones);
        SpannableString s = new SpannableString(tools.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.TextAppearanceSubtitleMenu), 0, s.length(), 0);
        tools.setTitle(s);
    }

    private void setupDrawerContent(NavigationView navigationView) {

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectItem(menuItem);
                        return true;
                    }
                }
        );
    }

    //--------------------MENU LATERAL---------------------
    private void selectItem(MenuItem menuItem) {
        menuItem.setChecked(true);
        String title = menuItem.getTitle().toString();
        toolbarWithOutLogo();
        switch (menuItem.getItemId()){
            case R.id.menu_control_primario:
                setControlBasicoView();
                break;

            case R.id.menu_closet:
                setClosetView();
                break;

            case R.id.menu_sonido:
                setSonidoView();
                break;

            case R.id.menu_pc:
                Intent startIntent = new Intent(MainActivity.this, SocketServiceProvider.class);
                startIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                startService(startIntent);
                break;

            case R.id.menu_compartir:
                Intent stopIntent = new Intent(MainActivity.this, SocketServiceProvider.class);
                stopIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                startService(stopIntent);
                break;

        }

        drawerLayout.closeDrawers(); // Cerrar drawer
    }

    public void setControlBasicoView(){
        if(!fragmentControlBasico.isVisible()) {
            fragmentControlBasico = FragmentControlBasico.newInstance(false,false);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, fragmentControlBasico)
                    .commit();
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    public void setSonidoView(){
        if(!fragmentSonido.isVisible()) {
            fragmentSonido = FragmentSonido.newInstance(false,false);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, fragmentSonido)
                    .commit();
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    public void setClosetView(){
        if(!fragmentCloset.isVisible()) {
            fragmentCloset = FragmentCloset.newInstance(false,false);
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.main_content, fragmentCloset)
                    .commit();
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }
}
