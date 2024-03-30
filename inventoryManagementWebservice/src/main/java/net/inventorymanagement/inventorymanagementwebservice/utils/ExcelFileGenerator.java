package net.inventorymanagement.inventorymanagementwebservice.utils;

import net.inventorymanagement.inventorymanagementwebservice.model.InventoryItem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelFileGenerator {

    public ExcelFileGenerator() {
    }

    public byte[] generateFile(List<InventoryItem> items, String filterString) throws IOException {
        Workbook workbook = new XSSFWorkbook();

        // create all the needed styles
        CellStyle headlineStyle = workbook.createCellStyle();
        Font headlineFont = workbook.createFont();
        headlineFont.setUnderline(Font.U_SINGLE);
        headlineFont.setBold(true);
        headlineFont.setFontHeightInPoints((short) 14);
        headlineStyle.setFont(headlineFont);

        CellStyle subHeadlineStyle = workbook.createCellStyle();
        Font subHeadlineFont = workbook.createFont();
        subHeadlineFont.setBold(true);
        subHeadlineFont.setFontHeightInPoints((short) 12);
        subHeadlineStyle.setFont(subHeadlineFont);

        CellStyle headerFullBorderStyle = workbook.createCellStyle();
        headerFullBorderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerFullBorderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerFullBorderStyle.setBorderTop(BorderStyle.THIN);
        headerFullBorderStyle.setBorderBottom(BorderStyle.THIN);
        headerFullBorderStyle.setBorderLeft(BorderStyle.THIN);
        headerFullBorderStyle.setBorderRight(BorderStyle.THIN);

        CellStyle fullBorderStyle = workbook.createCellStyle();
        fullBorderStyle.setBorderTop(BorderStyle.THIN);
        fullBorderStyle.setBorderBottom(BorderStyle.THIN);
        fullBorderStyle.setBorderLeft(BorderStyle.THIN);
        fullBorderStyle.setBorderRight(BorderStyle.THIN);

        CellStyle numericFullBorderStyle = workbook.createCellStyle();
        numericFullBorderStyle.setBorderTop(BorderStyle.THIN);
        numericFullBorderStyle.setBorderBottom(BorderStyle.THIN);
        numericFullBorderStyle.setBorderLeft(BorderStyle.THIN);
        numericFullBorderStyle.setBorderRight(BorderStyle.THIN);
        DataFormat numericDataFormat = workbook.createDataFormat();
        numericFullBorderStyle.setDataFormat(numericDataFormat.getFormat("0"));

        CellStyle dateFullBorderStyle = workbook.createCellStyle();
        dateFullBorderStyle.setBorderTop(BorderStyle.THIN);
        dateFullBorderStyle.setBorderBottom(BorderStyle.THIN);
        dateFullBorderStyle.setBorderLeft(BorderStyle.THIN);
        dateFullBorderStyle.setBorderRight(BorderStyle.THIN);
        dateFullBorderStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd.MM.yyyy"));

        Sheet sheet = workbook.createSheet("Auswertung");

        int rowNum = 0;

        rowNum = writeTitle(headlineStyle, subHeadlineStyle, sheet, rowNum, filterString);

        rowNum = writeTableHeader(sheet, rowNum, headerFullBorderStyle);

        for (InventoryItem item : items) {
            Row itemRow = sheet.createRow(rowNum++);

            Cell cell1 = itemRow.createCell(0);
            cell1.setCellValue(item.getItemInternalNumber());
            cell1.setCellStyle(fullBorderStyle);

            Cell cell2 = itemRow.createCell(1);
            cell2.setCellValue(item.getType().getCategory().getCategoryName());
            cell2.setCellStyle(fullBorderStyle);

            Cell cell3 = itemRow.createCell(2);
            cell3.setCellValue(item.getType().getTypeName());
            cell3.setCellStyle(fullBorderStyle);

            Cell cell4 = itemRow.createCell(3);
            cell4.setCellValue(item.getItemName());
            cell4.setCellStyle(fullBorderStyle);

            Cell cell5 = itemRow.createCell(4);
            cell5.setCellValue(item.getStatus());
            cell5.setCellStyle(fullBorderStyle);

            Cell cell6 = itemRow.createCell(5);
            cell6.setCellValue(item.getOldItemNumber());
            cell6.setCellStyle(fullBorderStyle);

            Cell cell7 = itemRow.createCell(6);
            cell7.setCellValue(item.getSerialNumber());
            cell7.setCellStyle(fullBorderStyle);

            Cell cell8 = itemRow.createCell(7);
            cell8.setCellValue(item.getDepartment().getDepartmentName());
            cell8.setCellStyle(fullBorderStyle);

            Cell cell9 = itemRow.createCell(8);
            cell9.setCellValue(item.getLocation().getLocationName());
            cell9.setCellStyle(fullBorderStyle);

            Cell cell10 = itemRow.createCell(9);
            cell10.setCellValue(item.getFirstChange().getChangeDate());
            cell10.setCellStyle(dateFullBorderStyle);

            Cell cell11 = itemRow.createCell(10);
            cell11.setCellValue(item.getSupplier().getSupplierName());
            cell11.setCellStyle(fullBorderStyle);

            Cell cell12 = itemRow.createCell(11);
            cell12.setCellValue(item.getIssueDate());
            cell12.setCellStyle(dateFullBorderStyle);

            Cell cell13 = itemRow.createCell(12);
            cell13.setCellValue(item.getPieces());
            cell13.setCellStyle(numericFullBorderStyle);

            Cell cell14 = itemRow.createCell(13);
            cell14.setCellValue(item.getPiecesStored());
            cell14.setCellStyle(numericFullBorderStyle);

            Cell cell15 = itemRow.createCell(14);
            cell15.setCellValue(item.getPiecesIssued());
            cell15.setCellStyle(numericFullBorderStyle);

            Cell cell16 = itemRow.createCell(15);
            cell16.setCellValue(item.getPiecesDropped());
            cell16.setCellStyle(numericFullBorderStyle);

            Cell cell17 = itemRow.createCell(16);
            cell17.setCellValue(item.getIssuedTo());
            cell17.setCellStyle(fullBorderStyle);

            Cell cell18 = itemRow.createCell(17);
            cell18.setCellValue(item.getIssueDate());
            cell18.setCellStyle(dateFullBorderStyle);

            Cell cell19 = itemRow.createCell(18);
            cell19.setCellValue(item.getDroppingDate());
            cell19.setCellStyle(dateFullBorderStyle);

            Cell cell20 = itemRow.createCell(19);
            cell20.setCellValue(item.getDroppingReason());
            cell20.setCellStyle(fullBorderStyle);

            Cell cell21 = itemRow.createCell(20);
            cell21.setCellValue(item.getLastChange().getChangeDate());
            cell21.setCellStyle(dateFullBorderStyle);

            Cell cell22 = itemRow.createCell(21);
            cell22.setCellValue(item.getComments());
            cell22.setCellStyle(fullBorderStyle);
        }

        ByteArrayOutputStream fos = new ByteArrayOutputStream();

        workbook.write(fos);
        return fos.toByteArray();
    }

    private int writeTitle(CellStyle headlineStyle, CellStyle subHeadlineStyle, Sheet sheet, int rowNum, String filterString) {
        DateTimeFormatter headlineFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        // write company header
        Row headerRow1 = sheet.createRow(rowNum++);
        Cell headerCell1 = headerRow1.createCell(0);
        headerCell1.setCellValue("KBB - Kultur-Betriebe Burgenland GmbH");
        headerCell1.setCellStyle(subHeadlineStyle);
        Row headerRow2 = sheet.createRow(rowNum++);
        Cell headerCell2 = headerRow2.createCell(0);
        headerCell2.setCellValue("Franz Schubert-Platz 6");
        Row headerRow3 = sheet.createRow(rowNum++);
        Cell headerCell3 = headerRow3.createCell(0);
        headerCell3.setCellValue("7000 Eisenstadt");

        rowNum++;

        // write title row
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell1 = titleRow.createCell(0);
        titleCell1.setCellValue("Auswertung Inventarmanagement");
        titleCell1.setCellStyle(headlineStyle);

        Row timestampRow = sheet.createRow(rowNum++);
        Cell timestampCell1 = timestampRow.createCell(0);
        timestampCell1.setCellValue("Erstellt am");
        Cell timestampCell2 = timestampRow.createCell(1);
        timestampCell2.setCellValue(LocalDateTime.now().format(headlineFormatter) + " Uhr");

        Row filterRow = sheet.createRow(rowNum++);
        Cell filterCell1 = filterRow.createCell(0);
        filterCell1.setCellValue("Filter");
        Cell filterCell2 = filterRow.createCell(1);
        filterCell2.setCellValue(filterString);

        rowNum++;
        return rowNum;
    }

    private static int writeTableHeader(Sheet sheet, int rowNum, CellStyle headerFullBorderStyle) {
        Row headerRow = sheet.createRow(rowNum++);

        Cell headerCell1 = headerRow.createCell(0);
        headerCell1.setCellValue("Inventarnummer");
        headerCell1.setCellStyle(headerFullBorderStyle);

        Cell headerCell2 = headerRow.createCell(1);
        headerCell2.setCellValue("Kategorie");
        headerCell2.setCellStyle(headerFullBorderStyle);

        Cell headerCell3 = headerRow.createCell(2);
        headerCell3.setCellValue("Typ");
        headerCell3.setCellStyle(headerFullBorderStyle);

        Cell headerCell4 = headerRow.createCell(3);
        headerCell4.setCellValue("Beschreibung");
        headerCell4.setCellStyle(headerFullBorderStyle);

        Cell headerCell5 = headerRow.createCell(4);
        headerCell5.setCellValue("Status");
        headerCell5.setCellStyle(headerFullBorderStyle);

        Cell headerCell6 = headerRow.createCell(5);
        headerCell6.setCellValue("Alte Inventarnummer");
        headerCell6.setCellStyle(headerFullBorderStyle);

        Cell headerCell7 = headerRow.createCell(6);
        headerCell7.setCellValue("Seriennummer");
        headerCell7.setCellStyle(headerFullBorderStyle);

        Cell headerCell8 = headerRow.createCell(7);
        headerCell8.setCellValue("Abteilung");
        headerCell8.setCellStyle(headerFullBorderStyle);

        Cell headerCell9 = headerRow.createCell(8);
        headerCell9.setCellValue("Standort");
        headerCell9.setCellStyle(headerFullBorderStyle);

        Cell headerCell10 = headerRow.createCell(9);
        headerCell10.setCellValue("Anlagedatum");
        headerCell10.setCellStyle(headerFullBorderStyle);

        Cell headerCell11 = headerRow.createCell(10);
        headerCell11.setCellValue("Lieferant");
        headerCell11.setCellStyle(headerFullBorderStyle);

        Cell headerCell12 = headerRow.createCell(11);
        headerCell12.setCellValue("Lieferdatum");
        headerCell12.setCellStyle(headerFullBorderStyle);

        Cell headerCell13 = headerRow.createCell(12);
        headerCell13.setCellValue("Stück gesamt");
        headerCell13.setCellStyle(headerFullBorderStyle);

        Cell headerCell14 = headerRow.createCell(13);
        headerCell14.setCellValue("Stück lagernd");
        headerCell14.setCellStyle(headerFullBorderStyle);

        Cell headerCell15 = headerRow.createCell(14);
        headerCell15.setCellValue("Stück ausgegeben");
        headerCell15.setCellStyle(headerFullBorderStyle);

        Cell headerCell16 = headerRow.createCell(15);
        headerCell16.setCellValue("Stück ausgeschieden");
        headerCell16.setCellStyle(headerFullBorderStyle);

        Cell headerCell17 = headerRow.createCell(16);
        headerCell17.setCellValue("Ausgegeben an");
        headerCell17.setCellStyle(headerFullBorderStyle);

        Cell headerCell18 = headerRow.createCell(17);
        headerCell18.setCellValue("Ausgabedatum");
        headerCell18.setCellStyle(headerFullBorderStyle);

        Cell headerCell19 = headerRow.createCell(18);
        headerCell19.setCellValue("Ausscheidedatum");
        headerCell19.setCellStyle(headerFullBorderStyle);

        Cell headerCell20 = headerRow.createCell(19);
        headerCell20.setCellValue("Ausscheidegrund");
        headerCell20.setCellStyle(headerFullBorderStyle);

        Cell headerCell21 = headerRow.createCell(20);
        headerCell21.setCellValue("Letzte Änderung");
        headerCell21.setCellStyle(headerFullBorderStyle);

        Cell headerCell22 = headerRow.createCell(21);
        headerCell22.setCellValue("Anmerkungen");
        headerCell22.setCellStyle(headerFullBorderStyle);

        return rowNum;
    }

}
