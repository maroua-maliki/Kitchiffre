package org.demo.demo.services;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.demo.demo.dao.FichierDAO;
import org.demo.demo.dao.PdfExtraitDAO;
import org.demo.demo.entities.Fichier;
import org.demo.demo.entities.PdfExtrait;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Essay {

    private final PdfExtraitDAO pdfExtraitDAO = new PdfExtraitDAO();
    private final FichierDAO fichierDAO = new FichierDAO();

    public void traiterTousLesPdfs(String dossierPath) {
        File dossier = new File(dossierPath);

        if (!dossier.exists() || !dossier.isDirectory()) {
            System.out.println("Dossier non trouvé ou invalide : " + dossierPath);
            return;
        }

        File[] fichiers = dossier.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

        if (fichiers == null || fichiers.length == 0) {
            System.out.println("Aucun fichier PDF trouvé dans : " + dossierPath);
            return;
        }

        // Dossier de destination : pdfs/
        File dossierDestination = new File("pdfs");
        if (!dossierDestination.exists()) {
            dossierDestination.mkdirs();
        }

        for (File fichierPdf : fichiers) {
            try {
                String nomComplet = fichierPdf.getName(); // ex: contrat_marseille.pdf
                String nomSansExtension = nomComplet.replaceFirst("(?i)\\.pdf$", ""); // contrat_marseille

                if (fichierDAO.existsByFilename(nomSansExtension)) {
                    System.out.println("Fichier déjà traité : " + nomComplet);
                    continue;
                }

                // Copier le fichier dans le dossier pdfs/
                File destination = new File(dossierDestination, nomComplet);
                Files.copy(fichierPdf.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Enregistrer le chemin relatif "pdfs/nom.pdf"
                String cheminRelatif = "pdfs/" + nomComplet;

                Fichier fichierEntity = new Fichier(
                        nomSansExtension,  // nom sans extension
                        "pdf",
                        cheminRelatif      // chemin relatif
                );

                int fichierId = fichierDAO.save(fichierEntity);
                fichierEntity.setId(fichierId);

                String texte = extraireTexteCompletAvecOCR(fichierPdf);

                PdfExtrait extrait = new PdfExtrait();
                extrait.setContenu(texte);
                extrait.setFichier(fichierEntity);

                pdfExtraitDAO.save(extrait);

                System.out.println("✅ Fichier traité et enregistré : " + nomComplet);

            } catch (Exception e) {
                System.err.println("❌ Erreur avec le fichier : " + fichierPdf.getName());
                e.printStackTrace();
            }
        }
    }

    private String extraireTexteCompletAvecOCR(File fichierPdf) throws IOException, TesseractException {
        PDDocument document = PDDocument.load(fichierPdf);
        PDFRenderer renderer = new PDFRenderer(document);
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("tessdata");

        StringBuilder contenuComplet = new StringBuilder();
        PDFTextStripper stripper = new PDFTextStripper();

        int nbPages = document.getNumberOfPages();

        for (int page = 0; page < nbPages; page++) {
            stripper.setStartPage(page + 1);
            stripper.setEndPage(page + 1);
            String textePage = stripper.getText(document).trim();

            BufferedImage image = renderer.renderImageWithDPI(page, 300);
            String ocrPage = tesseract.doOCR(image).trim();

            contenuComplet.append("Page ").append(page + 1).append(":\n");

            if (!textePage.isEmpty()) {
                contenuComplet.append("Texte natif:\n").append(textePage).append("\n");
            }
            if (!ocrPage.isEmpty()) {
                contenuComplet.append("Texte OCR:\n").append(ocrPage).append("\n");
            }
            contenuComplet.append("\n------------------------------\n\n");
        }

        document.close();
        return contenuComplet.toString();
    }
}
