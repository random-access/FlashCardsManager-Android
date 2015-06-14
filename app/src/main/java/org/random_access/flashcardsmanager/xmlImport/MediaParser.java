package org.random_access.flashcardsmanager.xmlImport;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 14.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class MediaParser extends XMLParser{

    // flashcard property strings
    private static final String ELEM_ROOT_ENTRY= "medias";
    private static final String ELEM_BASE_ENTRY = "media";

    // media property strings
    private static final String ELEM_MEDIA_ID = "media_id";
    private static final String ELEM_CARD_ID = "card_id";
    private static final String ELEM_PATH_TO_MEDIA = "path_to_media";
    private static final String ELEM_PICTYPE = "pictype";

    // We don't use namespaces
    private static final String ns = null;

    public ArrayList<Media> parse(InputStream in) throws XmlPullParserException, IOException {
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

    private ArrayList<Media> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<Media> entries = new ArrayList<>();

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

    public static class Media {
        public final long id;
        public final long cardId;
        public final String pathToMedia;
        public final String picType;

        private Media(long id, long cardId, String pathToMedia, String picType) {
            this.id = id;
            this.cardId = cardId;
            this.pathToMedia = pathToMedia;
            this.picType = picType;
        }
    }

    // Parses the contents of an entry
    private Media readXML(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, ELEM_BASE_ENTRY);
        long id = 0;
        long cardId = 0;
        String pathToMedia = null;
        String picType = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case ELEM_MEDIA_ID:
                    id = Integer.parseInt(readContent(parser, ns, ELEM_MEDIA_ID));
                    break;
                case ELEM_CARD_ID:
                    id = Integer.parseInt(readContent(parser, ns, ELEM_CARD_ID));
                    break;
                case ELEM_PATH_TO_MEDIA:
                    pathToMedia = readContent(parser, ns, ELEM_PATH_TO_MEDIA);
                    break;
                case ELEM_PICTYPE:
                    picType = readContent(parser, ns, ELEM_PICTYPE);
                    // todo check if "q" or "a" otherwise damaged xml file!
                    break;
                default:
                    skip(parser);
            }
        }
        return new Media(id,cardId,pathToMedia,picType);
    }


}
