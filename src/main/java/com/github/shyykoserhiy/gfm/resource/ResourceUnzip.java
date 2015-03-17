package com.github.shyykoserhiy.gfm.resource;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResourceUnzip {
    /**
     * Unzips resources from zip file to plugin resources folder.
     *
     * @param zipResource reference of zip file
     * @return folder to which all is extracted
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public File unzipResources(String zipResource) {
        byte[] buffer = new byte[1024];
        File folder = new File(FileUtil.join(PathManager.getPluginsPath(), "gfm-plugin"));
        try {
            //create output directory is not exists
            if (!folder.exists()) {
                folder.mkdir();
            }
            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(getClass().getResourceAsStream(zipResource));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(folder + File.separator + fileName);
                //create all non exists folders
                new File(newFile.getParent()).mkdirs();
                if (!ze.isDirectory()) {
                    FileOutputStream fos = new FileOutputStream(newFile);

                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                } else {
                    newFile.mkdir();
                }
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        } catch (IOException ex) {
            ex.printStackTrace(); //todo
        }
        return folder;
    }
}
