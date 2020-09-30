package com.innoxgen.olavo.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

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
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.model.ExamModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fathima Shifna K on 02-09-2020.
 */
public  class ResultActivity extends AppCompatActivity {
    TextView txt_score,txt_time_taken;
    RecyclerView recyclerView;
    String user_id,device_id,exam_id;
    private ArrayList<ExamModel> examdata = new ArrayList<>();
    public static final String MY_PREFS_NAME = "LoginStatus";
    ImageView iv_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_show);
       getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        txt_score=findViewById(R.id.score);
        txt_time_taken=findViewById(R.id.time_taken);
        recyclerView=findViewById(R.id.recycleview_rslt);
        iv_back = findViewById(R.id.iv_back);
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (getIntent().getExtras() != null) {
            exam_id = getIntent().getStringExtra("exam_id");
        }


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        if (isNetworkConnectionAvailable())
        {
            getrslt(user_id,device_id,exam_id);
        }
        else
        {
            startActivity(new Intent(ResultActivity.this, NoNetworkActivity.class));
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
    @Override
    public void onBackPressed() {
        //super.onBackPressed();

        Intent intent=new Intent(ResultActivity.this,NavigationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity( intent);
         finish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getrslt(final String user_id, final String device_id, final String exm_id) {

        String url1 = BaseClass.mainURL + "exam_result_score";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(ResultActivity.this);
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

                            //Log.e("status",""+response);


                            JSONObject jo = null;
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                           // Log.e("length",""+jsonArray.length());
                            if (status.equals("true")) {
                                String total_question = jsonObject.getString("total_question");
                                String correct_question = jsonObject.getString("correct_question");
                                String time_taken = jsonObject.getString("time_taken");
                                txt_score.setText(correct_question+"/"+total_question);
                                txt_time_taken.setText(time_taken);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String id = jo.getString("id");
                                    String questions = jo.getString("questions");
                                    String optionA = jo.getString("optionA");
                                    String optionB = jo.getString("optionB");
                                    String optionC = jo.getString("optionC");
                                    String optionD = jo.getString("optionD");
                                    String correct_ans = jo.getString("correct_ans");
                                    String explanation = jo.getString("explanation");
                                   String your_ans = jo.getString("your_ans");

                                    ExamModel examModel = new ExamModel(id, questions, optionA, optionB, optionC, optionD, correct_ans, explanation,your_ans);
                                    examdata.add(examModel);
                                }
                              RecyclerviewAdapter  recyclerviewAdapter = new  RecyclerviewAdapter(getApplicationContext(), examdata);
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
                params.put("exam_id", exm_id);
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

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView qstn_no, qstn,explntn,optionA,optionB,optionC,optionD;
            RadioButton radio_one, radio_two, radio_three, radio_four;

            public MyViewHolder(View view) {
                super(view);
                qstn_no = view.findViewById(R.id.question_counting);
                qstn = view.findViewById(R.id.TvQuestion);
                optionA=view.findViewById(R.id.TvOptionA);
                optionB=view.findViewById(R.id.TvOptionB);
                optionC=view.findViewById(R.id.TvOptionC);
                optionD=view.findViewById(R.id.TvOptionD);
                radio_one = view.findViewById(R.id.radio_one);
                radio_two = view.findViewById(R.id.radio_two);
                radio_three = view.findViewById(R.id.radio_three);
                radio_four = view.findViewById(R.id.radio_four);
                explntn=view.findViewById(R.id.explntn);

            }
        }


        public RecyclerviewAdapter(Context context, List<ExamModel> qstnList) {
            this.context = context;
            this.qstnList = qstnList;

        }

        @Override
        public RecyclerviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_result, parent, false);

            return new RecyclerviewAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerviewAdapter.MyViewHolder holder, int position) {
            ExamModel examModel = qstnList.get(position);
            int pos = position + 1;
            holder.qstn_no.setText("" + pos);
            holder.qstn.setText(examModel.getQuestion());
            holder.optionA.setText(examModel.getOptionA());
            holder.optionB.setText(examModel.getOptionB());
            holder.optionC.setText(examModel.getOptionC());
            holder.optionD.setText(examModel.getOptionD());
            holder.explntn.setText(Html.fromHtml(examModel.getExplntn()));
            String correct_ans=examModel.getCorrect_ans();
            String your_ans=examModel.getYour_ans();
            if (correct_ans.equals(your_ans))
            {
                if (correct_ans.equals("A"))
                {
                    holder.radio_one.setButtonDrawable(R.drawable.ic_action_name);
                }
                else if (correct_ans.equals("B"))
                {
                    holder.radio_two.setButtonDrawable(R.drawable.ic_action_name);
                }
                else if (correct_ans.equals("C"))
                {
                    holder.radio_three.setButtonDrawable(R.drawable.ic_action_name);
                }
                else if (correct_ans.equals("D"))
                {
                    holder.radio_four.setButtonDrawable(R.drawable.ic_action_name);
                }
            }
            else
            {
                if (correct_ans.equals("A"))
                {
                    holder.radio_one.setButtonDrawable(R.drawable.ic_action_name);
                }
                else if (correct_ans.equals("B"))
                {
                    holder.radio_two.setButtonDrawable(R.drawable.ic_action_name);
                }
                else if (correct_ans.equals("C"))
                {
                    holder.radio_three.setButtonDrawable(R.drawable.ic_action_name);
                }
                else if (correct_ans.equals("D"))
                {
                    holder.radio_four.setButtonDrawable(R.drawable.ic_action_name);
                }
                if (your_ans.equals("A"))
                {
                    holder.radio_one.setButtonDrawable(R.drawable.ic_wrong);
                }
                else if (your_ans.equals("B"))
                {
                    holder.radio_two.setButtonDrawable(R.drawable.ic_wrong);
                }
                else if (your_ans.equals("C"))
                {
                    holder.radio_three.setButtonDrawable(R.drawable.ic_wrong);
                }
                else if (your_ans.equals("D"))
                {
                    holder.radio_four.setButtonDrawable(R.drawable.ic_wrong);
                }
            }






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
