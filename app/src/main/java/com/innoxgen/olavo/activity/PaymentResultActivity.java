package com.innoxgen.olavo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.others.BaseClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PaymentResultActivity extends AppCompatActivity {

    public static final String MY_PREFS_NAME = "LoginStatus";
    String user_id,device_id,course_id,plan_id,transaction_ref_no,order_no,amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);
        TextView okbtn=findViewById(R.id.txt_ok);
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (getIntent().getExtras()!=null)
        {
            course_id=  getIntent().getStringExtra("course_id");
            plan_id=  getIntent().getStringExtra("plan_id");
            transaction_ref_no=  getIntent().getStringExtra("BANKTXNID");
            order_no=  getIntent().getStringExtra("ORDERID");
            amount=  getIntent().getStringExtra("TXNAMOUNT");
        }

        okbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlan(user_id,device_id,course_id,plan_id,transaction_ref_no,order_no,amount);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void addPlan(final String user_id, final String device_id,  final String course_id,final String plan_id,final String transaction_ref_no,final String order_no,final String amount) {

        String url1 = BaseClass.mainURL + "user_take_plan";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(PaymentResultActivity.this);
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                            Log.e("status",""+response);
                            JSONObject jo = null;

                            if (status.equals("true")) {
                                Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                                startActivity(intent);
                                finish();

                            } else {
                                Toast.makeText(PaymentResultActivity.this, data, Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.dismiss();
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error-1", "" + error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                Log.e("user_id",""+user_id);
                params.put("device_token", device_id);
                Log.e("device_token",""+device_id);
                params.put("course_id", course_id);
                Log.e("course_id",""+course_id);
                params.put("plan_id", plan_id);
                Log.e("plan_id",""+plan_id);
                params.put("transaction_ref_no", transaction_ref_no);
                Log.e("transaction_ref_no",""+transaction_ref_no);
                params.put("order_no", order_no);
                Log.e("order_no",""+order_no);
                params.put("amount", amount);
                Log.e("amount",""+amount);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

}