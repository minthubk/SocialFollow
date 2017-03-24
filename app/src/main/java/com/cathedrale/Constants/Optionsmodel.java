package com.cathedrale.Constants;

/**
 * Created by Aspire on 2/5/2016.
 */
public class Optionsmodel {


    String questions, id, answers, score,color;

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Optionsmodel(String question__c, String id, String answer__c, String score__c, String color__c) {
        this.questions = question__c;
        this.id = id;
        this.answers = answer__c;
        this.score = score__c;
        this.color=color__c;

    }
}
