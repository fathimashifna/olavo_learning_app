package com.innoxgen.olavo.model;

/**
 * Created by Fathima Shifna K on 20-09-2020.
 */
public class ScoreModel {

    String user_id,name,score;
    public ScoreModel(String user_id,String name,String score)
    {
        this.user_id=user_id;
        this.name=name;
        this.score=score;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
