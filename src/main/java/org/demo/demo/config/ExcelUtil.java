package org.demo.demo.config;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class ExcelUtil {

    private static final Path BASE_PATH = findSharedKitchiffreFolder();

    public static File getUtilisateurExcelFile() {
        return BASE_PATH.resolve("users.xlsx").toFile();
    }

    public static File getFichierProduitExcelFile() {
        return BASE_PATH.resolve("fichier_produit.xlsx").toFile();
    }

    public static File getPdfExtraitFile() {
        return BASE_PATH.resolve("pdf_extrait.xlsx").toFile();
    }

    public static File getProduitExcelFile() {
        return BASE_PATH.resolve("produit_excel.xlsx").toFile();
    }

    public static File getProduitPdfFile() {
        return BASE_PATH.resolve("produit_pdf.xlsx").toFile();
    }

    private static Path findSharedKitchiffreFolder() {
        File usersRoot = new File("C:\\Users");
        File[] userDirs = usersRoot.listFiles(File::isDirectory);

        if (userDirs != null) {
            for (File userDir : userDirs) {
                // Cas 1 : OneDrive - Capgemini
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

        throw new RuntimeException("📁 Dossier partagé 'Kitchiffre' introuvable sur cette machine.");
    }
}
