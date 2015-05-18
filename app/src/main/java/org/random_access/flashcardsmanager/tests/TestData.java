package org.random_access.flashcardsmanager.tests;

import android.content.Context;

import org.random_access.flashcardsmanager.queries.FlashCardQueries;
import org.random_access.flashcardsmanager.queries.ProjectQueries;

/**
 * Project: FlashCards Manager for Android
 * Date: 14.05.15
 * Author: Monika Schrenk
 * E-Mail: software@random-access.org
 */
public class TestData {

    private Context context;

    public TestData(Context context) {
        this.context = context;
        insertProjects();
        insertLabels();
        insertFlashcards();
        insertLfRelations();
    }

    private void insertProjects() {
        ProjectQueries pQueries = new ProjectQueries(context);
        pQueries.insertProject("Testprojekt 1", "Testbeschreibung 1", 3);
        pQueries.insertProject("Testprojekt 2", "Testbeschreibung 2", 4);
        pQueries.insertProject("Testprojekt 3", "Testbeschreibung 3", 2);
        pQueries.insertProject("Testprojekt 4", "Testbeschreibung 4", 1);
        pQueries.insertProject("Testprojekt 5", "Testbeschreibung 5", 3);
    }

    private void insertLabels() {


    }

    private void insertFlashcards() {
        FlashCardQueries fQueries = new FlashCardQueries(context);
        fQueries.insertCard("Testfrage 1-1", "Testantwort 1-1", 3, 1);
        fQueries.insertCard("Testfrage 2-1", "Testantwort 2-1", 2, 1);
        fQueries.insertCard("Testfrage 3-1", "Testantwort 3-1", 1, 1);
        fQueries.insertCard("Testfrage 4-1", "Testantwort 4-1", 3, 1);

        fQueries.insertCard("Testfrage 1-2", "Testantwort 1-2", 4, 2);
        fQueries.insertCard("Testfrage 2-2", "Testantwort 2-2", 4, 2);
        fQueries.insertCard("Testfrage 3-2", "Testantwort 3-2", 1, 2);
        fQueries.insertCard("Testfrage 4-2", "Testantwort 4-2", 3, 2);
        fQueries.insertCard("Testfrage 5-2", "Testantwort 5-2", 1, 2);
        fQueries.insertCard("Testfrage 6-2", "Testantwort 6-2", 3, 2);

        fQueries.insertCard("Testfrage 1-3", "Testantwort 1-3", 2, 3);
        fQueries.insertCard("Testfrage 2-3", "Testantwort 2-3", 2, 3);
        fQueries.insertCard("Testfrage 3-3", "Testantwort 3-3", 1, 3);
        fQueries.insertCard("Testfrage 4-3", "Testantwort 4-3", 1, 3);
        fQueries.insertCard("Testfrage 5-3", "Testantwort 5-3", 2, 3);
        fQueries.insertCard("Testfrage 6-3", "Testantwort 6-3", 1, 3);
        fQueries.insertCard("Testfrage 7-3", "Testantwort 7-3", 1, 3);
        fQueries.insertCard("Testfrage 8-3", "Testantwort 8-3", 1, 3);
    }

    private void insertLfRelations() {

    }




}
