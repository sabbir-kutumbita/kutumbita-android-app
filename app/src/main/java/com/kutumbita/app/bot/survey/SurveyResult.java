package com.kutumbita.app.bot.survey;

import java.io.Serializable;

public class SurveyResult implements Serializable {

    String question_uuid;
    int[] answers;

    public SurveyResult(){


    }

    public SurveyResult(String question_uuid, int[] answers) {
        this.question_uuid = question_uuid;
        this.answers = answers;
    }

    public String getQuestion_uuid() {
        return question_uuid;
    }

    public void setQuestion_uuid(String question_uuid) {
        this.question_uuid = question_uuid;
    }

    public int[] getAnswers() {
        return answers;
    }

    public void setAnswers(int[] answers) {
        this.answers = answers;
    }
}
