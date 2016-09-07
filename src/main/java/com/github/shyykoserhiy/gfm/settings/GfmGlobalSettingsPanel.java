package com.github.shyykoserhiy.gfm.settings;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.editor.RenderingEngine;
import com.github.shyykoserhiy.gfm.markdown.offline.JnaMarkdownParser;
import com.github.shyykoserhiy.gfm.ui.Utils;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorImpl;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class GfmGlobalSettingsPanel implements Disposable {
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
        this.additionalCssTextArea = Utils.createEditor("css");
        this.additionalCssPanel.add(this.additionalCssTextArea.getComponent(), "Center");
    }

    @Override
    public void dispose() {
        if (this.additionalCssTextArea instanceof EditorImpl) {
            EditorImpl editor = (EditorImpl) additionalCssTextArea;
            if (!editor.isDisposed()) {//!isReleased
                //for some reason Editor is not Disposable. release in this case is dispose.
                editor.release();
            }
        }
    }
}
