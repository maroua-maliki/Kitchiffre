package org.demo.demo.services;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.demo.demo.entities.Fichier;
import org.demo.demo.entities.ProduitExcel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.io.FilenameUtils.getExtension;

public class ExcelReaderService {

    public List<ProduitExcel> readProduitExcel(String filePath, Fichier fichier) throws IOException {
        List<ProduitExcel> produits = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook;
            String extension = getExtension(filePath).toLowerCase();

            if (extension.equals("xlsx") || extension.equals("xlsm")) {
                workbook = new XSSFWorkbook(fis);
            } else if (extension.equals("xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IOException("Format de fichier non pris en charge.");
            }

            Sheet sheet;
            int startRow;

            if (extension.equals("xlsm")) {
                sheet = workbook.getSheetAt(0);
                startRow = 3;
            } else {
                sheet = workbook.getSheetAt(2);
                startRow = 5;
            }

            int currentRow = 0;

            for (Row row : sheet) {
                if (currentRow >= startRow) {
                    Cell col1, col2, col3, col4;

                    if (extension.equals("xlsm")) {
                        col1 = row.getCell(0);
                        col2 = row.getCell(1);
                        col3 = row.getCell(2);
                        col4 = row.getCell(5);
                    } else {
                        col1 = row.getCell(0);
                        col2 = row.getCell(1);
                        col3 = row.getCell(148);
                        col4 = row.getCell(149);
                    }

                    if (col2 != null && !col2.toString().trim().isEmpty()) {
                        String nom = getCellValue(col1);
                        String description = getCellValue(col2);
                        double prixProto = parseDoubleSafe(getCellValue(col3));
                        double prixSerie = parseDoubleSafe(getCellValue(col4));

                        ProduitExcel produit = new ProduitExcel(nom, description, prixProto, prixSerie, fichier);
                        produits.add(produit);
                    }
                }
                currentRow++;
            }

            workbook.close();
        }

        return produits;
    }

    private String getCellValue(Cell cell) {
        return (cell == null) ? "" : cell.toString().trim();
    }

    private double parseDoubleSafe(String value) {
        try {
            return Double.parseDouble(value.replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
