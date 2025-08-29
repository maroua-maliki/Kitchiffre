package org.demo.demo.entities;

public class ProduitExcel {
    private int id;
    private String nom;
    private String decoupage;
    private double prixUnitaireProto;
    private double prixUnitaireSerie;
    private Fichier fichier;

    public ProduitExcel(int id, String nom, String decoupage, double prixUnitaireProto, double prixUnitaireSerie, Fichier fichier) {
        this.id = id;
        this.nom = nom;
        this.decoupage = decoupage;
        this.prixUnitaireProto = prixUnitaireProto;
        this.prixUnitaireSerie = prixUnitaireSerie;
        this.fichier = fichier;
    }

    public ProduitExcel(String nom, String decoupage, double prixUnitaireProto, double prixUnitaireSerie, Fichier fichier) {
        this.nom = nom;
        this.decoupage = decoupage;
        this.prixUnitaireProto = prixUnitaireProto;
        this.prixUnitaireSerie = prixUnitaireSerie;
        this.fichier = fichier;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDecoupage() {
        return decoupage;
    }

    public double getPrixUnitaireProto() {
        return prixUnitaireProto;
    }

    public double getPrixUnitaireSerie() {
        return prixUnitaireSerie;
    }

    public Fichier getFichier() {
        return fichier;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDecoupage(String decoupage) {
        this.decoupage = decoupage;
    }

    public void setPrixUnitaireProto(double prixUnitaireProto) {
        this.prixUnitaireProto = prixUnitaireProto;
    }

    public void setPrixUnitaireSerie(double prixUnitaireSerie) {
        this.prixUnitaireSerie = prixUnitaireSerie;
    }

    public void setFichier(Fichier fichier) {
        this.fichier = fichier;
    }

    public String getNomFichier() {
        if (fichier != null) {
            return fichier.getNom_fichier();  // adapte selon le getter exact de ta classe Fichier
        }
        return "";
    }
}

