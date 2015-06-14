package org.random_access.flashcardsmanager;

import android.app.Fragment;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 12.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class LearningFragment extends Fragment {

    private Map<Long, Integer> progressChanges = new HashMap<>();
    private Map<Long, LearningActivity.Result> statsTracking = new HashMap<>();
    private int cursorPosition;
    private boolean isAnswerVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    public Map<Long, Integer> getProgressChanges() {
        return progressChanges;
    }

    public void setProgressChanges(Map<Long, Integer> progressChanges) {
        this.progressChanges = progressChanges;
    }

    public Map<Long, LearningActivity.Result> getStatsTracking() {
        return statsTracking;
    }

    public void setStatsTracking(Map<Long, LearningActivity.Result> statsTracking) {
        this.statsTracking = statsTracking;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    public void setCursorPosition(int cursorPosition) {
        this.cursorPosition = cursorPosition;
    }

    public boolean isAnswerVisible() {
        return isAnswerVisible;
    }

    public void setIsAnswerVisible(boolean isAnswerVisible) {
        this.isAnswerVisible = isAnswerVisible;
    }
}
