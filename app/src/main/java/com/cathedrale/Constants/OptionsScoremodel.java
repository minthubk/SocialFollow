package com.cathedrale.Constants;

/**
 * Created by Aspire on 2/12/2016.
 */
public class OptionsScoremodel {
    String answers;
    String score;
    String color;
    String selectedanswer;
    String selectedscore;

    public String getQuestionid() {
        return questionid;
    }

    public void setQuestionid(String questionid) {
        this.questionid = questionid;
    }

    String questionid;

    public String getSelectedscore() {
        return selectedscore;
    }

    public void setSelectedscore(String selectedscore) {
        this.selectedscore = selectedscore;
    }

    public String getSelectedanswer() {
        return selectedanswer;
    }

    public void setSelectedanswer(String selectedanswer) {
        this.selectedanswer = selectedanswer;
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

    public OptionsScoremodel(String answers, String score, String color, String questionid) {
        this.answers=answers;
        this.color=color;
        this.questionid = questionid;
        this.score=score;
    }
}
