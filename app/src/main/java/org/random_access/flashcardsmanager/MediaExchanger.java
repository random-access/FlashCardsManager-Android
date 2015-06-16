package org.random_access.flashcardsmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.random_access.flashcardsmanager.helpers.MyFileUtils;

import java.io.File;
import java.io.IOException;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 14.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class MediaExchanger {

    private static final String TAG = MediaExchanger.class.getSimpleName();

    private static final String PROJECT_DIR_TEMPLATE = "Project_";

    /**
     * Constructs an absolute path out of the relative image path from database and the project ID
     * and returns an image at the given path or null if it could not be decoded
     * @param context the calling activity
     * @param projectId id of project
     * @param path relative image path
     * @return a bitmap
     */
    public static Bitmap getImage(Context context, long projectId, String path) {
        return BitmapFactory.decodeFile(context.getFilesDir().getAbsolutePath() + "/" + PROJECT_DIR_TEMPLATE + projectId + "/" + path);
    }

    /**
     * Imports a given media from sourceAbsPath by copying it to the project directory in the internal storage
     * @param context the calling activity
     * @param projectId id of project
     * @param cardId id of flashcard
     * @param mediaType type of media (currently "q" for question / "a" for answer
     * @param sourceAbsPath absolute source path of media
     * @return the relative target path where the media is stored
     * @throws IOException
     */
    public static String importImage(Context context, long projectId, long cardId, String mediaType, String sourceAbsPath) throws IOException {
        String extension = MyFileUtils.getFileExtension(sourceAbsPath);
        String targetRelPath = constructImagePath(context, projectId, cardId, mediaType, extension);
        String targetAbsPath = context.getFilesDir().getAbsolutePath() + "/" + PROJECT_DIR_TEMPLATE + projectId + "/" + targetRelPath;
        MyFileUtils.copyFile(sourceAbsPath, targetAbsPath);
        return targetRelPath;
    }

    /**
     * Delete media with the given relative path
     * @param context the calling activity
     * @param path the path of media
     * @param projectId id of project
     * @return true if deleting was successful, false otherwise
     */
    public static boolean deleteImage(Context context, long projectId, String path) {
        return new File(context.getFilesDir().getAbsolutePath() + "/" + PROJECT_DIR_TEMPLATE + projectId, path).delete();
    }

    /**
     * Create project directory if not existing
     * @param context the calling activity
     * @param projectId project id
     * @return true if the directory already exists or creating directory was successful, false otherwise
     */
    public static boolean createProjectDirectory(Context context, long projectId) {
        File path = new File(context.getFilesDir().getAbsolutePath() + "/" + PROJECT_DIR_TEMPLATE + projectId + "");
        boolean b =  path.exists() || path.mkdir();
        Log.d(TAG, "Created project directory: " + path.toString());
        return b;
    }

    public static boolean deleteProjectDirectory(Context context, long projectId) {
        return MyFileUtils.deleteRecursive(new File(context.getFilesDir().getAbsolutePath() + "/" + PROJECT_DIR_TEMPLATE + projectId + ""));
    }

    /**
     * Constructs path for a given media in project directory with the pattern [PathToInternalStorage]/[ProjectDir]/media-[ProjectId]-[CardId]-[mediaType].[extension]
     * e.g. /data/data
     * @param context the calling activity
     * @param projectId id of project
     * @param cardId id of flashcard
     * @param mediaType type of media (currently "q" for question / "a" for answer)
     * @param extension file extension (currently ".jpg" / ".png") TODO validate extension!
     * @return media path
     */
    private static String constructImagePath(Context context, long projectId, long cardId, String mediaType, String extension) {
        String path = "media-" + projectId + "-" + cardId + "-" + mediaType + "." + extension;
        Log.d(TAG, path);
        return  path;
    }


}
