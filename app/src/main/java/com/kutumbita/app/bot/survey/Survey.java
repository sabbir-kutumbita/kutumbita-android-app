package com.kutumbita.app.bot.survey;


import java.util.ArrayList;

public class Survey {






    private String qId, uuId, qNo, question, weight, answerType;
    private ArrayList<Answer> answers;


    public Survey() {

    }

    public Survey(String qId, String uuId, String qNo, String question, String weight, String answerType, ArrayList<Answer> answers) {

        this.qId = qId;
        this.uuId = uuId;
        this.qNo = qNo;
        this.question = question;
        this.weight = weight;
        this.answerType = answerType;
        this.answers = answers;

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

    public String getqId() {
        return qId;
    }

    public void setqId(String qId) {
        this.qId = qId;
    }

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public String getqNo() {
        return qNo;
    }

    public void setqNo(String qNo) {
        this.qNo = qNo;
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

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
    }
}
