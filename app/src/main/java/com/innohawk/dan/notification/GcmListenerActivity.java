package com.innohawk.dan.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.innohawk.dan.Main;
import com.innohawk.dan.R;
import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by innohawk on 24/3/16.
 */

public class GcmListenerActivity extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Cuando el mensage es recibido.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        String title = data.getString("title");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        /**
         * El  mensaje recibido se proceso en este metodo.
         * Ejemplo: - Sincronizar con servidor.
         *     - Almacenar mensajes en base de datos local.
         *     - Actualizar UI.
         *     - Mostrar nofificaciones
         */

        //En este caso mostraremos una notificacion
        this.MostrarNotification(message,title);
    }

    private void MostrarNotification(String message, String title) {
        Intent intent = new Intent(this, Main.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int idNotificacion = 1;
        notificationManager.notify(1, notificationBuilder.build());
    }
}
