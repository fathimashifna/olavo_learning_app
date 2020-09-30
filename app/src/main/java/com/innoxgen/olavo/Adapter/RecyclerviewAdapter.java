package com.innoxgen.olavo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.innoxgen.olavo.activity.VimeVideoActivity;
import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.activity.VideoViewActivity;
import com.innoxgen.olavo.model.ClassVideoModel;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * Created by Fathima Shifna K on 20-07-2020.
 */
public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.MyViewHolder> {

    private List<ClassVideoModel> vdeoList;
    String encodedurl;
    URL url, vdeurl;
    URI uri, vdeuri;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subject_name, class_name;
        public ImageView image_view;
        public LinearLayout linear_layout_1;

        public MyViewHolder(View view) {
            super(view);
            subject_name = view.findViewById(R.id.subject_name);
            class_name = view.findViewById(R.id.class_name);
            image_view = view.findViewById(R.id.image_view_thm);
            linear_layout_1 = view.findViewById(R.id.linear_layout_1);

        }
    }


    public RecyclerviewAdapter(Context context, List<ClassVideoModel> videoList) {
        this.context = context;
        this.vdeoList = videoList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_video, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        ClassVideoModel classVideoModel = vdeoList.get(position);
        holder.subject_name.setText(classVideoModel.getSubject_name());
        holder.class_name.setText(classVideoModel.getClass_name());
        String subname = classVideoModel.getClass_name() + "-" + classVideoModel.getSubject_name();
        String vdename = classVideoModel.getVideo();
        String img = classVideoModel.getImage();
        String imgurl = BaseClass.videoImgURL + img;
        String vdeURL = BaseClass.videoURL + vdename;

        try {
            url = new URL(imgurl);
            vdeurl = new URL(vdeURL);
            uri = null;
            uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            url = uri.toURL();
            vdeuri = null;
            vdeuri = new URI(vdeurl.getProtocol(), vdeurl.getUserInfo(), vdeurl.getHost(), vdeurl.getPort(), vdeurl.getPath(), vdeurl.getQuery(), vdeurl.getRef());
            vdeurl = vdeuri.toURL();
        } catch (URISyntaxException | MalformedURLException e) {
            e.printStackTrace();
        }

        //Log.e("TEST", String.valueOf(url));

        Picasso.get()
                .load(String.valueOf(url)).fit()
                .into(holder.image_view);

        holder.linear_layout_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = classVideoModel.getLink();
                if (!link.equals(" ")) {
                   // Log.e("LINK-1", "" + vdeURL);
                    Intent intent = new Intent(context, VimeVideoActivity.class);
                    intent.putExtra("video_uri", link)
                            .putExtra("ClassName", subname);
                    context.startActivity(intent);
                } else {
                    context.startActivity(new Intent(context, VideoViewActivity.class).putExtra("video_uri", vdeURL)
                            .putExtra("ClassName", subname));
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