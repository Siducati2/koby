package com.innohawk.dan.notification;

/**
 * Created by xploresound on 6/12/17.
 */

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.innohawk.dan.Connector;
import com.innohawk.dan.appconfig.AppConfig;
import com.innohawk.dan.sqlite.SQLiteActivity;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by innohawk on 30/8/17.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private SQLiteActivity db;
    private String sToken;
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        //sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(AppConfig.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        //Guardamos token en memoria...
        SharedPreferences.Editor editor = getSharedPreferences(AppConfig.My_IH_NameLogin, MODE_PRIVATE).edit();
        editor.putString("token_firebase","" + token);
        editor.commit();
        // sending gcm token to server
        Log.e(TAG, "FIREBASE sendRegistrationToServer: " + token);
        String imei = "innoHawk";
        String sCorrectUrl = AppConfig.URL_METHODS;
        String sMethod = "pcint.register_gcm";
        Object[] aVars = {token, imei};
        Object[] aParams = {
                "push_notification",
                "GCM",
                aVars
        };
        Connector o = new Connector(sCorrectUrl, "", "", 0);
        o.execAsyncMethod(sMethod, aParams, new Connector.Callback() {
            @SuppressWarnings("unchecked")
            public void callFinished(Object result) {
            }
        }, this);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(AppConfig.MY_PREFS_SYSYEM, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("token", token);
        editor.commit();
        //en BBDD
        // Iniciamos la BBDD y Grabamos de nuevo, actualizamos o nada.. aunque en este caso solo valdria actualizar...
        db = new SQLiteActivity(getApplicationContext());
        HashMap<String, String> gcm = db.getGCMvalues(token, "innoHawk");
        Integer id = Integer.valueOf(gcm.get("id"));
        String token_ = gcm.get("token");
        String emai = gcm.get("emai");
        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date()); //Get Data today

        if(token_ == null && emai == null) { //Escribimos en la BBDD
            Log.d(TAG, "FIREBASE Esta vacio asi que creamos nuevo");
            db.addToken(token, "innoHawk", currentDateTimeString);
            //sendRegistrationToServer(token);
            sToken = token;
            Log.d(TAG, "El resultado de enviar el token e IMEI:" + token);
            MyFirebaseInstanceIDService.RegitroGcmcAsyncTask regitroGcmcAsyncTask = new MyFirebaseInstanceIDService.RegitroGcmcAsyncTask();
            regitroGcmcAsyncTask.execute();
        }else if (!token_.equals(token)){ //Debemos Actualizar el nuevo TOKEN
            Log.d(TAG, "FIREBASE Es diferente asi que actualizamos");
            db.updateGCM(token, Integer.valueOf(id), currentDateTimeString);
            //sendRegistrationToServer(token);
            sToken = token;
            MyFirebaseInstanceIDService.RegitroGcmcAsyncTask regitroGcmcAsyncTask = new MyFirebaseInstanceIDService.RegitroGcmcAsyncTask();
            regitroGcmcAsyncTask.execute();
        }else{ // Pues si existe y no esta repetido se deja igual
            Log.d(TAG, "FIREBASE Lo dejamos igual");
        }
    }

    private class RegitroGcmcAsyncTask extends AsyncTask<String , String, Object>
    {
        @Override
        protected Object doInBackground(String ... params) {
            try {
                sendRegistrationToServer(sToken);
                return true;
            }
            catch (Exception ex){
                return ex;
            }
        }

        protected void onProgressUpdate(String... progress) {
            //Toast.makeText(getApplicationContext(),progress[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Object result)
        {
            if(result instanceof  String)
            {
                String resulatado = (String)result;
                //Toast.makeText(getApplicationContext(), "Registro exitoso. " + resulatado, Toast.LENGTH_SHORT).show();
            }
            else if (result instanceof Exception)//Si el resultado es una Excepcion..hay error
            {
                Exception ex = (Exception) result;
                //Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }
}
