package com.wso2.migrator.util;

import com.wso2.migrator.constants.Constants;
import com.wso2.migrator.exception.APIMigrationException;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static final Logger log = Logger.getLogger(ZipUtil.class);

    /**
     * Archive a provided source directory to a zipped file
     *
     * @param sourceDirectory Source directory
     * @throws APIMigrationException If an error occurs while generating archive
     */
    public static void archiveDirectory(String sourceDirectory) throws APIMigrationException {

        File directoryToZip = new File(sourceDirectory);

        List<File> fileList = new ArrayList<File>();
        getAllFiles(directoryToZip, fileList);
        writeArchiveFile(directoryToZip, fileList);

        if (log.isDebugEnabled()) {
            log.debug("Archived API generated successfully");
        }

    }

    /**
     * Retrieve all the files included in the source directory to be archived
     *
     * @param sourceDirectory Source directory
     * @param fileList        List of files
     */
    private static void getAllFiles(File sourceDirectory, List<File> fileList) {

        File[] files = sourceDirectory.listFiles();
        if (files != null) {
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    getAllFiles(file, fileList);
                }
            }
        }
    }

    /**
     * Generate archive file
     *
     * @param directoryToZip Location of the archive
     * @param fileList       List of files to be included in the archive
     * @throws APIMigrationException If an error occurs while adding files to the archive
     */
    private static void writeArchiveFile(File directoryToZip, List<File> fileList) throws APIMigrationException {

        try (FileOutputStream fileOutputStream = new FileOutputStream(directoryToZip.getPath() + File.separator + Constants.ARCHIVE_NAME)) {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
                for (File file : fileList) {
                    if (!file.isDirectory()) {
                        addToArchive(directoryToZip, file, zipOutputStream);
                    }
                }
            }

        } catch (IOException e) {
            String errorMessage = "I/O error while adding files to archive";
            log.error(errorMessage, e);
            throw new APIMigrationException(errorMessage, e);
        }
    }

    /**
     * Add files of the directory to the archive
     *
     * @param directoryToZip  Location of the archive
     * @param file            File to be included in the archive
     * @param zipOutputStream Output stream
     * @throws APIMigrationException If an error occurs while writing files to the archive
     */
    private static void addToArchive(File directoryToZip, File file, ZipOutputStream zipOutputStream)
            throws APIMigrationException {

        try {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {

                // Get relative path from archive directory to the specific file
                String zipFilePath = file.getCanonicalPath()
                        .substring(directoryToZip.getCanonicalPath().length() + 1);
                if (File.separatorChar != Constants.ZIP_FILE_SEPARATOR)
                    zipFilePath = zipFilePath.replace(File.separatorChar, Constants.ZIP_FILE_SEPARATOR);
                ZipEntry zipEntry = new ZipEntry(zipFilePath);
                zipOutputStream.putNextEntry(zipEntry);

                IOUtils.copy(fileInputStream, zipOutputStream);
            }

            zipOutputStream.closeEntry();
        } catch (IOException e) {
            String errorMessage = "I/O error while writing files to archive";
            log.error(errorMessage, e);
            throw new APIMigrationException(errorMessage, e);
        }
    }
}

