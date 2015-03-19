package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.markdown.GfmRequestDoneListener;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lobobrowser.context.ClientletFactory;
import org.lobobrowser.primary.clientlets.PrimaryClientletSelector;

import javax.swing.*;
import java.io.File;

public class GfmPreviewJX extends AbstractGfmPreview {

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
                }
            });
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
