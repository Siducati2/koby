package com.innohawk.dan.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapsInitializer;
import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.LoaderImageView;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.activityDropMenu.ActivityDropMenu;
import com.innohawk.dan.activitymaps.ActivityMapsDispensaries;
import com.innohawk.dan.activitymaps.GPSTracker;
import com.innohawk.dan.activitymaps.GetSetMaps;
import com.innohawk.dan.appconfig.AppConfig;
import com.innohawk.dan.appconfig.RoundedCornersTransformation;
import com.innohawk.dan.profile.ProfileActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by innohawk on 20/11/17.
 */

public class SearchHeaderActivity extends ActivityBase {
    private static final String TAG = "OO ActivitySearch";
    String isGuest;
    private String Error = null;
    protected Object[] m_aMenu; //Array con la lista
    public static ArrayList<GetSetMaps> arrayList;
    ListView list_detail;
    ProgressDialog progressDialog;
    int MainPosition = 0; //Esto es para ir a la ficha detalles
    int pos; // Es la posicion del elemento al hacer "click". No es el mismo que la id!!!!!
    String Search;
    //Permission
    GPSTracker gps;
    static double latitude_permission;
    static double longitude_permission;
    private static final int MY_LOCATION = 0; //Tiene implicito el READ (que es el que necesitamos
    View layoutActivityNotData,layoutHomePermission;
    RelativeLayout rl_dialog,rl_dialoguser; //sale el mensaje d eaceptar permission!
    Button btn_back,reload;
    protected LoaderImageView m_viewImageLoader;

