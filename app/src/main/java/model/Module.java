package model;

public class Module {

    private String intitule, professeur, description, volumeHoraire, id;

    public Module() {
    }

    public Module(String intitule, String professeur, String description, String volumeHoraire, String id) {
        this.intitule = intitule;
        this.professeur = professeur;
        this.description = description;
        this.volumeHoraire = volumeHoraire;
        this.id = id;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getProfesseur() {
        return professeur;
    }

    public void setProfesseur(String professeur) {
        this.professeur = professeur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVolumeHoraire() {
        return volumeHoraire;
    }

    public void setVolumeHoraire(String volumeHoraire) {
        this.volumeHoraire = volumeHoraire;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
