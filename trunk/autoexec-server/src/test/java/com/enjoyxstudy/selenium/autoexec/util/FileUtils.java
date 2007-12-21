package com.enjoyxstudy.selenium.autoexec.util;

import java.io.File;
import java.io.IOException;

/**
 * @author onozaty
 */
public class FileUtils {

    /**
     * delete directory
     * @param directory
     * @throws IOException
     */
    public static void deleteDirectory(File directory) throws IOException {

        if (!directory.exists()) {
            String message = directory + " does not exist";
            throw new IllegalArgumentException(message);
        }

        if (!directory.isDirectory()) {
            String message = directory + " is not a directory";
            throw new IllegalArgumentException(message);
        }

        File[] files = directory.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        directory.delete();
    }
}
