package com.innohawk.dan.appconfig;

/**
 * Created by innohawk on 16/3/16.
 */
public class AppConfig {
    // Server user login url
    public static String URL_MAIN                   = "https://social.budbuddee.com/xmlrpc/"; //Get Methods
    public static String URL_METHODS                = "https://social.budbuddee.com/xmlrpc/"; //Get Methods
    public static String URL_VIDEOS                 = "https://social.budbuddee.com/modules/pcint/PcintPlayVideo.php"; //Update GCM value
    public static String LOCK_TO_SITE               = "https://social.budbuddee.com"; //Fix URL
    public static String URL_REGISTER               = "https://social.budbuddee.com/xmlrpc/"; //Get Methods

    public static String URL_WALL                   = "modules%2Fpcint%2Fmobilehotnews%2Fclasses%2FPcintMobHotNewsRequest.php%3Faction%3Dmenu";
    public static String URL_STORE                  = "modules%2Fpcint%2Fmobilestore%2Fclasses%2FPcintMobStoreRequest.php%3Faction%3Ddispensary";
    public static String SENDER_ID                  = ""; //FOR PUSH NOTIFICATION -- HAY que modificarlo en STRINGS:: Tantas lenguas como tengas...
    public static String Fb_ID                      = ""; //This field define in MANIFETS
    public static String GoogleMaps                 = ""; //This field define in MANIFETS
    public static final int DATABASE_VERSION        = 1; //Versión de la BBDD
    public static final String DATABASE_NAME        = "INNOHAWK"; //Nombre de la BBDD
    public static final String FILENAME             = "sites.txt"; //Fichero donde se guarda La URL de los sitios
    /* Creación de las Tablas*/
    public static final String TABLE_USER           = "user"; //Nombre de la tabla de la BBDD - En este caso meteremos push y lengua
        public static final String KEY_ID           = "id"; //Nombre de los campos de la taba USER pos=0
        public static final String KEY_USERNAME     = "username"; //Nombre de los campos de la taba USER pos=1
        public static final String KEY_PWR          = "pwr"; //Nombre de los campos de la taba USER pos=2
        public static final String KEY_STATUS       = "status"; //Nombre de los campos de la taba USER pos=3
        public static final String KEY_CREATED_AT   = "created_at"; //Nombre de los campos de la taba USER pos=4
    public static final String TABLE_GCM            = "gcm";
        public static final String KEY2_ID           = "id"; //Nombre de los campos de la taba USER pos=0
        public static final String KEY2_TOKEN        = "token"; //Nombre de los campos de la taba USER pos=1
        public static final String KEY2_EMAI         = "emai"; //Nombre de los campos de la taba USER pos=2
        public static final String KEY2_CREATED_AT   = "created_at"; //Nombre de los campos de la taba USER pos=3
    /* Fin creacion tablas TABLE_USER */
    /* Color General de los iconos del menu Slide */
    public static final String COLOR_MENU_ICON_SLIDE   = "#000000"; //Tinte negro
    public static final String COLOR_MENU_TEXT_BADGE   = "#ffffff"; //Tinte blanco

    //Preferencias
    public static final String My_IH_NameLogin = "ihCoLogin";
    public static final String FilterConfig = "ihFilter";
    public static final String EmailSend = "admin@budbuddee.com";

    //FIREBASE MENSAJERIA
    public static final String MY_PREFS_SYSYEM = "System";
    public static final String MY_PREFS_NAME_ERROR = "ErrorValue"; //Existen valores como: error_reviewlist (que es para errores en Review.java)
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
}
