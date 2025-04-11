package net.blackcrate.bls_bh_xlsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.xml.sax.SAXException;

public class App {

    private static final String TIMESTAMP;

    static {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        TIMESTAMP = formatter.format(currentDate);
    }

    private void parseBowlerHistory(File[] blsFiles) {
        File xlsxFile = new File(blsFiles[0].getParent() + "/BowlerStats_" + TIMESTAMP + ".xlsx");

        try (XLSXOutputHandler xlsxHandler = new XLSXOutputHandler(xlsxFile)) {
            System.out.println("Processing " + blsFiles.length + " files...");
            for (File blsFile : blsFiles) {
                try (InputStream blsStream = new FileInputStream(blsFile)) {
                    PDFParser parser = new PDFParser();
                    BLSContentHandler blsHandler = new BLSContentHandler();
                    Metadata metadata = new Metadata();
                    ParseContext context = new ParseContext();

                    parser.parse(blsStream, blsHandler, metadata, context);

                    String sheetName = FilenameUtils.getBaseName(blsFile.getName());
                    xlsxHandler.writeSheet(sheetName, blsHandler.records);
                } catch (IOException | SAXException | TikaException e) {
                    e.printStackTrace();
                }
            }
        } catch (EncryptedDocumentException | IOException e1) {
            e1.printStackTrace();
        }
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("No file or directory specified!");
            System.exit(1);
        }

        File[] blsFiles = new File[] { new File(args[0]) };
        if (blsFiles[0].exists()) {
            if (blsFiles[0].isDirectory()) {
                blsFiles = blsFiles[0].listFiles((File dir, String name) -> name.toLowerCase().endsWith(".pdf"));
            }
            App app = new App();
            app.parseBowlerHistory(blsFiles);
        } else {
            System.err.println("The file or directory does not exist!");
        }
    }
}
