package org.demo.demo.dao;

import org.demo.demo.entities.Utilisateur;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

public class UtilisateurDAO {

    private final File excelFile;

    public UtilisateurDAO(File excelFile) {
        this.excelFile = excelFile;
    }

    private Workbook loadWorkbook() throws IOException {
        if (excelFile.exists()) {
            try (FileInputStream fis = new FileInputStream(excelFile)) {
                return new XSSFWorkbook(fis);
            }
        } else {
            return new XSSFWorkbook(); // nouveau fichier Excel vide
        }
    }

    private void saveWorkbook(Workbook wb) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(excelFile)) {
            wb.write(fos);
        }
    }

    // Nouvelle méthode pour récupérer la première feuille ou la créer si aucune
    private Sheet getOrCreateSheet(Workbook wb) {
        Sheet sheet;
        if (wb.getNumberOfSheets() > 0) {
            sheet = wb.getSheetAt(0);
        } else {
            sheet = wb.createSheet("users");
            // Créer ligne d'en-tête
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("id");
            header.createCell(1).setCellValue("username");
            header.createCell(2).setCellValue("password_hash");
            header.createCell(3).setCellValue("role");
        }
        return sheet;
    }

    public Optional<Utilisateur> findByUsername(String username) {
        List<Utilisateur> users = getAllUtilisateurs();
        return users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public boolean save(Utilisateur user) {
        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);

            int lastId = 0;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                int id = (int) row.getCell(0).getNumericCellValue();
                if (id > lastId) lastId = id;
            }
            int newId = lastId + 1;
            user.setId(newId);

            int newRowNum = sheet.getLastRowNum() + 1;
            Row row = sheet.createRow(newRowNum);
            row.createCell(0).setCellValue(user.getId());
            row.createCell(1).setCellValue(user.getUsername());
            row.createCell(2).setCellValue(user.getPasswordHash());
            row.createCell(3).setCellValue(user.getRole());

            saveWorkbook(wb);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Utilisateur> getAllUtilisateurs() {
        List<Utilisateur> users = new ArrayList<>();
        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int id = (int) row.getCell(0).getNumericCellValue();
                String username = row.getCell(1).getStringCellValue();
                String passwordHash = row.getCell(2).getStringCellValue();
                String role = row.getCell(3).getStringCellValue();

                users.add(new Utilisateur(id, username, passwordHash, role));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean updateUtilisateur(Utilisateur user) {
        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);

            boolean found = false;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int id = (int) row.getCell(0).getNumericCellValue();
                if (id == user.getId()) {
                    row.getCell(1).setCellValue(user.getUsername());
                    row.getCell(2).setCellValue(user.getPasswordHash());
                    row.getCell(3).setCellValue(user.getRole());
                    found = true;
                    break;
                }
            }
            if (found) {
                saveWorkbook(wb);
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUtilisateur(int userId) {
        try (Workbook wb = loadWorkbook()) {
            Sheet sheet = getOrCreateSheet(wb);

            boolean found = false;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int id = (int) row.getCell(0).getNumericCellValue();
                if (id == userId) {
                    int lastRowNum = sheet.getLastRowNum();
                    if (i >= 0 && i < lastRowNum) {
                        sheet.shiftRows(i + 1, lastRowNum, -1);
                    } else if (i == lastRowNum) {
                        Row removingRow = sheet.getRow(i);
                        if (removingRow != null) {
                            sheet.removeRow(removingRow);
                        }
                    }
                    found = true;
                    break;
                }
            }
            if (found) {
                saveWorkbook(wb);
                return true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
