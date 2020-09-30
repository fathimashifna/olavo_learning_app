package com.innoxgen.olavo.btm_nav;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.fragment.NoNetworkFragment;
import com.innoxgen.olavo.others.BaseClass;
import com.onurkagan.ksnack_lib.Animations.Fade;
import com.onurkagan.ksnack_lib.KSnack.KSnack;
import com.onurkagan.ksnack_lib.KSnack.KSnackBarEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class HelpLineFragment extends Fragment {
    EditText et_phone, et_query;
    String phone_no, query, user_id, device_id;
    KSnack kSnack;
    public static final String MY_PREFS_NAME = "LoginStatus";
    Button submit;
    // private HelpLineFragmentViewModel helpLineFragmentViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qanda, container, false);
        getActivity().setTitle("HelpLine");
        et_phone = root.findViewById(R.id.et_phone);
        et_query = root.findViewById(R.id.et_query);
        kSnack = new KSnack((Activity) getContext());
        submit = root.findViewById(R.id.submit);
        SharedPreferences sharedPref = getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        if (isNetworkConnectionAvailable()) {
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    phone_no = et_phone.getText().toString();
                    query = et_query.getText().toString();
                    if (phone_no.length() == 0) {
                        et_phone.setError("Please enter your phone no");
                        et_phone.requestFocus();
                    } else if (query.length() == 0) {
                        et_query.setError("Please enter your question");
                        et_query.requestFocus();
                    } else {
                        submitqry(user_id, phone_no, query, device_id);
                    }

                }
            });

        } else {
            Fragment fragment = new NoNetworkFragment();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, fragment);
            ft.addToBackStack(null);
            getActivity().getSupportFragmentManager().popBackStack();
            ft.commit();
        }

        return root;
    }

    boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void submitqry(final String user_id, final String cntct_no, final String qry, final String device_id) {

        String url1 = BaseClass.mainURL + "helpline";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog((Activity) getContext());
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                            //Log.e("status",""+response);

                            progress.dismiss();
                            kSnack.setListener(new KSnackBarEventListener() {
                                @Override
                                public void showedSnackBar() {
                                    System.out.println("Showed");
                                    et_phone.setFocusable(false);
                                    et_query.setFocusable(false);
                                }

                                @Override
                                public void stoppedSnackBar() {
                                    et_phone.setFocusable(true);
                                    et_phone.setFocusableInTouchMode(true);
                                    et_query.setFocusable(true);
                                    et_query.setFocusableInTouchMode(true);
                                    //System.out.println("Stopped");

                                }
                            })
                                    .setAction("Close", new View.OnClickListener() { // name and clicklistener
                                        @Override
                                        public void onClick(View v) {
                                            // System.out.println("Your action !");
                                            kSnack.dismiss();
                                            et_phone.setFocusable(true);
                                            et_phone.setFocusableInTouchMode(true);
                                            et_query.setFocusable(true);
                                            et_query.setFocusableInTouchMode(true);
                                            et_query.getText().clear();
                                            et_phone.getText().clear();
                                        }
                                    })
                                    .setMessage(data) // message
                                    .setTextColor(R.color.white) // message text color
                                    .setBackColor(R.color.colorPrimary) // background color
                                    .setButtonTextColor(R.color.white) // action button text color
                                    .setAnimation(Fade.In.getAnimation(), Fade.Out.getAnimation()) // show and hide animations
                                    //.setDuration(3000) // for auto close.
                                    .show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error-helpline_alert", "" + error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("contact_no", cntct_no);
                params.put("query", qry);
                params.put("device_token", device_id);
                params.put("user_id", user_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
}