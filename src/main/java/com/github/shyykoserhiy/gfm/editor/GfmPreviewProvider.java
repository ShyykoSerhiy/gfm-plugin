package com.github.shyykoserhiy.gfm.editor;

import com.github.shyykoserhiy.gfm.GfmBundle;
import com.github.shyykoserhiy.gfm.file.MarkdownFile;
import com.github.shyykoserhiy.gfm.settings.GfmGlobalSettings;
import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class GfmPreviewProvider implements FileEditorProvider {
    private static final String EDITOR_TYPE_ID = GfmBundle.message("gfm.editor.type");

    @Override
    public boolean accept(@NotNull Project project, @NotNull VirtualFile virtualFile) {
        String extension = virtualFile.getExtension();
        return MarkdownFile.isMarkdown(extension);
    }

    @NotNull
    @Override
    public FileEditor createEditor(@NotNull Project project, @NotNull VirtualFile virtualFile) { //FIXME @see
        FileEditor fileEditor = null;
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        switch (GfmGlobalSettings.getInstance().getRenderingEngine()) {
            case JX_BROWSER:
                fileEditor = new GfmPreviewJX(virtualFile, document);
                break;
            case FX_WEBVIEW:
                String javaHome = System.getProperty("java.home");
                File jfxrt = new File(FileUtil.join(javaHome, "lib", "ext", "jfxrt.jar"));
                if (jfxrt.exists()) {
                    try {
                        PluginClassLoader pluginClassLoader = (PluginClassLoader) this.getClass().getClassLoader();
                        URL url = jfxrt.toURI().toURL();
                        if (!pluginClassLoader.getUrls().contains(url)) {
                            ((PluginClassLoader) this.getClass().getClassLoader()).addURL(url);
                        }
                        fileEditor = new GfmPreviewFX(virtualFile, document);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case LOBOEVOLUTION:
                fileEditor = new GfmPreviewLobo(virtualFile, document);
                break;
        }
        if (fileEditor == null) {
            fileEditor = new GfmPreviewJX(virtualFile, document); //todo show message that selected is not supported
        }
        return fileEditor;
    }

    @Override
    public void disposeEditor(@NotNull FileEditor fileEditor) {
        fileEditor.dispose();
    }

    @NotNull
    @Override
    public FileEditorState readState(@NotNull Element element, @NotNull Project project, @NotNull VirtualFile virtualFile) {
        return FileEditorState.INSTANCE;
    }

    @Override
    public void writeState(@NotNull FileEditorState fileEditorState, @NotNull Project project, @NotNull Element element) {
        //nothing to do here. Preview is stateless.
    }

    @NotNull
    @Override
    public String getEditorTypeId() {
        return EDITOR_TYPE_ID;
    }

    @NotNull
    @Override
    public FileEditorPolicy getPolicy() {
        return FileEditorPolicy.PLACE_AFTER_DEFAULT_EDITOR;
    }
}
