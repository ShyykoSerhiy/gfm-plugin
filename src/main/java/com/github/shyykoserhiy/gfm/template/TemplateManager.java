package com.github.shyykoserhiy.gfm.template;

public class TemplateManager {
    private static class Holder{
        private static TemplateManager INSTANCE = new TemplateManager();
    }

    private final Template markdownTemplate;
    private final Template errorTemplate;

    private TemplateManager(){
        markdownTemplate = new Template(TemplateResource.GFM);
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
