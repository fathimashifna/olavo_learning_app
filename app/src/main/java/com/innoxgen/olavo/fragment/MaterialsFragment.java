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
import com.innoxgen.olavo.activity.PDFViewActivity;
import com.innoxgen.olavo.model.ChapModel;

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
 * Created by Fathima Shifna K on 01-09-2020.
 */
public class MaterialsFragment extends Fragment {
    RecyclerView recyclerView;
    String chap_id, user_id, device_id;
    private List<ChapModel> chapList = new ArrayList<>();
    public static final String MY_PREFS_NAME = "LoginStatus";
    RecyclerviewAdapter recyclerviewAdapter;
    String plan_status,course_id;
    private boolean isViewShown = false;
    TextView txt_nodata;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_material, container, false);
        recyclerView = root.findViewById(R.id.recycleview);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        txt_nodata=root.findViewById(R.id.txt_nodata);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getActivity().getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        ChapterDetailsActivity activity = (ChapterDetailsActivity) getActivity();
        chap_id = activity.getMyData();
        //  Log.e("chapid",""+chap_id);
         if (isNetworkConnectionAvailable()) {
             getchapMat(user_id, device_id, chap_id);
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
        ConnectivityManager cm = (ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

            if (getView() != null && isVisibleToUser) {
                isViewShown = true; // fetchdata() contains logic to show data when page is selected mostly asynctask to fill the data

            } else {
                isViewShown = false;
              //  getchapMat(user_id, device_id, chap_id);
            }


    }*/

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getchapMat(final String user_id, final String device_id, final String chap_id) {

        String url1 = BaseClass.mainURL + "chapters_pdf_doc";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        chapList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status = jsonObject.getString("result");
                            String data = jsonObject.getString("msg");
                          //  Log.e("status", "" + response);
                            JSONObject jo = null;
                            if (status.equals("true")) {
                                plan_status = jsonObject.getString("plan_status");
                                course_id=jsonObject.getString("plan_course_id");
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    //String id = jo.getString("id");
                                    String title = jo.getString("title");
                                    String chapter_pdf = jo.getString("chapter_pdf");
                                     String type = jo.getString("type");
                                    String chapter_name = jo.getString("sub_chapter");

                                    ChapModel chapModel = new ChapModel(title, chapter_pdf, chapter_name,type);
                                    chapList.add(chapModel);
                                }
                                recyclerviewAdapter = new RecyclerviewAdapter(getContext(), chapList,plan_status,course_id);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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

        private List<ChapModel> ChapList;
        String plan_status,course_id;
        URL url, url_vdeo;
        URI uri, uri_video;
        private Context context;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView title, video_desc, txt_open;
            //public ImageView image_view;
            public LinearLayout linear_layout;

            public MyViewHolder(View view) {
                super(view);
                title = view.findViewById(R.id.textView);
                txt_open = view.findViewById(R.id.txt_open);
                //    image_view = view.findViewById(R.id.banner_img);
                linear_layout = view.findViewById(R.id.linear_mat);

            }
        }


        public RecyclerviewAdapter(Context context, List<ChapModel> chapList,String plan_status,String course_id) {
            this.context = context;
            this.ChapList = chapList;
            this.plan_status=plan_status;
            this.course_id=course_id;
        }

        @Override
        public RecyclerviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_material_layout, parent, false);

            return new RecyclerviewAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerviewAdapter.MyViewHolder holder, int position) {
            ChapModel chapModel = chapList.get(position);
            holder.title.setText(chapModel.getTitle());
            String type = chapModel.getType();
            holder.txt_open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type.equals("1")) {
                        if (plan_status.equals("1")) {
                            Intent intent = new Intent(context, PDFViewActivity.class);
                            intent.putExtra("chapter_name", chapModel.getChapter_name());
                            intent.putExtra("chapter_pdf", chapModel.getChapter_pdf());
                            startActivity(intent);
                        } else {
                            CustomDialogClass_upgrade cdd = new CustomDialogClass_upgrade(getActivity(), course_id);
                            //cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            cdd.show();
                        }
                    }
                    else
                    {
                        Intent intent = new Intent(context, PDFViewActivity.class);
                        intent.putExtra("chapter_name", chapModel.getChapter_name());
                        intent.putExtra("chapter_pdf", chapModel.getChapter_pdf());
                        startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            if (ChapList != null) {
                return ChapList.size();
            } else {
                return 0;
            }
            //  return productList.size();
        }
    }
}
