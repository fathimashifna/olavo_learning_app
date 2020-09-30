package com.innoxgen.olavo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LiveExamActivity extends AppCompatActivity {
    //RecyclerView recyclerview;
    String exam_id, user_id, device_id, title, no_of_qstn, duration;
    RecyclerView recyclerView;
    public static final String MY_PREFS_NAME = "LoginStatus";
    private ArrayList<ExamModel> examdata = new ArrayList<>();
    private ArrayList<ExamModel> answerdata = new ArrayList<>();
    public static TextView btnsubmit;
    RecyclerviewAdapter recyclerviewAdapter;
    TextView total_no_question, tv_time_slot, current_date, title_txt;
    long elapsed_time;
    String answer, id, jsonStr;
    ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_exam);
   getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        recyclerView=findViewById(R.id.recycleview_liveexam);
        total_no_question = findViewById(R.id.total_no_question);
        tv_time_slot = findViewById(R.id.tv_time_slot);
        current_date = findViewById(R.id.current_date);
        title_txt = findViewById(R.id.tv_header_comment);
        btnsubmit = findViewById(R.id.submit);
        iv_back=findViewById(R.id.iv_back);

        if (getIntent().getExtras() != null) {
            exam_id = getIntent().getStringExtra("live_exam_id");
           // title = getIntent().getStringExtra("exam_title");
            no_of_qstn = getIntent().getStringExtra("no_of_qstn");
            duration = getIntent().getStringExtra("duration");


        }
      //  Log.e("live_exam_id",""+ exam_id);
        total_no_question.setText(no_of_qstn);
      //  title_txt.setText(title);
        tv_time_slot.setText(duration);
        long date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy");
        String dateString = sdf.format(date);
        current_date.setText(dateString);
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        if (isNetworkConnectionAvailable())
        {
            getexmList(user_id, device_id, exam_id);
            String time = duration + ":00"; //mm:ss
            long min = Integer.parseInt(time.substring(0, 2));
            long sec = Integer.parseInt(time.substring(3));
            long t = (min * 60L) + sec;
            long result = TimeUnit.SECONDS.toMillis(t);
            //  Log.e("duration", "" + result);
            CountDownTimer countDownTimer = new CountDownTimer(result, 1000) { // adjust the milli seconds here

                public void onTick(long millisUntilFinished) {
                    tv_time_slot.setText("" + String.format("%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                    elapsed_time = result - millisUntilFinished;
                }

                public void onFinish() {
                    tv_time_slot.setText("00:00");
                }
            };

            countDownTimer.start();

            btnsubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    countDownTimer.cancel();
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(elapsed_time);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(elapsed_time);
                    String time_taken = minutes + ":" + seconds;
                    // Log.e("Elapsed time",time_taken);
                    String message = "";
                    if (answerdata.size() == 0) {
                        countDownTimer.start();
                        Toast.makeText(getApplicationContext(), "Please choose from given options", Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject object = new JSONObject();
                        JSONArray jsonArray = new JSONArray();
                        JSONObject jsonObject = new JSONObject();
                        // get the value of selected answers from custom adapter
                        for (int i = 0; i < answerdata.size(); i++) {
                            answer = answerdata.get(i).getAnswer();
                            id = answerdata.get(i).getId();
                            //Log.e("Answer",answer);
                            //   Log.e("id",id);
                            try {
                                object.put(id,answer);
                                //  object.put("correct_ans", answer);

                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }


                        }


                        jsonStr = object.toString();

                        //  System.out.println("jsonString: " + jsonStr);
                        // display the message on screen with the help of Toast.

                    }
                    submitexm(user_id,device_id,exam_id,time_taken,jsonStr);

                }
            });
            iv_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    finish();
                }
            });
        }
        else
        {
            startActivity(new Intent(LiveExamActivity.this, NoNetworkActivity.class));
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
    public void getexmList(final String user_id, final String device_id, final String exm_id) {

        String url1 = BaseClass.mainURL + "live_exam_questions";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(LiveExamActivity.this);
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
                              Log.e("status",""+response);
                            JSONObject jo = null;
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            if (status.equals("true")) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String qstn_id = jo.getString("id");
                                    String questions = jo.getString("questions");
                                    String optionA = jo.getString("optionA");
                                    String optionB = jo.getString("optionB");
                                    String optionC = jo.getString("optionC");
                                    String optionD = jo.getString("optionD");
                                    String correct_ans = jo.getString("correct_ans");
                                    String explanation = jo.getString("explanation");

                                    ExamModel examModel = new ExamModel(qstn_id, questions, optionA, optionB, optionC, optionD, correct_ans, explanation,correct_ans);
                                    examdata.add(examModel);
                                }
                                recyclerviewAdapter = new RecyclerviewAdapter(getApplicationContext(), examdata);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerviewAdapter.notifyDataSetChanged();
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
                params.put("live_exam_id", exm_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void submitexm(final String user_id, final String device_id, final String exm_id,final String time_taken,final String json) {

        String url1 = BaseClass.mainURL + "live_exam_submit";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(LiveExamActivity.this);
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progress.dismiss();
                        //Log.e("response",""+response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            if(status.equals("true"))
                            {
                                Log.e("id",""+exm_id);
                                Intent intent = new Intent(getApplicationContext(), LiveResultActivity.class);
                                intent.putExtra("live_exam_id", exam_id);
                                startActivity(intent);
                                finish();
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
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                Log.e("timetaken",""+time_taken);
                params.put("device_token", device_id);
                params.put("user_id", user_id);
                params.put("live_exam_id", exm_id);
                params.put("time_taken", time_taken);
                params.put("json", json);
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

        private List<ExamModel> qstnList;
        public ArrayList<ExamModel> selectedAnswers;
        private Context context;
        private CheckBox lastChecked = null;
        private int lastCheckedPos = 0;
        String answer;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView qstn_no, qstn;
            RadioGroup radioGroup;
            RadioButton radio_one, radio_two, radio_three, radio_four;

            public MyViewHolder(View view) {
                super(view);
                qstn_no = view.findViewById(R.id.question_counting);
                qstn = view.findViewById(R.id.TvQuestion);
                radioGroup = view.findViewById(R.id.radio_group);
                radio_one = view.findViewById(R.id.radio_one);
                radio_two = view.findViewById(R.id.radio_two);
                radio_three = view.findViewById(R.id.radio_three);
                radio_four = view.findViewById(R.id.radio_four);



            }
        }


        public RecyclerviewAdapter(Context context, List<ExamModel> qstnList) {
            this.context = context;
            this.qstnList = qstnList;
            selectedAnswers = new ArrayList<>();
            /*Log.e("qstnlis",""+qstnList.size());
            for (int i = 1; i < qstnList.size()+1; i++) {
                selectedAnswers.add("Not Attempted");
            }*/
        }

        @Override
        public RecyclerviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_exam_start_layout, parent, false);

            return new RecyclerviewAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerviewAdapter.MyViewHolder holder, int position) {
            ExamModel examModel = qstnList.get(position);
            int pos = position + 1;
            holder.qstn_no.setText("" + pos);
            holder.qstn.setText(examModel.getQuestion());
            holder.radio_one.setText(examModel.getOptionA());
            holder.radio_two.setText(examModel.getOptionB());
            holder.radio_three.setText(examModel.getOptionC());
            holder.radio_four.setText(examModel.getOptionD());
            holder.radioGroup.clearCheck();

            holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @SuppressLint("ResourceType")
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton rb = holder.radioGroup.findViewById(checkedId);
                    int question_id = Integer.parseInt(examModel.getId());
                    if (checkedId == -1) {
                        Toast.makeText(context, "Nothing selected", Toast.LENGTH_SHORT).show();
                    } else {
                        int position = group.indexOfChild(rb);

                        //   Log.e("position",""+position);
                        if (position==0)
                        {
                            answer="A";
                        }
                        else if (position==1)
                        {
                            answer="B";
                        }
                        else if (position==2)
                        {
                            answer="C";
                        }
                        else if (position==3)
                        {
                            answer="D";
                        }
                        String id = examModel.getId();
                        //Toast.makeText(context,rb.getText(), Toast.LENGTH_SHORT).show();
                        // selectedAnswers.set(question_id,rb.getText().toString());
                        ExamModel examModel1 = new ExamModel(id, answer);
                        answerdata.add(examModel1);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            if (examdata != null) {
                return examdata.size();
            } else {
                return 0;
            }
            //  return productList.size();
        }
    }

}