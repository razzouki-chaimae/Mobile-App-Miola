package model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

// we implements Serializable so we can pass our object to a new activity
public class Professeur implements Serializable {

    //private String uid;
    private String fullName;
    private String departement;
    private String email;
    private String password;
    //private Bitmap profilPhoto;

    private String ID;
    //private int imageResource;
    //private Uri imageResource;

    public Professeur(){
    }

    public Professeur(String fullName, String departement, String email, String password, String id) {
        this.fullName = fullName;
        this.departement = departement;
        this.email = email;
        this.password = password;
        this.ID = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
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

    public String getId() {
        return ID;
    }

    public void setId(String id) {
        this.ID = id;
    }
}
