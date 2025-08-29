package org.demo.demo.controller;

import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import org.demo.demo.services.AddFileService;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;

public class AddFileController {

    @FXML
    private Label Text;

    @FXML
    private TextField filePathField;

    @FXML
    private FontIcon loadingSpinner;

    private RotateTransition spinnerAnimation;

    private final AddFileService addFileService = new AddFileService();

    @FXML
    public void initialize() {
        spinnerAnimation = new RotateTransition(Duration.seconds(1), loadingSpinner);
        spinnerAnimation.setByAngle(360);
        spinnerAnimation.setCycleCount(RotateTransition.INDEFINITE);
    }

    @FXML
    protected void onDownloadButtonClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers Excel ou PDF", "*.xlsx", "*.xls", "*.xlsm", "*.pdf")
        );
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    protected void onAddButtonClick() {
        // Réinitialise le message à chaque clic
        Text.setText("");
        Text.setStyle("");

        // Affiche le spinner
        loadingSpinner.setVisible(true);
        spinnerAnimation.play();

        String filePath = filePathField.getText();

        if (filePath != null && !filePath.isEmpty()) {
            new Thread(() -> {
                try {
                    String result = addFileService.saveFichierVersBD(filePath);
                    Platform.runLater(() -> {
                        Text.setText("✅ " + result);
                        Text.setStyle("-fx-text-fill: green;");
                        loadingSpinner.setVisible(false);
                        spinnerAnimation.stop();
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    String msg = e.getMessage() != null ? e.getMessage() : "Erreur inconnue.";
                    Platform.runLater(() -> {
                        Text.setText("❌ " + msg);
                        Text.setStyle("-fx-text-fill: red;");
                        loadingSpinner.setVisible(false);
                        spinnerAnimation.stop();
                    });
                }
            }).start();
        } else {
            loadingSpinner.setVisible(false);
            spinnerAnimation.stop();
            Text.setText("Aucun fichier sélectionné.");
            Text.setStyle("-fx-text-fill: red;");
        }
    }

}
