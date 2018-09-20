package com.innohawk.dan.notification;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.innohawk.dan.sqlite.SQLiteActivity;
import com.google.android.gms.iid.InstanceIDListenerService;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.innohawk.dan.notification.GcmUtilitiesActivity.DameIMEI;

/**
 * Created by innohawk on 24/3/16.
 */
public class GcmInstanceIDActivity extends InstanceIDListenerService {

    private static final String TAG = "MyInstanceIDLS";
    private SQLiteActivity db;
    /**
     * Se llama cuando Gcm servers actualizan el registration token, principalemnte por motivos  de seguridad
     */
    @Override
    public void onTokenRefresh() {
        //obtener nuevamente el token y enviarlo a la aplicacion servidor
        RegitroGcmcAsyncTask regitroGcmcAsyncTask = new RegitroGcmcAsyncTask();
        regitroGcmcAsyncTask.execute();
    }

    private class RegitroGcmcAsyncTask extends AsyncTask<String , String, Object>
    {

        @Override
        protected Object doInBackground(String ... params) {

            try {

                publishProgress("Obteniendo Registration Token en GCM Servers...");
                String registrationToken = GcmUtilitiesActivity.ObtenerRegistrationTokenEnGcm(getApplicationContext());

                String IMEI = DameIMEI(getApplicationContext());

                // Iniciamos la BBDD y Grabamos de nuevo, actualizamos o nada.. aunque en este caso solo valdria actualizar...
                db = new SQLiteActivity(getApplicationContext());
                HashMap<String, String> gcm = db.getGCMvalues(registrationToken, IMEI);
                Integer id = Integer.valueOf(gcm.get("id"));
                String token = gcm.get("token");
                String emai = gcm.get("emai");
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date()); //Get Data today

                if(token == null && emai == null) { //Escribimos en la BBDD
                    Log.d(TAG, "Esta vacio asi que creamos nuevo");
                    db.addToken(registrationToken, IMEI, currentDateTimeString);
                    String sSendInfoServer = GcmUtilitiesActivity.RegistrarseEnAplicacionServidor(getApplicationContext(), registrationToken);
                    Log.d(TAG, "El resultado de enviar el token e IMEI:" + sSendInfoServer);
                }else if (!token.equals(registrationToken)){ //Debemos Actualizar el nuevo TOKEN
                    Log.d(TAG, "Es diferente asi que actualizamos");
                    db.updateGCM(token, Integer.valueOf(id), currentDateTimeString);
                    String sSendInfoServer = GcmUtilitiesActivity.RegistrarseEnAplicacionServidor(getApplicationContext(), registrationToken);
                    Log.d(TAG, "El resultado de enviar el token e IMEI:" + sSendInfoServer);
                }else{ // Pues si existe y no esta repetido se deja igual
                    Log.d(TAG, "Lo dejamos igual");
                }
                publishProgress("Enviando Registration a mi aplicacion servidor y Actualizando el Token...");
                String respuesta = GcmUtilitiesActivity.RegistrarseEnAplicacionServidor(getApplicationContext(), registrationToken);
                return respuesta;
            }
            catch (Exception ex){
                return ex;
            }
        }

        protected void onProgressUpdate(String... progress) {
            Toast.makeText(getApplicationContext(),progress[0], Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Object result)
        {
            if(result instanceof  String)
            {
                String resulatado = (String)result;
                Toast.makeText(getApplicationContext(), "Registro exitoso. " + resulatado, Toast.LENGTH_SHORT).show();
            }
            else if (result instanceof Exception)//Si el resultado es una Excepcion..hay error
            {
                Exception ex = (Exception) result;
                Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

    }

}
