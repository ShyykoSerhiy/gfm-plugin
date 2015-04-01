package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.markdown.GfmRequestDoneListener;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserFunction;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lobobrowser.context.ClientletFactory;
import org.lobobrowser.primary.clientlets.PrimaryClientletSelector;

import javax.swing.*;
import java.io.File;

public class GfmPreviewJX extends ModernGfmPreview {

    private final BrowserView webView;

    public GfmPreviewJX(@NotNull VirtualFile markdownFile, @NotNull Document document) {
        super(markdownFile, document);

        ClientletFactory.getInstance().addClientletSelector(new PrimaryClientletSelector());
        webView = new BrowserView(new Browser());
    }

    @Override
    public boolean isImmediateUpdate() {
        return true;
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return webView;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return webView;
    }

    @NotNull
    @Override
    public String getName() {
        return GfmBundle.message("gfm.editor.preview.tab-name-jx");
    }

    @Override
    public void dispose() {
        super.dispose();
        webView.getBrowser().dispose();
    }

    @Override
    protected GfmRequestDoneListener getRequestDoneListener() {
        return new RequestDoneListener();
    }

    private class RequestDoneListener implements GfmRequestDoneListener {
        @Override
        public void onRequestDone(final File result) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    webView.getBrowser().loadURL("file:" + result.getAbsolutePath());
                    onceUpdated = true;
                }
            });
        }

        @Override
        public void onRequestDone(final String title, final String markdown) {
            Browser browser = webView.getBrowser();
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
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    webView.getBrowser().loadHTML(error);
                }
            });
        }
    }
}
