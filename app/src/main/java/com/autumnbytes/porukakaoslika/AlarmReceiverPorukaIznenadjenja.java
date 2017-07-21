package com.autumnbytes.porukakaoslika;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

/**
 * Created by Simona Tošić on 02-Mar-17.
 */

public class AlarmReceiverPorukaIznenadjenja extends BroadcastReceiver {

    @Override
    public void onReceive (Context context, Intent intent) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder (context);
        builder
                .setSmallIcon(R.drawable.ic_notify_p)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setContentTitle("Poruka kao slika")
                .setContentText("Stigla je poruka iznenađenja")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true);

        intent = new Intent(context, NotificationFullscreenActivity.class);
        intent.putExtra("porukaIznenadjenja", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_ONE_SHOT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        NotificationManagerCompat.from(context).notify (0, notification);

        TaskStackBuilder tsb = TaskStackBuilder.create(context);
        tsb.addParentStack(NotificationFullscreenActivity.class);
    }
}