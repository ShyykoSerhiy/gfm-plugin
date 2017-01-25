package com.github.shyykoserhiy.gfm.markdown;

import com.github.shyykoserhiy.gfm.settings.GfmGlobalSettings;
import com.github.shyykoserhiy.gfm.template.TemplateManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

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

    public void queueMarkdownHtmlRequest(String parentFolder, String filename, String markdown, boolean useFileAsSuccessResponse) {
        connectionsThreadPool.submit(getWorker(parentFolder, filename, markdown, useFileAsSuccessResponse));
    }

    public abstract GfmWorker getWorker(String parentFolder, String filename, String markdown, boolean useFileAsSuccessResponse);


    protected abstract class GfmWorker implements Runnable {
        protected String parentFolder;
        protected String filename;
        protected String markdown;
        protected boolean useFileAsSuccessResponse;

        public GfmWorker(String parentFolder, String filename, String markdown, boolean useFileAsSuccessResponse) {
            this.parentFolder = parentFolder;
            this.filename = filename;
            this.markdown = new ImageUtil().processImagesUrl(markdown, parentFolder);
            this.useFileAsSuccessResponse = useFileAsSuccessResponse;
        }

        protected void fireFail(String message, String stackTrace) {
            requestDoneListener.onRequestFail(templateManager.getErrorHtml(message, stackTrace));
        }

        protected void fireSuccess(String html) throws IOException {
            if (useFileAsSuccessResponse) {
                String responseText = templateManager.getMarkdownHtml(filename, html);
                File file = File.createTempFile("markdown", ".html"); //todo get rid of files (Fix Lobo to accept Strings?)
                OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), Charset.forName("UTF-8"));
                writer.write(responseText);
                writer.close();
                requestDoneListener.onRequestDone(file);
            } else {
                requestDoneListener.onRequestDone(filename, html);
            }
        }
    }
}
