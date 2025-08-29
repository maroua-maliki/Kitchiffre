package org.demo.demo.controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.demo.demo.entities.Utilisateur;

import java.io.IOException;

public class HomeController {

    @FXML
    private BorderPane root;
    @FXML
    private Pane navbarInclude;

    @FXML
    private Button importButton;

    @FXML
    private Button searchButton;

    @FXML
    private Button analyzeButton;

    @FXML
    private Button empButton;

    @FXML
    private VBox adminFeatureCard;
    @FXML
    private Button emppButton;

    private Utilisateur user;


    @FXML
    public void initialize() {
        adminFeatureCard.setVisible(false);
        adminFeatureCard.setManaged(false);
    }
    public void setUser(Utilisateur user) {
        this.user = user;
        FXMLLoader loader = (FXMLLoader) navbarInclude.getProperties().get("FXMLLoader");
        if (loader != null) {
            NavbarController navbarController = loader.getController();
            if (navbarController != null) {
                navbarController.setUser(user); // transmet l'utilisateur au Navbar
            }
        }

        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            adminFeatureCard.setVisible(true);
            adminFeatureCard.setManaged(true);
        } else {
            adminFeatureCard.setVisible(false);
            adminFeatureCard.setManaged(false);
        }

    }
    @FXML
    private void onManageEmployeesClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/demo/demo/manageEmployees.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) empButton.getScene().getWindow();
            // Maintenir la taille constante de 890x600
            Scene scene = new Scene(root, 890, 600);
            stage.setScene(scene);
            stage.setTitle("Gérer les Employés");
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }



    @FXML
    private void onImportButtonClick() {
        navigateToAddFile();
    }

    @FXML
    private void onSearchButtonClick() {
        navigateToSearch();
    }

    @FXML
    private void onAnalyzeButtonClick() {
        navigateToAddFileManuel();
    }

    private void navigateToAddFile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/demo/demo/addFile.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) importButton.getScene().getWindow();
            // Maintenir la taille constante de 890x600
            Scene scene = new Scene(root, 890, 600);
            stage.setScene(scene);
            stage.setTitle("Gérer les fichiers");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void navigateToSearch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/demo/demo/Recherche.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) searchButton.getScene().getWindow();
            // Maintenir la taille constante de 890x600
            Scene scene = new Scene(root, 890, 600);
            stage.setScene(scene);
            stage.setTitle("Accéder aux données");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void navigateToAddFileManuel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/demo/demo/addFileManuel.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) analyzeButton.getScene().getWindow();
            // Maintenir la taille constante de 890x600
            Scene scene = new Scene(root, 890, 600);
            stage.setScene(scene);
            stage.setTitle("Saisie Manuelle");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}