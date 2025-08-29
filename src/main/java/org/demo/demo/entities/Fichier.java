package org.demo.demo.entities;

public class Fichier {
    private int id;
    private String nom_fichier;
    private String type_fichier;
    private String path;  // <-- nouvel attribut

    public Fichier() {
    }

    // Constructeur avec tous les champs
    public Fichier(int id, String nom_fichier, String type_fichier, String path) {
        this.id = id;
        this.nom_fichier = nom_fichier;
        this.type_fichier = type_fichier;
        this.path = path;
    }

    // Constructeur sans id (pour création avant insertion en base)
    public Fichier(String nom_fichier, String type_fichier, String path) {
        this.nom_fichier = nom_fichier;
        this.type_fichier = type_fichier;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public String getNom_fichier() {
        return nom_fichier;
    }

    public String getType_fichier() {
        return type_fichier;
    }

    public String getPath() {
        return path;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom_fichier(String nom_fichier) {
        this.nom_fichier = nom_fichier;
    }

    public void setType_fichier(String type_fichier) {
        this.type_fichier = type_fichier;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
