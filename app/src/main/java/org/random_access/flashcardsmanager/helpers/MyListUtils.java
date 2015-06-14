package org.random_access.flashcardsmanager.helpers;

import java.util.ArrayList;

/**
 * <b>Project:</b> FlashCards Manager for Android <br>
 * <b>Date:</b> 13.06.15 <br>
 * <b>Author:</b> Monika Schrenk <br>
 * <b>E-Mail:</b> software@random-access.org <br>
 */
public class MyListUtils {

    public static int[] buildIntArray(ArrayList<Integer> integerList) {
        int[] intArray = new int[integerList.size()];
        int i = 0;
        for (Integer j : integerList) {
            intArray[i++] = j;
        }
        return intArray;
    }

    public static long[] buildLongArray(ArrayList<Long> longList) {
        long[] longArray = new long[longList.size()];
        int i = 0;
        for (Long l : longList) {
            longArray[i++] = l;
        }
        return longArray;
    }

}
