package com.innoxgen.olavo.model;

import java.util.Date;

/**
 * Created by Fathima Shifna K on 27-08-2020.
 */
public class MembershipModel {
    private String class_name,desc,image,mrp,price,course_id;
    private String start_date,end_date;

    public MembershipModel(String class_name,String desc,String image,String price)
    {
        this.class_name=class_name;
        this.desc=desc;
        this.image=image;
        this.price=price;

    }

    public MembershipModel(String course_id,String class_name,String desc,String mrp,String price,String start_date,String end_date)
    {
        this.course_id=course_id;
        this.class_name=class_name;
        this.desc=desc;
        this.mrp=mrp;
        this.price=price;
        this.start_date=start_date;
        this.end_date=end_date;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getPrice() {
        return price;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getMrp() {
        return mrp;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
