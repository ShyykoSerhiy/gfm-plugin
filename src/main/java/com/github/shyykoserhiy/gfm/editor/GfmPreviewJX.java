package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.browser.BrowserJx;
import com.github.shyykoserhiy.gfm.markdown.GfmRequestDoneListener;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserFunction;
import com.teamdev.jxbrowser.chromium.JSValue;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class GfmPreviewJX extends ModernGfmPreview {

    public GfmPreviewJX(@NotNull VirtualFile markdownFile, @NotNull Document document) {
        super(markdownFile, document);
        this.browser = new BrowserJx();
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
            browser.loadUrl("file:" + result.getAbsolutePath());
            onceUpdated = true;
        }

        @Override
        public void onRequestDone(final String title, final String markdown) {
            Browser browser = ((BrowserJx) GfmPreviewJX.this.browser).getWebView().getBrowser();
            browser.unregisterFunction("getMarkdown");
            browser.unregisterFunction("getTitle");
            browser.registerFunction("getMarkdown", new BrowserFunction() {
                public JSValue invoke(JSValue... args) {
                    return JSValue.create(markdown);
                }
            });
            browser.registerFunction("getTitle", new BrowserFunction() {
                public JSValue invoke(JSValue... args) {
                    return JSValue.create(title);
                }
            });
            browser.executeJavaScript("document.getElementById('title').innerHTML = getTitle();" +
                    "document.querySelector('.markdown-body.entry-content').innerHTML = getMarkdown();");
        }

        @Override
        public void onRequestFail(final String error) {
            previewIsUpToDate = false;
            browser.loadContent(error);
        }
    }
}
