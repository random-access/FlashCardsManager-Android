package org.random_access.flashcardsmanager.xmlImport;

import android.content.Context;
import android.text.Html;

import org.random_access.flashcardsmanager.R;
import org.random_access.flashcardsmanager.queries.FlashCardQueries;
import org.random_access.flashcardsmanager.queries.LabelQueries;
import org.random_access.flashcardsmanager.queries.ProjectQueries;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
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

    private Context context;
    private final String DIRECTORY;

    private ArrayList<FlashCardParser.FlashCard> flashCards;
    private ArrayList<ProjectParser.Project> projects;
    private ArrayList<LabelParser.Label> labels;
    private ArrayList<LFRelParser.LFRel> lfRels;

    Map<Integer, Integer> labelIdConversionMap = new HashMap<>();
    private long uncategorizedLabelId;
    private boolean usedUncategorizedLabel;

    public XMLExchanger(Context context, String directory) {
        this.context = context;
        this.DIRECTORY = directory;
    }

    public void importProjects() throws XmlPullParserException, IOException {
        parseProjectsFromXML();
        parseFlashcardsFromXML();
        parseLabelFromXML();
        parseLFRelFromXML();
        Log.d(TAG, "Parsing complete!");
        saveToDatabase();
    }

    private void saveToDatabase() {
        ProjectQueries queries = new ProjectQueries(context);
        FlashCardQueries flashCardQueries = new FlashCardQueries(context);
        for (ProjectParser.Project p : projects) {
            int pId = Integer.parseInt(queries.insertProject(p.title, "", p.noOfStacks).getLastPathSegment());
            uncategorizedLabelId = Long.parseLong(new LabelQueries(context).addLabel(pId, context.getResources().getString(R.string.uncategorized)).getLastPathSegment());
            convertLabels(p.id, pId);
            convertFlashcards(p.id, pId);
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

    private void convertLabels(int xmlPid, int pId) {
        LabelQueries labelQueries = new LabelQueries(context);
        for (LabelParser.Label l : labels) {
            if (l.projId == xmlPid){
                int lId = Integer.parseInt(labelQueries.addLabel(pId, l.name).getLastPathSegment());
                labelIdConversionMap.put(l.id,lId);
            }
        }
    }

    private void convertFlashcards(int xmlPid, int pId) {
        FlashCardQueries flashCardQueries = new FlashCardQueries(context);
        for (FlashCardParser.FlashCard f : flashCards) {
            if (f.projId == xmlPid) {
                int fId = Integer.parseInt(flashCardQueries.insertCard(f.question, f.answer, f.stack, pId).getLastPathSegment());
                convertLfRels(f.id, pId, fId);
            }
        }
    }

    private void convertLfRels(int xmlFId, int pId, int fId) {
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


    private void parseProjectsFromXML() throws XmlPullParserException, IOException {
        ProjectParser projectParser = new ProjectParser();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(context.getFilesDir().getAbsolutePath() + "/" + DIRECTORY, FILE_PROJECTS));
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
            inputStream = new FileInputStream(new File(context.getFilesDir().getAbsolutePath() + "/" + DIRECTORY, FILE_FLASHCARDS));
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
            inputStream = new FileInputStream(new File(context.getFilesDir().getAbsolutePath() + "/" + DIRECTORY, FILE_LABELS));
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
            inputStream = new FileInputStream(new File(context.getFilesDir().getAbsolutePath() + "/" + DIRECTORY, FILE_LFRELS));
            lfRels = lfRelParser.parse(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

}
