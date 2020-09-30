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
import android.graphics.Color;
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
import com.innoxgen.olavo.others.CustomDialogClass_upgrade;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.model.SubjectModel;
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

public class ChapterActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    String user_id, device_id, sub_id;
    private ArrayList<SubjectModel> classdata = new ArrayList<>();
    public static final String MY_PREFS_NAME = "LoginStatus";
    RecyclerviewAdapter recyclerviewAdapter;
    ImageView iv_back;
    public Button cancel, upgrade;
    TextView txt_nodata;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        recyclerView = findViewById(R.id.recycleviewchapter);
        iv_back = findViewById(R.id.iv_back);
        txt_nodata = findViewById(R.id.txt_nodata);
        if (getIntent().getExtras() != null) {
            sub_id = getIntent().getStringExtra("Sub_id");
        }
        SharedPreferences sharedPref = getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        //getchapList(user_id, device_id, sub_id);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });
        if (isNetworkConnectionAvailable()) {
            getchapList(user_id, device_id, sub_id);
        } else {
            startActivity(new Intent(ChapterActivity.this, NoNetworkActivity.class));
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
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getchapList(final String user_id, final String device_id, final String sub_id) {

        String url1 = BaseClass.mainURL + "chapters_by_subjectID";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(ChapterActivity.this);
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

                            Log.e("status", "" + response);
                            JSONObject jo = null;
                            classdata.clear();
                            if (status.equals("true")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                String plan_status = jsonObject.getString("plan_status");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String course_id = jo.getString("course_id");
                                    String id = jo.getString("id");
                                    String subname = jo.getString("sub_chapter");
                                    String image = jo.getString("chapter_img");
                                    String type = jo.getString("type");
                                    String datacount = jo.getString("datacount");
                                    SubjectModel subjectModel = new SubjectModel(id, subname, type, image, course_id, datacount);
                                    classdata.add(subjectModel);
                                }
                                recyclerviewAdapter = new RecyclerviewAdapter(getApplicationContext(), classdata, plan_status);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(recyclerviewAdapter);

                            } else {
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
                params.put("subject_id", sub_id);
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

        private List<SubjectModel> chapList;
        URL url, vdeurl;
        URI uri, vdeuri;
        private Context context;
        String plan_status;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView subject_name, chap_name;
            public ImageView image_view;
            public LinearLayout linear_layout_1, lin_1;

            public MyViewHolder(View view) {
                super(view);
                chap_name = view.findViewById(R.id.chapter_name);
                image_view = view.findViewById(R.id.image);
                linear_layout_1 = view.findViewById(R.id.linear_layout2);
                lin_1 = view.findViewById(R.id.lin_1);

            }
        }


        public RecyclerviewAdapter(Context context, List<SubjectModel> chapList, String plan_status) {
            this.context = context;
            this.chapList = chapList;
            this.plan_status = plan_status;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chapter_layout, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            SubjectModel subjectModel = chapList.get(position);
            holder.chap_name.setText(subjectModel.getSubname());
            String img = subjectModel.getImage();

            String imgurl = BaseClass.courseURL + img;
            try {
                url = new URL(imgurl);
                uri = null;
                uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                url = uri.toURL();

            } catch (URISyntaxException | MalformedURLException e) {
                e.printStackTrace();
            }
            String datacount = subjectModel.getDatacount();

            String type = subjectModel.getType();
            if (type.equals("1")) {
                //  Log.e("TYPE", "1");
                if (plan_status.equals("1")) {
                    Picasso.get()
                            .load(R.drawable.open).fit().centerInside()
                            .into(holder.image_view);
                    if (datacount.equals("0")) {
                        holder.linear_layout_1.setBackgroundColor(getResources().getColor(R.color.dim_foreground_disabled_material_dark));
                        holder.lin_1.setVisibility(View.VISIBLE);
                    } else {
                        holder.linear_layout_1.setBackgroundColor(getResources().getColor(R.color.white));
                        holder.lin_1.setVisibility(View.GONE);
                    }

                } else {
                    Picasso.get()
                            .load(R.drawable.lock).fit().centerInside()
                            .into(holder.image_view);
                }
            } else {
                //  Log.e("TYPE", "0");

                Picasso.get()
                        .load(R.drawable.right_grey_arrow).fit().centerInside()
                        .into(holder.image_view);
            }

            String id = subjectModel.getId();
            String course_id = subjectModel.getCourse_id();
            holder.linear_layout_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (type.equals("1") && !(plan_status.equals("1"))) {

                        CustomDialogClass_upgrade cdd = new CustomDialogClass_upgrade(ChapterActivity.this, course_id);
                        cdd.show();
                    } else {
                        if (!datacount.equals("0")) {
                            context.startActivity(new Intent(context, ChapterDetailsActivity.class).putExtra("chap_id", id).putExtra("Chapname", subjectModel.getSubname()).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            // finish();
                        } else {

                            Toast.makeText(context, "Coming Soon", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
            });
        }

        @Override
        public int getItemCount() {
            if (chapList != null) {
                return chapList.size();
            } else {
                return 0;
            }
        }
    }


}