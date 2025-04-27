package net.blackcrate.bls_bh_xlsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FilenameUtils;
import org.apache.poi.EncryptedDocumentException;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.xml.sax.SAXException;

import javafx.application.Platform;

public class AppModel implements Contract.Model {

    private static final Logger logger = Logger.getLogger(AppModel.class.getName());
    private static final SimpleDateFormat DATE_FORMATTER;

    private List<File> blsFiles;

    private File xlsxFile;

    static {
        DATE_FORMATTER = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
    }

    public AppModel() {
        logger.info("AppModel object initialized");
    }

    private String getTimestamp() {
        return DATE_FORMATTER.format(new Date());
    }

    private File autoXlsxFile() {
        if (blsFiles.isEmpty()) {
            return null;
        }
        return new File(blsFiles.get(0).getParent() + "/BowlerStats_" + getTimestamp() + ".xlsx");
    }

    private void updateProgress(Contract.Model.onProcessUpdateListener listener, double progress, String status) {
        logger.log(Level.INFO, "Progress updated to: {0}%", progress * 100);
        logger.log(Level.INFO, "Progress status: {0}", status);
        if (listener != null) {
            Platform.runLater(() -> {
                listener.onProgressUpdate(progress, status);
            });
        }
    }

    @Override
    public int getBlsFileCount() {
        return blsFiles.size();
    }

    @Override
    public void setBlsFiles(List<File> blsFiles) {
        this.blsFiles = blsFiles;
        xlsxFile = autoXlsxFile();
    }

    @Override
    public File getXlsxFile() {
        return xlsxFile;
    }

    @Override
    public void setXlsxFile(String xlsxFile) {
        this.xlsxFile = new File(xlsxFile);
    }

    @Override
    public void setXlsxFile(File xlsxFile) {
        this.xlsxFile = xlsxFile;
    }

    @Override
    public void parseBowlerHistory(Contract.Model.onProcessUpdateListener listener) {
        if (blsFiles.isEmpty()) {
            logger.warning("BLS files list is empty");
            return;
        }

        new Thread(() -> {
            double jobs = (double) blsFiles.size() * 2; // multiple by 2 for process and write
            double completed = 0.00;
            String status = "Warming up...";
            updateProgress(listener, completed/jobs, status);            

            try (XLSXOutputHandler xlsxHandler = new XLSXOutputHandler(xlsxFile)) {
                for (File blsFile : blsFiles) {
                    status = "Processing \"" + blsFile.getName() + "\"";
                    updateProgress(listener, completed/jobs, status);
                    try (InputStream blsStream = new FileInputStream(blsFile)) {
                        PDFParser parser = new PDFParser();
                        BLSContentHandler blsHandler = new BLSContentHandler();
                        Metadata metadata = new Metadata();
                        ParseContext context = new ParseContext();
                        
                        parser.parse(blsStream, blsHandler, metadata, context);
                        completed++;
                        
                        status = "Writing bowler history stats for \"" + blsFile.getName() + "\"";
                        updateProgress(listener, completed/jobs, status);
                        String sheetName = FilenameUtils.getBaseName(blsFile.getName());
                        xlsxHandler.writeSheet(sheetName, blsHandler.records);
                        completed++;
                    } catch (IOException | SAXException | TikaException ex) {
                        logger.log(Level.WARNING, "Failed to process BLS file \"" + blsFile.getName() + "\"", ex);
                    }
                }
                status = "Done processing";
                updateProgress(listener, completed/jobs, status);
                Platform.runLater(() -> {
                    listener.onProcessingComplete();
                });
            } catch (EncryptedDocumentException | IOException ex) {
                logger.log(Level.SEVERE, "Failed to write to Excel (xlsx) file", ex);
            }
        }).start();
    }
}
