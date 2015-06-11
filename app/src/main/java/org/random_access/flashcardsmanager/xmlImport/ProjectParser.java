package org.random_access.flashcardsmanager.xmlImport;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 11.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class ProjectParser extends XMLParser{

    // project property strings
    private static final String ELEM_ROOT_ENTRY = "projects";
    private static final String ELEM_BASE_ENTRY = "project";

    private static final String ELEM_PROJ_ID = "proj_id";
    private static final String ELEM_PROJ_TITLE = "proj_title";
    private static final String ELEM_NO_OF_STACKS = "no_of_stacks";

    // We don't use namespaces
    private static final String ns = null;

    public ArrayList parse(InputStream in) throws XmlPullParserException, IOException {
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

    private ArrayList readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Project> entries = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, ELEM_ROOT_ENTRY);
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the project tag
            if (name.equals(ELEM_BASE_ENTRY)) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    public static class Project {
        public final int id;
        public final String title;
        public final int noOfStacks;

        private Project(int id, String title, int noOfStacks) {
            this.id = id;
            this.title = title;
            this.noOfStacks = noOfStacks;
        }
    }

    // Parses the contents of an entry
    private Project readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ELEM_BASE_ENTRY);
        int id = 0;
        String title = null;
        int noOfStacks = 0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case ELEM_PROJ_ID:
                    id = Integer.parseInt(readContent(parser, ns, ELEM_PROJ_ID));
                    break;
                case ELEM_PROJ_TITLE:
                    title = readContent(parser, ns, ELEM_PROJ_TITLE);
                    break;
                case ELEM_NO_OF_STACKS:
                    noOfStacks = Integer.parseInt(readContent(parser, ns, ELEM_NO_OF_STACKS));
                    break;
                default:
                    skip(parser);
            }
        }
        return new Project(id, title, noOfStacks);
    }

}
