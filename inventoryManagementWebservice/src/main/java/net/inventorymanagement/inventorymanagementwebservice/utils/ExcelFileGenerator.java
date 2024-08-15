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
        CellStyle headlineStyle = createCellStyle(workbook, 14, true, true, false, false, null);
        CellStyle subHeadlineStyle = createCellStyle(workbook, 12, true, false, false, false, null);
        CellStyle headerFullBorderStyle = createCellStyle(workbook, 11, false, false, true, true, null);
        CellStyle fullBorderStyle = createCellStyle(workbook, 11, false, false, false, true, null);
        CellStyle numericFullBorderStyle = createCellStyle(workbook, 11, false, false, false, true, "0");
        CellStyle dateFullBorderStyle = createCellStyle(workbook, 11, false, false, false, true, "dd.MM.yyyy");

        Sheet sheet = workbook.createSheet("Auswertung");

        int rowNum = 0;

        rowNum = writeTitle(headlineStyle, subHeadlineStyle, sheet, rowNum, filterString);

        rowNum = writeTableHeader(sheet, rowNum, headerFullBorderStyle);

        for (InventoryItem item : items) {
            Row itemRow = sheet.createRow(rowNum++);

            int cellNum = 0;
            cellNum = addStringCell(itemRow, cellNum, item.getItemInternalNumber(), fullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getType().getCategory().getCategoryName(), fullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getType().getTypeName(), fullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getItemName(), fullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getStatus(), fullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getOldItemNumber(), fullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getSerialNumber(), fullBorderStyle);
            cellNum = addLocalDateTimeCell(itemRow, cellNum, item.getWarrantyEndDate(), dateFullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getLocation().getLocationName(), fullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getDepartment().getDepartmentName(), fullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getRoom(), fullBorderStyle);
            cellNum = addLocalDateTimeCell(itemRow, cellNum, item.getFirstChange().getChangeDate(), dateFullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getSupplier().getSupplierName(), fullBorderStyle);
            cellNum = addLocalDateTimeCell(itemRow, cellNum, item.getDeliveryDate(), dateFullBorderStyle);
            cellNum = addIntegerCell(itemRow, cellNum, item.getPieces(), numericFullBorderStyle);
            cellNum = addIntegerCell(itemRow, cellNum, item.getPiecesStored(), numericFullBorderStyle);
            cellNum = addIntegerCell(itemRow, cellNum, item.getPiecesIssued(), numericFullBorderStyle);
            cellNum = addIntegerCell(itemRow, cellNum, item.getPiecesDropped(), numericFullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getIssuedTo(), fullBorderStyle);
            cellNum = addLocalDateTimeCell(itemRow, cellNum, item.getIssueDate(), dateFullBorderStyle);
            cellNum = addLocalDateTimeCell(itemRow, cellNum, item.getDroppingDate(), dateFullBorderStyle);
            cellNum = addStringCell(itemRow, cellNum, item.getDroppingReason(), fullBorderStyle);
            cellNum = addLocalDateTimeCell(itemRow, cellNum, item.getLastChange().getChangeDate(), dateFullBorderStyle);
            addStringCell(itemRow, cellNum, item.getComments(), fullBorderStyle);
        }

        ByteArrayOutputStream fos = new ByteArrayOutputStream();

        workbook.write(fos);
        return fos.toByteArray();
    }

    private int writeTitle(CellStyle headlineStyle, CellStyle subHeadlineStyle, Sheet sheet, int rowNum, String filterString) {
        DateTimeFormatter headlineFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

        // write company header
        Row headerRow1 = sheet.createRow(rowNum++);
        addStringCell(headerRow1, 0, "KBB - Kultur-Betriebe Burgenland GmbH", subHeadlineStyle);
        Row headerRow2 = sheet.createRow(rowNum++);
        addStringCell(headerRow2, 0, "Franz Schubert-Platz 6", null);
        Row headerRow3 = sheet.createRow(rowNum++);
        addStringCell(headerRow3, 0, "7000 Eisenstadt", null);

        rowNum++;

        // write title row
        Row titleRow = sheet.createRow(rowNum++);
        addStringCell(titleRow, 0, "Auswertung Inventarmanagement", headlineStyle);

        Row timestampRow = sheet.createRow(rowNum++);
        addStringCell(timestampRow, 0, "Erstellt am", null);
        addStringCell(timestampRow, 1, LocalDateTime.now().format(headlineFormatter) + " Uhr", null);

        Row filterRow = sheet.createRow(rowNum++);
        addStringCell(filterRow, 0, "Filter", null);
        addStringCell(filterRow, 1, filterString, null);

        return ++rowNum;
    }

    private static int writeTableHeader(Sheet sheet, int rowNum, CellStyle headerFullBorderStyle) {
        Row headerRow = sheet.createRow(rowNum++);
        int cellNum = 0;
        cellNum = addStringCell(headerRow, cellNum, "Inventarnummer", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Kategorie", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Typ", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Beschreibung", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Status", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Alte Inventarnummer", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Seriennummer", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Garantieablaufdatum", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Abteilung", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Standort", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Raum", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Anlagedatum", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Lieferant", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Lieferdatum", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Stück gesamt", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Stück lagernd", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Stück ausgegeben", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Stück ausgeschieden", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Ausgegeben an", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Ausgabedatum", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Ausscheidedatum", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Ausscheidegrund", headerFullBorderStyle);
        cellNum = addStringCell(headerRow, cellNum, "Letzte Änderung", headerFullBorderStyle);
        addStringCell(headerRow, cellNum, "Anmerkungen", headerFullBorderStyle);
        return rowNum;
    }

    private static CellStyle createCellStyle(Workbook workbook, int fontHeight, boolean bold, boolean underline, boolean fillGrey, boolean border, String dataFormat) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) fontHeight);
        if (bold) {
            font.setBold(true);
        }
        if (underline) {
            font.setUnderline(Font.U_SINGLE);
        }
        style.setFont(font);
        if (fillGrey) {
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        }
        if (border) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }

        if (dataFormat != null) {
            style.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat(dataFormat));
        }
        return style;
    }

    private static int addStringCell(Row row, int cellNum, String content, CellStyle cellStyle) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(content);
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        return ++cellNum;
    }

    private static int addIntegerCell(Row row, int cellNum, Integer content, CellStyle cellStyle) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(content);
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        return ++cellNum;
    }

    private static int addLocalDateTimeCell(Row row, int cellNum, LocalDateTime content, CellStyle cellStyle) {
        Cell cell = row.createCell(cellNum);
        cell.setCellValue(content);
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        return ++cellNum;
    }

}
