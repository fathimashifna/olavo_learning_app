package com.innoxgen.olavo.nav_item;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.activity.ActivationActivity;
import com.innoxgen.olavo.activity.NoNetworkActivity;
import com.innoxgen.olavo.activity.PaymentActivity;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.model.MembershipModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PurchaseHistoryActivity extends AppCompatActivity {

    MembershipModel membershipModel;
    RecyclerView recyclerView;
    String user_id, device_id;
    private List<MembershipModel> membershipModelList = new ArrayList<>();
    public static final String MY_PREFS_NAME = "LoginStatus";
    RecyclerviewAdapter recyclerviewAdapter;
    Date date_start, date_end;
    String str_date, end_date;
    ImageView iv_back;
    LinearLayout linear_layout2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_purchasehistory);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        recyclerView = findViewById(R.id.recycleview_purchase);
        iv_back = findViewById(R.id.iv_back);
        linear_layout2=findViewById(R.id.linear_layout2);
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
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
        linear_layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PurchaseHistoryActivity.this, ActivationActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            }
        });

        if (isNetworkConnectionAvailable()) {
            getcourseList(user_id, device_id);
        } else {
            startActivity(new Intent(PurchaseHistoryActivity.this, NoNetworkActivity.class));
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
    public void getcourseList(final String user_id, final String device_id) {

        String url1 = BaseClass.mainURL + "get_course_plan";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(PurchaseHistoryActivity.this);
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        membershipModelList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                            // Log.e("status",""+response);
                            JSONObject jo = null;
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (status.equals("true")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String id = jo.getString("id");
                                    String course_id = jo.getString("course_id");
                                    String mrp = jo.getString("mrp");
                                    String price = jo.getString("price");
                                    String desc = jo.getString("description");
                                    String class_name = jo.getString("class_name");
                                    String startdate = jo.getString("start_date");
                                    String enddate = jo.getString("end_date");
                                    // Log.e("Date",""+startdate);

                                    membershipModel = new MembershipModel(course_id, class_name, desc, mrp, price, startdate, enddate);
                                    membershipModelList.add(membershipModel);
                                }
                                recyclerviewAdapter = new RecyclerviewAdapter(PurchaseHistoryActivity.this, membershipModelList, user_id, device_id);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(recyclerviewAdapter);

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
                        Log.e("Error-mem_alert", "" + error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device_token", device_id);
                params.put("user_id", user_id);
               // Log.e("user_id", "" + user_id);
               // Log.e("device_token", "" + device_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(PurchaseHistoryActivity.this);
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

        private List<MembershipModel> modelList;
        private Context context;
        String user_id, device_id;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txt_desc, txt_class_name, txt_mrp, txt_price, txt_start, txt_end, view_details;
            LinearLayout lin1;


            public MyViewHolder(View view) {
                super(view);
                txt_desc = view.findViewById(R.id.tv_description);
                txt_class_name = view.findViewById(R.id.class_name);
                txt_mrp = view.findViewById(R.id.tv_mrp);
                txt_price = view.findViewById(R.id.tv_price);
                view_details = view.findViewById(R.id.view_details);

            }
        }


        public RecyclerviewAdapter(Context context, List<MembershipModel> modelList, String user_id, String device_id) {
            this.context = context;
            this.modelList = modelList;
            this.user_id = user_id;
            this.device_id = device_id;
        }

        @Override
        public RecyclerviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_membership_adapter, parent, false);
            // itemView.setOnClickListener(mOnClickListener);
            return new RecyclerviewAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerviewAdapter.MyViewHolder holder, final int position) {
            MembershipModel membershipModel = modelList.get(position);
            holder.txt_desc.setText(membershipModel.getDesc());
            holder.txt_class_name.setText(membershipModel.getClass_name());
            holder.txt_mrp.setText("\u20B9" + membershipModel.getMrp() + ".00");
            holder.txt_price.setText("\u20B9" + membershipModel.getPrice() + ".00");
            holder.txt_mrp.setPaintFlags(holder.txt_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String course_id = membershipModel.getCourse_id();
            holder.view_details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    checksubscrptn(user_id, device_id, course_id);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (modelList != null) {
                return modelList.size();
            } else {
                return 0;
            }
            //  return productList.size();
        }

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void checksubscrptn(final String user_id, final String device_id, final String course_id) {

        String url1 = BaseClass.mainURL + "check_user_plan";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(PurchaseHistoryActivity.this);
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            //Log.e("status", "" + response);
                            if (status.equals("true")) {
                                Toast.makeText(PurchaseHistoryActivity.this, "Already subscribed", Toast.LENGTH_SHORT).show();
                            } else {

                                startActivity(new Intent(PurchaseHistoryActivity.this, PaymentActivity.class).putExtra("course_id", course_id)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                            // Toast.makeText(SubjectActivity.this, data, Toast.LENGTH_SHORT).show();


                        } catch (JSONException e) {
                            e.printStackTrace();
                            progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
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
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}