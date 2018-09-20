package com.innohawk.dan.activitymaps;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.MapsInitializer;
import com.innohawk.dan.ActivityBase;
import com.innohawk.dan.Connector;
import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.innohawk.dan.appconfig.AppConfig;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by innohawk on 24/10/17.
 */
public class ActivityMapsFilter extends ActivityBase {
    private static final String TAG = "OO ActivityMapsFilter";
    String isGuest;
    SeekBar filter_ratio, filter_age;
    Switch switch_profiles,switch_dispensaries,switch_connects;
    Boolean switch_Profiles,switch_Dispensaries,switch_Connects;
    TextView filter_ratio_text,filter_age_text;
    String filter_search_String,filter_ratio_String,filter_age_String,filter_Gender_String,filter_Status_String,filter_Connects_String;
    EditText filter_search;
    protected RadioButton Gender_male,Gender_female,Gender_gay,Status_single,Status_couple;
    boolean gender_male,gender_female,gender_gay,status_single,status_couple;
    Spinner SpinnerTypes,SpinnerCat,SpinnerList;
    public static ArrayList<String> arrayListSpinnerTypes,arrayListSpinnerCat,arrayListSpinnerList;
    String SpinnerTypes_,SpinnerCat_,SpinnerList_;
    protected Object[] m_aType,m_aCategory,m_aList; //Array con la lista de categorias
    Button btn_cancel,btn_apply;
    //Lo que recibimos
    String getintentToActivity;
    String setintentToActivity;
    static double latitude_permission;
    static double longitude_permission;
    int filter_active;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, false, false, false);
        setContentView(R.layout.activity_maps_filter);
        Typeface tt = Typeface.createFromAsset(getAssets(), "fonts/Mermaid1001.ttf");
        //Borramos variable para saber si filtro activo...
        SharedPreferences prefs_filter = getSharedPreferences(AppConfig.FilterConfig, MODE_PRIVATE);
        prefs_filter.edit().remove("isFilterActive").commit();

        //TEXT SEARCH
        filter_search = (EditText)findViewById(R.id.edit_search);
        filter_search.setTypeface(tt);
        //Ratio
        TextView titleRatio = (TextView)findViewById(R.id.filter_ratio);
        titleRatio.setTypeface(tt);
        TextView titleProfle = (TextView)findViewById(R.id.Text_filter_profiles);
        titleProfle.setTypeface(tt);
        TextView titleAges = (TextView)findViewById(R.id.filter_ages);
        titleAges.setTypeface(tt);
        TextView titleGender = (TextView)findViewById(R.id.filter_gender);
        titleGender.setTypeface(tt);
        TextView titleStatus = (TextView)findViewById(R.id.filter_status);
        titleStatus.setTypeface(tt);
        TextView titleDispensaries = (TextView)findViewById(R.id.Text_filter_dispensaries);
        titleDispensaries.setTypeface(tt);
        TextView titleTypes = (TextView)findViewById(R.id.filter_types);
        titleTypes.setTypeface(tt);
        TextView titleCategories = (TextView)findViewById(R.id.filter_categories);
        titleCategories.setTypeface(tt);
        TextView titleListing = (TextView)findViewById(R.id.filter_listing);
        titleListing.setTypeface(tt);
        TextView titleConnects = (TextView)findViewById(R.id.Text_filter_connects);
        titleConnects.setTypeface(tt);

        filter_ratio=(SeekBar)findViewById(R.id.seekbar_filter_ratio);
        filter_ratio_text=(TextView)findViewById(R.id.Textseekbar_filter_ratio);
        filter_ratio_text.setTypeface(tt);
        filter_ratio_text.setText(String.valueOf(2) + " mi");
        filter_ratio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar bar) {
            }
            public void onStartTrackingTouch(SeekBar bar) {
            }
            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                if( (paramInt>=0)&& (paramInt<=2) ){
                    filter_ratio_text.setText(String.valueOf(2) + " mi");
                }else if( (paramInt>2)&& (paramInt<5000) ){
                    filter_ratio_text.setText(String.valueOf(paramInt) + " mi");
                }else if( (paramInt==5000)) {
                    filter_ratio_text.setText("5000 mi");
                }
            }
        });
        //*******
        //Profiles
        //*******
        switch_profiles = (Switch)findViewById(R.id.switch_profiles);
        filter_age =(SeekBar)findViewById(R.id.seekbar_filter_ages);
        filter_age_text=(TextView)findViewById(R.id.Textseekbar_filter_ages);
        filter_age_text.setTypeface(tt);
        filter_age_text.setText("-");
        //Checks Orders
        Gender_male = (RadioButton) findViewById(R.id.gendermale);
        Gender_male.setTypeface(tt);
        Gender_female = (RadioButton) findViewById(R.id.genderfemale);
        Gender_female.setTypeface(tt);
        //Gender_gay = (RadioButton) findViewById(R.id.gendergay);
        //Gender_gay.setTypeface(tt);
        filter_Gender_String = "-1";
        Status_single = (RadioButton) findViewById(R.id.status_single);
        Status_single.setTypeface(tt);
        Status_couple = (RadioButton) findViewById(R.id.status_couple);
        Status_couple.setTypeface(tt);
        filter_Status_String = "-1";
        switch_Profiles = false;
        //Desactivamos click gender
        Gender_male.setClickable(false);
        Gender_female.setClickable(false);
        //Gender_gay.setClickable(false);
        Status_single.setClickable(false);
        Status_couple.setClickable(false);
        switch_profiles.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch_Profiles = isChecked;
                if(switch_Profiles.equals(true)) {
                    filter_age.setEnabled(true);
                    filter_age.setClickable(true);
                    filter_age_text.setText(filter_age_text.getText());
                    //Gender:: Si esta activada... la que marque
                    Gender_male.setClickable(true);
                    Gender_female.setClickable(true);
                    //Gender_gay.setClickable(true);
                    //Status
                    Status_single.setClickable(true);
                    Status_couple.setClickable(true);
                }else{
                    filter_age.setEnabled(false);
                    filter_age.setClickable(false);
                    filter_age_text.setText("-");
                    //Gender:: Desacttivamos el group y lo ponemos a -1
                    Gender_male.setClickable(false);
                    Gender_female.setClickable(false);
                    //Gender_gay.setClickable(false);
                    Gender_male.setChecked(false);
                    Gender_female.setChecked(false);
                    //Gender_gay.setChecked(false);
                    filter_Gender_String = "-1";
                    //Status
                    Status_single.setClickable(false);
                    Status_couple.setClickable(false);
                    Status_single.setChecked(false);
                    Status_couple.setChecked(false);
                    filter_Status_String = "-1";
                }
            }
        });
        filter_age.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStopTrackingTouch(SeekBar bar) {
            }
            public void onStartTrackingTouch(SeekBar bar) {
            }
            public void onProgressChanged(SeekBar bar, int paramInt, boolean paramBoolean) {
                if( (paramInt>=0)&& (paramInt<=1) ){
                    filter_age_text.setText(String.valueOf(1));
                }else if( (paramInt>1)&& (paramInt<99) ){
                    filter_age_text.setText(String.valueOf(paramInt));
                }else if( (paramInt==99)) {
                    filter_age_text.setText("99");
                }
            }
        });
        //*******
        //Dispensaries
        //*******
        switch_dispensaries = (Switch)findViewById(R.id.switch_dispensaries);
        switch_Dispensaries = false;
        //SPINNER
        SpinnerTypes = (Spinner)findViewById(R.id.spinner_types);
        SpinnerCat = (Spinner)findViewById(R.id.spinner_categories);
        SpinnerList = (Spinner)findViewById(R.id.spinner_listing);
        arrayListSpinnerTypes = new ArrayList<String>();
        arrayListSpinnerTypes.add(getResources().getString(R.string.filter_dispensaries_showall));
        arrayListSpinnerTypes.add(getResources().getString(R.string.filter_dispensaries_typeMed));
        arrayListSpinnerTypes.add(getResources().getString(R.string.filter_dispensaries_typeRec));
        arrayListSpinnerList = new ArrayList<String>();
        arrayListSpinnerList.add(getResources().getString(R.string.filter_dispensaries_showall));
        arrayListSpinnerList.add(getResources().getString(R.string.filter_dispensaries_HourOpen));
        arrayListSpinnerList.add(getResources().getString(R.string.filter_dispensaries_HourClosed));
        //SPINNER
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityMapsFilter.this, R.layout.innohawk_list_spinner, arrayListSpinnerTypes);
        SpinnerTypes.setAdapter(adapter);
        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(ActivityMapsFilter.this, R.layout.innohawk_list_spinner, arrayListSpinnerList);
        SpinnerList.setAdapter(adapterList);
        SpinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //SpinnerTypes_ = (String) parent.getItemAtPosition(position);
                if(switch_Dispensaries.equals(true)) {
                    if (position == 0)
                        SpinnerTypes_ = "all";
                    else if (position == 1)
                        SpinnerTypes_ = "medicinal";
                    else if (position == 2)
                        SpinnerTypes_ = "recreational";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //SpinnerTypes_ = getResources().getString(R.string.filter_dispensaries_showall);
                SpinnerTypes_ = "all";
            }
        });
        SpinnerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //SpinnerList_ = (String) parent.getItemAtPosition(position);
                if(switch_Dispensaries.equals(true)) {
                    if (position == 0)
                        SpinnerList_ = "all";
                    else if (position == 1)
                        SpinnerList_ = "open";
                    else if (position == 2)
                        SpinnerList_ = "closed";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //SpinnerList_ = getResources().getString(R.string.filter_dispensaries_showall);
                SpinnerList_ = "all";
            }
        });

        SpinnerTypes.setEnabled(false);
        SpinnerCat.setEnabled(false);
        SpinnerList.setEnabled(false);
        SpinnerTypes_ = "";
        SpinnerCat_ = getResources().getString(R.string.filter_dispensaries_showall);
        SpinnerList_ = "";
        switch_dispensaries.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch_Dispensaries = isChecked;
                if(switch_Dispensaries.equals(true)) {
                    //Activamos para elegir
                    SpinnerTypes.setEnabled(true);
                    SpinnerCat.setEnabled(true);
                    SpinnerList.setEnabled(true);
                    SpinnerTypes_ = "all"; //Lo iniciamos.. a ver que pasa!
                    SpinnerList_ = "all";
                }else{
                    SpinnerTypes.setEnabled(false);
                    SpinnerCat.setEnabled(false);
                    SpinnerList.setEnabled(false);
                    SpinnerTypes_ = "";
                    SpinnerCat_ = getResources().getString(R.string.filter_dispensaries_showall);
                    SpinnerList_ = "";
                }
            }
        });
        //*******
        //Connects
        //*******
        switch_connects = (Switch)findViewById(R.id.switch_connects);
        switch_Connects = false;
        filter_Connects_String = "no";
        switch_connects.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch_Connects = isChecked;
                if(switch_Connects.equals(true))
                    filter_Connects_String = "yes";
                else
                    filter_Connects_String = "no";
            }
        });
        //Buttons
        btn_apply = (Button) findViewById(R.id.btn_filter_apply);
        btn_apply.setTypeface(tt);
        btn_cancel = (Button) findViewById(R.id.btn_filter_cancel);
        btn_cancel.setTypeface(tt);

        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //Search
                    filter_search_String = filter_search.getText().toString().replace(" ", "%20");
                    if (filter_search_String.equals(null)) {
                        filter_search_String = "";
                    }
                    //Ratio
                    filter_ratio_String = filter_ratio_text.getText().toString().replace(" mi", "");
                    //Ages
                    filter_age_String = filter_age_text.getText().toString();
                    //Gender:Check Order
                    gender_male = Gender_male.isChecked();
                    gender_female = Gender_female.isChecked();
                    //gender_gay = Gender_gay.isChecked();
                    if(gender_male==true)
                        filter_Gender_String = "0";
                    else if(gender_female==true)
                        filter_Gender_String = "1";
                    else
                        filter_Gender_String = "-1";
                    //else if(gender_gay==true)
                        //filter_Gender_String = "2";

                    //Status:Check Order
                    status_single = Status_single.isChecked();
                    status_couple = Status_couple.isChecked();
                    if(status_single==true)
                        filter_Status_String = "0";
                    else if(status_couple==true)
                        filter_Status_String = "1";
                    else
                        filter_Status_String = "-1";
                    //Connects
                   // filter_Connects_String;
                    //Cats:
                   // SpinnerTypes_ = SpinnerTypes_;
                   // SpinnerCat_=  SpinnerCat_;
                   // SpinnerList_=SpinnerList_;


                } catch (NullPointerException e) {
                    // TODO: handle exception
                }
                //Toast.makeText(ActivityMapsFilter.this,"Type: "+SpinnerTypes_ +" -CAT: "+SpinnerCat_ +" -List: "+SpinnerList_, Toast.LENGTH_LONG).show();
                //Llamamos al server
                //Guardamos en variables
                SharedPreferences.Editor editor = getSharedPreferences(AppConfig.FilterConfig, MODE_PRIVATE).edit();
                editor.putString("isFilterActive","yes");
                editor.commit();
                //Nos vamos a la clase! con el filtro activado!
                Intent ih = null;
                if(getintentToActivity.equals("yes"))
                    ih = new Intent(ActivityMapsFilter.this, ActivityMaps.class);
                else
                    ih = new Intent(ActivityMapsFilter.this, ActivityMapsList.class);
                ih.putExtra("KeySearch", ""+filter_search_String);
                ih.putExtra("Ratio", ""+filter_ratio_String);
                ih.putExtra("Age", ""+filter_age_String);
                ih.putExtra("Gender", ""+filter_Gender_String);
                ih.putExtra("Status", ""+filter_Status_String);
                ih.putExtra("SpinnerTypes", ""+SpinnerTypes_);
                ih.putExtra("SpinnerCat", ""+SpinnerCat_);
                ih.putExtra("SpinnerList", ""+SpinnerList_);
                ih.putExtra("Connect", ""+filter_Connects_String);
                ih.putExtra("APP_PACKAGE_EXTRAS", "com.filter");
                ih.putExtra("lat", ""+latitude_permission);
                ih.putExtra("lng", ""+longitude_permission);
                ih.putExtra("filteractive", "1");
                startActivity(ih);
                overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Borramos la variable filter, para que una vez en mapa o en list, busque normal
                SharedPreferences prefs_filter = getSharedPreferences(AppConfig.FilterConfig, MODE_PRIVATE);
                prefs_filter.edit().remove("isFilterActive").commit();
                //ActivityMapsFilter.this.finish();
                Intent ih = null;
                if(getintentToActivity.equals("yes"))
                    ih = new Intent(ActivityMapsFilter.this, ActivityMaps.class);
                else
                    ih = new Intent(ActivityMapsFilter.this, ActivityMapsList.class);
                ih.putExtra("lat", ""+latitude_permission);
                ih.putExtra("lng", ""+longitude_permission);
                ih.putExtra("filteractive", "0");
                startActivity(ih);
                overridePendingTransition(R.anim.lefttoright, R.anim.right_slide_out);
            }
        });
        //Inicializamos
        getintent(); //En el intent, recibimos la latitud, longitud de la posicion del usuario!

    }

    private void getintent() {
        // TODO Auto-generated method stub
        Intent ih = getIntent();
        getintentToActivity = ih.getStringExtra("map"); //Asi sabremos la clase!
        latitude_permission = Double.parseDouble(ih.getStringExtra("lat"));
        longitude_permission = Double.parseDouble(ih.getStringExtra("lng"));
        filter_active = (int) Double.parseDouble(ih.getStringExtra("filteractive"));
        MapsInitializer.initialize(getApplicationContext());
        //Load Data:: Las categorias
        //reloadRemoteData();
    }

    protected void reloadRemoteData() {
        Connector o = Main.getConnector();
        SharedPreferences prefs = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE);
        isGuest = prefs.getString("isGuest",null);
        Object[] aParams = {
                o.getUsername(),
                o.getPassword(),
                Main.getLang(),
                isGuest,
                "0"
        };
        o.execAsyncMethod("ih.dispensariesfiltercategories", aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
                Map<String, Object> map = (Map<String, Object>) result;
                initMenu((Object[]) map.get("GetCatListFilter"),(Object[]) map.get("GetTypesListFilter"),(Object[]) map.get("GetListingListFilter"));
            }
        }, this);

    }
    protected void initMenu(Object[] aCat,Object[] aType,Object[] aList) {


        m_aType = aType;
        arrayListSpinnerTypes = new ArrayList<String>();
        arrayListSpinnerTypes.add(getResources().getString(R.string.filter_dispensaries_showall));
        /*for (int k = 0; k < m_aType.length; ++k)
        {
            Map<String, String> map = (Map<String, String>) m_aType[k];
            String Category = map.get("title");
            arrayListSpinnerTypes.add(Category);
        }*/
        arrayListSpinnerTypes.add(getResources().getString(R.string.filter_dispensaries_typeMed));
        arrayListSpinnerTypes.add(getResources().getString(R.string.filter_dispensaries_typeRec));


        m_aCategory = aCat;
        arrayListSpinnerCat = new ArrayList<String>();
        arrayListSpinnerCat.add(getResources().getString(R.string.filter_dispensaries_showall));
        for (int m = 0; m < m_aCategory.length; ++m)
        {
            Map<String, String> map = (Map<String, String>) m_aCategory[m];
            String Category = map.get("title");
            arrayListSpinnerCat.add(Category);
        }
        m_aList = aList;
        arrayListSpinnerList = new ArrayList<String>();
        arrayListSpinnerList.add(getResources().getString(R.string.filter_dispensaries_showall));
        arrayListSpinnerList.add(getResources().getString(R.string.filter_dispensaries_HourOpen));
        arrayListSpinnerList.add(getResources().getString(R.string.filter_dispensaries_HourClosed));
        /*for (int n = 0; n < m_aList.length; ++n)
        {
            Map<String, String> map = (Map<String, String>) m_aList[n];
            String Category = map.get("title");
            arrayListSpinnerList.add(Category);
        }*/


        //SPINNER
        SpinnerTypes = (Spinner)findViewById(R.id.spinner_types);
        SpinnerCat = (Spinner)findViewById(R.id.spinner_categories);
        SpinnerList = (Spinner)findViewById(R.id.spinner_listing);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityMapsFilter.this, R.layout.innohawk_list_spinner, arrayListSpinnerTypes);
        SpinnerTypes.setAdapter(adapter);
        ArrayAdapter<String> adapterCat = new ArrayAdapter<String>(ActivityMapsFilter.this, R.layout.innohawk_list_spinner, arrayListSpinnerCat);
        SpinnerCat.setAdapter(adapterCat);
        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(ActivityMapsFilter.this, R.layout.innohawk_list_spinner, arrayListSpinnerList);
        SpinnerList.setAdapter(adapterList);
        SpinnerTypes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //SpinnerTypes_ = (String) parent.getItemAtPosition(position);
                    if(position==0)
                        SpinnerTypes_="all";
                    else if(position==1)
                        SpinnerTypes_="medicinal";
                    else if(position==2)
                        SpinnerTypes_="recreational";
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //SpinnerTypes_ = getResources().getString(R.string.filter_dispensaries_showall);
                    SpinnerTypes_ = "";
                }
            });
        SpinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SpinnerCat_ = (String) parent.getItemAtPosition(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    SpinnerCat_ = getResources().getString(R.string.filter_dispensaries_showall);
                }
            });
        SpinnerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    //SpinnerList_ = (String) parent.getItemAtPosition(position);
                    if(position==0)
                        SpinnerList_="all";
                    else if(position==1)
                        SpinnerList_="open";
                    else if(position==2)
                        SpinnerList_="closed";
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    //SpinnerList_ = getResources().getString(R.string.filter_dispensaries_showall);
                    SpinnerList_ = "";
                }
            });

    }

    public void onBackPressed() {
        super.onBackPressed();
    }
}
