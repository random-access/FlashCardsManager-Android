package org.random_access.flashcardsmanager.xmlImport;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 12.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class FlashCardParser extends XMLParser {

    // flashcard property strings
    private static final String ELEM_ROOT_ENTRY= "flashcards";
    private static final String ELEM_BASE_ENTRY = "flashcard";

    private static final String ELEM_CARD_ID = "card_id";
    private static final String ELEM_PROJ_ID = "proj_id";
    private static final String ELEM_STACK = "stack";
    private static final String ELEM_QUESTION = "question";
    private static final String ELEM_ANSWER = "answer";

    // We don't use namespaces
    private static final String ns = null;

    public ArrayList<FlashCard> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<FlashCard> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<FlashCard> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, ELEM_ROOT_ENTRY);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(ELEM_BASE_ENTRY)) {
                entries.add(readXML(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class FlashCard {
        public final long id;
        public final long projId;
        public final int stack;
        public final String question;
        public final String answer;

        private FlashCard(long id, long projId, int stack, String question, String answer) {
            this.id = id;
            this.projId = projId;
            this.stack = stack;
            this.question = question;
            this.answer = answer;
        }
    }

    // Parses the contents of an entry
    private FlashCard readXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ELEM_BASE_ENTRY);
        long id = 0;
        long projId = 0;
        int stack = 0;
        String question = null;
        String answer = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case ELEM_CARD_ID:
                    id = Integer.parseInt(readContent(parser, ns, ELEM_CARD_ID));
                    break;
                case ELEM_PROJ_ID:
                    projId = Integer.parseInt(readContent(parser, ns, ELEM_PROJ_ID));
                    break;
                case ELEM_STACK:
                    stack = Integer.parseInt(readContent(parser, ns, ELEM_STACK));
                    break;
                case ELEM_QUESTION:
                    question = readContent(parser, ns, ELEM_QUESTION);
                    break;
                case ELEM_ANSWER:
                    answer = readContent(parser, ns, ELEM_ANSWER);
                    break;
                default:
                    skip(parser);
            }
        }
        return new FlashCard(id, projId, stack, question, answer);
    }

}
