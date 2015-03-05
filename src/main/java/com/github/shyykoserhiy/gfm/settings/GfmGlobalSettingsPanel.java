package com.github.shyykoserhiy.gfm.settings;

import javax.swing.*;

public class GfmGlobalSettingsPanel {
    private JPasswordField githubAccessTokenField;
    private JLabel githubAccessTokenLabel;
    private JLabel connectionTimeoutLabel;
    private JLabel socketTimeoutLabel;
    private JPanel panel;
    private JSpinner connectionTimeoutSpinner;
    private JSpinner socketTimeoutSpinner;

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

    private void createUIComponents() {
        connectionTimeoutSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 100));
        socketTimeoutSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 100));
    }
}
