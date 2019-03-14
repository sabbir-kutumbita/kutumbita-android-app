package com.kutumbita.app.chat.model;


import java.util.ArrayList;

public class Survey {


    private String id, type, survey_uuid, question_no, question, weight, answer_type;
    private ArrayList<Answer> answers;
    private ArrayList<Answer> user_answer;
    private boolean isEnd;


    public Survey() {

    }

    public Survey(String id, String type, String survey_uuid, String question_no, String question, String weight, String answer_type, ArrayList<Answer> answers, boolean isEnd) {

        this.id = id;
        this.type = type;
        this.survey_uuid = survey_uuid;
        this.question_no = question_no;
        this.question = question;
        this.weight = weight;
        this.answer_type = answer_type;
        this.answers = answers;
        this.isEnd = isEnd;

    }

    public Survey(String question, String type, String answer_type, ArrayList<Answer> answers) {

        this.question = question;
        this.type = type;
        this.answer_type = answer_type;
        this.answers = answers;


    }

    public static class Answer {

        private String title, scoreOrEvent, nextOrSurveyId;
        //private int weight;


        public Answer(String title, String scoreOrEvent, String nextOrSurveyId) {

            this.title = title;
            this.scoreOrEvent = scoreOrEvent;
            this.nextOrSurveyId = nextOrSurveyId;

        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getScoreOrEvent() {
            return scoreOrEvent;
        }

        public void setScoreOrEvent(String scoreOrEvent) {
            this.scoreOrEvent = scoreOrEvent;
        }

        public String getNextOrSurveyId() {
            return nextOrSurveyId;
        }

        public void setNextOrSurveyId(String nextOrSurveyId) {
            this.nextOrSurveyId = nextOrSurveyId;
        }
    }

    public ArrayList<Answer> getUser_answer() {
        return user_answer;
    }

    public void setUser_answer(ArrayList<Answer> user_answer) {
        this.user_answer = user_answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSurvey_uuid() {
        return survey_uuid;
    }

    public void setSurvey_uuid(String survey_uuid) {
        this.survey_uuid = survey_uuid;
    }

    public String getQuestion_no() {
        return question_no;
    }

    public void setQuestion_no(String question_no) {
        this.question_no = question_no;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getAnswer_type() {
        return answer_type;
    }

    public void setAnswer_type(String answer_type) {
        this.answer_type = answer_type;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }

    public boolean isEnd() {
        return isEnd;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setEnd(boolean end) {
        isEnd = end;
    }
}
