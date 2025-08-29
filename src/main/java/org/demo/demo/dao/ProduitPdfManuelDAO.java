package org.demo.demo.dao;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.demo.demo.config.ExcelUtil;
import org.demo.demo.entities.Fichier;
import org.demo.demo.entities.ProduitPdfManuel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitPdfManuelDAO {

    private final File excelFile = ExcelUtil.getProduitPdfFile();

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
            Sheet sheet = wb.createSheet("produit_pdf");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("id");
            header.createCell(1).setCellValue("ref");
            header.createCell(2).setCellValue("prix");
            header.createCell(3).setCellValue("designation");
            header.createCell(4).setCellValue("fichier_id");
            return sheet;
        }
    }

    public int save(ProduitPdfManuel produit) {
        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);

            int lastId = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell idCell = row.getCell(0);
                if (idCell != null && idCell.getCellType() == CellType.NUMERIC) {
                    int id = (int) idCell.getNumericCellValue();
                    if (id > lastId) lastId = id;
                }
            }

            int newId = lastId + 1;
            produit.setId(newId);

            Row newRow = sheet.createRow(sheet.getLastRowNum() + 1);
            newRow.createCell(0).setCellValue(produit.getId());
            newRow.createCell(1).setCellValue(produit.getRef());
            newRow.createCell(2).setCellValue(produit.getPrix());
            newRow.createCell(3).setCellValue(produit.getDesignation());
            if (produit.getFichier() != null) {
                newRow.createCell(4).setCellValue(produit.getFichier().getId());
            } else {
                newRow.createCell(4).setCellValue("");
            }

            saveWorkbook(wb);
            return newId;

        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<ProduitPdfManuel> rechercheManuel(String keyword) {
        List<ProduitPdfManuel> result = new ArrayList<>();
        FichierDAO fichierDAO = new FichierDAO();

        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String ref = getCellAsString(row.getCell(1));
                String designation = getCellAsString(row.getCell(3));
                double prix = row.getCell(2).getNumericCellValue();

                boolean match = (ref != null && ref.toLowerCase().contains(keyword.toLowerCase()))
                        || (designation != null && designation.toLowerCase().contains(keyword.toLowerCase()));

                if (match) {
                    int id = (int) row.getCell(0).getNumericCellValue();
                    int fichierId = 0;

                    Cell fichierCell = row.getCell(4);
                    if (fichierCell != null) {
                        if (fichierCell.getCellType() == CellType.NUMERIC) {
                            fichierId = (int) fichierCell.getNumericCellValue();
                        } else if (fichierCell.getCellType() == CellType.STRING) {
                            try {
                                fichierId = Integer.parseInt(fichierCell.getStringCellValue());
                            } catch (NumberFormatException ignored) {}
                        }
                    }

                    Fichier fichier = fichierId > 0 ? fichierDAO.findById(fichierId) : null;

                    ProduitPdfManuel produit = new ProduitPdfManuel(id, ref, prix, designation, fichier);
                    result.add(produit);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private String getCellAsString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                try {
                    yield cell.getStringCellValue();
                } catch (Exception e) {
                    yield cell.getCellFormula();
                }
            }
            default -> "";
        };
    }
}
