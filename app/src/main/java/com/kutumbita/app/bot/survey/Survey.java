package com.kutumbita.app.bot.survey;


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

    public static class Answer {

        private String title, score, next;
        //private int weight;


        public Answer(String title, String score, String next) {

            this.title = title;
            this.score = score;
            this.next = next;

        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getNext() {
            return next;
        }

        public void setNext(String next) {
            this.next = next;
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
