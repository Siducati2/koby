package com.innohawk.dan.notification;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.innohawk.dan.Connector;
import com.innohawk.dan.R;
import com.innohawk.dan.appconfig.AppConfig;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.util.Map;

/**
 * Created by innohawk on 24/3/16.
 */

public class GcmUtilitiesActivity {

    public static final String sURL = AppConfig.URL_METHODS;
    private static final String TAG = "OO Connection";
    public static GcmUtilitiesActivity Activity = null;

    public static AlertDialog MostrarAlertDialog(Context activity, String mensaje, String titulo, int icono)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(activity);
        builder1.setMessage(mensaje);
        builder1.setIcon(icono);
        builder1.setTitle(titulo);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder1.create();
        return alertDialog;
    }

    public static boolean CheckPlayServices(Activity context) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, context, 9000).show();
            }
            else
            {
                Toast.makeText(context,"Dispositivo no soportado", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    public static String ObtenerRegistrationTokenEnGcm(Context context) throws  Exception
    {
        InstanceID instanceID = InstanceID.getInstance(context);
        String token = instanceID.getToken(context.getString(R.string.Gcm_Sender_id),
                GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

        return token;
    }

    public static String RegistrarseEnAplicacionServidor(Context context, String registrationToken) throws  Exception
    {


        String imei = DameIMEI(context);
        String sCorrectUrl = AppConfig.URL_METHODS;
        String sMethod = "pcint.register_gcm";
        Object[] aVars = {registrationToken, imei};
        Object[] aParams = {
                "push_notification",
                "GCM",
                aVars
        };

        Connector o = new Connector(sCorrectUrl, "", "", 0);
        m_oSocialCallback.setMethod(sMethod);
        m_oSocialCallback.setParams(aParams);
        m_oSocialCallback.setConnector(o);
        String respuesta = o.execAsyncMethod(sMethod, aParams, m_oSocialCallback, context);

        return respuesta;
       /*
        String stringUrl = sURL + "?device=android&imei=" + imei + "&registrationId=" + registrationToken;

        URL url = new URL(stringUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(10000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("GET");

        int codigoEstado = connection.getResponseCode();
        if(codigoEstado != 200)
            throw new Exception("Error al procesar registro. Estado Http: " + codigoEstado);

        InputStream inputStream = connection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));

        String respuesta = "",linea;
        while ((linea = bufferedReader.readLine()) != null) {
            respuesta = respuesta + linea;
        }

        bufferedReader.close();
        inputStream.close();

        respuesta = new JSONObject(respuesta).getString("RegistroGcmResult");

        if(!respuesta.equals("OK"))
            throw new Exception("Error al registrarse en aplicacion servidor: " + respuesta);

        return respuesta;
        */
    }



    public static String DameIMEI(Context context)
    {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }


    private static sendGCM m_oSocialCallback = new sendGCM() {

        public boolean callFailed(Exception e) {
            return true;
        }

        public void callFinished(Object result) {
            String sStatus = null;

            if (result instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) result;

                sStatus = (String) map.get("RegistroGcmResult");

                Log.d(TAG, "Status Register:" + sStatus);
            }
        }
    };

    public static class sendGCM extends Connector.Callback {
        protected GcmUtilitiesActivity context;
        protected String sMethod;
        protected Object[] aParams;
        protected Connector oConnector;

        public void setMethod(String s) {
            sMethod = s;
        }
        public void setParams(Object[] a) {
            aParams = a;
        }

        public void setConnector(Connector o) {
            oConnector = o;
        }
    }
}
