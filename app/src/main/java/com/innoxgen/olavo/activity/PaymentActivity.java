package com.innoxgen.olavo.activity;

import androidx.appcompat.app.AppCompatActivity;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.R;
import com.onurkagan.ksnack_lib.KSnack.KSnack;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PaymentActivity extends AppCompatActivity {
    Button online_payment;
    private String TAG = "PaymentActivity";
    private ProgressBar progressBar;
    private TextView tv_amount, txnAmount, tv_order_no, tv_plan, tv_date, help;
    String price, course_name, user_id, device_id, course_id, plan_id, transaction_ref_no, order_no, amount;
    private String midString = "spFAVj55374237789746", txnAmountString = "", orderIdString = "", txnTokenString = "";
    private Button btn_cancel;
    private Integer ActivityRequestCode = 2;
    ImageView iv_back;
    KSnack kSnack;


    public static final String MY_PREFS_NAME = "LoginStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        online_payment = findViewById(R.id.btn_submit);
        tv_amount = findViewById(R.id.tv_amount);
        tv_order_no = findViewById(R.id.tv_order_no);
        tv_plan = findViewById(R.id.tv_plan);
        tv_date = findViewById(R.id.tv_date);
        txnAmount = findViewById(R.id.tv_total);
        iv_back = findViewById(R.id.iv_back);
        btn_cancel = findViewById(R.id.btn_cancel);
        help = findViewById(R.id.help);
        kSnack = new KSnack(PaymentActivity.this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        long date1 = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = sdf.format(date1);
        tv_date.setText(dateString);

        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (getIntent().getExtras() != null) {
            course_id = getIntent().getStringExtra("course_id");
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
        String date = df.format(c.getTime());
        Random rand = new Random();
        int min = 1000, max = 9999;
// nextInt as provided by Random is exclusive of the top value so you need to add 1
        int randomNum = rand.nextInt((max - min) + 1) + min;
        orderIdString = date + String.valueOf(randomNum);
        tv_order_no.setText(orderIdString);

        help.setText("Your Order id is " + orderIdString + ". " + "Please contact us with this ID in case you run into any problems.");

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        if (isNetworkConnectionAvailable()) {
            getcourseList(user_id, device_id, course_id);

            online_payment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  /*  if (ContextCompat.checkSelfPermission(PaymentActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(PaymentActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                    }*/

                    txnAmountString = price;//txnAmount.getText().toString();
                    String errors = "";
                    if (orderIdString.equalsIgnoreCase("")) {
                        errors = "Enter valid Order ID here\n";
                        Toast.makeText(PaymentActivity.this, errors, Toast.LENGTH_SHORT).show();

                    } else {

                        //getToken(orderIdString);
                        getTxnTocken(orderIdString,txnAmountString);
                    }
                }
            });

        } else {
            startActivity(new Intent(PaymentActivity.this, NoNetworkActivity.class));
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
    public void getcourseList(final String user_id, final String device_id, final String course_id) {

        String url1 = BaseClass.mainURL + "get_coursebased_plan";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(PaymentActivity.this);
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
                            //  Log.e("status",""+response);
                            JSONObject jo = null;

                            if (status.equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    plan_id = jo.getString("id");
                                    price = jo.getString("price");
                                    course_name = jo.getString("class_name");


                                }
                                tv_amount.setText("\u20B9" + price);
                                txnAmount.setText("\u20B9" + price);
                                tv_plan.setText(course_name);
                            } else {
                                Toast.makeText(PaymentActivity.this, data, Toast.LENGTH_SHORT).show();
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
                params.put("device_token", device_id);
                params.put("user_id", user_id);
                params.put("course_id", course_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getTxnTocken(final String order_id, final String amount) {

        String url1 = "https://olavo.in/app/Checksum-master/init_Transaction.php";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(PaymentActivity.this);
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
                           // String status = jsonObject.getString("result");
                          //  String data = jsonObject.getString("body");
                            // Log.e("status",""+response);
                            //Toast.makeText(PaymentActivity.this,""+response, Toast.LENGTH_SHORT).show();
                            JSONObject jsonObject1=jsonObject.getJSONObject("body");
                            String txnToken=jsonObject1.getString("txnToken");
                         //   Toast.makeText(PaymentActivity.this,""+txnToken, Toast.LENGTH_SHORT).show();
                            startPaytmPayment(txnToken);
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
                params.put("orderid", order_id);
                params.put("amount", amount);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }


    public void startPaytmPayment(String token) {
        Log.e("tocken",""+token);
        txnTokenString = token;
        // for test mode use it
        String host = "https://securegw-stage.paytm.in/";
        // for production mode use it
        //String host = "https://securegw.paytm.in/";
        String orderDetails = "MID: " + midString + ", OrderId: " + orderIdString + ", TxnToken: " + txnTokenString
                + ", Amount: " + txnAmountString;
        Log.e(TAG, "order details " + orderDetails);

        String callBackUrl = host + "theia/paytmCallback?ORDER_ID=" + orderIdString;
        // String callBackUrl = host + "order/status";
        //Log.e(TAG, " callback URL " + callBackUrl);
        PaytmOrder paytmOrder = new PaytmOrder(orderIdString, midString, txnTokenString, txnAmountString, callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(Bundle bundle) {
                //  Log.e(TAG, "Response (onTransactionResponse) : " + bundle.toString());
                transaction_ref_no = bundle.getString("BANKTXNID");
                order_no = bundle.getString("ORDERID");
                amount = bundle.getString("TXNAMOUNT");
                String resp_msg = bundle.getString("RESPMSG");
                if (resp_msg.equals("Txn Success")) {
                    Intent intent = new Intent(PaymentActivity.this, PaymentResultActivity.class);
                    intent.putExtra("course_id", course_id);
                    intent.putExtra("plan_id", plan_id);
                    intent.putExtra("BANKTXNID", transaction_ref_no);
                    intent.putExtra("ORDERID", order_no);
                    intent.putExtra("TXNAMOUNT", amount);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PaymentActivity.this, PaymentFailedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }


            }

            @Override
            public void networkNotAvailable() {
                Log.e(TAG, "network not available ");
                Toast.makeText(PaymentActivity.this, "network not available ", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onErrorProceed(String s) {
                Log.e(TAG, " onErrorProcess " + s.toString());
                Toast.makeText(PaymentActivity.this, " onErrorProcess " + s, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void clientAuthenticationFailed(String s) {
                Log.e(TAG, "Clientauth " + s);
                Toast.makeText(PaymentActivity.this, "Clientauth " + s, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                Log.e(TAG, " UI error " + s);
                Toast.makeText(PaymentActivity.this, " UI error " + s, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Log.e(TAG, " error loading web " + s + "--" + s1);
                Toast.makeText(PaymentActivity.this, " error loading web " + s + "--" + s1, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onBackPressedCancelTransaction() {
                Log.e(TAG, "backPress ");

            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Log.e(TAG, " transaction cancel " + s);
                Toast.makeText(PaymentActivity.this, " transaction cancel " + s, Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, ActivityRequestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Log.e(TAG, " result code " + resultCode);
        // -1 means successful  // 0 means failed
        // one error is - nativeSdkForMerchantMessage : networkError
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityRequestCode && data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    Log.e(TAG, key + " : " + (bundle.get(key) != null ? bundle.get(key) : "NULL"));
                }
            }
            Log.e(TAG, " data " + data.getStringExtra("nativeSdkForMerchantMessage"));
            Log.e(TAG, " data response  " + data.getStringExtra("response"));


            /*Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage")
                    + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();*/
        } else {
            Log.e(TAG, " payment failed");
            Toast.makeText(this, " payment failed", Toast.LENGTH_SHORT).show();
        }
    }


}