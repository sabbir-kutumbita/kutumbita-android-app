package com.kutumbita.app.model;

import java.io.Serializable;

public class Inbox implements Serializable {

    String uuId;
    MessageType messageType;
    Message message;

    public Inbox(String uuId, MessageType messageType, Message message){

        this.uuId=uuId;
        this.messageType=messageType;
        this.message=message;


    }

    public static class MessageType {


        String uuid, title, icon;

        public MessageType(String uuid, String title, String icon) {

            this.uuid = uuid;
            this.title = title;
            this.icon = icon;

        }

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public static class Message {

        String title, body, sentTime, timeZone, companyUuid, link, venue, date, time, image;

        public Message(String title, String body, String sentTime, String timeZone, String companyUuid, String link, String venue, String date, String time, String image) {

            this.title = title;
            this.body = body;
            this.sentTime = sentTime;
            this.timeZone = timeZone;
            this.companyUuid = companyUuid;
            this.link = link;
            this.venue = venue;
            this.date = date;
            this.time = time;
            this.image = image;


        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }

        public String getSentTime() {
            return sentTime;
        }

        public void setSentTime(String sentTime) {
            this.sentTime = sentTime;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(String timeZone) {
            this.timeZone = timeZone;
        }

        public String getCompanyUuid() {
            return companyUuid;
        }

        public void setCompanyUuid(String companyUuid) {
            this.companyUuid = companyUuid;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getVenue() {
            return venue;
        }

        public void setVenue(String venue) {
            this.venue = venue;
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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }

    public String getUuId() {
        return uuId;
    }

    public void setUuId(String uuId) {
        this.uuId = uuId;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }
}
