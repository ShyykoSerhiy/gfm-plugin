package com.github.shyykoserhiy.gfm.markdown;

import com.github.shyykoserhiy.gfm.settings.GfmGlobalSettings;
import com.github.shyykoserhiy.gfm.template.TemplateManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractMarkdownParser {
    private ExecutorService connectionsThreadPool = Executors.newSingleThreadExecutor();

    protected GfmGlobalSettings globalSettings;
    protected GfmRequestDoneListener requestDoneListener;
    protected TemplateManager templateManager;

    public AbstractMarkdownParser(GfmRequestDoneListener requestDoneListener) {
        this.requestDoneListener = requestDoneListener;
        templateManager = TemplateManager.getInstance();
        globalSettings = GfmGlobalSettings.getInstance();
    }

    public void queueMarkdownHtmlRequest(String filename, String markdown) {
        connectionsThreadPool.submit(getWorker(filename, markdown));
    }

    public abstract GfmWorker getWorker(String filename, String markdown);


    protected abstract class GfmWorker implements Runnable {
        protected String filename;
        protected String markdown;

        public GfmWorker(String filename, String markdown) {
            this.filename = filename;
            this.markdown = markdown;
        }

        protected void fireFail(String message, String stackTrace) {
            requestDoneListener.onRequestFail(templateManager.getErrorHtml(message, stackTrace));
        }

        protected void fireSuccess(String html) throws IOException {
            String responseText = templateManager.getMarkdownHtml(filename, html);
            File file = File.createTempFile("markdown", ".html"); //todo get rid of files (Fix Lobo to accept Strings?)
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(responseText);
            fileWriter.close();
            requestDoneListener.onRequestDone(file);
        }
    }
}
