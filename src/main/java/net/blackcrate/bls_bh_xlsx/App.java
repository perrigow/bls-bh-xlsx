package net.blackcrate.bls_bh_xlsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.xml.sax.SAXException;

public class App {

    private void parseBowlerHistory(File filepath) {
        try (InputStream blsFile = new FileInputStream(filepath)) {
            PDFParser parser = new PDFParser();
            BLSContentHandler handler = new BLSContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            parser.parse(blsFile, handler, metadata, context);

            // for (BowlerRecord record : handler.records) {
            //     System.out.println(record + "\n");
            // }
            this.writeToExcelFile(handler.records, filepath.getName());
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }
    }

    private void writeToExcelFile(List<BowlerRecord> records, String filename) {
        try(Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();

            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont font = ((XSSFWorkbook) workbook).createFont();
            font.setFontName("Arial");
            font.setFontHeightInPoints((short) 16);
            font.setBold(true);
            headerStyle.setFont(font);

            CellStyle style = workbook.createCellStyle();
            font.setFontHeightInPoints((short) 12);
            font.setBold(false);
            style.setFont(font);

            CellStyle statStyle = style;
            statStyle.setAlignment(HorizontalAlignment.CENTER);
            
            int rowIndex = 0;
            int colIndex = 0;
            for (BowlerRecord record : records) {
                List<BowlerStatistic> stats = Stream.concat(record.gamesStats.stream(), record.seriesStats.stream()).collect(Collectors.toList());
                if (rowIndex == 0) {
                    Row header = sheet.createRow(rowIndex++);
                    Cell headerCell = header.createCell(colIndex++);
                    headerCell.setCellValue("Name");
                    headerCell.setCellStyle(headerStyle);
                    for (BowlerStatistic stat : stats) {
                        headerCell = header.createCell(colIndex++);
                        headerCell.setCellValue(stat.category);
                        headerCell.setCellStyle(headerStyle);
                    }
                    headerCell = header.createCell(colIndex);
                    headerCell.setCellValue("Final Avg");
                    headerCell.setCellStyle(headerStyle);
                }

                colIndex = 0;
                Row row = sheet.createRow(rowIndex++);
                Cell cell = row.createCell(colIndex++);
                cell.setCellValue(record.name);
                cell.setCellStyle(style);
                for (BowlerStatistic stat : stats) {
                    cell = row.createCell(colIndex++);
                    cell.setCellValue(stat.count());
                    cell.setCellStyle(statStyle);
                }
                cell = row.createCell(colIndex);
                cell.setCellValue(record.avg());
                cell.setCellStyle(statStyle);
            }

            try(FileOutputStream outputStream = new FileOutputStream("/home/wayne/Public/Bowler History/Bowler History.xlsx")) {
                workbook.write(outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {
        App app = new App();
        app.parseBowlerHistory(new File("/home/wayne/Public/Bowler History/Bowler History.pdf"));
    }
}
