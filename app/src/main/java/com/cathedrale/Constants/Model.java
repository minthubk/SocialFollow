package com.cathedrale.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aspire on 1/5/2016.
 */
public class Model {

    private String id;
    private String order;
    private String questions;
    private String Week;
    private String answer;
    private String score;
    private String type;
    private boolean selectedanswer;
    private int seletedPostion;
    private int Score;

    private List<Optionsmodel> optionsmodelList = new ArrayList<>();

    private List<OptionsScoremodel> optionsScoreList = new ArrayList<>();


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Model(String id, String order, String questions, String Week, String type) {
        this.questions = questions;
        this.id=id;
        this.order=order;
        this.Week=Week;

        this.type=type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getWeek() {
        return Week;
    }

    public void setWeek(String week) {
        Week = week;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public boolean isSelectedanswer() {
        return selectedanswer;
    }

    public void setSelectedanswer(boolean selectedanswer) {
        this.selectedanswer = selectedanswer;
    }

    public List<Optionsmodel> getOptionsmodelList() {
        return optionsmodelList;
    }

    public void setOptionsmodelList(List<Optionsmodel> optionsmodelList) {
        this.optionsmodelList = optionsmodelList;
    }

    public int getSeletedPostion() {
        return seletedPostion;
    }

    public void setSeletedPostion(int seletedPostion) {
        this.seletedPostion = seletedPostion;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public List<OptionsScoremodel> getOptionsScoreList() {
        return optionsScoreList;
    }

    public void setOptionsScoreList(List<OptionsScoremodel> optionsScoreList) {
        this.optionsScoreList = optionsScoreList;
    }
}
