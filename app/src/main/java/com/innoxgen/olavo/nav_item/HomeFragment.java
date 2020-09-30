package com.innoxgen.olavo.nav_item;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.innoxgen.olavo.Adapter.RecyclerviewAdapter;
import com.innoxgen.olavo.fragment.NoNetworkFragment;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.activity.Login;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.activity.SubjectActivity;
import com.innoxgen.olavo.model.ClassVideoModel;
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
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class HomeFragment extends Fragment {
    RecyclerView recyclerView;
    private RecyclerviewAdapter recyclerviewAdapter;
    String device_id;
    public static final String MY_PREFS_NAME = "LoginStatus";
    String user_id;
    RecyclerView gridView,grid_view_mycourse;
    ImageView img_banner,img_arrow;
    String image;
    GifImageView image_call;
    TextView txt_latest,txt_mycourse,txt_course;


    private ArrayList<ClassVideoModel>vdoList = new ArrayList<>();
    private ArrayList<ClassVideoModel>classdata=new ArrayList<>();
    private ArrayList<ClassVideoModel>myclassdata=new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().setTitle("Home");
        recyclerView =root.findViewById(R.id.vdeo_recyclerview);
        gridView=root.findViewById(R.id.recycler_grid_view_course);
        grid_view_mycourse=root.findViewById(R.id.recycler_grid_view_mycourse);
        img_banner=root.findViewById(R.id.img2);
        image_call=root.findViewById(R.id.imageView_call);
        txt_latest=root.findViewById(R.id.txt_latest);
        txt_mycourse=root.findViewById(R.id.txt_mycourse);
        txt_course=root.findViewById(R.id.txt_course);
        img_arrow=root.findViewById(R.id.img_arrow);

        SharedPreferences sharedPref =getContext().getSharedPreferences(MY_PREFS_NAME,Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
       if (isNetworkConnectionAvailable())
        {
            getVdeoList(user_id,device_id);
            getCLassList(user_id,device_id);
            getmycourse(user_id,device_id);
            getBanner(user_id,device_id);
            image_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String phone = "+917592001115 ";
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                    startActivity(intent);
                }
            });

        }
        else
        {
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
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getVdeoList(final String user_id,final String device_id) {

        String url1 = BaseClass.mainURL+"get_video";
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
                            String status=jsonObject.getString("result");
                            String data=jsonObject.getString("msg");
                            Log.e("status",""+response);
                            JSONObject jo= null;

                            if (status.equals("true"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String course_id=jo.getString("course_id");

                                    String subject_id=jo.getString("subject_id");
                                    String chapter_id=jo.getString("chapter_id");
                                    String subject_name=jo.getString("subject_name");
                                    String class_name=jo.getString("class_name");
                                    String image=jo.getString("image");
                                    String video=jo.getString("video");
                                    String link=jo.getString("link");

                                    ClassVideoModel classVideoModel = new ClassVideoModel(course_id,image,video,subject_name,class_name,link);
                                    vdoList.add(classVideoModel);
                                }
                                recyclerviewAdapter= new RecyclerviewAdapter(getContext(),vdoList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.HORIZONTAL, false);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(recyclerviewAdapter);

                            }
                            else if (status.equals("false"))
                            {

                                Intent intent=new Intent(getContext(), Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity( intent);
                                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                SharedPreferences sharedPref =getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putInt("login_status", 0);
                                editor.commit();
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
                        Log.e("Error-login_alert",""+ error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
             //   Log.e("device_id",""+device_id);
              //  Log.e("user_id",""+user_id);
                params.put("device_token",device_id);
                params.put("user_id",user_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
          int socketTimeout = 30000;//30 seconds - change to what you want
       RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
           stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void getCLassList(final String user_id,final String device_id) {

        String url1 = BaseClass.mainURL+"get_all_course";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(getContext());
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
                            String status=jsonObject.getString("result");
                            String data=jsonObject.getString("msg");
                            Log.e("status course",""+response);
                            JSONObject jo= null;
                            if (status.equals("true"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String id=jo.getString("id");
                                    String class_name=jo.getString("class_name");
                                    String image=jo.getString("image");
                                    String status1=jo.getString("status");

                                    ClassVideoModel classVideoModel1 = new ClassVideoModel(id,class_name,image);
                                    classdata.add(classVideoModel1);
                                }
                                RecyclerviewAdapter_grid   recyclerviewAdapter= new RecyclerviewAdapter_grid(getActivity().getApplicationContext(),classdata);
                               //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(new GridLayoutManager(CourseActivity.this, 2));
                                gridView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 3));
                                gridView.setItemAnimator(new DefaultItemAnimator());
                                gridView.setAdapter(recyclerviewAdapter);
                            }
                            else
                            {


                               if (data.equals("Invalid token"))
                               {
                                   Intent intent=new Intent(getContext(), Login.class);
                                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                   startActivity( intent);
                                   getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                   SharedPreferences sharedPref =getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                                   SharedPreferences.Editor editor = sharedPref.edit();
                                   editor.putInt("login_status", 0);
                                   editor.commit();
                               }
                               else if (data.equals("Invalid user"))
                               {
                                   Intent intent=new Intent(getContext(), Login.class);
                                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                   startActivity( intent);
                                   getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                   SharedPreferences sharedPref =getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
                                   SharedPreferences.Editor editor = sharedPref.edit();
                                   editor.putInt("login_status", 0);
                                   editor.commit();
                               }
                               else
                               {
                                   txt_course.setVisibility(View.INVISIBLE);
                               }
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
                        Log.e("Error-login_alert",""+ error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
              //  Log.e("device_id",""+device_id);
              //  Log.e("user_id",""+user_id);
                params.put("device_token",device_id);
                params.put("user_id",user_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
          int socketTimeout = 30000;//30 seconds - change to what you want
         RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
          stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void getmycourse(final String user_id,final String device_id) {

        String url1 = BaseClass.mainURL+"myCourse";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        myclassdata.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status=jsonObject.getString("result");
                            String data=jsonObject.getString("msg");
                          Log.e("statusmycourse",""+response);
                            JSONObject jo= null;
                            if (status.equals("true"))
                            {
                                txt_mycourse.setVisibility(View.VISIBLE);
                                grid_view_mycourse.setVisibility(View.VISIBLE);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String id=jo.getString("course_id");
                                    String class_name=jo.getString("class_name");
                                    String image=jo.getString("image");
                                   // String status1=jo.getString("");

                                    ClassVideoModel classVideoModel1 = new ClassVideoModel(id,class_name,image);
                                    myclassdata.add(classVideoModel1);
                                }
                                RecyclerviewAdapter_grid   recyclerviewAdapter= new RecyclerviewAdapter_grid(getActivity().getApplicationContext(),myclassdata);
                                //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(new GridLayoutManager(CourseActivity.this, 2));
                                grid_view_mycourse.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 3));
                                grid_view_mycourse.setItemAnimator(new DefaultItemAnimator());
                                grid_view_mycourse.setAdapter(recyclerviewAdapter);
                            }
                            else if (status.equals("false"))
                            {

                                txt_mycourse.setVisibility(View.GONE);
                                grid_view_mycourse.setVisibility(View.GONE);

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
                        Log.e("Error-login_alert",""+ error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //  Log.e("device_id",""+device_id);
                //  Log.e("user_id",""+user_id);
                params.put("device_token",device_id);
                params.put("user_id",user_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void getBanner(final String user_id,final String device_id) {

        String url1 = BaseClass.mainURL+"get_banner";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(getContext());
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
                            String status=jsonObject.getString("result");
                            String data=jsonObject.getString("msg");
                            Log.e("status",""+response);
                            JSONObject jo= null;


                            if (status.equals("true"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String id=jo.getString("id");
                                    image=jo.getString("banner_img");
                                    String b_name=jo.getString("banner_name");

                                }
                                String b_url=BaseClass.bannerURL+image;
                                Picasso.get()
                                        .load(b_url).fit()
                                        .into(img_banner);
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
                        Log.e("Error-login_alert",""+ error.getMessage());
                        progress.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("device_token",device_id);
                params.put("user_id",user_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
         int socketTimeout = 30000;//30 seconds - change to what you want
          RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
           stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class RecyclerviewAdapter_grid extends RecyclerView.Adapter<RecyclerviewAdapter_grid.MyViewHolder> {


        private ArrayList<ClassVideoModel> dataArrayList;
        URL url,vdeurl;
        URI uri,vdeuri;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView item_name;
            public ImageView image_view;
            public LinearLayout linear_layout_1;

            public MyViewHolder(View view) {
                super(view);
               item_name = view.findViewById(R.id.textView);
                image_view = view.findViewById(R.id.imageView);
                linear_layout_1=view.findViewById(R.id.linear_layoutgrid);

            }
        }


        public RecyclerviewAdapter_grid(Context context,ArrayList<ClassVideoModel> dataArray) {
            this.context=context;
            this.dataArrayList = dataArray;
        }

        @Override
        public RecyclerviewAdapter_grid.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.grid_item, parent, false);

            return new RecyclerviewAdapter_grid.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerviewAdapter_grid.MyViewHolder holder, int position) {
            ClassVideoModel dataModel = dataArrayList.get(position);
            // holder.subject_name.setText(classVideoModel.getSubject_name());
            holder.item_name.setText(dataModel.getClass_name());
            String img=dataModel.getImage();
            String imgurl= BaseClass.courseURL+img;


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
            Picasso.get()
                    .load(imgurl)
                    .into(holder.image_view);
            String crs_id=dataModel.getCourse_id();
            holder.linear_layout_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context, SubjectActivity.class).putExtra("course_id", crs_id).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    );
                }
            });
        }

        @Override
        public int getItemCount() {
            if (dataArrayList != null) {
                return dataArrayList.size();
            } else {
                return 0;
            }
            //  return productList.size();
        }
    }
}