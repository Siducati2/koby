package com.innohawk.dan.search;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.innohawk.dan.Connector;
import com.innohawk.dan.ListActivityBase;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.home.WebPageActivity;

import java.util.HashMap;
import java.util.Map;

public class SearchHomeActivity extends ListActivityBase {

    private static final String TAG = "SearchHomeActivity";
    Typeface tt;
    private static final int ACTIVITY_SEARCH_KEYWORD = 0;
    private static final int ACTIVITY_SEARCH_LOCATION = 1;
    private static final int ACTIVITY_SEARCH_NEAR_ME = 2;
    private static final int ACTIVITY_WEB_PAGE = 7;

    //Permission
    private static final int MY_PERMISSION_VAL = 0; //Tiene implicito el READ (que es el que necesitamos
    View layoutHomePermission;
    RelativeLayout rl_dialog;
    protected TextView textTitle;
    protected Button btn_action_back;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.list);

        tt = Typeface.createFromAsset(getResources().getAssets(), "fonts/Mermaid1001.ttf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_search_home);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        reloadRemoteData();
    }

    protected void initMenuPredefined() {
        Map<String, String> mapKeyword = new HashMap<String, String>();
        mapKeyword.put("title", getString(R.string.search_keyword));
        mapKeyword.put("action", "30");
        mapKeyword.put("bubble", "");

        Map<String, String> mapLocation = new HashMap<String, String>();
        mapLocation.put("title", getString(R.string.search_location));
        mapLocation.put("action", "31");
        mapLocation.put("bubble", "");

        Map<String, String> mapNearMe = new HashMap<String, String>();
        mapNearMe.put("title", getString(R.string.search_near_me));
        mapNearMe.put("action", "32");
        mapNearMe.put("bubble", "");

        Object[] aMenuDefault = {mapKeyword, mapLocation, mapNearMe};
        SearchHomeAdapter adapter = new SearchHomeAdapter(this, aMenuDefault,tt);
        setListAdapter(adapter);
    }

    protected void initMenu(Object[] aMenu) {
        SearchHomeAdapter adapter = new SearchHomeAdapter(this, aMenu, tt);
        setListAdapter(adapter);
    }


    protected void reloadRemoteData() {

        if (Main.getConnector().getProtocolVer() < 3) {
            initMenuPredefined();
            return;
        }

        Connector o = Main.getConnector();
        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                Main.getLang()
        };

        o.execAsyncMethod("dolphin.getSeachHomeMenu3", aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.getSeachHomeMenu3 result: " + result.toString());
                Map<String, Object> map = (Map<String, Object>) result;
                Log.d(TAG, "dolphin.getSeachHomeMenu3 menu: " + map.get("menu"));
                initMenu((Object[]) map.get("menu"));
            }
        }, this);

    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Object[] aMenu = ((SearchHomeAdapter) getListAdapter()).getMenu();
        if (position < 0 || position > aMenu.length)
            return;

        @SuppressWarnings("unchecked")
        Map<String, String> map = (Map<String, String>) aMenu[position];
        String sAction = map.get("action");
        int iAction = Integer.parseInt(sAction);

        switch (iAction) {
            case 30: {
                Intent i = new Intent(this, SearchKeywordActivity.class);
                startActivityForResult(i, ACTIVITY_SEARCH_KEYWORD);
                this.overridePendingTransition(0, 0);
            }
            break;
            case 31: {
                Intent i = new Intent(this, SearchLocationActivity.class);
                startActivityForResult(i, ACTIVITY_SEARCH_LOCATION);
                this.overridePendingTransition(0, 0);
            }
            break;
            case 32: {
                /*Intent i = new Intent(this, SearchNearMeActivity.class);
                startActivityForResult(i, ACTIVITY_SEARCH_NEAR_ME);
                this.overridePendingTransition(0, 0);*/
                verifyPermissionRequest();
            }
            break;
            case 100: {
                String sUrl = map.get("action_data");
                String sTitle = map.get("title");
                Intent i = new Intent(this, WebPageActivity.class);
                i.putExtra("title", sTitle);
                i.putExtra("url", sUrl);
                startActivityForResult(i, ACTIVITY_WEB_PAGE);
                this.overridePendingTransition(0, 0);
            }
            break;
            case 101: {
                String sUrl = map.get("action_data");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sUrl));
                startActivity(browserIntent);
                this.overridePendingTransition(0, 0);
            }
            break;
        }

    }

    /* Start code innoHawk permission Request */

    //Paso 1. Verificar permiso
    @SuppressLint("WrongConstant")
    private void verifyPermissionRequest() {
        //WRITE_EXTERNAL_STORAGE tiene implícito READ_EXTERNAL_STORAGE porque pertenecen al mismo grupo de permisos
        int locationPermission = 0;
        int locationPermission2 = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            locationPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            locationPermission2 = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if ((locationPermission != PackageManager.PERMISSION_GRANTED)||(locationPermission2 != PackageManager.PERMISSION_GRANTED)) {
            requestPermission();
        } else {
            doAction();
        }
    }

    //Paso 2: Solicitar permiso
    private void requestPermission() {
        //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
        //anteriormente el dialogo de permisos y el usuario lo negó
        if( (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION))||(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {
            showSnackBar();
        } else {
            //si es la primera vez se solicita el permiso directamente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSION_VAL);
            }
        }
    }

    //Paso 3: Procesar respuesta de usuario
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //Si el requestCode corresponde al que usamos para solicitar el permiso y
        //la respuesta del usuario fue positiva
        if (requestCode == MY_PERMISSION_VAL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doAction();
            } else {
                showSnackBar();
            }
        }
    }


    /**
     * Método para mostrar el snackbar de la aplicación.
     * Snackbar es un componente de la librería de diseño 'com.android.support:design:23.1.0'
     * y puede ser personalizado para realizar una acción, como por ejemplo abrir la actividad de
     * configuración de nuestra aplicación.
     */
    private void showSnackBar() {
        RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        if (rl_back == null) {
            rl_dialog = (RelativeLayout) findViewById(R.id.rl_infodialog);
            layoutHomePermission = getLayoutInflater().inflate(R.layout.activity_permission, rl_dialog, false);
            rl_dialog.addView(layoutHomePermission);
            TextView txt_name_permission=(TextView)findViewById(R.id.txt_dia);
            txt_name_permission.setText(R.string.permission_request_location);
            Button btn_yes = (Button) layoutHomePermission.findViewById(R.id.btn_yes);
            Button btn_settings = (Button) layoutHomePermission.findViewById(R.id.btn_settings);
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    rl_dialog.setVisibility(View.GONE);
                }
            });
            btn_settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    openSettings();
                    Button btn_yes = (Button) layoutHomePermission.findViewById(R.id.btn_yes);
                    Button btn_settings = (Button) layoutHomePermission.findViewById(R.id.btn_settings);
                    btn_yes.setText("OK");
                    btn_settings.setVisibility(View.GONE);
                    btn_yes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            rl_dialog.setVisibility(View.GONE);
                        }
                    });
                }
            });
        }
    }

    /**
     * Abre el intento de detalles de configuración de nuestra aplicación
     */
    public void openSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    /**
     * Hace la Accion
     */
    private void doAction() {
        Intent i = new Intent(this, SearchNearMeActivity.class);
        startActivityForResult(i, ACTIVITY_SEARCH_NEAR_ME);
        this.overridePendingTransition(0, 0);

    }
}
