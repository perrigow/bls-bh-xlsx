package net.blackcrate.bls_bh_xlsx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FilenameUtils;
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

    private void parseBowlerHistory(File blsFile) {
        String sheetName = FilenameUtils.getBaseName(blsFile.getName());

        try (InputStream blsStream = new FileInputStream(blsFile)) {
            PDFParser parser = new PDFParser();
            BLSContentHandler blsHandler = new BLSContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            parser.parse(blsStream, blsHandler, metadata, context);

            File xlsxFile = new File(blsFile.getParent() + "/BowlerStats_" + TIMESTAMP + ".xlsx");
            XLSXOutputHandler xlsxHandler = new XLSXOutputHandler(xlsxFile, sheetName);
            xlsxHandler.write(blsHandler.records);
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        App app = new App();
        app.parseBowlerHistory(new File("/home/wayne/Public/Bowler History/AJ & Dennis Gregory Memorial Doubles.pdf"));
    }
}
