package com.innoxgen.olavo.fragment;

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
import android.widget.LinearLayout;
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
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.others.CustomDialogClass_upgrade;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.activity.ChapterDetailsActivity;
import com.innoxgen.olavo.activity.ExamActivity;
import com.innoxgen.olavo.model.ExamModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Fathima Shifna K on 02-09-2020.
 */
public class ExamZoneFragment extends Fragment {
    RecyclerView recyclerView;
    String chap_id, user_id, device_id;
    private List<ExamModel> examList = new ArrayList<>();
    public static final String MY_PREFS_NAME = "LoginStatus";
    TextView txt_nodata;

    String plan_status, course_id;
    private boolean isViewShown = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_my_exam_zone, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        recyclerView = root.findViewById(R.id.recycler_exam);
        txt_nodata = root.findViewById(R.id.txt_nodata);

        SharedPreferences sharedPref = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ChapterDetailsActivity activity = (ChapterDetailsActivity) getActivity();
        chap_id = activity.getMyData();
        Log.e("chapid", "" + chap_id);
        if (isNetworkConnectionAvailable()) {
            getchapexam(user_id, device_id, chap_id);
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
    public void getchapexam(final String user_id, final String device_id, final String chap_id) {

        String url1 = BaseClass.mainURL + "exam_list";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        examList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                            Log.e("status exam", "" + response);
                            JSONObject jo = null;

                            if (status.equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                plan_status = jsonObject.getString("plan_status");
                                course_id = jsonObject.getString("plan_course_id");
                                //Log.e("plan_status",""+plan_status);

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    jo = jsonArray.getJSONObject(i);
                                    String id = jo.getString("id");
                                    String title = jo.getString("topic");
                                    String no_of_question = jo.getString("no_of_question");
                                    String type = jo.getString("type");
                                    String time_slot = jo.getString("time_slot");
                                    //Log.e("type",""+type);
                                    ExamModel examModel = new ExamModel(id, title, no_of_question, time_slot, type);
                                    examList.add(examModel);
                                    // Log.e("id",""+examModel.getId());
                                }
                                // Log.e("size",""+examList.size());
                                RecyclerviewAdapter recyclerviewAdapter = new RecyclerviewAdapter(getContext(), examList, plan_status, course_id);
                                // LinearLayoutManager manager = new LinearLayoutManager(getActivity().getApplicationContext());
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerviewAdapter.notifyDataSetChanged();
                                recyclerView.setAdapter(recyclerviewAdapter);

                            } else {
                                txt_nodata.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.INVISIBLE);
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
                params.put("chapter_id", chap_id);
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

        private List<ExamModel> exmList;
        String plan_status, course_id;
        URL url, url_vdeo;
        URI uri, uri_video;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private TextView title, no_of_qstn, duration, start_now;
            //public ImageView image_view;
            public LinearLayout linear_layout;

            public MyViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.exam_title);
                no_of_qstn = view.findViewById(R.id.exam_no_of_que);
                duration = view.findViewById(R.id.exam_duration);
                //    image_view = view.findViewById(R.id.banner_img);
                start_now = view.findViewById(R.id.exam_start_now);

            }
        }


        public RecyclerviewAdapter(Context context, List<ExamModel> exmList, String plan_status, String course_id) {
            this.context = context;
            this.exmList = exmList;
            this.plan_status = plan_status;
            this.course_id = course_id;
        }


        @Override
        public RecyclerviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_exam_layout, parent, false);

            return new RecyclerviewAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerviewAdapter.MyViewHolder holder, int position) {
            ExamModel examModel = exmList.get(position);
            holder.title.setText(examModel.getTitle());
            holder.no_of_qstn.setText(examModel.getNo_of_qstn());
            holder.duration.setText(examModel.getTimeslot());

            holder.start_now.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String type = examModel.getType();
                    Log.e("plan_status1", "" + plan_status);
                    Log.e("type1", "" + type);
                    if (type.equals("1")) {

                        if (plan_status.equals("1")) {

                            Intent intent = new Intent(context, ExamActivity.class);
                            intent.putExtra("exam_id", examModel.getId());
                            intent.putExtra("exam_title", examModel.getTitle());
                            intent.putExtra("no_of_qstn", examModel.getNo_of_qstn());
                            intent.putExtra("duration", examModel.getTimeslot());
                            startActivity(intent);
                        } else {
                            CustomDialogClass_upgrade cdd = new CustomDialogClass_upgrade(getActivity(), course_id);
                            //cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            cdd.show();
                        }
                    } else {
                        Intent intent = new Intent(context, ExamActivity.class);
                        intent.putExtra("exam_id", examModel.getId());
                        intent.putExtra("exam_title", examModel.getTitle());
                        intent.putExtra("no_of_qstn", examModel.getNo_of_qstn());
                        intent.putExtra("duration", examModel.getTimeslot());
                        startActivity(intent);
                    }


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
}
