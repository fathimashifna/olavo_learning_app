package com.innoxgen.olavo.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.fragment.NoNetworkFragment;
import com.innoxgen.olavo.others.BaseClass;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Fathima Shifna K on 23-09-2020.
 */
public class ProfileActivity extends AppCompatActivity {

    TextView tv_name,tv_mobile,tv_email,chnge_pwd,user_name;
    FloatingActionButton edit_icon;
    String user_id,device_id,name,mobile,email,img;
    CircleImageView profile_image,img_cam;
    public static final String MY_PREFS_NAME = "LoginStatus";
    ImageView iv_back;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_profile);

        tv_name=findViewById(R.id.tv_name);
        user_name=findViewById(R.id.user_name);
        tv_mobile=findViewById(R.id.tv_mobile);
        tv_email=findViewById(R.id.tv_email);
        chnge_pwd=findViewById(R.id.chnge_pwd);
        edit_icon=findViewById(R.id.edit_icon);
        profile_image=findViewById(R.id.profile_image);
        img_cam=findViewById(R.id.img_cam);
        iv_back = findViewById(R.id.iv_back);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        SharedPreferences sharedPref =getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        if (isNetworkConnectionAvailable())
        {
            getProfile(user_id,device_id);
            img_cam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, ProfileResetActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("image", img);
                    intent.putExtra("email",email);
                    intent.putExtra("userid",user_id);
                    intent.putExtra("mobile",mobile);
                    startActivity(intent);
                }
            });
            chnge_pwd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, PasswdResetActivity.class);
                    intent.putExtra("userid",user_id);
                    startActivity(intent);
                }
            });
            edit_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ProfileActivity.this, ProfileResetActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("image", img);
                    intent.putExtra("email",email);
                    intent.putExtra("userid",user_id);
                    intent.putExtra("mobile",mobile);
                    startActivity(intent);
                }
            });
        }
        else
        {
            startActivity(new Intent(ProfileActivity.this, NoNetworkActivity.class));
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
    public void getProfile(final String user_id,final String device_id) {

        String url1 = BaseClass.mainURL+"get_user_profile";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog( ProfileActivity.this);
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
                            Log.e("status",""+response);
                            progress.dismiss();
                            if (status.equals("true")) {
                                JSONObject jo = (JSONObject) jsonObject.get("data");
                                // jo = jsonArray.getJSONObject(i);
                                String id=jo.getString("id");
                                String user_id=jo.getString("user_id");
                                img=jo.getString("image");
                                name=jo.getString("name");
                                mobile=jo.getString("mobile");
                                email=jo.getString("email");
                            }
                            tv_name.setText(name);
                            user_name.setText(name);
                            tv_email.setText(email);
                            tv_mobile.setText(mobile);
                            String imgurl=BaseClass.userURL+img;
                            Picasso.get()
                                    .load(imgurl).fit().centerCrop()
                                    .into(profile_image);



                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error-helpline_alert",""+ error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device_token",device_id);
                params.put("user_id",user_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ProfileActivity.this);
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
}
