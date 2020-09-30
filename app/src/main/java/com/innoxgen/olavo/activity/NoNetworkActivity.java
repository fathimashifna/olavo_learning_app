package com.innoxgen.olavo.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.innoxgen.olavo.R;

public class NoNetworkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_no_internet);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);




        TextView no_conection=findViewById(R.id.txt_no_connection);
       // no_conection.setTypeface(txt2);
        TextView check=findViewById(R.id.txt_1);
       // check.setTypeface(txt2);
        TextView txt_try=(TextView)findViewById(R.id.txt_try);
        //txt_try.setTypeface(txt2);

        LinearLayout layout_try=findViewById(R.id.layout_try);
        layout_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);

                startActivity(new Intent(NoNetworkActivity.this,NavigationActivity.class));
                finish();
            }
        });

    }
}
