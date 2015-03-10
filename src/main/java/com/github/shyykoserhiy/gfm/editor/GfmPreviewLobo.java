package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.network.GfmRequestDoneListener;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lobobrowser.context.ClientletFactory;
import org.lobobrowser.gui.FramePanel;
import org.lobobrowser.primary.clientlets.PrimaryClientletSelector;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

public class GfmPreviewLobo extends AbstractGfmPreview implements FileEditor {

    private final FramePanel webView;

    public GfmPreviewLobo(@NotNull VirtualFile markdownFile, @NotNull Document document) {
        super(markdownFile, document);

        ClientletFactory.getInstance().addClientletSelector(new PrimaryClientletSelector());
        webView = new FramePanel();
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
        return GfmBundle.message("gfm.editor.preview.tab-name-lobo");
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
                    try {
                        webView.navigate("file:" + result.getAbsolutePath());
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onRequestFail(String error) {
            previewIsUpToDate = false;
            FileWriter fileWriter = null;
            try {
                File file = File.createTempFile("markdown", ".html"); //fixme ugly
                fileWriter = new FileWriter(file);
                fileWriter.write(error);
                webView.navigate("file:" + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
