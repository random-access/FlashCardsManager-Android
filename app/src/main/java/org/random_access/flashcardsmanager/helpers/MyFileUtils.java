package org.random_access.flashcardsmanager.helpers;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 14.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class MyFileUtils {

    private static final String TAG = MyFileUtils.class.getSimpleName();

    // prevent instantiation
    private MyFileUtils() {}

    /**
     * Deletes the directory at the given path and all subfiles and folders - use carefully...
     * @param file directory file to delet
     * @return true if deleting all subfiles and the file itself was successful, false otherwise
     */
    public static boolean deleteRecursive(File file) {
        Log.d(TAG, "Deleting: " + file.getName());
        boolean success = true;
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                success &= deleteRecursive(f);
            }
        }  else {
            success = file.delete();
        }
        return success;
    }

    /**
     * Copies a file from src to target
     * @param src source path
     * @param target target path
     * @throws IOException
     */
    public static boolean copyFile(String src, String target) throws IOException {
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        try {
            Log.d(TAG, "in copy method: src=" + src + ", target=" + target);
            inputStream = new FileInputStream(src);
            outputStream = new FileOutputStream(target);
            FileChannel inputChannel = inputStream.getChannel();
            FileChannel outputChannel = outputStream.getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            return true;
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
            } finally {
                if (outputStream != null)
                    outputStream.close();
            }
        }
    }

    /**
     * Returns the extension of a given file e.g. "jpg" / "png" /...
     * @param pathName source path containing file extension
     * @return file extension
     */
    public static String getFileExtension(String pathName) {
        String extension = pathName.substring(pathName.lastIndexOf('.') + 1, pathName.length());
        Log.d(TAG, extension);
        return extension;
    }

}
