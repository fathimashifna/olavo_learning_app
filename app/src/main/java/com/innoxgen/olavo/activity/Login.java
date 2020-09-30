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
public class Login extends AppCompatActivity {
    TextView forget_pwd;
    Button signInButton,signup;
     KSnack kSnack;
     EditText txt_email,txt_pwd;
     String str_email,str_pwd,device_id;
    String refreshedToken;
    public static final String MY_PREFS_NAME = "LoginStatus";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        signup=findViewById(R.id.signup);
        forget_pwd=findViewById(R.id.forget_pwd);
        signInButton=findViewById(R.id.signInButton);
        txt_email=findViewById(R.id.email);
        txt_pwd=findViewById(R.id.passwordEditText);
        kSnack = new KSnack(Login.this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( Login.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
               refreshedToken = instanceIdResult.getToken();
            Log.e("newToken",refreshedToken);

            }
        });
        device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
                //finish();
            }
        });

        forget_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, ForgotPassword.class));
            }
        });

        if (isNetworkConnectionAvailable())
        {
            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    str_email=txt_email.getText().toString();
                    str_pwd=txt_pwd.getText().toString();

                    if (str_email.length() == 0) {
                        txt_email.setError("Please enter your email or mobile no");
                        txt_email.requestFocus();
                    } else if (str_pwd.length() == 0) {
                        txt_pwd.setError("Please enter your password");
                        txt_pwd.requestFocus();
                    } /*else if (password.length() < 6) {
                    editText_pass.setError("Password having minimum 6 characters");
                    editText_pass.requestFocus();
                } */ else {

                        LoginData(str_email,str_pwd,refreshedToken,device_id);

                    }

                }
            });
        }
        else
        {
            startActivity(new Intent(Login.this, NoNetworkActivity.class));
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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void LoginData(final String username, final String passwd, final String fcm_id, final String device_id) {

        String url1 = BaseClass.mainURL+"login";
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
                            //Log.e("status",""+response);
                            if (status.equals("true"))
                            {

                                JSONObject jo = (JSONObject) jsonObject.get("data");
                                // jo = jsonArray.getJSONObject(i);
                                String id=jo.getString("id");
                                String user_id=jo.getString("user_id");
                                String img=jo.getString("image");
                                String name=jo.getString("name");
                                String mobile=jo.getString("mobile");
                                String email=jo.getString("email");
                                String password=jo.getString("password");
                                //String otp=jo.getString("otp");
                                String create_date=jo.getString("create_date");
                                String login_status=jo.getString("status");

                                SharedPreferences sharedPref =getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("login_status", 1);
                                editor.putString("Name",name);
                                editor.putString("Img",img);
                                editor.putString("User_id",id);
                                editor.commit();
                                Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("image", img);
                                //intent.putExtra("user_id",user_id);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
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
                        Log.e("Error-login_alert",""+ error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                  params.put("mobile_email", username);
                  params.put("password",passwd);
                  params.put("device_token",device_id);
                  params.put("fcm_id",fcm_id);
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
