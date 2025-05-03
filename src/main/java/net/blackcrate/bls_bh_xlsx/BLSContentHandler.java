package net.blackcrate.bls_bh_xlsx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.text.StringEscapeUtils;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class BLSContentHandler extends DefaultHandler {

    private static final Logger logger = Logger.getLogger(BLSContentHandler.class.getName());

    private static final String REGEX_WEEK = "^([1-9]|[12][0-9])$";
    private static final String REGEX_DATE = "^(0?[1-9]|1[012])/(0?[1-9]|[12][0-9]|3[01])/(\\d{2})$";
    private static final String REGEX_GAME = "^([1,2]{0,1}[0-9]{1,2}|300)$";

    private final StringBuilder currentText;
    private boolean isHdcp;

    private BowlerRecord currentRecord;
    public final List<BowlerRecord> records;

    public BLSContentHandler() {
        currentText = new StringBuilder();
        isHdcp = false;

        records = new ArrayList<>();
    }

    private static boolean isWeek(String[] data) {
        if (data.length < 2)
            return false;

        return (data[0].matches(REGEX_WEEK) && data[1].matches(REGEX_DATE));
    }

    private static String boldedGameCheck(String game) {
        if (game.length() % 10 != 0) {
            return game;
        }
        return game.substring(0,game.length()/10);
    }

    private void processWeek(String[] data) {
        // game 1 (and bowledCheck), game 2, game 3, series
        int[] indices = new int[] {3,4,5,6};
        if (isHdcp) {
            indices = new int[] {4,5,6,7};
        }

        List<String> parts = new ArrayList<>(Arrays.asList(data));
        if (!parts.get(indices[0]).equals("0")) {
            int gamesBowled = 0;
            for (int gameIdx : Arrays.copyOf(indices, 3)) {
                String game = boldedGameCheck(parts.get(gameIdx));
                if (game.matches(REGEX_GAME)) {
                    logger.log(Level.FINE, "Game: {0}", game);
                    currentRecord.addGame(Integer.parseInt(game));
                    gamesBowled++;
                }
            }

            if (gamesBowled == 3) {
                currentRecord.addSeries(Integer.parseInt(parts.get(indices[3])));
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentText.append(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (!StringEscapeUtils.escapeJava(new String(ch)).equals("\\n"))
            return;

        String text = currentText.toString().trim();
        logger.log(Level.FINEST, "text = {0}", text);

        if (text.contains("Bowling Record")) {
            if (currentRecord != null) {
                records.add(currentRecord);
            }

            String name = text.substring(0, text.indexOf("'s"));
            logger.log(Level.FINE, "\nNew Record for {0}", name);
            currentRecord = new BowlerRecord(name);
        }

        if (text.contains("HDCP")) {
            isHdcp = true;
        }

        String[] textArray = text.split("\\s+");
        if (!text.isEmpty() && isWeek(textArray)) {
            logger.fine(String.join(" ", textArray));
            processWeek(textArray);
        }

        currentText.setLength(0);
    }

    @Override
    public void endDocument() throws SAXException {
        records.add(currentRecord);
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
        return records;
    }

}
