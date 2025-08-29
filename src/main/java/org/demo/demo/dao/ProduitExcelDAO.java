package org.demo.demo.dao;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.demo.demo.config.ExcelUtil;
import org.demo.demo.entities.Fichier;
import org.demo.demo.entities.ProduitExcel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitExcelDAO {

    private final File excelFile = ExcelUtil.getProduitExcelFile();

    private Workbook loadWorkbook() throws IOException {
        if (excelFile.exists()) {
            try (FileInputStream fis = new FileInputStream(excelFile)) {
                return new XSSFWorkbook(fis);
            }
        } else {
            return new XSSFWorkbook();
        }
    }

    private void saveWorkbook(Workbook wb) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(excelFile)) {
            wb.write(fos);
        }
    }

    private Sheet getOrCreateSheet(Workbook wb) {
        if (wb.getNumberOfSheets() > 0) {
            return wb.getSheetAt(0);
        } else {
            Sheet sheet = wb.createSheet("produit_excel");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("id");
            header.createCell(1).setCellValue("nom");
            header.createCell(2).setCellValue("decoupage");
            header.createCell(3).setCellValue("prix_unitaire_proto");
            header.createCell(4).setCellValue("prix_unitaire_serie");
            header.createCell(5).setCellValue("id_fichier");
            return sheet;
        }
    }


    public void save(ProduitExcel produitExcel) {
        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);

            int lastId = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    int id = (int) cell.getNumericCellValue();
                    if (id > lastId) lastId = id;
                }
            }

            int newId = lastId + 1;
            produitExcel.setId(newId);

            int newRowNum = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(newRowNum);
            row.createCell(0).setCellValue(produitExcel.getId());
            row.createCell(1).setCellValue(produitExcel.getNom());
            row.createCell(2).setCellValue(produitExcel.getDecoupage());
            row.createCell(3).setCellValue(produitExcel.getPrixUnitaireProto());
            row.createCell(4).setCellValue(produitExcel.getPrixUnitaireSerie());
            row.createCell(5).setCellValue(produitExcel.getFichier().getId());

            saveWorkbook(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<ProduitExcel> rechercherParDecoupage(String description) {
        List<ProduitExcel> produits = new ArrayList<>();
        FichierDAO fichierDAO = new FichierDAO();

        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String decoupage = row.getCell(2).getStringCellValue();
                if (decoupage.toLowerCase().contains(description.toLowerCase())) {
                    int id = (int) row.getCell(0).getNumericCellValue();
                    String nom = row.getCell(1).getStringCellValue();
                    double prixProto = row.getCell(3).getNumericCellValue();
                    double prixSerie = row.getCell(4).getNumericCellValue();
                    int idFichier = (int) row.getCell(5).getNumericCellValue();

                    Fichier fichier = fichierDAO.findById(idFichier); // méthode à ajouter dans FichierDAO

                    ProduitExcel produit = new ProduitExcel(id, nom, decoupage, prixProto, prixSerie, fichier);
                    produits.add(produit);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return produits;
    }

}
