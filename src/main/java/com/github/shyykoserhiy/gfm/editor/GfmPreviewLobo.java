package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.browser.BrowserLobo;
import com.github.shyykoserhiy.gfm.markdown.GfmRequestDoneListener;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class GfmPreviewLobo extends AbstractGfmPreview {

    public GfmPreviewLobo(@NotNull VirtualFile markdownFile, @NotNull Document document) {
        super(markdownFile, document);
        this.browser = new BrowserLobo();
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
            browser.loadFile(result);
        }

        @Override
        public void onRequestDone(String title, String markdown) {
            //ignore
        }

        @Override
        public void onRequestFail(String error) {
            previewIsUpToDate = false;
            FileWriter fileWriter = null;
            try {
                File file = File.createTempFile("markdown", ".html"); //fixme ugly
                fileWriter = new FileWriter(file);
                fileWriter.write(error);
                browser.loadFile(file);
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
