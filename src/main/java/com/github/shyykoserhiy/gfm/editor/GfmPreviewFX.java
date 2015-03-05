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
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.io.File;

public class GfmPreviewFX extends UserDataHolderBase implements FileEditor {
    private Document document;

    private final JPanel jPanel;
    private WebView webView;
    private GfmClient client;
    private boolean previewIsUpToDate = false;
    private final JFXPanelRetina jfxPanelRetina;

    public GfmPreviewFX(@NotNull Document document) {
        this.document = document;
        this.client = new GfmClient(new RequestDoneListener());

        // Listen to the document modifications.
        this.document.addDocumentListener(new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
                setPreviewIsUpToDate(false);
            }
        });
        jPanel = new JPanel(new BorderLayout(), true);
        jfxPanelRetina = new JFXPanelRetina(); // initializing javafx
        jPanel.add(jfxPanelRetina, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView = new WebView();
                webView.getEngine().setUserStyleSheetLocation(getClass().getResource("/com/github/shyykoserhiy/gfm/stylesheet/javafxstylesheet.css").toExternalForm());
                jfxPanelRetina.setScene(new Scene(webView));
            }
        });
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return jPanel;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return jPanel;
    }

    @NotNull
    @Override
    public String getName() {
        return GfmBundle.message("gfm.editor.preview.tab-name-fx");
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

    public boolean isPreviewIsUpToDate() {
        return previewIsUpToDate;
    }

    public void setPreviewIsUpToDate(boolean previewIsUpToDate) {
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
        //todo?
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
        jfxPanelRetina.removeNotify(); //fixme @see com.github.shyykoserhiy.gfm.editor.GfmPreviewFX.JFXPanelRetina.removeNotify()
    }

    private class RequestDoneListener implements GfmRequestDoneListener {
        @Override
        public void onRequestDone(final File result) {
            loadFile(result);
        }

        @Override
        public void onRequestFail(String error) {
            setPreviewIsUpToDate(false);
            loadContent(error);
        }
    }

    private void loadFile(final File file) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().load("file:" + file.getAbsolutePath());
            }
        });
    }

    private void loadContent(final String content){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().loadContent(content);
            }
        });
    }

    /**
     * Fix for rendering bug on retina
     *
     * @see <http://cr.openjdk.java.net/~ant/RT-38915/webrev.0/>
     */
    private class JFXPanelRetina extends JFXPanel {
        @Override
        public void removeNotify() {
            /*try { //fixme? significantly increases performance(but probably can lead to errors:))
                Field scaleFactor = JFXPanel.class.getDeclaredField("scaleFactor");
                scaleFactor.setAccessible(true);
                scaleFactor.setInt(this, 1);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            super.removeNotify();*/
        }
    }
}
