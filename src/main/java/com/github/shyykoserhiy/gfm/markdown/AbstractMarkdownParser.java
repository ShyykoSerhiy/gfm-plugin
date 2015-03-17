package com.github.shyykoserhiy.gfm.markdown;

import com.github.shyykoserhiy.gfm.settings.GfmGlobalSettings;
import com.github.shyykoserhiy.gfm.template.TemplateManager;

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
    }
}
