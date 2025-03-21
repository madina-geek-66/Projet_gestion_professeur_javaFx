package sn.groupeisi.projetgestionprofesseurs.controllers;

import org.apache.poi.ss.usermodel.*;
import sn.groupeisi.projetgestionprofesseurs.dao.EmargementImpl;
import sn.groupeisi.projetgestionprofesseurs.entities.Emargement;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.util.stream.Stream;

public class ExportService {
    private final EmargementImpl emargementDao;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public ExportService() {
        this.emargementDao = new EmargementImpl();
    }


    public void exporterEmargementExcel(LocalDate dateDebut, LocalDate dateFin, String filePath) throws IOException {
        List<Emargement> emargements = emargementDao.findByDateRange(dateDebut, dateFin);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Émargements");

            // Style pour les en-têtes
            org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);
            headerCellStyle.setAlignment(HorizontalAlignment.CENTER);

            // Création des en-têtes
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Date", "Statut", "Professeur", "Cours"};
            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
            }

            // Remplissage des données
            int rowNum = 1;
            for (Emargement emargement : emargements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(emargement.getId());
                row.createCell(1).setCellValue(emargement.getDate().format(DATE_FORMATTER));
                row.createCell(2).setCellValue(emargement.getStatut());
                row.createCell(3).setCellValue(emargement.getProfesseur().getNom() + " " + emargement.getProfesseur().getPrenom());
                row.createCell(4).setCellValue(emargement.getCours().getNom());
            }

            // Ajustement de la largeur des colonnes
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Écriture du fichier
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        }
    }


    public void exporterEmargementPDF(LocalDate dateDebut, LocalDate dateFin, String filePath) throws IOException, DocumentException {
        List<Emargement> emargements = emargementDao.findByDateRange(dateDebut, dateFin);

        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Titre du document
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
        Paragraph title = new Paragraph("Rapport d'émargements", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Sous-titre avec la période
        Font subTitleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL);
        Paragraph subTitle = new Paragraph("Période du " + dateDebut.format(DATE_FORMATTER) +
                " au " + dateFin.format(DATE_FORMATTER), subTitleFont);
        subTitle.setAlignment(Element.ALIGN_CENTER);
        subTitle.setSpacingAfter(20);
        document.add(subTitle);

        // Création du tableau
        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);

        // En-têtes du tableau
        Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        Stream.of("ID", "Date", "Statut", "Professeur", "Cours")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, headerFont));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });

        // Ajout des données
        for (Emargement emargement : emargements) {
            table.addCell(String.valueOf(emargement.getId()));
            table.addCell(emargement.getDate().format(DATE_FORMATTER));
            table.addCell(emargement.getStatut());
            table.addCell(emargement.getProfesseur().getNom() + " " + emargement.getProfesseur().getPrenom());
            table.addCell(emargement.getCours().getNom());
        }

        document.add(table);
        document.close();
    }
}