package com.innoxgen.olavo.model;

/**
 * Created by Fathima Shifna K on 02-09-2020.
 */

public class ExamModel {
    private String id,question,optionA,optionB,optionC,optionD,correct_ans,explntn,answer,your_ans;
    private String date,time,title,chapter_pdf,chapter_name,type;
    private String no_of_qstn,timeslot;
    private String  qstn_id, topic,  no_of_question,  time_slot, start_date,  start_time, format_dateTime;
    public ExamModel(String id,String question,String optionA,String optionB,String optionC,String optionD,String correct_ans,String explntn,String your_ans)
    {
        this.id=id;
        this.question=question;
        this.optionA=optionA;
        this.optionB=optionB;
        this.optionC=optionC;
        this.optionD=optionD;
        this.correct_ans=correct_ans;
        this.explntn=explntn;
        this.your_ans=your_ans;
    }
    public  ExamModel(String qstn_id,String topic, String no_of_question,String  time_slot,String start_date, String start_time,String format_dateTime)
    {
        this.qstn_id = qstn_id;
        this.topic = topic;
        this.no_of_question = no_of_question;
        this.time_slot = time_slot;
        this.format_dateTime = format_dateTime;
        this.start_date = start_date;
        this.start_time = start_time;

    }
    public ExamModel(String id,String qstn,String date,String time) {
        this.id = id;
        this.question = qstn;
        this.date = date;
        this.time = time;
    }
    public ExamModel(String id,String answer)
    {
        this.id=id;
        this.answer=answer;
    }
    public ExamModel(String id,String title,String no_of_qstn,String timeslot,String type)
    {
        this.id=id;
        this.title=title;
        this.no_of_qstn=no_of_qstn;
        this.timeslot=timeslot;
        this.type=type;

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    public String getNo_of_qstn() {
        return no_of_qstn;
    }

    public void setNo_of_qstn(String no_of_qstn) {
        this.no_of_qstn = no_of_qstn;
    }

    public String getYour_ans() {
        return your_ans;
    }

    public void setYour_ans(String your_ans) {
        this.your_ans = your_ans;
    }

    public String getNo_of_question() {
        return no_of_question;
    }

    public String getQstn_id() {
        return qstn_id;
    }

    public String getTime_slot() {
        return time_slot;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getFormat_dateTime() {
        return format_dateTime;
    }

    public String getTopic() {
        return topic;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setFormat_dateTime(String format_dateTime) {
        this.format_dateTime = format_dateTime;
    }

    public void setNo_of_question(String no_of_question) {
        this.no_of_question = no_of_question;
    }

    public void setQstn_id(String qstn_id) {
        this.qstn_id = qstn_id;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public void setTime_slot(String time_slot) {
        this.time_slot = time_slot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public String getChapter_pdf() {
        return chapter_pdf;
    }

    public void setChapter_pdf(String chapter_pdf) {
        this.chapter_pdf = chapter_pdf;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrect_ans() {
        return correct_ans;
    }

    public void setCorrect_ans(String correct_ans) {
        this.correct_ans = correct_ans;
    }

    public String getExplntn() {
        return explntn;
    }

    public void setExplntn(String explntn) {
        this.explntn = explntn;
    }
}
