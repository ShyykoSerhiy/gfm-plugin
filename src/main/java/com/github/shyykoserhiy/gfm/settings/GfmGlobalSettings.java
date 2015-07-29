package com.github.shyykoserhiy.gfm.settings;

import com.github.shyykoserhiy.gfm.editor.RenderingEngine;
import com.github.shyykoserhiy.gfm.markdown.offline.JnaMarkdownParser;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.*;
import com.intellij.openapi.extensions.ExtensionPoint;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.fileEditor.FileEditorProvider;
import com.intellij.openapi.util.Disposer;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

@State(
        name = "GfmSettings",
        storages = @Storage(id = "gfm", file = StoragePathMacros.APP_CONFIG + "/gfm.xml")
)
public class GfmGlobalSettings implements PersistentStateComponent<Element> {
    private static final String GITHUB_ACCESS_TOKEN = "githubAccessToken";
    private static final String CONNECTION_TIMEOUT = "connectionTimeout";
    private static final String SOCKET_TIMEOUT = "socketTimeout";
    private static final String RENDERING_ENGINE = "renderingEngine";
    private static final String USE_OFFLINE = "useOffline";
    private static final String REPLACE_PREVIEW_TAB = "replacePreviewTab";
    private static final String USE_FULL_WIDTH_RENDERING = "useFullWidthRendering";
    private static final String ADDITIONAL_CSS = "additionalCss";

    private Set<GfmGlobalSettingsChangedListener> listeners = new HashSet<GfmGlobalSettingsChangedListener>();

    private String githubAccessToken = "";
    private int connectionTimeout = 2000;
    private int socketTimeout = 2000;
    private RenderingEngine renderingEngine = RenderingEngine.JX_BROWSER;
    private boolean useOffline = JnaMarkdownParser.isSupported();
    private boolean replacePreviewTab = false;
    private boolean useFullWidthRendering = false;
    private String additionalCss = "";

    public static GfmGlobalSettings getInstance() {
        return ServiceManager.getService(GfmGlobalSettings.class);
    }

    public String getGithubAccessToken() {
        return githubAccessToken;
    }

    public void setGithubAccessToken(String githubAccessToken) {
        if (!this.githubAccessToken.equals(githubAccessToken)) {
            this.githubAccessToken = githubAccessToken;
            notifyListeners();
        }
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        if (this.connectionTimeout != connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            notifyListeners();
        }
    }

    public int getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(int socketTimeout) {
        if (this.socketTimeout != socketTimeout) {
            this.socketTimeout = socketTimeout;
            notifyListeners();
        }
    }

    public RenderingEngine getRenderingEngine() {
        return renderingEngine;
    }

    public void setRenderingEngine(RenderingEngine renderingEngine) {
        if (renderingEngine != this.renderingEngine) {
            this.renderingEngine = renderingEngine;
            notifyListeners();
        }
    }

    public void setUseOffline(boolean useOffline) {
        if (this.useOffline != useOffline) {
            this.useOffline = useOffline;
            notifyListeners();
        }
    }

    public boolean isUseOffline() {
        return useOffline;
    }

    public boolean isReplacePreviewTab() {
        return replacePreviewTab;
    }

    public void setReplacePreviewTab(boolean replacePreviewTab) {
        if (this.replacePreviewTab != replacePreviewTab) {
            this.replacePreviewTab = replacePreviewTab;
            notifyListeners();
        }
        if (replacePreviewTab) {
            ExtensionPoint<FileEditorProvider> extensionPoint = Extensions.getRootArea().getExtensionPoint(FileEditorProvider.EP_FILE_EDITOR_PROVIDER);
            FileEditorProvider[] extensions = extensionPoint.getExtensions();
            for (FileEditorProvider extension : extensions) {
                if (extension.getEditorTypeId().equals("MarkdownPreviewEditor")) { //removing extension of Markdown
                    extensionPoint.unregisterExtension(extension);
                    break;
                }
            }
        }
    }

    public boolean isUseFullWidthRendering() {
        return useFullWidthRendering;
    }

