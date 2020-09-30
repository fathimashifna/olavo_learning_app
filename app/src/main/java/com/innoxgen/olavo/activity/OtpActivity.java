package com.innoxgen.olavo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
 * Created by Fathima Shifna K on 24-08-2020.
 */
public class OtpActivity extends AppCompatActivity {

    String check,otp,txt_string1,txt_string2,txt_string3,txt_string4,txt_string5,entered_otp,name,img,mobile,userid,device_id;
    EditText txt_1,txt_2,txt_3,txt_4,txt_5;
    Button submit_otp;
    TextView mobile_no,rsnd_otp;
    KSnack kSnack;
    public static final String MY_PREFS_NAME = "LoginStatus";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_otp);
        txt_1=findViewById(R.id.pin_first_edittext);
        txt_2=findViewById(R.id.pin_second_edittext);
        txt_3=findViewById(R.id.pin_third_edittext);
        txt_4=findViewById(R.id.pin_forth_edittext);
        txt_5=findViewById(R.id.pin_fifth_edittext);
        submit_otp=findViewById(R.id.submit_otp);
        kSnack = new KSnack(OtpActivity.this);
        mobile_no=findViewById(R.id.mobile_no);
        rsnd_otp=findViewById(R.id.rsnd_otp);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        if (getIntent().getExtras() != null) {
            check=getIntent().getStringExtra("check");
            otp = getIntent().getStringExtra("OTP");
            name = getIntent().getStringExtra("name");
            img= getIntent().getStringExtra("image");
            mobile=getIntent().getStringExtra("mobile");
            userid=getIntent().getStringExtra("User_id");
            Log.e("check", "" + check);
        }
        device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String mob=mobile.substring(8,10);
        mobile_no.setText("+9XXXXXXXXX"+mob);
        txt_1.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (txt_1.getText().toString().length() == 1)     //size as per your requirement
                {
                    txt_2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
        txt_2.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (txt_2.getText().toString().length() == 1)     //size as per your requirement
                {
                    txt_3.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
        txt_3.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (txt_3.getText().toString().length() == 1)     //size as per your requirement
                {
                    txt_4.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }


            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });
        txt_4.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                if (txt_4.getText().toString().length() == 1)     //size as per your requirement
                {
                    txt_5.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
                // TODO Auto-generated method stub

            }


            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

        });


        if (isNetworkConnectionAvailable())
        {
            rsnd_otp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resendOTP(userid,device_id);
                }
            });


            submit_otp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txt_string1 = txt_1.getText().toString();
                    txt_string2 = txt_2.getText().toString();
                    txt_string3 = txt_3.getText().toString();
                    txt_string4 = txt_4.getText().toString();
                    txt_string5 = txt_5.getText().toString();
                    String x = txt_string1 + txt_string2 + txt_string3 + txt_string4 + txt_string5;
                    entered_otp = x;
                    if (entered_otp.length() == 0) {
                        kSnack.setListener(new KSnackBarEventListener() {
                            @Override
                            public void showedSnackBar() {
                                System.out.println("Showed");
                                txt_1.setFocusable(false);
                                txt_2.setFocusable(false);
                                txt_3.setFocusable(false);
                                txt_4.setFocusable(false);
                                txt_5.setFocusable(false);
                            }

                            @Override
                            public void stoppedSnackBar() {
                                txt_1.setFocusable(true);
                                txt_2.setFocusable(true);
                                txt_3.setFocusable(true);
                                txt_4.setFocusable(true);
                                txt_5.setFocusable(true);
                                txt_1.setFocusableInTouchMode(true);
                                txt_2.setFocusableInTouchMode(true);
                                txt_3.setFocusableInTouchMode(true);
                                txt_4.setFocusableInTouchMode(true);
                                txt_5.setFocusableInTouchMode(true);
                                txt_1.getText().clear();
                                txt_2.getText().clear();
                                txt_3.getText().clear();
                                txt_4.getText().clear();
                                txt_5.getText().clear();

                            }
                        })
                                .setAction("Close", new View.OnClickListener() { // name and clicklistener
                                    @Override
                                    public void onClick(View v) {
                                        // System.out.println("Your action !");
                                        kSnack.dismiss();
                                        txt_1.setFocusable(true);
                                        txt_2.setFocusable(true);
                                        txt_3.setFocusable(true);
                                        txt_4.setFocusable(true);
                                        txt_5.setFocusable(true);
                                        txt_1.setFocusableInTouchMode(true);
                                        txt_2.setFocusableInTouchMode(true);
                                        txt_3.setFocusableInTouchMode(true);
                                        txt_4.setFocusableInTouchMode(true);
                                        txt_5.setFocusableInTouchMode(true);
                                        txt_1.getText().clear();
                                        txt_2.getText().clear();
                                        txt_3.getText().clear();
                                        txt_4.getText().clear();
                                        txt_5.getText().clear();
                                    }
                                })
                                .setMessage("Please enter otp") // message
                                .setTextColor(R.color.white) // message text color
                                .setBackColor(R.color.colorPrimary) // background color
                                .setButtonTextColor(R.color.white) // action button text color
                                .setAnimation(Fade.In.getAnimation(), Fade.Out.getAnimation()) // show and hide animations
                                //.setDuration(3000) // for auto close.
                                .show();
                    } else {

                        verify(userid,entered_otp);


                    }
                }
            });
        }
        else
        {
            startActivity(new Intent(OtpActivity.this, NoNetworkActivity.class));
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
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void resendOTP(final String user_id,final String device_id) {

        String url1 = BaseClass.mainURL+"resend_otp";
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

                            progress.dismiss();
                            kSnack.setListener(new KSnackBarEventListener() {
                                @Override
                                public void showedSnackBar() {
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
                params.put("user_id", user_id);
                 params.put("device_token",device_id);
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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void verify(final String user_id,final String otp) {

        String url1 = BaseClass.mainURL+"verify_otp";
//Log.e("url",""+url1);
       /* final ProgressDialog progress = new ProgressDialog(OtpActivity.this);
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();*/
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           // progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status=jsonObject.getString("result");
                            String data=jsonObject.getString("msg");
                            Log.e("status",""+response);
                            if (status.equals("true"))
                            {
                               if (check.equals("1"))
                                {
                                    SharedPreferences sharedPref =getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPref.edit();
                                    editor.putInt("login_status", 1);
                                    editor.commit();
                                    Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                                    intent.putExtra("name", name);
                                    intent.putExtra("image", img);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity( intent);
                                    finish();
                                }
                               else if (check.equals("2"))
                               {
                                   SharedPreferences sharedPref =getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                                   SharedPreferences.Editor editor = sharedPref.edit();
                                   editor.putInt("login_status", 0 );
                                   editor.commit();
                                   Intent intent = new Intent(getApplicationContext(), PasswdResetActivity.class);
                                   intent.putExtra("userid", userid);
                                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                   startActivity( intent);
                                   finish();
                               }

                            }

                            kSnack.setListener(new KSnackBarEventListener() {
                                    @Override
                                    public void showedSnackBar() {
                                        System.out.println("Showed");
                                        txt_1.setFocusable(false);
                                        txt_2.setFocusable(false);
                                        txt_3.setFocusable(false);
                                        txt_4.setFocusable(false);
                                        txt_5.setFocusable(false);
                                    }

                                    @Override
                                    public void stoppedSnackBar() {
                                        txt_1.setFocusable(true);
                                        txt_2.setFocusable(true);
                                        txt_3.setFocusable(true);
                                        txt_4.setFocusable(true);
                                        txt_5.setFocusable(true);
                                        txt_1.setFocusableInTouchMode(true);
                                        txt_2.setFocusableInTouchMode(true);
                                        txt_3.setFocusableInTouchMode(true);
                                        txt_4.setFocusableInTouchMode(true);
                                        txt_5.setFocusableInTouchMode(true);
                                        txt_1.getText().clear();
                                        txt_2.getText().clear();
                                        txt_3.getText().clear();
                                        txt_4.getText().clear();
                                        txt_5.getText().clear();

                                    }
                                })
                                        .setAction("Close", new View.OnClickListener() { // name and clicklistener
                                            @Override
                                            public void onClick(View v) {
                                                // System.out.println("Your action !");
                                                kSnack.dismiss();
                                                txt_1.setFocusable(true);
                                                txt_2.setFocusable(true);
                                                txt_3.setFocusable(true);
                                                txt_4.setFocusable(true);
                                                txt_5.setFocusable(true);
                                                txt_1.setFocusableInTouchMode(true);
                                                txt_2.setFocusableInTouchMode(true);
                                                txt_3.setFocusableInTouchMode(true);
                                                txt_4.setFocusableInTouchMode(true);
                                                txt_5.setFocusableInTouchMode(true);
                                                txt_1.getText().clear();
                                                txt_2.getText().clear();
                                                txt_3.getText().clear();
                                                txt_4.getText().clear();
                                                txt_5.getText().clear();
                                            }
                                        })
                                        .setMessage("Invalid OTP") // message
                                        .setTextColor(R.color.white) // message text color
                                        .setBackColor(R.color.colorPrimary) // background color
                                        .setButtonTextColor(R.color.white) // action button text color
                                        .setAnimation(Fade.In.getAnimation(), Fade.Out.getAnimation()) // show and hide animations
                                        //.setDuration(3000) // for auto close.
                                        .show();




                        } catch (JSONException e) {
                            e.printStackTrace();
                          //  progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error-login_alert",""+ error.getMessage());
                   //     progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
               // params.put("device_token",device_id);
                params.put("otp",otp);
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