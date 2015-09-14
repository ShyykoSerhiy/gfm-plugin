package com.github.shyykoserhiy.gfm.toolwindow;

import com.github.shyykoserhiy.gfm.toolwindow.browser.BrowserToolWindow;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

public class GfmBrowserToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        BrowserToolWindow browserToolWindow = new BrowserToolWindow();
        //GfmBrowserToolWindow gfmBrowserToolWindow = new GfmBrowserToolWindow();
        final ContentManager contentManager = toolWindow.getContentManager();
        final Content content = contentManager.getFactory().createContent(browserToolWindow, null, false);
        contentManager.addContent(content);
    }
}
