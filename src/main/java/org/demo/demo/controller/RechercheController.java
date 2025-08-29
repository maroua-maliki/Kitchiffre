package org.demo.demo.controller;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.demo.demo.entities.ProduitExcel;
import org.demo.demo.entities.PdfExtrait;
import org.demo.demo.entities.ProduitPdfManuel;
import org.demo.demo.services.AddFileService;
import org.demo.demo.services.RechercheService;

import java.awt.Desktop;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class RechercheController {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<ProduitExcel> resultTable;

    @FXML
    private TableColumn<ProduitExcel, String> nomColumn;

    @FXML
    private TableColumn<ProduitExcel, String> decoupageColumn;

    @FXML
    private TableColumn<ProduitExcel, String> nomFichierColumn;

    @FXML
    private ComboBox<String> typeComboBox;

    @FXML
    private TableColumn<ProduitExcel, Double> protoColumn;

    @FXML
    private TableColumn<ProduitExcel, Double> serieColumn;

    // Table PDF
    @FXML
    private TableView<PdfExtrait> pdfResultTable;

    @FXML
    private TableColumn<PdfExtrait, String> pdfRefColumn; // changé en String

    @FXML
    private TableColumn<PdfExtrait, String> pdfNomFichierColumn;

    @FXML
    private TableColumn<PdfExtrait, Void> pdfActionColumn;

    @FXML
    private Label loadingLabel;

    @FXML
    private Label noResultLabel;

    private RechercheService recherchService = new RechercheService();

    //manuel

    @FXML
    private TableView<ProduitPdfManuel> pdfManuelResultTable;

    @FXML
    private TableColumn<ProduitPdfManuel, String> pdfManuelRefColumn;

    @FXML
    private TableColumn<ProduitPdfManuel, String> pdfManuelDesignationColumn;

    @FXML
    private TableColumn<ProduitPdfManuel, Double> pdfManuelPrixColumn;

    @FXML
    private TableColumn<ProduitPdfManuel, String> pdfManuelNomFichierColumn;

    @FXML
    public void initialize() {

        resultTable.getStyleClass().add("excel-table");
        pdfResultTable.getStyleClass().add("pdf-table");
        pdfManuelResultTable.getStyleClass().add("manuel-table");

        // Colonnes Excel
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        decoupageColumn.setCellValueFactory(new PropertyValueFactory<>("decoupage"));
        nomFichierColumn.setCellValueFactory(new PropertyValueFactory<>("nomFichier"));
        protoColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaireProto"));
        serieColumn.setCellValueFactory(new PropertyValueFactory<>("prixUnitaireSerie"));
        resultTable.setVisible(false);

        pdfRefColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(searchField.getText())
        );

        pdfNomFichierColumn.setCellValueFactory(cellData -> {
            PdfExtrait extrait = cellData.getValue();
            String nomFichier = "";
            if (extrait.getFichier() != null) {
                nomFichier = extrait.getFichier().getNom_fichier();
            }
            return new javafx.beans.property.SimpleStringProperty(nomFichier);
        });

        addDownloadButtonToTable();

        pdfResultTable.setVisible(false);

        typeComboBox.setValue("Tout");

        resultTable.widthProperty().addListener((obs, oldVal, newVal) -> ajusterLargeurColonnes());
        pdfResultTable.widthProperty().addListener((obs, oldVal, newVal) -> ajusterLargeurColonnesPdf());

        //manuel
        pdfManuelRefColumn.setCellValueFactory(new PropertyValueFactory<>("ref"));
        pdfManuelDesignationColumn.setCellValueFactory(new PropertyValueFactory<>("designation"));
        pdfManuelPrixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        pdfManuelNomFichierColumn.setCellValueFactory(cellData -> {
            ProduitPdfManuel produit = cellData.getValue();
            String nomFichier = "";
            if (produit.getFichier() != null) {
                nomFichier = produit.getFichier().getNom_fichier();
            }
            return new javafx.beans.property.SimpleStringProperty(nomFichier);
        });

        pdfManuelNomFichierColumn.setPrefWidth(150);
        pdfManuelResultTable.setMinHeight(150);
        pdfManuelResultTable.setVisible(false);

        pdfManuelResultTable.widthProperty().addListener((obs, oldVal, newVal) -> ajusterLargeurColonnesManuel());
    }

    private void ajusterLargeurColonnes() {
        double totalWidth = resultTable.getWidth();
        double extraWidth = 60; // largeur supplémentaire pour la colonne fichier
        long visibleCols = resultTable.getColumns().stream().filter(TableColumn::isVisible).count();

        if (visibleCols == 0) return;

        double baseWidth = (totalWidth - extraWidth) / visibleCols;

        for (TableColumn<?, ?> col : resultTable.getColumns()) {
            if (!col.isVisible()) continue;

            if (col == nomFichierColumn) {
                col.setPrefWidth(baseWidth + extraWidth);
            } else {
                col.setPrefWidth(baseWidth);
            }
        }
    }


    private void ajusterLargeurColonnesPdf() {
        long colonnesVisibles = pdfResultTable.getColumns().stream().filter(TableColumn::isVisible).count();
        if (colonnesVisibles == 0) return;
        double largeurColonne = pdfResultTable.getWidth() / colonnesVisibles;
        for (TableColumn<?, ?> col : pdfResultTable.getColumns()) {
            if (col.isVisible()) col.setPrefWidth(largeurColonne);
        }
    }
    private void ajusterLargeurColonnesManuel() {
        long colonnesVisibles = pdfManuelResultTable.getColumns().stream().filter(TableColumn::isVisible).count();
        if (colonnesVisibles == 0) return;
        double largeurColonne = pdfManuelResultTable.getWidth() / colonnesVisibles;
        for (TableColumn<?, ?> col : pdfManuelResultTable.getColumns()) {
            if (col.isVisible()) col.setPrefWidth(largeurColonne);
        }
    }

    private void addDownloadButtonToTable() {
        pdfActionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<PdfExtrait, Void> call(final TableColumn<PdfExtrait, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Télécharger");

                    {
                        btn.setOnAction(event -> {
                            PdfExtrait extrait = getTableView().getItems().get(getIndex());
                            if (extrait.getFichier() != null) {
                                //Récupérer le chemin RELATIF enregistré en base
                                String relativePath = extrait.getFichier().getPath(); // Ex: pdfs/fichier.pdf

                                if (relativePath != null && !relativePath.isEmpty()) {
                                    try {
                                        //Trouver dynamiquement le dossier OneDrive partagé (Kitchiffre)
                                        Path basePath = AddFileService.findSharedKitchiffreFolder();
                                        if (basePath == null) {
                                            showAlert("Erreur", "Le dossier partagé 'Kitchiffre' est introuvable sur ce poste.");
                                            return;
                                        }

                                        //Reconstituer le chemin absolu à partir du chemin relatif
                                        Path fullPath = basePath.resolve(relativePath);
                                        File file = fullPath.toFile();

                                        if (file.exists()) {
                                            Desktop.getDesktop().open(file);
                                        } else {
                                            showAlert("Fichier introuvable", "Le fichier est introuvable : " + file.getAbsolutePath());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        showAlert("Erreur", "Impossible d'ouvrir le fichier.");
                                    }
                                } else {
                                    showAlert("Chemin manquant", "Le chemin du fichier n'est pas défini.");
                                }
                            } else {
                                showAlert("Fichier manquant", "Aucun fichier associé à cet extrait.");
                            }
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        });
    }



    private void showAlert(String titre, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onSearchClicked() {
        String keyword = searchField.getText();

        if (keyword == null || keyword.trim().isEmpty()) {
            // Champ vide → ne pas lancer la recherche
            resultTable.setVisible(false);
            pdfResultTable.setVisible(false);
            pdfManuelResultTable.setVisible(false);

            noResultLabel.setText("Veuillez saisir un mot-clé de recherche.");
            noResultLabel.setVisible(true);
            return;
        }

        String type = typeComboBox.getValue();
        loadingLabel.setVisible(true);
        noResultLabel.setVisible(false); // au cas où il était visible précédemment

        Task<Void> task = new Task<>() {
            List<ProduitExcel> results;
            List<PdfExtrait> pdfResults;
            List<ProduitPdfManuel> pdfManuelResults;

            @Override
            protected Void call() {
                results = recherchService.rechercherProduitsParDescription(keyword);
                pdfResults = recherchService.rechercherDansExtraitsPDF(keyword);
                pdfManuelResults = recherchService.rechercherProduitsPdfDepuisBase(keyword);
                return null;
            }

            @Override
            protected void succeeded() {
                resultTable.setItems(FXCollections.observableArrayList(results));
                pdfResultTable.setItems(FXCollections.observableArrayList(pdfResults));
                pdfManuelResultTable.setItems(FXCollections.observableArrayList(pdfManuelResults));

                boolean hasExcel = !results.isEmpty();
                boolean hasPdf = !pdfResults.isEmpty();
                boolean hasPdfManuel = !pdfManuelResults.isEmpty();

                resultTable.setVisible(hasExcel);
                pdfResultTable.setVisible(hasPdf);
                pdfManuelResultTable.setVisible(hasPdfManuel);

                if (!hasExcel && !hasPdf && !hasPdfManuel) {
                    noResultLabel.setText("Aucun résultat trouvé.");
                    noResultLabel.setVisible(true);
                }

                if (hasExcel) {
                    switch (type) {
                        case "Proto" -> {
                            protoColumn.setVisible(true);
                            serieColumn.setVisible(false);
                        }
                        case "Série" -> {
                            protoColumn.setVisible(false);
                            serieColumn.setVisible(true);
                        }
                        default -> {
                            protoColumn.setVisible(true);
                            serieColumn.setVisible(true);
                        }
                    }
                    ajusterLargeurColonnes();
                }

                if (hasPdf) {
                    ajusterLargeurColonnesPdf();
                    pdfResultTable.refresh();
                }

                if (hasPdfManuel) {
                    ajusterLargeurColonnesManuel();
                }

                loadingLabel.setVisible(false);
            }

            @Override
            protected void failed() {
                loadingLabel.setVisible(false);
                showAlert("Erreur", "Une erreur est survenue lors de la recherche.");
            }
        };

        new Thread(task).start();
    }

}