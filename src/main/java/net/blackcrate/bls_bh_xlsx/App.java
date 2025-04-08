package net.blackcrate.bls_bh_xlsx;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.xml.sax.SAXException;

public class App {
    public static void main(String[] args) {
        try (InputStream blsFile = new FileInputStream("/home/wayne/Public/Bowler History/Bowler History.pdf")) {
            PDFParser parser = new PDFParser();
            BLSContentHandler handler = new BLSContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            parser.parse(blsFile, handler, metadata, context);

            // for (BowlerRecord record : handler.records) {
            //     System.out.println(record + "\n");
            // }
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
        }
    }
}
