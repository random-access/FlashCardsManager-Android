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
public class LFRelParser extends XMLParser {
    // project property strings
    // flashcard property strings
    private static final String ELEM_ROOT_ENTRY= "labels_flashcards";
    private static final String ELEM_BASE_ENTRY = "label_flashcard";
    private static final String ELEM_LABELS_FLASHCARDS_ID = "labels_flashcards_id";
    private static final String ELEM_LABEL_ID = "label_id";
    private static final String ELEM_CARD_ID = "card_id";

    // We don't use namespaces
    private static final String ns = null;

    public ArrayList<LFRel> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readXML(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<LFRel> readXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<LFRel> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, ELEM_ROOT_ENTRY);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals(ELEM_BASE_ENTRY)) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class LFRel {
        public final long id;
        public final long labelId;
        public final long cardId;

        private LFRel(long id, long labelId, long cardId) {
            this.id = id;
            this.labelId = labelId;
            this.cardId = cardId;
        }
    }

    // Parses the contents of an entry
    private LFRel readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ELEM_BASE_ENTRY);
        long id = 0;
        long labelId = 0;
        long cardId = 0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case ELEM_LABELS_FLASHCARDS_ID:
                    id = Integer.parseInt(readContent(parser, ns, ELEM_LABELS_FLASHCARDS_ID));
                    break;
                case ELEM_CARD_ID:
                    cardId = Integer.parseInt(readContent(parser, ns, ELEM_CARD_ID));
                    break;
                case ELEM_LABEL_ID:
                    labelId = Integer.parseInt(readContent(parser, ns, ELEM_LABEL_ID));
                    break;
                default:
                    skip(parser);
            }
        }
        return new LFRel(id, labelId, cardId);
    }

}