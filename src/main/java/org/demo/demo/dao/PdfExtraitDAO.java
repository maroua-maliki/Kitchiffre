package org.demo.demo.dao;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.demo.demo.config.ExcelUtil;
import org.demo.demo.entities.Fichier;
import org.demo.demo.entities.PdfExtrait;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PdfExtraitDAO {

    private final File excelFile = ExcelUtil.getPdfExtraitFile();

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
            Sheet sheet = wb.createSheet("pdf_extrait");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("id");
            header.createCell(1).setCellValue("contenu");
            header.createCell(2).setCellValue("id_fichier");
            return sheet;
        }
    }

    public void save(PdfExtrait extrait) {
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
            extrait.setId(newId);

            int newRowNum = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(newRowNum);
            row.createCell(0).setCellValue(extrait.getId());
            row.createCell(1).setCellValue(extrait.getContenu());
            if (extrait.getFichier() != null) {
                row.createCell(2).setCellValue(extrait.getFichier().getId());
            } else {
                row.createCell(2).setCellValue("");
            }

            saveWorkbook(wb);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<PdfExtrait> rechercherParContenu(String motCle) {
        List<PdfExtrait> resultats = new ArrayList<>();
        FichierDAO fichierDAO = new FichierDAO();

        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell contenuCell = row.getCell(1);
                if (contenuCell == null) continue;

                String contenu = switch (contenuCell.getCellType()) {
                    case STRING -> contenuCell.getStringCellValue();
                    case NUMERIC -> String.valueOf(contenuCell.getNumericCellValue());
                    case BOOLEAN -> String.valueOf(contenuCell.getBooleanCellValue());
                    case FORMULA -> {
                        try {
                            yield contenuCell.getStringCellValue();
                        } catch (Exception e) {
                            yield contenuCell.getCellFormula();
                        }
                    }
                    default -> "";
                };

                if (contenu.toLowerCase().contains(motCle.toLowerCase())) {

                    int id = (int) row.getCell(0).getNumericCellValue();
                    int idFichier = 0;
                    Cell fichierCell = row.getCell(2);

                    if (fichierCell != null) {
                        if (fichierCell.getCellType() == CellType.NUMERIC) {
                            idFichier = (int) fichierCell.getNumericCellValue();
                        } else if (fichierCell.getCellType() == CellType.STRING) {
                            try {
                                idFichier = Integer.parseInt(fichierCell.getStringCellValue());
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }

                    Fichier fichier = null;
                    if (idFichier > 0) {
                        fichier = fichierDAO.findById(idFichier);
                    }

                    PdfExtrait extrait = new PdfExtrait();
                    extrait.setId(id);
                    extrait.setContenu(contenu);
                    extrait.setFichier(fichier);

                    resultats.add(extrait);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultats;
    }

}
