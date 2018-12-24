package com.kutumbita.app.bot.survey;


import java.util.ArrayList;

public class Survey {

    private String name;
    private ArrayList<Content> contents;


    public Survey() {

    }

    public Survey(String name, ArrayList<Content> contents) {

        this.name = name;
        this.contents = contents;

    }

    public static class Content {

        private String question, answerType, uuid;
        //private int weight;
        private ArrayList<Answer> answers = new ArrayList<>();

        public Content(String question, String answerType, String uuid, ArrayList<Answer> answers) {

            this.question = question;
            this.answerType = answerType;
            this.uuid = uuid;
            this.answers = answers;

        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public static class Answer {

            private String title;


            public Answer(String title) {

                this.title = title;


            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }
        }

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Content> getContents() {
        return contents;
    }

    public void setContents(ArrayList<Content> contents) {
        this.contents = contents;
    }


}
