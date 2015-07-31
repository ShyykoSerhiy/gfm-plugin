package com.github.shyykoserhiy.gfm.settings;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.editor.RenderingEngine;
import com.github.shyykoserhiy.gfm.markdown.offline.JnaMarkdownParser;
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

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class GfmGlobalSettingsPanel {
    private JPasswordField githubAccessTokenField;
    private JLabel githubAccessTokenLabel;
    private JLabel connectionTimeoutLabel;
    private JLabel socketTimeoutLabel;
    private JPanel panel;
    private JSpinner connectionTimeoutSpinner;
    private JSpinner socketTimeoutSpinner;
    private JCheckBox useOfflineCheckBox;
    private JLabel useOfflineLabel;
    private JLabel renderingEngineLabel;
    private JComboBox renderingEngineComboBox;
    private JCheckBox replacePreviewTabCheckBox;
    private JLabel replacePreviewTabLabel;
    private JLabel useFullWidthRenderingLabel;
    private JCheckBox useFullWidthRenderingCheckBox;
    private JLabel additionalCssLabel;
    private JPanel additionalCssPanel;
    private Editor additionalCssTextArea;

    public GfmGlobalSettingsPanel() {
        if (!JnaMarkdownParser.isSupported()) {
            useOfflineLabel.setText(useOfflineLabel.getText() + GfmBundle.message("gfm.offline.not-supported"));
            useOfflineLabel.setEnabled(false);
            useOfflineCheckBox.setEnabled(false);
            useOfflineCheckBox.setSelected(false);
        }
    }

    public JPanel getPanel() {
        return panel;
    }

    public JPasswordField getGithubAccessTokenField() {
        return githubAccessTokenField;
    }

    public JSpinner getConnectionTimeoutSpinner() {
        return connectionTimeoutSpinner;
    }

    public JSpinner getSocketTimeoutSpinner() {
        return socketTimeoutSpinner;
    }

    public JCheckBox getUseOfflineCheckBox() {
        return useOfflineCheckBox;
    }

    public JCheckBox getReplacePreviewTabCheckBox() {
        return replacePreviewTabCheckBox;
    }

    public JCheckBox getUseFullWidthRenderingCheckBox() {
        return useFullWidthRenderingCheckBox;
    }

    public Editor getAdditionalCssTextArea() {
        return additionalCssTextArea;
    }

    public RenderingEngine getRenderingEngine() {
        return RenderingEngine.getTypeByText((String) renderingEngineComboBox.getSelectedItem());
    }

    public void setRenderingEngine(RenderingEngine renderingEngine) {
        this.renderingEngineComboBox.setSelectedItem(renderingEngine.getText());
    }

    private void createUIComponents() {
        connectionTimeoutSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 100));
        socketTimeoutSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 100));
        additionalCssPanel = new JPanel(new BorderLayout());
        this.additionalCssTextArea = createEditor();
        this.additionalCssPanel.add(this.additionalCssTextArea.getComponent(), "Center");
    }

    private static Editor createEditor() {
        EditorFactory editorFactory = EditorFactory.getInstance();
        Document editorDocument = editorFactory.createDocument("");
        EditorEx editor = (EditorEx) editorFactory.createEditor(editorDocument);
        fillEditorSettings(editor.getSettings());
        setHighlighting(editor);
        return editor;
    }

    private static void setHighlighting(EditorEx editor) {
        FileType cssFileType = FileTypeManager.getInstance().getFileTypeByExtension("css");
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
