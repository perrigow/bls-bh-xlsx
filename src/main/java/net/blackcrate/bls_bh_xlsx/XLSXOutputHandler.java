package net.blackcrate.bls_bh_xlsx;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSXOutputHandler implements Closeable {

    private final File xlsxFile;
    private final XSSFWorkbook workbook;

    private final CellStyle headerStyle;
    private final CellStyle cellStyle;
    private final CellStyle  cellCenteredStyle;
    private final CellStyle cellDecimalStyle;

    public XLSXOutputHandler(File xlsxFile) throws IOException, EncryptedDocumentException {
        this.xlsxFile = xlsxFile;

        if (xlsxFile.exists()) {
            workbook = (XSSFWorkbook) WorkbookFactory.create(xlsxFile);
        } else {
            workbook = (XSSFWorkbook) WorkbookFactory.create(true);
        }

        DataFormat dataFormat = workbook.createDataFormat();

        XSSFFont headerFont = workbook.createFont();
        headerFont.setFontHeightInPoints((short) 14);
        headerFont.setBold(true);

        XSSFFont cellFont = workbook.createFont();
        cellFont.setFontHeightInPoints((short) 14);

        headerStyle = workbook.createCellStyle();
        headerStyle.setFont(headerFont);

        cellStyle = workbook.createCellStyle();
        cellStyle.setFont(cellFont);

        cellCenteredStyle = workbook.createCellStyle();
        cellCenteredStyle.setFont(cellFont);
        cellCenteredStyle.setAlignment(HorizontalAlignment.CENTER);

        cellDecimalStyle = workbook.createCellStyle();
        cellDecimalStyle.setFont(cellFont);
        cellDecimalStyle.setAlignment(HorizontalAlignment.CENTER);
        cellDecimalStyle.setDataFormat(dataFormat.getFormat("#,##0.00"));

    }
    
    private void writeCell(Sheet sheet, Row row, int index, float value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        sheet.autoSizeColumn(index);
    }

    private void writeCell(Sheet sheet, Row row, int index, int value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        sheet.autoSizeColumn(index);
    }

    private void writeCell(Sheet sheet, Row row, int index, String value, CellStyle style) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value.trim().concat(" "));
        cell.setCellStyle(style);
        sheet.autoSizeColumn(index);
    }

    public void writeSheet(String sheetName, List<BowlerRecord> records) {
        Sheet sheet = workbook.createSheet(sheetName);

        int rowIndex = 0;
        int colIndex = 0;
        for (BowlerRecord record : records) {
            List<BowlerStatistic> stats = Stream.concat(
                                                    record.gamesStats.stream(),
                                                    record.seriesStats.stream()
                                                )
                                                .collect(Collectors.toList());
            if (rowIndex == 0) {
                Row header = sheet.createRow(rowIndex++);
                writeCell(sheet, header, colIndex++, "Name", headerStyle);
                for (BowlerStatistic stat : stats) {
                    writeCell(sheet, header, colIndex++, stat.category, headerStyle);
                }
                writeCell(sheet, header, colIndex, "Final Avg", headerStyle);
            }

            colIndex = 0;
            Row row = sheet.createRow(rowIndex++);
            writeCell(sheet, row, colIndex++, record.name, cellStyle);
            for (BowlerStatistic stat : stats) {
                writeCell(sheet, row, colIndex++, stat.count(), cellCenteredStyle);
            }
            writeCell(sheet, row, colIndex, record.avg(), cellDecimalStyle);
        }

        try(OutputStream outputStream = new FileOutputStream(xlsxFile)) {
            workbook.write(outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        if (workbook != null) {
            workbook.close();
        }
    }
}
