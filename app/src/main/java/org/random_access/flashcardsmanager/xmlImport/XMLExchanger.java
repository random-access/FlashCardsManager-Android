package org.random_access.flashcardsmanager.xmlImport;

import android.content.Context;

import org.random_access.flashcardsmanager.R;
import org.random_access.flashcardsmanager.queries.FlashCardQueries;
import org.random_access.flashcardsmanager.queries.LabelQueries;
import org.random_access.flashcardsmanager.queries.MediaQueries;
import org.random_access.flashcardsmanager.queries.ProjectQueries;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 11.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class XMLExchanger {

    private static final String TAG = XMLExchanger.class.getSimpleName();

    private static final String FILE_FLASHCARDS = "flashcards.xml";
    private static final String FILE_PROJECTS = "projects.xml";
    private static final String FILE_LABELS = "labels.xml";
    private static final String FILE_LFRELS = "labels-flashcards-rel.xml";
    private static final String FILE_MEDIA = "media.xml";

    private Context context;
    private final String IMPORT_DIRECTORY;
    private final String IMPORT_DIRECTORY_MEDIA;

    private ArrayList<FlashCardParser.FlashCard> flashCards;
    private ArrayList<ProjectParser.Project> projects;
    private ArrayList<LabelParser.Label> labels;
    private ArrayList<LFRelParser.LFRel> lfRels;
    private ArrayList<MediaParser.Media> media;

    Map<Long, Long> labelIdConversionMap = new HashMap<>();
    private long uncategorizedLabelId;
    private boolean usedUncategorizedLabel;

    public XMLExchanger(Context context, String directory) {
        this.context = context;
        this.IMPORT_DIRECTORY = directory;
        Log.d(TAG, "Import directory: " + directory);
        this.IMPORT_DIRECTORY_MEDIA = IMPORT_DIRECTORY + "/" + "media";
    }

    public void importProjects() throws XmlPullParserException, IOException {
        parseProjectsFromXML();
        parseFlashcardsFromXML();
        parseLabelFromXML();
        parseLFRelFromXML();
        parseMediaFromXML();
        Log.d(TAG, "Parsing complete!");
        saveToDatabase();
    }

    private void saveToDatabase() throws IOException{
        ProjectQueries queries = new ProjectQueries(context);
        for (ProjectParser.Project p : projects) {
            long pId = Long.parseLong(queries.insertProject(p.title, "", p.noOfStacks).getLastPathSegment());
            uncategorizedLabelId = Long.parseLong(new LabelQueries(context).addLabel(pId, context.getResources().getString(R.string.uncategorized)).getLastPathSegment());
            importLabels(p.id, pId);
            importFlashcards(p.id, pId);
            if (!usedUncategorizedLabel) {
                // delete uncategorized label if not needed
                LabelQueries labelQueries = new LabelQueries(context);
                labelQueries.deleteLabel(uncategorizedLabelId, pId);
            }
            // reset uncategorized label
            usedUncategorizedLabel = false;
            uncategorizedLabelId = 0;
        }
    }

    private void importLabels(long xmlPid, long pId) {
        LabelQueries labelQueries = new LabelQueries(context);
        for (LabelParser.Label l : labels) {
            if (l.projId == xmlPid){
                long lId = Long.parseLong(labelQueries.addLabel(pId, l.name).getLastPathSegment());
                labelIdConversionMap.put(l.id,lId);
            }
        }
    }

    private void importFlashcards(long xmlPid, long pId) throws IOException {
        FlashCardQueries flashCardQueries = new FlashCardQueries(context);
        for (FlashCardParser.FlashCard f : flashCards) {
            if (f.projId == xmlPid) {
                long fId = Long.parseLong(flashCardQueries.insertCard(f.question, f.answer, f.stack, pId).getLastPathSegment());
                importLfRels(f.id, pId, fId);
                importMedia(f.id, pId, fId);
            }
        }
    }

    private void importLfRels(long xmlFId, long pId, long fId) {
        FlashCardQueries flashCardQueries = new FlashCardQueries(context);
        boolean hasLabel = false;
        for (LFRelParser.LFRel lfRel : lfRels) {
            if (xmlFId == lfRel.cardId) {
                flashCardQueries.assignLabelToCard(fId, labelIdConversionMap.get(lfRel.labelId));
                hasLabel = true;
            }
        }
        if (!hasLabel) {
            flashCardQueries.assignLabelToCard(fId, uncategorizedLabelId);
            usedUncategorizedLabel = true;
        }
    }

    private void importMedia(long xmlFId, long pId, long fId) throws IOException{
        MediaQueries mediaQueries = new MediaQueries(context);
        for (MediaParser.Media m : media) {
            if (xmlFId == m.cardId) {
                mediaQueries.insertMedia(pId, fId, m.picType, IMPORT_DIRECTORY_MEDIA + "/" +  m.pathToMedia);
            }
        }
    }


    private void parseProjectsFromXML() throws XmlPullParserException, IOException {
        ProjectParser projectParser = new ProjectParser();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(IMPORT_DIRECTORY, FILE_PROJECTS));
            projects = projectParser.parse(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private void parseFlashcardsFromXML()  throws XmlPullParserException, IOException {
        FlashCardParser flashCardParser = new FlashCardParser();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(IMPORT_DIRECTORY, FILE_FLASHCARDS));
            flashCards = flashCardParser.parse(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private void parseLabelFromXML() throws XmlPullParserException, IOException {
        LabelParser labelParser = new LabelParser();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(IMPORT_DIRECTORY, FILE_LABELS));
            labels = labelParser.parse(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private void parseLFRelFromXML() throws XmlPullParserException, IOException {
        LFRelParser lfRelParser = new LFRelParser();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(IMPORT_DIRECTORY, FILE_LFRELS));
            lfRels = lfRelParser.parse(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private void parseMediaFromXML() throws XmlPullParserException, IOException  {
        MediaParser mediaParser = new MediaParser();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(IMPORT_DIRECTORY, FILE_MEDIA));
            media = mediaParser.parse(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    private String constructPicName(long pId, long fId, String picType, String picName) {
        String extension = picName.substring(picName.lastIndexOf('.'));
        return "pic-" + pId + "-" + fId + "-" + picType + extension;
    }

}
