package com.github.shyykoserhiy.gfm;

import com.github.shyykoserhiy.gfm.settings.GfmGlobalSettings;
import com.intellij.openapi.components.ApplicationComponent;
import org.jetbrains.annotations.NotNull;

public class GfmApplicationComponent implements ApplicationComponent {
    public GfmApplicationComponent() {
        GfmGlobalSettings.getInstance();// force to load settings
    }

    public void initComponent() {
        //empty
    }

    public void disposeComponent() {
        //empty
    }

    @NotNull
    public String getComponentName() {
        return "GfmApplicationComponent";
    }
}
