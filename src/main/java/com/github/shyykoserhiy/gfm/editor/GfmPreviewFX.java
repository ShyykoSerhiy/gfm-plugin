package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.browser.BrowserFx;
import com.github.shyykoserhiy.gfm.markdown.GfmRequestDoneListener;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GfmPreviewFX extends ModernGfmPreview {

    public GfmPreviewFX(@NotNull VirtualFile markdownFile, @NotNull Document document) {
        super(markdownFile, document);
        this.browser = new BrowserFx();
    }

    @Override
    public boolean isImmediateUpdate() {
        return true;
    }

    @NotNull
    @Override
    public String getName() {
        return GfmBundle.message("gfm.editor.preview.tab-name-fx");
    }

    @Override
    protected GfmRequestDoneListener getRequestDoneListener() {
        return new RequestDoneListener();
    }

    private void loadFile(final File file) {
        browser.loadFile(file);
        onceUpdated = true;
    }

    private void loadContent(final String content) {
        browser.loadContent(content);
    }

    private class RequestDoneListener implements GfmRequestDoneListener {
        @Override
        public void onRequestDone(final File result) {
            loadFile(result);
        }

        @Override
        public void onRequestDone(final String title, final String markdown) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    final WebView webView = ((BrowserFx) browser).getWebView(); //FIXME refactor somehow
                    final WebEngine webEngine = webView.getEngine();
                    final String script = "document.getElementById('title').innerHTML = window.java.getTitle();" +
                            "document.querySelector('.markdown-body.entry-content').innerHTML = window.java.getMarkdown();";
                    JSObject jsobj = (JSObject) webEngine.executeScript("window");
                    jsobj.setMember("java", new JSMarkdownBridge(markdown, title));
                    if (webEngine.getLoadWorker().getState() == Worker.State.SUCCEEDED) {
                        webEngine.executeScript(script);
                        webView.requestLayout();
                    } else {
                        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
                            @Override
                            public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue, Worker.State newValue) {
                                if (newValue == Worker.State.SUCCEEDED) {
                                    webEngine.executeScript(script);
                                    webView.requestLayout();
                                }
                            }
                        });
                    }

                }
            });
        }

        @Override
        public void onRequestFail(String error) {
            previewIsUpToDate = false;
            loadContent(error);
        }
    }

    public static class JSMarkdownBridge {
        private String markdown;
        private String title;

        public JSMarkdownBridge(String markdown, String title) {
            this.markdown = markdown;
            this.title = title;
        }

        public String getMarkdown() {
            return markdown;
        }

        public String getTitle() {
            return title;
        }
    }
}
