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
public class LabelParser  extends XMLParser{

    // label property strings
    private static final String ELEM_ROOT_ENTRY = "labels";
    private static final String ELEM_BASE_ENTRY = "label";
    private static final String ELEM_LABEL_ID = "label_id";
    private static final String ELEM_PROJ_ID = "proj_id";
    private static final String ELEM_LABEL_NAME = "label_name";

    // We don't use namespaces
    private static final String ns = null;

    public ArrayList<Label> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private ArrayList<Label> readXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Label> entries = new ArrayList<>();

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

    public static class Label {
        public final long id;
        public final long projId;
        public final String name;

        private Label(long id, long projId, String name) {
            this.id = id;
            this.projId = projId;
            this.name = name;
        }
    }

    // Parses the contents of an entry
    private Label readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ELEM_BASE_ENTRY);
        long id = 0;
        long projId = 0;
        String labelName = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case ELEM_LABEL_ID:
                    id = Long.parseLong(readContent(parser, ns, ELEM_LABEL_ID));
                    break;
                case ELEM_PROJ_ID:
                    projId = Long.parseLong(readContent(parser, ns, ELEM_PROJ_ID));
                    break;
                case ELEM_LABEL_NAME:
                    labelName = readContent(parser, ns, ELEM_LABEL_NAME);
                    break;
                default:
                    skip(parser);
            }
        }
        return new Label(id, projId, labelName);
    }

}
