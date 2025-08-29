package org.demo.demo.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.demo.demo.config.DatabaseUtil;
import org.demo.demo.dao.UtilisateurDAO;
import org.demo.demo.entities.Utilisateur;
import org.demo.demo.services.AuthService;
import org.demo.demo.session.UserSession;

import java.io.IOException;
import java.sql.Connection;
import java.util.Optional;
import org.demo.demo.config.ExcelUtil;
import java.io.File;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private AuthService authService;

    public void initialize() {
        try {
            File excelFile = ExcelUtil.getUtilisateurExcelFile();
            UtilisateurDAO userDAO = new UtilisateurDAO(excelFile);
            authService = new AuthService(userDAO);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger le fichier Excel.");
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Vérification email
        if (!username.matches("^[A-Za-z0-9._%+-]+@capgemini\\.com$")) {
            showAlert("Erreur", "L'adresse e-mail doit se terminer par @capgemini.com.");
            return;
        }

        Optional<Utilisateur> userOpt = authService.login(username, password);

        if (userOpt.isPresent()) {
            Utilisateur user = userOpt.get();
            // Définir l'utilisateur dans la session
            UserSession.getInstance().setCurrentUser(user);

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/demo/demo/home.fxml"));
                Parent homeView = loader.load();

                HomeController homeController = loader.getController();
                homeController.setUser(user);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                // Préserver la taille de la fenêtre (890x600)
                Scene scene = new Scene(homeView, 890, 600);
                stage.setScene(scene);
                stage.setTitle("KitChiffre");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible de charger la page d'accueil.");
            }

        } else {
            showAlert("Erreur", "Nom d'utilisateur ou mot de passe incorrect.");
            passwordField.clear();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR); // ⚡ corrigé : erreur au lieu d’information
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
