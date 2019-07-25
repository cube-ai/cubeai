package com.wyy.web.rest.util;

import java.io.*;


public final class FileUtil {

    public static void deleteDirectory(String path) {
        File filePath = new File(path);
        if (filePath.exists()) {
            if (filePath.isDirectory()) {
                File[] files = filePath.listFiles();
                if (null != files) {
                    for (File file : files) {
                        deleteDirectory(file.getAbsolutePath());
                    }
                }
            }
            filePath.delete();
        }
    }

}
