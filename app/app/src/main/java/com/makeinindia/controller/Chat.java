package com.makeinindia.controller;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Chat extends Service  {

    private static final String TAG = "ChatHead";
    static String incoming_number;
    WindowManager.LayoutParams params, chatHeadParams;
    Context context;
    TextView numberText, localTextView, localoperator;
    Button close, minimize;
    AppConstant appConstant;
    ImageView localImageView;
    DBAdapters ad;
    Cursor c;
    int sHeight = 0;
    int sWeight = 0;
    Typeface type;
    private WindowManager windowManager;
    private ImageView chatHead;
    private RelativeLayout mIncomingPopup, mIncomingPopupMin;
    private String country_code;
    private String mob_operator;
    private int x_init_cord, y_init_cord;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @Override
    public void onCreate() {
        super.onCreate();
        appConstant = new AppConstant();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        context = getApplicationContext();

        type = Typeface.createFromAsset(getAssets(), "LatoLig.otf");
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            sHeight = size.y;
            sWeight = size.x;
        } else {
            Display display = windowManager.getDefaultDisplay();
            sHeight = display.getHeight();
            sWeight = display.getWidth();
        }

        this.ad = new DBAdapters(context);
        this.ad.createDatabase();
        try {
            this.ad.open();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        initCustomLayout(context);
        chatHead = new ImageView(this);
        chatHead.setImageResource(R.drawable.phone2);

        params = new WindowManager.LayoutParams(
                sWeight - 50,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.x = 0;
        params.y = 100;

        chatHeadParams = new WindowManager.LayoutParams(
                120, 120,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        chatHeadParams.gravity = Gravity.TOP | Gravity.LEFT;
        chatHeadParams.x = sWeight;
        chatHeadParams.y = 100;

        //this code is for dragging the chat head
        chatHead.setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0, time_end = 0;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int x_cord = (int) event.getRawX();
                int y_cord = (int) event.getRawY();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        time_start = System.currentTimeMillis();
                        x_init_cord = x_cord;
                        y_init_cord = y_cord;

                        initialX = chatHeadParams.x;
                        initialY = chatHeadParams.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:

                        int x_diff = x_cord - x_init_cord;
                        int y_diff = y_cord - y_init_cord;

                        if (x_diff < 5 && y_diff < 5) {
                            time_end = System.currentTimeMillis();
                            if ((time_end - time_start) < 300) {
                                chathead_click();
                            }
                        }
                     return true;
                    case MotionEvent.ACTION_MOVE:
                        chatHeadParams.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        chatHeadParams.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(chatHead, chatHeadParams);
                        return true;
                }
                return false;
            }

        });

        //this code is for dragging the chat head
        mIncomingPopup.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX
                                + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY
                                + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(mIncomingPopup, params);
                        return true;
                }
                return false;
            }
        });
        windowManager.addView(mIncomingPopup, params);
    }

    private void chathead_click() throws RuntimeException {
        if (windowManager != null) {
            windowManager.removeView(chatHead);

            windowManager.addView(mIncomingPopup, params);
            if (mIncomingPopup != null)
                mIncomingPopup.setVisibility(View.VISIBLE);
        }
    }

    boolean isNumeric(String paramString) {
        int j = paramString.length();
        int i = 0;
        for (; ; ) {
            if (i >= j) {
                return true;
            }
            if ((paramString.charAt(i) < '0') || (paramString.charAt(i) > '9')) {
                return false;
            }
            i += 1;
        }
    }

    protected void initCustomLayout(Context context) {
        Log.d(TAG, "initCustomLayout ");
        LayoutInflater inflater = LayoutInflater.from(context);
        mIncomingPopup = (RelativeLayout) inflater.inflate(R.layout.service_layout, null);
        //mIncomingPopup.setLayoutParams(new ViewGroup.LayoutParams(50,100));

        numberText = (TextView) mIncomingPopup.findViewById(R.id.textView1);
        localTextView = (TextView) mIncomingPopup.findViewById(R.id.textViewCallScreen);
        localoperator = (TextView) mIncomingPopup.findViewById(R.id.operator);
        localImageView = (ImageView) mIncomingPopup.findViewById(R.id.imageViewCallScreen);


        if (numberText != null)
            numberText.setTypeface(type);
        if (localTextView != null)
            localTextView.setTypeface(type);
        if (localoperator != null)
            localoperator.setTypeface(type);
        close = (Button) mIncomingPopup.findViewById(R.id.button1);
        minimize = (Button) mIncomingPopup.findViewById(R.id.Button01);

        //Log.d(TAG, "initCustomLayout : appConstant.phoneNumber :" + AppConstant.phoneNumber);

        if (numberText != null)
            numberText.setText(AppConstant.phoneNumber);
        incoming_number = AppConstant.phoneNumber;

        //Log.i(TAG, "incoming number " + incoming_number + " appConstant.incomingFlag " + AppConstant.incomingFlag);

        if (AppConstant.incomingFlag) {
            if (incoming_number != null) {
                country_code = incoming_number.substring(1, 3);
                mob_operator = incoming_number.substring(3, 7);

            }
        } else {
            if (incoming_number != null) {
                switch (incoming_number.length()) {
                    case 10:
                        country_code = "91";
                        mob_operator = incoming_number.substring(0, 4);
                        break;

                    case 11:
                        country_code = "91";
                        mob_operator = incoming_number.substring(1, 5);
                        break;

                    case 13:
                        country_code = incoming_number.substring(1, 3);
                        mob_operator = incoming_number.substring(3, 7);
                        break;

                    case 14:
                        incoming_number.charAt(1);
                        break;

                    default:
                        break;
                }
            }
        }
        try {
            this.c = this.ad.get_details(mob_operator, country_code);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //Log.i(TAG, "incoming IDLE " + "c code " + country_code + "  mob_operator " + mob_operator);
        if (this.c != null && this.c.getCount() > 0) {
            this.c.moveToFirst();
            String string = this.c.getString(3).trim();
            if (string.equalsIgnoreCase("AIRTEL") || string.equalsIgnoreCase("Airtel")) {
                this.localImageView.setImageResource(R.drawable.airtel);
            } else if (string.equalsIgnoreCase("VODAFONE")) {
                this.localImageView.setImageResource(R.drawable.vodafone);
            } else if (string.equalsIgnoreCase("AIRCEL")) {
                this.localImageView.setImageResource(R.drawable.aircel);
            } else if (string.equalsIgnoreCase("RELIANCE CDMA") || string.equalsIgnoreCase("RELIANCE GSM")) {
                this.localImageView.setImageResource(R.drawable.rel);
            } else if (string.equalsIgnoreCase("CELLONE GSM") || string.equalsIgnoreCase("BSNL")) {
                this.localImageView.setImageResource(R.drawable.cellone);
            } else if (string.equalsIgnoreCase("LOOP MOBILE")) {
                this.localImageView.setImageResource(R.drawable.loop);
            } else if (string.equalsIgnoreCase("TATA INDICOM")) {
                this.localImageView.setImageResource(R.drawable.datacom);
            } else if (string.equalsIgnoreCase("DATACOM") || string.equalsIgnoreCase("VIDEOCON")) {
                this.localImageView.setImageResource(R.drawable.videocon);
            } else if (string.equalsIgnoreCase("ETISALAT")) {
                this.localImageView.setImageResource(R.drawable.rel);
            } else if (string.equalsIgnoreCase("DOLPHIN")) {
                this.localImageView.setImageResource(R.drawable.dolphin);
            } else if (string.equalsIgnoreCase("RELIANCE GSM")) {
                this.localImageView.setImageResource(R.drawable.rel);
            } else if (string.equalsIgnoreCase("UNINOR")) {
                this.localImageView.setImageResource(R.drawable.uni);
            } else if (string.equalsIgnoreCase("S TEL")) {
                this.localImageView.setImageResource(R.drawable.stel);
            } else if (string.equalsIgnoreCase("MTS CDMA") || string.equalsIgnoreCase("MTS")) {
                this.localImageView.setImageResource(R.drawable.mts);
            } else if (string.equalsIgnoreCase("TATA DOCOMO")) {
                this.localImageView.setImageResource(R.drawable.docomo);
            } else if (string.equalsIgnoreCase("CELLONE GSM")) {
                this.localImageView.setImageResource(R.drawable.bsnl);
            } else if (string.equalsIgnoreCase("SPICE")) {
                this.localImageView.setImageResource(R.drawable.spice);
            } else if (string.equalsIgnoreCase("VIRGIN")) {
                this.localImageView.setImageResource(R.drawable.virgin);
            } else if (string.equalsIgnoreCase("Vodafone")) {
                this.localImageView.setImageResource(R.drawable.vodafone);
            } else {
                this.localImageView.setImageResource(R.drawable.landline);
            }
            if (localTextView != null)
                localTextView.setText("Circle : " + c.getString(4));
            if (localoperator != null)
                localoperator.setText("Operator : " + c.getString(3));
        }
        close.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getApplicationContext().stopService(new Intent(getApplicationContext(), Chat.class));

            }
        });
        minimize.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                    if (mIncomingPopup.getVisibility() == 0) {
                    mIncomingPopup.setVisibility(View.INVISIBLE);
                    if (windowManager != null)
                        windowManager.removeView(mIncomingPopup);
                    windowManager.addView(chatHead, chatHeadParams);
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        //Log.i("TAG", "Hellow world");
        super.onDestroy();

        //Log.i("TAG", "incoming visibility" + mIncomingPopup.getVisibility());
        //Log.i("TAG", "chathead visibility" + chatHead.getVisibility());
        if (mIncomingPopup != null || chatHead != null) {
            if (mIncomingPopup.getVisibility() == 0) {
                if (windowManager != null)
                    windowManager.removeView(mIncomingPopup);
            } else if (chatHead.getVisibility() == 0) {
                chatHead.setVisibility(View.GONE);
                if (windowManager != null)
                    windowManager.removeView(chatHead);
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}