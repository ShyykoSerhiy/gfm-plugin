package com.github.shyykoserhiy.gfm.markdown;

import com.github.shyykoserhiy.gfm.settings.GfmGlobalSettings;
import com.github.shyykoserhiy.gfm.template.TemplateManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public abstract class AbstractMarkdownParser {
    private ThrottlePoolExecutor connectionsThreadPool = new ThrottlePoolExecutor(500);

    protected GfmGlobalSettings globalSettings;
    protected GfmRequestDoneListener requestDoneListener;
    protected TemplateManager templateManager;

    public AbstractMarkdownParser(GfmRequestDoneListener requestDoneListener) {
        this.requestDoneListener = requestDoneListener;
        templateManager = TemplateManager.getInstance();
        globalSettings = GfmGlobalSettings.getInstance();
    }

    public void queueMarkdownHtmlRequest(String filename, String markdown, boolean useFileAsSuccessResponse) {
        connectionsThreadPool.submit(getWorker(filename, markdown, useFileAsSuccessResponse));
    }

    public abstract GfmWorker getWorker(String filename, String markdown, boolean useFileAsSuccessResponse);


    protected abstract class GfmWorker implements Runnable {
        protected String filename;
        protected String markdown;
        protected boolean useFileAsSuccessResponse;

        public GfmWorker(String filename, String markdown, boolean useFileAsSuccessResponse) {
            this.filename = filename;
            this.markdown = markdown;
            this.useFileAsSuccessResponse = useFileAsSuccessResponse;
        }

        protected void fireFail(String message, String stackTrace) {
            requestDoneListener.onRequestFail(templateManager.getErrorHtml(message, stackTrace));
        }

        protected void fireSuccess(String html) throws IOException {
            if (useFileAsSuccessResponse) {
                String responseText = templateManager.getMarkdownHtml(filename, html);
                File file = File.createTempFile("markdown", ".html"); //todo get rid of files (Fix Lobo to accept Strings?)
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(responseText);
                fileWriter.close();
                requestDoneListener.onRequestDone(file);
            } else {
                requestDoneListener.onRequestDone(filename, html);
            }
        }
    }
}
