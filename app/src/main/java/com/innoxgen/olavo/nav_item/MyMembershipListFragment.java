package com.innoxgen.olavo.nav_item;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.model.MembershipModel;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyMembershipListFragment extends Fragment {

    MembershipModel membershipModel;
    RecyclerView recyclerView;
    String user_id,device_id;
    private List<MembershipModel> membershipModelList = new ArrayList<>();
    public static final String MY_PREFS_NAME = "LoginStatus";
    RecyclerviewAdapter recyclerviewAdapter;
    TextView txt_nodata;

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_packagelist, container, false);
        getActivity().setTitle("My Subscription");
        recyclerView=root.findViewById(R.id.recycleview_membership);
        txt_nodata=root.findViewById(R.id.txt_nodata);
        SharedPreferences sharedPref =getContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        user_id = sharedPref.getString("User_id", "12");
        device_id = Settings.Secure.getString(getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        if (isNetworkConnectionAvailable())
        {
            getcourseList(user_id,device_id);
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
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public void getcourseList(final String user_id,final String device_id ) {

        String url1 = BaseClass.mainURL+"get_user_course_plan";
//Log.e("url",""+url1);
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        membershipModelList.clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url1,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progress.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            String status=jsonObject.getString("result");
                            String data=jsonObject.getString("msg");
                            Log.e("status mem",""+response);
                            JSONObject jo= null;

                            if (status.equals("true"))
                            {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    jo = jsonArray.getJSONObject(i);
                                    String plan_status=jo.getString("plan_status");
                                    String course_id=jo.getString("course_id");
                                    String image=jo.getString("image");
                                    String price=jo.getString("price");
                                    String desc=jo.getString("description");
                                    String class_name=jo.getString("class_name");

                                    membershipModel = new MembershipModel(class_name,desc,image,price);
                                    membershipModelList.add(membershipModel);
                                }
                                recyclerviewAdapter= new RecyclerviewAdapter(membershipModelList);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(recyclerviewAdapter);

                            }
                            else
                            {
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
                        Log.e("Error-mem_alert",""+ error.getMessage());
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
        //   int socketTimeout = 30000;//30 seconds - change to what you want
        //  RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        //   stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

        private List<MembershipModel> modelList;
        URL url;
        URI uri;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public TextView txt_desc, txt_class_name,txt_mrp,txt_price;
            ImageView img;
            public MyViewHolder(View view) {
                super(view);
                txt_desc = view.findViewById(R.id.mem_description);
                txt_class_name = view.findViewById(R.id.class_name_mem);
                txt_mrp = view.findViewById(R.id.tv_mrp);
                txt_price = view.findViewById(R.id.tv_price);
                img=view.findViewById(R.id.img_course);

            }
        }


        public RecyclerviewAdapter(List<MembershipModel> modelList) {
            this.modelList = modelList;
        }

        @Override
        public RecyclerviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_membership, parent, false);

            return new RecyclerviewAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerviewAdapter.MyViewHolder holder, int position) {
            MembershipModel membershipModel = modelList.get(position);
            holder.txt_desc.setText(membershipModel.getDesc());
            holder.txt_class_name.setText(membershipModel.getClass_name());
           // holder.txt_mrp.setText("\u20B9"+membershipModel.getMrp());
           holder.txt_price.setText("\u20B9"+membershipModel.getPrice());
           // holder.txt_mrp.setPaintFlags(holder.txt_mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            String imgurl= BaseClass.courseURL+membershipModel.getImage();

            try {
                url = new URL(imgurl);
                uri = null;
                uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                url = uri.toURL();

            } catch (URISyntaxException | MalformedURLException e) {
                e.printStackTrace();
            }
            Picasso.get()
                    .load(imgurl)
                    .into(holder.img);


        }

        @Override
        public int getItemCount() {
            if (modelList != null) {
                return modelList.size();
            } else {
                return 0;
            }
            //  return productList.size();
        }
    }
}