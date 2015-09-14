package com.github.shyykoserhiy.gfm.browser;

import com.intellij.openapi.Disposable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;

public interface IsBrowser extends Disposable {
    void loadUrl(@NotNull String url);

    void loadFile(@NotNull File file);

    void loadContent(@NotNull String content);

    @NotNull
    JComponent getComponent();

    void executeJavaScriptAndReturnValue(String script, JsExecutedListener jsExecutedListener);

    void goBack();

    void goForward();

    void reload();

    void stop();

    void addLoadListener(IsBrowser.LoadListener loadAdapter);

    String getUrl();

    boolean canGoForward();

    boolean canGoBack();

    interface LoadListener {
        void onStartLoadingFrame();

        void onProvisionalLoadingFrame();

        void onFinishLoadingFrame();
    }

    interface JsExecutedListener {
        void onJsExecuted(String result);
    }
}
