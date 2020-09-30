package com.innoxgen.olavo.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.innoxgen.olavo.R;

/**
 * Created by Fathima Shifna K on 21-08-2020.
 */
public class SplashActivity extends AppCompatActivity {
    Integer status;
    String name,img;
    public static final String MY_PREFS_NAME = "LoginStatus";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        }
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }
        SharedPreferences sharedPref =getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
         status = sharedPref.getInt("login_status", 0);
         name=sharedPref.getString("Name","test");
         img=sharedPref.getString("Img","img");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(status==1)
                {
                   if(isNetworkConnectionAvailable())
                   {
                       Intent intent = new Intent(getApplicationContext(), NavigationActivity.class);
                       intent.putExtra("name", name);
                       intent.putExtra("image", img);
                       startActivity(intent);
                       finish();
                   }
                   else
                   {
                       Intent intent = new Intent(getApplicationContext(), NoNetworkActivity.class);
                       startActivity(intent);
                       finish();
                   }
                }
                else
                {
                    startActivity(new Intent(SplashActivity.this, Login.class));
                    finish();
                }


            }
        }, 3000);
    }
    boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
}
