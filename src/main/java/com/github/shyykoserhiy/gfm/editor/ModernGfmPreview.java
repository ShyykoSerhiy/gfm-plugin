package com.github.shyykoserhiy.gfm.editor;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public abstract class ModernGfmPreview extends AbstractGfmPreview {
    protected boolean onceUpdated = false;

    public ModernGfmPreview(@NotNull VirtualFile markdownFile, @NotNull Document document) {
        super(markdownFile, document);
    }

    @Override
    public void updatePreview() {
        previewIsUpToDate = true; //todo
        markdownParser.queueMarkdownHtmlRequest(markdownFile.getName(), document.getText(), !onceUpdated);
    }

    /**
     * @param forceFullReload if true, speed optimizations are disabled for this preview update.
     */
    public void updatePreview(boolean forceFullReload) {
        onceUpdated = false;
        updatePreview();
    }
}
