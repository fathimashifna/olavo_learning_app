package com.innoxgen.olavo.Adapter;

/**
 * Created by Fathima Shifna K on 01-09-2020.
 */
public class RowItem {

    private String clsId;
    private String Title;

    public RowItem(String Title, String clsId) {

        this.Title = Title;
        this.clsId = clsId;
    }

    public String getTitle() {

        return Title;
    }

    public void setTitle(String Title) {

        this.Title = Title;
    }

    public String getClsId() {
        return clsId;
    }

    public void setClsId(String clsId) {
        this.clsId = clsId;
    }

    @Override
    public String toString() {
        return Title;
    }
}