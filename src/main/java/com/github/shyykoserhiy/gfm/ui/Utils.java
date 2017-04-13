package com.github.shyykoserhiy.gfm.ui;

import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.UnknownFileType;

public class Utils {
    public static Editor createEditor(String fileExtension) {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document editorDocument = editorFactory.createDocument("");
        EditorEx editor = (EditorEx) editorFactory.createEditor(editorDocument);
        fillEditorSettings(editor.getSettings());
        setHighlighting(editor, fileExtension);
        return editor;
    }

    public static void releaseEditor(Editor editor) {
        EditorFactory editorFactory = EditorFactory.getInstance();
        editorFactory.releaseEditor(editor);
    }

    private static void setHighlighting(EditorEx editor, String fileExtension) {
        FileType cssFileType = FileTypeManager.getInstance().getFileTypeByExtension(fileExtension);
        if (cssFileType != UnknownFileType.INSTANCE) {
            EditorHighlighter editorHighlighter = HighlighterFactory.createHighlighter(cssFileType, EditorColorsManager.getInstance().getGlobalScheme(), null);
            editor.setHighlighter(editorHighlighter);
        }
    }

    private static void fillEditorSettings(EditorSettings editorSettings) {
        editorSettings.setWhitespacesShown(false);
        editorSettings.setLineMarkerAreaShown(false);
        editorSettings.setIndentGuidesShown(false);
        editorSettings.setLineNumbersShown(true);
        editorSettings.setFoldingOutlineShown(false);
        editorSettings.setAdditionalColumnsCount(1);
        editorSettings.setAdditionalLinesCount(1);
        editorSettings.setUseSoftWraps(false);
    }
}
