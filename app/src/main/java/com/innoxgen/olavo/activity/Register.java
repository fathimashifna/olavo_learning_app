package com.innoxgen.olavo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
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
public class Register extends AppCompatActivity {
    TextView login;
    EditText txt_name, txt_email, txt_phnno, txt_password, txt_confirm_pwd, txt_referal_code;
    String name, email, phno, pwd, cfm_pwd, referal_code, device_id, refreshedToken;
    Button signin;
    KSnack kSnack;

    public static final String MY_PREFS_NAME = "LoginStatus";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_registration);
        login = findViewById(R.id.Login);
        txt_name = findViewById(R.id.et_name);
        txt_email = findViewById(R.id.et_email);
        txt_phnno = findViewById(R.id.edit_number);
        txt_password = findViewById(R.id.passwordEditText);
        txt_confirm_pwd = findViewById(R.id.confirmpasswordEditText);
        txt_referal_code = findViewById(R.id.referal_code);
        signin = findViewById(R.id.signInButton);
        kSnack = new KSnack(Register.this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity( intent);
                finish();
            }
        });
        device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        // Log.e("device_id",""+device_id);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(Register.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                refreshedToken = instanceIdResult.getToken();
                //

            }
        });

        if (isNetworkConnectionAvailable())
        {
            signin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    name = txt_name.getText().toString();
                    email = txt_email.getText().toString();
                    phno = txt_phnno.getText().toString();
                    pwd = txt_password.getText().toString();
                    cfm_pwd = txt_confirm_pwd.getText().toString();
                    referal_code = txt_referal_code.getText().toString();
                    if (name.length() == 0) {
                        txt_name.setError("Please enter your name");
                        txt_name.requestFocus();
                    } else if (email.length() == 0) {
                        txt_email.setError("Please enter your email");
                        txt_email.requestFocus();
                    } else if (phno.length() == 0) {
                        txt_phnno.setError("Please enter your phone number");
                        txt_phnno.requestFocus();
                    } else if (pwd.length() == 0) {
                        txt_password.setError("Please enter your password");
                        txt_password.requestFocus();
                    }
                /*else if (password.length() < 6) {
                    editText_pass.setError("Password having minimum 6 characters");
                    editText_pass.requestFocus();
                } */
                    else {
                        if (isValidEmail(email)) {
                            if (!TextUtils.isEmpty(pwd) && !TextUtils.isEmpty(cfm_pwd)) {
                                if (pwd.equals(cfm_pwd)) {
                                    if (referal_code.length() == 0) {
                                        signin(name, email, pwd, phno, device_id, refreshedToken, "");
                                    } else {
                                        signin(name, email, pwd, phno, device_id, refreshedToken, referal_code);
                                    }
                                } else {
                                    //are different
                                    kSnack.setListener(new KSnackBarEventListener() {
                                        @Override
                                        public void showedSnackBar() {
                                            System.out.println("Showed");
                                            txt_password.setFocusable(false);
                                            txt_confirm_pwd.setFocusable(false);
                                        }

                                        @Override
                                        public void stoppedSnackBar() {
                                            txt_password.setFocusable(true);
                                            txt_password.setFocusableInTouchMode(true);
                                            txt_confirm_pwd.setFocusable(true);
                                            txt_confirm_pwd.setFocusableInTouchMode(true);
                                            txt_password.getText().clear();
                                            txt_confirm_pwd.getText().clear();
                                        }
                                    })
                                            .setAction("Close", new View.OnClickListener() { // name and clicklistener
                                                @Override
                                                public void onClick(View v) {
                                                    // System.out.println("Your action !");
                                                    kSnack.dismiss();
                                                    txt_password.getText().clear();
                                                    txt_confirm_pwd.getText().clear();
                                                    txt_password.setFocusable(true);
                                                    txt_password.setFocusableInTouchMode(true);
                                                    txt_confirm_pwd.setFocusable(true);
                                                    txt_confirm_pwd.setFocusableInTouchMode(true);
                                                }
                                            })
                                            .setMessage("Password Mismatch, Please check") // message
                                            .setTextColor(R.color.white) // message text color
                                            .setBackColor(R.color.colorPrimary) // background color
                                            .setButtonTextColor(R.color.white) // action button text color
                                            .setAnimation(Fade.In.getAnimation(), Fade.Out.getAnimation()) // show and hide animations
                                            //.setDuration(3000) // for auto close.
                                            .show();
                                }
                            }
                        }
                        else
                        {
                            kSnack.setListener(new KSnackBarEventListener() {
                                @Override
                                public void showedSnackBar() {
                                    System.out.println("Showed");
                                    txt_password.setFocusable(false);
                                    txt_confirm_pwd.setFocusable(false);
                                }

                                @Override
                                public void stoppedSnackBar() {
                                    txt_password.setFocusable(true);
                                    txt_password.setFocusableInTouchMode(true);
                                    txt_confirm_pwd.setFocusable(true);
                                    txt_confirm_pwd.setFocusableInTouchMode(true);
                                    txt_password.getText().clear();
                                    txt_confirm_pwd.getText().clear();
                                }
                            })
                                    .setAction("Close", new View.OnClickListener() { // name and clicklistener
                                        @Override
                                        public void onClick(View v) {
                                            // System.out.println("Your action !");
                                            kSnack.dismiss();
                                            txt_password.getText().clear();
                                            txt_confirm_pwd.getText().clear();
                                            txt_password.setFocusable(true);
                                            txt_password.setFocusableInTouchMode(true);
                                            txt_confirm_pwd.setFocusable(true);
                                            txt_confirm_pwd.setFocusableInTouchMode(true);
                                        }
                                    })
                                    .setMessage("Please enter valid email id") // message
                                    .setTextColor(R.color.white) // message text color
                                    .setBackColor(R.color.colorPrimary) // background color
                                    .setButtonTextColor(R.color.white) // action button text color
                                    .setAnimation(Fade.In.getAnimation(), Fade.Out.getAnimation()) // show and hide animations
                                    //.setDuration(3000) // for auto close.
                                    .show();
                        }
                    }


                }
            });
        }
        else
        {
            startActivity(new Intent(Register.this, NoNetworkActivity.class));
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

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
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
    public void signin(final String username, final String email, final String passwd, final String phnno, final
    String device_id, final String fcm_id, final String referal_code) {

        String url1 = BaseClass.mainURL + "user_signup";
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
                            Log.e("status", "" + response);
                            if (status.equals("true")) {
                                JSONObject jo = (JSONObject) jsonObject.get("data");
                                // jo = jsonArray.getJSONObject(i);
                                String id = jo.getString("id");
                                String user_id = jo.getString("user_id");
                                String img = jo.getString("image");
                                String name = jo.getString("name");
                                String mobile = jo.getString("mobile");
                                String email = jo.getString("email");
                                String password = jo.getString("password");
                                String otp = jo.getString("otp");
                                String create_date = jo.getString("create_date");

                                Log.e("OTP", "" + otp);
                                // progress.dismiss();
                                SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("login_status", 1);
                                editor.putString("Name", name);
                                editor.putString("Img", img);
                                editor.putString("User_id", id);
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
                                intent.putExtra("check", "1");
                                intent.putExtra("OTP", otp);
                                intent.putExtra("name", name);
                                intent.putExtra("image", img);
                                intent.putExtra("mobile", mobile);
                                intent.putExtra("User_id", id);
                                startActivity(intent);
                                finish();

                            }

                            progress.dismiss();
                            kSnack.setListener(new KSnackBarEventListener() {
                                @Override
                                public void showedSnackBar() {
                                    System.out.println("Showed");
                                    txt_password.setFocusable(false);
                                    txt_confirm_pwd.setFocusable(false);
                                    txt_name.setFocusable(false);
                                    txt_email.setFocusable(false);
                                    txt_phnno.setFocusable(false);
                                    txt_referal_code.setFocusable(false);
                                }

                                @Override
                                public void stoppedSnackBar() {
                                    txt_password.setFocusable(true);
                                    txt_confirm_pwd.setFocusable(true);
                                    txt_name.setFocusable(true);
                                    txt_email.setFocusable(true);
                                    txt_phnno.setFocusable(true);
                                    txt_referal_code.setFocusable(true);
                                    txt_password.setFocusableInTouchMode(true);
                                    txt_confirm_pwd.setFocusableInTouchMode(true);
                                    txt_name.setFocusableInTouchMode(true);
                                    txt_email.setFocusableInTouchMode(true);
                                    txt_phnno.setFocusableInTouchMode(true);
                                    txt_referal_code.setFocusableInTouchMode(true);
                                }
                            })
                                    .setAction("Close", new View.OnClickListener() { // name and clicklistener
                                        @Override
                                        public void onClick(View v) {
                                            // System.out.println("Your action !");
                                            kSnack.dismiss();
                                            txt_password.setFocusable(true);
                                            txt_confirm_pwd.setFocusable(true);
                                            txt_name.setFocusable(true);
                                            txt_email.setFocusable(true);
                                            txt_phnno.setFocusable(true);
                                            txt_referal_code.setFocusable(true);
                                            txt_password.setFocusableInTouchMode(true);
                                            txt_confirm_pwd.setFocusableInTouchMode(true);
                                            txt_name.setFocusableInTouchMode(true);
                                            txt_email.setFocusableInTouchMode(true);
                                            txt_phnno.setFocusableInTouchMode(true);
                                            txt_referal_code.setFocusableInTouchMode(true);
                                           /* txt_password.getText().clear();
                                            txt_confirm_pwd.getText().clear();
                                            txt_name.getText().clear();
                                            txt_email.getText().clear();
                                            txt_phnno.getText().clear();
                                            txt_referal_code.getText().clear();*/
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
                        Log.e("Error-login_alert", "" + error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", username);
                params.put("mobile", phnno);
                params.put("email", email);
                params.put("password", passwd);
                params.put("device_token", device_id);
                params.put("fcm_id", fcm_id);
                params.put("referral_code", referal_code);
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
