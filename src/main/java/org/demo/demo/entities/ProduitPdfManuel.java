package org.demo.demo.entities;

public class ProduitPdfManuel {

    private int id;
    private String ref;
    private double prix;
    private String designation;

    private Fichier fichier; // <-- Nouvel attribut

    // Constructeurs
    public ProduitPdfManuel(String ref, double prix, String designation, Fichier fichier) {
        this.ref = ref;
        this.prix = prix;
        this.designation = designation;
        this.fichier = fichier;
    }

    public ProduitPdfManuel(int id, String ref, double prix, String designation, Fichier fichier) {
        this.id = id;
        this.ref = ref;
        this.prix = prix;
        this.designation = designation;
        this.fichier = fichier;
    }

    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Fichier getFichier() {
        return fichier;
    }

    public void setFichier(Fichier fichier) {
        this.fichier = fichier;
    }

    @Override
    public String toString() {
        return "ProduitPdfManuel{" +
                "id=" + id +
                ", ref='" + ref + '\'' +
                ", prix=" + prix +
                ", designation='" + designation + '\'' +
                ", fichier=" + fichier +
                '}';
    }
}
