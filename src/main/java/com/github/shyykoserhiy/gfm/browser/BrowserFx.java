package com.github.shyykoserhiy.gfm.browser;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.concurrent.*;

public class BrowserFx implements IsBrowser {
    private final JPanel jPanel;
    private WebView webView;
    private final JFXPanelRetina jfxPanelRetina;

    public BrowserFx() {
        jPanel = new JPanel(new BorderLayout(), true);
        jfxPanelRetina = new JFXPanelRetina(); // initializing javafx
        jPanel.add(jfxPanelRetina, BorderLayout.CENTER);
        Platform.setImplicitExit(false);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView = new WebView();
                AnchorPane anchorPane = new AnchorPane();
                AnchorPane.setTopAnchor(webView, 0.0);
                AnchorPane.setBottomAnchor(webView, 0.0);
                AnchorPane.setLeftAnchor(webView, 0.0);
                AnchorPane.setRightAnchor(webView, 0.0);
                anchorPane.getChildren().add(webView);
                jfxPanelRetina.setScene(new Scene(anchorPane));
            }
        });
    }

    public synchronized WebView getWebView() {
        return webView;
    }

    @NotNull
    @Override
    public synchronized JComponent getComponent() {
        return jPanel;
    }

    @Override
    public void executeJavaScriptAndReturnValue(final String script, final JsExecutedListener jsExecutedListener) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Object result;
                try {
                    result = webView.getEngine().executeScript(script);
                } catch (netscape.javascript.JSException e) {
                    result = e.toString();
                }
                if (result == null) {
                    jsExecutedListener.onJsExecuted("null");
                }
                jsExecutedListener.onJsExecuted(result.toString());
            }
        });
    }

    @Override
    public void goBack() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebHistory history = webView.getEngine().getHistory();
                if (canGoBack()) {
                    history.go(-1);
                }
            }
        });
    }

    @Override
    public void goForward() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebHistory history = webView.getEngine().getHistory();
                if (canGoForward()) {
                    history.go(+1);
                }
            }
        });
    }

    @Override
    public void reload() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().reload();
            }
        });

    }

    @Override
    public void stop() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().getLoadWorker().cancel();
            }
        });
    }

    @Override
    public void addLoadListener(final LoadListener loadAdapter) {
        if (webView != null) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
                        @Override
                        public void changed(ObservableValue ov, State oldState, State newState) {
                            if (newState == State.SCHEDULED) {
                                loadAdapter.onStartLoadingFrame();
                                return;
                            }
                            if (newState == State.RUNNING) {
                                loadAdapter.onProvisionalLoadingFrame();
                                return;
                            }
                            loadAdapter.onFinishLoadingFrame(); //CANCELED, FAILED, READY?
                        }
                    });
                }
            });
        } else {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    addLoadListener(loadAdapter);
                }
            });
        }
    }

    @Override
    public String getUrl() {
        return webView.getEngine().getLocation();
    }

    @Override
    public String getHtml() {
        FutureTask<String> futureTask = new FutureTask<String>(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return (String) webView.getEngine().executeScript("document.documentElement.outerHTML");
            }
        });
        String result = "";
        try {
            result = futureTask.get(1000, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean canGoForward() {
        WebHistory history = webView.getEngine().getHistory();
        int previousIndex = history.getCurrentIndex() + 1;
        return previousIndex < history.getEntries().size();
    }

    @Override
    public boolean canGoBack() {
        WebHistory history = webView.getEngine().getHistory();
        int previousIndex = history.getCurrentIndex() - 1;
        return previousIndex > 0;
    }

    @Override
    public synchronized void dispose() {
        jfxPanelRetina.removeNotify(); //fixme @see com.github.shyykoserhiy.gfm.editor.GfmPreviewFX.JFXPanelRetina.removeNotify()
    }

    @Override
    public synchronized void loadUrl(@NotNull final String url) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().load(url);
            }
        });
    }

    @Override
    public synchronized void loadFile(@NotNull final File file) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().load("file:" + file.getAbsolutePath());
            }
        });
    }

    @Override
    public synchronized void loadContent(@NotNull final String content) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                webView.getEngine().loadContent(content);
            }
        });
    }

    /**
     * Fix for rendering bug on retina
     *
     * @see <http://cr.openjdk.java.net/~ant/RT-38915/webrev.0/>
     */
    private class JFXPanelRetina extends JFXPanel {
        @Override
        public void removeNotify() {
            /*try { //fixme? significantly increases performance(but probably can lead to errors:))
                Field scaleFactor = JFXPanel.class.getDeclaredField("scaleFactor");
                scaleFactor.setAccessible(true);
                scaleFactor.setInt(this, 1);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            super.removeNotify();*/
        }
    }
}
