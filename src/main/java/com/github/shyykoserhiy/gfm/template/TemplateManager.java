package com.github.shyykoserhiy.gfm.template;

import com.github.shyykoserhiy.gfm.resource.ResourceUnzip;
import com.github.shyykoserhiy.gfm.settings.GfmGlobalSettings;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TemplateManager {
    private static class Holder{
        private static TemplateManager INSTANCE = new TemplateManager();
    }

    private final Template markdownTemplate;
    private final Template errorTemplate;

    private TemplateManager() {
        File outputFile = new ResourceUnzip().unzipResources("/com/github/shyykoserhiy/gfm/html/css.zip");
        HashMap<String, Object> markdownParams = new HashMap<String, Object>();
        URL githubCss;
        URL githubCss2;
        URL highlightGithubCss;
        URL highlightPackJs;
        try {
            githubCss = new File(outputFile, FileUtil.join("css", "github-fff66249e57e12b5b264967f6a4d21f8923d59247f86c4419d1e3092660fe54b.css")).toURI().toURL();
            githubCss2 = new File(outputFile, FileUtil.join("css", "github2-ade0148a562b52311cf36a8e5f019126eb5ef7054bf2a0463ea00c536a358d33.css")).toURI().toURL();
            highlightGithubCss = new File(outputFile, FileUtil.join("css", "highlightjs8_8_0", "github.css")).toURI().toURL();
            highlightPackJs = new File(outputFile, FileUtil.join("css", "highlightjs8_8_0", "highlight.pack.js")).toURI().toURL();

            markdownParams.put("github.css", githubCss.toExternalForm());
            markdownParams.put("github2.css", githubCss2.toExternalForm());
            markdownParams.put("highlight.github.css", highlightGithubCss.toExternalForm());
            markdownParams.put("highlight.pack.js", highlightPackJs.toExternalForm());
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
        GfmGlobalSettings gfmGlobalSettings = GfmGlobalSettings.getInstance();
        Map<String, String> params = new HashMap<String, String>();
        params.put("width", gfmGlobalSettings.isUseFullWidthRendering() ? "100%" : "980px");
        params.put("additionalCss", gfmGlobalSettings.getAdditionalCss());
        return markdownTemplate.applyTemplate(params, filename, gfm); //todo
    }

    public String getErrorHtml(String errorMessage, String stackTrace){
        return errorTemplate.applyTemplate(errorMessage, stackTrace);
    }
}
