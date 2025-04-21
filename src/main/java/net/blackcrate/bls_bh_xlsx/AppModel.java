package net.blackcrate.bls_bh_xlsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.xml.sax.SAXException;

public class AppModel {

    private static final Logger logger = Logger.getLogger(AppModel.class.getName());
    private static final SimpleDateFormat DATE_FORMATTER;

    private File[] blsFiles;

    static {
        DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    }

    public AppModel() {
    }

    private String getTimestamp() {
        return DATE_FORMATTER.format(new Date());
    }

    private File getXlsxFile() {
        return new File(blsFiles[0].getParent() + "/BowlerStats_" + getTimestamp() + ".xlsx");
    }
    
    public void parseBowlerHistory(File[] blsFiles) {
        if (blsFiles.length == 0) {
            // TODO show no files dialog box
            return;
        }

        this.blsFiles = blsFiles;

        try (XLSXOutputHandler xlsxHandler = new XLSXOutputHandler(getXlsxFile())) {
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
}
