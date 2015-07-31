package com.github.shyykoserhiy.gfm.settings;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class GfmGlobalSettingsConfigurable implements SearchableConfigurable {

    private GfmGlobalSettings gfmGlobalSettings = GfmGlobalSettings.getInstance();
    private GfmGlobalSettingsPanel gfmGlobalSettingsPanel;

    @NotNull
    @Override
    public String getId() {
        return "gfm";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return GfmBundle.message("gfm.settings.name");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (gfmGlobalSettingsPanel == null){
            gfmGlobalSettingsPanel = new GfmGlobalSettingsPanel();
        }
        return gfmGlobalSettingsPanel.getPanel();
    }

    @Override
    public boolean isModified() {
        return gfmGlobalSettings.getConnectionTimeout() != (Integer)gfmGlobalSettingsPanel.getConnectionTimeoutSpinner().getValue() ||
                gfmGlobalSettings.getSocketTimeout() != (Integer)gfmGlobalSettingsPanel.getSocketTimeoutSpinner().getValue() ||
                !gfmGlobalSettings.getGithubAccessToken().equals(String.valueOf(gfmGlobalSettingsPanel.getGithubAccessTokenField().getPassword())) ||
                gfmGlobalSettings.getRenderingEngine() != gfmGlobalSettingsPanel.getRenderingEngine() ||
                gfmGlobalSettings.isUseOffline() != gfmGlobalSettingsPanel.getUseOfflineCheckBox().isSelected() ||
                gfmGlobalSettings.isReplacePreviewTab() != gfmGlobalSettingsPanel.getReplacePreviewTabCheckBox().isSelected() ||
                gfmGlobalSettings.isUseFullWidthRendering() != gfmGlobalSettingsPanel.getUseFullWidthRenderingCheckBox().isSelected() ||
                !gfmGlobalSettings.getAdditionalCss().equals(gfmGlobalSettingsPanel.getAdditionalCssTextArea().getDocument().getText());
    }

    @Override
    public void apply() throws ConfigurationException {
        gfmGlobalSettings.setGithubAccessToken(String.valueOf(gfmGlobalSettingsPanel.getGithubAccessTokenField().getPassword()));//todo not secure
        gfmGlobalSettings.setConnectionTimeout((Integer) gfmGlobalSettingsPanel.getConnectionTimeoutSpinner().getValue());
        gfmGlobalSettings.setSocketTimeout((Integer) gfmGlobalSettingsPanel.getSocketTimeoutSpinner().getValue());
        gfmGlobalSettings.setRenderingEngine(gfmGlobalSettingsPanel.getRenderingEngine());
        gfmGlobalSettings.setUseOffline(gfmGlobalSettingsPanel.getUseOfflineCheckBox().isSelected());
        gfmGlobalSettings.setReplacePreviewTab(gfmGlobalSettingsPanel.getReplacePreviewTabCheckBox().isSelected());
        gfmGlobalSettings.setUseFullWidthRendering(gfmGlobalSettingsPanel.getUseFullWidthRenderingCheckBox().isSelected());
        gfmGlobalSettings.setAdditionalCss(gfmGlobalSettingsPanel.getAdditionalCssTextArea().getDocument().getText());
    }

    @Override
    public void reset() {
        gfmGlobalSettingsPanel.getGithubAccessTokenField().setText(gfmGlobalSettings.getGithubAccessToken());
        gfmGlobalSettingsPanel.getConnectionTimeoutSpinner().setValue(gfmGlobalSettings.getConnectionTimeout());
        gfmGlobalSettingsPanel.getSocketTimeoutSpinner().setValue(gfmGlobalSettings.getSocketTimeout());
        gfmGlobalSettingsPanel.setRenderingEngine(gfmGlobalSettings.getRenderingEngine());
        gfmGlobalSettingsPanel.getUseOfflineCheckBox().setSelected(gfmGlobalSettings.isUseOffline());
        gfmGlobalSettingsPanel.getReplacePreviewTabCheckBox().setSelected(gfmGlobalSettings.isReplacePreviewTab());
        gfmGlobalSettingsPanel.getUseFullWidthRenderingCheckBox().setSelected(gfmGlobalSettings.isUseFullWidthRendering());
        gfmGlobalSettingsPanel.getAdditionalCssTextArea().getDocument().setText(gfmGlobalSettings.getAdditionalCss());
    }

    @Override
    public void disposeUIResources() {
        this.gfmGlobalSettingsPanel = null;
    }
}
