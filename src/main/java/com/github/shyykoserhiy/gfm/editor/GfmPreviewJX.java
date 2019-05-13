package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.browser.BrowserJx;
import com.github.shyykoserhiy.gfm.markdown.GfmRequestDoneListener;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSFunctionCallback;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.ScriptContextAdapter;
import com.teamdev.jxbrowser.chromium.events.ScriptContextEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GfmPreviewJX extends ModernGfmPreview {
    private String markdown;
    private String title;


    public GfmPreviewJX(@NotNull VirtualFile markdownFile, @NotNull Document document) {
        super(markdownFile, document);
        this.browser = new BrowserJx();
        addPopupListener();
    }

    @Override
    public boolean isImmediateUpdate() {
        return true;
    }

    @NotNull
    @Override
    public String getName() {
        return GfmBundle.message("gfm.editor.preview.tab-name-jx");
    }

    @Override
    protected GfmRequestDoneListener getRequestDoneListener() {
        return new RequestDoneListener();
    }

    private class RequestDoneListener implements GfmRequestDoneListener {
        @Override
        public void onRequestDone(final File result) {
            ((BrowserJx) GfmPreviewJX.this.browser).getWebView().getBrowser().addScriptContextListener(new ScriptContextAdapter() {
                @Override
                public void onScriptContextCreated(ScriptContextEvent event) {
                    Browser browser = event.getBrowser();
                    JSValue window = browser.executeJavaScriptAndReturnValue("window");
                    window.asObject().setProperty("getMarkdown", (JSFunctionCallback) args -> markdown);
                    window.asObject().setProperty("getTitle", (JSFunctionCallback) args -> title);
                }
            });

            browser.loadUrl("file:" + result.getAbsolutePath());
            onceUpdated = true;
        }

        @Override
        public void onRequestDone(final String title, final String markdown) {
            Browser browser = ((BrowserJx) GfmPreviewJX.this.browser).getWebView().getBrowser();
            GfmPreviewJX.this.markdown = markdown;
            GfmPreviewJX.this.title = title;

            browser.executeJavaScript("document.getElementById('title').innerHTML = getTitle();" +
                    "document.querySelector('.markdown-body.entry-content').innerHTML = getMarkdown();" +
                    "Array.prototype.slice.apply(document.querySelectorAll('pre code')).forEach(function(block){" +
                    "   hljs.highlightBlock(block);" +
                    "});");
        }

        @Override
        public void onRequestFail(final String error) {
            previewIsUpToDate = false;
            browser.loadContent(error);
        }
    }
}
