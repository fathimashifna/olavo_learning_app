package com.innoxgen.olavo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.model.ChatModel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String user_id, device_id, doubt_ques_id, qstn;
    private ArrayList<ChatModel> chatData = new ArrayList<>();
    public static final String MY_PREFS_NAME = "LoginStatus";
    RecyclerviewAdapter recyclerviewAdapter;
    EditText text_send;
    TextView title;
    ImageButton btn_send;
    String send_msg;
    ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        recyclerView = findViewById(R.id.recycler_view_chat);
        title = findViewById(R.id.tv_header_chat);
        text_send=findViewById(R.id.text_send);
        btn_send=findViewById(R.id.btn_send);
        iv_back=findViewById(R.id.iv_back);


        if (getIntent().getExtras() != null) {
            doubt_ques_id = getIntent().getStringExtra("qstn_id");
            qstn = getIntent().getStringExtra("dbt_qstn");
        }
        title.setText(qstn);
        //Log.e("qstn_id", "" + doubt_ques_id);
        //Log.e("dbt_qstn", "" + qstn);
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
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
            getchatList(user_id, device_id, doubt_ques_id);

            btn_send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send_msg=text_send.getText().toString();
                    sendMSg(user_id,device_id,doubt_ques_id,send_msg);
                }
            });
        }
        else
        {
            startActivity(new Intent(ChatActivity.this, NoNetworkActivity.class));
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
    public void getchatList(final String user_id, final String device_id, final String qstn_id) {

        String url1 = BaseClass.mainURL + "get_doubt_chat";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(ChatActivity.this);
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        chatData.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                            Log.e("status", "" + response);
                            JSONObject jo = null;

                            if (status.equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String id = jo.getString("id");
                                    String doubt_ques_id = jo.getString("doubt_ques_id");
                                    String user_type = jo.getString("user_type");
                                    String msg = jo.getString("msg");
                                    String date = jo.getString("date");
                                    String time = jo.getString("time");
                                    String file=jo.getString("file");

                                    ChatModel chatModel = new ChatModel(id, doubt_ques_id, msg, user_type, date, time,file);
                                    chatData.add(chatModel);
                                }
                                recyclerviewAdapter = new RecyclerviewAdapter(getApplicationContext(), chatData);
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
                        Log.e("Error-1", "" + error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device_token", device_id);
                params.put("user_id", user_id);
                params.put("doubt_ques_id", qstn_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
    /////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void sendMSg(final String user_id, final String device_id, final String qstn_id,final String msg) {

        String url1 = BaseClass.mainURL + "doubt_chat_to_admin";

        chatData.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                         //   progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                            Log.e("status", "" + response);
                            JSONObject jo = null;

                            if (status.equals("true")) {
                                getchatList(user_id, device_id, qstn_id);
                                text_send.getText().clear();


                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                          //  progress.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error-1", "" + error.getMessage());
                       // progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device_token", device_id);
                params.put("user_id", user_id);
                params.put("doubt_ques_id", qstn_id);
                params.put("u_msg",msg);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

        private List<ChatModel> chatList;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView admin_msg, admin_time, customer_msg, customer_time,txt_date,img_time;
            ImageView customer_img;
            //  public ImageView image_view;
            public RelativeLayout rel_ad_msg, rel_cstm_msg,img_lin;

            public MyViewHolder(View view) {
                super(view);
                admin_msg = view.findViewById(R.id.admin_msg);
                admin_time = view.findViewById(R.id.admin_time);
                customer_msg = view.findViewById(R.id.customer_msg);
                customer_time = view.findViewById(R.id.my_time);
                rel_ad_msg = view.findViewById(R.id.rel_ad_msg);
                rel_cstm_msg = view.findViewById(R.id.rel_cstm_msg);
                txt_date = view.findViewById(R.id.txt_date);
                customer_img=view.findViewById(R.id.customer_img);
                img_time=view.findViewById(R.id.img_time);
                img_lin=view.findViewById(R.id.img_lin);

            }
        }


        public RecyclerviewAdapter(Context context, List<ChatModel> chatList) {
            this.context = context;
            this.chatList = chatList;
        }

        @Override
        public RecyclerviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat, parent, false);

            return new RecyclerviewAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerviewAdapter.MyViewHolder holder, int position) {
            ChatModel chatModel = chatList.get(position);
            String msg = chatModel.getMsg();
            String time = chatModel.getUr_time();
            String user_type = chatModel.getUser_type();
            String date=chatModel.getUr_date();
            holder.txt_date.setText(date);
            if (user_type.equals("user"))
            {
                holder.customer_time.setText(time);
                holder.customer_msg.setText(msg);
                holder.rel_ad_msg.setVisibility(View.GONE);
                String img=chatModel.getFile();
                Log.e("img",""+img);
                if(!img.equals(""))
                {
                    holder.img_lin.setVisibility(View.VISIBLE);
                    String imgurl=BaseClass.dbt_url+img;
                    holder.img_time.setText(chatModel.getUr_time());
                    Picasso.get()
                            .load(imgurl).fit().centerCrop()
                            .into(holder.customer_img);
                }
                else
                {
                    holder.img_lin.setVisibility(View.GONE);
                }

            }
            else
            {
                holder.admin_msg.setText(msg);
                holder.admin_time.setText(time);
                holder.rel_cstm_msg.setVisibility(View.GONE);
            }

        }

        @Override
        public int getItemCount() {
            if (chatList != null) {
                return chatList.size();
            } else {
                return 0;
            }
            //  return productList.size();
        }
    }

}