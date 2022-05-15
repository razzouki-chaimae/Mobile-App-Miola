package model;

public class Message {

    private String Professeur;
    private String dateMessage;
    private String Corps;

    public Message() {
    }

    public Message(String prof, String dateMessage, String message) {
        this.Professeur = prof;
        this.dateMessage = dateMessage;
        this.Corps = message;
    }

    public String getProfesseur() {
        return this.Professeur;
    }

    public void setProfesseur(String prof) {
        this.Professeur = prof;
    }

    public String getDateMessage() {
        return this.dateMessage;
    }

    public void setDateMessage(String dateMessage) {
        this.dateMessage = dateMessage;
    }

    public String getCorps() {
        return Corps;
    }

    public void setCorps(String message) {
        this.Corps = message;
    }
}
