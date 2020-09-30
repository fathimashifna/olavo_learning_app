package com.innoxgen.olavo.nav_item;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.Adapter.RecyclerviewAdapter;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.activity.ChatActivity;
import com.innoxgen.olavo.activity.LiveExamActivity;
import com.innoxgen.olavo.fragment.NoNetworkFragment;
import com.innoxgen.olavo.model.ChatModel;
import com.innoxgen.olavo.model.ClassVideoModel;
import com.innoxgen.olavo.model.ScoreModel;
import com.innoxgen.olavo.others.BaseClass;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeaderBoardFragment extends Fragment {

    TextView txt_username, txt_score,score_list,txt_nodata;
    ImageView img_prfl;
    RecyclerView recyclerView;
    String user_id, device_id, live_exam_id;
    public static final String MY_PREFS_NAME = "LoginStatus";
    private ArrayList<ScoreModel> scoredata = new ArrayList<>();
    LinearLayout linear_layout;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_leaderboard, container, false);
        getActivity().setTitle("LeaderBoard");
        txt_username = root.findViewById(R.id.user_name_leaderboard);
        txt_score = root.findViewById(R.id.score);
        score_list=root.findViewById(R.id.score_list);
        img_prfl = root.findViewById(R.id.profile_image_leaderboard);
        recyclerView = root.findViewById(R.id.recycleview_scoreboard);
        linear_layout=root.findViewById(R.id.linear_layout);
        txt_nodata=root.findViewById(R.id.txt_nodata);
        SharedPreferences sharedPref = getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (isNetworkConnectionAvailable()) {

            getscore(user_id, device_id);

        } else {
            Fragment fragment = new NoNetworkFragment();
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.nav_host_fragment, fragment);
            ft.addToBackStack(null);
            getActivity().getSupportFragmentManager().popBackStack();
            ft.commit();
        }
        return root;
    }

    boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getscore(final String user_id, final String device_id) {

        String url1 = BaseClass.mainURL + "user_score_history";
        // Log.e("url", "" + url1);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //    progress.dismiss();
                     //  Log.e("response", "" + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            JSONObject jo = null;
                            String name = jsonObject.getString("name");
                            String image = jsonObject.getString("image");
                            String imgurl = BaseClass.userURL + image;
                            txt_username.setText(name);
                            Picasso.get()
                                    .load(imgurl)
                                    .into(img_prfl);
                            if (status.equals("true")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                int max_score = 0;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String id = jo.getString("live_exam_id");
                                    String class_name = jo.getString("class_name");
                                    String time_slot = jo.getString("time_slot");
                                    String marks = jo.getString("marks");
                                //    Log.e("Score", "" + marks);
                                    String no_of_question = jo.getString("no_of_question");
                                    int score = Integer.parseInt(marks);
                                    if (score > max_score) {
                                        max_score = score;
                                      //  Log.e("max_score", "" + max_score);
                                    }
                                    live_exam_id = jsonArray.getJSONObject(0).getString("live_exam_id");
                                   // Log.e("live_exam_id", "" + live_exam_id);


                                }
                                txt_score.setText("Your best score is: " + max_score);
                                getscorelist(user_id,device_id,live_exam_id);

                            }
                            else
                            {
                                txt_score.setText("Your best score is: 0" );
                               //  recyclerView.setVisibility(View.INVISIBLE);
                                txt_nodata.setVisibility(View.VISIBLE);
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
                params.put("device_token", device_id);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void getscorelist(final String user_id, final String device_id, final String live_exam_id) {

        String url1 = BaseClass.mainURL + "live_score_history";
        // Log.e("url", "" + url1);
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        scoredata.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                       progress.dismiss();
                      //  Log.e("response", "" + response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            JSONObject jo = null;
                            if (status.equals("true")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String user_id = jo.getString("user_id");
                                    String name = jo.getString("name");
                                    String marks = jo.getString("marks");
                                    //  String no_of_question = jo.getString("no_of_question");
                                    ScoreModel scoreModel = new ScoreModel(user_id, name, marks);
                                    scoredata.add(scoreModel);
                                }
                                RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(getContext(), scoredata);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
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
                params.put("user_id", user_id);
                params.put("device_token", device_id);
                params.put("live_exam_id", live_exam_id);

                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

        private List<ScoreModel> scoreList;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txt_score, txt_name, txt_serial_no;
           public MyViewHolder(View view) {
                super(view);
                txt_score = view.findViewById(R.id.txt_score);
                txt_name = view.findViewById(R.id.txt_name);
                txt_serial_no = view.findViewById(R.id.txt_serial_no);


            }
        }


        public RecyclerviewAdapter(Context context, List<ScoreModel> scoreList) {
            this.context = context;
            this.scoreList = scoreList;
        }

        @Override
        public RecyclerviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_score, parent, false);

            return new RecyclerviewAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerviewAdapter.MyViewHolder holder, int position) {
            ScoreModel scoreModel = scoreList.get(position);
            int pos = position + 1;
            holder.txt_serial_no.setText("" + pos);
            String score = scoreModel.getScore();
            String name = scoreModel.getName();
                   if (pos>5)
                   {
                       holder.txt_name.setText(name);
                       holder.txt_score.setText(score);
                   }
                   else
                   {
                       for (pos=0;pos<=5;pos++)
                       {
                           holder.txt_name.setText("*********");
                           holder.txt_score.setText("***");
                       }
                   }



        }

        @Override
        public int getItemCount() {
            if (scoreList != null) {
                return scoreList.size();
            } else {
                return 0;
            }
            //  return productList.size();
        }
    }
}