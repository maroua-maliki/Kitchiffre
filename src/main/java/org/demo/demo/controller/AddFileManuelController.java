package org.demo.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.demo.demo.entities.Fichier;
import org.demo.demo.entities.ProduitPdfManuel;
import org.demo.demo.services.AddFileManuelService;

public class AddFileManuelController {

    @FXML
    private TextField refField;

    @FXML
    private TextField prixField;

    @FXML
    private TextField designationField;

    @FXML
    private TextField nomFichierField;

    @FXML
    private Button enregistrerButton;

    @FXML
    private Button annulerButton;

    private final AddFileManuelService service = new AddFileManuelService();

    @FXML
    public void initialize() {
        enregistrerButton.setOnAction(event -> enregistrerProduit());
    }

    private void enregistrerProduit() {
        String ref = refField.getText().trim();
        String prixText = prixField.getText().trim();
        String designation = designationField.getText().trim();
        String nomFichier = nomFichierField.getText().trim();

        if (prixText.isEmpty() || nomFichier.isEmpty()) {
            showAlert("Les champs 'prix' et 'nom du fichier' sont obligatoires.");
            return;
        }

        if (ref.isEmpty() && designation.isEmpty()) {
            showAlert("Veuillez remplir au moins la référence ou la désignation.");
            return;
        }

        double prix;
        try {
            prix = Double.parseDouble(prixText.replace(",", "."));
        } catch (NumberFormatException e) {
            showAlert("Le prix doit être un nombre valide.");
            return;
        }

        try {
            // Créer un objet Fichier (ajuste les valeurs par défaut selon ton besoin)
            Fichier fichier = service.getOrCreateFichier(nomFichier, null);

            ProduitPdfManuel produit = new ProduitPdfManuel(ref, prix, designation, fichier);
            service.enregistrerProduitPdfManuel(produit);

            showAlert("Produit enregistré avec succès !");
            clearFields();
        } catch (Exception e) {
            showAlert("Erreur : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        refField.clear();
        prixField.clear();
        designationField.clear();
        nomFichierField.clear();
    }

    @FXML
    private void onAnnulerClicked() {
        clearFields();  // Vide tous les champs
    }
}
