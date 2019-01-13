package com.makeinindia.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {


        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
         /* if (isConnected)  {     
              Log.i("NET", "connected" + isConnected);
              intent = new Intent(context , AdMobService.class);  
              context.startService(intent);
              
              Calendar cal = Calendar.getInstance();
              PendingIntent pintent = PendingIntent.getService(context, 0, intent, 0);
   
              AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
              // Start service every 20 seconds
              alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                      20* 1000, pintent);
          }  else Log.i("NET", "not connected" + isConnected);
        }*/
    }
}