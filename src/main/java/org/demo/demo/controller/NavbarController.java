package org.demo.demo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.demo.demo.entities.Utilisateur;
import org.demo.demo.session.UserSession;

import java.io.IOException;

public class NavbarController {


    @FXML
    private Button homeButton;

    @FXML
    private Button addFileButton;

    @FXML
    private Button searchButton;

    @FXML
    private Button addFileManuelButton;

    @FXML
    private Button emppButton;

    @FXML
    private Button logoutButton;

    private Utilisateur user;


    @FXML
    public void initialize() {
        // Vérifier la session utilisateur et masquer le bouton si nécessaire
        Utilisateur currentUser = UserSession.getInstance().getCurrentUser();
        if (currentUser == null || !"ADMIN".equalsIgnoreCase(currentUser.getRole())) {
            emppButton.setVisible(false);
        } else {
            emppButton.setVisible(true);
        }

        // Configuration des actions des boutons
        homeButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/demo/demo/home.fxml"));
                Parent root = loader.load();

                // Récupérer l'utilisateur de la session et le transmettre au HomeController
                HomeController homeController = loader.getController();
                Utilisateur sessionUser = UserSession.getInstance().getCurrentUser();
                if (sessionUser != null) {
                    homeController.setUser(sessionUser);
                }

                Stage stage = (Stage) homeButton.getScene().getWindow();
                // Maintenir la taille constante de 890x600
                Scene scene = new Scene(root, 890, 600);
                stage.setScene(scene);
                stage.setTitle("Page d'Acceuil");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        addFileButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/demo/demo/addFile.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) addFileButton.getScene().getWindow();
                // Maintenir la taille constante de 890x600
                Scene scene = new Scene(root, 890, 600);
                stage.setScene(scene);
                stage.setTitle("Gérer les Fichiers");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        searchButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/demo/demo/Recherche.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) searchButton.getScene().getWindow();
                // Maintenir la taille constante de 890x600
                Scene scene = new Scene(root, 890, 600);
                stage.setScene(scene);
                stage.setTitle("Accéder aux Données");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        addFileManuelButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/demo/demo/addFileManuel.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) addFileManuelButton.getScene().getWindow();
                // Maintenir la taille constante de 890x600
                Scene scene = new Scene(root, 890, 600);
                stage.setScene(scene);
                stage.setTitle("Saisie Manuelle");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Action pour le bouton de déconnexion
        logoutButton.setOnAction(e -> {
            try {
                // Effacer la session utilisateur
                UserSession.getInstance().clearSession();

                // Rediriger vers la page de connexion
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/demo/demo/login.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) logoutButton.getScene().getWindow();
                // Maintenir la taille constante de 890x600
                Scene scene = new Scene(root, 890, 600);
                stage.setScene(scene);
                stage.setTitle("Connexion - KitChiffre");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        // Action pour le bouton de gestion des employés
        emppButton.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/demo/demo/manageEmployees.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) emppButton.getScene().getWindow();
                // Maintenir la taille constante de 890x600
                Scene scene = new Scene(root, 890, 600);
                stage.setScene(scene);
                stage.setTitle("Gérer les Employés");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }
    public void setUser(Utilisateur user) {
        this.user = user;

        if (user != null && "ADMIN".equalsIgnoreCase(user.getRole())) {
            emppButton.setVisible(true);
        } else {
            emppButton.setVisible(false);
        }

    }

}