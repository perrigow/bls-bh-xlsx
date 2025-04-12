package net.blackcrate.bls_bh_xlsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.xml.sax.SAXException;

public class App {

    private static final Logger logger = Logger.getLogger(App.class.getName());
    private static final String TIMESTAMP;

    static {
        Date currentDate = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        TIMESTAMP = formatter.format(currentDate);

        try (InputStream loggingProperties = App.class.getClassLoader().getResourceAsStream("logging.properties")) {
            LogManager.getLogManager().readConfiguration(loggingProperties);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Could not load logger properties", ex);
        }
    }

    private void parseBowlerHistory(File[] blsFiles) {
        File xlsxFile = new File(blsFiles[0].getParent() + "/BowlerStats_" + TIMESTAMP + ".xlsx");

        try (XLSXOutputHandler xlsxHandler = new XLSXOutputHandler(xlsxFile)) {
            for (File blsFile : blsFiles) {
                logger.log(Level.INFO, "Processing {0}...", blsFile.getName());
                try (InputStream blsStream = new FileInputStream(blsFile)) {
                    PDFParser parser = new PDFParser();
                    BLSContentHandler blsHandler = new BLSContentHandler();
                    Metadata metadata = new Metadata();
                    ParseContext context = new ParseContext();

                    parser.parse(blsStream, blsHandler, metadata, context);

                    logger.info("\tWriting bowler history stats...");
                    String sheetName = FilenameUtils.getBaseName(blsFile.getName());
                    xlsxHandler.writeSheet(sheetName, blsHandler.records);
                } catch (IOException | SAXException | TikaException ex) {
                    logger.log(Level.WARNING, "Failed to process BLS file " + blsFile.getName(), ex);
                }
            }
        } catch (EncryptedDocumentException | IOException ex) {
            logger.log(Level.WARNING, "Failed to write to Excel (xlsx) file", ex);
        }
    }

    public static void main(String[] args) {

        if (args.length != 1) {
            logger.severe("No file or directory specified, exiting...");
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
            logger.severe("The file or directory does not exist, exiting...");
            System.exit(2);
        }
    }
}
