package com.github.shyykoserhiy.gfm.template;

import java.net.URL;
import java.util.HashMap;

public class TemplateManager {
    private static class Holder{
        private static TemplateManager INSTANCE = new TemplateManager();
    }

    private final Template markdownTemplate;
    private final Template errorTemplate;

    private TemplateManager(){
        URL githubCss = getClass().getResource("/com/github/shyykoserhiy/gfm/html/css/github-fff66249e57e12b5b264967f6a4d21f8923d59247f86c4419d1e3092660fe54b.css");
        URL githubCss2 = getClass().getResource("/com/github/shyykoserhiy/gfm/html/css/github2-ade0148a562b52311cf36a8e5f019126eb5ef7054bf2a0463ea00c536a358d33.css");
        HashMap<String, Object> markdownParams = new HashMap<String, Object>();
        markdownParams.put("github.css", githubCss.toExternalForm());
        markdownParams.put("github2.css", githubCss2.toExternalForm());
        markdownTemplate = new Template(TemplateResource.GFM, markdownParams);
        errorTemplate = new Template(TemplateResource.ERROR);
    }

    public static TemplateManager getInstance(){
        return Holder.INSTANCE;
    }

    /**
     * @param gfm generated gfm
     * @return markdown html
     */
    public String getMarkdownHtml(String gfm){
        return markdownTemplate.applyTemplate(gfm);//.replace("data-canonical-src", "scr");
    }

    public String getErrorHtml(String errorMessage, String stackTrace){
        return errorTemplate.applyTemplate(errorMessage, stackTrace);
    }
}
