package com.innoxgen.olavo.model;

/**
 * Created by Fathima Shifna K on 15-09-2020.
 */
public class ChapModel {
    private String title,chapter_pdf,chapter_name,type;
    public ChapModel(String title,String chapter_pdf,String chapter_name,String type)
    {
        this.title=title;
        this.chapter_pdf=chapter_pdf;
        this.chapter_name=chapter_name;
        this.type=type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getChapter_pdf() {
        return chapter_pdf;
    }

    public void setChapter_pdf(String chapter_pdf) {
        this.chapter_pdf = chapter_pdf;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }




}
