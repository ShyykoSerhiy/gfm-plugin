package com.github.shyykoserhiy.gfm.actions;

import com.github.shyykoserhiy.gfm.editor.AbstractGfmPreview;
import com.github.shyykoserhiy.gfm.editor.ModernGfmPreview;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class ReloadGfmPreviewAction extends AnAction {
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            FileEditorManager manager = FileEditorManager.getInstance(project);
            FileEditor[] allEditors = manager.getAllEditors();
            for (FileEditor editor : allEditors) {
                if (editor instanceof AbstractGfmPreview) {
                    AbstractGfmPreview gfmPreview = (AbstractGfmPreview) editor;
                    if (gfmPreview.isPreviewIsSelected()) {
                        if (gfmPreview instanceof ModernGfmPreview) {
                            ((ModernGfmPreview) gfmPreview).updatePreview(true);
                        } else {
                            gfmPreview.updatePreview();
                        }
                    }
                }
            }
        }
    }
}
