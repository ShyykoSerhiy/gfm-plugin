package com.github.shyykoserhiy.gfm.settings;

import com.github.shyykoserhiy.gfm.editor.RenderingEngine;
import com.github.shyykoserhiy.gfm.markdown.offline.JnaMarkdownParser;
import com.intellij.openapi.components.*;
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

    private Set<GfmGlobalSettingsChangedListener> listeners = new HashSet<GfmGlobalSettingsChangedListener>();

    private String githubAccessToken = "";
    private int connectionTimeout = 2000;
    private int socketTimeout = 2000;
    private RenderingEngine renderingEngine = RenderingEngine.JX_BROWSER;
    private boolean useOffline = JnaMarkdownParser.isSupported();

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

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("GfmSettings");
        element.setAttribute(GITHUB_ACCESS_TOKEN, githubAccessToken);
        element.setAttribute(CONNECTION_TIMEOUT, String.valueOf(connectionTimeout));
        element.setAttribute(SOCKET_TIMEOUT, String.valueOf(socketTimeout));
        element.setAttribute(RENDERING_ENGINE, String.valueOf(renderingEngine));
        element.setAttribute(USE_OFFLINE, String.valueOf(useOffline));
        return element;
    }

    @Override
    public void loadState(Element state) {
        String githubAccessToken = state.getAttributeValue(GITHUB_ACCESS_TOKEN);
        if (githubAccessToken != null) {
            this.githubAccessToken = githubAccessToken;
        }
        String connectionTimeout = state.getAttributeValue(CONNECTION_TIMEOUT);
        if (connectionTimeout != null) {
            this.connectionTimeout = Integer.parseInt(connectionTimeout);
        }
        String socketTimeout = state.getAttributeValue(SOCKET_TIMEOUT);
        if (socketTimeout != null) {
            this.socketTimeout = Integer.parseInt(socketTimeout);
        }
        String renderingEngine = state.getAttributeValue(RENDERING_ENGINE);
        if (renderingEngine != null) {
            this.renderingEngine = RenderingEngine.valueOf(renderingEngine);
        }
        String useOffline = state.getAttributeValue(USE_OFFLINE);
        if (useOffline != null) {
            this.useOffline = Boolean.valueOf(useOffline);
        }
        notifyListeners();
    }

    public void addGlobalSettingsChangedListener(GfmGlobalSettingsChangedListener gfmGlobalSettingsChangedListener) {
        listeners.add(gfmGlobalSettingsChangedListener);
    }

    public void notifyListeners() {
        for (GfmGlobalSettingsChangedListener listener : this.listeners) {
            if (listener != null) {
                listener.onGfmGlobalSettingsChanged(this);
            }
        }
    }

}
