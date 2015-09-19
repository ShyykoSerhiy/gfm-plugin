package com.github.shyykoserhiy.gfm.browser;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.events.ProvisionalLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.StartLoadingEvent;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

public class BrowserJx implements IsBrowser {
    private final BrowserView webView;

    public BrowserJx() {
        webView = new BrowserView(new Browser());
    }

    public synchronized BrowserView getWebView() {
        return webView;
    }

    @Override
    public synchronized void loadUrl(@NotNull final String url) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                webView.getBrowser().loadURL(url);
            }
        });
    }

    @Override
    public synchronized void loadFile(@NotNull final File file) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                webView.getBrowser().loadURL("file:" + file.getAbsolutePath());
            }
        });
    }

    @Override
    public synchronized void loadContent(@NotNull final String content) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                webView.getBrowser().loadHTML(content);
            }
        });
    }

    @NotNull
    @Override
    public synchronized JComponent getComponent() {
        return webView;
    }

    @Override
    public synchronized void dispose() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                webView.getBrowser().dispose();
            }
        });
    }

    @Override
    public void executeJavaScriptAndReturnValue(String script, JsExecutedListener jsExecutedListener) {
        JSValue jsValue = webView.getBrowser().executeJavaScriptAndReturnValue(script);
        if (jsValue.isBoolean()) {
            jsExecutedListener.onJsExecuted(Boolean.toString(jsValue.getBoolean()));
        }
        if (jsValue.isFunction()) {
            jsExecutedListener.onJsExecuted(jsValue.getString());
        }
        if (jsValue.isNumber()) {
            jsExecutedListener.onJsExecuted(Double.toString(jsValue.getNumber()));
        }
        jsExecutedListener.onJsExecuted(jsValue.toString());
    }

    @Override
    public void goBack() {
        webView.getBrowser().goBack();
    }

    @Override
    public void goForward() {
        webView.getBrowser().goForward();
    }

    @Override
    public void reload() {
        webView.getBrowser().reload();
    }

    @Override
    public void stop() {
        webView.getBrowser().stop();
    }

    @Override
    public void addLoadListener(final LoadListener loadAdapter) {
        webView.getBrowser().addLoadListener(new LoadAdapter() {
            @Override
            public void onStartLoadingFrame(StartLoadingEvent startLoadingEvent) {
                if (startLoadingEvent.isMainFrame()) {
                    loadAdapter.onStartLoadingFrame();
                }
            }

            @Override
            public void onProvisionalLoadingFrame(ProvisionalLoadingEvent provisionalLoadingEvent) {
                if (provisionalLoadingEvent.isMainFrame()) {
                    loadAdapter.onProvisionalLoadingFrame();
                }
            }

            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent finishLoadingEvent) {
                if (finishLoadingEvent.isMainFrame()) {
                    loadAdapter.onFinishLoadingFrame();
                }
            }
        });
    }

    @Override
    public String getUrl() {
        return webView.getBrowser().getURL();
    }

    @Override
    public boolean canGoForward() {
        return webView.getBrowser().canGoForward();
    }

    @Override
    public boolean canGoBack() {
        return webView.getBrowser().canGoBack();
    }
}
