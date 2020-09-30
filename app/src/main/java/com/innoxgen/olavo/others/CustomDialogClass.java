package com.innoxgen.olavo.others;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.activity.Login;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fathima Shifna K on 29-08-2020.
 */
public class CustomDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public static final String MY_PREFS_NAME = "LoginStatus";
    public Activity c;
    public Dialog d;
    public Button yes, no;
    String user_id;

    public CustomDialogClass(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.logout_alert);
        yes =  findViewById(R.id.dialogButtonOK);
        no = findViewById(R.id.dialogButtonCancel);
        SharedPreferences sharedPref =getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialogButtonOK:
                Logout(user_id);
                SharedPreferences sharedPref =getContext().getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("login_status", 0);
                editor.commit();
                Intent intent = new Intent(getContext(), Login.class);
                getContext().startActivity(intent);
                c.finish();
                break;
            case R.id.dialogButtonCancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void Logout(final String userid) {

        String url1 = BaseClass.mainURL+"logout";
//Log.e("url",""+url1);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status=jsonObject.getString("result");
                            String data=jsonObject.getString("msg");
                            //Log.e("status",""+response);




                        } catch (JSONException e) {
                            e.printStackTrace();
                            //progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error-logout_alert",""+ error.getMessage());
                       // progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device_token",user_id);
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