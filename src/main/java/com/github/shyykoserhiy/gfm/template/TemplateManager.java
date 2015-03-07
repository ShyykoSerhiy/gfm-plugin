package com.github.shyykoserhiy.gfm.template;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class TemplateManager {
    private static class Holder{
        private static TemplateManager INSTANCE = new TemplateManager();
    }

    private final Template markdownTemplate;
    private final Template errorTemplate;

    private TemplateManager() {
        File outputFile = unzipResources();
        HashMap<String, Object> markdownParams = new HashMap<String, Object>();
        URL githubCss;
        URL githubCss2;
        try {
            githubCss = new File(outputFile, FileUtil.join("css", "github-fff66249e57e12b5b264967f6a4d21f8923d59247f86c4419d1e3092660fe54b.css")).toURI().toURL();
            githubCss2 = new File(outputFile, FileUtil.join("css", "github2-ade0148a562b52311cf36a8e5f019126eb5ef7054bf2a0463ea00c536a358d33.css")).toURI().toURL();
            markdownParams.put("github.css", githubCss.toExternalForm());
            markdownParams.put("github2.css", githubCss2.toExternalForm());
        } catch (MalformedURLException e) {
            e.printStackTrace(); //todo?
        }
        markdownTemplate = new Template(TemplateResource.GFM, markdownParams);
        errorTemplate = new Template(TemplateResource.ERROR);
    }

    public static TemplateManager getInstance(){
        return Holder.INSTANCE;
    }

    /**
     * @param filename name of markdown file
     * @param gfm generated gfm
     * @return markdown html
     */
    public String getMarkdownHtml(String filename, String gfm){
        return markdownTemplate.applyTemplate(filename, gfm);//.replace("data-canonical-src", "scr");
    }

    public String getErrorHtml(String errorMessage, String stackTrace){
        return errorTemplate.applyTemplate(errorMessage, stackTrace);
    }

    private File unzipResources(){
        byte[] buffer = new byte[1024];
        File folder = new File(FileUtil.join(PathManager.getPluginsPath(), "gfm-plugin"));
        try{
            //create output directory is not exists
            if(!folder.exists()){
                folder.mkdir();
            }
            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(getClass().getResourceAsStream("/com/github/shyykoserhiy/gfm/html/css.zip"));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while(ze!=null){
                String fileName = ze.getName();
                File newFile = new File(folder + File.separator + fileName);
                //create all non exists folders
                new File(newFile.getParent()).mkdirs();
                if (!ze.isDirectory()){
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
        }catch(IOException ex){
            ex.printStackTrace(); //todo
        }
        return folder;
    }
}
