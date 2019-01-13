package com.makeinindia.controller;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;

public class PhoneStateReceiver extends BroadcastReceiver {

    private static final String TAG = "PhoneStatReceiver";
    public static boolean incomingFlag = false;
    public static String incoming_number = null;
    public static String outgoing_number = null;
    static Toast toast;
    private static Context context = null;
    Intent serviceIntent;
    AppConstant appConstant;
    DBAdapters ad;
    Cursor cursor;
    LinearLayout localObject2;
    TextView localTextView, localoperator;
    ImageView localImageView;
    SharedPreferences sp;
    private String country_code;

    private String mob_operator;

    @Override

    public void onReceive(Context context, Intent intent) {
        appConstant = new AppConstant();
        PhoneStateReceiver.context = context;
        this.sp = context.getSharedPreferences("call_setings", 0);


        this.ad = new DBAdapters(context);
        this.ad.createDatabase();
        try {
            this.ad.open();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            //this.sp = this.context.getSharedPreferences("call_setings", 0);
            outgoing_number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);


            incomingFlag = false;
            AppConstant.incomingFlag = incomingFlag;
            AppConstant.phoneNumber = outgoing_number;
            serviceIntent = new Intent(context, Chat.class);
            if (this.sp.getBoolean("out_call_value", true) == true && outgoing_number != null) {

                if (outgoing_number.startsWith("*") || outgoing_number.startsWith("#")) {

                } else if (outgoing_number.length() == 10) {
                    Log.i(TAG, " chat at 0 " + outgoing_number.charAt(0));
                    switch (outgoing_number.charAt(0)) {

                        case '9':

                        case '8':

                        case '7':
                            context.startService(serviceIntent);
                            break;
                        default:
                            break;
                    }

                } else if (outgoing_number.length() == 11) {

                    if (outgoing_number.startsWith("0")) {
                        switch (outgoing_number.charAt(1)) {

                            case '9':
                            case '8':
                            case '7':
                                context.startService(serviceIntent);
                                break;
                            default:
                        }
                    }
                } else if (outgoing_number.length() == 13) {
                    Log.i(TAG, " chat of  sub" + outgoing_number.substring(0, 3));
                    if (outgoing_number.substring(0, 3).equals("+91")) {
                        Log.i(TAG, " chat  " + outgoing_number.charAt(3));
                        switch (outgoing_number.charAt(3)) {

                            case '9':
                            case '8':
                            case '7':
                                context.startService(serviceIntent);
                                break;
                            default:
                        }
                    }

                }
            }
            incomingFlag = false;
            AppConstant.incomingFlag = incomingFlag;


            Log.i(TAG, "call OUT:" + outgoing_number + " Length " + outgoing_number.length());

        } else {

            TelephonyManager tm =

                    (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

            switch (tm.getCallState()) {

                case TelephonyManager.CALL_STATE_RINGING:
                    this.sp = PhoneStateReceiver.context.getSharedPreferences("call_setings", 0);
                    incomingFlag = true;
                    AppConstant.incomingFlag = incomingFlag;
                    incoming_number = intent.getStringExtra("incoming_number");

                    Log.i(TAG, "RINGING :" + incoming_number + ".sp.getBoolean" + this.sp.getBoolean("in_call_value", false));

                    AppConstant.phoneNumber = incoming_number;


                    if (this.sp.getBoolean("in_call_value", true) == true && incoming_number != null) {
                        serviceIntent = new Intent(context, Chat.class);

                        //serviceIntent.putExtra("number", incoming_number);
                        if (incoming_number.length() == 13) {

                            if (incoming_number.substring(0, 3).equals("+91")) {
                                switch (incoming_number.charAt(3)) {

                                    case '9':
                                    case '8':
                                    case '7':
                                        context.startService(serviceIntent);
                                        break;
                                    default:
                                }
                            }

                        }
                    }
                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK:
                    this.sp = PhoneStateReceiver.context.getSharedPreferences("call_setings", 0);
                    //context.stopService(new Intent(context, ChatHeadService.class));

                    if (incomingFlag) {

                        Log.i(TAG, "incoming ACCEPT :" + incoming_number);

                    }

                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    this.sp = PhoneStateReceiver.context.getSharedPreferences("call_setings", 0);
                    context.stopService(new Intent(context, Chat.class));

                    if (incomingFlag) {

                        Log.i(TAG, "incoming IDLE");

                    }

                    break;

            }


        }

    }

    boolean isNumeric(String paramString) {
        int j = paramString.length();
        Log.i(TAG, "isNumeric length" + j);
        int i = 0;

        if (paramString.startsWith("*") || paramString.startsWith("#")) {
            return false;
        }
        for (; ; ) {
            /*'0' - 48
			    '1' - 49
			    '2' - 50
			    '3' - 51
			    '4' - 52
			    '5' - 53
			    '6' - 54
			    '7' - 55
			    '8' - 56
			    '9' - 57*/

            if (i >= j) {
                return true;
            }
            Log.i(TAG, "isNumeric length" + paramString.charAt(i));
            if ((paramString.charAt(i) < '0') || (paramString.charAt(i) > '9')) {

                return false;
            }
            i += 1;
        }
    }

}