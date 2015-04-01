package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.markdown.AbstractMarkdownParser;
import com.github.shyykoserhiy.gfm.markdown.GfmRequestDoneListener;
import com.github.shyykoserhiy.gfm.markdown.network.GitHubApiMarkdownParser;
import com.github.shyykoserhiy.gfm.markdown.offline.JnaMarkdownParser;
import com.github.shyykoserhiy.gfm.settings.GfmGlobalSettings;
import com.github.shyykoserhiy.gfm.settings.GfmGlobalSettingsChangedListener;
import com.intellij.codeHighlighting.BackgroundEditorHighlighter;
import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.fileEditor.FileEditorStateLevel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeListener;

@SuppressWarnings("unused")
public abstract class AbstractGfmPreview extends UserDataHolderBase implements Disposable, FileEditor {
    protected boolean previewIsUpToDate = false;
    protected boolean previewIsSelected = false;

    private Document document;
    private AbstractMarkdownParser markdownParser;
    private final VirtualFile markdownFile;
    private final GfmGlobalSettings settings;

    public AbstractGfmPreview(@NotNull VirtualFile markdownFile, @NotNull Document document) {
        this.markdownFile = markdownFile;
        this.document = document;
        settings = GfmGlobalSettings.getInstance();
        updateMarkdownParser(settings);
        addListeners();
    }

    @NotNull
    public FileEditorState getState(@NotNull FileEditorStateLevel fileEditorStateLevel) {
        return FileEditorState.INSTANCE;
    }

    public void setState(@NotNull FileEditorState fileEditorState) {
        //empty
    }

    public boolean isModified() {
        return false;
    }

    public boolean isValid() {
        return document.getTextLength() != 0;
    }

    /**
     * @return if preview should be updated even if it's not selected
     */
    public boolean isImmediateUpdate() {
        return false;
    }

    /**
     * Invoked when the editor is selected.
     */
    public void selectNotify() {
        previewIsSelected = true;
        if (!previewIsUpToDate) {
            updatePreview();
        }
    }

    /**
     * Invoked when the editor is deselected.
     */
    public void deselectNotify() {
        previewIsSelected = false;
    }

    public void addPropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
        //empty
    }

    public void removePropertyChangeListener(@NotNull PropertyChangeListener propertyChangeListener) {
        //empty
    }

    @Nullable
    public BackgroundEditorHighlighter getBackgroundHighlighter() {
        return null;
    }

    @Nullable
    public FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Nullable
    public StructureViewBuilder getStructureViewBuilder() {
        return null;
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    /**
     * Updates preview ignoring com.github.shyykoserhiy.gfm.editor.AbstractGfmPreview#previewIsUpToDate value
     */
    public void updatePreview() {
        previewIsUpToDate = true; //todo
        this.markdownParser.queueMarkdownHtmlRequest(markdownFile.getName(), document.getText());
    }

    public boolean isPreviewIsSelected() {
        return previewIsSelected;
    }

    protected abstract GfmRequestDoneListener getRequestDoneListener();

    private void updateMarkdownParser(GfmGlobalSettings gfmGlobalSettings) {
        if (gfmGlobalSettings.isUseOffline() && JnaMarkdownParser.isSupported()) {
            markdownParser = new JnaMarkdownParser(getRequestDoneListener());
        } else {
            markdownParser = new GitHubApiMarkdownParser(getRequestDoneListener());
        }
    }

    private void addListeners() {
        // Listen to the document modifications.
        DocumentAdapter documentListener = new DocumentAdapter() {
            @Override
            public void documentChanged(DocumentEvent e) {
                previewIsUpToDate = false;
                if (previewIsSelected || isImmediateUpdate()) {//todo offline only?
                    selectNotify();
                }
            }
        };
        GfmGlobalSettingsChangedListener settingsChangedListener = new GfmGlobalSettingsChangedListener() {
            @Override
            public void onGfmGlobalSettingsChanged(GfmGlobalSettings newGfmGlobalSettings) {
                updateMarkdownParser(newGfmGlobalSettings);
            }
        };

        this.document.addDocumentListener(documentListener, this);
        settings.addGlobalSettingsChangedListener(settingsChangedListener, this);
    }
}
