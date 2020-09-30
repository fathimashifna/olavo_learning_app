package com.innoxgen.olavo.btm_nav;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.fragment.NoNetworkFragment;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.activity.ChatActivity;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.model.ExamModel;
import com.onurkagan.ksnack_lib.KSnack.KSnack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fathima Shifna K on 30-08-2020.
 */
public class DiscussionFragment extends Fragment {
    RecyclerView recyclerView;
    String user_id, device_id;
    private List<ExamModel> vdoList = new ArrayList<>();
    public static final String MY_PREFS_NAME = "LoginStatus";
    RecyclerviewAdapter recyclerviewAdapter;
    private boolean isViewShown = false;
    KSnack kSnack;
    TextView txt_nodata;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_discussion, container, false);

        kSnack = new KSnack((Activity) getContext());
        if (kSnack != null ) {
            kSnack.dismiss();
        }

        recyclerView = root.findViewById(R.id.recyclerciew_dscsn);
        txt_nodata=root.findViewById(R.id.txt_nodata);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        return root;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

            if (getView() != null && isVisibleToUser) {
                isViewShown = true; // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data
                getdscn(user_id, device_id);
            } else {
                isViewShown = false;
            }



    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getdscn(final String user_id, final String device_id) {

        String url1 = BaseClass.mainURL + "get_doubt_question";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        vdoList.clear();
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
                                    String title = jo.getString("question");
                                    String date = jo.getString("created_at");
                                    String time = jo.getString("time");
                                    String class_name = jo.getString("class_name");
                                    String subject_name = jo.getString("subject_name");
                                    ExamModel examModel = new ExamModel(id, title, date, time);
                                    vdoList.add(examModel);
                                }
                                recyclerviewAdapter = new RecyclerviewAdapter(getContext(), vdoList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(recyclerviewAdapter);

                            }
                            else
                            {
                                recyclerView.setVisibility(View.GONE);
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
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

        private List<ExamModel> qstnList;

        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, desc, answer;

            public MyViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.question_tv);
                desc = view.findViewById(R.id.tv_date_time);
                answer = view.findViewById(R.id.answer);

            }
        }


        public RecyclerviewAdapter(Context context, List<ExamModel> qstnList) {
            this.context = context;
            this.qstnList = qstnList;
        }

        @Override
        public RecyclerviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_query_layout, parent, false);

            return new RecyclerviewAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            ExamModel examModel = qstnList.get(position);
            holder.title.setText(examModel.getQuestion());
            holder.desc.setText("Asked on " + examModel.getDate() + " at " + examModel.getTime());

            holder.answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("qstn_id", examModel.getId());
                    intent.putExtra("dbt_qstn", examModel.getQuestion());
                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            if (qstnList != null) {
                return qstnList.size();
            } else {
                return 0;
            }
            //  return productList.size();
        }
    }
}