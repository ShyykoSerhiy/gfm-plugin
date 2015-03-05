package com.github.shyykoserhiy.gfm.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

@State(
        name = "GfmSettings",
        storages = @Storage(id = "gfm", file = "$APP_CONFIG/gfm.xml")
)
public class GfmGlobalSettings implements PersistentStateComponent<Element> {
    private static final String GITHUB_ACCESS_TOKEN = "githubAccessToken";
    private static final String CONNECTION_TIMEOUT = "connectionTimeout";
    private static final String SOCKET_TIMEOUT = "socketTimeout";

    private Set<WeakReference<GfmGlobalSettingsChangedListener>> listeners = new HashSet<WeakReference<GfmGlobalSettingsChangedListener>>();

    private String githubAccessToken = "";
    private int connectionTimeout = 2000;
    private int socketTimeout = 2000;

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

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("GfmSettings");
        element.setAttribute(GITHUB_ACCESS_TOKEN, githubAccessToken);
        element.setAttribute(CONNECTION_TIMEOUT, String.valueOf(connectionTimeout));
        element.setAttribute(SOCKET_TIMEOUT, String.valueOf(socketTimeout));
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
        notifyListeners();
    }

    public void notifyListeners(){
        for (WeakReference<GfmGlobalSettingsChangedListener> listener : this.listeners) {
            GfmGlobalSettingsChangedListener gfmGlobalSettingsChangedListener = listener.get();
            if (gfmGlobalSettingsChangedListener != null){
                gfmGlobalSettingsChangedListener.onGfmGlobalSettingsChanged(this);
            }
        }

    }

}
