package com.innoxgen.olavo.others;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.innoxgen.olavo.R;
import com.innoxgen.olavo.activity.PaymentActivity;

/**
 * Created by Fathima Shifna K on 07-09-2020.
 */
public class CustomDialogClass_upgrade extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button yes, no;
    public  String course_id;

    public CustomDialogClass_upgrade(Activity a ,String course_id) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.course_id=course_id;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.subscription_alert_msg);
        yes = findViewById(R.id.btn_upgrade_now);
        no =  findViewById(R.id.btn_cancel);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upgrade_now:
               c.startActivity(new Intent(c, PaymentActivity.class).putExtra("course_id",course_id));
              // c.finish();
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }
}