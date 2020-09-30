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
import android.widget.ImageView;
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
import com.innoxgen.olavo.activity.VideoViewActivity;
import com.innoxgen.olavo.activity.VimeVideoActivity;
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
import java.util.List;
import java.util.Map;

/**
 * Created by Fathima Shifna K on 01-09-2020.
 */
public class StudyFragment extends Fragment {
    RecyclerView recyclerView;
    String chap_id, user_id, device_id;
    RecyclerviewAdapter recyclerviewAdapter;
    String plan_status,course_id;
    private boolean isViewShown = false;
    private List<ClassVideoModel> vdoList = new ArrayList<>();
    public static final String MY_PREFS_NAME = "LoginStatus";
    TextView txt_nodata;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_study, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        recyclerView = root.findViewById(R.id.recycler_videoview);
        txt_nodata=root.findViewById(R.id.txt_nodata);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ChapterDetailsActivity activity = (ChapterDetailsActivity) getActivity();
        chap_id = activity.getMyData();
       // Log.e("chapid", "" + chap_id);

        if (isNetworkConnectionAvailable()) {
            getchapVideo(user_id, device_id, chap_id);
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


    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
            if (getView() != null && isVisibleToUser) {
                isViewShown = true; // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data

            } else {
                isViewShown = false;
             //  getchapVideo(user_id, device_id, chap_id);
            }


    }*/
    boolean isNetworkConnectionAvailable() {
        try {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            return (mNetworkInfo == null) ? false : true;

        }catch (NullPointerException e){
            return false;

        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getchapVideo(final String user_id, final String device_id, final String chap_id) {

        String url1 = BaseClass.mainURL + "chapters_by_video";
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
                                plan_status = jsonObject.getString("plan_status");
                                course_id=jsonObject.getString("plan_course_id");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String id = jo.getString("id");
                                    String title = jo.getString("title");
                                    String chapter_video = jo.getString("chapter_video");
                                    String image = jo.getString("chapter_banner");
                                    String chapter_name = jo.getString("sub_chapter");
                                    String type = jo.getString("type");
                                    String video_link=jo.getString("link");
                                   // Log.e("Video",""+video_link);
                                    ClassVideoModel classVideoModel = new ClassVideoModel(id, title, chapter_video, image, chapter_name, type,video_link);
                                    vdoList.add(classVideoModel);
                                }
                                recyclerviewAdapter = new RecyclerviewAdapter(getContext(), vdoList, plan_status,course_id);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(recyclerviewAdapter);

                            }
                            else {
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

        private List<ClassVideoModel> vdeoList;
        String plan_status,course_id;
        URL url, url_vdeo;
        URI uri, uri_video;
        private Context context;
         String vdeURL;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView video_title, video_desc;
            public ImageView image_view;
            public LinearLayout linear_layout_video;

            public MyViewHolder(View view) {
                super(view);
                video_title = view.findViewById(R.id.video_title);
                video_desc = view.findViewById(R.id.video_desc);
                image_view = view.findViewById(R.id.banner_img);
                linear_layout_video = view.findViewById(R.id.linear_layout_videos);

            }
        }


        public RecyclerviewAdapter(Context context, List<ClassVideoModel> videoList, String plan_status,String course_id) {
            this.context = context;
            this.vdeoList = videoList;
            this.plan_status = plan_status;
            this.course_id=course_id;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_studyzone_layout, parent, false);

            return new RecyclerviewAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            ClassVideoModel classVideoModel = vdeoList.get(position);
            holder.video_title.setText(classVideoModel.getTitle());
            holder.video_desc.setText(classVideoModel.getChap_name());
            String img = classVideoModel.getImage();
            String vdeo = classVideoModel.getVideo();
            String imgurl = BaseClass.videoImgURL + img;

            final String subname = classVideoModel.getTitle();
           if (!vdeo.equals(""))
           {
               vdeURL = BaseClass.videoURL + vdeo;
               try {
                   url_vdeo = new URL(vdeURL);
                   uri_video = null;
                  uri_video = new URI(url_vdeo.getProtocol(), url_vdeo.getUserInfo(), url_vdeo.getHost(), url_vdeo.getPort(), url_vdeo.getPath(), url_vdeo.getQuery(), url_vdeo.getRef());
                   url_vdeo = uri_video.toURL();
                   vdeURL=String.valueOf(url_vdeo);
               } catch (URISyntaxException | MalformedURLException e) {
                   e.printStackTrace();
               }
           }
           else
           {
               vdeURL=classVideoModel.getLink();
             //  Log.e("LINK", ""+vdeURL);
           }

            try {
                url = new URL(imgurl);
                uri = null;
                uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                url = uri.toURL();
            }
            catch (URISyntaxException | MalformedURLException e) {
                e.printStackTrace();
            }
                Picasso.get()
                    .load(String.valueOf(url)).fit()
                    .into(holder.image_view);

            holder.linear_layout_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String type = classVideoModel.getType();
                    String link=classVideoModel.getLink();
                    if (type.equals("1")) {
                        if (plan_status.equals("1")) {
                           if (!link.equals(" "))
                           {
                               Log.e("LINK-1", ""+vdeURL);
                               Intent intent = new Intent(context, VimeVideoActivity.class);
                               intent.putExtra("video_uri", link)
                                       .putExtra("ClassName", subname);
                               startActivity(intent);
                           }
                           else
                           {
                               Intent intent = new Intent(context, VideoViewActivity.class);
                               intent.putExtra("video_uri", vdeURL)
                                       .putExtra("ClassName", subname);
                               startActivity(intent);
                           }
                        }
                        else {
                            CustomDialogClass_upgrade cdd = new CustomDialogClass_upgrade(getActivity(), course_id);
                            //cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            cdd.show();
                        }
                    }
                    else {
                        if (!link.equals(""))
                        {
                            Log.e("LINK-1", ""+vdeURL);
                            Intent intent = new Intent(context, VimeVideoActivity.class);
                            intent.putExtra("video_uri", link)
                                    .putExtra("ClassName", subname);
                            startActivity(intent);
                        }
                        else
                        {
                            Intent intent = new Intent(context, VideoViewActivity.class);
                            intent.putExtra("video_uri", vdeURL)
                                    .putExtra("ClassName", subname);
                            startActivity(intent);
                        }
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (vdeoList != null) {
                return vdeoList.size();
            } else {
                return 0;
            }
            //  return productList.size();
        }
    }
}