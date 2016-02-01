package org.random_access.flashcardsmanager.xmlImport;

import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

/**
 * <b>Project:</b> FlashcardsManager for Android <br>
 * <b>Date:</b> 31.01.16 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class ProjectRootFinder {

    private ArrayList<String> projectRoots = new ArrayList<>();
    private String baseDir;

    public ProjectRootFinder(String baseDir) {
        this.baseDir = baseDir;
    }

    public ArrayList<String> findProjectRootDirs() {
        return searchRecursive(baseDir);
    }

    private ArrayList<String> searchRecursive(String pathToCurrentDir) {
        File currentDir = new File(pathToCurrentDir);
        if (currentDir.isDirectory()) {
            File[] content = currentDir.listFiles();
            for (File f : content) {
                if (f.getName().equals("media") && f.isDirectory()) {
                    projectRoots.add(currentDir.getAbsolutePath());
                } else {
                    searchRecursive(f.getAbsolutePath());
                }
            }
        }
        return projectRoots;
    }
}
