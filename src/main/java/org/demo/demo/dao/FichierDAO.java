package org.demo.demo.dao;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.demo.demo.config.ExcelUtil;
import org.demo.demo.entities.Fichier;

import java.io.*;
import java.util.*;

public class FichierDAO {

    private final File excelFile = ExcelUtil.getFichierProduitExcelFile();

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
            header.createCell(1).setCellValue("nom_fichier");
            header.createCell(2).setCellValue("type_fichier");
            header.createCell(3).setCellValue("path");
            return sheet;
        }
    }

    public int save(Fichier fichier) {
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
            fichier.setId(newId);

            int newRowNum = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(newRowNum);
            row.createCell(0).setCellValue(fichier.getId());
            row.createCell(1).setCellValue(fichier.getNom_fichier());
            row.createCell(2).setCellValue(fichier.getType_fichier());
            row.createCell(3).setCellValue(fichier.getPath());

            saveWorkbook(wb);
            return newId;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean existsByFilename(String nomFichier) {
        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Cell cell = row.getCell(1);
                if (cell != null && cell.getCellType() == CellType.STRING) {
                    if (nomFichier.equalsIgnoreCase(cell.getStringCellValue())) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Fichier findByNom(String nomFichier) {
        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell nomCell = row.getCell(1);
                if (nomCell != null && nomFichier.equalsIgnoreCase(nomCell.getStringCellValue())) {
                    int id = (int) row.getCell(0).getNumericCellValue();
                    String type = row.getCell(2).getStringCellValue();
                    String path = row.getCell(3).getStringCellValue();
                    return new Fichier(id, nomFichier, type, path);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Fichier findById(int id) {
        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell idCell = row.getCell(0);
                if (idCell == null) continue;

                int rowId = -1;

                if (idCell.getCellType() == CellType.NUMERIC) {
                    rowId = (int) idCell.getNumericCellValue();
                } else if (idCell.getCellType() == CellType.STRING) {
                    try {
                        rowId = Integer.parseInt(idCell.getStringCellValue());
                    } catch (NumberFormatException e) {
                        System.out.println("Impossible de parser l'id : " + idCell.getStringCellValue());
                        continue;
                    }
                } else {
                    continue; // autre type non géré
                }

                if (rowId == id) {
                    String nom = row.getCell(1).getStringCellValue();
                    String type = row.getCell(2).getStringCellValue();
                    String path = row.getCell(3).getStringCellValue();
                    return new Fichier(rowId, nom, type, path);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Aucun fichier trouvé avec l'id : " + id);
        return null;
    }




}
