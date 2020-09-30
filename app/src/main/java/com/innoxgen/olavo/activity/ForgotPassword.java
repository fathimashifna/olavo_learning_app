package com.innoxgen.olavo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.R;
import com.onurkagan.ksnack_lib.Animations.Fade;
import com.onurkagan.ksnack_lib.KSnack.KSnack;
import com.onurkagan.ksnack_lib.KSnack.KSnackBarEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fathima Shifna K on 21-08-2020.
 */
public class ForgotPassword extends AppCompatActivity {
    KSnack kSnack;
    public static final String MY_PREFS_NAME = "LoginStatus";
    String mobile_no;
    EditText txt_mobile;
    Button bt_submit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_forget_password);
        kSnack = new KSnack(ForgotPassword.this);
        txt_mobile=findViewById(R.id.et_phnno);
        bt_submit=findViewById(R.id.bt_submit);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }

        if (isNetworkConnectionAvailable())
        {
            bt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mobile_no=txt_mobile.getText().toString();
                    verify(mobile_no);
                }
            });
        }
        else
        {
            startActivity(new Intent(ForgotPassword.this, NoNetworkActivity.class));
            finish();
        }
    }
    boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity( intent);
        finish();
       // finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void verify(final String mobile_no) {

        String url1 = BaseClass.mainURL+"forgot_password";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String status=jsonObject.getString("result");
                            String data=jsonObject.getString("msg");
                           // Log.e("status",""+response);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(bt_submit.getWindowToken(), 0);
                            if (status.equals("true"))
                            {

                                JSONObject jo = (JSONObject) jsonObject.get("data");
                                // jo = jsonArray.getJSONObject(i);
                                String id = jo.getString("id");
                                String user_id = jo.getString("user_id");
                                String img = jo.getString("image");
                                String name = jo.getString("name");
                                String mobile = jo.getString("mobile");
                              //  String email = jo.getString("email");
                              //  String password = jo.getString("password");
                                String otp = jo.getString("otp");
                            //    String create_date = jo.getString("create_date");
                                Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
                                intent.putExtra("check", "2");
                                intent.putExtra("OTP", otp);
                                intent.putExtra("name", name);
                                intent.putExtra("image", img);
                                intent.putExtra("mobile", mobile);
                                intent.putExtra("User_id", id);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity( intent);
                                finish();
                            }
                            progress.dismiss();
                            kSnack.setListener(new KSnackBarEventListener() {
                                @Override
                                public void showedSnackBar() {
                                    System.out.println("Showed");
                                    txt_mobile.setFocusable(false);

                                }

                                @Override
                                public void stoppedSnackBar() {
                                    txt_mobile.setFocusable(true);
                                    txt_mobile.setFocusableInTouchMode(true);
                                    txt_mobile.getText().clear();


                                }
                            })
                                    .setAction("Close", new View.OnClickListener() { // name and clicklistener
                                        @Override
                                        public void onClick(View v) {
                                            // System.out.println("Your action !");
                                            kSnack.dismiss();
                                            txt_mobile.setFocusable(true);
                                            txt_mobile.setFocusableInTouchMode(true);
                                            txt_mobile.getText().clear();

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
                        Log.e("Error-frgt_alert",""+ error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile_no);
                // params.put("device_token",device_id);
              //  params.put("otp",otp);
                //params.put("referral_code",referal_code);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
}
