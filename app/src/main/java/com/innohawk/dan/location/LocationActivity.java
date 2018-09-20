package com.innohawk.dan.location;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.innohawk.dan.Connector;
import com.innohawk.dan.FragmentActivityBase;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class LocationActivity extends FragmentActivityBase {
    private static final String TAG = "OO LocationActivity";
    public static final int RESULT_OK = RESULT_FIRST_USER + 1;
    public static final int ZOOM = 16;
    private GoogleMap m_frMap;
    private String m_sUsername;
    private LocationActivity m_actThis;

    //Permission
    private static final int MY_PERMISSION_VAL = 0; //Tiene implicito el READ (que es el que necesitamos
    View layoutHomePermission;
    RelativeLayout rl_dialog;
    protected TextView textTitle;
    protected Button btn_action_back;
    protected Button btn_action_locationme;
    protected Button btn_action_reload;
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);

        m_actThis = this;

        setContentView(R.layout.location);
        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/nimbusmono-bold.otf");
        textTitle = (TextView) findViewById(R.id.textTitle);
        textTitle.setText(R.string.title_location);
        textTitle.setTypeface(tt);
        //Back
        btn_action_back = (Button) findViewById(R.id.buttonBack);
        btn_action_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //locationme
        btn_action_locationme = (Button) findViewById(R.id.buttonlocation);
        btn_action_locationme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yourMethod();
            }
        });

        //reload
        btn_action_reload = (Button) findViewById(R.id.buttonRefresh);
        btn_action_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadRemoteData();
            }
        });




        Intent i = getIntent();
        m_sUsername = i.getStringExtra("username");

        //m_frMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap_) {
                m_frMap  = googleMap_;
            }
        });

        reloadRemoteData();
        verifyPermissionRequest();
    }

    protected void reloadRemoteData() {
        Connector o = Main.getConnector();

        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                m_sUsername
        };
        o.execAsyncMethod("dolphin.getUserLocation", aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.getUserLocation result: " + result.toString());

                if (true == result.toString().equals("0") || true == result.toString().equals("-1")) {

                    AlertDialog dialog = new AlertDialog.Builder(m_actThis).create();
                    dialog.setMessage(getString(true == result.toString().equals("-1") ? R.string.access_denied : R.string.location_undefined));
                    dialog.setButton(DialogInterface.BUTTON_NEUTRAL, getString(R.string.close), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                            Connector o = Main.getConnector();
                            if (false == m_sUsername.equalsIgnoreCase(o.getUsername())) {
                                finish();
                            }
                        }
                    });
                    dialog.show();

                } else {

                    Map<String, String> map = (Map<String, String>) result;
                    String sLat = map.get("lat");
                    String sLng = map.get("lng");
                    String sType = map.get("type");
                    String sZoom = map.get("zoom");

                    double fLat = 0;
                    double fLng = 0;
                    float fZoom = 3;
                    try {
                        fLat = Double.parseDouble(sLat);
                        fLng = Double.parseDouble(sLng);
                        fZoom = Float.parseFloat(sZoom);
                    } catch (NumberFormatException e) {
                        Log.e(TAG, e.toString());
                    }

                    setMapLocation(fLat, fLng, fZoom, sType);
                }

            }
        }, this);
    }

    public void setMapLocation(double lat, double lng, float fZoom, String sType) {
        if (0 == lat && 0 == lng) {
            Toast toast = Toast.makeText(this, getString(R.string.location_undefined), Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        LatLng latLng = new LatLng(lat, lng);

        m_frMap.clear();
        m_frMap.addMarker(new MarkerOptions().position(latLng).title(m_sUsername));
        m_frMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, fZoom));

        if (sType.equals("satellite") || sType.equals("hybrid"))
            m_frMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

        m_frMap.setMapType(GoogleMap.MAP_TYPE_NORMAL); //Añadimos esta linea pq no queremos que sea satelite.. deberiamos cambiarlo desdde panel admin ....

        Log.i(TAG, "setMapLocation - lat:" + lat + " / lng:" + lng);
    }

    public void setLocation(double fLat, double fLng) {

        setMapLocation(fLat, fLng, ZOOM, "");

        Connector o = Main.getConnector();
        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                String.format("%.8f", fLat).replace(",", "."),
                String.format("%.8f", fLng).replace(",", "."),
                String.format("%d", ZOOM),
                m_frMap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE || m_frMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID ? "hybrid" : "normal"
        };
        o.execAsyncMethod("dolphin.updateUserLocation", aParams, new Connector.Callback() {
            public void callFinished(Object result) {
                Log.d(TAG, "dolphin.updateUserLocation result: " + result.toString());
            }
        }, this);
    }

    public void startProgress() {
        getActionBarHelper().setRefreshActionItemState(true);
    }

    public void stopProgress() {
        getActionBarHelper().setRefreshActionItemState(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Connector o = Main.getConnector();
        if (m_sUsername.equalsIgnoreCase(o.getUsername())) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.location, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.location_update:
                yourMethod();
                return true;
            //break;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Start code innoHawk permission Request */


    //Sacar Método Del menu (menu/location)
    @SuppressLint("WrongConstant")
    private void yourMethod() {
        // TODO Auto-generated method stub
        int locationPermission = 0;
        int locationPermission2 = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            locationPermission = checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
            locationPermission2 = checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION);
            if ((locationPermission == PackageManager.PERMISSION_GRANTED)||(locationPermission2 == PackageManager.PERMISSION_GRANTED)) {
                LocationHelper.LocationResult locationResult = new LocationHelper.LocationResult() {
                    @Override
                    public void gotLocation(Location location) {
                        stopProgress();
                        if (null != location) {
                            Log.i(TAG, "Got Location: " + location);
                            setLocation(location.getLatitude(), location.getLongitude());
                        } else {
                            Toast.makeText(m_actThis, R.string.location_not_available, Toast.LENGTH_LONG).show();
                        }
                    }
                };
                LocationHelper myLocation = new LocationHelper();
                if (myLocation.getLocation(m_actThis, locationResult))
                    startProgress();
                else
                    myLocation.openLocationEnableDialog();
            } else {
                //No has permitodo el acceso mamón!
            }
        }else {

            LocationHelper.LocationResult locationResult = new LocationHelper.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    stopProgress();
                    if (null != location) {
                        Log.i(TAG, "Got Location: " + location);
                        setLocation(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(m_actThis, R.string.location_not_available, Toast.LENGTH_LONG).show();
                    }
                }
            };
            LocationHelper myLocation = new LocationHelper();
            if (myLocation.getLocation(m_actThis, locationResult))
                startProgress();
            else
                myLocation.openLocationEnableDialog();

        }
    }

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


    }

    /* End Code */
}
