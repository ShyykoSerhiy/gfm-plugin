package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.network.GfmClient;
import com.github.shyykoserhiy.gfm.network.GfmRequestDoneListener;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lobobrowser.context.ClientletFactory;
import org.lobobrowser.gui.FramePanel;
import org.lobobrowser.primary.clientlets.PrimaryClientletSelector;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

public class GfmPreviewLobo extends UserDataHolderBase implements FileEditor {
    private Document document;

    private final FramePanel webView;
    private GfmClient client;
    private boolean previewIsUpToDate = false;


    public GfmPreviewLobo(@NotNull Document document) {
        this.document = document;
        this.client = new GfmClient(new RequestDoneListener());

        // Listen to the document modifications.
        this.document.addDocumentListener(new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
                previewIsUpToDate = false;
            }
        });

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

    @NotNull
    @Override
    public FileEditorState getState(@NotNull FileEditorStateLevel fileEditorStateLevel) {
        return FileEditorState.INSTANCE;
    }

    @Override
    public void setState(@NotNull FileEditorState fileEditorState) {
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return document.getTextLength() != 0;
    }

    public synchronized boolean isPreviewIsUpToDate() {
        return previewIsUpToDate;
    }

    public synchronized void setPreviewIsUpToDate(boolean previewIsUpToDate) {
        this.previewIsUpToDate = previewIsUpToDate;
    }

    /**
     * Invoked when the editor is selected.
     */
    @Override
    public void selectNotify() {
        if (!isPreviewIsUpToDate()) {
            setPreviewIsUpToDate(true);
            this.client.queueMarkdownHtmlRequest(document.getText());
        }
    }

    /**
     * Invoked when the editor is deselected.
     */
    @Override
    public void deselectNotify() {

    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
    }

    @Nullable
    @Override
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    @Nullable
    @Override
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    private class RequestDoneListener implements GfmRequestDoneListener{
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
            setPreviewIsUpToDate(false);
            FileWriter fileWriter = null;
            try {
                File file = File.createTempFile("markdown", ".html"); //fixme ugly
                fileWriter = new FileWriter(file);
                fileWriter.write(error);
                webView.navigate("file:" + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileWriter != null){
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
