package org.demo.demo.services;

import org.demo.demo.dao.FichierDAO;
import org.demo.demo.dao.ProduitExcelDAO;
import org.demo.demo.dao.PdfExtraitDAO;
import org.demo.demo.entities.Fichier;
import org.demo.demo.entities.ProduitExcel;
import org.demo.demo.entities.PdfExtrait;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class AddFileService {

    private final ExcelReaderService excelReaderService = new ExcelReaderService();
    private final PdfReaderService pdfReaderService = new PdfReaderService();
    private final FichierDAO fichierDAO = new FichierDAO();

    public Path copyFileToResources(String filePath) throws IOException {
        String extension = getExtension(filePath);

        String subFolder;
        switch (extension) {
            case "xlsx":
            case "xls":
            case "xlsm":
                subFolder = "excel";
                break;
            case "pdf":
                subFolder = "pdfs";
                break;
            default:
                throw new IOException("Extension non supportée pour la copie : " + extension);
        }

        //Chercher dynamiquement le dossier partagé "Kitchiffre"
        Path kitchiffreFolder = findSharedKitchiffreFolder();
        if (kitchiffreFolder == null) {
            throw new IOException("Le dossier partagé 'Kitchiffre' est introuvable dans les utilisateurs Windows.");
        }

        Path destinationDir = kitchiffreFolder.resolve(subFolder);
        Files.createDirectories(destinationDir);

        File sourceFile = new File(filePath);
        Path destinationFile = destinationDir.resolve(sourceFile.getName());

        Files.copy(sourceFile.toPath(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        return destinationFile;
    }

    public static Path findSharedKitchiffreFolder() {
        File usersRoot = new File("C:\\Users");
        File[] userDirs = usersRoot.listFiles(File::isDirectory);

        if (userDirs != null) {
            for (File userDir : userDirs) {

                File onedrive = new File(userDir, "OneDrive - Capgemini");
                File kitchiffre = new File(onedrive, "Kitchiffre");
                if (kitchiffre.exists() && kitchiffre.isDirectory()) {
                    return kitchiffre.toPath();
                }


                File capgemini = new File(userDir, "Capgemini");
                if (capgemini.exists()) {
                    File[] capgeminiSubDirs = capgemini.listFiles(File::isDirectory);
                    if (capgeminiSubDirs != null) {
                        for (File subDir : capgeminiSubDirs) {
                            if (subDir.getName().toLowerCase().contains("kitchiffre")) {
                                return subDir.toPath();
                            }
                        }
                    }
                }
            }
        }

        return null;
    }


    public String saveFichierVersBD(String filePath) throws Exception {
        File file = new File(filePath);
        String nomComplet = file.getName();
        String nomFichier = nomComplet.contains(".")
                ? nomComplet.substring(0, nomComplet.lastIndexOf('.'))
                : nomComplet;
        String extension = getExtension(filePath);

        if (fichierDAO.existsByFilename(nomFichier)) {
            throw new Exception("Le fichier a déjà été ajouté.");
        }

        Path copiedPath = copyFileToResources(filePath);

        Path basePath = findSharedKitchiffreFolder();
        Path relativePath = basePath.relativize(copiedPath);
        Fichier fichier = new Fichier(nomFichier, extension, relativePath.toString());

        int idFichier = fichierDAO.save(fichier);
        fichier.setId(idFichier);

        if (extension.equals("xlsx") || extension.equals("xls") || extension.equals("xlsm")) {
            List<ProduitExcel> produits = excelReaderService.readProduitExcel(filePath, fichier);
            ProduitExcelDAO produitDAO = new ProduitExcelDAO();
            for (ProduitExcel produit : produits) {
                produitDAO.save(produit);
            }
            return produits.size() + " produit(s) enregistrés depuis Excel.";
        } else if (extension.equals("pdf")) {
            // Traitement PDF
            String textePdf = pdfReaderService.extraireTexteCompletAvecOCR(new File(filePath));
            PdfExtrait extrait = new PdfExtrait();
            extrait.setFichier(fichier);
            extrait.setContenu(textePdf);
            new PdfExtraitDAO().save(extrait);
            return "PDF analysé et extrait enregistré avec succès.";
        } else {
            throw new Exception("Type de fichier non supporté : " + extension);
        }
    }

    private String getExtension(String filePath) {
        return filePath.substring(filePath.lastIndexOf(".") + 1).toLowerCase();
    }
}
