package com.makeinindia.controller;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Search.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    protected String entered_mobile_number;
    WindowManager.LayoutParams params, chatHeadParams;
    Context context;
    TextView numberText, localTextView, localoperator;
    //private RelativeLayout mIncomingPopup,mIncomingPopupMin;
    Button close;//,minimize;
    RelativeLayout searchResult_layout;
    Spinner country_code;
    DBAdapters ad;
    Button find_location;
    EditText mobile_number;
    String[] countries = {"+91(India)"};
    ImageView localImageView;
    Cursor c;
    int sHeight = 0;
    int sWeight = 0;
    View rootView;
    InterstitialAd mInterstitialAd;
    AdView adView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private WindowManager windowManager;
    private ImageView chatHead;
    private String country_code_value;
    private Typeface type;
    private OnFragmentInteractionListener mListener;

    public Search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search.
     */
    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static Search newInstance() {
        Search fragment = new Search();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        numberText = (TextView) rootView.findViewById(R.id.textView1);
        localTextView = (TextView) rootView.findViewById(R.id.textViewCallScreen);
        localoperator = (TextView) rootView.findViewById(R.id.operator);
        localImageView = (ImageView) rootView.findViewById(R.id.imageViewCallScreen);
        searchResult_layout = (RelativeLayout) rootView.findViewById(R.id.relativeLayout1);
        close = (Button) rootView.findViewById(R.id.button1);
        type = Typeface.createFromAsset(getActivity().getAssets(), "KacstDigital.ttf");
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
        context = getActivity();
        initCustomLayout(context);


        params = new WindowManager.LayoutParams(
                sWeight - 50,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.x = 0;
        params.y = (int) ((3 * sHeight) / 4.5);

        country_code = (Spinner) rootView.findViewById(R.id.country_code);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, countries);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country_code.setAdapter(dataAdapter);


		/*country_code.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(mIncomingPopup.isAttachedToWindow())
					windowManager.removeView(mIncomingPopup);
				return false;
			}
		});*/

        country_code.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                Toast.makeText(getActivity(), "You selected " + country_code.getSelectedItem().toString(), Toast.LENGTH_LONG).show();

                if (country_code.getSelectedItem().toString().equalsIgnoreCase("+91(India)")) {
                    country_code_value = "91";
                    return;
                }

                if (country_code.getSelectedItem().toString().equalsIgnoreCase("+98(Iran)")) {
                    country_code_value = "98";
                    return;
                }
                if (country_code.getSelectedItem().toString().equalsIgnoreCase("+39(Italy)")) {
                    country_code_value = "39";
                    return;
                }
                if (country_code.getSelectedItem().toString().equalsIgnoreCase("+92(Pakisthan)")) {
                    country_code_value = "92";
                    return;
                }
                if (country_code.getSelectedItem().toString().equalsIgnoreCase("+44(UK)")) {
                    country_code_value = "44";
                    return;
                }
                if (country_code.getSelectedItem().toString().equalsIgnoreCase("+57(Columbia)")) {
                    country_code_value = "57";
                    return;
                }
                if (country_code.getSelectedItem().toString().equalsIgnoreCase("+60(Malaysia)")) {
                    country_code_value = "60";
                    return;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }

        });


        find_location = ((Button) rootView.findViewById(R.id.find));
        mobile_number = ((EditText) rootView.findViewById(R.id.mobile_number));
        ad = new DBAdapters(getActivity());
        ad.createDatabase();
        try {
            ad.open();
        } catch (Exception e) {
            e.printStackTrace();
        }

        requestNewInterstitial();

		/*mobile_number.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(mIncomingPopup.isAttachedToWindow())
					windowManager.removeView(mIncomingPopup);
				return false;
			}
		});
		 */

        find_location.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                InputMethodManager inputManager = (InputMethodManager)
                        getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
                entered_mobile_number = mobile_number.getText().toString();

                if (entered_mobile_number.length() == 4) {
                    if (entered_mobile_number.charAt(0) == '0')
                        showError(arg0);
                    else {
                        getDataFromDB(entered_mobile_number);

                    }

                } else if (entered_mobile_number.length() == 10) {
                    if (entered_mobile_number.charAt(0) == '0')
                        showError(arg0);
                    else {
                        getDataFromDB(entered_mobile_number.substring(0, 4).toString());
                    }

                } else if ((entered_mobile_number.length() > 4) && (entered_mobile_number.length() < 10)) {
                    showError(arg0);
                } else if (entered_mobile_number.length() < 4) {
                    showError(arg0);
                } else if (entered_mobile_number.length() > 10) {
                    showError(arg0);
                }


            }

            private void getDataFromDB(String entered_mobile_number) {
                try {
                    c = ad.get_details(entered_mobile_number, country_code_value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Log.i(TAG, "incoming IDLE " +  "c code " + country_code + "  mob_operator " + mob_operator);
                if (c.getCount() > 0) {
                    c.moveToFirst();
                    if (c.getString(3).trim().toString().equalsIgnoreCase("AIRTEL") || c.getString(3).trim().toString().equalsIgnoreCase("Airtel")) {
                        localImageView.setImageResource(R.drawable.airtel);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("VODAFONE")) {
                        localImageView.setImageResource(R.drawable.vodafone);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("AIRCEL")) {
                        localImageView.setImageResource(R.drawable.aircel);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("RELIANCE CDMA") || c.getString(3).trim().toString().equalsIgnoreCase("RELIANCE GSM")) {
                        localImageView.setImageResource(R.drawable.rel);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("CELLONE GSM") || c.getString(3).trim().toString().equalsIgnoreCase("BSNL")) {
                        localImageView.setImageResource(R.drawable.cellone);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("LOOP MOBILE")) {
                        localImageView.setImageResource(R.drawable.loop);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("TATA INDICOM")) {
                        localImageView.setImageResource(R.drawable.datacom);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("DATACOM") || c.getString(3).trim().toString().equalsIgnoreCase("VIDEOCON")) {
                        localImageView.setImageResource(R.drawable.videocon);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("ETISALAT")) {
                        localImageView.setImageResource(R.drawable.rel);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("DOLPHIN")) {
                        localImageView.setImageResource(R.drawable.dolphin);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("RELIANCE GSM")) {
                        localImageView.setImageResource(R.drawable.rel);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("UNINOR")) {
                        localImageView.setImageResource(R.drawable.uni);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("S TEL")) {
                        localImageView.setImageResource(R.drawable.stel);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("MTS CDMA") || c.getString(3).trim().toString().equalsIgnoreCase("MTS")) {
                        localImageView.setImageResource(R.drawable.mts);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("TATA DOCOMO")) {
                        localImageView.setImageResource(R.drawable.docomo);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("CELLONE GSM")) {
                        localImageView.setImageResource(R.drawable.bsnl);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("SPICE")) {
                        localImageView.setImageResource(R.drawable.spice);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("VIRGIN")) {
                        localImageView.setImageResource(R.drawable.virgin);
                    } else if (c.getString(3).trim().toString().equalsIgnoreCase("Vodafone")) {
                        localImageView.setImageResource(R.drawable.vodafone);
                    } /*else if (c.getString(3).toString().equalsIgnoreCase("Mobilink")) {
                        localImageView.setImageResource(2130837682);
					} else if (c.getString(3).toString().equalsIgnoreCase("Zong")) {
						localImageView.setImageResource(2130837741);
					} else if (c.getString(3).toString().equalsIgnoreCase("SCOM")) {
						localImageView.setImageResource(2130837711);
					} else if (c.getString(3).toString().equalsIgnoreCase("Isle of Man")) {
						localImageView.setImageResource(2130837667);
					} else if (c.getString(3).toString().equalsIgnoreCase("Comcel")) {
						localImageView.setImageResource(2130837589);
					} else if (c.getString(3).toString().equalsIgnoreCase("DiGi")) {
						localImageView.setImageResource(2130837621);
					} else if (c.getString(3).toString().equalsIgnoreCase("RighTel")) {
						localImageView.setImageResource(2130837710);
					} else if (c.getString(3).toString().equalsIgnoreCase("MTN Irancell")) {
						localImageView.setImageResource(2130837669);
					}*/ else {
                        localImageView.setImageResource(R.drawable.landline);
                    }

                    if (numberText != null)
                        numberText.setText("Searched no. " + entered_mobile_number);
                    if (localTextView != null)
                        localTextView.setText("Circle : " + c.getString(4));
                    if (localoperator != null)
                        localoperator.setText("Operator : " + c.getString(3));

                    if (searchResult_layout != null) {
                        searchResult_layout.setVisibility(View.VISIBLE);
                    }
                    /*if(mIncomingPopup.isAttachedToWindow()) {
						windowManager.updateViewLayout(mIncomingPopup, params);
					} else {
						windowManager.addView(mIncomingPopup, params);
					}*/

                } /*else {
					localImageView.setImageResource(R.drawable.landline);
					localTextView.setText("");
					localoperator.setText("Sorry! Incorrect Operator");
				}*/


            }

            private void showError(View paramAnonymousView) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirm")
                        .setMessage("Please enter a valid mobile Number")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int) {
                                paramAnonymous2DialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });

        return rootView;
    }

    protected void initCustomLayout(Context context) {
        //Log.d(TAG, "initCustomLayout ");
		/*		LayoutInflater inflater = LayoutInflater.from(context);
		mIncomingPopup = (RelativeLayout) inflater.inflate(R.layout.search_layout, null);*/
        //mIncomingPopup.setLayoutParams(new ViewGroup.LayoutParams(50,100));

		/*	numberText = (TextView) mIncomingPopup.findViewById(R.id.textView1);
		localTextView = (TextView)mIncomingPopup.findViewById(R.id.textViewCallScreen);
		localoperator = (TextView)mIncomingPopup.findViewById(R.id.operator);
		localImageView = (ImageView)mIncomingPopup.findViewById(R.id.imageViewCallScreen);

		 */
        if (numberText != null)
            numberText.setTypeface(type);
        if (localTextView != null)
            localTextView.setTypeface(type);
        if (localoperator != null)
            localoperator.setTypeface(type);
        //close = (Button) mIncomingPopup.findViewById(R.id.button1);
        //minimize = (Button) mIncomingPopup.findViewById(R.id.Button01);

        //Log.d(TAG, "initCustomLayout : appConstant.phoneNumber :" + appConstant.phoneNumber);

        //incoming_number = appConstant.phoneNumber;

        //	Log.i(TAG, "incoming number " + incoming_number  + " appConstant.incomingFlag " + appConstant.incomingFlag);


		/*	mIncomingPopup.setOnTouchListener(new View.OnTouchListener() {
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
					Toast.makeText(getActivity(), "You Clicked here", 500).show();

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
		});*/


        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (searchResult_layout != null) {
                    searchResult_layout.setVisibility(View.GONE);
                }


            }
        });


    }

    private void requestNewInterstitial() {
        mInterstitialAd = new InterstitialAd(getActivity());
        if (mInterstitialAd != null)
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        if (mInterstitialAd != null)
            mInterstitialAd.loadAd(adRequest);

        adView = (AdView) rootView.findViewById(R.id.myAdView);
        if (adView != null)
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
                }
            });

        if (adView != null)
            adView.loadAd(adRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
