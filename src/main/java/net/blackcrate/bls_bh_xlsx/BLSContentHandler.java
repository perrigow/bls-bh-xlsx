package net.blackcrate.bls_bh_xlsx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.text.StringEscapeUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class BLSContentHandler extends DefaultHandler {

    private static final String REGEX_WEEK = "^([1-9]|[12][0-9])$";
    private static final String REGEX_DATE = "^(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/(\\d{2})$";

    private final StringBuilder currentText;
    private boolean isHdcp;

    private BowlerRecord currentRecord;
    public final List<BowlerRecord> records;

    public BLSContentHandler() {
        this.currentText = new StringBuilder();
        this.isHdcp = false;

        this.records = new ArrayList<>();
    }

    private static boolean isWeek(String[] data) {
        if (data.length < 2)
            return false;

        return (data[0].matches(REGEX_WEEK) && data[1].matches(REGEX_DATE));
    }

    private void processWeek(String[] data) {
        // game 1 (and bowledCheck), game 2, game 3, series
        int[] indices = new int[] {3,4,5,6};
        if (this.isHdcp) {
            indices = new int[] {4,5,6,7};
        }

        List<String> parts = new ArrayList<>(Arrays.asList(data));
        if (!parts.get(indices[0]).equals("0")) {
            int gamesBowled = 0;
            for (int gameIdx : Arrays.copyOf(indices, 3)) {
                String game = parts.get(gameIdx);
                if (game.matches("^[1-9][0-9]{0,}$")) {
                    this.currentRecord.addGame(Integer.parseInt(game));
                    gamesBowled++;
                }
            }

            if (gamesBowled == 3) {
                this.currentRecord.addSeries(Integer.parseInt(parts.get(indices[3])));
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.currentText.append(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (!StringEscapeUtils.escapeJava(new String(ch)).equals("\\n"))
            return;

        String text = this.currentText.toString().trim();
        if (text.contains("Bowling Record")) {
            if (this.currentRecord != null) {
                this.records.add(this.currentRecord);
            }

            String name = text.substring(0, text.indexOf("'s"));
            this.currentRecord = new BowlerRecord(name);
        }

        if (text.contains("HDCP")) {
            isHdcp = true;
        }

        String[] textArray = text.split("\\s+");
        if (!text.isEmpty() && isWeek(textArray)) {
            this.processWeek(textArray);
        }

        this.currentText.setLength(0);
    }

    @Override
    public void endDocument() throws SAXException {
        this.records.add(this.currentRecord);
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }

    public List<BowlerRecord> getRecords(){
        return this.records;
    }

}
