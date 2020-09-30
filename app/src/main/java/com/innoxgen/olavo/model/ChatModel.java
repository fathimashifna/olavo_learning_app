package com.innoxgen.olavo.model;

/**
 * Created by Fathima Shifna K on 03-09-2020.
 */
public class ChatModel {
    private String id, dbt_qstn_id, msg, user_type, ur_date, ur_time,file;
    public ChatModel(String id,String dbt_qstn_id,String msg,String user_type,String ur_date,String ur_time,String file)
    {
        this.id=id;
        this.dbt_qstn_id=dbt_qstn_id;
        this.msg=msg;
        this.user_type=user_type;
        this.ur_date=ur_date;
        this.ur_time=ur_time;
        this.file=file;

    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getId() {
        return id;
    }




    public String getDbt_qstn_id() {
        return dbt_qstn_id;
    }

    public String getMsg() {
        return msg;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getUr_date() {
        return ur_date;
    }

    public String getUr_time() {
        return ur_time;
    }

    public void setId(String id) {
        this.id = id;
    }



    public void setDbt_qstn_id(String dbt_qstn_id) {
        this.dbt_qstn_id = dbt_qstn_id;
    }

    public void setUr_date(String ur_date) {
        this.ur_date = ur_date;
    }

    public void setUr_time(String ur_time) {
        this.ur_time = ur_time;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }
}
