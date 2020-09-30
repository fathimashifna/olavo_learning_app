package com.innoxgen.olavo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
 * Created by Fathima Shifna K on 30-08-2020.
 */
public class PasswdResetActivity extends AppCompatActivity {
    EditText txt_pass, txt_confrmpass, txt_oldpass;
    Button btn_submit;
    String old_pass, passwd, confrm_pass, user_id;
    KSnack kSnack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_reset_password);
        kSnack = new KSnack(PasswdResetActivity.this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        txt_pass = findViewById(R.id.txt_pass);
        txt_confrmpass = findViewById(R.id.txt_confirmpass);
        btn_submit = findViewById(R.id.submit);
        if (getIntent().getExtras() != null) {
            user_id = getIntent().getStringExtra("userid");
        }

        if (isNetworkConnectionAvailable()) {
            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // old_pass = txt_oldpass.getText().toString();
                    passwd = txt_pass.getText().toString();
                    confrm_pass = txt_confrmpass.getText().toString();
                    if (passwd.length() == 0) {
                        txt_pass.setError("Please enter your password");
                        txt_pass.requestFocus();
                    } else if (confrm_pass.length() == 0) {
                        txt_confrmpass.setError("Please enter your password");
                        txt_confrmpass.requestFocus();
                    } else {
                        editPass(user_id, passwd, confrm_pass);
                    }
                }
            });
        } else {
            startActivity(new Intent(PasswdResetActivity.this, NoNetworkActivity.class));
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
        finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void editPass(final String user_id, final String new_pass, final String confrm_pass) {

        String url1 = BaseClass.mainURL + "reset_user_password";
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
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                            //Log.e("status",""+response);
                            txt_pass.getText().clear();
//                            txt_oldpass.getText().clear();
                            txt_confrmpass.getText().clear();
                            if (status.equals("true")) {
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(btn_submit.getWindowToken(), 0);
                                kSnack.setListener(new KSnackBarEventListener() {
                                    @Override
                                    public void showedSnackBar() {
                                        progress.dismiss();
                                        System.out.println("Showed");

                                    }

                                    @Override
                                    public void stoppedSnackBar() {

                                    }
                                })
                                        .setAction("Close", new View.OnClickListener() { // name and clicklistener
                                            @Override
                                            public void onClick(View v) {
                                                // System.out.println("Your action !");
                                                kSnack.dismiss();
                                                Intent intent = new Intent(getApplicationContext(), Login.class);
                                                // intent.putExtra("userid", userid);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                                // finish();


                                            }
                                        })
                                        .setMessage("Profile updated Successfully") // message
                                        .setTextColor(R.color.white) // message text color
                                        .setBackColor(R.color.colorPrimary) // background color
                                        .setButtonTextColor(R.color.white) // action button text color
                                        .setAnimation(Fade.In.getAnimation(), Fade.Out.getAnimation()) // show and hide animations
                                        //.setDuration(3000) // for auto close.
                                        .show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error-profilereset_alert", "" + error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_id", user_id);
                // params.put("old_password", old_pass);
                params.put("new_password", new_pass);
                params.put("confirm_password", confrm_pass);
                // params.put("image","");

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
