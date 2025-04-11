package net.blackcrate.bls_bh_xlsx;

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

public class XLSXOutputHandler {

    private final File xlsxFile;
    private final String sheetName;

    public XLSXOutputHandler(File xlsxFile, String sheetName) {
        this.xlsxFile = xlsxFile;
        this.sheetName = sheetName;
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

    public void write(List<BowlerRecord> records) {
        try (XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(true)) {
            Sheet sheet = workbook.createSheet(sheetName);
            DataFormat format = workbook.createDataFormat();

            XSSFFont FONT_HEADER = workbook.createFont();
            FONT_HEADER.setFontHeightInPoints((short) 14);
            FONT_HEADER.setBold(true);

            XSSFFont FONT_CELL = workbook.createFont();
            FONT_CELL.setFontHeightInPoints((short) 14);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(FONT_HEADER);

            CellStyle cellStyle = workbook.createCellStyle();
            cellStyle.setFont(FONT_CELL);

            CellStyle  cellCenteredStyle = workbook.createCellStyle();
            cellCenteredStyle.setFont(FONT_CELL);
            cellCenteredStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle cellDecimalStyle = workbook.createCellStyle();
            cellDecimalStyle.setFont(FONT_CELL);
            cellDecimalStyle.setAlignment(HorizontalAlignment.CENTER);
            cellDecimalStyle.setDataFormat(format.getFormat("#,##0.00"));

            
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

            try(OutputStream outputStream = new FileOutputStream(this.xlsxFile)) {
                workbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (EncryptedDocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}

/*
try (InputStream inp = new FileInputStream("workbook.xls")) {
    Workbook wb = WorkbookFactory.create(inp);
    Sheet sheet = wb.getSheetAt(0);
    Row row = sheet.getRow(2);
    Cell cell = row.getCell(3);

    if (cell == null)
        cell = row.createCell(3);

    cell.setCellType(CellType.STRING);
    cell.setCellValue("a test");

    // Write the output to a file
    try (OutputStream fileOut = new FileOutputStream("workbook.xls")) {
        wb.write(fileOut);
    }
}
 */