package org.demo.demo.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.geometry.Insets;
import org.demo.demo.dao.UtilisateurDAO;
import org.demo.demo.entities.Utilisateur;
import org.demo.demo.config.ExcelUtil;
import org.demo.demo.services.EmployeeService;

import java.io.File;
import java.util.List;

public class ManageEmployeesController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    @FXML
    private VBox employeeContainer;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    private EmployeeService employeeService;

    @FXML
    public void initialize() {
        try {
            File excelFile = ExcelUtil.getUtilisateurExcelFile();
            UtilisateurDAO utilisateurDAO = new UtilisateurDAO(excelFile);
            employeeService = new EmployeeService(utilisateurDAO);

            // Charger toute la liste au démarrage
            refreshEmployeeList();

            // Recherche en direct (optionnel)
            searchField.textProperty().addListener((obs, oldVal, newVal) -> handleSearch());

        } catch (Exception e) {
            statusLabel.setText("Erreur lors du chargement du fichier Excel");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddEmployee() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Veuillez remplir tous les champs !");
            return;
        }

        try {
            boolean success = employeeService.addEmployee(username, password);

            if(success) {
                statusLabel.setText("Employé ajouté avec succès !");
                usernameField.clear();
                passwordField.clear();
                refreshEmployeeList();
            } else {
                statusLabel.setText("Erreur lors de l'ajout de l'employé !");
            }

        } catch (IllegalArgumentException e) {
            statusLabel.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Erreur lors de l'ajout de l'employé !");
        }
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim().toLowerCase();

        try {
            List<Utilisateur> users = employeeService.getAllEmployees();

            List<Utilisateur> filteredUsers = users.stream()
                    .filter(user -> user.getUsername().toLowerCase().contains(keyword))
                    .toList();

            employeeContainer.getChildren().clear();
            for (Utilisateur user : filteredUsers) {
                HBox employeeRow = createEmployeeRow(user);
                employeeContainer.getChildren().add(employeeRow);
            }

            if (filteredUsers.isEmpty()) {
                statusLabel.setText("Aucun employé trouvé !");
            } else {
                statusLabel.setText(filteredUsers.size() + " employé(s) trouvé(s).");
            }

        } catch (Exception e) {
            statusLabel.setText("Erreur lors de la recherche !");
            e.printStackTrace();
        }
    }

    private void refreshEmployeeList() {
        try {
            List<Utilisateur> users = employeeService.getAllEmployees();
            employeeContainer.getChildren().clear();

            for (Utilisateur user : users) {
                HBox employeeRow = createEmployeeRow(user);
                employeeContainer.getChildren().add(employeeRow);
            }
        } catch (Exception e) {
            statusLabel.setText("Erreur lors du chargement de la liste !");
        }
    }

    private HBox createEmployeeRow(Utilisateur user) {
        HBox row = new HBox();
        row.getStyleClass().add("employee-row");
        row.setSpacing(10);
        row.setPadding(new Insets(5, 10, 5, 10));

        Label nameLabel = new Label(user.getUsername());
        nameLabel.getStyleClass().add("employee-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button editBtn = new Button("Modifier");
        editBtn.getStyleClass().add("action-button");
        editBtn.setTooltip(new Tooltip("Modifier le mot de passe"));
        editBtn.setOnAction(e -> handleEditEmployee(user.getUsername()));

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("action-button");
        deleteBtn.setTooltip(new Tooltip("Supprimer l'employé"));
        deleteBtn.setOnAction(e -> handleDeleteEmployee(user.getUsername()));

        row.getChildren().addAll(nameLabel, spacer, editBtn, deleteBtn);
        return row;
    }

    private void handleEditEmployee(String username) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Modifier l'employé");
        dialog.setHeaderText("Modifier le mot de passe de : " + username);
        dialog.setContentText("Nouveau mot de passe:");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword.trim().isEmpty()) {
                statusLabel.setText("Le mot de passe ne peut pas être vide !");
                return;
            }

            try {
                boolean success = employeeService.updateEmployeePassword(username, newPassword);
                if (success) {
                    statusLabel.setText("Mot de passe modifié avec succès !");
                } else {
                    statusLabel.setText("Erreur lors de la modification !");
                }
            } catch (Exception e) {
                statusLabel.setText("Erreur: " + e.getMessage());
            }
        });
    }

    private void handleDeleteEmployee(String username) {
        Alert confirmAlert = new Alert(AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer la suppression");
        confirmAlert.setHeaderText("Supprimer l'employé");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer l'employé : " + username + " ?");

        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean success = employeeService.deleteEmployeeByUsername(username);
                    if (success) {
                        statusLabel.setText("Employé supprimé avec succès !");
                        refreshEmployeeList();
                    } else {
                        statusLabel.setText("Erreur lors de la suppression !");
                    }
                } catch (Exception e) {
                    statusLabel.setText("Erreur: " + e.getMessage());
                }
            }
        });
    }
}