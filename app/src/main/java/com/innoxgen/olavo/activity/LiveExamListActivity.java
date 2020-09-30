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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.model.ExamModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LiveExamListActivity extends AppCompatActivity {
    RecyclerView recycleview_live;
    String course_id, user_id, device_id;
    private ArrayList<ExamModel> examdata = new ArrayList<>();
    public static final String MY_PREFS_NAME = "LoginStatus";
    ImageView iv_back;
    String status, data, attmpt_status = "0";
    TextView txt_nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_exam_list);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        recycleview_live = findViewById(R.id.recycleview_live);
        iv_back = findViewById(R.id.iv_back);
        txt_nodata=findViewById(R.id.txt_nodata);

        if (getIntent().getExtras() != null) {
            course_id = getIntent().getStringExtra("course_id");
        }
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);


        //Date c = Calendar.getInstance().getTime();

        if (isNetworkConnectionAvailable()) {
            getliveexmList(user_id, device_id, course_id);
        } else {
            startActivity(new Intent(LiveExamListActivity.this, NoNetworkActivity.class));
            finish();
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

    }

    boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getliveexmList(final String user_id, final String device_id, final String course_id) {

        String url1 = BaseClass.mainURL + "get_live_exam_list";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(LiveExamListActivity.this);
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        examdata.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                           // Log.e("status", "" + response);
                            JSONObject jo = null;
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (status.equals("true")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String qstn_id = jo.getString("id");
                                    String question_type = jo.getString("question_type");
                                    String topic = jo.getString("topic");
                                    String no_of_question = jo.getString("no_of_question");
                                    String time_slot = jo.getString("time_slot");
                                    String start_date = jo.getString("start_date");
                                    String start_time = jo.getString("start_time");
                                    String format_dateTime = jo.getString("format_dateTime");

                                    ExamModel examModel = new ExamModel(qstn_id, topic, no_of_question, time_slot, start_date, start_time, format_dateTime);
                                    examdata.add(examModel);
                                }
                                RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(getApplicationContext(), examdata);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                recycleview_live.setLayoutManager(mLayoutManager);
                                recycleview_live.setItemAnimator(new DefaultItemAnimator());
                                recyclerviewAdapter.notifyDataSetChanged();
                                recycleview_live.setAdapter(recyclerviewAdapter);

                            }
                            else
                            {
                                txt_nodata.setVisibility(View.VISIBLE);
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


    /////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

        private List<ExamModel> exmList;
        String encodedurl;
        long eventTime;
        private Context context;
        static final long ONE_MINUTE_IN_MILLIS = 60000;
        Boolean targetInZone = false;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView tv_topic, exam_start, duration;
            public Button start_now;
            //public ImageView image_view;
            public LinearLayout linear_layout;

            public MyViewHolder(View view) {
                super(view);
                tv_topic = view.findViewById(R.id.tv_topic);
                exam_start = view.findViewById(R.id.exam_start);
                duration = view.findViewById(R.id.duration);
                //    image_view = view.findViewById(R.id.banner_img);
                start_now = view.findViewById(R.id.btn_start_now);

            }
        }


        public RecyclerviewAdapter(Context context, List<ExamModel> exmList) {
            this.context = context;
            this.exmList = exmList;
        }

        @Override
        public RecyclerviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_live_exam_list, parent, false);

            return new RecyclerviewAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerviewAdapter.MyViewHolder holder, int position) {
            ExamModel examModel = exmList.get(position);
            holder.tv_topic.setText(examModel.getTopic());
            holder.exam_start.setText((examModel.getStart_date() + " at " + examModel.getStart_time()));
            holder.duration.setText(examModel.getTime_slot());


            String start_time = examModel.getFormat_dateTime();
            DateFormat df = new SimpleDateFormat("HH:mm:ss");
            String substr1 = start_time.substring(11, 19);
            //Log.e("Start time", ""+substr1);
            Date date1 = null;
            try {
                date1 = df.parse(substr1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long t = date1.getTime();
            //Log.e("End time1",""+t);
            int min = Integer.parseInt(examModel.getTime_slot());
            Date newMins = new Date(t + (min * ONE_MINUTE_IN_MILLIS));

            String endtime = String.valueOf(newMins).substring(11, 19);
            Date currentTime = Calendar.getInstance().getTime();
            String currentTimeString = String.valueOf(currentTime);
            String newtime = currentTimeString.substring(11, 19);
           String start_date = examModel.getStart_date();
            SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currntDate = df1.format(currentTime);
            LocalTime target = LocalTime.parse(newtime);
            targetInZone = (
                    target.isAfter(LocalTime.parse(substr1))
                            &&
                            target.isBefore(LocalTime.parse(endtime))
            );

            if (start_date.equals(currntDate)) {
                if (targetInZone) {
                    holder.start_now.setBackgroundResource(R.drawable.submitbackground);

                }
            }


            holder.start_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (start_date.equals(currntDate)) {

                        if (targetInZone) {
                            String id = examModel.getQstn_id();
                            //Log.e("id",""+id);
                            String user_id1 = user_id;
                            String device_id1 = device_id;
                            String no_of_qstn=examModel.getNo_of_qstn();
                            String timeslot=examModel.getTime_slot();
                            checkattend(user_id1, device_id1, id,no_of_qstn,timeslot);

                        } else {

                            Toast.makeText(getApplicationContext(), "Please wait for next  Exam!!!", Toast.LENGTH_SHORT).show();


                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please wait!!!", Toast.LENGTH_SHORT).show();

                    }

//


                }
            });
        }

        @Override
        public int getItemCount() {
            if (exmList != null) {
                return exmList.size();
            } else {
                return 0;
            }
            //  return productList.size();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void checkattend(final String user_id, final String device_id, final String live_exam_id,final String no_of_qstn,final String timeslot) {

        String url1 = BaseClass.mainURL + "check_attempt_live_exam";
       // Log.e("url", "" + url1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //    progress.dismiss();
                        //Log.e("response", "" + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            if (status.equals("true")) {
                                attmpt_status = jsonObject.getString("attempt_status");
                                if (attmpt_status.equals("0")) {

                                    Intent intent = new Intent(LiveExamListActivity.this, LiveExamActivity.class);
                                    intent.putExtra("live_exam_id", live_exam_id);
                                    intent.putExtra("no_of_qstn", no_of_qstn);
                                    intent.putExtra("duration", timeslot);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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
                params.put("user_id", user_id);
                // Log.e("user_id",""+user_id);
                params.put("device_token", device_id);
                //  Log.e("device_token",""+device_id);
                params.put("live_exam_id", live_exam_id);
                // Log.e("live_exam_id",""+live_exam_id);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

}