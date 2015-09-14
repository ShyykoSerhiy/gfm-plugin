package com.github.shyykoserhiy.gfm.browser;

import org.jetbrains.annotations.NotNull;
import org.lobobrowser.context.ClientletFactory;
import org.lobobrowser.gui.FramePanel;
import org.lobobrowser.primary.clientlets.PrimaryClientletSelector;

import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;

public class BrowserLobo implements IsBrowser {
    private final FramePanel webView;

    public BrowserLobo() {
        ClientletFactory.getInstance().addClientletSelector(new PrimaryClientletSelector());
        webView = new FramePanel();
    }

    @Override
    public void loadUrl(@NotNull String url) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void loadFile(@NotNull final File file) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    webView.navigate("file:" + file.getAbsolutePath());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void loadContent(@NotNull String content) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return webView;
    }

    @Override
    public void executeJavaScriptAndReturnValue(String script, JsExecutedListener jsExecutedListener) {
        jsExecutedListener.onJsExecuted("null"); //todo throw unsupported exception?
    }

    @Override
    public void goBack() {
        //todo throw unsupported exception?
    }

    @Override
    public void goForward() {
        //todo throw unsupported exception?
    }

    @Override
    public void reload() {
        //todo throw unsupported exception?
    }

    @Override
    public void stop() {
        //todo throw unsupported exception?
    }

    @Override
    public void addLoadListener(LoadListener loadAdapter) {
        //todo throw unsupported exception?
    }

    @Override
    public String getUrl() {
        return null;//todo throw unsupported exception?
    }

    @Override
    public boolean canGoForward() {
        return false;//todo throw unsupported exception?
    }

    @Override
    public boolean canGoBack() {
        return false;//todo throw unsupported exception?
    }


    @Override
    public void dispose() {
        //empty?
    }
}
