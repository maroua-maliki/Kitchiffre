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

public class PdfReaderService {

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

        for (File fichierPdf : fichiers) {
            try {
                if (fichierDAO.existsByFilename(fichierPdf.getName())) {
                    System.out.println("Fichier déjà traité : " + fichierPdf.getName());
                    continue;
                }

                String cheminFichier = fichierPdf.getAbsolutePath();

                Fichier fichierEntity = new Fichier(
                        fichierPdf.getName(),
                        "pdf",
                        cheminFichier
                );

                int fichierId = fichierDAO.save(fichierEntity);
                fichierEntity.setId(fichierId);

                String texte = extraireTexteCompletAvecOCR(fichierPdf);

                PdfExtrait extrait = new PdfExtrait();
                extrait.setContenu(texte);
                extrait.setFichier(fichierEntity);

                pdfExtraitDAO.save(extrait);

                System.out.println("✅ Fichier traité et enregistré : " + fichierPdf.getName());

            } catch (Exception e) {
                System.err.println("❌ Erreur avec le fichier : " + fichierPdf.getName());
                e.printStackTrace();
            }
        }
    }


    public String extraireTexteCompletAvecOCR(File fichierPdf) throws IOException, TesseractException {
        PDDocument document = PDDocument.load(fichierPdf);
        PDFRenderer renderer = new PDFRenderer(document);
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath(new File("tessdata").getAbsolutePath());

        StringBuilder contenuComplet = new StringBuilder();
        PDFTextStripper stripper = new PDFTextStripper();

        int nbPages = document.getNumberOfPages();

        for (int page = 0; page < nbPages; page++) {
            // Texte natif PDF
            stripper.setStartPage(page + 1);
            stripper.setEndPage(page + 1);
            String textePage = stripper.getText(document).trim();

            // OCR image
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
