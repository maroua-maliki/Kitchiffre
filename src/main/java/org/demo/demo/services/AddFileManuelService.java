package org.demo.demo.services;

import org.demo.demo.dao.FichierDAO;
import org.demo.demo.dao.ProduitPdfManuelDAO;
import org.demo.demo.entities.Fichier;
import org.demo.demo.entities.ProduitPdfManuel;

public class AddFileManuelService {

    private final FichierDAO fichierDao = new FichierDAO();
    private final ProduitPdfManuelDAO produitDao = new ProduitPdfManuelDAO();

    // Nouvelle méthode : retourne un objet Fichier (plus seulement l’ID)
    public Fichier getOrCreateFichier(String nomFichier, String cheminFichier) throws Exception {
        Fichier fichierExistant = fichierDao.findByNom(nomFichier);
        if (fichierExistant != null) {
            return fichierExistant;
        }

        String typeFichier = "pdf"; // ou détecté dynamiquement selon le nom

        Fichier nouveauFichier = new Fichier(nomFichier, typeFichier, cheminFichier);
        int idFichier = fichierDao.save(nouveauFichier);

        if (idFichier == -1) {
            throw new Exception("Erreur lors de la création du fichier.");
        }

        nouveauFichier.setId(idFichier);
        return nouveauFichier;
    }

    public void enregistrerProduitPdfManuel(ProduitPdfManuel produit) throws Exception {
        if (produit == null) {
            throw new IllegalArgumentException("Le produit ne peut pas être null.");
        }

        int id = produitDao.save(produit);
        if (id == -1) {
            throw new Exception("Erreur lors de l'enregistrement du produit manuel.");
        }
    }
}
