package bankingservice;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.makeinindia.controller.R;

import java.util.ArrayList;
import java.util.List;

import bankingservice.adapter.BankListAdapter;
import bankingservice.database.DBAdapters;
import bankingservice.model.BankModel;

public class BankFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Resources res;
    String[] values;
    View rootView;
    private String mParam1;
    private String mParam2;
    DBAdapters dbAdapters;
    Cursor cursor;
    Context mContext;
    private GridView gridView;
    private List<BankModel> bankModelList;
    private OnFragmentInteractionListener mListener;
    InterstitialAd mInterstitialAd;
    AdView adView;

    public BankFragment() {
        // Required empty public constructor
    }

    public static BankFragment newInstance() {
        BankFragment fragment = new BankFragment();
        return fragment;
    }

    public static BankFragment newInstance(String param1, String param2) {
        BankFragment fragment = new BankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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
        rootView = container;
        // Inflate the layout for this fragment
        try {
            rootView = inflater.inflate(R.layout.bank_list_fragment, container, false);
        } catch (InflateException e) {
            e.printStackTrace();
        }

        requestNewInterstitial();

        mContext = getActivity();
        bankModelList = new ArrayList<BankModel>();
        this.dbAdapters = new DBAdapters(mContext);
        this.dbAdapters.createDatabase();
        try {
            this.dbAdapters.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.cursor = this.dbAdapters.get_all();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (this.cursor != null && this.cursor.getCount() > 0) {
            int length = this.cursor.getCount();
            System.out.println("Length " + length);
            this.cursor.moveToFirst();
            while (length > 0) {
                String id = this.cursor.getString(0).trim();
                String name = this.cursor.getString(1).trim();
                String inquiry = this.cursor.getString(2).trim();
                String care = this.cursor.getString(3).trim();
                String img = this.cursor.getString(4).trim();
                //Log.d("Values are ", id + " " + name + " " + inquiry + " " + care + " " + img);
                BankModel bankModel = new BankModel(id, name, inquiry, care, img);
                length--;
                bankModelList.add(bankModel);

                this.cursor.moveToNext();
            }

        }

        gridView = (GridView) rootView.findViewById(R.id.gridView);

        //res = getResources();
        //values = res.getStringArray(R.array.bank_list_array);
        gridView.setAdapter(new BankListAdapter(getActivity(), bankModelList));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(getActivity(), CheckBankBalance.class);
                in.putExtra("pos", position);
                in.putExtra("enquiry", bankModelList.get(position).getBank_inquiry());
                in.putExtra("customer", bankModelList.get(position).getBank_care());
                in.putExtra("image", bankModelList.get(position).getBank_img());
                in.putExtra("bankName", bankModelList.get(position).getBank_name());
                //Log.d("val is at 1 " , bankModelList.get(position).getBank_care());
                startActivity(in);
            }
        });

        return rootView;
    }

    private void requestNewInterstitial() {

        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (rootView != null) {
            ViewGroup parentViewGroup = (ViewGroup) rootView.getParent();
            if (parentViewGroup != null) {
                Log.d("TAG", "destroy on device");
                parentViewGroup.removeAllViews();
            }
        }


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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}


