package com.innoxgen.olavo.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.innoxgen.olavo.others.BaseClass;
import com.innoxgen.olavo.activity.SubjectActivity;
import com.innoxgen.olavo.R;
import com.innoxgen.olavo.model.ClassVideoModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Fathima Shifna K on 22-07-2020.
 */
public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ClassVideoModel> dataArrayList;


    public GridViewAdapter(Context context, ArrayList<ClassVideoModel> dataArray) {
        this.context = context;
        this.dataArrayList = dataArray;
    }

    @Override
    public int getCount() {
        return dataArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHoder viewHoder;
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.grid_item, parent, false);

            viewHoder = new ViewHoder();

            viewHoder.item_name = convertView.findViewById(R.id.textView);
            viewHoder.img_item = convertView.findViewById(R.id.imageView);



            convertView.setTag(viewHoder);


        } else {
            viewHoder = (ViewHoder) convertView.getTag();
        }


        final ClassVideoModel dataModel = (ClassVideoModel) getItem(position);
        viewHoder.item_name.setText(dataModel.getClass_name());
        String img=dataModel.getImage();
        String imgurl= BaseClass.courseURL+img;
        String crs_id=dataModel.getCourse_id();
        Picasso.get()
                .load(imgurl)
                .into(viewHoder.img_item);
        viewHoder.img_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SubjectActivity.class).putExtra("course_id", crs_id)
                        );

            }
        });



        return convertView;

    }

    static class ViewHoder {
        ImageView img_item;
        TextView item_name;
        TextView price;
        TextView brand_name;



    }
}