    public void setUseFullWidthRendering(boolean useFullWidthRendering) {
        if (this.useFullWidthRendering != useFullWidthRendering) {
            this.useFullWidthRendering = useFullWidthRendering;
            notifyListeners();
        }
    }

    public String getAdditionalCss() {
        return additionalCss;
    }

    public void setAdditionalCss(String additionalCss) {
        if (!this.additionalCss.equals(additionalCss)) {
            this.additionalCss = additionalCss;
            notifyListeners();
        }
    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("GfmSettings");
        element.setAttribute(GITHUB_ACCESS_TOKEN, githubAccessToken);
        element.setAttribute(CONNECTION_TIMEOUT, String.valueOf(connectionTimeout));
        element.setAttribute(SOCKET_TIMEOUT, String.valueOf(socketTimeout));
        element.setAttribute(RENDERING_ENGINE, String.valueOf(renderingEngine));
        element.setAttribute(USE_OFFLINE, String.valueOf(useOffline));
        element.setAttribute(REPLACE_PREVIEW_TAB, String.valueOf(replacePreviewTab));
        element.setAttribute(USE_FULL_WIDTH_RENDERING, String.valueOf(useFullWidthRendering));
        element.setAttribute(ADDITIONAL_CSS, String.valueOf(additionalCss));
        return element;
    }

    @Override
    public void loadState(Element state) {
        String githubAccessToken = state.getAttributeValue(GITHUB_ACCESS_TOKEN);
        if (githubAccessToken != null) {
            setGithubAccessToken(githubAccessToken);
        }
        String connectionTimeout = state.getAttributeValue(CONNECTION_TIMEOUT);
        if (connectionTimeout != null) {
            setConnectionTimeout(Integer.parseInt(connectionTimeout));
        }
        String socketTimeout = state.getAttributeValue(SOCKET_TIMEOUT);
        if (socketTimeout != null) {
            setSocketTimeout(Integer.parseInt(socketTimeout));
        }
        String renderingEngine = state.getAttributeValue(RENDERING_ENGINE);
        if (renderingEngine != null) {
            setRenderingEngine(RenderingEngine.valueOf(renderingEngine));
        }
        String useOffline = state.getAttributeValue(USE_OFFLINE);
        if (useOffline != null) {
            setUseOffline(Boolean.valueOf(useOffline));
        }
        String replacePreviewTab = state.getAttributeValue(REPLACE_PREVIEW_TAB);
        if (replacePreviewTab != null) {
            setReplacePreviewTab(Boolean.valueOf(replacePreviewTab));
        }
        String useFullWidthRendering = state.getAttributeValue(USE_FULL_WIDTH_RENDERING);
        if (useFullWidthRendering != null) {
            setUseFullWidthRendering(Boolean.valueOf(useFullWidthRendering));
        }
        String additionalCss = state.getAttributeValue(ADDITIONAL_CSS);
        if (additionalCss != null) {
            setAdditionalCss(additionalCss);
        }
        notifyListeners();
    }

    public void addGlobalSettingsChangedListener(GfmGlobalSettingsChangedListener gfmGlobalSettingsChangedListener, Disposable parent) {
        Disposer.register(parent, new DisposableGlobalSettingsChangedListener(gfmGlobalSettingsChangedListener));
        listeners.add(gfmGlobalSettingsChangedListener);
    }

    public void notifyListeners() {
        for (GfmGlobalSettingsChangedListener listener : this.listeners) {
            if (listener != null) {
                listener.onGfmGlobalSettingsChanged(this);
            }
        }
    }

    private class DisposableGlobalSettingsChangedListener implements Disposable {
        private GfmGlobalSettingsChangedListener gfmGlobalSettingsChangedListener;

        public DisposableGlobalSettingsChangedListener(GfmGlobalSettingsChangedListener gfmGlobalSettingsChangedListener) {
            this.gfmGlobalSettingsChangedListener = gfmGlobalSettingsChangedListener;
        }

        @Override
        public void dispose() {
            listeners.remove(gfmGlobalSettingsChangedListener);
        }
    }
}
