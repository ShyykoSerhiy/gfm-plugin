package com.github.shyykoserhiy.gfm.settings;

import com.github.shyykoserhiy.gfm.editor.RenderingEngine;

import javax.swing.*;

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

    public RenderingEngine getRenderingEngine() {
        return RenderingEngine.getTypeByText((String) renderingEngineComboBox.getSelectedItem());
    }

    public void setRenderingEngine(RenderingEngine renderingEngine) {
        this.renderingEngineComboBox.setSelectedItem(renderingEngine.getText());
    }

    private void createUIComponents() {
        connectionTimeoutSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 100));
        socketTimeoutSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 100));
    }
}
