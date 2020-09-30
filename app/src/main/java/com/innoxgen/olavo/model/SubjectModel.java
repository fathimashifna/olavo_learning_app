package com.innoxgen.olavo.model;

/**
 * Created by Fathima Shifna K on 03-09-2020.
 */
public class SubjectModel {
    private String id,course_id,subname,image,type,status,datacount;
    public SubjectModel(String course_id,String subname,String image,String status)
    {
        this.course_id=course_id;
        this.subname=subname;
        this.image=image;
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public SubjectModel(String id,String subname,String type,String image,String course_id,String datacount)
    {
        this.id=id;
        this.course_id=course_id;
        this.subname=subname;
        this.type=type;
        this.image=image;
        this.datacount=datacount;
    }

    public String getDatacount() {
        return datacount;
    }

    public void setDatacount(String datacount) {
        this.datacount = datacount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCourse_id() {
        return course_id;
    }

    public void setCourse_id(String course_id) {
        this.course_id = course_id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }
}
