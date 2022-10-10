package com.celes.lnctassist;

public class Complaints {
    public String subject, complaint, suggestions, userUID, compID;

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getComplaint() {
        return complaint;
    }

    public void setComplaint(String complaint) {
        this.complaint = complaint;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }

    public String getCompID() {return compID; }

    public void setCompID(String compID) {this.compID = compID; }


    public Complaints(){

    }

    public Complaints(String subject, String complaint, String suggestions, String userUID, String compID) {
        this.subject = subject;
        this.complaint = complaint;
        this.suggestions = suggestions;
        this.userUID = userUID;
        this.compID = compID;
    }
}
