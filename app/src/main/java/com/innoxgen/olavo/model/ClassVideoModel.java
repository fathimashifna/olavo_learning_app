package com.innoxgen.olavo.model;

/**
 * Created by Fathima Shifna K on 25-08-2020.
 */
public class ClassVideoModel {
    private String course_id,subject_id,chapter_id,video,subject_name,class_name,title,chap_name,type,id,link;
    private String image,chap_pdf,no_of_qstn,timeslot;
    public ClassVideoModel()
    {}
    public ClassVideoModel(String course_id,String image,String video,String subject_name,String class_name,String link)
    {
        this.image=image;
        this.video=video;
        this.subject_name=subject_name;
        this.class_name=class_name;
        this.course_id=course_id;
        this.link=link;

    }

    public ClassVideoModel(String course_id,String class_name,String img)
    {
        this.course_id=course_id;
        this.image=img;
        this.class_name=class_name;
    }
    public ClassVideoModel(String id,String title,String video,String image,String chap_name,String type,String link)
    {
        this.id=id;
        this.title=title;
        this.video=video;
        this.chap_name=chap_name;
        this.type=type;
        this.image=image;
        this.link=link;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getNo_of_qstn() {
        return no_of_qstn;
    }

    public void setNo_of_qstn(String no_of_qstn) {
        this.no_of_qstn = no_of_qstn;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    public String getChap_pdf() {
        return chap_pdf;
    }

    public void setChap_pdf(String chap_pdf) {
        this.chap_pdf = chap_pdf;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChap_name() {
        return chap_name;
    }

    public void setChap_name(String chap_name) {
        this.chap_name = chap_name;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(String subject_id) {
        this.subject_id = subject_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getImage() {
        return image;
    }

    public void setSubject_name(String subject_name) {
        this.subject_name = subject_name;
    }

    public String getSubject_name() {
        return subject_name;
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
}
