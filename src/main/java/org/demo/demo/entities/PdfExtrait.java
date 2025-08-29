package org.demo.demo.entities;

public class PdfExtrait {
    private int id ;
    private String contenu;
    private Fichier fichier;

    public PdfExtrait(){

    }

    public PdfExtrait(int id, String contenu, Fichier fichier) {
        this.id = id;
        this.contenu = contenu;
        this.fichier = fichier;
    }

    public PdfExtrait(String contenu, Fichier fichier) {
        this.contenu = contenu;
        this.fichier = fichier;
    }


    public int getId() {
        return id;
    }

    public String getContenu() {
        return contenu;
    }

    public Fichier getFichier() {
        return fichier;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public void setFichier(Fichier fichier) {
        this.fichier = fichier;
    }

}
