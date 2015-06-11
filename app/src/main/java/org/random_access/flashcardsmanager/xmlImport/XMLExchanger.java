package org.random_access.flashcardsmanager.xmlImport;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 11.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class XMLExchanger {

    // flashcard property strings
    private static final String ELEM_FLASHCARDS = "flashcards";
    private static final String ELEM_FLASHCARD = "flashcard";
    private static final String ELEM_CARD_ID = "card_id";
    private static final String ELEM_STACK = "stack";
    private static final String ELEM_QUESTION = "question";
    private static final String ELEM_ANSWER = "answer";
    private static final String ELEM_CUSTOM_WIDTH_QUESTION = "custom_width_question";
    private static final String ELEM_CUSTOM_WIDTH_ANSWER = "custom_width_answer";

    // project property strings
    private static final String ELEM_PROJECTS = "projects";
    private static final String ELEM_PROJECT = "project";
    private static final String ELEM_PROJ_ID = "proj_id";
    private static final String ELEM_PROJ_TITLE = "proj_title";
    private static final String ELEM_NO_OF_STACKS = "no_of_stacks";

    // media property strings
    private static final String ELEM_MEDIAS = "medias";
    private static final String ELEM_MEDIA = "media";
    private static final String ELEM_MEDIA_ID = "media_id";
    private static final String ELEM_PATH_TO_MEDIA = "path_to_media";
    private static final String ELEM_PICTYPE = "pictype";

    // label property strings
    private static final String ELEM_LABELS = "labels";
    private static final String ELEM_LABEL = "label";
    private static final String ELEM_LABEL_ID = "label_id";
    private static final String ELEM_LABEL_NAME = "label_name";

    // labels-flashcards property strings
    private static final String ELEM_LABELS_FLASHCARDS = "labels_flashcards";
    private static final String ELEM_LABEL_FLASHCARD = "label_flashcard";
    private static final String ELEM_LABELS_FLASHCARDS_ID = "labels_flashcards_id";

    private static final String xml10pattern = "[^" + "\u0009\r\n" + "\u0020-\uD7FF" + "\uE000-\uFFFD"
            + "\ud800\udc00-\udbff\udfff" + "]";





}
