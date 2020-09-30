package com.innoxgen.olavo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.others.CustomDialogClass_upgrade;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.model.SubjectModel;
import com.onurkagan.ksnack_lib.Animations.Fade;
import com.onurkagan.ksnack_lib.KSnack.KSnack;
import com.onurkagan.ksnack_lib.KSnack.KSnackBarEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubjectActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String user_id, device_id, course_id;
    private ArrayList<SubjectModel> classdata = new ArrayList<>();
    public static final String MY_PREFS_NAME = "LoginStatus";
    RecyclerviewAdapter recyclerviewAdapter;
    LinearLayout linear_layout_live;
    ImageView iv_back;
    KSnack kSnack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        recyclerView = findViewById(R.id.recycleview_subject);
        linear_layout_live = findViewById(R.id.linear_layout_live);
        iv_back = findViewById(R.id.iv_back);
        kSnack = new KSnack(SubjectActivity.this);
        if (getIntent().getExtras() != null) {
            course_id = getIntent().getStringExtra("course_id");
        }
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
        if (isNetworkConnectionAvailable()) {
            getclassList(user_id, device_id, course_id);
            linear_layout_live.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    checksubscrptn(user_id, device_id, course_id);

                }
            });
        } else {
            startActivity(new Intent(SubjectActivity.this, NoNetworkActivity.class));
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
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void checksubscrptn(final String user_id, final String device_id, final String course_id) {

        String url1 = BaseClass.mainURL + "check_user_plan";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(SubjectActivity.this);
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
                            String data = jsonObject.getString("msg");
                            //Log.e("status", "" + response);
                            if (status.equals("true")) {
                                Intent intent = new Intent(getApplicationContext(), LiveExamListActivity.class);
                                intent.putExtra("course_id", course_id);
                                startActivity(intent);
                            } else {
                                CustomDialogClass_upgrade cdd = new CustomDialogClass_upgrade(SubjectActivity.this, course_id);
                                //cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                cdd.show();
                                // Toast.makeText(SubjectActivity.this, data, Toast.LENGTH_SHORT).show();
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getclassList(final String user_id, final String device_id, final String course_id) {

        String url1 = BaseClass.mainURL + "subjects_by_courseID";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(SubjectActivity.this);
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        classdata.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                            //Log.e("status", "" + response);
                            JSONObject jo = null;

                            if (status.equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                linear_layout_live.setVisibility(View.VISIBLE);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String course_id = jo.getString("id");
                                    String subname = jo.getString("subject_name");
                                    String image = jo.getString("subject_img");
                                    String sub_status = jo.getString("status");
                                    SubjectModel subjectModel = new SubjectModel(course_id, subname, image, sub_status);
                                    classdata.add(subjectModel);
                                }
                                recyclerviewAdapter = new RecyclerviewAdapter(getApplicationContext(), classdata);
                                //   RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(new GridLayoutManager(CourseActivity.this, 2));
                                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(recyclerviewAdapter);

                            } else {
                                Toast.makeText(SubjectActivity.this, data, Toast.LENGTH_SHORT).show();
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


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

        private List<SubjectModel> clsList;
        URL url, vdeurl;
        URI uri, vdeuri;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView subject_name, class_name;
            public ImageView image_view;
            public LinearLayout linear_layout_1;
            public CardView card_subject;

            public MyViewHolder(View view) {
                super(view);
                class_name = view.findViewById(R.id.textView);
                image_view = view.findViewById(R.id.imageView);
                linear_layout_1 = view.findViewById(R.id.linear_layout1);
                card_subject = view.findViewById(R.id.card_subject);

            }
        }


        public RecyclerviewAdapter(Context context, List<SubjectModel> videoList) {
            this.context = context;
            this.clsList = videoList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_subject_layout, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            SubjectModel subjectModel = clsList.get(position);
            // holder.subject_name.setText(classVideoModel.getSubject_name());
            holder.class_name.setText(subjectModel.getSubname());
            // String subname=classVideoModel.getClass_name() + "-" + classVideoModel.getSubject_name();
            String img = subjectModel.getImage();

            String imgurl = BaseClass.courseURL + img;
            //Log.e("img",""+imgurl);
            try {
                url = new URL(imgurl);
                uri = null;
                uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                url = uri.toURL();

            } catch (URISyntaxException | MalformedURLException e) {
                e.printStackTrace();
            }

            //Log.e("TEST", String.valueOf(url));

            String sub_status = subjectModel.getStatus();
            // Log.e("sub_status", ""+sub_status);
            if (sub_status.equals("0")) {

                holder.linear_layout_1.setBackgroundColor(getResources().getColor(R.color.dim_foreground_disabled_material_dark));

            }

            Picasso.get()
                    .load(String.valueOf(url)).fit()
                    .into(holder.image_view);
            String sub_id = subjectModel.getCourse_id();
            holder.linear_layout_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.e("ID",""+sub_id);
                    if (sub_status.equals("1")) {
                        context.startActivity(new Intent(context, ChapterActivity.class).putExtra("Sub_id", sub_id).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

                    } else {
                        Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (clsList != null) {
                return clsList.size();
            } else {
                return 0;
            }
            //  return productList.size();
        }
    }
}