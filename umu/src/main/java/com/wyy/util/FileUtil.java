package com.wyy.util;

import org.springframework.core.io.Resource;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class FileUtil {

    public static Boolean validateModelFile(File file) {
        Boolean zipFilePresent = false;
        Boolean schemaFilePresent = false;
        Boolean metadataFilePresent = false;

        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                if (zipEntry.getName().contains(".zip") || zipEntry.getName().contains(".jar")
                    || zipEntry.getName().contains(".bin") || zipEntry.getName().contains(".tar")
                    || zipEntry.getName().toUpperCase().contains(".R")) {
                    zipFilePresent = true;
                }

                if (zipEntry.getName().contains(".proto"))
                    schemaFilePresent = true;

                if (zipEntry.getName().contains(".json"))
                    metadataFilePresent = true;

                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
            zis.close();
        } catch (Exception e) {
            return false;
        }
        return zipFilePresent && schemaFilePresent && metadataFilePresent;
    }

    public static List<File> getListOfFiles(String directoryName) {
        List<File> fileList;

        try {
            File directory = new File(directoryName);
            File[] files = directory.listFiles();

            if (null == files) {
                return null;
            }

            fileList = new ArrayList<>(Arrays.asList(files));

            for (File file : files) {
                if (file.isDirectory()) {
                    List<File> newFileList = getListOfFiles(file.getAbsolutePath());
                    if (null != newFileList) {
                        fileList.addAll(newFileList);
                    }
                }
            }
        } catch (Exception e) {
            return null;
        }

        return fileList;
    }

    public static Boolean extractZipFile(File file, String destinationPath) {

        byte[] buf = new byte[1024];
        ZipEntry zipEntry;

        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
            while ((zipEntry = zis.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    continue;
                }

                File newFile = new File(destinationPath, zipEntry.getName());
                if(!newFile.exists()) {
                    (new File(newFile.getParent())).mkdirs();
                }

                int n;
                try {
                    FileOutputStream fos = new FileOutputStream(newFile);
                    while ((n = zis.read(buf, 0, 1024)) > -1) {
                        fos.write(buf, 0, n);
                    }
                    fos.close();
                } catch (IOException e) {
                    return false;
                }
                zis.closeEntry();
            }
            zis.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

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

    public static Boolean copyFile(Resource srcFile, File destFile) {
        try {
            return copyFile(srcFile.getInputStream(), destFile);
        } catch (Exception e) {
            return  false;
        }

    }

    public static Boolean copyFile(File srcFile, File destFile) {
        try {
            return copyFile(new FileInputStream(srcFile), destFile);
        } catch (IOException e) {
            return false;
        }
    }

    public static Boolean copyFile(InputStream inputStream, File destFile) {
        try {
            OutputStream outputStream = new FileOutputStream(destFile);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