    Typeface tt;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, true, false, false);
        setContentView(R.layout.activity_maps_bysearch);
        //Inicializamos
        //Fx para saber si es guest!
        SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
        isGuest = prefs.getString("isGuest", null);
        btn_back = (Button)findViewById(R.id.buttonBack);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        reload = (Button) findViewById(R.id.buttonRefresh);
        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadRemoteData();
            }
        });
        getintent(); //En el intent, recibimos la latitud, longitud de la posicion del usuario!
    }

    private void getintent() {
        // TODO Auto-generated method stub
        Intent ih = getIntent();
        Search = ih.getStringExtra("search");
        MapsInitializer.initialize(getApplicationContext());
        //Load Data
        TextView title = (TextView)findViewById(R.id.textTitle);
        title.setText(getResources().getString(R.string.header_Search_layout) + " " + Search);
        tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        title.setTypeface(tt);
        verifyPermissionLocation();
    }


    //Server
    protected void reloadRemoteData() {
        String lat_server = String.valueOf(latitude_permission);
        String lng_server = String.valueOf(longitude_permission);
        Connector o = Main.getConnector();
        Object[] aParams;
        String sMethod;
        final String sGetMap;
        sMethod = "ih.SearchSpecialHeader";
        sGetMap = "ihGetSearch";
        aParams = new Object[]{
                o.getUsername(),
                o.getPassword(),
                Main.getLang(),
                lat_server,
                lng_server,
                isGuest,
                Search,
                "0"
        };

        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Map<String, Object> map = (Map<String, Object>) result;
                initMenu((Object[]) map.get(sGetMap));
            }
        }, this);

    }

    protected void initMenu(Object[] aMenu) {
        m_aMenu = aMenu;
        arrayList = new ArrayList<GetSetMaps>();
        arrayList.clear();
        if(getCountArray() == 0){
            rl_dialoguser = (RelativeLayout) findViewById(R.id.rl_infodialog);
            layoutActivityNotData = getLayoutInflater().inflate(R.layout.dialog_notdata, rl_dialoguser, false);
            rl_dialoguser.addView(layoutActivityNotData);
            rl_dialoguser.startAnimation(AnimationUtils.loadAnimation(SearchHeaderActivity.this, R.anim.popup));
            Button btn_yes = (Button) layoutActivityNotData.findViewById(R.id.btn_yes);
            btn_yes.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    rl_dialoguser.setVisibility(View.GONE);
                }
            });
        }
        for (int i = 0; i < getCountArray(); ++i) {
            GetSetMaps temp = new GetSetMaps();
            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) m_aMenu[i];
            if (map.get("visibility").equals("yes")) {
                temp.setId(map.get("id"));
                temp.setName(map.get("name"));
                temp.setAddress(map.get("address"));
                temp.setLatitude(map.get("latitude"));
                temp.setLongitude(map.get("longitude"));
                temp.setDistance(map.get("distance"));
                temp.setFlagType(map.get("type"));
                temp.setSex(map.get("sex"));
                temp.setUsername(map.get("username"));
                temp.setAdressList(map.get("city")); //Lista que lo pasamos a city.. habra que cambiarlo
                temp.setThubnailimage(map.get("img"));//img
                temp.setTime(map.get("age"));//age

                arrayList.add(temp);
                list_detail = (ListView) findViewById(R.id.list_detail);
                list_detail.setAdapter(null);
                LazyAdapter lazy = new LazyAdapter(SearchHeaderActivity.this, arrayList);
                lazy.notifyDataSetChanged();
                list_detail.setAdapter(lazy);
                list_detail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // TODO Auto-generated method stub
                        MainPosition = 1;
                        pos = position;
                        ContinueIntent();
                    }
                });
            }
        }
    }

    public int getCountArray() {
        return m_aMenu.length;
    }

    // binding data in listview usind adapter class: Normal Load
    public class LazyAdapter extends BaseAdapter {
        private Activity activity;
        private ArrayList<GetSetMaps> data;
        private LayoutInflater inflater = null;

        public LazyAdapter(Activity a, ArrayList<GetSetMaps> str) {
            activity = a;
            data = str;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        public void addItems(ArrayList<GetSetMaps> fileList) {
            // TODO Auto-generated method stub
            if (data != null) {
                data.addAll(fileList);
            } else {
                data = fileList;
            }
        }
        @Override
        public int getCount() {
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            return position;
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View ih = convertView;
            if (convertView == null) {
                ih = inflater.inflate(R.layout.activity_maps_listviewcellsnew, null);
            }

            TextView title = (TextView) ih.findViewById(R.id.text_title);
            title.setTypeface(tt);
            title.setText(" " +data.get(position).getName());
            TextView age = (TextView) ih.findViewById(R.id.txt_age);
            age.setTypeface(tt);
            TextView txt_km = (TextView) ih.findViewById(R.id.txt_distance);
            txt_km.setTypeface(tt);
            //Pasamos indivicualmente
            TextView sex = (TextView) ih.findViewById(R.id.txt_sex);
            sex.setTypeface(tt);
            String Flag_active = data.get(position).getFlagType();
            if (Flag_active.equals("dispensaries")) {
                txt_km.setText(" " +data.get(position).getDistance() + " " +getResources().getString(R.string.ud_distance));
                age.setText("");
                age.setVisibility(View.INVISIBLE);
                sex.setText(" " +data.get(position).getAddress() + " - " +data.get(position).getAdressList());

            } else if (Flag_active.equals("profiles")) {
                txt_km.setText(" " +data.get(position).getDistance() + " " +getResources().getString(R.string.ud_distance));
                age.setText(" " +data.get(position).getTime() +" "+getResources().getString(R.string.ud_age));
                sex.setText(" " +data.get(position).getSex());
            }else if (Flag_active.equals("sp")) {
                txt_km.setText(" " +data.get(position).getDistance());
                age.setText("");
                age.setVisibility(View.INVISIBLE);
                sex.setText("THC: " +data.get(position).getAddress() + "   CBD: " +data.get(position).getAdressList()+ "   CBN: " +data.get(position).getTime());
            }
            //IMG BG
            String image = data.get(position).getThubnailimage().replace(" ", "%20");
            ImageView programImage = (ImageView) ih.findViewById(R.id.img_thumb);
            m_viewImageLoader = (LoaderImageView) ih.findViewById(R.id.media_images_image_view);
            m_viewImageLoader.setOnlySpinnerDrawable((String) image);
            if(image.isEmpty()) {
                image = String.valueOf(R.drawable.logo_about_256);
                programImage.setImageResource(Integer.parseInt(image));
            }else{
                final int radius = 6;
                final int margin = 0;
                final Transformation transformation = new RoundedCornersTransformation(radius, margin);
                Picasso.with(SearchHeaderActivity.this)
                        .load(image)
                        .transform(transformation)
                        .into(programImage);
            }
            return ih;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.gmap, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return true;

    }

    private void ContinueIntent() {
        progressDialog = new ProgressDialog(SearchHeaderActivity.this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(true);
        progressDialog.show();
        if (MainPosition == 1) {
            if (arrayList.get(pos).getFlagType().equals("dispensaries")) {
                Intent iv = new Intent(SearchHeaderActivity.this, ActivityMapsDispensaries.class);
                iv.putExtra("lat", "" + latitude_permission);
                iv.putExtra("lng", "" + longitude_permission);
                iv.putExtra("id", "" + arrayList.get(pos).getId());
                startActivityForResult(iv, 0);
                SearchHeaderActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }else if(arrayList.get(pos).getFlagType().equals("sp")){
                Intent i = new Intent(SearchHeaderActivity.this, ActivityDropMenu.class);
                i.putExtra("action", arrayList.get(pos).getId());
                startActivityForResult(i, 0);
                SearchHeaderActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }else {
                if (arrayList.get(pos).getFlagType().equals("profiles")) {
                    if (isGuest.equals("no")) {
                        Intent i = new Intent(SearchHeaderActivity.this, ProfileActivity.class);
                        i.putExtra("username", arrayList.get(pos).getUsername());
                        startActivityForResult(i, 0);
                        SearchHeaderActivity.this.overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
                    } else {
                        Toast.makeText(SearchHeaderActivity.this, getResources().getString(R.string.userGuest_exception), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(SearchHeaderActivity.this, getResources().getString(R.string.userGuest_exception), Toast.LENGTH_LONG).show();
                }
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    }


    //LOCATION MAPS
    //Paso 1. Verificar permiso
    @SuppressLint("WrongConstant")
    private void verifyPermissionLocation() {
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
            getGPSLocation();
        }
    }
    private void requestPermission() {
        //shouldShowRequestPermissionRationale es verdadero solamente si ya se había mostrado
        //anteriormente el dialogo de permisos y el usuario lo negó
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)||(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {
            showSnackBar();
        } else {
            //si es la primera vez se solicita el permiso directamente
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_LOCATION);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Si el requestCode corresponde al que usamos para solicitar el permiso y
        //la respuesta del usuario fue positiva
        if (requestCode == MY_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getGPSLocation();
            } else {
                showSnackBar();
            }
        }
    }
    private void showSnackBar() {
        RelativeLayout rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        if (rl_back == null) {
            rl_dialog = (RelativeLayout) findViewById(R.id.rl_permissiondialog);
            layoutHomePermission = getLayoutInflater().inflate(R.layout.activity_permission, rl_dialog, false);
            rl_dialog.addView(layoutHomePermission);
            rl_dialog.startAnimation(AnimationUtils.loadAnimation(SearchHeaderActivity.this, R.anim.popup));
            Button btn_yes = (Button) layoutHomePermission.findViewById(R.id.btn_yes);
            Button btn_settings = (Button) layoutHomePermission.findViewById(R.id.btn_settings);
            btn_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    rl_dialog.setVisibility(View.GONE);
                    getGPSLocation();
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
                            getGPSLocation();
                        }
                    });
                }
            });
        }
    }
    public void openSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
    private void getGPSLocation() { //SIEMPRE tenemos en cuenta si es GUEST
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)||(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))) {
            latitude_permission = 0.0f;
            longitude_permission = 0.0f;
        }else {
            gps = new GPSTracker(SearchHeaderActivity.this,isGuest);
            if (gps.canGetLocation()) {
                latitude_permission = gps.getLatitude();
                longitude_permission = gps.getLongitude();
            } else {
                // can't get location GPS or Network is not enabled Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
                //Damos los valores manualmente
                latitude_permission = 0.0f;
                longitude_permission = 0.0f;
            }
        }
        reloadRemoteData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}