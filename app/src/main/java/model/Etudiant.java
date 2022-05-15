package model;

public class Etudiant {

    private  String ID;
    private String fullName;
    private String email;
    private String password;

    public Etudiant() {
    }

    public Etudiant(String fullName, String email, String password, String ID) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
